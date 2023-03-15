package cipm.consistency.commitintegration.lang.lua.changeresolution.equality;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.ecore.EObject;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_Length;
import org.xtext.lua.lua.Expression_TableConstructor;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Field_AppendEntryToTable;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Statement_Assignment;

import com.google.common.cache.LoadingCache;

public class LuaEqualityHelper extends EqualityHelper {

//    private static final Logger LOGGER = Logger.getLogger(LuaEqualityHelper.class);

    private EditionDistanceEqualityChecker editionDistanceEqualityChecker;

    /**
     * Constructor to initialize the required cache.
     *
     * @param uriCache
     *            The cache to use during the equality checks.
     */
    public LuaEqualityHelper(LoadingCache<EObject, org.eclipse.emf.common.util.URI> uriCache) {
        super(uriCache);

        editionDistanceEqualityChecker = new EditionDistanceEqualityChecker();
    }

    private boolean matchEList(EList<? extends EObject> left, EList<? extends EObject> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (var i = 0; i < left.size(); i++) {
            if (!match(left.get(i), right.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean eClassMatch(EObject left, EObject right) {
        var leftClass = left.eClass()
            .getName();
        var rightClass = right.eClass()
            .getName();
        return leftClass.equals(rightClass);
    }

    private static boolean match(Refble left, Refble right) {
        if (left == null || right == null) {
            return false;
        }
        return left.getName()
            .equals(right.getName());
    }

    private boolean match(Expression_VariableName left, Expression_VariableName right) {
        return match(left.getRef(), right.getRef());
    }

    private boolean match(Expression_Functioncall_Direct left, Expression_Functioncall_Direct right) {
        if (left.getCalledFunction() == null || right.getCalledFunction() == null
                || !match(left.getCalledFunction(), right.getCalledFunction())) {
            return false;
        }
        return matchEList(left.getCalledFunctionArgs()
            .getArguments(),
                right.getCalledFunctionArgs()
                    .getArguments());
    }

    private boolean match(Expression_TableConstructor left, Expression_TableConstructor right) {
        return matchEList(left.getFields(), right.getFields());
    }

    private boolean match(Field_AppendEntryToTable left, Field_AppendEntryToTable right) {
        return match(left.getValue(), right.getValue());
    }

    private boolean match(Expression_Length left, Expression_Length right) {
        return match(left.getExp(), right.getExp());
    }
    private boolean match(Statement_Assignment left, Statement_Assignment right) {
        var destsMatch = matchEList(left.getDests(), right.getDests());
        var valsMatch = matchEList(left.getValues(), right.getValues());
        return destsMatch && valsMatch;
    }

    /**
     * Do objects match based on lua attributes, like name
     * 
     * @param left
     * @param right
     * @return True if matching, false if not
     */
    public boolean match(EObject left, EObject right) {
        if (left == null || right == null) {
            return false;
        }

        if (!eClassMatch(left, right)) {
            return false;
        }

        if (left instanceof Statement_Assignment l && right instanceof Statement_Assignment r) {
            return match(l, r);
        } else if (left instanceof Expression_Functioncall_Direct l
                && right instanceof Expression_Functioncall_Direct r) {
            return match(l, r);
        } else if (left instanceof Expression_VariableName l && right instanceof Expression_VariableName r) {
            return match(l, r);
        } else if (left instanceof Field_AppendEntryToTable l && right instanceof Field_AppendEntryToTable r) {
            return match(l, r);
        } else if (left instanceof Expression_TableConstructor l && right instanceof Expression_TableConstructor r) {
            return match(l, r);
        } else if (left instanceof Expression_Length l && right instanceof Expression_Length r) {
            return match(l, r);
        } else if (left instanceof Refble l && right instanceof Refble r) {
            return match(l, r);
        }

        // no decision from the custom matchers -> use fallback
        return editionDistanceEqualityChecker.match(left, right);
    }

    @Override
    protected boolean matchingEObjects(EObject left, EObject right) {
        return match(left, right);
    }
}
