package tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring;

public class ExtractMethodResume {

	private String methodDefinition;
	private String methodCall;
	
	public String getMethodDefinition() {
		return methodDefinition;
	}
	public void setMethodDefinition(String methodDefinition) {
		this.methodDefinition = methodDefinition;
	}
	public String getMethodCall() {
		return methodCall;
	}
	public void setMethodCall(String methodCall) {
		this.methodCall = methodCall;
	}
	
	public ExtractMethodResume(String methodDefinition, String methodCall) {
		super();
		this.methodDefinition = methodDefinition;
		this.methodCall = methodCall;
	}
	public ExtractMethodResume() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
