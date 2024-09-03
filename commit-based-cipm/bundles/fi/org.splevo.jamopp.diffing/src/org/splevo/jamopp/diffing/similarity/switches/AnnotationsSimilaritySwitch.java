package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.annotations.AnnotationAttributeSetting;
import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.annotations.util.AnnotationsSwitch;
import org.emftext.language.java.classifiers.Classifier;

/**
 * Similarity decisions for annotation elements.
 */
private class AnnotationsSimilaritySwitch extends AnnotationsSwitch<Boolean> {

    @Override
    public Boolean caseAnnotationInstance(AnnotationInstance instance1) {
        AnnotationInstance instance2 = (AnnotationInstance) compareElement;

        Classifier class1 = instance1.getAnnotation();
        Classifier class2 = instance2.getAnnotation();
        Boolean classifierSimilarity = similarityChecker.isSimilar(class1, class2);
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
        AnnotationAttributeSetting setting2 = (AnnotationAttributeSetting) compareElement;
        Boolean similarity = similarityChecker.isSimilar(setting1.getAttribute(), setting2.getAttribute());
        if (similarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean defaultCase(EObject object) {
        return Boolean.TRUE;
    }
}
