package cipm.consistency.base.vitruv;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cipm.consistency.base.vitruv.integration.BaseVsumIntegrationTest;
import cipm.consistency.base.vitruv.persistence.CorrespondencePersistenceTest;
import cipm.consistency.base.vitruv.reactions.VsumReactionsTests;

@RunWith(Suite.class)
@SuiteClasses({ BaseVsumIntegrationTest.class, CorrespondencePersistenceTest.class, VsumReactionsTests.class })
public class VsumTestSuite {
}
