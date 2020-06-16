package mir.reactions.packageMappingIntegrationExtended;

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
    return new mir.routines.packageMappingIntegrationExtended.RoutinesFacadesProvider();
  }
  
  protected void setup() {
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.AddImportReactionReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.RemoveImportReactionReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.InterfaceMethodCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.ClassMethodCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.RemoveInterfaceMethodEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.RemoveClassMethodEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.ClassCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.PackageCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.JavaPackageRenamedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.InterfaceCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.JavaInterfaceRenamedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.JavaClassRenamedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
    this.addReaction(new mir.reactions.packageMappingIntegrationExtended.TypeReferenceCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageMappingIntegrationExtended"))));
  }
}
