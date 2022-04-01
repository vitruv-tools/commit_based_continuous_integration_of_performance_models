package cipm.consistency.cpr.javapcm.teammates.internal;

import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended.CommitIntegrationCodeToSeffFactory;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff.FunctionClassificationStrategyForPackageMapping;
import tools.vitruv.framework.correspondence.CorrespondenceModel;

/**
 * An internal utility class.
 * 
 * @author Martin Armbruster
 */
public final class InternalUtils {
	private InternalUtils() {
	}
	
	/**
	 * Checks if a call to a method is an external call.
	 * 
	 * @param method the method to check.
	 * @param cm the correspondence model.
	 * @param com basic component in which the call occurs.
	 * @return true if a call to the method is an external call.
	 */
	public static boolean isExternalCall(Method method, CorrespondenceModel cm, BasicComponent com) {
		var factory = new CommitIntegrationCodeToSeffFactory();
		class LocalStrategy extends FunctionClassificationStrategyForPackageMapping {
			public LocalStrategy() {
				super(factory.createBasicComponentFinding(), cm, com);
			}

			@Override
			public boolean isExternalCall(Method call) {
				return super.isExternalCall(call);
			}
		}
		var strategy = new LocalStrategy();
		return strategy.isExternalCall(method);
	}
}
