package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.InternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.xtext.lua.LuaUtil;
import org.xtext.lua.lua.BlockWrapper;
import org.xtext.lua.lua.Expression_Functioncall;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_Functioncall_Table;
import org.xtext.lua.lua.Statement;
import org.xtext.lua.lua.Statement_For;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.lua.Statement_If_Then_Else;
import org.xtext.lua.lua.Statement_Repeat;
import org.xtext.lua.lua.Statement_While;
import org.xtext.lua.scoping.LuaLinkingService;

import cipm.consistency.cpr.luapcm.Config;
import cipm.consistency.cpr.luapcm.Config.ReconstructionType;
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
public final class ActionReconstruction {

    private static final Logger LOGGER = Logger.getLogger(ActionReconstruction.class.getName());

    private static final String LOOP_COUNT_SPECIFICATION = "10";

    private EditableCorrespondenceModelView<ReactionsCorrespondence> correspondenceModelView;

    private ActionReconstruction(EditableCorrespondenceModelView<ReactionsCorrespondence> cmv) {
        this.correspondenceModelView = cmv;
    }

    /**
     * Do we need to reconstruct actions for this object?
     */
    public static boolean needsActionReconstruction(EObject eObj) {
        if (eObj == null) {
            return false;
        }

        var infos = ComponentSetInfoRegistry.getInfosForComponentSet(eObj);
        return infos.needsActionReconstruction(eObj);
    }

    public static Expression_Functioncall_Direct getServeCallForDeclaration(Statement_Function_Declaration eObj) {
        if (eObj != null) {
            var infos = ComponentSetInfoRegistry.getInfosForComponentSet(eObj);
            if (infos != null) {
                return infos.getServeCallForDeclaration(eObj);
            }
        }

        return null;
    }

    private static boolean isControlFlowStatement(EObject eObj) {
        return (eObj instanceof Statement_If_Then_Else || eObj instanceof Statement_For
                || eObj instanceof Statement_While || eObj instanceof Statement_Repeat);
    }

    private AbstractAction reconstructBranchAction(Statement_If_Then_Else ifStatement) {

        // we only reconstruct the if statement as a branch if at least one brach was marked as
        // interesting
        var ifBlocks = ComponentSetInfo.getBlocksFromIfStatement(ifStatement);
        var reconstructAsBranchAction = false;
        for (var block : ifBlocks) {
            if (needsActionReconstruction(block)) {
                reconstructAsBranchAction = true;
                break;
            }
        }

        if (reconstructAsBranchAction) {
            var branchAction = SeffFactory.eINSTANCE.createBranchAction();
            if (Config.descriptiveNames()) {
                branchAction.setEntityName("IF " + ifStatement.getIfExpression()
                    .eClass()
                    .getName());
            }
            return branchAction;
        }

        var action = SeffFactory.eINSTANCE.createInternalAction();
        if (Config.descriptiveNames()) {
            action.setEntityName("IF BLOCK " + ifStatement.getIfExpression()
                .eClass()
                .getName());
        }
        return action;
    }

    private AbstractAction reconstructLoopAction(BlockWrapper statement) {
        var loopBlock = statement.getBlock();
        if (needsActionReconstruction(loopBlock)) {

            var loopAction = SeffFactory.eINSTANCE.createLoopAction();
            if (Config.descriptiveNames()) {
                loopAction.setEntityName(statement.toString());
            }

            var randomLoopCount = CoreFactory.eINSTANCE.createPCMRandomVariable();
            randomLoopCount.setSpecification(LOOP_COUNT_SPECIFICATION);
            loopAction.setIterationCount_LoopAction(randomLoopCount);

            return loopAction;
        }

        var action = SeffFactory.eINSTANCE.createInternalAction();
        if (Config.descriptiveNames()) {
            action.setEntityName("LOOP BLOCK " + statement.toString());
        }
        return action;
    }

    private AbstractAction reconstructControlFlowStatementToAction(Statement statement) {
        if (statement instanceof Statement_If_Then_Else ifStatement) {
            return reconstructBranchAction(ifStatement);
        } else if (statement instanceof BlockWrapper blockWrappingStatement) {
            return reconstructLoopAction(blockWrappingStatement);
        }

        LOGGER.error("Action reconstructon of control flow statement is not implemented");
        return null;
    }

    private static List<Expression_Functioncall> getFunctionCallsFromStatement(EObject statement) {
        if (statement instanceof Expression_Functioncall functionCall) {
            return List.of(functionCall);
        }
        return EcoreUtil2.getAllContentsOfType(statement, Expression_Functioncall.class);
    }

    private static boolean isCallArchitecturallyRelevant(Expression_Functioncall call, ComponentSetInfo info) {
        if (call instanceof Expression_Functioncall_Direct directCall) {
            var calledFunction = directCall.getCalledFunction();
            var callingComponent = LuaUtil.getComponent(directCall);
            var calledComponent = LuaUtil.getComponent(calledFunction);
            
            if (calledFunction == null || calledComponent == null) {
                return false;
            }

            var isCallToFunction = calledFunction instanceof Statement_Function_Declaration;
            var isCallToMockedFunction = calledComponent.getName()
                .equals(LuaLinkingService.MOCK_URI.path());
            var isInternalCall = calledComponent.equals(callingComponent);
            var isExternalCall = !isInternalCall;

            var calledFunctionHasSeff = info.needsSeffReconstruction(calledFunction);

            return isCallToFunction && !isCallToMockedFunction
                    && (isExternalCall
                            || (Config.getReconstructionTypeInternalSeffCall() == ReconstructionType.InternalCallAction
                                    && isInternalCall && calledFunctionHasSeff));
        }

        // currently table calls and others are never architecturally relevant
        return false;
    }

    protected static boolean doesStatementContainArchitecturallyRelevantCall(Statement statement,
            ComponentSetInfo info) {
        var calls = getFunctionCallsFromStatement(statement);
        for (var call : calls) {
            if (isCallArchitecturallyRelevant(call, info)) {
                if (call instanceof Expression_Functioncall_Direct directCall) {
                    LOGGER
                        .debug("Scan found architecturally relevant function call to: " + directCall.getCalledFunction()
                            .getName());
                }
                return true;
            }
        }
        return false;
    }

    /*
     * TODO Using internal call actions to model calling SEFFs from the same component does not work
     * as we cannot reference the step behaviour of the called SEFF. We would have to duplicate it,
     * which would likely break the fine grained seff reconstruction.
     * 
     * TODO method does currently not work correctly
     */
    private InternalCallAction reconstructInternalSeffCallAsInternalCallAction(
            Expression_Functioncall_Direct directCall) {
        var calledFunction = (Statement_Function_Declaration) directCall.getCalledFunction();
        var calledComponent = LuaUtil.getComponent(calledFunction);

        // internal call
        LOGGER.trace(String.format("Call classification: Internal call to SEFF %s ", calledFunction.getName()));
        var internalCallAction = SeffFactory.eINSTANCE.createInternalCallAction();
        if (Config.descriptiveNames()) {
            internalCallAction.setEntityName("CALL_TO_INTERNAL_SEFF " + calledFunction.getName());
        }

        var calledFunctionRootBlock = calledFunction.getFunction()
            .getBlock();
        var rdSeff = CorrespondenceUtil.getCorrespondingEObjectByType(correspondenceModelView, calledFunctionRootBlock,
                ResourceDemandingSEFF.class);
        if (rdSeff != null) {
            var internalBehaviour = SeffFactory.eINSTANCE.createResourceDemandingInternalBehaviour();
            internalBehaviour.setResourceDemandingSEFF_ResourceDemandingInternalBehaviour(rdSeff);
            internalCallAction.setCalledResourceDemandingInternalBehaviour(internalBehaviour);
        } else {
            // TODO implement this if needed
            LOGGER.warn("Cannot find rd seff for internal call");
        }

        ComponentSetInfoRegistry.getInfosForComponentSet(directCall)
            .getDeclarationToCallingActions()
            .put(calledFunction, internalCallAction);

        LOGGER.trace("Adding correspondence for internal call " + internalCallAction.toString());
        correspondenceModelView.addCorrespondenceBetween(internalCallAction, calledComponent, null);
        return internalCallAction;
    }

    private AbstractAction reconstructExternalSeffCall(Expression_Functioncall_Direct directCall) {
        var calledFunction = (Statement_Function_Declaration) directCall.getCalledFunction();
        var calledComponent = LuaUtil.getComponent(calledFunction);

        // external call
        var externalCallAction = SeffFactory.eINSTANCE.createExternalCallAction();
        if (Config.descriptiveNames()) {
            externalCallAction.setEntityName("CALL_TO_SEFF " + calledFunction.getName());
        }

        // determine the signature of the called function
        var calledSignature = CorrespondenceUtil.getCorrespondingEObjectByType(correspondenceModelView, calledFunction,
                OperationSignature.class);
        if (calledSignature != null) {
            externalCallAction.setCalledService_ExternalService(calledSignature);
        } else {
            LOGGER.debug(calledFunction.getName() + ": Signature does not exist");
        }

        ComponentSetInfoRegistry.getInfosForComponentSet(directCall)
            .getDeclarationToCallingActions()
            .put(calledFunction, externalCallAction);

        LOGGER.trace("Adding correspondence for external call " + externalCallAction.toString());
        correspondenceModelView.addCorrespondenceBetween(externalCallAction, calledComponent, null);

        return externalCallAction;
    }

    private AbstractAction reconstructInternalSeffCall(Expression_Functioncall_Direct directCall) {
        var calledFunction = (Statement_Function_Declaration) directCall.getCalledFunction();

        // we have two alternatives to modelling internal seff calls as internal actions:
        // Both are currently not working / fit our overall modelling
        switch (Config.getReconstructionTypeInternalSeffCall()) {
        case ExternalCallAction:
            return reconstructExternalSeffCall(directCall);
        case InternalCallAction:
            return reconstructInternalSeffCallAsInternalCallAction(directCall);
        default:
        }

        LOGGER.trace(String.format("Call classification: Internal call to non-SEFF %s ", calledFunction.getName()));
        var action = SeffFactory.eINSTANCE.createInternalAction();
        if (Config.descriptiveNames()) {
            action.setEntityName("CALL_TO_INTERNAL_NON_SEFF " + calledFunction.getName());
        }
        return action;
    }

    private AbstractAction reconstructExternalStdlibCrownCall(Expression_Functioncall_Direct directCall) {
        var calledFunction = (Statement_Function_Declaration) directCall.getCalledFunction();
        var calledComponent = LuaUtil.getComponent(calledFunction);

        var action = SeffFactory.eINSTANCE.createInternalAction();
        if (Config.descriptiveNames()) {
            action.setEntityName("CALL_TO_STDLIB/CROWN " + calledComponent.getName());
        }
        return action;
    }

    private AbstractAction reconstructDirectFunctionCallToAction(Expression_Functioncall_Direct directCall) {
        if (directCall.getCalledFunction() == null) {
            return null;
        }

        if (!(directCall.getCalledFunction() instanceof Statement_Function_Declaration)) {
            LOGGER.warn("Called function is not function declaration");
            return null;
        }
        var calledFunction = (Statement_Function_Declaration) directCall.getCalledFunction();
        var callingComponent = LuaUtil.getComponent(directCall);
        var calledComponent = LuaUtil.getComponent(calledFunction);

        // if we call another of our own seffs we use their step behaviour
        if (callingComponent.equals(calledComponent)) {
            if (SeffHelper.needsSeffReconstruction(calledFunction)) {
                LOGGER.trace("Call classification: Internal call to SEFF " + calledFunction.getName());
                return reconstructInternalSeffCall(directCall);

            } else {
                LOGGER.trace("Call classification: Internal call to non-SEFF " + calledFunction.getName());
                var action = SeffFactory.eINSTANCE.createInternalAction();
                if (Config.descriptiveNames()) {
                    action.setEntityName("CALL_TO_NON-SEFF_" + calledFunction.getName());
                }
                return action;
            }
        }

        // external or library call
        var isCallToMockedFunction = calledComponent.getName()
            .equals(LuaLinkingService.MOCK_URI.path());
        if (isCallToMockedFunction) {
            // library call
            LOGGER.trace(String.format("Call classification: Library call %s ", calledFunction.getName()));
            return reconstructExternalStdlibCrownCall(directCall);
        }

        LOGGER.debug(String.format("Call classification: External call to SEFF %s ", calledFunction.getName()));
        return reconstructExternalSeffCall(directCall);
    }

    private AbstractAction reconstructFunctionCallToAction(Expression_Functioncall call) {
        if (call instanceof Expression_Functioncall_Direct directCall) {
            return reconstructDirectFunctionCallToAction(directCall);
        } else if (call instanceof Expression_Functioncall_Table tableCall) {
            // currently all table calls are mapped to internal actions
            LOGGER.trace("Call classification: Internal table call");
            var action = SeffFactory.eINSTANCE.createInternalAction();
            return action;
        }

        LOGGER.error("");
        return null;
    }

    private List<AbstractAction> reconstructFunctionCallStatementToActions(EObject statement) {
        List<AbstractAction> actions = new ArrayList<>();

        // all function calls in this statement (or the statement may be a call itself)
        var functionCalls = getFunctionCallsFromStatement(statement);

        AbstractAction predecessor = null;
        AbstractAction action = null;
        for (var functionCall : functionCalls) {
            action = reconstructFunctionCallToAction(functionCall);
            if (action != null) {
                actions.add(action);

                ActionUtil.chainActions(predecessor, action);
                predecessor = action;
            }
        }

        return actions;
    }

    private List<AbstractAction> reconstructActionsForStatement(EObject statement) {
        if (isControlFlowStatement(statement)) {
            var cfAction = reconstructControlFlowStatementToAction((Statement) statement);
            if (cfAction != null) {
                return List.of(cfAction);
            }
        } else {
            // statements which contains function calls, but is NO control flow statement
            var statementActions = reconstructFunctionCallStatementToActions(statement);
            if (statementActions.size() > 0) {
                return statementActions;
            }
        }
        return List.of();
    }

    public static List<AbstractAction> getActionsForStatement(EObject statement,
            EditableCorrespondenceModelView<ReactionsCorrespondence> cmv) {
        var instance = new ActionReconstruction(cmv);
        return instance.reconstructActionsForStatement(statement);
    }
}
