package mir.reactions.packageAndClassifiers;

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
    return new mir.routines.packageAndClassifiers.RoutinesFacadesProvider();
  }
  
  protected void setup() {
    this.addReaction(new mir.reactions.packageAndClassifiers.AddImportReactionReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.RemoveImportReactionReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.InterfaceMethodCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.ClassMethodCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.RemoveInterfaceMethodEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.RemoveClassMethodEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.ClassCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.InterfaceCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.RemoveClassEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.RemoveInterfaceEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.PackageCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.RemovePackageReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.TypeReferenceCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.TypeReferenceRemovedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.JavaPackageRenamedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.JavaInterfaceRenamedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
    this.addReaction(new mir.reactions.packageAndClassifiers.JavaClassRenamedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("packageAndClassifiers"))));
  }
}
