package cipm.consistency.cpr.luapcm.seffreconstruction;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.xtext.lua.lua.Block;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.Expression;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Statement;
import org.xtext.lua.lua.Statement_For;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.lua.Statement_If_Then_Else;
import org.xtext.lua.lua.Statement_If_Then_Else_ElseIfPart;
import org.xtext.lua.lua.Statement_Repeat;
import org.xtext.lua.lua.Statement_While;
import org.xtext.lua.scoping.LuaLinkingService;

/**
 * This class serves as a utility class for the SEFF reconstruction
 * 
 * We can not use tools.vitruv.applications.pcmjava.seffstatements.Code2SeffFactory as it is
 * specific to the JaMoPP meta-model.
 * 
 * @author Lukas Burgey
 *
 */
@Deprecated
public class SeffReconstruction {
    private static final Logger LOGGER = Logger.getLogger(SeffReconstruction.class.getName());

    // TODO find a better default for this stochastic expression
//    private static final String LOOP_COUNT_SPECIFICATION = "10";

    // used when we find no block to reconstruct
    private static List<AbstractAction> emptyStepBehaviour() {
        var start = SeffFactory.eINSTANCE.createStartAction();
        var stop = SeffFactory.eINSTANCE.createStopAction();
        var actions = surroundActionList(start, null, stop);
        return actions;
    }

    public static void doReconstruction(Statement_Function_Declaration declaration, ResourceDemandingSEFF seff) {
        List<AbstractAction> stepBehaviour = null;
        stepBehaviour = getStepBehaviour(declaration);
        if (stepBehaviour == null) {
            LOGGER.trace("Fallback to empty step behaviour");
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

        LOGGER.trace(String.format("Reconstructing Seff for %s", declaration.getName()));
        var stepBehaviour = doActionReconstructionBackwards(declaration, declarationBlock);
        LOGGER.trace(String.format("Finished reconstructing Seff for %s", declaration.getName()));
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

    private static boolean isControlFlowStatement(EObject eObj) {
        return (eObj instanceof Statement_If_Then_Else || eObj instanceof Statement_If_Then_Else_ElseIfPart
                || eObj instanceof Statement_For || eObj instanceof Statement_While
                || eObj instanceof Statement_Repeat);
    }

    private static List<Expression_Functioncall_Direct> getFunctionCallsInBlock(Block block) {
        // TODO Table calls
        return EcoreUtil2.getAllContentsOfType(block, Expression_Functioncall_Direct.class);
    }

    private static AbstractAction controlFlowStatementToAction(Statement statement) {
        AbstractAction action = null;
        if (statement instanceof Statement_If_Then_Else) {
            action = SeffFactory.eINSTANCE.createBranchAction();
        } else if (statement instanceof Statement_For) {
            action = SeffFactory.eINSTANCE.createLoopAction();
        } else if (statement instanceof Statement_While) {
            action = SeffFactory.eINSTANCE.createLoopAction();
        } else if (statement instanceof Statement_Repeat) {
            action = SeffFactory.eINSTANCE.createLoopAction();
        } else {
            return null;
        }

        // TODO is this a good name?
        action.setEntityName(statement.toString());
        return action;
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

    private static List<EObject> getEobjectsBetween(Expression start, Block root) {
        List<EObject> path = new ArrayList<>();
        var current = start.eContainer();
        while (!current.equals(root)) {
            path.add(current);
            current = current.eContainer();
        }
        return path;
    }

    /*
     * find all control flow statements between start and the root block
     */
    private static List<Statement> getControlFlowStatementsBetween(Expression start, Block root) {
        return getEobjectsBetween(start, root).stream()
            .filter(e -> isControlFlowStatement(e))
            .map(e -> (Statement) e)
            .collect(Collectors.toList());
    }

    private static ResourceDemandingBehaviour getBehaviourForActionList(List<AbstractAction> actions) {
        var behaviour = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
        var start = SeffFactory.eINSTANCE.createStartAction();
        var stop = SeffFactory.eINSTANCE.createStopAction();
        behaviour.getSteps_Behaviour()
            .addAll(surroundActionList(start, actions, stop));
        return behaviour;
    }

    private static void createControlFlowFromStatementAndChilds(EObject controlFlowStatement,
            List<AbstractAction> childActions) {
        if (controlFlowStatement instanceof LoopAction) {
            var loopAction = (LoopAction) controlFlowStatement;
            // create the loop behaviour
            var bodyBehaviour = getBehaviourForActionList(childActions);
            loopAction.setBodyBehaviour_Loop(bodyBehaviour);
//        } else if (controlFlowStatement instanceof BranchAction) {
            // TODO reconstruct the branches of the branch action
            // the current data in the maps is not sufficient i think
        }

    }

    public static List<AbstractAction> doActionReconstructionBackwards(Statement_Function_Declaration decl,
            Block block) {
        List<Expression_Functioncall_Direct> functionCalls = getFunctionCallsInBlock(block);
        if (functionCalls.size() == 0) {
            return null;
        }

        // Maps control flow statements to the architecturaly relevent calls (like external calls)
        // which are their direct children direct
        ListMultimap<EObject, AbstractAction> controlFlowStatementsToFunctioncalls = ArrayListMultimap.create();

        // the actions which are occuring on the top level of the function without control flow
        // between them and the root
        var topLevelActions = new LinkedHashSet<AbstractAction>();

        // Map a control flow statement to into parent in the control flow graph
        var controlFlowStatementParents = new HashMap<EObject, EObject>();

        // add all external calls and the control flow "above" them
        for (var functionCall : functionCalls) {
            var classifiedCall = classifyFunctionCall(functionCall);
//            callClassification.put(functionCall, classifiedCall);
            if (!(classifiedCall instanceof ExternalCallAction)) {
                continue;
            }

            // control flow statements between the actual call and the function declaration
            var controlFlowStatements = getEobjectsBetween(functionCall, block);
//            var controlFlowActions = controlFlowStatements.stream()
//                .map(statement -> controlFlowStatementToAction(statement))
//                .collect(Collectors.toList());

            // track the hierarchy of the control flow statements
            EObject previousAction = null;
            for (var current : controlFlowStatements) {
                // where are we in the chain?
                if (previousAction == null) {
                    // at the beginning we track the function call
                    controlFlowStatementsToFunctioncalls.put(current, classifiedCall);
                } else {
                    // an the path to the root we track the path
                    controlFlowStatementParents.put(previousAction, current);
                }
                previousAction = current;
            }
            // add the last action in the chain or the function call itself if it is on the top
            // level
            if (previousAction == null) {
                topLevelActions.add(classifiedCall);
            }
        }

        // model the control flow actions that contain calls directly

        // Link the architecturally relevant function calls to their control flow statements
//        for (var cfAction : controlFlowStatementsToFunctioncalls.keySet()) {
//            linkControlFlowStatementAndChilds(cfAction, controlFlowStatementsToFunctioncalls.get(cfAction));
//        }

        // TODO convert the eObject control flow graph to abstract actions

        // Link the control flow statements
//        for (var entry : controlFlowStatementParents.entrySet()) {
//            linkControlFlowStatementAndChilds(entry.getKey(), List.of(entry.getValue()));
//        }

        // add the top level actions into the step behaviour for the declaration
        var start = SeffFactory.eINSTANCE.createStartAction();
        var stop = SeffFactory.eINSTANCE.createStopAction();
        List<AbstractAction> actions = null;
//        List<AbstractAction> actions = surroundActionList(start, topLevelActions.stream()
//            .collect(Collectors.toList()), stop);
        return actions;
    }
}
