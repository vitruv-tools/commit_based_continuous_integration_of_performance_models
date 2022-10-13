package cipm.consistency.vsum.test.temp;

import java.io.IOException;
import java.nio.file.Paths;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.junit.jupiter.api.Test;

public class GitTest {
    @Test
    public void testGitRepo() throws IOException, NoHeadException, GitAPIException {
        var parentPath = Paths.get("../../.git");
        System.out.println("Parent path: "+ parentPath.toAbsolutePath().toString());
        var parentFile = parentPath.toFile();
        var parentRepo = Git.open(parentFile);

//        var srcLocalRepository = URI.createFileURI(Path.of("ciTestRepos", "caseStudy1").toString()).toFileString();
//        var targetDir = Path.of("testData", "tests", this.getClass()
//            .getSimpleName(), "repo").toFile();

        // var git = Git.cloneRepository()
//                    .setURI(srcLocalRepository.toString())
//                    .setDirectory(targetDir)
//                    .setCloneAllBranches(false)
//                    .setRemote("origin")
//                    .call();
//        var git = new FileRepository(srcLocalRepository);

        var submodule = SubmoduleWalk.getSubmoduleRepository(parentRepo.getRepository(),
                "change-based-adaptive-instrumentation/cipm.consistency.vsum.test/ciTestRepos/caseStudy1");
        var gitSubmodule = new Git(submodule);
        var commits = gitSubmodule.log().call();


        System.out.println("foobar");

    }
}
