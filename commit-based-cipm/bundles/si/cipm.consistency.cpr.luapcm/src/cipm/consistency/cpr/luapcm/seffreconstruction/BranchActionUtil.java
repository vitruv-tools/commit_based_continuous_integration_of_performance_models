package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;

/**
 * A utility class for handling branches of BranchAction
 * 
 * @author Lukas Burgey
 */
public class BranchActionUtil {
    public static final String NAME_SYNTHETIC_ELSE_BRANCH = "SYNTHETIC_ELSE_BRANCH";

    public static Optional<AbstractBranchTransition> getSyntheticBranchTransition(BranchAction action) {
        for (var branch : action.getBranches_Branch()) {
            if (branch.getEntityName()
                .equals(NAME_SYNTHETIC_ELSE_BRANCH)) {
                return Optional.of(branch);
            }
        }
        return Optional.empty();
    }
}
