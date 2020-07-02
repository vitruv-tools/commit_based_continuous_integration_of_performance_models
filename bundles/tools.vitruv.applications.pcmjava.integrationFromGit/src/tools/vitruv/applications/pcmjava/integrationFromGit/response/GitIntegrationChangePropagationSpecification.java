package tools.vitruv.applications.pcmjava.integrationFromGit.response;

import mir.reactions.allReactions.AllReactionsChangePropagationSpecification;
import mir.reactions.classifierBody.ClassifierBodyChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.TuidUpdatePreprocessor;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor;
import tools.vitruv.applications.pcmjava.util.java2pcm.Java2PcmPackagePreprocessor;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;


/**
 * Change propagation specification used for integrated projects in order to propagate changes on JaMoPP models to the PCM models.
 * It includes change propagation rules for several types of changes on package and compilation unit levels as well as changes in classes and interfaces,
 * but NOT on method bodies. The change propagation rules are defined in 
 * tools.vitruv.applications.pcmjava.integrationFromGit.response.internal.ClassifierBody.reactions and
 * tools.vitruv.applications.pcmjava.integrationFromGit.response.internal.MethodBody.reactions
 * The generated files for this rules can be found in src-gen folder.
 * The defined rules are based on rules from
 * tools.vitruv.applications.pcmjava.linkingintegration.change2command.internal.response.PackageMappingIntegration.reactions and
 * tools.vitruv.applications.pcmjava.pojotransformations.java2pcm
 * 
 * @author Ilia Chupakhin
 *
 */
@SuppressWarnings("all")
public class GitIntegrationChangePropagationSpecification extends AllReactionsChangePropagationSpecification{
	  
	protected void setup() {
		TuidUpdatePreprocessor _tuidUpdatePreprocessor = new TuidUpdatePreprocessor();
		this.addChangeMainprocessor(_tuidUpdatePreprocessor);
		super.setup();
		Java2PcmPackagePreprocessor _java2PcmPackagePreprocessor = new Java2PcmPackagePreprocessor();
		this.addChangePreprocessor(_java2PcmPackagePreprocessor);
		//Rules for changes on method bodies for non-integrated projects. Disabled by now. 
		//That means, the projects which were created from scratch with Vitruv and therefore have a specific structure required by Vitruv.
		//These rules must be adapted for integrated projects, that do not have structure required by Vitruv.
		//Java2PcmPackageMappingMethodBodyChangePreprocessor _java2PcmPackageMappingMethodBodyChangePreprocessor = new Java2PcmPackageMappingMethodBodyChangePreprocessor(getUserInteractor());
		//this.addChangePreprocessor(_java2PcmPackageMappingMethodBodyChangePreprocessor);
	}
}
