package cipm.consistency.base.vitruv.vsum.test;

import java.nio.file.Paths;
import java.util.HashSet;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractLoopAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.InternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import cipm.consistency.base.vsum.domains.InstrumentationModelDomain;
import tools.vitruv.domains.pcm.PcmNamespace;

public class EvaluationIMUpdate {
	private static final String repositoryPath = Paths.get("target/TeaStoreTest/pcm/Repository."
			+ PcmNamespace.REPOSITORY_FILE_EXTENSION).toAbsolutePath().toString();
	private static final String imPath = Paths.get("target/TeaStoreTest/im/InstrumentationModel"
			+ InstrumentationModelDomain.INSTRUMENTATION_SUFFIX).toAbsolutePath().toString();
	private HashSet<EObject> points;
	
	@Test
	public void evaluateIMUpdate() {
		ResourceSet set = new ResourceSetImpl();
		Resource repoResource = set.getResource(URI.createFileURI(repositoryPath), true);
		Repository repo = (Repository) repoResource.getContents().get(0);
		Resource imResource = set.getResource(URI.createFileURI(imPath), true);
		EcoreUtil.resolveAll(imResource);
		InstrumentationModel im = (InstrumentationModel) imResource.getContents().get(0);
		points = new HashSet<>();
		
		int numberAllInstrumentationPoints = 0;
		for (TreeIterator<EObject> iter = im.eAllContents(); iter.hasNext();) {
			numberAllInstrumentationPoints++;
			points.add(iter.next());
		}
		
		int numberMatchedInstrumentationPoints = 0;
		for (RepositoryComponent com : repo.getComponents__Repository()) {
			if (com instanceof BasicComponent) {
				BasicComponent basicCom = (BasicComponent) com;
				for (ServiceEffectSpecification seff :
						basicCom.getServiceEffectSpecifications__BasicComponent()) {
					if (seff instanceof ResourceDemandingSEFF) {
						ResourceDemandingSEFF rdseff = (ResourceDemandingSEFF) seff;
						var sip = findSIP(im, rdseff);
						if (sip != null) {
							numberMatchedInstrumentationPoints++;
							numberMatchedInstrumentationPoints += checkActionInstrumentationPoints(sip, rdseff);
						}
					}
				}
			}
		}
		
		System.out.println("Does the number of matched instrumentation points (" + numberMatchedInstrumentationPoints
				+ ") equal the number of all instrumentation points (" + numberAllInstrumentationPoints + ")?");
		System.out.println(numberMatchedInstrumentationPoints == numberAllInstrumentationPoints);
	}
	
	private ServiceInstrumentationPoint findSIP(InstrumentationModel im, ResourceDemandingSEFF seff) {
		for (var sip : im.getPoints()) {
			if (sip.getService() == seff) {
				points.remove(sip);
				return sip;
			}
		}
		System.out.println("The RDSEFF " + seff.getDescribedService__SEFF().getEntityName() + " in "
				+ seff.getBasicComponent_ServiceEffectSpecification().getEntityName()
				+ " has no instrumentation point.");
		return null;
	}
	
	private int checkActionInstrumentationPoints(ServiceInstrumentationPoint sip, ResourceDemandingSEFF seff) {
		int numberOfFoundAIPs = 0;
		for (AbstractAction aa : seff.getSteps_Behaviour()) {
			numberOfFoundAIPs += checkActionInstrumentationPoint(sip, aa);
		}
		return numberOfFoundAIPs;
	}
	
	private int checkActionInstrumentationPoint(ServiceInstrumentationPoint sip, AbstractAction aa) {
		int numberOfFoundAIPs = 0;
		if (aa instanceof AbstractLoopAction) {
			AbstractLoopAction loop = (AbstractLoopAction) aa;
			for (AbstractAction innerAA : loop.getBodyBehaviour_Loop().getSteps_Behaviour()) {
				numberOfFoundAIPs += checkActionInstrumentationPoint(sip, innerAA);
			}
			if (findAIP(sip, loop)) {
				numberOfFoundAIPs++;
			}
		} else if (aa instanceof BranchAction) {
			BranchAction branch = (BranchAction) aa;
			for (var transition : branch.getBranches_Branch()) {
				for (AbstractAction innerAA :
						transition.getBranchBehaviour_BranchTransition().getSteps_Behaviour()) {
					numberOfFoundAIPs += checkActionInstrumentationPoint(sip, innerAA);
				}
			}
			if (findAIP(sip, branch)) {
				numberOfFoundAIPs++;
			}
		} else if (aa instanceof InternalAction || aa instanceof ExternalCallAction
				|| aa instanceof InternalCallAction) {
			if (findAIP(sip, aa)) {
				numberOfFoundAIPs++;
			}
		}
		return numberOfFoundAIPs;
	}
	
	private boolean findAIP(ServiceInstrumentationPoint sip, AbstractAction aa) {
		for (var aip : sip.getActionInstrumentationPoints()) {
			if (aip.getAction() == aa) {
				points.remove(aip);
				return true;
			}
		}
		System.out.println("The action " + aa.getEntityName() + " has no instrumentation point.");
		return false;
	}
}
