package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.instantiations.ExplicitConstructorCall;
import org.emftext.language.java.instantiations.NewConstructorCall;
import org.emftext.language.java.instantiations.util.InstantiationsSwitch;
import org.emftext.language.java.types.Type;

/**
 * Similarity decisions for object instantiation elements.
 */
private class InstantiationsSimilaritySwitch extends InstantiationsSwitch<Boolean> {

    /**
     * Check class instance creation similarity.<br>
     * Similarity is checked by
     * <ul>
     * <li>instance type similarity</li>
     * <li>number of constructor arguments</li>
     * <li>types of constructor arguments</li>
     * </ul>
     * 
     * @param call1
     *            The class instance creation to compare with the compare element.
     * @return True/False if the class instance creations are similar or not.
     */
    @Override
    public Boolean caseExplicitConstructorCall(ExplicitConstructorCall call1) {

        ExplicitConstructorCall call2 = (ExplicitConstructorCall) compareElement;

        // check the class instance types
        Boolean typeSimilarity = similarityChecker.isSimilar(call1.getCallTarget(), call2.getCallTarget());
        if (typeSimilarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }

        // check number of type arguments
        EList<Expression> cic1Args = call1.getArguments();
        EList<Expression> cic2Args = call2.getArguments();
        if (cic1Args.size() != cic2Args.size()) {
            return Boolean.FALSE;
        }

        // check the argument similarity
        for (int i = 0; i < cic1Args.size(); i++) {
            Boolean argumentSimilarity = similarityChecker.isSimilar(cic1Args.get(i), cic2Args.get(i));
            if (argumentSimilarity == Boolean.FALSE) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean caseNewConstructorCall(NewConstructorCall call1) {
        NewConstructorCall call2 = (NewConstructorCall) compareElement;

        Type type1 = call1.getTypeReference().getTarget();
        Type type2 = call2.getTypeReference().getTarget();
        Boolean typeSimilarity = similarityChecker.isSimilar(type1, type2);
        if (typeSimilarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }

        EList<Expression> types1 = call1.getArguments();
        EList<Expression> types2 = call2.getArguments();
        if (types1.size() != types2.size()) {
            return Boolean.FALSE;
        }
        for (int i = 0; i < types1.size(); i++) {
            Expression argType1 = types1.get(i);
            Expression argType2 = types2.get(i);
            Boolean similarity = similarityChecker.isSimilar(argType1, argType2);
            if (similarity == Boolean.FALSE) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean defaultCase(EObject object) {
        return Boolean.TRUE;
    }
}
