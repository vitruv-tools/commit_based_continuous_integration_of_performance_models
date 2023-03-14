package cipm.consistency.commitintegration.lang.lua.changeresolution.equality;

import org.eclipse.emf.ecore.EObject;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_TableConstructor;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Field_AppendEntryToTable;
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

    private static boolean match(Refble left, Refble right) {
        if (left == null || right == null) {
            return false;
        }
        return left.getName()
            .equals(right.getName());
    }

    private static Boolean match(Expression_VariableName left, Expression_VariableName right) {
        return match(left.getRef(), right.getRef());
    }

    private static Boolean match(Expression_Functioncall_Direct left, Expression_Functioncall_Direct right) {
        if (left.getCalledFunction() == null || right.getCalledFunction() == null
                || !match(left.getCalledFunction(), right.getCalledFunction())) {
            return false;
        }

        var leftArgs = left.getCalledFunctionArgs()
            .getArguments();
        var rightArgs = right.getCalledFunctionArgs()
            .getArguments();
        if (leftArgs.size() != rightArgs.size()) {
            return false;
        }
        for (var i = 0; i < leftArgs.size(); i++) {
            var argsMatch = match(leftArgs.get(i), rightArgs.get(i));
            if (argsMatch != null && !argsMatch) {
                return false;
            }
        }

        return true;
    }

    private static Boolean match(Expression_TableConstructor left, Expression_TableConstructor right) {
        var leftFields = left.getFields();
        var rightFields = left.getFields();
        if (leftFields.size() != rightFields.size()) {
            return false;
        }
        var i = 0;
        for (var leftField : leftFields) {
            var rightField = rightFields.get(i);
            var doMatch = match(leftField, rightField);
            if (doMatch != null && !doMatch) {
                return false;
            }
            i++;
        }
        return true;
    }

    private static Boolean match(Field_AppendEntryToTable left, Field_AppendEntryToTable right) {
        var leftValue = left.getValue();
        var rightValue = left.getValue();
        return match(leftValue, rightValue);
    }

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

        var leftVals = left.getValues();
        var rightVals = right.getValues();
        if (leftVals.size() != rightVals.size()) {
            return false;
        }
        for (var i = 0; i < leftVals.size(); i++) {
            var valMatch = match(leftVals.get(i), rightVals.get(i));
            if (valMatch != null && !valMatch) {
                return false;
            }
        }
        return true;
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
        } else if (left instanceof Refble l && right instanceof Refble r) {
            return match(l, r);
        }
        return null;
    }
}
