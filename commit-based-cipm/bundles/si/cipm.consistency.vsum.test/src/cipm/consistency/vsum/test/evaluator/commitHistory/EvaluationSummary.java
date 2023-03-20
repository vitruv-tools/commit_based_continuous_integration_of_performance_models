package cipm.consistency.vsum.test.evaluator.commitHistory;

public class EvaluationSummary {

    private double worstCodeUpdateEvalJaccardCoefficient = 1;
    private double worstPcmUpdateEvalJaccardCoefficient = 1;
    private double worstImUpdateEvalFScoreAIP = 1;
    private double worstImUpdateEvalFScoreActiveAIP = 1;

    public double getWorstCodeUpdateEvalJaccardCoefficient() {
        return worstCodeUpdateEvalJaccardCoefficient;
    }

    public void addCodeUpdateEvalJaccardCoefficient(double worstCodeUpdateEvalJaccardCoefficient) {
        if (worstCodeUpdateEvalJaccardCoefficient < this.worstCodeUpdateEvalJaccardCoefficient) {
            this.worstCodeUpdateEvalJaccardCoefficient = worstCodeUpdateEvalJaccardCoefficient;
        }
    }

    public double getWorstPcmUpdateEvalJaccardCoefficient() {
        return worstPcmUpdateEvalJaccardCoefficient;
    }

    public void addWorstPcmUpdateEvalJaccardCoefficient(double worstPcmUpdateEvalJaccardCoefficient) {
        if (worstPcmUpdateEvalJaccardCoefficient < this.worstPcmUpdateEvalJaccardCoefficient) {
            this.worstPcmUpdateEvalJaccardCoefficient = worstPcmUpdateEvalJaccardCoefficient;
        }
    }

    public double getWorstImUpdateEvalFScore() {
        return worstImUpdateEvalFScoreAIP;
    }

    public void setWorstImUpdateEvalFScoreAIP(double worstImUpdateEvalFScore) {
        if (worstImUpdateEvalFScore < this.worstImUpdateEvalFScoreAIP) {
            this.worstImUpdateEvalFScoreAIP = worstImUpdateEvalFScore;
        }
    }

    public double getWorstImUpdateEvalFScoreActiveAIP() {
        return worstImUpdateEvalFScoreActiveAIP;
    }

    public void setWorstImUpdateEvalFScoreActiveAIP(double worstImUpdateEvalFScoreActiveAIP) {
        if (worstImUpdateEvalFScoreActiveAIP < this.worstImUpdateEvalFScoreActiveAIP
                && worstImUpdateEvalFScoreActiveAIP >= 0) {
            this.worstImUpdateEvalFScoreActiveAIP = worstImUpdateEvalFScoreActiveAIP;
        }
    }
}
