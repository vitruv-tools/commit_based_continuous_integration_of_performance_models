package cipm.consistency.base.vitruv.vsum.test;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.base.vitruv.vsum.VSUMFacade;
import tools.vitruv.applications.pcmjava.commitintegration.CommitChangePropagator;

public abstract class AbstractCITest {
	protected CommitChangePropagator prop;
	
	@BeforeEach
	public void setUp() throws Exception {
		Logger logger = Logger.getLogger(CommitChangePropagator.class.getSimpleName());
		ConsoleAppender ap = new ConsoleAppender();
		ap.setTarget(ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
		logger.setLevel(Level.ALL);
		VSUMFacade fac = new VSUMFacade(Paths.get(getTestPath()));
		prop = new CommitChangePropagator(new File(getRepositoryPath() + File.separator + ".git")
				.getAbsoluteFile(), fac.getFileLayout().getJavaPath().toString(), fac.getVSUM());
		prop.initialize();
	}
	
	protected abstract String getTestPath();
	
	protected abstract String getRepositoryPath();
}
