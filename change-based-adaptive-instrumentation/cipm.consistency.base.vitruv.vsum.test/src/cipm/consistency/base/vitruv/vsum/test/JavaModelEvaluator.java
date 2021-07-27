package cipm.consistency.base.vitruv.vsum.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.emf.compare.Match;
import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.base.vitruv.vsum.test.evaluation.JavaEvaluationResult;
import cipm.consistency.commitintegration.JavaParserAndPropagatorUtility;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;

public class JavaModelEvaluator {
	private JavaEvaluationResult currentEvalResult;
	
	public JavaEvaluationResult evaluateJavaModels(Resource javaModel, Path srcDir) {
		currentEvalResult = new JavaEvaluationResult();
		Path referenceModelPath = Paths.get(javaModel.getURI().toFileString());
		Resource parsed = JavaParserAndPropagatorUtility
				.parseJavaCodeIntoOneModel(srcDir, referenceModelPath);
		parsed.getAllContents().forEachRemaining(o -> currentEvalResult.newElementsCount++);
		javaModel.getAllContents().forEachRemaining(o -> currentEvalResult.oldElementsCount++);
		calculateJC(parsed, javaModel);
		return currentEvalResult;
	}
	
	private void calculateJC(Resource parsed, Resource loaded) {
		var result = JavaModelComparator.compareJavaModels(parsed, loaded,
				List.of(parsed), List.of(loaded), null);
		double unionCardinality = calculateUnionCardinality(result.getMatches());
		currentEvalResult.unionCardinality = (int) unionCardinality;
		double intersectionCardinality = calculateIntersectionCardinality(result.getMatches());
		currentEvalResult.intersectionCardinality = (int) intersectionCardinality;
		if (unionCardinality == 0) {
			currentEvalResult.jc = -1;
		} else {
			currentEvalResult.jc = intersectionCardinality / unionCardinality;
		}
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
