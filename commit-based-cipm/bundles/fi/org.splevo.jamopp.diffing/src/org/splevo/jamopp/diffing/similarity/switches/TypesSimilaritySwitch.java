package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.InferableType;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.util.TypesSwitch;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

import com.google.common.base.Strings;

/**
 * Similarity decisions for elements of the types package.
 */
private class TypesSimilaritySwitch extends TypesSwitch<Boolean> {

    /**
     * Check element reference similarity.<br>
     * 
     * Is checked by the target (the method called). Everything else are containment references
     * checked indirectly.
     * 
     * @param ref1
     *            The method call to compare with the compare element.
     * @return True As null always means null.
     */
    @Override
    public Boolean caseClassifierReference(ClassifierReference ref1) {
        ClassifierReference ref2 = (ClassifierReference) compareElement;

        Boolean targetSimilarity = similarityChecker.isSimilar(ref1.getTarget(), ref2.getTarget());
        if (targetSimilarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean caseTypeReference(TypeReference ref1) {

        TypeReference ref2 = (TypeReference) compareElement;

        Boolean targetSimilarity = similarityChecker.isSimilar(ref1.getTarget(), ref2.getTarget());
        if (targetSimilarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean caseNamespaceClassifierReference(NamespaceClassifierReference ref1) {

        NamespaceClassifierReference ref2 = (NamespaceClassifierReference) compareElement;

        String namespace1 = Strings.nullToEmpty(ref1.getNamespacesAsString());
        String namespace2 = Strings.nullToEmpty(ref2.getNamespacesAsString());
        if (!namespace1.equals(namespace2)) {
            return Boolean.FALSE;
        }

        ClassifierReference pureRef1 = ref1.getPureClassifierReference();
        ClassifierReference pureRef2 = ref2.getPureClassifierReference();

        return similarityChecker.isSimilar(pureRef1, pureRef2);
    }

    /**
     * Primitive types are always similar as their class similarity is assumed before by the
     * outer {@link SimilarityChecker}.
     * 
     * Note: The fall back to the default case is not sufficient here, as the common
     * TypeReference case would be used before, leading to a loop.
     * 
     * @param type
     *            The primitive type object.
     * @return TRUE
     */
    @Override
    public Boolean casePrimitiveType(PrimitiveType type) {
        return Boolean.TRUE;
    }
    
    /**
     * Inferable types are considered to be similar.
     * 
     * @param type The element to compare with the compare element.
     * @return true.
     */
    @Override
    public Boolean caseInferableType(InferableType type) {
    	return Boolean.TRUE;
    }

    /**
     * Primitive type elements are strongly typed and the exact type is already checked by the
     * outer {@link SimilarityChecker}. <br>
     * {@inheritDoc}
     */
    @Override
    public Boolean defaultCase(EObject object) {
        return Boolean.TRUE;
    }
}
