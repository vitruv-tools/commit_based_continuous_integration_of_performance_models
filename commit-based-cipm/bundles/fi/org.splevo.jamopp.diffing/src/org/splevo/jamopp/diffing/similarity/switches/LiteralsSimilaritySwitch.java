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
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * Similarity decisions for literal elements.
 */
public class LiteralsSimilaritySwitch extends LiteralsSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

	public LiteralsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch) {
		this.similaritySwitch = similaritySwitch;
	}

	@Override
	public Boolean caseBooleanLiteral(BooleanLiteral boolean1) {
		this.logMessage("caseBooleanLiteral");

		BooleanLiteral boolean2 = (BooleanLiteral) this.getCompareElement();
		return (boolean1.isValue() == boolean2.isValue());
	}

	@Override
	public Boolean caseCharacterLiteral(CharacterLiteral char1) {
		this.logMessage("caseCharacterLiteral");

		CharacterLiteral char2 = (CharacterLiteral) this.getCompareElement();

		var val1 = char1.getValue();
		var val2 = char2.getValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseDecimalFloatLiteral(DecimalFloatLiteral float1) {
		this.logMessage("caseDecimalFloatLiteral");

		DecimalFloatLiteral float2 = (DecimalFloatLiteral) this.getCompareElement();
		return compareDouble(float1.getDecimalValue(), float2.getDecimalValue());
	}

	@Override
	public Boolean caseHexFloatLiteral(HexFloatLiteral float1) {
		this.logMessage("caseHexFloatLiteral");

		HexFloatLiteral float2 = (HexFloatLiteral) this.getCompareElement();
		return compareDouble(float1.getHexValue(), float2.getHexValue());
	}

	@Override
	public Boolean caseDecimalDoubleLiteral(DecimalDoubleLiteral double1) {
		this.logMessage("caseDecimalDoubleLiteral");

		DecimalDoubleLiteral double2 = (DecimalDoubleLiteral) this.getCompareElement();
		return compareDouble(double1.getDecimalValue(), double2.getDecimalValue());
	}

	@Override
	public Boolean caseHexDoubleLiteral(HexDoubleLiteral double1) {
		this.logMessage("caseHexDoubleLiteral");

		HexDoubleLiteral double2 = (HexDoubleLiteral) this.getCompareElement();
		return compareDouble(double1.getHexValue(), double2.getHexValue());
	}

	private boolean compareDouble(double d1, double d2) {
		return d1 == d2 || Double.isNaN(d1) && Double.isNaN(d2);
	}

	@Override
	public Boolean caseDecimalIntegerLiteral(DecimalIntegerLiteral int1) {
		this.logMessage("caseDecimalIntegerLiteral");

		DecimalIntegerLiteral int2 = (DecimalIntegerLiteral) this.getCompareElement();

		var val1 = int1.getDecimalValue();
		var val2 = int2.getDecimalValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseHexIntegerLiteral(HexIntegerLiteral int1) {
		this.logMessage("caseHexIntegerLiteral");

		HexIntegerLiteral int2 = (HexIntegerLiteral) this.getCompareElement();

		var val1 = int1.getHexValue();
		var val2 = int2.getHexValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseOctalIntegerLiteral(OctalIntegerLiteral int1) {
		this.logMessage("caseOctalIntegerLiteral");

		OctalIntegerLiteral int2 = (OctalIntegerLiteral) this.getCompareElement();

		var val1 = int1.getOctalValue();
		var val2 = int2.getOctalValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseDecimalLongLiteral(DecimalLongLiteral long1) {
		this.logMessage("caseDecimalLongLiteral");

		DecimalLongLiteral long2 = (DecimalLongLiteral) this.getCompareElement();

		var val1 = long1.getDecimalValue();
		var val2 = long2.getDecimalValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseHexLongLiteral(HexLongLiteral long1) {
		this.logMessage("caseHexLongLiteral");

		HexLongLiteral long2 = (HexLongLiteral) this.getCompareElement();

		var val1 = long1.getHexValue();
		var val2 = long2.getHexValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseOctalLongLiteral(OctalLongLiteral long1) {
		this.logMessage("caseOctalLongLiteral");

		OctalLongLiteral long2 = (OctalLongLiteral) this.getCompareElement();

		var val1 = long1.getOctalValue();
		var val2 = long2.getOctalValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseBinaryLongLiteral(BinaryLongLiteral long1) {
		this.logMessage("caseBinaryLongLiteral");

		BinaryLongLiteral long2 = (BinaryLongLiteral) this.getCompareElement();

		var val1 = long1.getBinaryValue();
		var val2 = long2.getBinaryValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseBinaryIntegerLiteral(BinaryIntegerLiteral int1) {
		this.logMessage("caseBinaryIntegerLiteral");

		BinaryIntegerLiteral int2 = (BinaryIntegerLiteral) this.getCompareElement();

		var val1 = int1.getBinaryValue();
		var val2 = int2.getBinaryValue();

		// Null check to avoid NullPointerExceptions
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	/**
	 * Check null literal similarity.<br>
	 * 
	 * Null literals are always assumed to be similar.
	 * 
	 * @param object The literal to compare with the compare element.
	 * @return True As null always means null.
	 */
	@Override
	public Boolean defaultCase(EObject object) {
		this.logMessage("defaultCase for Literals");

		return Boolean.TRUE;
	}
}