package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.commons.layout.util.LayoutSwitch;

/**
 * Similarity Decisions for layout information is always true as they are not considered for
 * now.
 */
private class LayoutSimilaritySwitch extends LayoutSwitch<Boolean> {
    @Override
    public Boolean defaultCase(EObject object) {
        return Boolean.TRUE;
    }
}
