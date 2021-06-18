package tools.vitruv.applications.pcmjava.commitintegration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.instantiations.NewConstructorCall;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.types.Type;
import org.emftext.language.java.types.TypeReference;

import jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser;
import tools.vitruv.applications.pcmjava.commitintegration.propagation.JavaStateBasedChangeResolutionStrategy;
import tools.vitruv.framework.change.description.VitruviusChange;
import tools.vitruv.framework.vsum.VirtualModel;

/**
 * A utility class for the initial integration of Java code into Vitruvius.
 * 
 * @author Martin Armbruster
 */
public final class JavaIntegrationUtility {
	private JavaIntegrationUtility() {
	}
	
	/**
	 * Performs an initial integration of Java code into Vitruvius.
	 * 
	 * @param dir the directory with the Java code.
	 * @param vsum the VSUM.
	 */
	public static void integrateJavaCode(Path dir, VirtualModel vsum) {
		// 1. Parse the code and resolve the references.
		JaMoPPJDTSingleFileParser parser = new JaMoPPJDTSingleFileParser();
		parser.setResourceSet(new ResourceSetImpl());
		parser.setExclusionPatterns(".*?/src/test/java/.*?");
		ResourceSet resourceSet = parser.parseDirectory(dir);
		ArrayList<Resource> parsedResources = new ArrayList<>(resourceSet.getResources());
		for (Resource res : parsedResources) {
			EcoreUtil.resolveAll(res);
		}
		
		// 2. All Resources which have dependencies to other Resources that are already integrated
		//    are integrated. In the first iteration, all Resources which have no dependencies are
		//    integrated.
		HashSet<String> foundClassifiers = new HashSet<>();
		int oldSize;
		do {
			oldSize = parsedResources.size();
			for (int index = 0; index < parsedResources.size(); index++) {
				Resource current = parsedResources.get(index);
				if(containsResolvedDependencies(parsedResources.get(index), foundClassifiers)) {
					CompilationUnit cu = (CompilationUnit) current.getContents().get(0);
					for (ConcreteClassifier classifier : cu.getClassifiers()) {
						foundClassifiers.add(classifier.getQualifiedName());
					}
					vsum.propagateChangedState(current);
					parsedResources.remove(index);
					index--;
				}
			}
		} while (parsedResources.size() > 0 && oldSize != parsedResources.size());
		
		// 3. If Resources are left, they contain a circular dependency. Therefore, all remaining Resources
		//    are integrated at once by calculating a change sequence for them and propagating these changes.
		if (parsedResources.size() > 0) {
			VitruviusChange change = new JavaStateBasedChangeResolutionStrategy().getChangeSequenceForResourceSet(resourceSet, parsedResources);
			vsum.propagateChange(change);
		}
	}
	
	private static boolean containsResolvedDependencies(Resource a, HashSet<String> resolved) {
		if (a.getContents().get(0) instanceof CompilationUnit) {
			CompilationUnit cu = (CompilationUnit) a.getContents().get(0);
			for (var iter = a.getAllContents(); iter.hasNext();) {
				EObject next = iter.next();
				ConcreteClassifier cc = null;
				if (next instanceof ConcreteClassifier) {
					cc = (ConcreteClassifier) next;
				} else if (next instanceof TypeReference) {
					TypeReference tr = (TypeReference) next;
					Type target = tr.getTarget();
					if (target instanceof ConcreteClassifier) {
						cc = (ConcreteClassifier) target;
					}
				} else if (next instanceof Import) {
					cc = ((Import) next).getClassifier();
				} else if (next instanceof IdentifierReference) {
					IdentifierReference idref = (IdentifierReference) next;
					if (idref.getTarget() instanceof ConcreteClassifier) {
						cc = (ConcreteClassifier) idref.getTarget();
					}
				} else if (next instanceof NewConstructorCall) {
					NewConstructorCall call = (NewConstructorCall) next;
					TypeReference ref = call.getTypeReference();
					Type target = ref.getTarget();
					if (target instanceof ConcreteClassifier) {
						cc = (ConcreteClassifier) target;
					}
				}
				if (cc != null) {
					String cn = cc.getQualifiedName();
					// The found classifier is from another compilation unit which 
					if (cc.getContainingCompilationUnit() != cu
							&& cc.eResource().getURI().isFile()
							&& !resolved.contains(cn)) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
