package cipm.consistency.commitintegration.diff.util;

import java.util.regex.Pattern;

import org.eclipse.emf.compare.match.eobject.EqualityHelperExtensionProvider;
import org.eclipse.emf.compare.utils.IEqualityHelper;
import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

public class SimilarityCheckerBasedEqualityHelperExtensionProviderDescriptor implements EqualityHelperExtensionProvider.Descriptor {
	private SimilarityChecker checker;

	public SimilarityCheckerBasedEqualityHelperExtensionProviderDescriptor(
			SimilarityChecker check) {
		checker = check;
	}
	
	@Override
	public EqualityHelperExtensionProvider getEqualityHelperExtensionProvider() {
		return new EqualityHelperExtensionProvider() {
			@Override
			public SpecificMatch matchingEObjects(EObject object1, EObject object2, IEqualityHelper equalityHelper) {
				Boolean r = checker.isSimilar(object1, object2);
				return r == null ? SpecificMatch.UNKNOWN : (r == true ? SpecificMatch.MATCH : SpecificMatch.UNMATCH);
			}
		};
	}

	@Override
	public int getRanking() {
		return 20;
	}

	@Override
	public Pattern getNsURI() {
		return Pattern.compile(".*");
	}
}
