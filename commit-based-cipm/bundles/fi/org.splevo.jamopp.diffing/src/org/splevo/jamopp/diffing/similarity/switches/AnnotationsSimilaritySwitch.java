package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.annotations.AnnotationAttributeSetting;
import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.annotations.util.AnnotationsSwitch;
import org.emftext.language.java.classifiers.Classifier;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * Similarity decisions for annotation elements.
 */
public class AnnotationsSimilaritySwitch extends AnnotationsSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;
	private boolean checkStatementPosition;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public boolean shouldCheckStatementPosition() {
		return this.checkStatementPosition;
	}

	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

	public AnnotationsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	@Override
	public Boolean caseAnnotationInstance(AnnotationInstance instance1) {
		this.logMessage("caseAnnotationInstance");

		AnnotationInstance instance2 = (AnnotationInstance) this.getCompareElement();

		Classifier class1 = instance1.getAnnotation();
		Classifier class2 = instance2.getAnnotation();

		Boolean classifierSimilarity = this.isSimilar(class1, class2);
		if (classifierSimilarity == Boolean.FALSE) {
			return Boolean.FALSE;
		}

		String namespace1 = instance1.getNamespacesAsString();
		String namespace2 = instance2.getNamespacesAsString();

		if (namespace1 == null) {
			return (namespace2 == null);
		} else {
			return (namespace1.equals(namespace2));
		}
	}

	@Override
	public Boolean caseAnnotationAttributeSetting(AnnotationAttributeSetting setting1) {
		this.logMessage("caseAnnotationAttributeSetting");

		AnnotationAttributeSetting setting2 = (AnnotationAttributeSetting) this.getCompareElement();

		Boolean similarity = this.isSimilar(setting1.getAttribute(), setting2.getAttribute());
		if (similarity == Boolean.FALSE) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@Override
	public Boolean defaultCase(EObject object) {
		this.logMessage("defaultCase for Annotation");

		this.logMessage("Default annotation comparing case for " + AnnotationsSimilaritySwitch.class.getSimpleName()
				+ ", similarity: true");
		return Boolean.TRUE;
	}
}