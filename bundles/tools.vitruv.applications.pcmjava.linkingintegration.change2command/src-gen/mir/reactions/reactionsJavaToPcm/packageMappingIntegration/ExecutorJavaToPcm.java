package mir.reactions.reactionsJavaToPcm.packageMappingIntegration;

import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsExecutor;
import tools.vitruv.framework.userinteraction.UserInteracting;

@SuppressWarnings("all")
public class ExecutorJavaToPcm extends AbstractReactionsExecutor {
  public ExecutorJavaToPcm(final UserInteracting userInteracting) {
    super(userInteracting,
    	new JavaDomainProvider().getDomain(), 
    	new PcmDomainProvider().getDomain());
  }
  
  protected void setup() {
    tools.vitruv.framework.userinteraction.UserInteracting userInteracting = getUserInteracting();
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddImportReactionReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddImportReactionReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveImportReactionReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveImportReactionReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RenameMethodReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RenameMethodReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveMethodEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveMethodEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddMethodEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddMethodEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.CreateMetodParameterEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.CreateMetodParameterEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.MethodParameterNameChangeEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.MethodParameterNameChangeEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodTypeEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodTypeEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddFieldEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddFieldEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldTypeEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldTypeEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldModifierEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeFieldModifierEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldModifierEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveFieldModifierEventReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodModifierEventReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.ChangeMethodModifierEventReaction(userInteracting));
  }
}
