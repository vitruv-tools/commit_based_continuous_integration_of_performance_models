package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.SeffFactory;

/**
 * A utility class for handling AbstractAction s
 * 
 * @author Lukas Burgey
 */
public class ActionUtil {

    // used when we find no block to reconstruct
    public static List<AbstractAction> emptyStepBehaviour() {
        var start = SeffFactory.eINSTANCE.createStartAction();
        var stop = SeffFactory.eINSTANCE.createStopAction();
        var actions = surroundActionList(start, null, stop);
        return actions;
    }


    public static void chainActions(AbstractAction a, AbstractAction b) {
        if (a != null && b != null) {
            a.setSuccessor_AbstractAction(b);
            b.setPredecessor_AbstractAction(a);
        }
    }

    private static void chainActionList(List<AbstractAction> actions) {
        AbstractAction predecessor = null;
        for (var action : actions) {
            if (predecessor != null) {
                chainActions(predecessor, action);
            }
            predecessor = action;
        }
    }

    public static List<AbstractAction> surroundActionList(AbstractAction start, List<AbstractAction> list,
            AbstractAction stop) {
        List<AbstractAction> actions = new ArrayList<>();
        if (start != null) {
            actions.add(start);
        }
        if (list != null) {
            actions.addAll(list);
        }
        if (stop != null) {
            actions.add(stop);
        }
        chainActionList(actions);
        return actions;
    }
}
