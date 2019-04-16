package tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.jamoppplaincodeparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.members.MembersPackage;
import org.emftext.language.java.resource.JaMoPPUtil;
import org.emftext.language.java.resource.java.IJavaOptions;
import org.emftext.language.java.statements.Statement;

public class JamoppPlainCodeParser {
	
	public static void addAfterContainingStatement(Statement statement, String code) throws IOException {
		List<Statement> statements = getCodeStatements(code);
		if(statements != null) {

			for(Statement statementToAdd: statements) {
				statement.addAfterContainingStatement(statementToAdd);
			}
		}
	}
	
	
	public static void addBeforeContainingStatement(Statement statement, String code) throws IOException {
		List<Statement> statements = getCodeStatements(code);
		if(statements != null) {

			for(Statement statementToAdd: statements) {
				statement.addBeforeContainingStatement(statementToAdd);
			}
		}
	}
	
	
	public static List<Statement> getCodeStatements(String code) throws IOException{
		if(code == null) {
			return null;
		}
		else {
			code = getExtendedCode(code);
		}
			
		List<Statement> statements = new ArrayList<Statement>();
		
		JaMoPPUtil.initialize();
		URI uri = URI.createURI("temp.java");
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(uri);
		
		byte[] byteCode = code.getBytes();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(byteCode);
		
		Map<?, ?> typeOption = Collections.singletonMap(IJavaOptions.RESOURCE_CONTENT_TYPE, 
				MembersPackage.Literals.CLASS_METHOD);
		resource.load(inputStream, typeOption );
					
		if(resource.getContents() == null) {
			return null;
		}
		else {
			ClassMethod classMethod = (ClassMethod)resource.getContents().get(0);
			for(Statement statement: classMethod.getStatements()) {
				statements.add(statement);
			}
		}			
		
		return statements;    
    }
	
	
	private static String getExtendedCode(String code) {
		String extendedCode = "public class{ public void sitter(){" + code + "}}";
        return extendedCode;
	}
}
