package cipm.consistency.base.vsum.domains.java;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public interface IJavaPCMCorrespondenceModel {

	public void clear();

	public void addSeffCorrespondence(String javaMethodId, String seffId);

	public void addLoopCorrespondence(String javaLoopId, String loopId);

	public void addBranchCorrespondence(String javaBranchId, String transitionId);

	public void addInternalActionCorrespondence(String startExprId, String endExprId, String actionId);

	public void addExternalCallCorrespondence(String generateId, String externalCallId);

	public List<Pair<String, String>> getSeffCorrespondences();

	public List<Pair<String, String>> getLoopCorrespondences();

	public List<Pair<String, String>> getBranchCorrespondences();

	public List<Pair<Pair<String, String>, String>> getInternalActionCorrespondences();

	public List<Pair<String, String>> getExternalCallCorrespondences();

	public String getCorrespondingSeffId(String javaMethodId);

	public String getCorrespondingLoopId(String javaLoopId);

	public String getCorrespondingBranchId(String javaBranchId);

	public String getCorrespondingActionId(String startExprId, String endExprId);

	public String getCorrespondingExternalCallId(String exprId);

}
