package tools.vitruv.applications.pcmjava.modelrefinement.coderefactoring;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

public class RefactorCode {
	/**
	 * format all the java resource of a given Java eclipse Project
	 * @param iJavaProject:  java eclipse project
	 * @throws JavaModelException
	 */
	public static void formatProjectSourceCode(IJavaProject iJavaProject) throws JavaModelException {
		if(iJavaProject != null) {
			IPackageFragment[] packageFragments = iJavaProject.getPackageFragments();
			for(IPackageFragment packageFragment: packageFragments) {
				for(ICompilationUnit iCompilationUnit: packageFragment.getCompilationUnits()) {
					if(iCompilationUnit.getResource().getType() == IResource.FILE) {
						String iCompulationUnitPath = iCompilationUnit.getPath().toString();
						if(iCompulationUnitPath.endsWith(".java")) {
							// format the source code
							formatUnitCompilationSourceCode(iCompilationUnit, new NullProgressMonitor());	
						}
					}
				}
				
			}
		}
	}
	
	
	/**
	 * format the code of a compilation unit based on JDT
	 * 
	 * @param iCompilationUnit: compilation unit
	 * @param monitor
	 * @throws JavaModelException
	 */
	public static void formatUnitCompilationSourceCode(ICompilationUnit iCompilationUnit,
			IProgressMonitor monitor) throws JavaModelException {
		CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
		ISourceRange range = iCompilationUnit.getSourceRange();
		TextEdit formatEdit = formatter.format(
				CodeFormatter.K_COMPILATION_UNIT, iCompilationUnit.getSource(),
				range.getOffset(), range.getLength(), 0, null);
		
		// edit compilation unit
		editCompilationUnit(iCompilationUnit, monitor, formatEdit);		
	}
	

	private static void editCompilationUnit(ICompilationUnit iCompilationUnit, IProgressMonitor monitor, TextEdit... textEdits) throws JavaModelException {
		iCompilationUnit.becomeWorkingCopy(new NullProgressMonitor()); 
        for(TextEdit textEdit: textEdits) {
        	iCompilationUnit.applyTextEdit(textEdit, null);
        }
        iCompilationUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
        iCompilationUnit.commitWorkingCopy(false, monitor);
        iCompilationUnit.discardWorkingCopy();
	}
	
	
	/**
	 * insert try finally clause to all the methods in the compilation unit except the constructor 
	 * because its not needed
	 * @param iCompilationUnit: Compilation unit
	 * @param monitor
	 * @throws JavaModelException
	 */
	public static void addTryFinalyClauseToMethodOfCompilationUnit(ICompilationUnit iCompilationUnit, IProgressMonitor monitor) throws JavaModelException {
		for(IJavaElement iJavaElement: iCompilationUnit.getChildren()) {
			if(iJavaElement instanceof IType) {
				IType iType = (IType) iJavaElement;
				for(IMethod iMethod: iType.getMethods()) {
					if(!iMethod.isConstructor()) {
						// add try finally clause to the method
						insertTryFinallyClauseToMethod(iMethod, iCompilationUnit, monitor);
					}	
				}
			}
		}
	}
	
	
	
	/**
	 * insert try finally clause in method, this needed for services instrumentation
	 * @param iMethod: the in which we want to insert try finally clause
	 * @param iCompilationUnit: CompilationUnit, concrete classifier
	 * @param monitor
	 * @throws JavaModelException
	 */
	private static void insertTryFinallyClauseToMethod(IMethod iMethod, ICompilationUnit iCompilationUnit, 
			IProgressMonitor monitor) throws JavaModelException {
		 int methodDeclarationLength = 0;
		 final String[] splitedMethodSource = iMethod.getSource().split("\\{");
		 if(splitedMethodSource.length == 0) {
			 return;
		 }
		 else {
			 methodDeclarationLength = splitedMethodSource[0].length();
		 }
		 
		 final String tryCode = "try{";
		 final String finallyCode = "\n}finally {}";
		 
		 int iMethodOffset = iMethod.getSourceRange().getOffset();
		 
		 // try clause
		 int offsetForTryClause = iMethodOffset + methodDeclarationLength + 1;
	     final InsertEdit insertTryClauseCode = new InsertEdit(offsetForTryClause, tryCode);
		 
		 // finally clause
	     int offsetFinallyClause = iMethodOffset + iMethod.getSource().length() - 1;
		 final InsertEdit insertFinallyClauseCode = new InsertEdit(offsetFinallyClause, finallyCode);
		 
		 // insert try finally clause in the method
		 editCompilationUnit(iCompilationUnit, monitor, insertTryClauseCode, insertFinallyClauseCode);
	}
	
}
