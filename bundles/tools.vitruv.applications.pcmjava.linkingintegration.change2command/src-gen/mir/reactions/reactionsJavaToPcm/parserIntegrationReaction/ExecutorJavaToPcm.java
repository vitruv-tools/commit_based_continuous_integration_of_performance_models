package mir.reactions.reactionsJavaToPcm.parserIntegrationReaction;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsExecutor;

@SuppressWarnings("all")
public class ExecutorJavaToPcm extends AbstractReactionsExecutor {
  public ExecutorJavaToPcm() {
    super(new tools.vitruv.domains.java.JavaDomainProvider().getDomain(), 
    	new tools.vitruv.domains.pcm.PcmDomainProvider().getDomain());
  }
  
  protected void setup() {
    this.addReaction(new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.ChangeFieldModifierEventParserReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.RemoveFieldModifierEventParserReaction());
    this.addReaction(new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.AddMethodEventParserReaction());
  }
}
