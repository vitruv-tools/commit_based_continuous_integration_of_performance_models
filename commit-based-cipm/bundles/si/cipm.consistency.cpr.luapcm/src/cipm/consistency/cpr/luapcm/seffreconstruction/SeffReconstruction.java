package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.xtext.lua.LuaUtil;
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
public final class SeffReconstruction {
    private static final Logger LOGGER = Logger.getLogger(SeffReconstruction.class.getName());

    // TODO find a better default for this stochastic expression
    private static final String LOOP_COUNT_SPECIFICATION = "10";

    private EditableCorrespondenceModelView<ReactionsCorrespondence> correspondenceModelView;

    private SeffReconstruction(EditableCorrespondenceModelView<ReactionsCorrespondence> cmv) {
        this.correspondenceModelView = cmv;
    }

    private static boolean isControlFlowStatement(EObject eObj) {
        return (eObj instanceof Statement_If_Then_Else || eObj instanceof Statement_For
                || eObj instanceof Statement_While || eObj instanceof Statement_Repeat);
    }

    private BranchAction convertIfStatementToAction(Statement_If_Then_Else ifStatement) {
        var branchAction = SeffFactory.eINSTANCE.createBranchAction();
        return branchAction;
    }

    private LoopAction convertLoopStatementToAction(BlockWrapper statement) {
        var loopAction = SeffFactory.eINSTANCE.createLoopAction();
        loopAction.setEntityName(statement.toString());

        var randomLoopCount = CoreFactory.eINSTANCE.createPCMRandomVariable();
        randomLoopCount.setSpecification(LOOP_COUNT_SPECIFICATION);
        loopAction.setIterationCount_LoopAction(randomLoopCount);
        return loopAction;
    }

    private AbstractAction convertControlFlowStatementToAction(Statement statement) {
        if (statement instanceof Statement_If_Then_Else ifStatement) {
            return convertIfStatementToAction(ifStatement);
        } else if (statement instanceof BlockWrapper blockWrappingStatement) {
            return convertLoopStatementToAction(blockWrappingStatement);
        }

        LOGGER.error("Action reconstructon of control flow statement is not implemented");
        return null;
    }

    private static List<Expression_Functioncall_Direct> getFunctionCallsFromStatement(Statement statement) {
        if (statement instanceof Expression_Functioncall_Direct) {
            return List.of((Expression_Functioncall_Direct) statement);
        }
        return EcoreUtil2.getAllContentsOfType(statement, Expression_Functioncall_Direct.class);
    }

    private AbstractAction convertExternalDirectFunctionCallToAction(Expression_Functioncall_Direct directCall,
            Component callingComponent, Component calledComponent) {

        var calledFunction = directCall.getCalledFunction();
        if (calledFunction instanceof Statement_Function_Declaration calledFunctionDeclaration) {
            // external call
            var externalCallAction = SeffFactory.eINSTANCE.createExternalCallAction();

            // determine the signature of the called function
            // TODO PROBLEM this signature may not yet exist
            var calledSignature = CorrespondenceUtil.getCorrespondingEObjectByType(correspondenceModelView,
                    calledFunctionDeclaration, OperationSignature.class);
            if (calledSignature == null) {
                LOGGER.info(calledFunctionDeclaration.getName() + ": Signature does not exist");
            } else {
                externalCallAction.setCalledService_ExternalService(calledSignature);
            }

            ComponentSetInfoRegistry.getInfosForComponentSet(directCall)
                .getDeclarationToCallingActions()
                .put(calledFunctionDeclaration, externalCallAction);

            LOGGER.trace("Adding correspondence for external call " + externalCallAction.toString());
            correspondenceModelView.addCorrespondenceBetween(externalCallAction, calledComponent, null);

            return externalCallAction;
        }

        return null;
    }

    private AbstractAction convertDirectFunctionCallToAction(Expression_Functioncall_Direct directCall) {
        var calledFunction = directCall.getCalledFunction();
        if (calledFunction == null) {
            return null;
        }

        var callingComponent = LuaUtil.getComponent(directCall);
        var calledComponent = LuaUtil.getComponent(calledFunction);

        // if we call another of our own seffs we use their step behaviour
        if (callingComponent.equals(calledComponent)) {
            if (SeffHelper.needsSeffReconstruction(calledFunction)) {
                // internal call
                LOGGER.trace(String.format("Call classification: Internal call to SEFF %s ", calledFunction.getName()));
                var action = SeffFactory.eINSTANCE.createInternalCallAction();
                action.setEntityName(calledFunction.getName());
                // TODO implement this if needed
                action.setCalledResourceDemandingInternalBehaviour(null);
                return action;
            }

            LOGGER.trace(String.format("Call classification: Internal call to non-SEFF %s ", calledFunction.getName()));
            var action = SeffFactory.eINSTANCE.createInternalAction();
            action.setEntityName(calledFunction.getName());
            return action;
        }

        // external or library call
        var isCallToMockedFunction = calledComponent.getName()
            .equals(LuaLinkingService.MOCK_URI.path());
        if (isCallToMockedFunction) {
            // library call
            LOGGER.trace(String.format("Call classification: Library call %s ", calledFunction.getName()));
            var action = SeffFactory.eINSTANCE.createInternalAction();
            action.setEntityName(calledComponent.getName());
            return action;
        }

        LOGGER.trace(String.format("Call classification: External call to SEFF %s ", calledFunction.getName()));
        return convertExternalDirectFunctionCallToAction(directCall, callingComponent, calledComponent);
    }

    // TODO this is currently only implemented for direct functioncalls
    private AbstractAction convertFunctionCallToAction(Expression_Functioncall call) {
        if (call instanceof Expression_Functioncall_Direct directCall) {
            return convertDirectFunctionCallToAction(directCall);
        }
        LOGGER.error("Function classification for non-direct function calls is not yet implemented");
        return null;
    }

    private List<AbstractAction> convertFunctionCallStatementToActions(Statement statement) {
        List<AbstractAction> actions = new ArrayList<>();

        // all function calls in this statement (or the statement may be a call itself)
        var functionCalls = getFunctionCallsFromStatement(statement);

        // if statements contains function calls, but is NO control flow statement
        // TODO also do table stuff here
        AbstractAction predecessor = null;
        AbstractAction action = null;
        for (var functionCall : functionCalls) {
            action = convertFunctionCallToAction(functionCall);
            if (action != null) {
                action.setEntityName("CALL_TO " + functionCall.getCalledFunction()
                    .getName());
                actions.add(action);

                ActionUtil.chainActions(predecessor, action);
                predecessor = action;
            }
        }

        return actions;
    }

    private List<AbstractAction> convertStatementToActions(Statement statement) {
        if (isControlFlowStatement(statement)) {
            var cfAction = convertControlFlowStatementToAction(statement);
            if (cfAction != null) {
                return List.of(cfAction);
            }
        } else {
            // statements which contains function calls, but is NO control flow statement
            var statementActions = convertFunctionCallStatementToActions(statement);
            if (statementActions.size() > 0) {
                LOGGER.trace("Statement contains actions");
                return statementActions;
            }
        }
        return List.of();
    }

    public static List<AbstractAction> getActionsForStatement(Statement statement,
            EditableCorrespondenceModelView<ReactionsCorrespondence> cmv) {
        var instance = new SeffReconstruction(cmv);
        return instance.convertStatementToActions(statement);
    }
}
