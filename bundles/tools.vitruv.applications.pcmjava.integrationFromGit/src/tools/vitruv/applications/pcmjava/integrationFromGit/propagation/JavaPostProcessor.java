package tools.vitruv.applications.pcmjava.integrationFromGit.propagation;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.DifferenceState;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.emftext.language.java.members.Method;

public class JavaPostProcessor implements IPostProcessor {
	@Override
	public void postMatch(Comparison comparison, Monitor monitor) {
	}

	@Override
	public void postDiff(Comparison comparison, Monitor monitor) {
		comparison.getMatches().forEach(this::checkForMethods);
	}
	
	private void checkForMethods(Match m) {
		if (m.getLeft() instanceof Method || m.getRight() instanceof Method) {
			if (hasDifferences(m)) {
				ReferenceChange methodDiff = CompareFactory.eINSTANCE.createReferenceChange();
				if (m.getLeft() != null) {
					methodDiff.setKind(DifferenceKind.ADD);
				} else {
					methodDiff.setKind(DifferenceKind.DELETE);
				}
				methodDiff.setState(DifferenceState.UNRESOLVED);
				methodDiff.setSource(DifferenceSource.LEFT);
				methodDiff.setReference(m.getLeft().eContainmentFeature());
				methodDiff.setValue(m.getLeft());
				((Match) m.eContainer()).getDifferences().add(methodDiff);
			}
		} else {
			m.getSubmatches().forEach(this::checkForMethods);
		}
	}
	
	private boolean hasDifferences(Match m) {
		return m.getDifferences().size() > 0
				|| m.getSubmatches().stream().map(this::hasDifferences)
				.reduce(false, (a, b) -> a || b);
	}

	@Override
	public void postRequirements(Comparison comparison, Monitor monitor) {
	}

	@Override
	public void postEquivalences(Comparison comparison, Monitor monitor) {
	}

	@Override
	public void postConflicts(Comparison comparison, Monitor monitor) {
	}

	@Override
	public void postComparison(Comparison comparison, Monitor monitor) {
	}
}
