package mir.routines.remToPCM;

import java.io.IOException;
import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.vsum.util.ReactionHelper;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class EnrichContainerRoutine extends AbstractRepairRoutineRealization {
  private EnrichContainerRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final ResourceContainer container, final RuntimeResourceContainer corr, final ProcessingResourceSpecification processingRate, final PCMRandomVariable processingRateStoex, @Extension final RoutinesFacade _routinesFacade) {
      processingRateStoex.setSpecification("1");
      processingRate.setMTTF(0);
      processingRate.setMTTR(0);
      processingRate.setNumberOfReplicas(1);
      processingRate.setProcessingRate_ProcessingResourceSpecification(processingRateStoex);
      processingRate.setActiveResourceType_ActiveResourceSpecification(ReactionHelper.getCPUProcessingResourceType());
      processingRate.setSchedulingPolicy(ReactionHelper.getProcessSharingSchedulingPolicy());
      container.setEntityName(corr.getHostname());
      EList<ProcessingResourceSpecification> _activeResourceSpecifications_ResourceContainer = container.getActiveResourceSpecifications_ResourceContainer();
      _activeResourceSpecifications_ResourceContainer.add(processingRate);
    }
  }
  
  public EnrichContainerRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final ResourceContainer container, final RuntimeResourceContainer corr) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.remToPCM.EnrichContainerRoutine.ActionUserExecution(getExecutionState(), this);
    this.container = container;this.corr = corr;
  }
  
  private ResourceContainer container;
  
  private RuntimeResourceContainer corr;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine EnrichContainerRoutine with input:");
    getLogger().debug("   container: " + this.container);
    getLogger().debug("   corr: " + this.corr);
    
    org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification processingRate = org.palladiosimulator.pcm.resourceenvironment.impl.ResourceenvironmentFactoryImpl.eINSTANCE.createProcessingResourceSpecification();
    notifyObjectCreated(processingRate);
    
    org.palladiosimulator.pcm.core.PCMRandomVariable processingRateStoex = org.palladiosimulator.pcm.core.impl.CoreFactoryImpl.eINSTANCE.createPCMRandomVariable();
    notifyObjectCreated(processingRateStoex);
    
    userExecution.callRoutine1(container, corr, processingRate, processingRateStoex, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
