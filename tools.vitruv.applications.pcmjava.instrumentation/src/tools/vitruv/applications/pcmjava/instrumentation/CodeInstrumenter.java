package tools.vitruv.applications.pcmjava.instrumentation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.LogicalJavaURIGenerator;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.Origin;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementListContainer;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationType;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import tools.vitruv.applications.pcmjava.instrumentation.instrumenter.MinimalMonitoringEnvironmentModelGenerator;
import tools.vitruv.applications.pcmjava.instrumentation.instrumenter.ServiceInstrumentationPointInstrumenter;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;

/**
 * An instrumenter for the source code based on the instrumentation points in the instrumentation model.
 * 
 * @author Martin Armbruster
 */
public class CodeInstrumenter {
	public void instrument(InstrumentationModel im, CorrespondenceModel cm, Resource javaModel, Path output) {
		ResourceSet targetSet = new ResourceSetImpl();
		Resource copy = targetSet.createResource(javaModel.getURI());
		copy.getContents().addAll(EcoreUtil.copyAll(javaModel.getContents()));
		
		MinimalMonitoringEnvironmentModelGenerator gen =
				new MinimalMonitoringEnvironmentModelGenerator(copy);
		ServiceInstrumentationPointInstrumenter sipIns =
				new ServiceInstrumentationPointInstrumenter(gen);
		
		for (ServiceInstrumentationPoint sip : im.getPoints()) {
			Method service = CorrespondenceModelUtil
					.getCorrespondingEObjects(cm, sip.getService(), Method.class).iterator().next();
			Method copiedService = findCopiedEObject(targetSet, service);
			ActionStatementMapping statementMap = createActionStatementMapping(targetSet, sip, cm);
			sipIns.instrument(copiedService, sip, statementMap);
		}
		
		saveModels(targetSet, copy, output);
	}
	
	private ActionStatementMapping createActionStatementMapping(ResourceSet copyContainer,
			ServiceInstrumentationPoint sip, CorrespondenceModel cm) {
		ActionStatementMapping statementMap = new ActionStatementMapping();
		for (ActionInstrumentationPoint aip : sip.getActionInstrumentationPoints()) {
			Set<Statement> correspondingStatements = CorrespondenceModelUtil
					.getCorrespondingEObjects(cm, aip.getAction(), Statement.class);
			Statement firstStatement;
			if (aip.getType() == InstrumentationType.INTERNAL) {
				Statement lastStatement = findFirstOrLastStatement(correspondingStatements, false);
				statementMap.getAbstractActionToLastStatementMapping().put(aip.getAction(),
						findCopiedEObject(copyContainer, lastStatement));
				firstStatement = findFirstOrLastStatement(correspondingStatements, true);
			} else {
				firstStatement = correspondingStatements.iterator().next();
			}
			Statement copiedFirstStatement = findCopiedEObject(copyContainer, firstStatement);
			statementMap.put(aip.getAction(), copiedFirstStatement);
		}
		return statementMap;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends EObject> T findCopiedEObject(ResourceSet copyContainer, T original) {
		EObject potResult = copyContainer.getEObject(
				URI.createURI(EcoreUtil.getID(original)), false);
		if (potResult != null && original.eClass().isInstance(potResult)) {
			return (T) potResult;
		}
		return null;
	}
	
	private Statement findFirstOrLastStatement(Set<Statement> statements, boolean findFirst) {
		if (statements.size() == 1) {
			return statements.iterator().next();
		}
		EObject container = statements.iterator().next().eContainer();
		if (container instanceof StatementListContainer) {
			EList<Statement> stats = ((StatementListContainer) container).getStatements();
			if (findFirst) {
				for (Statement next : stats) {
					if (statements.contains(next)) {
						return next;
					}
				}
			}
			Set<Statement> copiedStatements = new HashSet<>(statements);
			for (Statement next : stats) {
				copiedStatements.remove(next);
				if (copiedStatements.size() == 1) {
					return copiedStatements.iterator().next();
				}
			}
		}
		return null;
	}
	
	private void saveModels(ResourceSet copyContainer, Resource copiedResource, Path target) {
		for (EObject root : new ArrayList<>(copiedResource.getContents())) {
			if (root instanceof CompilationUnit) {
				CompilationUnit cu = (CompilationUnit) root;
				if (cu.getOrigin() == Origin.FILE) {
					Resource newResource = copyContainer.createResource(createURI(cu, target));
					newResource.getContents().add(cu);
					try {
						newResource.save(null);
					} catch (IOException e) {
					}
				}
			}
		}
	}
	
	private URI createURI(CompilationUnit cu, Path newContainer) {
		Path resulting = newContainer;
		for (String ns : cu.getNamespaces()) {
			resulting = resulting.resolve(ns);
		}
		resulting = resulting.resolve(cu.getName() + LogicalJavaURIGenerator.JAVA_FILE_EXTENSION);
		return URI.createFileURI(resulting.toAbsolutePath().toString());
	}
}
