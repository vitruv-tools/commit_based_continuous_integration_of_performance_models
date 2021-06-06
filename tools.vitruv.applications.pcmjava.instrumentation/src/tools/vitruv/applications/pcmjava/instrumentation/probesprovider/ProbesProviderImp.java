package tools.vitruv.applications.pcmjava.instrumentation.probesprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.InternalCallAction;
import org.palladiosimulator.pcm.seff.LoopAction;

import tools.vitruv.applications.javaim.modelrefinement.InstrumentationModel;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;

public class ProbesProviderImp implements ProbesProvider{

	private CorrespondenceModel correspondenceModel;
	private InstrumentationModel instrumentationModel;
	
	
	public ProbesProviderImp(CorrespondenceModel correspondenceModel, InstrumentationModel instrumentationModel) {
		this.correspondenceModel = correspondenceModel;
		this.instrumentationModel = instrumentationModel;
	}
	
	
	@Override
	public List<AbstractAction> getListProbes() {
		List<AbstractAction> listAbstractAction = new ArrayList<AbstractAction>();
		List<String> probesID = this.instrumentationModel.getProbesIDs();
		for(AbstractAction aa: getAllAbstractAction(this.correspondenceModel)) {
			if(probesID.contains(aa.getId())) {
				listAbstractAction.add(aa);
			}
		}
		
		return listAbstractAction;
	}
	
	
	private  List<AbstractAction> getAllAbstractAction(CorrespondenceModel ci){
    	List<AbstractAction> listAbstractionAction = new ArrayList<AbstractAction>();
    	
    	Set<Correspondence> allCorrespondences = ci.getAllCorrespondencesWithoutDependencies();
    	for(Correspondence correspondence: allCorrespondences) {
    		try {
    			EObject correspondenceAbstractAction = correspondence.getAs().get(0);
    			if(correspondenceAbstractAction != null) {
    				if(isProbe(correspondenceAbstractAction)) {
                		listAbstractionAction.add((AbstractAction) correspondence.getAs().get(0));
                	}
    			}
    		}
    		catch (Exception e) {
				// if can not resolve just ignore, switch to the new version of vitruv may will help
			}

        }
    
        return listAbstractionAction.stream().distinct().collect(Collectors.toList());
    }
	
	
	private static boolean isProbe(EObject aa) {
		if(aa instanceof InternalAction) {
			return true;
		}
		else if(aa instanceof LoopAction) {
			return true;
		}
		else if(aa instanceof BranchAction) {
			return true;
		}
		else if(aa instanceof InternalCallAction) {
			return true;
		}
		else if(aa instanceof ExternalCallAction) {
			return true;
		}
		return false;
	}
	

}
