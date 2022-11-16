package cipm.consistency.vsum.test.evaluator;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.CommitIntegrationController;
import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.diff.util.JavaChangedMethodDetectorDiffPostProcessor;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.commitintegration.lang.java.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.lang.lua.LuaModelFacade;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.InstrumentationEvaluationData;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.repository.OperationSignature;
import tools.vitruv.change.correspondence.Correspondence;
//import tools.vitruv.domains.java.tuid.JamoppStringOperations;
//import tools.vitruv.changes.correspondence.model.CorrespondenceModel;
//import tools.vitruv.change.correspondence.util.CorrespondenceModelUtil;
import tools.vitruv.change.correspondence.view.CorrespondenceModelView;

/**
 * Evaluates the instrumentation.
 * 
 * @author Martin Armbruster
 */
public class InstrumentationEvaluator extends CommitIntegrationController<LuaModelFacade> {
    private final int numberAdditionalStatements = 10;
    private final int numberServiceStatements = 7;
    private final int numberStatementsPerParameter = 1;
    private final int numberExternalCallStatements = 1;
    private final int numberBranchStatements = 1;
    private final int numberLoopStatements = 3;
    private final int numberInternalActionStatements = 2;
    private final int numberInternalActionStatementsPerReturnStatement = 2;

    /**
     * Evaluates the instrumented model. It is assumed to be executed directly after the
     * instrumentation.
     * 
     * @param im
     *            the extended IM.
     * @param javaModel
     *            the original Java model.
     * @param instrumentedModel
     *            the instrumented Java model.
     * @param cm
     *            the correspondence model.
     */
    public void evaluateInstrumentationDependently(InstrumentationModel im, Resource javaModel,
            Resource instrumentedModel, CorrespondenceModelView<Correspondence> cmv) {
        if (instrumentedModel == null || instrumentedModel.getContents()
            .isEmpty()) {
            return;
        }
        InstrumentationEvaluationData insEvalData = EvaluationDataContainer.getGlobalContainer()
            .getInstrumentationData();
        int javaStatements = countStatements(javaModel);
        int instrumStatements = countStatements(instrumentedModel);
        insEvalData.setExpectedLowerStatementDifferenceCount(countExpectedStatements(im, cmv, true));
        insEvalData.setExpectedUpperStatementDifferenceCount(countExpectedStatements(im, cmv, false));
        insEvalData.setStatementDifferenceCount(instrumStatements - javaStatements);
    }

    /**
     * Reloads the instrumented code and evaluates it.
     * 
     * @param im
     *            the extended IM.
     * @param javaModel
     *            the original Java model.
     * @param fileLayout
     *            the Java file layout.
     * @param cm
     *            the correspondence model.
     */
    public void evaluateInstrumentationIndependently(CommitIntegrationState<LuaModelFacade> state) {
        
//    public void evaluateInstrumentationIndependently(InstrumentationModel im, Resource javaModel,
//            CommitIntegrationState<LuaModel> state, CorrespondenceModelView<Correspondence> cmv) {
        
        var im = state.getImFacade().getModel();
        var cmv = state.getVsumFacade().getCorrespondenceView();
        var codeModel = state.getCodeModelFacade().getResource();
        
        
        InstrumentationEvaluationData insEvalData = EvaluationDataContainer.getGlobalContainer()
            .getInstrumentationData();
        insEvalData.setExpectedLowerStatementDifferenceCount(countExpectedStatements(im, cmv, true));
        insEvalData.setExpectedUpperStatementDifferenceCount(countExpectedStatements(im, cmv, false));
        Resource reloadedModel = JavaParserAndPropagatorUtils.parseJavaCodeIntoOneModel(state.getImFacade()
            .getDirLayout()
            .getInstrumentationDirPath(),
                state.getCodeModelFacade()
                    .getDirLayout()
                    .getRootDirPath()
                    .resolve("ins.javaxmi"),
                state.getCodeModelFacade()
                    .getDirLayout()
                    .getModuleConfigurationPath());

        var potentialProxies = EcoreUtil.ProxyCrossReferencer.find(reloadedModel);

        int javaStatements = countStatements(codeModel);
        int instrumStatements = countStatements(reloadedModel);
        insEvalData.setReloadedStatementDifferenceCount(instrumStatements - javaStatements);
        if (!potentialProxies.isEmpty()) {
            insEvalData.getUnmatchedChangedMethods()
                .add("Reloaded model contains proxy objects.");
            return;
        }
        var postProcessor = new JavaChangedMethodDetectorDiffPostProcessor();
        JavaModelComparator.compareJavaModels(reloadedModel, javaModel, null, null, postProcessor);
        Set<Method> changed = new HashSet<>(postProcessor.getChangedMethods());
        insEvalData.setNumberChangedMethods(changed.size());
        for (var sip : im.getPoints()) {
            var corMeth = cmv.getCorrespondingEObjects(sip.getService(), null);
            boolean success = changed.remove(corMeth.stream()
                .findFirst()
                .get());
            if (!success) {
                insEvalData.getUnmatchedIPs()
                    .add(sip.getId());
            }
        }
        for (Method m : changed) {
            insEvalData.getUnmatchedChangedMethods()
                .add(convertToString(m));
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

    private int countExpectedStatements(InstrumentationModel im, CorrespondenceModelView<Correspondence> cmv,
            boolean lowerCount) {
        int statements = numberAdditionalStatements;
        for (var sip : im.getPoints()) {
            statements += numberServiceStatements;
            statements += ((OperationSignature) sip.getService()
                .getDescribedService__SEFF()).getParameters__OperationSignature()
                    .size() * numberStatementsPerParameter;
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
                    statements += numberInternalActionStatements;
                    if (!lowerCount) {
                        Set<EObject> stats = cmv.getCorrespondingEObjects(aip.getAction(), null);
                        for (EObject s : stats) {
                            if (s instanceof Return) {
                                statements += numberInternalActionStatementsPerReturnStatement;
                            }
                            statements += numberInternalActionStatementsPerReturnStatement
                                    * ((Statement) s).getChildrenByType(Return.class)
                                        .size();
                        }
                    }
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

    private String convertToString(Method method) {
        StringBuilder builder = new StringBuilder();
//		builder.append(method.getContainingConcreteClassifier().getQualifiedName());
//		builder.append("::");
//		builder.append(method.getName());
//		builder.append("(");
//		for (var param : method.getParameters()) {
//			builder.append(JamoppStringOperations.getStringRepresentation(param));
//			builder.append(",");
//		}
//		builder.append(")");
//		builder.append(JamoppStringOperations.getStringRepresentation(method.getTypeReference().getTarget(),
//				method.getTypeReference().getArrayDimension()));
        return builder.toString();
    }
}
