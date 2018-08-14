package mir.reactions.packageMappingIntegration;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsExecutor;
import tools.vitruv.extensions.dslsruntime.reactions.RoutinesFacadesProvider;
import tools.vitruv.extensions.dslsruntime.reactions.structure.ReactionsImportPath;

@SuppressWarnings("all")
class ReactionsExecutor extends AbstractReactionsExecutor {
  public ReactionsExecutor() {
    super(new tools.vitruv.domains.java.JavaDomainProvider().getDomain(), 
    	new tools.vitruv.domains.pcm.PcmDomainProvider().getDomain());
  }
  
  protected RoutinesFacadesProvider createRoutinesFacadesProvider() {
    return new mir.routines.packageMappingIntegration.RoutinesFacadesProvider();
  }
  
  protected void setup() {
    this.addReaction(new mir.reactions.packageMappingIntegration.AddImportReactionReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.RemoveImportReactionReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.RenameMethodReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.RemoveMethodEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.AddMethodEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.CreateMetodParameterEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.MethodParameterNameChangeEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.ChangeMethodTypeEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.RemoveFieldEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.AddFieldEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.ChangeFieldTypeEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.ChangeFieldModifierEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.RemoveFieldModifierEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
    this.addReaction(new mir.reactions.packageMappingIntegration.ChangeMethodModifierEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegration"))));
  }
}
