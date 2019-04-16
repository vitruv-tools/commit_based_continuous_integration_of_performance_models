package tools.vitruv.application.pcmjava.modelrefinement.adaptive.monitoring;

import tools.vitruv.models.im.AppProbes;
import tools.vitruv.models.im.Probe;

public class ProbesActivenessImp implements ProbesActiveness {

    @Override
    public boolean isProbeActivated(String probeId) {
        AppProbes appProbes = InstrumentationModel.getAppProbes();
        Probe probe = appProbes.getProbes().stream()
                .filter(p -> probeId.equals(p.getAbstractActionID()))
                .findAny()
                .orElse(null);

        if (probe != null) {
            return probe.isIsActive();
        } else {
            return false;
        }
    }

}
