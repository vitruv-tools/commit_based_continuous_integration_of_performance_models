package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.modifiers.util.ModifiersSwitch;

/**
 * Similarity decisions for modifier elements.
 * <p>
 * All modifier elements are strong typed with no identifying attributes or non-containment
 * references. Their location and runtime types are assumed to be checked before this switch is
 * called.
 * </p>
 */
private class ModifiersSimilaritySwitch extends ModifiersSwitch<Boolean> {
    @Override
    public Boolean defaultCase(EObject object) {
        return Boolean.TRUE;
    }
}
