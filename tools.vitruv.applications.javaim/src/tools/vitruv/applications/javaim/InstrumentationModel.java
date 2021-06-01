package tools.vitruv.applications.javaim;

import java.util.List;

import org.eclipse.emf.common.util.URI;

import tools.vitruv.models.im.AppProbes;
import tools.vitruv.models.im.Probe;

public interface InstrumentationModel {
	public List<String> getProbesIDs();
	public AppProbes getInstrumentationModelRoot();
}
