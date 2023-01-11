package tools.vitruv.applications.pcmjava.commitintegration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jgit.util.FileUtils;
import org.emftext.language.java.JavaClasspath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import tools.vitruv.applications.pcmjava.commitintegration.domains.java.AdjustedJavaDomainProvider;
//import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.vsum.VirtualModel;
import tools.vitruv.framework.vsum.VirtualModelBuilder;

public abstract class AbstractGitIntegrationTest {
	protected VirtualModel vsum;
	protected CommitChangePropagator prop;
	private String targetDir = "target";
	private String vsumDir = targetDir + File.separator + getRepository() + "-vsum";
	
	@BeforeAll
	public static void setUpBeforeAll() {
		Logger logger = Logger.getLogger(CommitChangePropagator.class.getSimpleName());
		ConsoleAppender ap = new ConsoleAppender();
		ap.setTarget(ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
		logger.setLevel(Level.ALL);
		JavaClasspath.get().registerStdLib();
	}
	
	@BeforeEach
	public void setUp() {
		String localRepoDirName = getRepository() + "-copy";
		try {
			FileUtils.delete(new File(targetDir + File.separator + localRepoDirName), FileUtils.RECURSIVE);
			FileUtils.delete(new File(vsumDir), FileUtils.RECURSIVE);
		} catch (IOException e) {
		}
		vsum = setUpVSUM();
		File localRepo = new File(targetDir + File.separator + getRepository() + File.separator + ".git").getAbsoluteFile();
		String repoCopy = Paths.get(targetDir, localRepoDirName).toAbsolutePath().toString();
		prop = new CommitChangePropagator(localRepo, repoCopy, vsum);
	}
	
	private VirtualModel setUpVSUM() {
		return new VirtualModelBuilder().withDomain(new AdjustedJavaDomainProvider().getDomain())
				.withStorageFolder(Paths.get(vsumDir))
				.withUserInteractor(UserInteractionFactory.instance.createDialogUserInteractor())
				.buildAndInitialize();
	}
	
	protected abstract String getRepository();
}
