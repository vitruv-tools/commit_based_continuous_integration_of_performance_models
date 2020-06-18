package tools.vitruv.applications.pcmjava.integrationFromGit.response;

import mir.reactions.allReactions.AllReactionsChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.TuidUpdatePreprocessor;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor;
import tools.vitruv.applications.pcmjava.util.java2pcm.Java2PcmPackagePreprocessor;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;


@SuppressWarnings("all")
public class GitIntegrationChangePropagationSpecification extends AllReactionsChangePropagationSpecification{
	  
	protected void setup() {
		TuidUpdatePreprocessor _tuidUpdatePreprocessor = new TuidUpdatePreprocessor();
		this.addChangeMainprocessor(_tuidUpdatePreprocessor);
		super.setup();
		Java2PcmPackagePreprocessor _java2PcmPackagePreprocessor = new Java2PcmPackagePreprocessor();
		this.addChangePreprocessor(_java2PcmPackagePreprocessor);
		//Java2PcmPackageMappingMethodBodyChangePreprocessor _java2PcmPackageMappingMethodBodyChangePreprocessor = new Java2PcmPackageMappingMethodBodyChangePreprocessor(getUserInteractor());
		//this.addChangePreprocessor(_java2PcmPackageMappingMethodBodyChangePreprocessor);
	}
}
