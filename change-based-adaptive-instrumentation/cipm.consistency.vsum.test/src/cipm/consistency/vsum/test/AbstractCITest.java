package cipm.consistency.vsum.test;

import java.nio.file.Paths;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.vsum.CommitIntegrationController;
import tools.vitruv.framework.propagation.ChangePropagationSpecification;

/**
 * An abstract superclass for test cases providing the setup.
 * 
 * @author Martin Armbruster
 */
public abstract class AbstractCITest {
	protected CommitIntegrationController controller;

	@BeforeEach
	public void setUp() throws Exception {
		Logger logger = Logger.getLogger("cipm");
		logger.setLevel(Level.ALL);
		logger = Logger.getLogger("jamopp");
		logger.setLevel(Level.ALL);
		logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		ConsoleAppender ap = new ConsoleAppender(new PatternLayout("[%d{DATE}] %-5p: %c - %m%n"),
				ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
		controller = new CommitIntegrationController(Paths.get(getTestPath()), getRepositoryPath(),
				Paths.get(getSettingsPath()), getJavaPCMSpecification());
	}

	@AfterEach
	public void tearDown() throws Exception {
		controller.shutdown();
	}

	/**
	 * Returns the path to the local directory in which the data is stored.
	 * 
	 * @return the path.
	 */
	protected abstract String getTestPath();

	/**
	 * Returns the path to the remote repository from which the commits are fetched.
	 * 
	 * @return the path.
	 */
	protected abstract String getRepositoryPath();

	/**
	 * Returns the path to the settings file.
	 * 
	 * @return the path.
	 */
	protected abstract String getSettingsPath();
	
	/**
	 * Returns the CPRs between Java and the PCM.
	 * 
	 * @return the CPRs.
	 */
	protected abstract ChangePropagationSpecification getJavaPCMSpecification();
}
