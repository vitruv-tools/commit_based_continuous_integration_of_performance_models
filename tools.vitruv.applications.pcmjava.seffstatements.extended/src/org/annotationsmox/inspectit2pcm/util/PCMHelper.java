package org.annotationsmox.inspectit2pcm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.annotationsmox.inspectit2pcm.model.SQLStatement;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.SeffPackage;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.palladiosimulator.pcm.seff.seff_performance.ResourceCall;
import org.palladiosimulator.pcm.seff.seff_performance.SeffPerformanceFactory;
import org.somox.util.DefaultResourceEnvironment;

/**
 * Static helper methods to simplify working with PCM models.
 * 
 * @author Philipp Merkle
 *
 */
public class PCMHelper {

    private PCMHelper() {
        // do not instantiate
    }

    public static PCMRandomVariable createPCMRandomVariable(String stoEx) {
        PCMRandomVariable rv = CoreFactory.eINSTANCE.createPCMRandomVariable();
        rv.setSpecification(stoEx);
        return rv;
    }

    public static PCMRandomVariable createPCMRandomVariable(int count) {
        PCMRandomVariable rv = CoreFactory.eINSTANCE.createPCMRandomVariable();
        rv.setSpecification(Integer.toString(count));
        return rv;
    }

    public static PCMRandomVariable createPCMRandomVariable(double demand) {
        PCMRandomVariable rv = CoreFactory.eINSTANCE.createPCMRandomVariable();
        rv.setSpecification(Double.toString(demand));
        return rv;
    }

    public static ParametricResourceDemand createParametricResourceDemandCPU(PCMRandomVariable demand) {
        ParametricResourceDemand prd = SeffPerformanceFactory.eINSTANCE.createParametricResourceDemand();
        prd.setSpecification_ParametericResourceDemand(demand);

        ProcessingResourceType cpu = DefaultResourceEnvironment.getCPUProcessingResourceType();
        prd.setRequiredResource_ParametricResourceDemand(cpu);

        return prd;
    }
    
    public static ParametricResourceDemand createParametricResourceDemandDELAY(PCMRandomVariable demand) {
        ParametricResourceDemand prd = SeffPerformanceFactory.eINSTANCE.createParametricResourceDemand();
        prd.setSpecification_ParametericResourceDemand(demand);

        ProcessingResourceType delay = DefaultResourceEnvironment.getDelayProcessingResourceType();
        prd.setRequiredResource_ParametricResourceDemand(delay);

        return prd;
    }

    public static StartAction findStartAction(ResourceDemandingBehaviour behaviour) {
        return (StartAction) behaviour.getSteps_Behaviour().stream().filter(a -> a instanceof StartAction).findFirst()
                .get();
    }

    public static ExternalCallAction findNextExternalCall(AbstractAction startingPoint) {
        AbstractAction currentAction = startingPoint;
        while (currentAction.getSuccessor_AbstractAction() != null) {
            if (currentAction instanceof ExternalCallAction) {
                return (ExternalCallAction) currentAction;
            }
            currentAction = currentAction.getSuccessor_AbstractAction();
        }
        return null;
    }

    public static ExternalCallAction findFirstExternalCall(ResourceDemandingBehaviour behaviour) {
        return findNextExternalCall(findStartAction(behaviour));
    }

    public static StopAction findStopAction(ResourceDemandingBehaviour behaviour) {
        return (StopAction) behaviour.getSteps_Behaviour().stream().filter(a -> a instanceof StopAction).findFirst()
                .get();
    }

    public static List<ExternalCallAction> findExternalCallActions(ResourceDemandingBehaviour behaviour) {
        List<ExternalCallAction> calls = behaviour.getSteps_Behaviour().stream()
                .filter(a -> a instanceof ExternalCallAction).map(a -> (ExternalCallAction) a)
                .collect(Collectors.toList());

        calls.addAll(findExternalCallActionsInBranches(behaviour));

        return calls;
    }

    private static List<ExternalCallAction> findExternalCallActionsInBranches(ResourceDemandingBehaviour behaviour) {
        final List<BranchAction> branches = behaviour.getSteps_Behaviour().stream()
                .filter(a -> a instanceof BranchAction).map(a -> (BranchAction) a).collect(Collectors.toList());

        // collect external call actions in branch transitions
        List<ExternalCallAction> calls = new ArrayList<>();
        for (BranchAction b : branches) {
            for (AbstractBranchTransition transition : b.getBranches_Branch()) {
                calls.addAll(findExternalCallActions(transition.getBranchBehaviour_BranchTransition()));
            }
        }

        return calls;
    }

    public static void insertSQLStatementAsResourceCall(InternalAction action, SQLStatement stmt) {
        ResourceCall call = SeffPerformanceFactory.eINSTANCE.createResourceCall();
        call.setEntityName(stmt.getSql());
        call.setNumberOfCalls__ResourceCall(createPCMRandomVariable(1));
        // TODO set resource interface
        action.getResourceCall__Action().add(call);
    }

    public static InternalAction createInternalActionStub(ResourceDemandingBehaviour container, String name) {
        InternalAction ia = SeffFactory.eINSTANCE.createInternalAction();
        ia.setResourceDemandingBehaviour_AbstractAction(container);
        ia.setEntityName(name);
        ia.getResourceDemand_Action().add(PCMHelper.createParametricResourceDemandCPU(createPCMRandomVariable(0)));
        return ia;
    }

    public static String entityToString(Entity entity) {
        return entity.eClass().getName() + ": " + entity.getEntityName() + " [" + entity.getId() + "]";
    }

    private static ResourceDemandingSEFF findSeffForBehaviour(ResourceDemandingBehaviour behaviour) {
        if (SeffPackage.eINSTANCE.getResourceDemandingSEFF().isInstance(behaviour)) {
            return (ResourceDemandingSEFF) behaviour;
        } else if (behaviour.getAbstractBranchTransition_ResourceDemandingBehaviour() != null) {
            AbstractBranchTransition transition = behaviour.getAbstractBranchTransition_ResourceDemandingBehaviour();
            BranchAction branch = transition.getBranchAction_AbstractBranchTransition();
            return findSeffForBehaviour(branch.getResourceDemandingBehaviour_AbstractAction());
        } else {
            throw new RuntimeException("Unexpected behaviour type: " + behaviour.eClass());
        }
    }

    public static ResourceDemandingSEFF findSeffForInternalAction(InternalAction action) {
        ResourceDemandingBehaviour behaviour = action.getResourceDemandingBehaviour_AbstractAction();
        return findSeffForBehaviour(behaviour);
    }

    public static void replaceAction(AbstractAction replaceAction, ResourceDemandingBehaviour behaviour,
            boolean keepReplaceAction) {
        // first collect all actions in a new ArrayList to avoid ConcurrentModificationException
        // thrown by EMF
        List<AbstractAction> insertActions = new ArrayList<>(behaviour.getSteps_Behaviour());

        // adjust container (ResourceDemandingBehaviour) for all actions to be inserted
        ResourceDemandingBehaviour container = replaceAction.getResourceDemandingBehaviour_AbstractAction();
        moveToResourceDemandingBehaviour(insertActions, container);

        AbstractAction actionBeforeReplace = replaceAction.getPredecessor_AbstractAction();
        AbstractAction firstInsertedAction = PCMHelper.findStartAction(behaviour).getSuccessor_AbstractAction();
        actionBeforeReplace.setSuccessor_AbstractAction(firstInsertedAction);

        AbstractAction actionAfterReplace = replaceAction.getSuccessor_AbstractAction();
        AbstractAction lastInsertedAction = PCMHelper.findStopAction(behaviour).getPredecessor_AbstractAction();
        actionAfterReplace.setPredecessor_AbstractAction(lastInsertedAction);

        if (!keepReplaceAction) {
            // remove action that has been replaced
            replaceAction.setResourceDemandingBehaviour_AbstractAction(null);
        } else {
            insertAfter(replaceAction, lastInsertedAction);
        }
    }

    /**
     * Moves the actions in {@code insertActions} to the ResourceDemandingBehaviour identified by
     * {@code behaviour}.
     * <p>
     * Actions of the types {@link StartAction} and {@link StopAction} are ignored.
     * 
     * @param insertActions
     * @param behaviour
     */
    private static void moveToResourceDemandingBehaviour(List<AbstractAction> insertActions,
            ResourceDemandingBehaviour behaviour) {
        for (AbstractAction insertAction : insertActions) {
            // ignore Start and Stop actions
            if (insertAction instanceof StartAction || insertAction instanceof StopAction) {
                continue;
            }
            insertAction.setResourceDemandingBehaviour_AbstractAction(behaviour);
        }
    }

    /**
     * Inserts the specified action as the predecessor of {@code reference} action.
     * 
     * @param insert
     *            the action to be inserted
     * @param reference
     *            the reference action
     */
    public static void insertBefore(AbstractAction insert, AbstractAction reference) {
        AbstractAction oldPredecessorOfReference = reference.getPredecessor_AbstractAction();
        insert.setPredecessor_AbstractAction(oldPredecessorOfReference);
        reference.setPredecessor_AbstractAction(insert);
    }

    /**
     * Inserts the specified action as the successor of {@code reference} action.
     * 
     * @param insert
     *            the action to be inserted
     * @param reference
     *            the reference action
     */
    public static void insertAfter(AbstractAction insert, AbstractAction reference) {
        AbstractAction oldSuccessorOfReference = reference.getSuccessor_AbstractAction();
        reference.setSuccessor_AbstractAction(insert);
        insert.setSuccessor_AbstractAction(oldSuccessorOfReference);
    }

    public static void ensureUniqueKeys(Map<? extends Entity, ?> map) {
        Set<String> seenKeys = new HashSet<>();
        for (Entity key : map.keySet()) {
            boolean added = seenKeys.add(key.getId());
            // if key was contained already
            if (!added) {
                throw new RuntimeException("Entity " + entityToString(key)
                        + " is contained more than once as a key because there are multiple objects for the same entity");
            }
        }
    }

}
