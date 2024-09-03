package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.common.util.EList;
import org.emftext.language.java.classifiers.AnonymousClass;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Constructor;
import org.emftext.language.java.members.EnumConstant;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.members.util.MembersSwitch;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.types.Type;

import com.google.common.base.Strings;

/**
 * Similarity decisions for the member elements.
 */
private class MembersSimilaritySwitch extends MembersSwitch<Boolean> {

    /**
     * Check abstract method declaration similarity. Similarity is checked by
     * <ul>
     * <li>name</li>
     * <li>parameter list size</li>
     * <li>parameter types</li>
     * <li>name</li>
     * <li>container for
     * <ul>
     * <li>AbstractTypeDeclaration</li>
     * <li>AnonymousClassDeclaration</li>
     * <li>Model</li>
     * </ul>
     * </li>
     * </ul>
     * 
     * The container must be checked to check similarity for referenced methods.
     * 
     * 
     * @param method1
     *            The abstract method declaration to compare with the compare element.
     * @return True/False if the abstract method declarations are similar or not.
     */
    @Override
    public Boolean caseMethod(Method method1) {

        Method method2 = (Method) compareElement;

        // if methods have different names they are not similar.
        if (!method1.getName().equals(method2.getName())) {
            return Boolean.FALSE;
        }

        if (method1.getParameters().size() != method2.getParameters().size()) {
            return Boolean.FALSE;
        }

        for (int i = 0; i < method1.getParameters().size(); i++) {
            Parameter param1 = method1.getParameters().get(i);
            Parameter param2 = method2.getParameters().get(i);
            Type type1 = param1.getTypeReference().getTarget();
            Type type2 = param2.getTypeReference().getTarget();
            Boolean typeSimilarity = similarityChecker.isSimilar(type1, type2);
            if (typeSimilarity == Boolean.FALSE) {
                return Boolean.FALSE;
            }
            if (param1.getTypeReference().getArrayDimension() != param2.getTypeReference().getArrayDimension()) {
            	return Boolean.FALSE;
            }
        }

        /* **************************************
         * methods as members of regular classes
         */
        if (method1.getContainingConcreteClassifier() != null) {
            ConcreteClassifier type1 = method1.getContainingConcreteClassifier();
            ConcreteClassifier type2 = method2.getContainingConcreteClassifier();
            return similarityChecker.isSimilar(type1, type2);
        }

        /* **************************************
         * methods as members of anonymous classes
         */
        if (method1.getContainingAnonymousClass() != null) {
            AnonymousClass type1 = method1.getContainingAnonymousClass();
            AnonymousClass type2 = method2.getContainingAnonymousClass();
            Boolean typeSimilarity = similarityChecker.isSimilar(type1, type2);
            if (typeSimilarity != null) {
                return typeSimilarity;
            }
        }

        logger.warn("MethodDeclaration in unknown container: " + method1.getName() + " : "
                + method1.eContainer());
        return super.caseMethod(method1);
    }

    /**
     * Check constuctor declaration similarity. Similarity is checked by
     * <ul>
     * <li>name</li>
     * <li>parameter list size</li>
     * <li>parameter types</li>
     * <li>name</li>
     * <li>container for
     * <ul>
     * <li>AbstractTypeDeclaration</li>
     * <li>AnonymousClassDeclaration</li>
     * <li>Model</li>
     * </ul>
     * </li>
     * </ul>
     * 
     * The container must be checked to check similarity for referenced methods.
     * 
     * 
     * @param constructor1
     *            The abstract method declaration to compare with the compare element.
     * @return True/False if the abstract method declarations are similar or not.
     */
    @Override
    public Boolean caseConstructor(Constructor constructor1) {

        Constructor constructor2 = (Constructor) compareElement;

        // if methods have different names they are not similar.
        if (!constructor1.getName().equals(constructor2.getName())) {
            return Boolean.FALSE;
        }

        EList<Parameter> params1 = constructor1.getParameters();
        EList<Parameter> params2 = constructor2.getParameters();
        Boolean parameterSimilarity = similarityChecker.areSimilar(params1, params2);
        if (parameterSimilarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }

        /* **************************************
         * methods as members of regular classes
         */
        if (constructor1.getContainingConcreteClassifier() != null) {
            ConcreteClassifier type1 = constructor1.getContainingConcreteClassifier();
            ConcreteClassifier type2 = constructor2.getContainingConcreteClassifier();
            return similarityChecker.isSimilar(type1, type2);
        }

        /* **************************************
         * methods as members of anonymous classes
         */
        if (constructor1.getContainingAnonymousClass() != null) {
            AnonymousClass type1 = constructor1.getContainingAnonymousClass();
            AnonymousClass type2 = constructor2.getContainingAnonymousClass();
            Boolean typeSimilarity = similarityChecker.isSimilar(type1, type2);
            if (typeSimilarity != null) {
                return typeSimilarity;
            }
        }

        logger.warn("ConstructorDeclaration in unknown container: " + constructor1.getName() + " : "
                + constructor1.eContainer().getClass().getSimpleName());
        return super.caseConstructor(constructor1);
    }

    @Override
    public Boolean caseEnumConstant(EnumConstant const1) {
        EnumConstant const2 = (EnumConstant) compareElement;
        String name1 = Strings.nullToEmpty(const1.getName());
        String name2 = Strings.nullToEmpty(const2.getName());
        return (name1.equals(name2));
    }

    @Override
    public Boolean caseMember(Member member1) {
        Member member2 = (Member) compareElement;
        String name1 = Strings.nullToEmpty(member1.getName());
        String name2 = Strings.nullToEmpty(member2.getName());
        return (name1.equals(name2));
    }
}
