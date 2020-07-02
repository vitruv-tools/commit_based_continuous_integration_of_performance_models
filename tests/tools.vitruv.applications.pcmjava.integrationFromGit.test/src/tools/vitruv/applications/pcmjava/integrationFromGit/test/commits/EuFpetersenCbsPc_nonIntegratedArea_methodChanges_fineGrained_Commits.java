package tools.vitruv.applications.pcmjava.integrationFromGit.test.commits;


/**
 * Contains commits hashes and branch names for tests on the project tools.vitruv.applications.pcmjava.integrationFromGit.test/testProjects/petersen/projectWithCommits/eu.fpetersen.cbs.pc.withGit
 * for atomic changes in non-integrated areas within classes on method headers and method bodies. 
 * 
 * @author Ilia Chupakhin
 *
 */
public abstract class EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits {
	
		//Branch and commits for project preparation for testing fine grained changes in a non integrated area on a method
		public final static String BRANCH_NAME_COMMON = "nonIntegratedArea_methodChanges_fineGrained";
		
		//initial commit
		public final static String INIT = "9fdb7d20a919cfa88fb51db47a20657073dfce92";
		//added nonIntegratedPackage in src folder
		public final static String ADD_NON_INTEGRATED_PACKAGE = "21cb2d82b25263d787053efd801b9ec34dff7e65";
		//added packages contracts and datatypes in nonIntegratedPackage
		public final static String ADD_CONTRACTS_DATATYPES = "6fe8266cf042b46e119293562bd0a136b4f28c69";
		//added FirstInterface.java in contracts
		public final static String ADD_FIRST_INTERFACE = "d69716a6d211b12c9603c2e519ae46b0052d1d5c";
		//added void 'firstMethodInFirstInterface();' in FirstInterface.java
		public final static String ADD_FIRST_METHOD_IN_FIRST_INTERFACE = "7d11a1af33ae9afdbf4c05a6c13affa770e3841a";
		//added SecondInterface.java in contracts
		public final static String ADD_SECOND_INTERFACE = "cfe54f6919a541b5e30bd3a40a6fd75874aa134a";
		//added 'void firstMethodInSecondInterface();' in SecondInterface.java
		public final static String ADD_FIRST_METHOD_IN_SECOND_INTERFACE = "a9d2360e6f7b68a6f3c4e6e020e8fc0fe96b250e";
		//added package FirstClass in nonIntegratedPackage
		public final static String ADD_FIRST_CLASS_PACKAGE = "43ad69c485644a79941baa59890ccb88b0c82c7f";
		//added FirstClassImpl.java in package FirstClass
		public final static String ADD_FIRST_CLASS_IMPL = "dada882b1b430469c5efdb27e5cccea6a58e6916";
		//added package SecondClass in nonIntegratedPackage
		public final static String ADD_SECOND_CLASS_PACKAGE = "2a0ae4644e24c229f668365733d38fc01e355646";
		//added SecondClassImpl.java in package SecondClass
		public final static String ADD_SECOND_CLASS_IMPL = "a4f52760ba8f42ff6883aa9123a2a3eb93422776";
		//added 'import nonIntegratedPackage.contracts.FirstInterface;' in FirstClassImpl.java
		public final static String ADD_FIRST_IMPORT_IN_FIRST_CLASS_IMPL = "6bea7569671ae19323e2612490310870e6601e19";
		//added 'implements FirstInterface' and 'public void firstMethodInFirstInterface() {}' in FirstClassImpl.java
		public final static String ADD_IMPLEMENTS_AND_METHOD_IN_FIRST_CLASS_IMPL = "4449973d7ea7216537cd22aa7e2f151d0720cd83";
		//added 'import nonIntegratedPackage.contracts.SecondInterface;' in SecondClassImpl.java
		public final static String ADD_FIRST_IMPORT_IN_SECOND_CLASS_IMPL = "9a5c6e4042ef2371f8f7cc96947cd579ef02be79";
		//added 'implements SecondInterface' and 'public void firstMethodInSecondInterface() {}' in SecondClassImpl.java
		public final static String ADD_IMPLEMENTS_AND_METHOD_IN_SECOND_CLASS_IMPL = "4f4cdcdeb10ac10123dd0ba1308b53e38c1dc386";
		
		
		//Branch and commits for testing fine grained changes in a non integrated area on  method implementation
		public final static String BRANCH_NAME_METHOD_IMPLEMENTATION = "nonIntegratedArea_methodChanges_fineGrained_methodImplementation";
		
		//added 'int i = 0;' in firstMethodInFirstInterface in FirstClassImpl.java
		public final static String ADD_FIRST_INTERNAL_ACTION_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL = "b062c3b27f021e19070a26fbea969923d44f4d97";
		//add 'import nonIntegratedPackage.SecondClass.SecondClassImpl;' in FirstClassImpl.java
		public final static String ADD_SECOND_IMPORT_IN_FIRST_CLASS_IMPL = "b2d95baf9f159cf92c3a136109fb5369c1fa3dae";		
		//added 'SecondClassImpl secondClassImpl = new SecondClassImpl();secondClassImpl.firstMethodInSecondInterface();' in firstMethodInFirstInterface in FirstClassImpl.java
		public final static String ADD_FIRST_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL = "c193eb70b1b97dec7a1e992ab853eee99995a22a";		
		//added 'int k = 10;for (int j = 0; j < k; j++) {secondClassImpl.firstMethodInSecondInterface();}' in firstMethodInFirstInterface in FirstClassImpl.java
		public final static String ADD_SECOND_INTERNAL_ACTION_AND_LOOP_WITH_SECOND_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL = "019bd57b0c7ebd695ff63ebddd81c8d5531051e9";		
		//added 'if (k < 10) {k = 0;}else {secondClassImpl.firstMethodInSecondInterface();}' in firstMethodInFirstInterface in FirstClassImpl.java
		public final static String ADD_IF_ELSE_WITH_INTERNAL_ACTION_AND_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL = "81236fa023c0d6ef4ca93e5d8ac7a52aa8cc90de";		
								
				
				
				
		//Branch and commits for testing fine grained changes in a non integrated area on  method modifiers
		public final static String BRANCH_NAME_METHOD_MODIFIERS = "nonIntegratedArea_methodChanges_fineGrained_methodModifiers";
		
		
}
