package cipm.consistency.commitintegration.git.impl;

import cipm.consistency.commitintegration.git.DiffComputation;

public class JavaDiffComputation implements DiffComputation {
    @Override
    public boolean getOnlySourceFiles() {
        return true;
    }

    @Override
    public boolean getDetectRenames() {
        return true;
    }
}
