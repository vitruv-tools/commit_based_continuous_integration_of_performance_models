package mir.reactions.reactionsJavaToPcm.parserIntegrationReaction;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsExecutor;
import tools.vitruv.framework.userinteraction.UserInteracting;

@SuppressWarnings("all")
public class ExecutorJavaToPcm extends AbstractReactionsExecutor {
  public ExecutorJavaToPcm(final UserInteracting userInteracting) {
    super(userInteracting, new tools.vitruv.framework.util.datatypes.MetamodelPair(org.emftext.language.java.impl.JavaPackageImpl.eNS_URI, org.palladiosimulator.pcm.impl.PcmPackageImpl.eNS_URI));
  }
  
  protected void setup() {
    tools.vitruv.framework.userinteraction.UserInteracting userInteracting = getUserInteracting();
    this.addReaction(mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.ChangeFieldModifierEventParserReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.ChangeFieldModifierEventParserReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.RemoveFieldModifierEventParserReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.RemoveFieldModifierEventParserReaction(userInteracting));
    this.addReaction(mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.AddMethodEventParserReaction.getExpectedChangeType(), new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.AddMethodEventParserReaction(userInteracting));
  }
}
