package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.commons.NamedElement;
import org.emftext.language.java.commons.util.CommonsSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * Similarity decisions for commons elements.
 */
public class CommonsSimilaritySwitch extends CommonsSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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

    public CommonsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

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
    	this.logMessage("caseNamedElement");
    	
        NamedElement element2 = (NamedElement) this.getCompareElement();

        if (element1.getName() == null) {
            return (element2.getName() == null);
        }

        return (element1.getName().equals(element2.getName()));
    }
}