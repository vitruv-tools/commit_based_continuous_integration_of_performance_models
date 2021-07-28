package cipm.consistency.tools.evaluation.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

public class EvaluationResultReaderWriter {
	public static EvaluationResult read(Path file) {
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			return new Gson().fromJson(reader, EvaluationResult.class);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static void write(EvaluationResult result, Path file) {
		Gson gson = new Gson();
		try (BufferedWriter writer = Files.newBufferedWriter(file)) {
			gson.toJson(result, EvaluationResult.class, gson.newJsonWriter(writer));
		} catch (IOException e) {
		}
	}
}
