package mir.reactions.reactionsJavaToPcm.packageMappingIntegration;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsExecutor;

@SuppressWarnings("all")
public class ExecutorJavaToPcm extends AbstractReactionsExecutor {
  public ExecutorJavaToPcm() {
    super(new tools.vitruv.domains.java.JavaDomainProvider().getDomain(), 
    	new tools.vitruv.domains.pcm.PcmDomainProvider().getDomain());
  }
  
  protected void setup() {
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddImportReactionReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveImportReactionReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RenameMethodReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveMethodEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddMethodEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.CreateMetodParameterEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.MethodParameterNameChangeEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodTypeEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddFieldEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldTypeEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldModifierEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldModifierEventReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodModifierEventReaction());
  }
}
