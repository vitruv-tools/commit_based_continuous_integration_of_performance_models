package tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.Condition;
import org.emftext.language.java.statements.ForLoop;
import org.emftext.language.java.statements.Statement;

public class FindStatementChilds {
	private Set<Statement> listSimpleStatements;

	public FindStatementChilds() {
		this.listSimpleStatements =  new HashSet<Statement>();
	}
	
	
	public Set<Statement> findStatementChilds(ClassMethod classMethod){
		for(Statement statement: classMethod.getStatements()) {
			this.findStatementChilds(statement);
		}
		return this.listSimpleStatements;
	}
	
	
	private void findStatementChilds(Statement statement){
		this.listSimpleStatements.add(statement);
		List<Statement> statementChilds = statement.getChildrenByType(Statement.class);
		for(Statement statementChild: statementChilds) {
			this.listSimpleStatements.add(statementChild);
			if(statementChild instanceof Condition || statementChild instanceof Block || statementChild instanceof ForLoop) {
				this.findStatementChilds(statementChild);
			}
		}
	}
	
	
}
