package tools.vitruv.applications.pcmjava.commitintegration;

import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import jamopp.options.ParserOptions;
import jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser;
import tools.vitruv.framework.vsum.VirtualModel;

/**
 * A utility class for the integration and change propagation of Java code into Vitruvius.
 * 
 * @author Martin Armbruster
 */
public final class JavaParserAndPropagatorUtility {
	private JavaParserAndPropagatorUtility() {
	}
	
	/**
	 * Performs an integration or change propagation of Java code into Vitruvius.
	 * 
	 * @param dir the directory with the Java code.
	 * @param target destination in which the complete Java model will be stored.
	 * @param vsum the VSUM.
	 */
	public static void parseAndPropagateJavaCode(Path dir, Path target, VirtualModel vsum) {
		// 1. Parse the code.
		ParserOptions.CREATE_LAYOUT_INFORMATION.setValue(Boolean.FALSE);
		JaMoPPJDTSingleFileParser parser = new JaMoPPJDTSingleFileParser();
		parser.setResourceSet(new ResourceSetImpl());
		parser.setExclusionPatterns(".*?/src/test/java/.*?");
		ResourceSet resourceSet = parser.parseDirectory(dir);
		
		// 2. Resolve all references.
		int oldSize;
		do {
			oldSize = resourceSet.getResources().size();
			for (Resource res : new ArrayList<>(resourceSet.getResources())) {
				if (res.getContents().isEmpty()) {
					continue;
				}
				EcoreUtil.resolveAll(res);
			}
		} while (oldSize != resourceSet.getResources().size());
		
		// 3. Create one resource with all Java models.
		ResourceSet next = new ResourceSetImpl();
		Resource all = next.createResource(URI.createFileURI(target.toAbsolutePath().toString()));
		for (Resource r : new ArrayList<>(resourceSet.getResources())) {
			all.getContents().addAll(r.getContents());
		}
		
		// 4. Propagate the Java models.
		vsum.propagateChangedState(all);
	}
}
