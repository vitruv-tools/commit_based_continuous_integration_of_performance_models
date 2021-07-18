package tools.vitruv.applications.pcmjava.commitintegration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.LogicalJavaURIGenerator;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
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
	private static final String POM_FILE_NAME = "pom.xml";
	private static final String DOCKERFILE_FILE_NAME = "Dockerfile";
	
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
		ParserOptions.RESOLVE_EVERYTHING.setValue(Boolean.TRUE);
		ParserOptions.REGISTER_LOCAL.setValue(Boolean.TRUE);
		JaMoPPJDTSingleFileParser parser = new JaMoPPJDTSingleFileParser();
		parser.setResourceSet(new ResourceSetImpl());
		parser.setExclusionPatterns(".*?/src/test/java/.*?");
		logger.debug("Parsing " + dir.toString());
		ResourceSet resourceSet = parser.parseDirectory(dir);
		logger.debug("Parsed " + resourceSet.getResources().size() + " files.");
		
		// 2. Filter the resources and create modules for components.
		filterResourcesForComponents(resourceSet, dir.toAbsolutePath());
		
		// 3. Create one resource with all Java models.
		logger.debug("Creating one resource with all Java models.");
		ResourceSet next = new ResourceSetImpl();
		Resource all = next.createResource(URI.createFileURI(target.toAbsolutePath().toString()));
		for (Resource r : new ArrayList<>(resourceSet.getResources())) {
			all.getContents().addAll(r.getContents());
		}
		return all;
	}
	
	private static void filterResourcesForComponents(ResourceSet resourceSet, Path dir) {
		HashMap<String, HashSet<Resource>> moduleToCUMap = new HashMap<>();
		for (Resource resource : resourceSet.getResources()) {
			if (resource.getContents().isEmpty()) {
				continue;
			}
			EObject root = resource.getContents().get(0);
			if (root instanceof org.emftext.language.java.containers.Module) {
				resource.getContents().clear();
			} else if (root instanceof org.emftext.language.java.containers.Package) {
				((org.emftext.language.java.containers.Package) root).setModule(null);
			} else if (root instanceof CompilationUnit) {
				if (resource.getURI().isFile()) {
					Path file = Paths.get(resource.getURI().toFileString()).toAbsolutePath();
					String potMod = detectModule(file, dir);
					if (potMod != null) {
						HashSet<Resource> target;
						if (moduleToCUMap.containsKey(potMod)) {
							target = moduleToCUMap.get(potMod);
						} else {
							target = new HashSet<>();
							moduleToCUMap.put(potMod, target);
						}
						target.add(resource);
					}
				}
			}
		}
		moduleToCUMap.entrySet().forEach(entry -> {
			URI uri = LogicalJavaURIGenerator.getModuleURI(entry.getKey());
			Resource targetResource = resourceSet.getResource(uri, false);
			if (targetResource == null) {
				targetResource = resourceSet.createResource(uri);
			}
			org.emftext.language.java.containers.Module mod =
					org.emftext.language.java.containers.ContainersFactory.eINSTANCE.createModule();
			mod.setName(entry.getKey());
			targetResource.getContents().add(mod);
			entry.getValue().stream().map(resource -> resource.getContents().get(0))
				.map(obj -> (CompilationUnit) obj).map(cu -> cu.getChildrenByType(ConcreteClassifier.class))
				.flatMap(cc -> cc.stream()).map(cc -> cc.getPackage()).filter(p -> p != null)
				.forEach(p -> p.setModule(mod));
		});
	}
	
	private static String detectModule(Path file, Path container) {
		Path parent = file.getParent();
		while (container.compareTo(parent) != 0) {
			boolean fileExistence = checkSiblingExistence(parent, POM_FILE_NAME);
//					&& checkSiblingExistence(parent, DOCKERFILE_FILE_NAME);
			if (fileExistence) {
				return parent.getParent().getFileName().toString();
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	private static boolean checkSiblingExistence(Path file, String siblingName) {
		Path sibling = file.resolveSibling(siblingName);
		return Files.exists(sibling);
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
		JavaClasspath.get().getURIMap().entrySet().stream()
			.filter(entry -> entry.getValue() == all.getURI())
			.map(Map.Entry::getKey).collect(Collectors.toList())
			.forEach(u -> JavaClasspath.get().getURIMap().remove(u));
	}
}
