package cipm.consistency.cpr.luapcm.seffreconstruction;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.xtext.lua.lua.Block;
import org.xtext.lua.lua.LastStatement;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Statement;

import com.google.common.collect.Iterables;

import tools.vitruv.change.correspondence.view.CorrespondenceModelView;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;

/**
 * A utility class for handling AbstractAction s
 * 
 * @author Lukas Burgey
 */
public class ActionUtil {
    private static final Logger LOGGER = Logger.getLogger(ActionUtil.class.getName());

    // used when we find no block to reconstruct
//    public static List<AbstractAction> emptyStepBehaviour() {
//        var start = SeffFactory.eINSTANCE.createStartAction();
//        var stop = SeffFactory.eINSTANCE.createStopAction();
//        var actions = surroundActionList(start, null, stop);
//        return actions;
//    }

    public static void chainActions(AbstractAction a, AbstractAction b) {
        if (a != null && b != null) {
            a.setSuccessor_AbstractAction(b);
            b.setPredecessor_AbstractAction(a);
        }
    }

    /**
     * Prepares an abstract action for removal.
     * 
     * Sets predecessor and successor references like the given action does not exist.
     * 
     * @param action
     *            The action that is going to be removed
     */
    public static void removeAbstractActionFromStepChain(AbstractAction action) {
        var pred = action.getPredecessor_AbstractAction();
        var succ = action.getPredecessor_AbstractAction();
        pred.setSuccessor_AbstractAction(succ);
        succ.setPredecessor_AbstractAction(pred);
    }

    public static int getMinStatementIndexOfCorrespondingAction(Statement statement, Block block,
            EditableCorrespondenceModelView<ReactionsCorrespondence> cmv) {
        var index = Integer.MAX_VALUE;
        var actionsOfStatement = CorrespondenceUtil.getCorrespondingEObjectsByType(cmv, statement,
                AbstractAction.class);
        for (var action : actionsOfStatement) {
            var statementsOfAction = CorrespondenceUtil.getCorrespondingEObjectsByType(cmv, action, Statement.class);
            for (var statementOfAction : statementsOfAction) {
                var statementIndex = block.getStatements().indexOf(statementOfAction);
                if (statementIndex < index) {
                    index = statementIndex;
                }
            }
        }

        return index;
    }

    public static int getMaxStatementIndexOfCorrespondingAction(Statement statement, Block block,
            EditableCorrespondenceModelView<ReactionsCorrespondence> cmv) {
        var index = -1;
        var actionsOfStatement = CorrespondenceUtil.getCorrespondingEObjectsByType(cmv, statement,
                AbstractAction.class);
        for (var action : actionsOfStatement) {
            var statementsOfAction = CorrespondenceUtil.getCorrespondingEObjectsByType(cmv, action, Statement.class);
            for (var statementOfAction : statementsOfAction) {
                var statementIndex = block.getStatements().indexOf(statementOfAction);
                if (statementIndex > index) {
                    index = statementIndex;
                }
            }
        }

        return index;
    }

    public static AbstractAction getPreviousActionOfStatement(EObject statement, Block block,
            ResourceDemandingBehaviour rdBehaviour, EditableCorrespondenceModelView<ReactionsCorrespondence> cmv) {
        var blockStatements = block.getStatements();
        var statementIndex = blockStatements.indexOf(statement);

        if (statement instanceof LastStatement) {
            // return statements start with the last statement
            statementIndex = blockStatements.size() - 1;
        }

        if (statementIndex > 0) {
            for (var i = statementIndex - 1; i >= 0; i--) {
                var previousStatement = blockStatements.get(i);
                var actionsOfPreviousStatement = CorrespondenceUtil.getCorrespondingEObjectsByType(cmv,
                        previousStatement, AbstractAction.class);
                var lastPreviousAction = Iterables.getLast(actionsOfPreviousStatement, null);
                if (lastPreviousAction != null) {
                    return lastPreviousAction;
                }
            }
        }

        // fall back to the start action if we find nothing else
        for (var action : rdBehaviour.getSteps_Behaviour()) {
            if (action instanceof StartAction) {
                return action;
            }
        }

        LOGGER.error("Could not find Start action of rd behaviour");
        return null;
    }

    public static AbstractAction getSubsequentActionOfStatement(EObject statement, Block block,
            ResourceDemandingBehaviour rdBehaviour, CorrespondenceModelView<ReactionsCorrespondence> cmv) {
        var blockStatements = block.getStatements();
        var statementIndex = blockStatements.indexOf(statement);
        
        if (statement instanceof LastStatement) {
            // return statements have the stop action as successor
            for (var action : rdBehaviour.getSteps_Behaviour()) {
                if (action instanceof StopAction) {
                    return action;
                }
            }
            return null;
        }

        if (statementIndex < blockStatements.size() - 1) {
            for (var i = statementIndex + 1; i < blockStatements.size(); i++) {
                var subsequentStatement = blockStatements.get(i);
                var actionsOfFollowingStatement = CorrespondenceUtil.getCorrespondingEObjectsByType(cmv,
                        subsequentStatement, AbstractAction.class);
                if (actionsOfFollowingStatement.size() > 0) {
                    return actionsOfFollowingStatement.get(0);
                }
            }
        }

        // fall back to the stop action if we find nothing else
        for (var action : rdBehaviour.getSteps_Behaviour()) {
            if (action instanceof StopAction) {
                return action;
            }
        }
        LOGGER.error("Could not find stop action of rd behaviour");

        return null;
    }
    
    public static String getBlockName(Block block) {
        var parentStatement = EcoreUtil2.getContainerOfType(block, Statement.class);
        if (parentStatement instanceof Refble refble) {
            return refble.getName() + "_ROOT_BLOCK";
        }
        return parentStatement.toString();
    }
}
