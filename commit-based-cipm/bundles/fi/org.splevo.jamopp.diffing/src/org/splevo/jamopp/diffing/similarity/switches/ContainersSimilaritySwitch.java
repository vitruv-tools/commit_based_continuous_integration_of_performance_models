package org.splevo.jamopp.diffing.similarity.switches;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.containers.util.ContainersSwitch;
import org.splevo.diffing.util.NormalizationUtil;
import org.splevo.jamopp.diffing.util.JaMoPPModelUtil;

import com.google.common.base.Strings;

/**
 * Similarity decisions for container elements.
 */
private class ContainersSimilaritySwitch extends ContainersSwitch<Boolean> {

    private LinkedHashMap<Pattern, String> compilationUnitNormalizations = null;

    private LinkedHashMap<Pattern, String> packageNormalizations = null;

    /**
     * Constructor to set the required configurations.
     * 
     * @param compilationUnitNormalizations
     *            A list of patterns replace any match in a classifier name with the defined
     *            replacement string.
     * @param packageNormalizations
     *            A list of package normalization patterns.
     */
    public ContainersSimilaritySwitch(LinkedHashMap<Pattern, String> compilationUnitNormalizations,
            LinkedHashMap<Pattern, String> packageNormalizations) {
        this.compilationUnitNormalizations = compilationUnitNormalizations;
        this.packageNormalizations = packageNormalizations;
    }

    /**
     * Check the similarity of two CompilationUnits.<br>
     * Similarity is checked by
     * <ul>
     * <li>Comparing their names (including renamings)</li>
     * <li>Comparing their namespaces' values (including renamings)</li>
     * </ul>
     * Note: CompilationUnit names are full qualified. So it is important to apply classifier as
     * well as package renaming normalizations to them.
     * 
     * @param unit1
     *            The compilation unit to compare with the compareElement.
     * @return True/False whether they are similar or not.
     */
    @Override
    public Boolean caseCompilationUnit(CompilationUnit unit1) {

        CompilationUnit unit2 = (CompilationUnit) compareElement;

        String name1 = NormalizationUtil.normalize(unit1.getName(), compilationUnitNormalizations);
        name1 = NormalizationUtil.normalize(name1, packageNormalizations);
        String name2 = unit2.getName();
        if (!name1.equals(name2)) {
            return Boolean.FALSE;
        }

        String namespaceString1 = NormalizationUtil.normalizeNamespace(unit1.getNamespacesAsString(),
                packageNormalizations);
        String namespaceString2 = Strings.nullToEmpty(unit2.getNamespacesAsString());
        if (!namespaceString1.equals(namespaceString2)) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * Check package similarity.<br>
     * Similarity is checked by
     * <ul>
     * <li>full qualified package path</li>
     * </ul>
     * 
     * @param package1
     *            The package to compare with the compare element.
     * @return True/False if the packages are similar or not.
     */
    @Override
    public Boolean casePackage(Package package1) {
        Package package2 = (Package) compareElement;

        String packagePath1 = JaMoPPModelUtil.buildNamespacePath(package1);
        packagePath1 = NormalizationUtil.normalizeNamespace(packagePath1, packageNormalizations);
        String packagePath2 = JaMoPPModelUtil.buildNamespacePath(package2);
        if (!packagePath1.equals(packagePath2)) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
    
    /**
     * Check module similarity.<br>
     * Similarity is checked by
     * <ul>
     * <li>module names</li>
     * </ul>
     * 
     * @param module1 The module to compare with the compare element.
     * @return True/False if the modules are similar or not.
     */
    @Override
    public Boolean caseModule(org.emftext.language.java.containers.Module module1) {
    	org.emftext.language.java.containers.Module module2 =
    			(org.emftext.language.java.containers.Module) compareElement;
    	if (!module1.getName().equals(module2.getName())) {
    		return Boolean.FALSE;
    	}
    	return Boolean.TRUE;
    }
}
