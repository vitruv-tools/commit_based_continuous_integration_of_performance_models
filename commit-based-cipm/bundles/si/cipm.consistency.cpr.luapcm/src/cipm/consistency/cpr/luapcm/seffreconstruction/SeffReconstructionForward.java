package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.xtext.lua.LuaUtil;
import org.xtext.lua.lua.Block;
import org.xtext.lua.lua.BlockWrapper;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.Expression_Functioncall;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Statement;
import org.xtext.lua.lua.Statement_For;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.lua.Statement_If_Then_Else;
import org.xtext.lua.lua.Statement_Repeat;
import org.xtext.lua.lua.Statement_While;
import org.xtext.lua.scoping.LuaLinkingService;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;

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
    private static final Logger LOGGER = Logger.getLogger(SeffReconstructionForward.class.getName());

    private static EditableCorrespondenceModelView<ReactionsCorrespondence> correspondenceModel;

    // TODO find a better default for this stochastic expression
    private static final String LOOP_COUNT_SPECIFICATION = "10";

    public static boolean needsSeffReconstruction(Statement_Function_Declaration declaration) {
        var infos = ComponentSetInfoRegistry.getInfosForComponentSet(declaration);
        var needsSeff = infos.needsSeffReconstruction(declaration);
        if (needsSeff) {
            LOGGER.trace(String.format("Statement_Function_Declaration needs SEFF reconstruction: %s",
                    declaration.getName()));
        }
        return needsSeff;
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

    private static <C extends EObject> C getCorrespondingEObjectByType(EObject eObj, Class<C> clazz) {
        var opt = correspondenceModel.getCorrespondingEObjects(eObj)
            .stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .findAny();
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    /*
     * Adds a required role
     */
    private static OperationRequiredRole getRequiredRole(Component callingComponent, Component calledComponent) {

        ComponentSetInfoRegistry.getInfosForComponentSet(calledComponent)
            .getComponentToRequiredComponents()
            .put(callingComponent, calledComponent);

        var pcmCallingComponent = getCorrespondingEObjectByType(callingComponent, BasicComponent.class);
        var pcmCalledInterface = getCorrespondingEObjectByType(calledComponent, OperationInterface.class);
        var requiredRole = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
        requiredRole.setRequiredInterface__OperationRequiredRole(pcmCalledInterface);
        requiredRole.setRequiringEntity_RequiredRole(pcmCallingComponent);
        requiredRole.setEntityName("role_requiring_" + pcmCalledInterface.getEntityName());
        pcmCallingComponent.getRequiredRoles_InterfaceRequiringEntity()
            .add(requiredRole);

        return requiredRole;
    }

    private static AbstractAction convertExternalDirectFunctionCallToAction(Expression_Functioncall_Direct directCall,
            Component callingComponent, Component calledComponent) {
        var calledFunction = directCall.getCalledFunction();

        if (calledFunction instanceof Statement_Function_Declaration calledFunctionDeclaration) {
            // external call
            var externalCallAction = SeffFactory.eINSTANCE.createExternalCallAction();

            // determine the signature of the called function
            // TODO PROBLEM this signature may not yet exist
            var calledSignature = getCorrespondingEObjectByType(calledFunctionDeclaration, OperationSignature.class);
            if (calledSignature == null) {
                LOGGER.error(String.format("Signature of called function does not exist: %s",
                        calledFunctionDeclaration.getName()));
            }
            externalCallAction.setCalledService_ExternalService(calledSignature);

            var requiredRole = getRequiredRole(callingComponent, calledComponent);
            if (requiredRole == null) {
                LOGGER.error(String.format("Unable to create required role for call to %s",
                        calledFunctionDeclaration.getName()));
            }
            externalCallAction.setRole_ExternalService(requiredRole);

            ComponentSetInfoRegistry.getInfosForComponentSet(directCall)
                .getDeclarationToCallingActions()
                .put(calledFunctionDeclaration, externalCallAction);

            return externalCallAction;
        }

        return null;
    }

    private static AbstractAction convertDirectFunctionCallToAction(Expression_Functioncall_Direct directCall) {
        var calledFunction = directCall.getCalledFunction();
        if (calledFunction == null) {
            return null;
        }

        var callingComponent = LuaUtil.getComponent(directCall);
        var calledComponent = LuaUtil.getComponent(calledFunction);

        if (callingComponent.equals(calledComponent)) {
            // internal call
            LOGGER.trace(String.format("Function classification: INTERNAL %s ", calledFunction.getName()));
            var action = SeffFactory.eINSTANCE.createInternalCallAction();
            action.setEntityName(calledFunction.getName());
            return action;
        }

        // external or library call
        var isCallToMockedFunction = calledComponent.getName()
            .equals(LuaLinkingService.MOCK_URI.path());
        if (isCallToMockedFunction) {
            // library call
            LOGGER.trace(String.format("Function classification: LIBRARY %s ", calledFunction.getName()));
            return SeffFactory.eINSTANCE.createInternalAction();
        }

        LOGGER.trace(String.format("Function classification: EXTERNAL %s ", calledFunction.getName()));
        return convertExternalDirectFunctionCallToAction(directCall, callingComponent, calledComponent);
    }

    // TODO this is currently only implemented for direct functioncalls
    private static AbstractAction convertFunctionCallToAction(Expression_Functioncall call) {
        if (call instanceof Expression_Functioncall_Direct directCall) {
            return convertDirectFunctionCallToAction(directCall);
        }
        LOGGER.error("Function classification for non-direct function calls is not yet implemented");
        return null;
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
            action = convertFunctionCallToAction(functionCall);
            if (action != null && isArchitecturallyRelevant(action)) {
                action.setEntityName("CALL_TO " + functionCall.getCalledFunction()
                    .getName());
                actions.add(action);
                ActionUtil.chainActions(predecessor, action);
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
        actions = ActionUtil.surroundActionList(start, actions, stop);
        return actions;
    }

    private static List<AbstractAction> getStepBehaviour(Statement_Function_Declaration declaration) {
        var function = declaration.getFunction();
        var declarationBlock = function.getBlock();
        if (function == null || declarationBlock == null) {
            return null;
        }

        LOGGER.trace(String.format("Reconstructing Seff for %s", declaration.getName()));
        var stepBehaviour = convertBlockToActions(declarationBlock);
        LOGGER.trace(String.format("Finished reconstructing Seff for %s", declaration.getName()));
        return stepBehaviour;
    }

    public static void doReconstruction(EditableCorrespondenceModelView<ReactionsCorrespondence> correspondenceModel,
            Statement_Function_Declaration declaration, ResourceDemandingSEFF seff) {
        // TODO ugly injection of the correspondence model
        SeffReconstructionForward.correspondenceModel = correspondenceModel;

        List<AbstractAction> stepBehaviour = null;
        stepBehaviour = getStepBehaviour(declaration);
        if (stepBehaviour == null) {
//            LOGGER.trace("Fallback to empty step behaviour");
            stepBehaviour = ActionUtil.emptyStepBehaviour();
        }
        seff.getSteps_Behaviour()
            .addAll(stepBehaviour);
    }
}
