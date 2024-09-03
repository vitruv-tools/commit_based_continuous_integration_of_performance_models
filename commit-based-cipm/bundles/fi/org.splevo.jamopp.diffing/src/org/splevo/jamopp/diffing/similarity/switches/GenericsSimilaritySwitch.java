package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.generics.ExtendsTypeArgument;
import org.emftext.language.java.generics.QualifiedTypeArgument;
import org.emftext.language.java.generics.SuperTypeArgument;
import org.emftext.language.java.generics.TypeParameter;
import org.emftext.language.java.generics.UnknownTypeArgument;
import org.emftext.language.java.generics.util.GenericsSwitch;

/**
 * Similarity decisions for the generic elements.
 */
private class GenericsSimilaritySwitch extends GenericsSwitch<Boolean> {
	@Override
	public Boolean caseQualifiedTypeArgument(QualifiedTypeArgument qta1) {
		QualifiedTypeArgument qta2 = (QualifiedTypeArgument) compareElement;
		return similarityChecker.isSimilar(qta1.getTypeReference(), qta2.getTypeReference());
	}
	
	@Override
	public Boolean caseSuperTypeArgument(SuperTypeArgument sta1) {
		SuperTypeArgument sta2 = (SuperTypeArgument) compareElement;
		return similarityChecker.isSimilar(sta1.getSuperType(), sta2.getSuperType());
	}
	
	@Override
	public Boolean caseExtendsTypeArgument(ExtendsTypeArgument eta1) {
		ExtendsTypeArgument eta2 = (ExtendsTypeArgument) compareElement;
		return similarityChecker.isSimilar(eta1.getExtendType(), eta2.getExtendType());
	}
	
	@Override
	public Boolean caseUnknownTypeArgument(UnknownTypeArgument arg) {
		return Boolean.TRUE;
	}
	
    @Override
    public Boolean caseTypeParameter(TypeParameter param1) {
    	TypeParameter param2 = (TypeParameter) compareElement;
    	
    	if (!param1.getName().equals(param2.getName())) {
    		return Boolean.FALSE;
    	}
    	
    	return similarityChecker.areSimilar(param1.getExtendTypes(), param2.getExtendTypes());
    }
}

