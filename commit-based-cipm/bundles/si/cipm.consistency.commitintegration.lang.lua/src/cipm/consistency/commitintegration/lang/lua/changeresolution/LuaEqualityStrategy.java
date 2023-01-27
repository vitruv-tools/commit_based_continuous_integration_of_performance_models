package cipm.consistency.commitintegration.lang.lua.changeresolution;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.eobject.EditionDistance;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.ProximityEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.ProximityEObjectMatcher.DistanceFunction;
import org.eclipse.emf.ecore.EObject;
import org.splevo.diffing.match.HierarchicalMatchEngine.EqualityStrategy;
import org.xtext.lua.lua.Chunk;
import org.xtext.lua.lua.Referenceable;

public class LuaEqualityStrategy implements EqualityStrategy {

    private static final Logger LOGGER = Logger.getLogger(LuaEqualityStrategy.class);

    private IComparisonFactory comparisonFactory;
    private DistanceFunction distanceFunction;
    private IEObjectMatcher eObjectMatcher;

    public LuaEqualityStrategy() {
        super();

        comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
        distanceFunction = new EditionDistance();
        eObjectMatcher = new ProximityEObjectMatcher(distanceFunction);
    }

    private Comparison compare(EObject left, EObject right) {
        var comparison = comparisonFactory.createComparison();
        comparison.setThreeWay(false);

        var monitor = new BasicMonitor();

        // TODO using the matcher like this seems really stupid
        eObjectMatcher.createMatches(comparison, List.of(left)
            .iterator(),
                List.of(right)
                    .iterator(),
                new ArrayList<EObject>().iterator(), monitor);

        return comparison;
    }

    @Override
    public boolean areEqual(EObject left, EObject right) {
        var classLeft = left.eClass()
            .getName();
        var classRight = right.eClass()
            .getName();
        if (!classLeft.equals(classRight)) {
            LOGGER.trace(String.format("Objects have different eClass: %s vs %s", classLeft, classRight));
            return false;
        }

        if (left instanceof Referenceable ref1 && right instanceof Referenceable ref2) {
            if (!ref1.getName()
                .equals(ref2.getName())) {
                return false;
            }
        }

        var comparison = compare(left, right);
        if (left instanceof Chunk) {
            var foo = 42;
        }
        
        // this only occurs for the ComponentSet currently (v010 -> v100) 
        if (comparison.getMatches().size() == 1) {
            return true;
        }

        // TODO the following condition did not work (differences is often just null)
//        // check f there were unresolvable differences
//        if (comparison.getDifferences()
//            .size() == 0) {
//            return true;
//        }

        return false;
    }

}
