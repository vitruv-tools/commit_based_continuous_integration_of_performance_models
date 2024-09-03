package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.modules.AccessProvidingModuleDirective;
import org.emftext.language.java.modules.ModuleReference;
import org.emftext.language.java.modules.ProvidesModuleDirective;
import org.emftext.language.java.modules.RequiresModuleDirective;
import org.emftext.language.java.modules.UsesModuleDirective;
import org.emftext.language.java.modules.util.ModulesSwitch;

/**
 * Similarity Decisions for module elements.
 */
private class ModulesSimilaritySwitch extends ModulesSwitch<Boolean> {
	/**
     * Check ModuleReference similarity.<br>
     * Similarity is checked by
     * <ul>
     * <li>module names</li>
     * </ul>
     * 
     * @param modRef1 The module reference to compare with the compare element.
     * @return True/False if the module references are similar or not.
     */
	@Override
	public Boolean caseModuleReference(ModuleReference modRef1) {
		ModuleReference modRef2 = (ModuleReference) compareElement;
		if (compareNamespacesByPart(modRef1, modRef2)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
     * Check similarity for access providing module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>the provided package</li>
     * </ul>
     * 
     * @param dir1 The access providing module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
     */
	@Override
	public Boolean caseAccessProvidingModuleDirective(AccessProvidingModuleDirective dir1) {
		AccessProvidingModuleDirective dir2 = (AccessProvidingModuleDirective) compareElement;
		if (!compareNamespacesByPart(dir1, dir2)) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	/**
     * Check similarity for require module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>required modules</li>
     * </ul>
     * 
     * @param dir1 The require module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
     */
	@Override
	public Boolean caseRequiresModuleDirective(RequiresModuleDirective dir1) {
		RequiresModuleDirective dir2 = (RequiresModuleDirective) compareElement;
		return similarityChecker.isSimilar(dir1.getRequiredModule(), dir2.getRequiredModule());
	}
	
	/**
     * Check similarity for provide module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>provided types</li>
     * </ul>
     * 
     * @param dir1 The provide module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
     */
	@Override
	public Boolean caseProvidesModuleDirective(ProvidesModuleDirective dir1) {
		ProvidesModuleDirective dir2 = (ProvidesModuleDirective) compareElement;
		return similarityChecker.isSimilar(dir1.getTypeReference(), dir2.getTypeReference());
	}
	
	/**
     * Check similarity for use module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>used types</li>
     * </ul>
     * 
     * @param dir1 The use module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
     */
	@Override
	public Boolean caseUsesModuleDirective(UsesModuleDirective dir1) {
		UsesModuleDirective dir2 = (UsesModuleDirective) compareElement;
		return similarityChecker.isSimilar(dir1.getTypeReference(), dir2.getTypeReference());
	}
}
