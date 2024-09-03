package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.literals.BinaryIntegerLiteral;
import org.emftext.language.java.literals.BinaryLongLiteral;
import org.emftext.language.java.literals.BooleanLiteral;
import org.emftext.language.java.literals.CharacterLiteral;
import org.emftext.language.java.literals.DecimalDoubleLiteral;
import org.emftext.language.java.literals.DecimalFloatLiteral;
import org.emftext.language.java.literals.DecimalIntegerLiteral;
import org.emftext.language.java.literals.DecimalLongLiteral;
import org.emftext.language.java.literals.HexDoubleLiteral;
import org.emftext.language.java.literals.HexFloatLiteral;
import org.emftext.language.java.literals.HexIntegerLiteral;
import org.emftext.language.java.literals.HexLongLiteral;
import org.emftext.language.java.literals.OctalIntegerLiteral;
import org.emftext.language.java.literals.OctalLongLiteral;
import org.emftext.language.java.literals.util.LiteralsSwitch;

/**
 * Similarity decisions for literal elements.
 */
private class LiteralsSimilaritySwitch extends LiteralsSwitch<Boolean> {

    @Override
    public Boolean caseBooleanLiteral(BooleanLiteral boolean1) {
        BooleanLiteral boolean2 = (BooleanLiteral) compareElement;
        return (boolean1.isValue() == boolean2.isValue());
    }

    @Override
    public Boolean caseCharacterLiteral(CharacterLiteral char1) {
        CharacterLiteral char2 = (CharacterLiteral) compareElement;
        return char1.getValue().equals(char2.getValue());
    }

    @Override
    public Boolean caseDecimalFloatLiteral(DecimalFloatLiteral float1) {
        DecimalFloatLiteral float2 = (DecimalFloatLiteral) compareElement;
        return compareDouble(float1.getDecimalValue(), float2.getDecimalValue());
    }

    @Override
    public Boolean caseHexFloatLiteral(HexFloatLiteral float1) {
        HexFloatLiteral float2 = (HexFloatLiteral) compareElement;
        return compareDouble(float1.getHexValue(), float2.getHexValue());
    }

    @Override
    public Boolean caseDecimalDoubleLiteral(DecimalDoubleLiteral double1) {
        DecimalDoubleLiteral double2 = (DecimalDoubleLiteral) compareElement;
        return compareDouble(double1.getDecimalValue(), double2.getDecimalValue());
    }

    @Override
    public Boolean caseHexDoubleLiteral(HexDoubleLiteral double1) {
        HexDoubleLiteral double2 = (HexDoubleLiteral) compareElement;
        return compareDouble(double1.getHexValue(), double2.getHexValue());
    }
    
    private boolean compareDouble(double d1, double d2) {
    	return d1 == d2 || Double.isNaN(d1) && Double.isNaN(d2);
    }

    @Override
    public Boolean caseDecimalIntegerLiteral(DecimalIntegerLiteral int1) {
        DecimalIntegerLiteral int2 = (DecimalIntegerLiteral) compareElement;
        return (int1.getDecimalValue().equals(int2.getDecimalValue()));
    }

    @Override
    public Boolean caseHexIntegerLiteral(HexIntegerLiteral int1) {
        HexIntegerLiteral int2 = (HexIntegerLiteral) compareElement;
        return (int1.getHexValue().equals(int2.getHexValue()));
    }

    @Override
    public Boolean caseOctalIntegerLiteral(OctalIntegerLiteral int1) {
        OctalIntegerLiteral int2 = (OctalIntegerLiteral) compareElement;
        return (int1.getOctalValue().equals(int2.getOctalValue()));
    }

    @Override
    public Boolean caseDecimalLongLiteral(DecimalLongLiteral long1) {
        DecimalLongLiteral long2 = (DecimalLongLiteral) compareElement;
        return (long1.getDecimalValue().equals(long2.getDecimalValue()));
    }

    @Override
    public Boolean caseHexLongLiteral(HexLongLiteral long1) {
        HexLongLiteral long2 = (HexLongLiteral) compareElement;
        return (long1.getHexValue().equals(long2.getHexValue()));
    }

    @Override
    public Boolean caseOctalLongLiteral(OctalLongLiteral long1) {
        OctalLongLiteral long2 = (OctalLongLiteral) compareElement;
        return (long1.getOctalValue().equals(long2.getOctalValue()));
    }
    
    @Override
    public Boolean caseBinaryLongLiteral(BinaryLongLiteral long1) {
    	BinaryLongLiteral long2 = (BinaryLongLiteral) compareElement;
    	return long1.getBinaryValue().equals(long2.getBinaryValue());
    }
    
    @Override
    public Boolean caseBinaryIntegerLiteral(BinaryIntegerLiteral int1) {
    	BinaryIntegerLiteral int2 = (BinaryIntegerLiteral) compareElement;
    	return int1.getBinaryValue().equals(int2.getBinaryValue());
    }

    /**
     * Check null literal similarity.<br>
     * 
     * Null literals are always assumed to be similar.
     * 
     * @param object
     *            The literal to compare with the compare element.
     * @return True As null always means null.
     */
    @Override
    public Boolean defaultCase(EObject object) {
        return Boolean.TRUE;
    }
}
