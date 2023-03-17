package cipm.consistency.vsum.test.evaluator.commitHistory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

public class CommitHistoryEvaluator {

    private List<EvaluationDataContainer> commitPropagationEvals = new ArrayList<>();
    private CommitHistoryEvaluationData historyEvalData = new CommitHistoryEvaluationData();

    public void addEvaluationDataContainer(EvaluationDataContainer commitPropagationEval) {
        commitPropagationEvals.add(commitPropagationEval);
    }

    public void evaluate() {
        evaluateImHistory(historyEvalData.getImUpdateHistoryEval());


        // also copy the per commit evals into the class
        historyEvalData.setCommitPropagationEvals(commitPropagationEvals);
    }

    private void evaluateImHistory(ImHistoryEvaluation imHistoryEval) {
        for (var eval : commitPropagationEvals) {
            imHistoryEval.setNumberAIP(imHistoryEval.getNumberAIP() + eval.getImEvalResult()
                .getNumberAIP());
            imHistoryEval.setNumberActiveAIP(imHistoryEval.getNumberActiveAIP() + eval.getImEvalResult()
                .getNumberActiveAIP());
        }
        imHistoryEval.calculateDerivedValue();
    }

    public void write(Path evalFileDir) {
        Gson gson = new Gson();
        try (BufferedWriter writer = Files.newBufferedWriter(evalFileDir.resolve("commitHistoryEvaluationData.json"))) {
            gson.toJson(historyEvalData, CommitHistoryEvaluationData.class, gson.newJsonWriter(writer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class CommitHistoryEvaluationData {
        private List<EvaluationDataContainer> commitPropagationEvals = new ArrayList<>();
        private ImHistoryEvaluation imUpdateHistoryEval = new ImHistoryEvaluation();

        public ImHistoryEvaluation getImUpdateHistoryEval() {
            return imUpdateHistoryEval;
        }

        public List<EvaluationDataContainer> getCommitPropagationEvals() {
            return commitPropagationEvals;
        }

        public void setCommitPropagationEvals(List<EvaluationDataContainer> commitPropagationEvals) {
            this.commitPropagationEvals = commitPropagationEvals;
        }
    }
}
