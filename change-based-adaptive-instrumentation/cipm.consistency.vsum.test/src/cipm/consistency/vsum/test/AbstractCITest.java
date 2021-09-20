package cipm.consistency.vsum.test;

import java.nio.file.Paths;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.vsum.CommitIntegrationController;

public abstract class AbstractCITest {
	protected CommitIntegrationController controller;
	
	@BeforeEach
	public void setUp() throws Exception {
		Logger logger = Logger.getLogger("cipm");
		logger.setLevel(Level.ALL);
		logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		ConsoleAppender ap = new ConsoleAppender(new PatternLayout("[%d{DATE}] %-5p: %c - %m%n"),
				ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
		controller = new CommitIntegrationController(Paths.get(getTestPath()),
				getRepositoryPath(), Paths.get(getSettingsPath()));
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		controller.shutdown();
	}
	
	protected abstract String getTestPath();
	
	protected abstract String getRepositoryPath();
	
	protected abstract String getSettingsPath();
}
