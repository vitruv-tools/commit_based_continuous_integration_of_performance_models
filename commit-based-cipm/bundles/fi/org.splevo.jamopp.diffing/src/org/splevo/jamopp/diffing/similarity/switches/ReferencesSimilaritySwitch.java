package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.references.ElementReference;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.Reference;
import org.emftext.language.java.references.ReferenceableElement;
import org.emftext.language.java.references.StringReference;
import org.emftext.language.java.references.util.ReferencesSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.util.JaMoPPElementUtil;

/**
 * Similarity decisions for reference elements.
 */
public class ReferencesSimilaritySwitch extends ReferencesSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;
	private boolean checkStatementPosition;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public boolean shouldCheckStatementPosition() {
		return this.checkStatementPosition;
	}

	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

	public ReferencesSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	@Override
	public Boolean caseStringReference(StringReference ref1) {
		this.logMessage("caseStringReference");

		StringReference ref2 = (StringReference) this.getCompareElement();

		var val1 = ref1.getValue();
		var val2 = ref2.getValue();

		// Null check to avoid NullPointerException
		if (val1 == val2) {
			return Boolean.TRUE;
		} else if (val1 == null ^ val2 == null) {
			return Boolean.FALSE;
		}

		return val1.equals(val2);
	}

	@Override
	public Boolean caseIdentifierReference(IdentifierReference ref1) {
		this.logMessage("caseIdentifierReference");

		IdentifierReference ref2 = (IdentifierReference) this.getCompareElement();
		ReferenceableElement target1 = ref1.getTarget();
		ReferenceableElement target2 = ref2.getTarget();

		// target identity similarity
		Boolean similarity = this.isSimilar(target1, target2);
		if (similarity == Boolean.FALSE) {
			return Boolean.FALSE;
		}

		if (target1 != null) {
			// target container similarity
			// check this only if the reference target is located
			// in another container than the reference itself.
			// Otherwise such a situation would lead to endless loops
			// e.g. for for "(Iterator i = c.iterator(); i.hasNext(); ) {"
			// Attention: The reference could be encapsulated by an expression!
			EObject ref1Container = JaMoPPElementUtil.getFirstContainerNotOfGivenType(ref1, Expression.class,
					ArraySelector.class);
			EObject ref2Container = JaMoPPElementUtil.getFirstContainerNotOfGivenType(ref2, Expression.class,
					ArraySelector.class);
			EObject target1Container = target1.eContainer();
			EObject target2Container = target2.eContainer();
			if (target1Container != ref1Container && target2Container != ref2Container && target1Container != ref1
					&& target2Container != ref2) {
				Boolean containerSimilarity = this.isSimilar(target1Container, target2Container);
				if (containerSimilarity == Boolean.FALSE) {
					return Boolean.FALSE;
				}
			}
		}

		var arrSels1 = ref1.getArraySelectors();
		var arrSels2 = ref2.getArraySelectors();

		// Null check to avoid NullPointerExceptions
		if (arrSels1 == null ^ arrSels2 == null) {
			return Boolean.FALSE;
		} else if (arrSels1 != null && arrSels2 != null) {
			if (arrSels1.size() != arrSels2.size()) {
				return Boolean.FALSE;
			}
			for (int i = 0; i < arrSels1.size(); i++) {
				ArraySelector selector1 = arrSels1.get(i);
				ArraySelector selector2 = arrSels2.get(i);
				Boolean positionSimilarity = this.isSimilar(selector1.getPosition(), selector2.getPosition());
				if (positionSimilarity == Boolean.FALSE) {
					return Boolean.FALSE;
				}
			}
		}

		Reference next1 = ref1.getNext();
		Reference next2 = ref2.getNext();
		Boolean nextSimilarity = this.isSimilar(next1, next2);
		if (nextSimilarity == Boolean.FALSE) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	/**
	 * Check element reference similarity.<br>
	 * 
	 * Is checked by the target (the method called). Everything else are containment
	 * references checked indirectly.
	 * 
	 * @param ref1 The method call to compare with the compare element.
	 * @return True As null always means null.
	 */
	@Override
	public Boolean caseElementReference(ElementReference ref1) {
		this.logMessage("caseElementReference");

		ElementReference ref2 = (ElementReference) this.getCompareElement();

		Boolean targetSimilarity = this.isSimilar(ref1.getTarget(), ref2.getTarget());
		if (targetSimilarity == Boolean.FALSE) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	/**
	 * Proof method call similarity.
	 * 
	 * Similarity is decided by the method referenced and the arguments passed by.
	 * 
	 * @param call1 The left / modified method call to compare with the original
	 *              one.
	 * @return True/False if the method calls are similar or not.
	 */
	@Override
	public Boolean caseMethodCall(MethodCall call1) {
		this.logMessage("caseMethodCall");

		MethodCall call2 = (MethodCall) this.getCompareElement();

		Boolean targetSimilarity = this.isSimilar(call1.getTarget(), call2.getTarget());
		if (targetSimilarity == Boolean.FALSE) {
			return Boolean.FALSE;
		}

		var args1 = call1.getArguments();
		var args2 = call2.getArguments();

		// Null check to avoid NullPointerExceptions
		if (args1 == null ^ args2 == null) {
			return Boolean.FALSE;
		} else if (args1 != null && args2 != null) {
			if (args1.size() != args2.size()) {
				return Boolean.FALSE;
			}

			for (int i = 0; i < args1.size(); i++) {
				Expression exp1 = args1.get(i);
				Expression exp2 = args2.get(i);
				Boolean argSimilarity = this.isSimilar(exp1, exp2);
				if (argSimilarity == Boolean.FALSE) {
					return Boolean.FALSE;
				}
			}
		}

		Boolean nextSimilarity = this.isSimilar(call1.getNext(), call2.getNext());
		if (nextSimilarity == Boolean.FALSE) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@Override
	public Boolean defaultCase(EObject object) {
		this.logMessage("defaultCase for Reference");

		return Boolean.TRUE;
	}
}