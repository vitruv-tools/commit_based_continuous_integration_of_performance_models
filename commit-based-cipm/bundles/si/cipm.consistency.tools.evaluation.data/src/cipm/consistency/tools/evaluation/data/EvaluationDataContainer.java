package cipm.consistency.tools.evaluation.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for the complete data.
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class EvaluationDataContainer {
    private static EvaluationDataContainer globalContainer;

    public static EvaluationDataContainer get() {
        if (globalContainer == null) {
            globalContainer = new EvaluationDataContainer();
        }
        return globalContainer;
    }

    public static void set(EvaluationDataContainer newContainer) {
        globalContainer = newContainer;
    }

    private boolean successful = false;
//    private long evaluationTime = System.currentTimeMillis();
    private ChangeStatistic changeStatistic = new ChangeStatistic();
    private JavaEvaluationData codeModelUpdateEval = new JavaEvaluationData();
    private List<PcmEvaluationData> pcmUpdateEvals = new ArrayList<>();
    private IMEvaluationData imUpdateEval = new IMEvaluationData();
//    private InstrumentationEvaluationData instrumentationData = new InstrumentationEvaluationData();
    private InstrumentationEvaluationData instrumentationData = null;
    private ExecutionTimeData executionTimes = new ExecutionTimeData();

//    public long getEvaluationTime() {
//        return evaluationTime;
//    }
    
    public ChangeStatistic resetChangeStatistic() {
        changeStatistic = new ChangeStatistic();
        return changeStatistic;
    }

    public ChangeStatistic getChangeStatistic() {
        return changeStatistic;
    }

    public JavaEvaluationData getJavaComparisonResult() {
        return codeModelUpdateEval;
    }

    public IMEvaluationData getImEvalResult() {
        return imUpdateEval;
    }

    public InstrumentationEvaluationData getInstrumentationData() {
        return instrumentationData;
    }

    public ExecutionTimeData getExecutionTimes() {
        return executionTimes;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean success) {
        this.successful = success;
    }

    public List<PcmEvaluationData> getPcmUpdateEvals() {
        return pcmUpdateEvals;
    }
}
