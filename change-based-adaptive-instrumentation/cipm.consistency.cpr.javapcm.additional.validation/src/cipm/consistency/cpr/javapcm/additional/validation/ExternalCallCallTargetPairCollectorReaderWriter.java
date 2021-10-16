package cipm.consistency.cpr.javapcm.additional.validation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

/**
 * A reader and writer for collections of external calls and their targets.
 * 
 * @author Martin Armbruster
 */
public final class ExternalCallCallTargetPairCollectorReaderWriter {
	private ExternalCallCallTargetPairCollectorReaderWriter() {
	}

	/**
	 * Writes a collection.
	 * 
	 * @param obj    the collection.
	 * @param target path to the file in which the collection is stored.
	 */
	public static void write(ExternalCallCallTargetPairCollector obj, Path target) {
		Gson gson = new Gson();
		try (BufferedWriter writer = Files.newBufferedWriter(target)) {
			gson.toJson(obj, ExternalCallCallTargetPairCollector.class, gson.newJsonWriter(writer));
		} catch (IOException e) {
		}
	}

	/**
	 * Reads a collection from a file.
	 * 
	 * @param target path to the file in which the collection is stored.
	 * @return the loaded collection.
	 */
	public static ExternalCallCallTargetPairCollector read(Path target) {
		try (BufferedReader reader = Files.newBufferedReader(target)) {
			return new Gson().fromJson(reader, ExternalCallCallTargetPairCollector.class);
		} catch (IOException e) {
		}
		return new ExternalCallCallTargetPairCollector();
	}
}
