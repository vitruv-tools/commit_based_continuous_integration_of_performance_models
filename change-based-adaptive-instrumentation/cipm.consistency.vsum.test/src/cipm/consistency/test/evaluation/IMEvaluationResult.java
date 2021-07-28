package cipm.consistency.test.evaluation;

import java.util.List;

public class IMEvaluationResult {
	public int numberMatchedIP;
	public int numberAllIP;
	public int numberSIP;
	public int numberAIP;
	public int numberDeactivatedAIP;
	public int numberActivatedAIP;
	public double deactivatedIPAllIPRatio;
	public double deactivatedAIPAllAIPRatio;
	public List<String> unmatchedSEFFElements;
}
