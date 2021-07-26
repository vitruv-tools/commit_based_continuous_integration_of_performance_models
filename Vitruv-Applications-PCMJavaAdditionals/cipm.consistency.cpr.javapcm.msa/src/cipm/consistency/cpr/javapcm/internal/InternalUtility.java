package cipm.consistency.cpr.javapcm.internal;

import org.emftext.language.java.classifiers.ConcreteClassifier;

/**
 * An internal utility class.
 * 
 * @author Martin Armbruster
 */
public final class InternalUtility {
	private InternalUtility() {
	}
	
	/**
	 * Returns the module in which a classifier is contained.
	 * 
	 * @param classifier the classifier for which the module is looked up.
	 * @return the module in which the classifier is contained
	 *         or null if the classifier is not contained within a module.
	 */
	public static org.emftext.language.java.containers.Module getModule(ConcreteClassifier classifier) {
		if (classifier.getPackage() != null) {
			if (classifier.getPackage().getModule() != null) {
				return classifier.getPackage().getModule();
			}
		}
		return null;
	}
}
