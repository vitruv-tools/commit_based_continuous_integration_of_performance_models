package tools.vitruv.applications.pcmjava.integrationFromGit.test;

import mir.reactions.packageMappingIntegration.PackageMappingIntegrationChangePropagationSpecification;
import mir.reactions.parserIntegrationReaction.ParserIntegrationReactionChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.linkingintegration.change2command.CodeIntegrationChangeProcessor;
import tools.vitruv.applications.pcmjava.linkingintegration.change2command.Java2PcmIntegrationChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.Java2PcmChangePropagationSpecification;



public class MyJava2PcmChangePropagationSpecification extends PackageMappingIntegrationChangePropagationSpecification /*Java2PcmIntegrationChangePropagationSpecification*/ {
	
	@Override
	protected void setup() {
		super.setup();
		this.addChangePreprocessor(
				new CodeIntegrationChangeProcessor()
				//new PackageMappingIntegrationChangePropagationSpecification()
				//new ParserIntegrationReactionChangePropagationSpecification()
				);
	}
}

/*
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
*/