package tools.vitruv.applications.pcmjava.modelrefinement.refactoring.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring.ExtractMethod;
import tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring.ExtractMethodDescriptor;
import tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring.ExtractMethodResume;
import tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring.ExtractMethodType;
import tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring.VariableDescriptor;

public class Test {
	
	
	public static void main(String[] args) throws IOException {
		
		/*   create ExtractMethodDescriptor for testing */
		ExtractMethodDescriptor internalActionExtractMethodDescriptor = createIternalActrionExtractMethodDescriptor();
		
		/**  */
	
		ExtractMethod test  = new ExtractMethod();
		
		ExtractMethodResume extractMethodResume = test.extractMethod(internalActionExtractMethodDescriptor);
		if(extractMethodResume != null){
			System.out.println("----------Method definition------------ \n\n" + extractMethodResume.getMethodDefinition());
			System.out.println("----------Method call-------------- \n\n" + extractMethodResume.getMethodCall());
		}
		else{
			System.out.println("Extract Method can not be resumed!");
		}
		
	}
	
	
	private static ExtractMethodDescriptor createIternalActrionExtractMethodDescriptor(){
		ExtractMethodDescriptor extractMethodDescriptor = new ExtractMethodDescriptor();
		
		List<VariableDescriptor> in_variables = new ArrayList<VariableDescriptor>();
		List<VariableDescriptor> out_top_variables = new ArrayList<VariableDescriptor>();
		List<VariableDescriptor> out_buttom_variables = new ArrayList<VariableDescriptor>();
		
		// in_variables
		in_variables.add(new VariableDescriptor("String", "testString", 1));
		in_variables.add(new VariableDescriptor("int", "testInt", 2));
		
		// out_top_variables
		out_top_variables.add(new VariableDescriptor("String", "testString", 1));
		
		// out_buttom_variables
		out_buttom_variables.add(new VariableDescriptor("int", "testInt", 2));
		
		
		String methodBody = "/* method body ...... */";
		
		extractMethodDescriptor.setType(ExtractMethodType.INTERNAL_ACTION);
		extractMethodDescriptor.setBody(methodBody);
		extractMethodDescriptor.setIn_variables(in_variables);
		extractMethodDescriptor.setOut_buttom_variables(out_buttom_variables);
		extractMethodDescriptor.setOut_top_variables(out_top_variables);
		
		return extractMethodDescriptor;
	}
	
	

	public String createClassAndMethod(String codeFragement){
		int i = 0;
		double j = 0.00;
		String code = "";
		
		double k = i + j;
	
		return code;	
	}
}

