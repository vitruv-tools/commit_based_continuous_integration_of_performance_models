package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateBasicComponentRoutine extends AbstractRepairRoutineRealization {
  private CreateBasicComponentRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void updatePcmBasicComponentElement(final org.emftext.language.java.containers.Package javaPackage, final String name, final String rootPackageName, final BasicComponent pcmBasicComponent) {
      pcmBasicComponent.setEntityName(name);
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package javaPackage, final String name, final String rootPackageName, final BasicComponent pcmBasicComponent, @Extension final RoutinesFacade _routinesFacade) {
      final Repository pcmRepository = IterableExtensions.<Repository>toList(this.correspondenceModel.<Repository>getAllEObjectsOfTypeInCorrespondences(Repository.class)).get(0);
      _routinesFacade.addCorrespondenceAndUpdateRepository(pcmBasicComponent, javaPackage, pcmRepository);
    }
  }
  
  public CreateBasicComponentRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage, final String name, final String rootPackageName) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateBasicComponentRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;this.name = name;this.rootPackageName = rootPackageName;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private String name;
  
  private String rootPackageName;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateBasicComponentRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   name: " + this.name);
    getLogger().debug("   rootPackageName: " + this.rootPackageName);
    
    org.palladiosimulator.pcm.repository.BasicComponent pcmBasicComponent = org.palladiosimulator.pcm.repository.impl.RepositoryFactoryImpl.eINSTANCE.createBasicComponent();
    notifyObjectCreated(pcmBasicComponent);
    userExecution.updatePcmBasicComponentElement(javaPackage, name, rootPackageName, pcmBasicComponent);
    
    userExecution.callRoutine1(javaPackage, name, rootPackageName, pcmBasicComponent, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
