package cipm.consistency.commitintegration.git;

/**
 * This interface specifies how diffs between commits are computed
 * @author burgey
 *
 */
public interface DiffComputation {
    public boolean getOnlySourceFiles();
    public boolean getDetectRenames();
}
