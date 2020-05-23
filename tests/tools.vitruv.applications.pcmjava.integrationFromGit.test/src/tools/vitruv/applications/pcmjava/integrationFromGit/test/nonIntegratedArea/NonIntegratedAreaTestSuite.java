package tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea.ClassAnnotationChangeTest;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea.ClassImportChangeTest;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea.ClassModifiersChangeTest;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea.ClassExtendsChangeTest;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea.ClassImplementsChangeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ClassImportChangeTest.class, ClassModifiersChangeTest.class})
@SuppressWarnings("all")
public class NonIntegratedAreaTestSuite {

}



