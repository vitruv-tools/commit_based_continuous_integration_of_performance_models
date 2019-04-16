package tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring;

import java.util.List;
import java.util.Map;

public class ExtractMethodDescriptor {

	private ExtractMethodType type;
	private List<VariableDescriptor> in_variables;
	private List<VariableDescriptor> out_top_variables;
	private List<VariableDescriptor> out_buttom_variables;
	private String body;
	private String methodName;
	
	
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public ExtractMethodType getType() {
		return type;
	}
	public void setType(ExtractMethodType type) {
		this.type = type;
	}
	public List<VariableDescriptor> getIn_variables() {
		return in_variables;
	}
	public void setIn_variables(List<VariableDescriptor> in_variables) {
		this.in_variables = in_variables;
	}
	public List<VariableDescriptor> getOut_top_variables() {
		return out_top_variables;
	}
	public void setOut_top_variables(List<VariableDescriptor> out_top_variables) {
		this.out_top_variables = out_top_variables;
	}
	public List<VariableDescriptor> getOut_buttom_variables() {
		return out_buttom_variables;
	}
	public void setOut_buttom_variables(List<VariableDescriptor> out_buttom_variables) {
		this.out_buttom_variables = out_buttom_variables;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	
	public ExtractMethodDescriptor(ExtractMethodType type, List<VariableDescriptor> in_variables,
			List<VariableDescriptor> out_top_variables, List<VariableDescriptor> out_buttom_variables, String body) {
		super();
		this.type = type;
		this.in_variables = in_variables;
		this.out_top_variables = out_top_variables;
		this.out_buttom_variables = out_buttom_variables;
		this.body = body;
	}
	public ExtractMethodDescriptor() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
