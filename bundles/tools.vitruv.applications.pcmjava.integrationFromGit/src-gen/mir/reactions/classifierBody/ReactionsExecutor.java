package mir.reactions.classifierBody;

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
    return new mir.routines.classifierBody.RoutinesFacadesProvider();
  }
  
  protected void setup() {
    this.addReaction(new mir.reactions.classifierBody.MemberRenamedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.ParameterCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.ParameterDeletedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.ParameterNameChangedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.FieldCreatedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.ChangeFieldTypeEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.RemoveFieldEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.JavaReturnTypeChangedReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.ChangeFieldModifierEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
    this.addReaction(new mir.reactions.classifierBody.RemoveFieldModifierEventReaction(this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("classifierBody"))));
  }
}
