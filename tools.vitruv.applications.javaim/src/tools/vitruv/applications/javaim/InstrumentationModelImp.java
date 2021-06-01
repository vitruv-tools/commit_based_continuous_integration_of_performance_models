package tools.vitruv.applications.javaim;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Code2SeffFactory;
import tools.vitruv.models.im.AppProbes;
import tools.vitruv.models.im.Probe;

public class InstrumentationModelImp implements InstrumentationModel{
   private URI modelUri;
   
	public InstrumentationModelImp(URI modelUri) {
		this.modelUri = modelUri;
	}
	
	
	@Override
	public List<String> getProbesIDs() {
		List<String> listPorbesIds = new ArrayList<String>();
		AppProbes appProbes = Java2ImMethodChangeTransformationUtil.getAppProbes(this.modelUri);
		if(appProbes != null) {
			for(Probe probe: appProbes.getProbes()) {
				listPorbesIds.add(probe.getAbstractActionID());
			}
		}
		return listPorbesIds;
	}


	@Override
	public AppProbes getInstrumentationModelRoot() {
		return Java2ImMethodChangeTransformationUtil.getAppProbes(this.modelUri);
	}
		

}
