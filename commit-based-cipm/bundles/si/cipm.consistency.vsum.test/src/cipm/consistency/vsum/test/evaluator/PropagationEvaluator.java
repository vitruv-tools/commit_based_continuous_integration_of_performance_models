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
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.xmi.UnresolvedReferenceException;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.repository.Repository;
import org.xtext.lua.lua.ComponentSet;

import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.vsum.Propagation;

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

    /**
     * Evaluates if the change resolution that was used during the propagation is correct.
     * 
     * During the change resolution we generate a change sequence between the previously parsed code
     * model and the parsed code model of the new ("target") version. If applying this change
     * sequence to the code model of the vsum does not result in a model similar to the parsed code
     * model of the target version something went wrong.
     * 
     * @param propagation
     * @return True if the vsum code model was updated correctly during the propagation
     */
    private static boolean evaluateChangeResolution(Propagation propagation) {
        var codeModelValid = evaluateVsumCodeModel(propagation);
        if (!codeModelValid) {
            LOGGER.warn("Vsum code model is invalid");
        }

        var targetModelPath = propagation.getParsedCodeModelTargetVersionPath();
        var actualModelPath = propagation.getPropagationResultCodeModelPath();
        if (targetModelPath == null || actualModelPath == null) {
            // no evaluation if we don't have the respective model files to evaluate
            return true;
        }

        var modelsSimilar = diffModelFiles(targetModelPath, actualModelPath);
        if (!modelsSimilar) {
            printDiffBetween(targetModelPath, actualModelPath);
            LOGGER.warn(
                    "Parsed target version and actual propagation result are not similar! Something is wrong with the change resolution!");
        }

        return modelsSimilar;
    }

    /*
     * Validate a given EObject and all its children
     */
    private static boolean validateEObject(EObject rootEObject) {
        var diagnostics = new BasicDiagnostic();
        var eObjValidator = new EObjectValidator();
        var allContents = EcoreUtil2.getAllContentsOfType(rootEObject, EObject.class);
        var contentsValid = true;
        for (var eObj : allContents) {
            var valid = eObjValidator.validate(eObj, diagnostics, null);
            contentsValid &= valid;
        }
        if (!contentsValid) {
            for (var diag : diagnostics.getChildren()) {
                LOGGER.warn(diag.getMessage());
            }
        }
        return contentsValid;
    }

    private static boolean evaluateVsumCodeModel(Propagation propagation) {
        var componentSet = ModelUtil.readFromFile(propagation.getPropagationResultCodeModelPath()
            .toFile(), ComponentSet.class);
        if (componentSet == null) {
            LOGGER.warn("Could not read vsum code model for evaluation");
            return false;
        }
        return validateEObject(componentSet);
    }

    /**
     * Runs the Repository validator against the repository model that was the result of this
     * propagation.
     * 
     * @param propagation
     * @return True if valid
     */
    private static boolean evaluateResultingRepositoryModel(Propagation propagation) {
        var repoModelPath = propagation.getPropagationResultRepositoryModelPath();
        try {
            var repo = ModelUtil.readFromFile(repoModelPath.toFile(), Repository.class);
            if (repo == null) {
                LOGGER.warn("Could not read the repo snapshot file for evaluation");
                return false;
            }
            return validateEObject(repo);
        } catch (WrappedException e) {
            LOGGER.warn(e.getMessage());
            if (e.getCause() instanceof UnresolvedReferenceException unresExp) {
                LOGGER.warn("Could not resolve " + unresExp.getFeature()
                    .getName() + " / " + unresExp.getReference() + " on " + unresExp.getObject());
            }
            return false;
        }
    }

    public static boolean evaluate(Propagation propagation) {
        var changeResolution = evaluateChangeResolution(propagation);
        var repositoryValid = evaluateResultingRepositoryModel(propagation);
        
        if (!changeResolution) {
            LOGGER.warn("Change resolution did not pass evaluation");
        }
        if (!repositoryValid) {
            LOGGER.warn("PCM repository did not pass evaluation");
        }

        var valid = changeResolution && repositoryValid;
        if (valid) {
            LOGGER.info("Propagation passed evaluation");
        }
        return valid;
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
                .exec(String.format("/usr/bin/icdiff --cols=200 %s %s", target.toString(), actual.toString()));

            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Future<?> future = Executors.newSingleThreadExecutor()
                .submit(streamGobbler);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                future.get();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
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
