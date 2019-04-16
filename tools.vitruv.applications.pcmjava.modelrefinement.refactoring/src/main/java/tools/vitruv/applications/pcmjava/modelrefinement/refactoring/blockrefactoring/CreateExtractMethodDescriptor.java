package tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

public class CreateExtractMethodDescriptor {
	
	private int startPosition;
	private int endPosition;
	

	public ExtractMethodDescriptor createExtractMethodDescriptor(MethodDeclaration methodDeclaration,
			int startPosition, int endPosition){
		
		// !!!  this can guide to race condition if the object is use parallel programming
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		
		return null;
	}
	
	
	class VariableFinderVisitor extends ASTVisitor{

		public boolean visit(SimpleName simpleName){
			
			
			if (simpleName.resolveBinding() instanceof IVariableBinding){
			    // simpleName is a variable identifier
				IVariableBinding binding = (IVariableBinding) simpleName.resolveBinding();
				
				System.out.println("------------------");
				System.out.println("binding: " + binding.getType().getName());

			}
					
			return true;
		}
	}
	
		
}


