package tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethod {

	public ExtractMethodResume extractMethod(ExtractMethodDescriptor extractMethodDescriptor){
		
		if(extractMethodDescriptor != null){
			
			ExtractMethodResume extractMethodResume = new ExtractMethodResume();
			
			extractMethodResume.setMethodDefinition(this.getExtractMethodDefinition(extractMethodDescriptor));
			extractMethodResume.setMethodCall(this.getExtractMethodCall(extractMethodDescriptor));
			
			return extractMethodResume;
		}
		else{
			return null;
		}
	}
	
	private String getExtractMethodDefinition(ExtractMethodDescriptor extractMethodDescriptor){
		StringBuilder methodDefinition = new StringBuilder();
		
		List<VariableDescriptor> in_variables = extractMethodDescriptor.getIn_variables();
		List<VariableDescriptor> out_top_variables = extractMethodDescriptor.getOut_top_variables();
		List<VariableDescriptor> out_buttom_variables = extractMethodDescriptor.getOut_buttom_variables();
		
		
		// the intersection of both sets out_top_variables and in_variables gives the method params
		List<VariableDescriptor> methodParams = this.getVariableListIntersection(out_top_variables, in_variables);
		
		// the intersection of both sets out_buttom_variables and in_variables gives the method output
		List<VariableDescriptor> methodOutputs = this.getVariableListIntersection(in_variables, out_buttom_variables);
		
		
		String methodSignatue = this.createExtractMethodSignature(extractMethodDescriptor, methodParams, methodOutputs);
		String mapMethodOutputs = this.mapMethodOutputs(methodOutputs);
		
		methodDefinition.append(methodSignatue);
		methodDefinition.append("{");
		methodDefinition.append(System.lineSeparator());
		
		methodDefinition.append(extractMethodDescriptor.getBody());
		methodDefinition.append(System.lineSeparator());
		
		methodDefinition.append(mapMethodOutputs);
		methodDefinition.append(System.lineSeparator());
		
		methodDefinition.append("}");
		
		
		return methodDefinition.toString();
	}
	
	private String getExtractMethodCall(ExtractMethodDescriptor extractMethodDescriptor){
		StringBuilder str = new StringBuilder();
		
		List<VariableDescriptor> in_variables = extractMethodDescriptor.getIn_variables();
		List<VariableDescriptor> out_top_variables = extractMethodDescriptor.getOut_top_variables();
		List<VariableDescriptor> out_buttom_variables = extractMethodDescriptor.getOut_buttom_variables();
		
		
		// the intersection of both sets out_top_variables and in_variables gives the method params
		List<VariableDescriptor> methodParams = this.getVariableListIntersection(out_top_variables, in_variables);
		
		// the intersection of both sets out_buttom_variables and in_variables gives the method output
		List<VariableDescriptor> methodOutputs = this.getVariableListIntersection(in_variables, out_buttom_variables);
		
		
		if(methodOutputs.size() == 0){
			str.append(extractMethodDescriptor.getMethodName());
		}
		else{
			str.append("java.util.Map<String, Object> mapOutputs " + extractMethodDescriptor.getMethodName());
		}
		
		if(methodParams.size() == 0){
			str.append("();");
			str.append(System.lineSeparator());
		}
		else{
			str.append("(");
			for(VariableDescriptor param: methodParams){
				str.append(param.getName());
				if(methodParams.indexOf(param) != methodParams.size() - 1){
					str.append(", ");
				}
			}
			str.append(");");
			str.append(System.lineSeparator());
		}
		
		// access to params
		if(methodOutputs.size() != 0){
			for(VariableDescriptor output: methodOutputs){
				if(BlockRefactoringUtil.isPrimitiveTypeFromString(output.getTypeName())){
					str.append(output.getTypeName() + " " + output.getName() + " = mapOutputs.get('" + output.getName() +"')");
				}
			}
		}
		
		return str.toString();
	}
	
	
	private String mapMethodOutputs(List<VariableDescriptor> methodOutputs){
		StringBuilder mapMethodOutput = new StringBuilder();
		
		if(methodOutputs.size() != 0){
			mapMethodOutput.append("java.util.Map<String, Object> mapMethodOuputs =  new java.util.HashMap<String, Object>()");
			mapMethodOutput.append(System.lineSeparator());
			
			for(VariableDescriptor varDesc: methodOutputs){
				mapMethodOutput.append("mapMethodOuputs.put('" + varDesc.getName() + "', " + varDesc.getName() + ")");
				mapMethodOutput.append(System.lineSeparator());
			}
		}
		
		return mapMethodOutput.toString();
	}
	
	private String createExtractMethodSignature(ExtractMethodDescriptor extractMethodDescriptor,
			List<VariableDescriptor> methodParams,
			List<VariableDescriptor> methodOutput){	
		// accessibility of the refactored method has to be defined !
		String methodSignature = "private";
		String uuid =  BlockRefactoringUtil.getUUID();
		String methodName = extractMethodDescriptor.getType().toString() + "_" + uuid;	
		
		extractMethodDescriptor.setMethodName(methodName);
		
		if(methodOutput.size() == 0){
			methodSignature += " void";
		}
		else{
			methodSignature += " Map<String, Object>";
		}
		
		//add the method name
		methodSignature += " " + methodName;
		
		if(methodParams.size() == 0){
			methodSignature += "()";
		}else{
			methodSignature += "(";		
			for(VariableDescriptor params: methodParams){
				methodSignature += params.getTypeName() + " " + params.getName();
				if(methodParams.indexOf(params) != methodParams.size()-1){
					methodSignature += ", ";
				}
				
			}	
			methodSignature += ")";
		}
				
		return methodSignature;
	}
	
	
	private List<VariableDescriptor> getVariableListIntersection(List<VariableDescriptor> list1, List<VariableDescriptor> list2){
		List<VariableDescriptor> listVariableIntersection = new ArrayList<VariableDescriptor>();
		
		for(VariableDescriptor varDesc1: list1){
			for(VariableDescriptor varDesc2: list2){
				if(varDesc1.getName().equals(varDesc2.getName())){
					listVariableIntersection.add(varDesc2);
				}
			}
		}
	
		return listVariableIntersection;
	}
}
