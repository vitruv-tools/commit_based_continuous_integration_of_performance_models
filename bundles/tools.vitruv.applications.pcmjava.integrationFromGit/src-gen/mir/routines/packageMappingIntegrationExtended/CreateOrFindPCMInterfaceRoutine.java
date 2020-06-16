package mir.routines.packageMappingIntegrationExtended;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import mir.routines.packageMappingIntegrationExtended.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersPackage;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindPCMInterfaceRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindPCMInterfaceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourcePcmRepository(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package containingPackage) {
      return containingPackage;
    }
    
    public EObject getCorrepondenceSource1(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package containingPackage, final Optional<Repository> pcmRepository) {
      return javaInterface;
    }
    
    public String getRetrieveTag1(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package containingPackage) {
      return "contracts";
    }
    
    public boolean getCorrespondingModelElementsPreconditionContainingPackage(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package containingPackage) {
      final Function1<CompilationUnit, String> _function = new Function1<CompilationUnit, String>() {
        public String apply(final CompilationUnit it) {
          String _namespacesAsString = it.getNamespacesAsString();
          String _plus = (_namespacesAsString + ".");
          String _name = it.getName();
          return (_plus + _name);
        }
      };
      List<String> _map = ListExtensions.<CompilationUnit, String>map(containingPackage.getCompilationUnits(), _function);
      String _namespacesAsString = compilationUnit.getNamespacesAsString();
      String _plus = (_namespacesAsString + ".");
      String _name = compilationUnit.getName();
      String _plus_1 = (_plus + _name);
      boolean _contains = _map.contains(_plus_1);
      return _contains;
    }
    
    public EObject getCorrepondenceSourceContainingPackage(final Interface javaInterface, final CompilationUnit compilationUnit) {
      return ContainersPackage.Literals.PACKAGE;
    }
    
    public void callRoutine1(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package containingPackage, final Optional<Repository> pcmRepository, @Extension final RoutinesFacade _routinesFacade) {
      boolean _isPresent = pcmRepository.isPresent();
      if (_isPresent) {
        _routinesFacade.createOrFindContractsInterface(javaInterface, compilationUnit, containingPackage, pcmRepository.get());
      } else {
        _routinesFacade.createNonContractsInterface(javaInterface, compilationUnit, containingPackage);
      }
    }
  }
  
  public CreateOrFindPCMInterfaceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Interface javaInterface, final CompilationUnit compilationUnit) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegrationExtended.CreateOrFindPCMInterfaceRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaInterface = javaInterface;this.compilationUnit = compilationUnit;
  }
  
  private Interface javaInterface;
  
  private CompilationUnit compilationUnit;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindPCMInterfaceRoutine with input:");
    getLogger().debug("   javaInterface: " + this.javaInterface);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    
    org.emftext.language.java.containers.Package containingPackage = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceContainingPackage(javaInterface, compilationUnit), // correspondence source supplier
    	org.emftext.language.java.containers.Package.class,
    	(org.emftext.language.java.containers.Package _element) -> userExecution.getCorrespondingModelElementsPreconditionContainingPackage(javaInterface, compilationUnit, _element), // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (containingPackage == null) {
    	return false;
    }
    registerObjectUnderModification(containingPackage);
    	Optional<org.palladiosimulator.pcm.repository.Repository> pcmRepository = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourcePcmRepository(javaInterface, compilationUnit, containingPackage), // correspondence source supplier
    		org.palladiosimulator.pcm.repository.Repository.class,
    		(org.palladiosimulator.pcm.repository.Repository _element) -> true, // correspondence precondition checker
    		userExecution.getRetrieveTag1(javaInterface, compilationUnit, containingPackage), 
    		false // asserted
    		)
    );
    registerObjectUnderModification(pcmRepository.isPresent() ? pcmRepository.get() : null);
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(javaInterface, compilationUnit, containingPackage, pcmRepository), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationInterface.class,
    	(org.palladiosimulator.pcm.repository.OperationInterface _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    userExecution.callRoutine1(javaInterface, compilationUnit, containingPackage, pcmRepository, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
