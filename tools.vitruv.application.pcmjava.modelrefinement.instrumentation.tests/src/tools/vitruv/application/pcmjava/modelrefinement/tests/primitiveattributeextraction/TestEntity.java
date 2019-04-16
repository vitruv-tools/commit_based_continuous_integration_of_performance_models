package tools.vitruv.application.pcmjava.modelrefinement.tests.primitiveattributeextraction;

public class TestEntity {

	private int int_test_entity_1 = 45;
	public double double_test_entity_1 = 1.80;
	public int[] byte_array;
	private TestEntity_2 testEntity_2;
	
	public TestEntity() {
		testEntity_2 = new TestEntity_2(2, 0.34);
		byte_array =  new int[20];
	}
	
	public int getInt_test_entity_1() {
		return int_test_entity_1;
	}
	public void setInt_test_entity_1(int int_test_entity_1) {
		this.int_test_entity_1 = int_test_entity_1;
	}
	public double getDouble_test_entity_1() {
		return double_test_entity_1;
	}
	public void setDouble_test_entity_1(double double_test_entity_1) {
		this.double_test_entity_1 = double_test_entity_1;
	}

	public TestEntity_2 getTestEntity_2() {
		return testEntity_2;
	}

	public void setTestEntity_2(TestEntity_2 testEntity_2) {
		this.testEntity_2 = testEntity_2;
	}
	
	
	
}
