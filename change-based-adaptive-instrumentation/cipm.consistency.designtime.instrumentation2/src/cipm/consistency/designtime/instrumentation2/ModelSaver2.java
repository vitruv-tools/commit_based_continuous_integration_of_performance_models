package cipm.consistency.designtime.instrumentation2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emftext.language.java.LogicalJavaURIGenerator;
import org.emftext.language.java.commons.NamespaceAwareElement;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;

/**
 * Saves the instrumented model by merging it with the original sources.
 * 
 * @author Martin Armbruster
 */
public class ModelSaver2 {
	void saveModels(ResourceSet copyContainer, Resource copiedResource, Path target, Path source) {
		try {
			FileUtils.copyDirectory(source.toFile(), target.toFile());
			var origJavaFiles = Files.walk(target).filter(Files::isRegularFile).map(Path::toAbsolutePath)
				.filter(p -> p.getFileName().toString().endsWith(LogicalJavaURIGenerator.JAVA_FILE_EXTENSION))
				.collect(Collectors.toList());
			for (EObject root : new ArrayList<>(copiedResource.getContents())) {
				JavaRoot cu = (JavaRoot) root;
				if (cu.getOrigin() == Origin.FILE) {
					String lastPart = createLastPathPart(cu);
					for (Path path : origJavaFiles) {
						if (path.toString().endsWith(lastPart)) {
							Resource newResource = copyContainer.createResource(
									URI.createFileURI(path.toString()));
							newResource.getContents().add(cu);
							newResource.save(null);
						}
					}
				}
			}
		} catch (IOException e) {
		}
	}
	
	private String createLastPathPart(JavaRoot cu) {
		StringBuilder builder = new StringBuilder();
		if (cu instanceof NamespaceAwareElement) {
			for (String ns : ((NamespaceAwareElement) cu).getNamespaces()) {
				builder.append(File.separator);
				builder.append(ns);
			}
		}
		builder.append(File.separator);
		if (cu instanceof CompilationUnit) {
			builder.append(cu.getName());
			builder.append(LogicalJavaURIGenerator.JAVA_FILE_EXTENSION);
		} else if (cu instanceof org.emftext.language.java.containers.Package) {
			builder.append(LogicalJavaURIGenerator.JAVA_PACKAGE_FILE_NAME);
		} else if (cu instanceof org.emftext.language.java.containers.Module) {
			builder.append(LogicalJavaURIGenerator.JAVA_MODULE_FILE_NAME);
		}
		return builder.toString();
	}
}
