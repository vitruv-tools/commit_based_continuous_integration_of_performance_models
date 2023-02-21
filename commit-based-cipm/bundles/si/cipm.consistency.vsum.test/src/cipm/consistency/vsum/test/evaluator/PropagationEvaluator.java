package cipm.consistency.vsum.test.evaluator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.xtext.lua.lua.Component;

import cipm.consistency.vsum.Propagation;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;

/**
 * This class can be used to evaluate one propagation step, including the state based change
 * resolution.
 * 
 * This is achieved by comparing the model resulting from the propagation with the parsed model of
 * the target version.
 *
 * 
 * @author Lukas Burgey
 */
public class PropagationEvaluator {
    private static final Logger LOGGER = Logger.getLogger(PropagationEvaluator.class);

    public static boolean evaluate(Propagation propagation) {
        var targetModelPath = propagation.getTargetVersionParsedModelPath();
        var actualModelPath = propagation.getPropagationResultModelPath();
        if (targetModelPath != null && actualModelPath != null) {
            var modelsSimilar = diffModelFiles(targetModelPath, actualModelPath);

            if (modelsSimilar) {
                LOGGER.info("Propagation passed evaluation");
            } else {
//                printDiffBetween(targetModelPath, actualModelPath);
                LOGGER.warn(
                        "Parsed target version and actual propagation result are not similar! Something is wrong with the change resolution!");
            }
            return modelsSimilar;
        }

        // no evaluation if we don't have the respective model files to evaluate
        return true;
    }

    private static boolean diffModelFiles(Path target, Path actual) {
        try {
            return FileUtils.contentEquals(target.toFile(), actual.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * These next two things from: https://www.baeldung.com/run-shell-command-in-java
     */
    private static void printDiffBetween(Path target, Path actual) {
        try {
            var process = Runtime.getRuntime()
                .exec(String.format("/usr/bin/icdiff --cols=120 %s %s", target.toString(), actual.toString()));

            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Future<?> future = Executors.newSingleThreadExecutor()
                .submit(streamGobbler);

            int exitCode = process.waitFor();
            assert exitCode == 0;

            future.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        }
    }
}
