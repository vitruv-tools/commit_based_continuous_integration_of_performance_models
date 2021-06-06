package tools.vitruv.applications.pcmjava.instrumentation.probesprovider;

import java.util.List;

import org.palladiosimulator.pcm.seff.AbstractAction;

public interface ProbesProvider {
	public List<AbstractAction> getListProbes();
}
