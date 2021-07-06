package cipm.consistency.base.vitruv.vsum.test;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.base.vitruv.vsum.VSUMFacade;
import tools.vitruv.applications.pcmjava.commitintegration.CommitChangePropagator;
import tools.vitruv.applications.pcmjava.commitintegration.JavaParserAndPropagatorUtility;

public abstract class AbstractCITest {
	protected VSUMFacade facade;
	protected CommitChangePropagator prop;
	
	@BeforeEach
	public void setUp() throws Exception {
		Logger logger = Logger.getLogger(CommitChangePropagator.class.getSimpleName());
		ConsoleAppender ap = new ConsoleAppender();
		ap.setTarget(ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
		logger.setLevel(Level.ALL);
		logger = Logger.getLogger(JavaParserAndPropagatorUtility.class.getSimpleName());
		logger.addAppender(ap);
		logger.setLevel(Level.ALL);
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
