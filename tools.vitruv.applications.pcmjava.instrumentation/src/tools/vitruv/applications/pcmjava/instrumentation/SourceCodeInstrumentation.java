package tools.vitruv.applications.pcmjava.instrumentation;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;

public interface SourceCodeInstrumentation {
	public void execute()throws IOException, JavaModelException, CoreException;
	public void execute(String iterationNumber)throws IOException, JavaModelException, CoreException;
}
