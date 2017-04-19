package mir.reactions.reactionsJavaToPcm.parserIntegrationReaction;

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
    this.addReaction(mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.ChangeFieldModifierEventParserReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.ChangeFieldModifierEventParserReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.RemoveFieldModifierEventParserReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.RemoveFieldModifierEventParserReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.AddMethodEventParserReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.AddMethodEventParserReaction(userInteracting));
  }
}
