package cipm.consistency.cpr.javapcm;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.cpr.javaim.Java2ImChangePropagationSpecification;
import mir.reactions.all.AllChangePropagationSpecification;
import mir.reactions.classifierBody.ClassifierBodyChangePropagationSpecification;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended.ExtendedJava2PcmMethodBodyChangePreprocessor;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.finegrained.FineGrainedJava2PcmMethodBodyChangePreprocessor;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor;

/**
 * Change propagation specification in order to propagate changes on JaMoPP models to the PCM models.
 * It includes change propagation rules for several types of changes on package and compilation unit levels as well as changes in classes and interfaces.
 * The change propagation rules are defined in: 
 * cipm.consistency.cpr.javapcm.internal.ClassifierBody.reactions and
 * cipm.consistency.cpr.javapcm.internal.PackageAndClassifiers.reactions
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
 * @author Martin Armbruster
 */
@SuppressWarnings("all")
public class CommitIntegrationJavaPCMChangePropagationSpecification extends AllChangePropagationSpecification {
	@Override
	protected void setup() {
		super.setup();
		// Change propagation specification for changes on method bodies.
		if (CommitIntegrationSettingsContainer.getSettingsContainer()
				.getPropertyAsBoolean(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)) {
			this.addChangeMainprocessor(new FineGrainedJava2PcmMethodBodyChangePreprocessor());
		} else {
			if (CommitIntegrationSettingsContainer.getSettingsContainer()
					.getPropertyAsBoolean(SettingKeys.USE_PCM_IM_CPRS)) {
				this.addChangeMainprocessor(new ExtendedJava2PcmMethodBodyChangePreprocessor());
			} else {
				this.addChangeMainprocessor(new Java2ImChangePropagationSpecification());
			}
		}
	}
}
