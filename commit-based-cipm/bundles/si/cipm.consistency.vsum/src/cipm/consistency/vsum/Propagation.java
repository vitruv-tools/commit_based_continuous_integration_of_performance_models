package cipm.consistency.vsum;

import java.nio.file.Path;
import java.util.List;

import tools.vitruv.change.composite.description.PropagatedChange;

/**
 * This class bundles the results of a propagation.
 * 
 * This includes the changes which were propagated, an the path to the previous, target and actual models of the propagation.
 * 
 * If the propagation and change resolution work correctly the files at the targetVersionParsedModel and propagationResultModel
 * should be (apart from ids) identical.
 * 
 * The original changes are the changes between the models at previousVersionParsedModel and targetVersionParsedModel.
 * 
 * @author Lukas Burgey
 */
public class Propagation {

    private Path parsedCodeModelPreviousVersionPath;

    // target version parsed model and propagation result should be identical if the change
    // resolution and propagation worked correctly
    private Path parsedCodeModelTargetVersionPath;
    private Path propagationResultCodeModelPath;
    private Path propagationResultRepositoryModelPath;

    private int originalChangeCount = 0;
    private int consequentialChangeCount = 0;

    private List<PropagatedChange> changes = null;

    Propagation(List<PropagatedChange> changes) {
        if (changes == null) {
            return;
        }

        this.changes = changes;
        for (var change : changes) {
            consequentialChangeCount += change.getConsequentialChanges()
                .getEChanges()
                .size();
            originalChangeCount += change.getOriginalChange()
                .getEChanges()
                .size();
        }
        originalChangeCount -= consequentialChangeCount;
    }

    public List<PropagatedChange> getChanges() {
        return changes;
    }

    public int getOriginalChangeCount() {
        return originalChangeCount;
    }

    public int getConsequentialChangeCount() {
        return consequentialChangeCount;
    }

    public boolean isEmpty() {
        return changes != null && changes.isEmpty();
    }

    public Path getParsedCodeModelPreviousVersionPath() {
        return parsedCodeModelPreviousVersionPath;
    }

    public void setParsedCodeModelPreviousVersionPath(Path parsedCodeModelPreviousVersionPath) {
        this.parsedCodeModelPreviousVersionPath = parsedCodeModelPreviousVersionPath;
    }

    public Path getParsedCodeModelTargetVersionPath() {
        return parsedCodeModelTargetVersionPath;
    }

    public void setParsedCodeModelTargetVersionPath(Path parsedCodeModelTargetVersionPath) {
        this.parsedCodeModelTargetVersionPath = parsedCodeModelTargetVersionPath;
    }

    public Path getPropagationResultCodeModelPath() {
        return propagationResultCodeModelPath;
    }

    public void setPropagationResultCodeModelPath(Path propagationResultCodeModelPath) {
        this.propagationResultCodeModelPath = propagationResultCodeModelPath;
    }

    public Path getPropagationResultRepositoryModelPath() {
        return propagationResultRepositoryModelPath;
    }

    public void setPropagationResultRepositoryModelPath(Path propagationResultRepositoryModelPath) {
        this.propagationResultRepositoryModelPath = propagationResultRepositoryModelPath;
    }

}
