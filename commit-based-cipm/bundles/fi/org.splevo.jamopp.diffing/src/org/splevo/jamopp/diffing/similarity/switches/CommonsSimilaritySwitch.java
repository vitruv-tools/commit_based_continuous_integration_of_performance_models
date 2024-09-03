package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.commons.NamedElement;
import org.emftext.language.java.commons.util.CommonsSwitch;

/**
 * Similarity decisions for commons elements.
 */
private class CommonsSimilaritySwitch extends CommonsSwitch<Boolean> {

    /**
     * Check named element
     * 
     * Similarity is defined by the names of the elements.
     * 
     * @param element1
     *            The method call to compare with the compare element.
     * @return True As null always means null.
     */
    @Override
    public Boolean caseNamedElement(NamedElement element1) {
        NamedElement element2 = (NamedElement) compareElement;

        if (element1.getName() == null) {
            return (element2.getName() == null);
        }

        return (element1.getName().equals(element2.getName()));
    }
}
