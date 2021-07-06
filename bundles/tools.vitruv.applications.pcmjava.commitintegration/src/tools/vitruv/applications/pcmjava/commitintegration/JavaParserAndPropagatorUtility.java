package tools.vitruv.applications.pcmjava.commitintegration;

import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.containers.JavaRoot;

import jamopp.options.ParserOptions;
import jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser;
import tools.vitruv.framework.vsum.VirtualModel;

/**
 * A utility class for the integration and change propagation of Java code into Vitruvius.
 * 
 * @author Martin Armbruster
 */
public final class JavaParserAndPropagatorUtility {
	private static final Logger logger = Logger.getLogger(JavaParserAndPropagatorUtility.class.getSimpleName());
	
	private JavaParserAndPropagatorUtility() {
	}
	
	/**
	 * Parses all Java code and creates one Resource with all models.
	 * 
	 * @param dir directory in which the Java code resides.
	 * @param target target file of the Resource with all models.
	 * @return the Resource with all models.
	 */
	public static Resource parseJavaCodeIntoOneModel(Path dir, Path target) {
		// 1. Parse the code.
		ParserOptions.CREATE_LAYOUT_INFORMATION.setValue(Boolean.FALSE);
		JaMoPPJDTSingleFileParser parser = new JaMoPPJDTSingleFileParser();
		parser.setResourceSet(new ResourceSetImpl());
		parser.setExclusionPatterns(".*?/src/test/java/.*?");
		logger.debug("Parsing " + dir.toString());
		ResourceSet resourceSet = parser.parseDirectory(dir);
		logger.debug("Parsed " + resourceSet.getResources().size() + " files.");
				
		// 2. Resolve all references.
		logger.debug("Resolving all references.");
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
		logger.debug("Creating one resource with all Java models.");
		ResourceSet next = new ResourceSetImpl();
		Resource all = next.createResource(URI.createFileURI(target.toAbsolutePath().toString()));
		for (Resource r : new ArrayList<>(resourceSet.getResources())) {
			all.getContents().addAll(r.getContents());
		}
		return all;
	}
	
	/**
	 * Performs an integration or change propagation of Java code into Vitruvius.
	 * 
	 * @param dir the directory with the Java code.
	 * @param target destination in which the complete Java model will be stored.
	 * @param vsum the VSUM.
	 */
	public static void parseAndPropagateJavaCode(Path dir, Path target, VirtualModel vsum) {
		// 1. Parse the Java code and create one Resource with all models.
		Resource all = parseJavaCodeIntoOneModel(dir, target);
		all.getContents().forEach(content ->
			JavaClasspath.get().registerJavaRoot((JavaRoot) content, all.getURI()));
		
		// 2. Propagate the Java models.
		logger.debug("Propagating the Java models.");
		vsum.propagateChangedState(all);
	}
}
