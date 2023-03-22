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
    private CodeModelCorrectnessEval codeModelCorrectness = new CodeModelCorrectnessEval();
    private CodeModelUpdateEvalData codeModelUpdateEval = new CodeModelUpdateEvalData();
    private List<PcmUpdateEvalData> pcmUpdateEvals = new ArrayList<>();
    private ImUpdateEvalData imUpdateEval = new ImUpdateEvalData();
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

    public CodeModelUpdateEvalData getCodeModelUpdateEvalData() {
        return codeModelUpdateEval;
    }

    public ImUpdateEvalData getImUpdateEvalData() {
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

    public List<PcmUpdateEvalData> getPcmUpdateEvals() {
        return pcmUpdateEvals;
    }

    public CodeModelCorrectnessEval getCodeModelCorrectness() {
        return codeModelCorrectness;
    }

    public void setCodeModelCorrectness(CodeModelCorrectnessEval codeModelCorrectness) {
        this.codeModelCorrectness = codeModelCorrectness;
    }
}
