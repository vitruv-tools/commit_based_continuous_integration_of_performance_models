package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.xtext.lua.lua.Block;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Statement;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.lua.Statement_If_Then_Else;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import cipm.consistency.commitintegration.lang.lua.appspace.AppSpaceSemantics;

/**
 * This class contains information about a ComponentSet and its contents which is needed during SEFF
 * reconstruction
 * 
 * @author Lukas Burgey
 */
public class ComponentSetInfo {

    private static final Logger LOGGER = Logger.getLogger(ComponentSetInfo.class.getName());

    private Map<String, Expression_Functioncall_Direct> functionNameToServeCall;

    // we track which Statement_Function_Declaration are called in an external call action
    private ListMultimap<Statement_Function_Declaration, AbstractAction> declarationToCallingActions;

    // map a component to components it depends upon (because it has external calls to it)
    private ListMultimap<Component, Component> componentToRequiredComponents;

    private Set<Block> blocksRequiringActionReconstruction;

    /**
     * Initialize the component set info.
     * 
     * This causes some calculation (served function names).
     * 
     * @param componentSet
     *            The component set for which this class will contain infos
     */
    public ComponentSetInfo(ComponentSet componentSet) {
        LOGGER.debug("Initializing ComponentSetInfo for " + componentSet.toString());

        declarationToCallingActions = ArrayListMultimap.create();
        componentToRequiredComponents = ArrayListMultimap.create();
        blocksRequiringActionReconstruction = new HashSet<>();

        functionNameToServeCall = generateServedFunctionNames(componentSet);
        scanFunctionsForActionReconstruction(componentSet);
    }

    /**
     * Served function are "exported" an can be called by other appspace apps
     * 
     * @param root
     * @return
     */
    private static Map<String, Expression_Functioncall_Direct> generateServedFunctionNames(EObject root) {
        Map<String, Expression_Functioncall_Direct> servedNamesToServeCalls = new HashMap<>();

        var functionCalls = EcoreUtil2.getAllContentsOfType(root, Expression_Functioncall_Direct.class);
        for (var functionCall : functionCalls) {
            if (!AppSpaceSemantics.isServingFunctionCall(functionCall)) {
                continue;
            }

            var args = functionCall.getCalledFunctionArgs()
                .getArguments();
            if (args.size() == 2 || args.size() == 3) {
                var nameIndex = args.size() - 1;
                var funcName = args.get(nameIndex);
                if (funcName instanceof Expression_String funcNameExpString) {
                    servedNamesToServeCalls.put(funcNameExpString.getValue(), functionCall);
                } else if (funcName instanceof Expression_VariableName funcNameExpVar) {
                    servedNamesToServeCalls.put(funcNameExpVar.getRef()
                        .getName(), functionCall);
                } else {
                    throw new IllegalStateException("Invalid Script.serveFunction call: Arguments are of invalid type");
                }
            } else {
                throw new IllegalStateException("Invalid Script.serveFunction call: Must have 2 or 3 arguments");
            }
        }
        return servedNamesToServeCalls;
    }

    /**
     * Determines if a function declaration requires SEFF reconstruction
     * 
     * 
     * @param declaration
     * @return boolean
     */
    public boolean needsSeffReconstruction(Refble declaration) {
        return functionNameToServeCall.containsKey(declaration.getName());
    }

    /**
     * Returns the serve call that causes a function to be served
     * 
     * @param declaration
     * @return
     */
    public Expression_Functioncall_Direct getServeCallForDeclaration(Refble declaration) {
        return functionNameToServeCall.get(declaration.getName());
    }

    public ListMultimap<Statement_Function_Declaration, AbstractAction> getDeclarationToCallingActions() {
        return declarationToCallingActions;
    }

    public ListMultimap<Component, Component> getComponentToRequiredComponents() {
        return componentToRequiredComponents;
    }

    /**
     * Determine if we need to reconstruct actions for a given eObject. This is only the case for
     * contenst of a function declaration which needs a seff, In addition only external calls and
     * objects above them in the tree need action recovery.s
     * 
     * @param eObj
     * @return
     */
    public boolean needsActionReconstruction(EObject eObj) {
        var parentDeclaration = EcoreUtil2.getContainerOfType(eObj, Statement_Function_Declaration.class);
        if (parentDeclaration == null || !needsSeffReconstruction(parentDeclaration)) {
            return false;
        }

        var parentBlock = EcoreUtil2.getContainerOfType(eObj, Block.class);
        if (blocksRequiringActionReconstruction.contains(parentBlock)) {
            return true;
        }

        if (eObj instanceof Statement_If_Then_Else ifStatement) {
            for (var block : getBlocksFromIfStatement(ifStatement)) {
                if (blocksRequiringActionReconstruction.contains(block)) {
                    return true;
                }
            }
        }
        return false;
    }

    // TODO move this to a utility class
    public static List<Block> getBlocksFromIfStatement(Statement_If_Then_Else ifStatement) {
        List<Block> blocks = new ArrayList<>();
        if (ifStatement.getBlock() != null) {
            blocks.add(ifStatement.getBlock());
        }
        for (var elseIf : ifStatement.getElseIf()) {
            blocks.add(elseIf.getBlock());
        }
        if (ifStatement.getElseBlock() != null) {
            blocks.add(ifStatement.getElseBlock());
        }
        return blocks;
    }

    private void scanFunctionsForActionReconstruction(ComponentSet componentSet) {
        var functionDecls = EcoreUtil2.getAllContentsOfType(componentSet, Statement_Function_Declaration.class);
        for (var functionDecl : functionDecls) {
            if (needsSeffReconstruction(functionDecl)) {
                scanSeffFunctionForActionReconstruction(functionDecl);
            }
        }
    }

    private void scanSeffFunctionForActionReconstruction(Statement_Function_Declaration decl) {
        /*
         * We always mark the root block of a seff function for reconstruction. The SEFF may only
         * contain an internal action in which case the marking algorithm will not catch the root
         * block.
         */
        var func = decl.getFunction();
        if (func != null) {
            var rootBlock = func.getBlock();
            if (rootBlock != null) {
                markBlockForActionReconstruction(rootBlock);
            }
        }

        /*
         * Mark architecturally relevant calls and all the blocks from them to the top level of
         * their containing declaration
         */
        var statements = EcoreUtil2.getAllContentsOfType(decl, Statement.class);
        for (var statement : statements) {
            if (!needsActionReconstruction(statement)
                    && ActionReconstruction.doesStatementContainArchitecturallyRelevantCall(statement, this)) {
                // mark objects and its parent towards the declaration for action reconstruction
                LOGGER.debug("Scan found cause for action reconstruction: " + statement.toString());
                markObjectAndParentsForActionReconstruction(statement, decl);
            }
        }
    }

    /*
     * Mark all e objects on the path from statement (inclusive) to root (exclusive) for action
     * reconstruction.
     * 
     * Also marks all other statements in blocks which are traversed.
     */
    private void markObjectAndParentsForActionReconstruction(Statement statement, Statement_Function_Declaration decl) {
        EObject current = statement;
        do {
            if (current instanceof Block block) {
                markBlockForActionReconstruction(block);
            } else if (current instanceof Statement_If_Then_Else ifStatement) {
                // also mark other child block when marking branch actions
                // mark other branches
                for (var block : getBlocksFromIfStatement(ifStatement)) {
                    markBlockForActionReconstruction(block);
                }
            }

            // continue traversal
            current = current.eContainer();
        } while (!current.equals(decl));
    }

    private void markBlockForActionReconstruction(Block block) {
        LOGGER.trace("Block marked for action reconstruction: " + block.toString());
        blocksRequiringActionReconstruction.add(block);
    }
}
