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
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Statement;
import org.xtext.lua.lua.Statement_For;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.lua.Statement_If_Then_Else;
import org.xtext.lua.lua.Statement_Repeat;
import org.xtext.lua.lua.Statement_While;

/**
 * This class contains the SEFF reconstruction for Lua AppSpace apps.
 * 
 * The class is called forward because the reconstruction works in a "forward" manner
 * 
 * We can not use tools.vitruv.applications.pcmjava.seffstatements.Code2SeffFactory as it is
 * specific to the JaMoPP meta-model.
 * 
 * @author Lukas Burgey
 *
 */
public final class SeffReconstructionForward {
    private static SeffReconstructionForward instance;

    private static final Logger LOGGER = Logger.getLogger(SeffReconstructionForward.class.getName());

    // TODO find a better default for this stochastic expression
    private static final String LOOP_COUNT_SPECIFICATION = "10";

    private ComponentSetInfoRegistry componentSetInfoRegistry;

    
    private SeffReconstructionForward() {
        componentSetInfoRegistry = new ComponentSetInfoRegistry();
    }
    
    private static SeffReconstructionForward getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new SeffReconstructionForward();
        return instance;
    }

    // used when we find no block to reconstruct
    private static List<AbstractAction> emptyStepBehaviour() {
        var start = SeffFactory.eINSTANCE.createStartAction();
        var stop = SeffFactory.eINSTANCE.createStopAction();
        var actions = surroundActionList(start, null, stop);
        return actions;
    }


    public static boolean needsSeffReconstruction(Statement_Function_Declaration declaration) {
        var infos = getInstance().componentSetInfoRegistry.getInfosForComponentSet(declaration);
        return infos.needsSeffReconstruction(declaration);
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
        var declarationBlock = function.getBlock();
        if (function == null || declarationBlock == null) {
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
        // infos about component set
        var infos = getInstance().componentSetInfoRegistry.getInfosForComponentSet(statement);

        List<AbstractAction> actions = new ArrayList<>();

        // all function calls in this statement (or the statement may be a call itself)
        var functionCalls = getFunctionCallsFromStatement(statement);

        // if statements contains function calls, but is NO control flow statement
        // TODO also do table stuff here
        AbstractAction predecessor = null;
        AbstractAction action = null;
        for (var functionCall : functionCalls) {
            action = infos.getFunctionCallClassification(functionCall);
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
