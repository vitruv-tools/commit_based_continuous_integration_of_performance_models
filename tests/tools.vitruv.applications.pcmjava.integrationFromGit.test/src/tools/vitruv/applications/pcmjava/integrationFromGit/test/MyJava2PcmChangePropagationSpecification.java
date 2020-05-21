package tools.vitruv.applications.pcmjava.integrationFromGit.test;

import mir.reactions.packageMappingIntegration.PackageMappingIntegrationChangePropagationSpecification;
import mir.reactions.parserIntegrationReaction.ParserIntegrationReactionChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.Java2PcmChangePropagationSpecification;

public class MyJava2PcmChangePropagationSpecification extends Java2PcmChangePropagationSpecification {
	
	@Override
	protected void setup() {
		super.setup();
		this.addChangePreprocessor(
				new PackageMappingIntegrationChangePropagationSpecification()
				//new ParserIntegrationReactionChangePropagationSpecification()
				);
	}
}
