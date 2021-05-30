package tools.vitruv.applications.pcmjava.commitintegration.propagation;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.emftext.language.java.members.Method;

/**
 * An adapter that holds the old method of a method that has changed.
 * 
 * @author Martin Armbruster
 */
public class OldMethodAdapter extends AdapterImpl {
	private Method oldMethod;
	
	public OldMethodAdapter(Method oldM) {
		oldMethod = oldM;
	}
	
	public Method getOldMethod() {
		return oldMethod;
	}
}
