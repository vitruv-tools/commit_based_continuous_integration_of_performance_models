package cipm.consistency.tools.evaluation.data;

import java.util.ArrayList;
import java.util.List;

public class InstrumentationEvaluationData {
	private int lowerStatementDifferenceCount;
	private int upperStatementDifferenceCount;
	private int statementDifferenceCount;
	private int reloadedStatementDifferenceCount;
	private boolean compiles;
	private int numberChangedMethods;
	private List<String> unmatchedChangedMethods = new ArrayList<>();
	private List<String> unmatchedIPs = new ArrayList<>();
	
	public int getLowerStatementDifferenceCount() {
		return lowerStatementDifferenceCount;
	}
	
	public void setLowerStatementDifferenceCount(int lowerStatementDifferenceCount) {
		this.lowerStatementDifferenceCount = lowerStatementDifferenceCount;
	}
	
	public int getUpperStatementDifferenceCount() {
		return upperStatementDifferenceCount;
	}
	
	public void setUpperStatementDifferenceCount(int upperStatementDifferenceCount) {
		this.upperStatementDifferenceCount = upperStatementDifferenceCount;
	}
	
	public int getStatementDifferenceCount() {
		return statementDifferenceCount;
	}
	
	public void setStatementDifferenceCount(int statementDifferenceCount) {
		this.statementDifferenceCount = statementDifferenceCount;
	}
	
	public int getReloadedStatementDifferenceCount() {
		return reloadedStatementDifferenceCount;
	}
	
	public void setReloadedStatementDifferenceCount(int reloadedStatementDifferenceCount) {
		this.reloadedStatementDifferenceCount = reloadedStatementDifferenceCount;
	}
	
	public boolean isCompiles() {
		return compiles;
	}
	
	public void setCompiles(boolean compiles) {
		this.compiles = compiles;
	}
	
	public int getNumberChangedMethods() {
		return numberChangedMethods;
	}
	
	public void setNumberChangedMethods(int numberChangedMethods) {
		this.numberChangedMethods = numberChangedMethods;
	}
	
	public List<String> getUnmatchedChangedMethods() {
		return unmatchedChangedMethods;
	}
	
	public List<String> getUnmatchedIPs() {
		return unmatchedIPs;
	}
}
