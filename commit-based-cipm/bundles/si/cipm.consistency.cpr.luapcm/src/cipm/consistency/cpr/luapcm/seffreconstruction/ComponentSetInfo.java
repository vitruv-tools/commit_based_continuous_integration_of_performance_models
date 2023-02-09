package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.xtext.lua.lua.Block;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Statement;
import org.xtext.lua.lua.Statement_Function_Declaration;

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

    private List<String> servedFunctionNames;

    // we track which Statement_Function_Declaration are called in an external call action
    private ListMultimap<Statement_Function_Declaration, ExternalCallAction> declarationToCallingActions;

    // map a component to components it depends upon (because it has external calls to it)
    private ListMultimap<Component, Component> componentToRequiredComponents;

    private Set<EObject> eObjectsRequiringActionReconstruction;

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
        servedFunctionNames = generateServedFunctionNames(componentSet);

        declarationToCallingActions = ArrayListMultimap.create();
        componentToRequiredComponents = ArrayListMultimap.create();

        eObjectsRequiringActionReconstruction = new HashSet<>();
        scanFunctionsForActionReconstruction(componentSet);
    }

    /**
     * Served function are "exported" an can be called by other appspace apps
     * 
     * @param root
     * @return
     */
    private static List<String> generateServedFunctionNames(EObject root) {
        // TODO we can assume that functions that end with '.register' and have 2 / 3 arguments are
        // registerring a function
        // so we don't have to hardcode so much
        List<String> servedNames = new ArrayList<>();

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
                    servedNames.add(funcNameExpString.getValue());
                } else if (funcName instanceof Expression_VariableName funcNameExpVar) {
                    servedNames.add(funcNameExpVar.getRef()
                        .getName());
                } else {
                    throw new IllegalStateException("Invalid Script.serveFunction call: Arguments are of invalid type");
                }
            } else {
                throw new IllegalStateException("Invalid Script.serveFunction call: Must have 2 or 3 arguments");
            }
        }
        return servedNames;
    }

    /**
     * Determines if a function declaration requires SEFF reconstruction
     * 
     * 
     * @param declaration
     * @return boolean
     */
    public boolean needsSeffReconstruction(Refble declaration) {
        // TODO Is being served sufficient to determine if a seff reconstruction is needed?
        return servedFunctionNames.contains(declaration.getName());
    }

    public ListMultimap<Statement_Function_Declaration, ExternalCallAction> getDeclarationToCallingActions() {
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

        return eObjectsRequiringActionReconstruction.contains(eObj);
    }

    private void scanFunctionsForActionReconstruction(ComponentSet componentSet) {
        var functionDecls = EcoreUtil2.getAllContentsOfType(componentSet, Statement_Function_Declaration.class);
        for (var functionDecl : functionDecls) {
            if (needsSeffReconstruction(functionDecl)) {
                scanFunctionForActionReconstruction(functionDecl);
            }
        }
    }

    private void scanFunctionForActionReconstruction(Statement_Function_Declaration decl) {
        var statements = EcoreUtil2.getAllContentsOfType(decl, Statement.class);
        for (var statement : statements) {
            if (!eObjectsRequiringActionReconstruction.contains(statement)
                    && ActionReconstruction.doesStatementContainExternalCall(statement)) {
                // mark objects and its parent towards the declaration for action reconstruction
                markForActionReconstruction(statement, decl);
            }
        }
    }

    /*
     * Mark all e objects on the path from statement (inclusive) to root (exclusive) for action
     * reconstruction.
     * 
     * Also marks all other statements in blocks which are traversed.
     */
    private void markForActionReconstruction(Statement statement, Statement_Function_Declaration decl) {
        EObject current = statement;
        do {
            // mark directly traversed Objects
            eObjectsRequiringActionReconstruction.add(current);

            // also mark other statements in traversed blocks
            if (current instanceof Block block) {
                for (var blockStatement : block.getStatements()) {
                    eObjectsRequiringActionReconstruction.add(blockStatement);
                }
            }

            // continue traversal
            current = current.eContainer();
        } while (!current.equals(decl));
    }
}
