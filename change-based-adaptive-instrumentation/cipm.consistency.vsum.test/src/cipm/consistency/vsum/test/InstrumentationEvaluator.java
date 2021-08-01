package cipm.consistency.vsum.test;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.repository.OperationSignature;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.JavaFileSystemLayout;
import cipm.consistency.commitintegration.JavaParserAndPropagatorUtility;
import cipm.consistency.commitintegration.diff.util.JavaChangedMethodDetectorDiffPostProcessor;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.InstrumentationEvaluationData;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;

public class InstrumentationEvaluator {
	private final int numberAdditionalStatements = 10;
	private final int numberServiceStatements = 7;
	private final int numberStatementsPerParameter = 1;
	private final int numberExternalCallStatements = 1;
	private final int numberBranchStatements = 1;
	private final int numberLoopStatements = 3;
	private final int lowerNumberInternalActionStatements = 2;
	private final int upperNumberInternalActionStatements = lowerNumberInternalActionStatements + 1;
	
	public void evaluateInstrumentation(InstrumentationModel im, Resource javaModel,
			Resource instrumentedModel, JavaFileSystemLayout fileLayout, CorrespondenceModel cm) {
		InstrumentationEvaluationData insEvalData = EvaluationDataContainer
				.getGlobalContainer().getInstrumentationData();
		int javaStatements = countStatements(javaModel);
		int instrumStatements = countStatements(instrumentedModel);
		insEvalData.setLowerStatementDifferenceCount(countExpectedStatements(im, true));
		insEvalData.setUpperStatementDifferenceCount(countExpectedStatements(im, false));
		insEvalData.setStatementDifferenceCount(instrumStatements - javaStatements);
		Resource reloadedModel = JavaParserAndPropagatorUtility.parseJavaCodeIntoOneModel(
				fileLayout.getInstrumentationCopy(),
				fileLayout.getJavaModelFile().resolveSibling("ins.javaxmi"),
				fileLayout.getModuleConfiguration());
		instrumStatements = countStatements(reloadedModel);
		insEvalData.setReloadedStatementDifferenceCount(instrumStatements - javaStatements);
		var postProcessor = new JavaChangedMethodDetectorDiffPostProcessor();
		JavaModelComparator.compareJavaModels(reloadedModel, javaModel,
				null, null, postProcessor);
		Set<Method> changed = new HashSet<>(postProcessor.getChangedMethods());
		insEvalData.setNumberChangedMethods(changed.size());
		for (var sip : im.getPoints()) {
			var corMeth = CorrespondenceModelUtil.getCorrespondingEObjects(cm, sip.getService(), Method.class);
			boolean success = changed.remove(corMeth.stream().findFirst().get());
			if (!success) {
				insEvalData.getUnmatchedIPs().add(sip.getId());
			}
		}
		for (Method m : changed) {
			insEvalData.getUnmatchedChangedMethods().add(convertToLongName(m));
		}
	}
	
	private int countStatements(Resource model) {
		int statements = 0;
		for (var iter = model.getAllContents(); iter.hasNext();) {
			EObject obj = iter.next();
			if (obj instanceof Statement) {
				statements++;
			}
		}
		return statements;
	}
	
	private int countExpectedStatements(InstrumentationModel im, boolean calcLowerBound) {
		int statements = numberAdditionalStatements;
		for (var sip : im.getPoints()) {
			statements += numberServiceStatements;
			statements += ((OperationSignature) sip.getService().getDescribedService__SEFF())
					.getParameters__OperationSignature().size() * numberStatementsPerParameter;
			for (var aip : sip.getActionInstrumentationPoints()) {
				if (!aip.isActive()) {
					continue;
				}
				switch (aip.getType()) {
					case BRANCH:
						statements += numberBranchStatements;
						break;
					case INTERNAL:
					case INTERNAL_CALL:
						statements += calcLowerBound ? lowerNumberInternalActionStatements
								: upperNumberInternalActionStatements;
						break;
					case EXTERNAL_CALL:
						statements += numberExternalCallStatements;
						break;
					case LOOP:
					default:
						statements += numberLoopStatements;
						break;
				}
			}
		}
		return statements;
	}
	
	private String convertToLongName(Method m) {
		StringBuilder builder = new StringBuilder();
		builder.append(m.getContainingConcreteClassifier().getQualifiedName());
		builder.append("::");
		builder.append(m.getName());
		builder.append("(");
		for (var param : m.getParameters()) {
			builder.append(param.getName());
			builder.append(",");
		}
		builder.append(")");
		return builder.toString();
	}
}
