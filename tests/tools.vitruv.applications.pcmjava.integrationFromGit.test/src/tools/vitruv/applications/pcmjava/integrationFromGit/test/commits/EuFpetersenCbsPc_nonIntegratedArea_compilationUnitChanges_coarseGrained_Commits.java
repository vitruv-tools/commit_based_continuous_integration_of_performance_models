package tools.vitruv.applications.pcmjava.integrationFromGit.test.commits;
/**
 * Contains commits hashes and branch names for tests on the project tools.vitruv.applications.pcmjava.integrationFromGit.test/testProjects/petersen/projectWithCommits/eu.fpetersen.cbs.pc.withGit
 * for coarse-grained changes in non-integrated areas within classes except method headers and method bodies. 
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 *
 */
public abstract class EuFpetersenCbsPc_nonIntegratedArea_compilationUnitChanges_coarseGrained_Commits {
	
	
	//Branch and commits for testing coarse grained changes in a non integrated area on a compilation unit
	//****************************************************************************************************************
	public final static String BRANCH_NAME = "nonIntegratedArea_compilationUnitChanges_coarseGrained";
	
	
	//initial commit
	public final static String INIT = "9fdb7d20a919cfa88fb51db47a20657073dfce92";
	//added nonIntegratedPackage in src folder
	public final static String ADD_NON_INTEGRATED_PACKAGE = "8e604e1710fc74acfbdf02fde2b244b76540c12b";
	//added packages contracts and datatypes in nonIntegratedPackage
	public final static String ADD_CONTRACTS_DATATYPES = "33ced62a159476f8c3fe2f81b4d3fd5ebd9f0e70";
	//added package FirstClass in nonIntegratedPackage
	public final static String ADD_FIRST_CLASS_PACKAGE = "fc3a8d24840696f70deac9237d5b061f3c94376e";
	//added FirstClassImpl.java in package FirstClass
	public final static String ADD_FIRST_CLASS_IMPL = "6f3b69ccfd55d03839d7a7b97a0502fbef7fc846";
	//added package SecondClass in nonIntegratedPackage
	public final static String ADD_SECOND_CLASS_PACKAGE = "dded6d690093d36d3b87c3f65901d40f3faa80a5";
	//added SecondClassImpl.java in package SecondClass
	public final static String ADD_SECOND_CLASS_IMPL = "720426f6d9d424d3b8d37d38335eb9207c618d71";
	//added FirstInterface.java in nonIntegratedPackage.contracts
	public final static String ADD_FIRST_INTERFACE = "c51189d8ec13da81ade2c981a4447b3499df6a00";
	//added SecondInterface.java in nonIntegratedPackage.contracts
	public final static String ADD_SECOND_INTERFACE = "276ae908642f09e272fe4d51ec3e36561ab0e36f";
	//added 'void firstMethodInFirstInterface();' in FirstInterface.java
	public final static String ADD_FIRST_METHOD_IN_FIRST_INTERFACE = "4b1e77bc0df26870edc09c87a8645484f11ccc4d";
	//added 'void firstMethodInSecondInterface();' in SecondInterface.java
	public final static String ADD_FIRST_METHOD_IN_SECOND_INTERFACE = "5f5c824b17478127c07316cb04be80d0cc8676df";
	
	
	//added import, implements, implemented method with override and implementation used only internal actions
	public final static String FIRST_COARSE_GRAINED_COMMIT = "2915e976745af9464bb669d2b8e36b428dba41a9";
	
	
	
	
	
	
}
