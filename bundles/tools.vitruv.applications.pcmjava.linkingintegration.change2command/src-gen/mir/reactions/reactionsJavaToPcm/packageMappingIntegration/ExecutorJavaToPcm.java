package mir.reactions.reactionsJavaToPcm.packageMappingIntegration;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsExecutor;

@SuppressWarnings("all")
public class ExecutorJavaToPcm extends AbstractReactionsExecutor {
  public ExecutorJavaToPcm() {
    super(new tools.vitruv.domains.java.JavaDomainProvider().getDomain(), 
    	new tools.vitruv.domains.pcm.PcmDomainProvider().getDomain());
  }
  
  protected void setup() {
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddImportReactionReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddImportReactionReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveImportReactionReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveImportReactionReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RenameMethodReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RenameMethodReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveMethodEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveMethodEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddMethodEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddMethodEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.CreateMetodParameterEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.CreateMetodParameterEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.MethodParameterNameChangeEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.MethodParameterNameChangeEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodTypeEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodTypeEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddFieldEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddFieldEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldTypeEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldTypeEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldModifierEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldModifierEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldModifierEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldModifierEventReaction());
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodModifierEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodModifierEventReaction());
  }
}
