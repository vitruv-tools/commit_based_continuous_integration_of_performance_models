package cipm.consistency.commitintegration.lang.lua.changeresolution.equality;

import org.eclipse.emf.ecore.EObject;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Statement_Assignment;

public class LuaEqualityChecker {

    private static boolean eClassMatch(EObject left, EObject right) {
        var leftClass = left.eClass()
            .getName();
        var rightClass = right.eClass()
            .getName();
        return leftClass.equals(rightClass);
    }

    /*
     * Do refbles match by name
     */
    private static boolean refblesDoMatch(Refble left, Refble right) {
        return left.getName()
            .equals(right.getName());
//        return left != null && right != null && left.getName()
//            .equals(right.getName());
    }

    private static Boolean match(Refble left, Refble right) {
        return refblesDoMatch(left, right);
    }

    private static Boolean match(Expression_VariableName left, Expression_VariableName right) {
        if (left.getRef() == null || right.getRef() == null) {
            return false;
        }
        return refblesDoMatch(left.getRef(), right.getRef());
    }

    /*
     * Only negative matching here
     */
    private static Boolean match(Expression_Functioncall_Direct left, Expression_Functioncall_Direct right) {
        if (left.getCalledFunction() == null || right.getCalledFunction() == null
                || !refblesDoMatch(left.getCalledFunction(), right.getCalledFunction())) {
            return false;
        }
        return null;
    }

    /*
     * Only negative matching here
     */
    private static Boolean match(Statement_Assignment left, Statement_Assignment right) {
        // assignment using same destinations
        var leftDests = left.getDests();
        var rightDests = right.getDests();
        if (leftDests.size() != rightDests.size()) {
            return false;
        }
        for (var i = 0; i < leftDests.size(); i++) {
            var destMatch = match(leftDests.get(i), rightDests.get(i));
            if (destMatch != null && !destMatch) {
                return false;
            }
        }
        return null;
    }

    /**
     * Do objects match based on lua attributes, like name
     * 
     * @param left
     * @param right
     * @return True if matching, false if not, null if undecided
     */
    public static Boolean match(EObject left, EObject right) {
        if (left == null || right == null) {
            return false;
        }
        if (!eClassMatch(left, right)) {
            return false;
        }

        if (left instanceof Refble leftRef && right instanceof Refble rightRef) {
            return match(leftRef, rightRef);
        } else if (left instanceof Expression_Functioncall_Direct leftCall
                && right instanceof Expression_Functioncall_Direct rightCall) {
            return match(leftCall, rightCall);
        } else if (left instanceof Expression_VariableName leftVar
                && right instanceof Expression_VariableName rightVar) {
            return match(leftVar, rightVar);
        } else if (left instanceof Statement_Assignment leftAss && right instanceof Statement_Assignment rightAss) {
            return match(leftAss, rightAss);
        }
        return null;
    }
}
