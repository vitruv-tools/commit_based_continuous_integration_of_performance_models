package cipm.consistency.tools.evaluation.data;

import java.util.ArrayList;
import java.util.List;

public class IMEvaluationData {
	private int numberMatchedIP;
	private int numberAllIP;
	private int numberSIP;
	private int numberAIP;
	private int numberDeactivatedAIP;
	private int numberActivatedAIP;
	private double deactivatedIPAllIPRatio;
	private double deactivatedAIPAllAIPRatio;
	private List<String> unmatchedSEFFElements = new ArrayList<>();
	
	public int getNumberMatchedIP() {
		return numberMatchedIP;
	}
	
	public void setNumberMatchedIP(int numberMatchedIP) {
		this.numberMatchedIP = numberMatchedIP;
	}
	
	public int getNumberAllIP() {
		return numberAllIP;
	}
	
	public void setNumberAllIP(int numberAllIP) {
		this.numberAllIP = numberAllIP;
	}
	
	public int getNumberSIP() {
		return numberSIP;
	}
	
	public void setNumberSIP(int numberSIP) {
		this.numberSIP = numberSIP;
	}
	
	public int getNumberAIP() {
		return numberAIP;
	}
	
	public void setNumberAIP(int numberAIP) {
		this.numberAIP = numberAIP;
	}
	
	public int getNumberDeactivatedAIP() {
		return numberDeactivatedAIP;
	}
	
	public void setNumberDeactivatedAIP(int numberDeactivatedAIP) {
		this.numberDeactivatedAIP = numberDeactivatedAIP;
	}
	
	public int getNumberActivatedAIP() {
		return numberActivatedAIP;
	}
	
	public void setNumberActivatedAIP(int numberActivatedAIP) {
		this.numberActivatedAIP = numberActivatedAIP;
	}
	
	public double getDeactivatedIPAllIPRatio() {
		return deactivatedIPAllIPRatio;
	}
	
	public void setDeactivatedIPAllIPRatio(double deactivatedIPAllIPRatio) {
		if (deactivatedIPAllIPRatio == Double.NaN) {
			this.deactivatedAIPAllAIPRatio = -1;
		} else {
			this.deactivatedIPAllIPRatio = deactivatedIPAllIPRatio;
		}
	}
	
	public double getDeactivatedAIPAllAIPRatio() {
		return deactivatedAIPAllAIPRatio;
	}
	
	public void setDeactivatedAIPAllAIPRatio(double deactivatedAIPAllAIPRatio) {
		if (deactivatedAIPAllAIPRatio == Double.NaN) {
			this.deactivatedAIPAllAIPRatio = -1;
		} else {
			this.deactivatedAIPAllAIPRatio = deactivatedAIPAllAIPRatio;
		}
	}
	
	public List<String> getUnmatchedSEFFElements() {
		return unmatchedSEFFElements;
	}
}
