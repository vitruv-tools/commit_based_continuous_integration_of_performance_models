package cipm.consistency.commitintegration.lang.lua.changeresolution;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.eobject.EditionDistance;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.ProximityEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.ProximityEObjectMatcher.DistanceFunction;
import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

public class LuaSimilarityChecker extends SimilarityChecker {

    private DistanceFunction distanceFunction;
    private IEObjectMatcher eObjectMatcher;

    private IComparisonFactory comparisonFactory;

    public LuaSimilarityChecker() {
        super();

        comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());

        distanceFunction = new EditionDistance();
        eObjectMatcher = new ProximityEObjectMatcher(distanceFunction);
    }

    @Override
    public Boolean isSimilar(EObject element1, EObject element2, boolean checkStatementPosition) {
        if (!element1.eClass()
            .equals(element2.eClass())) {
            return Boolean.FALSE;
        }

        var comparison = comparisonFactory.createComparison();
        comparison.setThreeWay(false);

        var monitor = new BasicMonitor();

        // TODO using the matcher like this seems really stupid
        eObjectMatcher.createMatches(comparison, List.of(element1)
            .iterator(),
                List.of(element2)
                    .iterator(),
                new ArrayList<EObject>().iterator(), monitor);

        // check the comparison if there were unresolveable differences
        if (comparison.getDifferences()
            .size() == 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

}
