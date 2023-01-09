package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.xtext.lua.lua.Block;
import org.xtext.lua.lua.BlockWrapper;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Statement;
import org.xtext.lua.lua.Statement_For;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.lua.Statement_If_Then_Else;
import org.xtext.lua.lua.Statement_Repeat;
import org.xtext.lua.lua.Statement_While;
import org.xtext.lua.scoping.LuaLinkingService;

/**
 * This class the SEFF reconstruction for Lua AppSpace apps.
 * 
 * We can not use tools.vitruv.applications.pcmjava.seffstatements.Code2SeffFactory as it is
 * specific to the JaMoPP meta-model.
 * 
 * @author Lukas Burgey
 *
 */
public class SeffReconstructionForward {
    private static final Logger LOGGER = Logger.getLogger(SeffReconstructionForward.class.getName());

    // TODO find a better default for this stochastic expression
    private static final String LOOP_COUNT_SPECIFICATION = "10";

    // used when we find no block to reconstruct
    private static List<AbstractAction> emptyStepBehaviour() {
        var start = SeffFactory.eINSTANCE.createStartAction();
        var stop = SeffFactory.eINSTANCE.createStopAction();
        var actions = surroundActionList(start, null, stop);
        return actions;
    }

    // TODO this is terribly inefficient
    private static List<String> getServedFunctionNames(EObject root) {
        LOGGER.trace("getServedFunctionNames was called for " + root.toString());
        // TODO we can assume that functions that end with '.register' and have 2 / 3 arguments are registerring a function
        // so we don't have to hardcode so much
        var servedNames = new ArrayList<String>();
        var servingFunctions = List.of("Script.serveFunction", "Script.register", "Image.Provider.Directory.register");
        var functionCalls = EcoreUtil2.getAllContentsOfType(root, Expression_Functioncall_Direct.class);
        for (var functionCall : functionCalls) {
            if (servingFunctions.contains(functionCall.getCalledFunction()
                .getName())) {
                var args = functionCall.getCalledFunctionArgs()
                    .getArguments();
                if (args.size() == 2 || args.size() == 3) {
                    var nameIndex = args.size() - 1;
                    var funcName = args.get(nameIndex);
                    if (funcName instanceof Expression_String) {
                        servedNames.add(((Expression_String) funcName).getValue());
                    } else if (funcName instanceof Expression_VariableName) {
                        servedNames.add(((Expression_VariableName) funcName).getRef()
                            .getName());
                    }
                } else {
                    throw new IllegalStateException("Invalid Script.serveFunction call");
                }
            }
        }
        return servedNames;
    }

    public static boolean needsSeffReconstruction(Statement_Function_Declaration declaration) {
        // TODO implement a usable policy to determine if the declaration needs a seff
        var root = EcoreUtil2.getContainerOfType(declaration, ComponentSet.class);
        var servedFuncNames = getServedFunctionNames(root);

        return servedFuncNames.contains(declaration.getName());
    }

    public static void doReconstruction(Statement_Function_Declaration declaration, ResourceDemandingSEFF seff) {
        List<AbstractAction> stepBehaviour = null;
        stepBehaviour = getStepBehaviour(declaration);
        if (stepBehaviour == null) {
//            LOGGER.trace("Fallback to empty step behaviour");
            stepBehaviour = emptyStepBehaviour();
        }
        seff.getSteps_Behaviour()
            .addAll(stepBehaviour);
    }

    private static List<AbstractAction> getStepBehaviour(Statement_Function_Declaration declaration) {
        var function = declaration.getFunction();
        if (function == null) {
            return null;
        }
        var declarationBlock = function.getBlock();
        if (declarationBlock == null) {
            return null;
        }

        LOGGER.debug(String.format("Reconstructing Seff for %s", declaration.getName()));
        var stepBehaviour = doActionReconstructionForwards(declarationBlock);
        LOGGER.debug(String.format("Finished reconstructing Seff for %s", declaration.getName()));
        return stepBehaviour;
    }

    private static void chainActions(AbstractAction a, AbstractAction b) {
        if (a != null && b != null) {
            a.setSuccessor_AbstractAction(b);
            b.setPredecessor_AbstractAction(a);
        }
    }

    private static void chainActionList(List<AbstractAction> actions) {
        AbstractAction predecessor = null;
        for (var action : actions) {
            if (predecessor != null) {
                chainActions(predecessor, action);
            }
            predecessor = action;
        }
    }

    private static List<AbstractAction> surroundActionList(AbstractAction start, List<AbstractAction> list,
            AbstractAction stop) {
        List<AbstractAction> actions = new ArrayList<>();
        if (start != null) {
            actions.add(start);
        }
        if (list != null) {
            actions.addAll(list);
        }
        if (stop != null) {
            actions.add(stop);
        }
        chainActionList(actions);
        return actions;
    }

    // forward approach
    private static List<AbstractAction> doActionReconstructionForwards(Block declarationBlock) {
        return convertBlockToActions(declarationBlock);
    }

    private static boolean isControlFlowStatement(EObject eObj) {
        return (eObj instanceof Statement_If_Then_Else || eObj instanceof Statement_For
                || eObj instanceof Statement_While || eObj instanceof Statement_Repeat);
    }

    /**
     * Classifies the function call
     * 
     * Possible classifications are: - ExternalCallAction for calls to another component -
     * InternalCall
     * 
     * @param decl
     */
    // TODO this is currently only implemented for direct functioncalls
    public static AbstractAction classifyFunctionCall(Expression_Functioncall_Direct call) {
        var calledFunction = call.getCalledFunction();
        if (calledFunction == null) {
            return null;
        }

        var callingComponent = EcoreUtil2.getContainerOfType(call, Component.class);
        var functionComponent = EcoreUtil2.getContainerOfType(call.getCalledFunction(), Component.class);

        if (callingComponent.equals(functionComponent)) {
            // internal call
            LOGGER.trace(String.format("Function classification: INTERNAL %s ", calledFunction.getName()));
            var action = SeffFactory.eINSTANCE.createInternalCallAction();
            action.setEntityName(calledFunction.getName());
            return action;
        }

        // external or library call
        if (functionComponent.getName()
            .equals(LuaLinkingService.MOCK_URI.path())) {
            // library call
            LOGGER.trace(String.format("Function classification: LIBRARY %s ", calledFunction.getName()));
            return SeffFactory.eINSTANCE.createInternalAction();
        } else {
            // external call
            LOGGER.trace(String.format("Function classification: EXTERNAL %s ", calledFunction.getName()));
            return SeffFactory.eINSTANCE.createExternalCallAction();
        }
    }

    private static BranchAction convertIfStatementToAction(Statement_If_Then_Else ifStatement) {
        // put all block into a list and add them ase branches to the branch action
        List<Block> branchBlocks = new ArrayList<>();
        if (ifStatement.getBlock() != null) {
            branchBlocks.add(ifStatement.getBlock());
        }
        if (ifStatement.getElseBlock() != null) {
            branchBlocks.add(ifStatement.getElseBlock());
        }

        for (var elseIfStatement : ifStatement.getElseIf()) {
            branchBlocks.add(elseIfStatement.getBlock());
        }

        var branchAction = SeffFactory.eINSTANCE.createBranchAction();
        var branches = branchAction.getBranches_Branch();
        for (var branchBlock : branchBlocks) {
            var actionsOfThisBranch = convertBlockToActions(branchBlock);
            if (actionsOfThisBranch == null) {
                continue;
            }

            var branchTransition = SeffFactory.eINSTANCE.createProbabilisticBranchTransition();
            branchTransition.setBranchProbability(1.0 / ((double) branchBlocks.size()));

            // create behaviour for the branch
            var behaviour = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
            behaviour.getSteps_Behaviour()
                .addAll(actionsOfThisBranch);

            branchTransition.setBranchBehaviour_BranchTransition(behaviour);
            branches.add(branchTransition);
        }
        if (branches.size() == 0) {
            // no architecturally relevant calls in this action

//            LOGGER.trace("NO ACTIONS IN BRANCH_ACTION");
            return null;
        }
        return branchAction;
    }

    private static AbstractAction convertControlFlowStatementToAction(Statement statement) {
        if (statement instanceof Statement_If_Then_Else) {
            return convertIfStatementToAction((Statement_If_Then_Else) statement);

        } else if (statement instanceof BlockWrapper) {
            var statementBlock = ((BlockWrapper) statement).getBlock();
            var actionsOfLoop = convertBlockToActions(statementBlock);
            if (actionsOfLoop == null) {
                // no architecturally relevant calls in this action
//                LOGGER.trace("NO ACTIONS IN BLOCK_WRAPPER");
                return null;
            }

            var loopAction = SeffFactory.eINSTANCE.createLoopAction();
            loopAction.setEntityName(statement.toString());

            var randomLoopCount = CoreFactory.eINSTANCE.createPCMRandomVariable();
            randomLoopCount.setSpecification(LOOP_COUNT_SPECIFICATION);

            loopAction.setIterationCount_LoopAction(randomLoopCount);

            var behaviour = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
            actionsOfLoop.forEach(action -> behaviour.getSteps_Behaviour()
                .add(action));
            loopAction.setBodyBehaviour_Loop(behaviour);

            return loopAction;
        } else {
//            LOGGER.trace("NO ACTIONS IN STATEMENT");
            return null;
        }
    }

    private static boolean isArchitecturallyRelevant(AbstractAction functionCallAction) {
        // TODO this definition should be extended to library functions
        return functionCallAction instanceof ExternalCallAction;
    }

    private static List<Expression_Functioncall_Direct> getFunctionCallsFromStatement(Statement statement) {
        if (statement instanceof Expression_Functioncall_Direct) {
            return List.of((Expression_Functioncall_Direct) statement);
        }
        return EcoreUtil2.getAllContentsOfType(statement, Expression_Functioncall_Direct.class);
    }

    private static List<AbstractAction> convertStatementToActions(Statement statement) {
        List<AbstractAction> actions = new ArrayList<>();

        // all function calls in this statement (or the statement may be a call itself)
        var functionCalls = getFunctionCallsFromStatement(statement);

        // if statements contains function calls, but is NO control flow statement
        // TODO also do table stuff here
        AbstractAction predecessor = null;
        AbstractAction action = null;
        for (var functionCall : functionCalls) {
            action = classifyFunctionCall(functionCall);
            if (action != null) {
                if (!isArchitecturallyRelevant(action)) {
                    continue;
                }
                action.setEntityName("CALL_TO " + functionCall.toString());
                actions.add(action);
                chainActions(predecessor, action);
                predecessor = action;
            }
        }

        return actions;
    }

    private static List<AbstractAction> convertBlockToActions(Block block) {

        List<AbstractAction> actions = new ArrayList<>();
        for (var statement : block.getStatements()) {
            // control flow statements
            if (isControlFlowStatement(statement)) {
                var cfAction = convertControlFlowStatementToAction(statement);
                if (cfAction != null) {
                    actions.add(cfAction);
                }
            } else {
                // statements which contains function calls, but is NO control flow statement
                var statementActions = convertStatementToActions(statement);
                if (statementActions != null) {
                    actions.addAll(statementActions);
                }
            }
        }
        if (actions.size() == 0) {
            // we don't reconstruct empty blocks
//            LOGGER.trace("NO ACTIONS IN BLOCK");
            return null;
        }

        var start = SeffFactory.eINSTANCE.createStartAction();
        start.setEntityName(block.toString() + " START");
        var stop = SeffFactory.eINSTANCE.createStopAction();
        stop.setEntityName(block.toString() + " END");
        actions = surroundActionList(start, actions, stop);
        return actions;
    }
}
