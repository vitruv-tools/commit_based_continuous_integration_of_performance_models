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
	private String oldCommit;
	private String newCommit;
	private JavaEvaluationData javaComparisonResult = new JavaEvaluationData();
	private IMEvaluationData imEvalResult = new IMEvaluationData();
	
	public long getEvaluationTime() {
		return evaluationTime;
	}
	
	public String getOldCommit() {
		return oldCommit;
	}
	
	public void setOldCommit(String oldCommit) {
		this.oldCommit = oldCommit;
	}
	
	public String getNewCommit() {
		return newCommit;
	}
	
	public void setNewCommit(String newCommit) {
		this.newCommit = newCommit;
	}
	
	public JavaEvaluationData getJavaComparisonResult() {
		return javaComparisonResult;
	}
	
	public IMEvaluationData getImEvalResult() {
		return imEvalResult;
	}
}
