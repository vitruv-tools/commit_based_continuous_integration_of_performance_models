package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.util.ParametersSwitch;

import com.google.common.base.Strings;

/**
 * Similarity decisions for parameter elements.
 * <p>
 * Parameters are variables and for this named elements. So their names must be checked but no
 * more identifying attributes or references exist.
 * </p>
 */
private class ParametersSimilaritySwitch extends ParametersSwitch<Boolean> {
    @Override
    public Boolean caseParameter(Parameter param1) {
        Parameter param2 = (Parameter) compareElement;
        String name1 = Strings.nullToEmpty(param1.getName());
        String name2 = Strings.nullToEmpty(param2.getName());
        return (name1.equals(name2));
    }
}
