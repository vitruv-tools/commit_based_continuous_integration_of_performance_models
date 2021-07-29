package cipm.consistency.vsum.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.emf.compare.Match;
import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.commitintegration.JavaParserAndPropagatorUtility;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.tools.evaluation.data.JavaEvaluationData;

public class JavaModelEvaluator {
	private JavaEvaluationData currentEvalResult;
	
	public void evaluateJavaModels(Resource javaModel, Path srcDir, JavaEvaluationData evalData,
			Path configPath) {
		currentEvalResult = evalData;
		Path referenceModelPath = Paths.get(javaModel.getURI().toFileString());
		Resource parsed = JavaParserAndPropagatorUtility
				.parseJavaCodeIntoOneModel(srcDir, referenceModelPath, configPath);
		parsed.getAllContents().forEachRemaining(o -> currentEvalResult.setNewElementsCount(currentEvalResult.getNewElementsCount()+1));
		javaModel.getAllContents().forEachRemaining(o -> currentEvalResult.setOldElementsCount(currentEvalResult.getOldElementsCount()+1));
		calculateJC(parsed, javaModel);
	}
	
	private void calculateJC(Resource parsed, Resource loaded) {
		var result = JavaModelComparator.compareJavaModels(parsed, loaded,
				List.of(parsed), List.of(loaded), null);
		double unionCardinality = calculateUnionCardinality(result.getMatches());
		currentEvalResult.setUnionCardinality((int) unionCardinality);
		double intersectionCardinality = calculateIntersectionCardinality(result.getMatches());
		currentEvalResult.setIntersectionCardinality((int) intersectionCardinality);
		if (unionCardinality == 0) {
			currentEvalResult.setJc(-1);
		} else {
			currentEvalResult.setJc(intersectionCardinality / unionCardinality);
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
