package tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;

public class EclipseJDTAST {

	public static void main(String[] args) throws IOException {
		String path ="C:/Users/nd/Desktop/SOMOX/blockrefactoring/src/main/java/com/sdq/kit/blockrefactoring/Test.java";
		String str = getCode(path);

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		parser.setBindingsRecovery(true);

		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);

		String unitName = "Test.java";
		parser.setUnitName(unitName);

		String[] sources = { "C:/Users/nd/Desktop/SOMOX/blockrefactoring/src" }; 
		String[] classpath = {"C:\\Program Files\\Java\\jre1.8.0_25\\lib\\rt.jar"};

		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(str.toCharArray());

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		if (cu.getAST().hasBindingsRecovery()) {
			System.out.println("Binding activated.");
		}
		

		MehtodFinderVisitor v = new MehtodFinderVisitor();
		cu.accept(v);		
	}
	
	  public static String getCode(String filePath) throws IOException{
	    	BufferedReader br = new BufferedReader(new FileReader(filePath));
	    	
	    	StringBuilder str = new StringBuilder();
	    	String line = br.readLine();
	    	while(line != null){
	    		str.append(line);
	    		str.append(System.lineSeparator());
	    		line = br.readLine();
	    	}
	    	
	    	return str.toString();
	    }
	  
}


class VariableFinderVisitor extends ASTVisitor{
    
	public boolean visit(SimpleName simpleName){
		
			
		if (simpleName.resolveBinding() instanceof IVariableBinding){
		    // simpleName is a variable identifier
			IVariableBinding binding = (IVariableBinding) simpleName.resolveBinding();
			
			System.out.println("------------------ " );
			
			String typeKey = binding.getType().getName();
			
			System.out.println("binding: " + binding.getName());

		}
				
		return true;
	}
}


class MehtodFinderVisitor extends ASTVisitor{

	public boolean visit(MethodDeclaration node){
		
		NodeFinder nodeFinder = new NodeFinder(node, 9, 57);
		ASTNode selectNodes = nodeFinder.getCoveredNode();
		
		
		if(selectNodes == null){
			System.out.println("+++++++++++++++++++++Selected Node is null");
		}
				
		VariableFinderVisitor v =  new VariableFinderVisitor();
		node.accept(v);
		
		return true;
	}
}



