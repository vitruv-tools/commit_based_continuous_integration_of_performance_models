package tools.vitruv.applications.pcmjava.integrationFromGit.response;

import mir.reactions.allReactions.AllReactionsChangePropagationSpecification;
import mir.reactions.classifierBody.ClassifierBodyChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.TuidUpdatePreprocessor;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor;
import tools.vitruv.applications.pcmjava.util.java2pcm.Java2PcmPackagePreprocessor;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;


/**
 * Change propagation specification used for integrated projects in order to propagate changes on JaMoPP models to the PCM models.
 * It includes change propagation rules for several types of changes on package and compilation unit levels as well as changes in classes and interfaces. 
 * The change propagation rules are defined in: 
 * tools.vitruv.applications.pcmjava.integrationFromGit.response.internal.ClassifierBody.reactions and
 * tools.vitruv.applications.pcmjava.integrationFromGit.response.internal.PackageAndClassifiers.reactions
 * The change propagation for changes on method bodies is processed by:
 * tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor.xtend
 * The generated files for this rules can be found in src-gen folder.
 * The defined rules are based on rules from
 * tools.vitruv.applications.pcmjava.linkingintegration.change2command.internal.response.PackageMappingIntegration.reactions and
 * tools.vitruv.applications.pcmjava.pojotransformations.java2pcm
 * tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor.xtend
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
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
		//Change propagation specification for changes on method bodies
		Java2PcmPackageMappingMethodBodyChangePreprocessor _java2PcmPackageMappingMethodBodyChangePreprocessor = new Java2PcmPackageMappingMethodBodyChangePreprocessor(getUserInteractor());
		this.addChangePreprocessor(_java2PcmPackageMappingMethodBodyChangePreprocessor);
	}
}
