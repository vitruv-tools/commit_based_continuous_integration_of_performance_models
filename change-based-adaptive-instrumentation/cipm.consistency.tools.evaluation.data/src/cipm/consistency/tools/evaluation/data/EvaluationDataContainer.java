package cipm.consistency.tools.evaluation.data;

public class EvaluationDataContainer {
	private static EvaluationDataContainer globalContainer;
	
	public static EvaluationDataContainer getGlobalContainer() {
		if (globalContainer == null) {
			globalContainer = new EvaluationDataContainer();
		}
		return globalContainer;
	}
	
	public static void setGlobalContainer(EvaluationDataContainer newContainer) {
		globalContainer = newContainer;
	}
	
	private long evaluationTime = System.currentTimeMillis();
	private ChangeStatistic changeStatistic = new ChangeStatistic();
	private JavaEvaluationData javaComparisonResult = new JavaEvaluationData();
	private IMEvaluationData imEvalResult = new IMEvaluationData();
	
	public long getEvaluationTime() {
		return evaluationTime;
	}
	
	public ChangeStatistic getChangeStatistic() {
		return changeStatistic;
	}
	
	public JavaEvaluationData getJavaComparisonResult() {
		return javaComparisonResult;
	}
	
	public IMEvaluationData getImEvalResult() {
		return imEvalResult;
	}
}
