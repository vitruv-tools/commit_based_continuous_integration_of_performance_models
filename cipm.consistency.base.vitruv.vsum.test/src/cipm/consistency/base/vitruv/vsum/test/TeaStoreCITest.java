package cipm.consistency.base.vitruv.vsum.test;

import java.io.File;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TeaStoreCITest extends AbstractCITest {
	private static final String COMMIT_TAG_1_1 = "77733d9c6ab6680c6cc460c631cd408a588a595c";
	private static final String COMMIT_TAG_1_2 = "53c6efa1dca64a87e536d8c5a3dcc3c12ad933b5";
	private static final String COMMIT_TAG_1_2_1 = "f8f13f4390f80d3dc8adb0a6167938a688ddb45e";
	
	@Override
	protected String getTestPath() {
		return "target" + File.separator + "TeaStoreTest";
	}
	
	@Override
	protected String getRepositoryPath() {
		return "../.git/modules/cipm.consistency.base.vitruv.vsum.test/TeaStore";
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_1Integration() throws Exception {
		// Integrates TeaStore version 1.1.
		prop.propagateChanges(COMMIT_TAG_1_1);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_1To1_2Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.1 and 1.2.
		prop.propagateChanges(COMMIT_TAG_1_1, COMMIT_TAG_1_2);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_2Integration() throws Exception {
		// Integrates TeaStore version 1.2.
		prop.propagateChanges(COMMIT_TAG_1_2);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_2To_1_2_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.2 and 1.2.1.
		prop.propagateChanges(COMMIT_TAG_1_2, COMMIT_TAG_1_2_1);
	}
	
//	@Disabled("Only one test case should run at once.")
//	@Test
//	public void testTeaStoreIntegration() throws Exception {
//		prop.propagateChanges("b0ecb45238772f06db1e11e8e7baaa72f48cdd96");
//	}
	
//	@Disabled("Only one test case should run at once.")
//	@Test
//	public void testTeaStoreFirstPropagation() throws Exception {
//		prop.propagateChanges("b0ecb45238772f06db1e11e8e7baaa72f48cdd96",
//					"653528e387a4fb764e48c9422ab218c65f87082f",
//					"0ce0cababe2590eff6d03c15f9eb3c977fdf4914");
//	}
	
//	@Disabled("Only one test case should run at once.")
//	@Test
//	public void testTeaStoreSecondPropagation() throws Exception {
//		prop.propagateChanges("0ce0cababe2590eff6d03c15f9eb3c977fdf4914",
//				"205d163bc2c878c90ac5038276ad04cdf502a695");
//	}
}
