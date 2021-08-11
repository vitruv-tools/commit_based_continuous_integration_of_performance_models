package cipm.consistency.vsum.test;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.commitintegration.CommitChangePropagator;
import cipm.consistency.vsum.VSUMFacade;

public abstract class AbstractCITest {
	protected VSUMFacade facade;
	protected CommitChangePropagator prop;
	
	@BeforeEach
	public void setUp() throws Exception {
		Logger logger = Logger.getLogger("cipm");
		logger.setLevel(Level.ALL);
		ConsoleAppender ap = new ConsoleAppender(new PatternLayout("[%d{DATE}] %-5p: %c - %m%n"),
				ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
		facade = new VSUMFacade(Paths.get(getTestPath()));
		prop = new CommitChangePropagator(new File(getRepositoryPath())
				.getAbsoluteFile(), facade.getFileLayout().getJavaPath().toString(), facade.getVSUM());
		prop.initialize();
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		facade.getVSUM().dispose();
		prop.shutdown();
	}
	
	protected abstract String getTestPath();
	
	protected abstract String getRepositoryPath();
}
