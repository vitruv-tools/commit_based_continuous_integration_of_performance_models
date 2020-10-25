package tools.vitruv.applications.pcmjava.integrationFromGit.response;

import mir.reactions.allReactions.AllReactionsChangePropagationSpecification;
import mir.reactions.classifierBody.ClassifierBodyChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.integrationFromGit.propagation.GitJava2PcmPackagePreprocessor;
import tools.vitruv.applications.pcmjava.integrationFromGit.propagation.GitTuidUpdatePreprocessor;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.TuidUpdatePreprocessor;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor;
import tools.vitruv.applications.pcmjava.util.java2pcm.Java2PcmPackagePreprocessor;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;


/**
 * Change propagation specification used for integrated projects in order to propagate changes on JaMoPP models to the PCM models using state based resolution strategy.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor.xtend
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 *
 */
@SuppressWarnings("all")
public class GitIntegrationChangePropagationSpecificationStateBased extends AllReactionsChangePropagationSpecification{
	  
	protected void setup() {
		GitTuidUpdatePreprocessor _tuidUpdatePreprocessor = new GitTuidUpdatePreprocessor();
		this.addChangeMainprocessor(_tuidUpdatePreprocessor);
		super.setup();
		GitJava2PcmPackagePreprocessor _java2PcmPackagePreprocessor = new GitJava2PcmPackagePreprocessor();
		this.addChangePreprocessor(_java2PcmPackagePreprocessor);
		//Change propagation specification for changes on method bodies
		Java2PcmPackageMappingMethodBodyChangePreprocessor _java2PcmPackageMappingMethodBodyChangePreprocessor = new Java2PcmPackageMappingMethodBodyChangePreprocessor(getUserInteractor());
		this.addChangePreprocessor(_java2PcmPackageMappingMethodBodyChangePreprocessor);
	}
}
