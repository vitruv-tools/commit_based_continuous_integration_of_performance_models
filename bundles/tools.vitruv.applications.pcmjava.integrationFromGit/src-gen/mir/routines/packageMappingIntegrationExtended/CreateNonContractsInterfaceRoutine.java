package mir.routines.packageMappingIntegrationExtended;

import java.io.IOException;
import mir.routines.packageMappingIntegrationExtended.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.containers.CompilationUnit;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.Java2PcmUserSelection;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.userinteraction.UserInteractionOptions;

/**
 * *
 * nUser selects if interface should be created if interface was not created into contract package.
 *  
 */
@SuppressWarnings("all")
public class CreateNonContractsInterfaceRoutine extends AbstractRepairRoutineRealization {
  private CreateNonContractsInterfaceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage, @Extension final RoutinesFacade _routinesFacade) {
      String _name = javaInterface.getName();
      String _plus = ("The created interface is not in the contracts packages. Should an architectural interface be created for the interface " + _name);
      final String userMsg = (_plus + " ?");
      String _message = Java2PcmUserSelection.SELECT_CREATE_INTERFACE_NOT_IN_CONTRACTS.getMessage();
      String _message_1 = Java2PcmUserSelection.SELECT_DONT_CREATE_INTERFACE_NOT_IN_CONTRACTS.getMessage();
      final String[] selections = new String[] { _message, _message_1 };
      final Integer selected = this.userInteractor.getSingleSelectionDialogBuilder().message(userMsg).choices(((Iterable<String>)Conversions.doWrapArray(selections))).windowModality(UserInteractionOptions.WindowModality.MODAL).startInteraction();
      int _selection = Java2PcmUserSelection.SELECT_CREATE_INTERFACE_NOT_IN_CONTRACTS.getSelection();
      boolean _equals = ((selected).intValue() == _selection);
      if (_equals) {
        _routinesFacade.createInterface(javaInterface, compilationUnit, javaPackage);
      }
    }
  }
  
  public CreateNonContractsInterfaceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegrationExtended.CreateNonContractsInterfaceRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaInterface = javaInterface;this.compilationUnit = compilationUnit;this.javaPackage = javaPackage;
  }
  
  private Interface javaInterface;
  
  private CompilationUnit compilationUnit;
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateNonContractsInterfaceRoutine with input:");
    getLogger().debug("   javaInterface: " + this.javaInterface);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    getLogger().debug("   javaPackage: " + this.javaPackage);
    
    userExecution.callRoutine1(javaInterface, compilationUnit, javaPackage, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
