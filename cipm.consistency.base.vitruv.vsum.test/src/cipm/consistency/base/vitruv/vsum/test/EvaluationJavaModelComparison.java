package cipm.consistency.base.vitruv.vsum.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.JavaClasspath;
import org.junit.jupiter.api.Test;

import jamopp.resource.JavaResource2;
import jamopp.resource.JavaResource2Factory;
import tools.vitruv.applications.pcmjava.commitintegration.JavaParserAndPropagatorUtility;
import tools.vitruv.applications.pcmjava.commitintegration.propagation.JavaModelComparator;
import tools.vitruv.domains.java.JavaNamespace;

public class EvaluationJavaModelComparison {
	private Path srcDir = Paths.get("target/TeaStoreTest/java/local-repo-clone").toAbsolutePath();
	private Path referenceModel = Paths.get("target/TeaStoreTest/java/vsum-variant/Java.javaxmi").toAbsolutePath();
	
	@Test
	public void evaluateJavaModels() {
		JavaResource2Factory factory = new JavaResource2Factory();
		var map = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
		map.put(JavaResource2.JAVAXMI_FILE_EXTENSION, factory);
		map.put(JavaNamespace.FILE_EXTENSION, factory);
		map.put("class", factory);
		JavaClasspath.get().registerStdLib();
		ResourceSet loadedSet = new ResourceSetImpl();
		Resource loaded = loadedSet.getResource(
				URI.createFileURI(referenceModel.toString()), true);
		Resource parsed = JavaParserAndPropagatorUtility
				.parseJavaCodeIntoOneModel(srcDir, referenceModel);
		System.out.println("JC for the Java models: " + calculateJC(parsed, loaded));
	}
	
	private double calculateJC(Resource parsed, Resource loaded) {
		var result = JavaModelComparator.compareJavaModels(parsed, loaded,
				List.of(parsed), List.of(loaded), null);
		double unionCardinality = calculateUnionCardinality(result.getMatches());
		if (unionCardinality == 0) {
			return -1;
		}
		return calculateIntersectionCardinality(result.getMatches()) / unionCardinality;
	}
	
	private double calculateIntersectionCardinality(List<Match> matches) {
		double card = 0;
		for (Match m : matches) {
			if (m.getLeft() != null && m.getRight() != null) {
				card++;
			}
			card += calculateIntersectionCardinality(m.getSubmatches());
		}
		return card;
	}
	
	private double calculateUnionCardinality(List<Match> matches) {
		double card = 0;
		for (Match m : matches) {
			for (Match submatch : m.getAllSubmatches()) {
				card++;
			}
			card++;
		}
		return card;
	}
}
