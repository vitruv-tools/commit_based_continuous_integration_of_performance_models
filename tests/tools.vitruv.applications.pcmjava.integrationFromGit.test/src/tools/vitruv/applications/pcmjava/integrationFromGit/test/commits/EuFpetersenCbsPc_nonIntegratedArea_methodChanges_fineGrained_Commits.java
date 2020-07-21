package tools.vitruv.applications.pcmjava.integrationFromGit.test.commits;


/**
 * Contains commits hashes and branch names for tests on the project tools.vitruv.applications.pcmjava.integrationFromGit.test/testProjects/petersen/projectWithCommits/eu.fpetersen.cbs.pc.withGit
 * for atomic changes in non-integrated areas within classes on method headers and method bodies. 
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
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
		
		//added 'import nonIntegratedPackage.contracts.SecondInterface;' in FirstClassImpl.java
		public final static String ADD_SECOND_IMPORT_IN_FIRST_CLASS_IMPL = "121884b6e6e596a7b5d0b78a71258e15efae6e28";
		//added 'SecondInterface externalClass;' in FirstClassImpl.java
		public final static String ADD_FIELD_IN_FIRST_CLASS_IMPL = "d2f84ecf18ed9cdc8421339c6658759cad974320";
		//added 'externalClass.firstMethodInSecondInterface();' in method firstMethodInFirstInterface in FirstClassImpl.java
		public final static String ADD_EXTERNAL_CALL = "21b208d3b65269435f408456a864c841fcca415b";
		//added 'int i = 10;' in method firstMethodInFirstInterface in FirstClassImpl.java
		public final static String  ADD_INTERNAL_ACTION= "70db2fb295e4d9a296d8ceab7d254e127edc092b";
		//added 'for (int k = 0; k < i; k++) {externalClass.firstMethodInSecondInterface();}' in method firstMethodInFirstInterface in FirstClassImpl.java
		public final static String ADD_FOR = "f25446498dd58a31e6a66f5ddcbb2d158cd5ab2e";
		//added 'if (i < 2) {i = 20;} else {externalClass.firstMethodInSecondInterface();}' in method firstMethodInFirstInterface in FirstClassImpl.java
		public final static String ADD_IF_ELSE = "881b13ac8874db4bc5f7bd357b85adb227923428";
		//removed 'if (i < 2) {i = 20;} else {externalClass.firstMethodInSecondInterface();}' in method firstMethodInFirstInterface in FirstClassImpl.java
		public final static String REMOVE_IF_ELSE = "3b5976e4c5000c1ffd432af1bd9b600872d6aabe";
		//removed 'for (int k = 0; k < i; k++) {externalClass.firstMethodInSecondInterface();}' in method firstMethodInFirstInterface in FirstClassImpl.java
		public final static String REMOVE_FOR = "806351160ed0aff2146ca5eb81cec1f044e7f44a";
		//removed 'int i = 10;' in method firstMethodInFirstInterface in FirstClassImpl.java
		public final static String REMOVE_INTERNAL_ACTION = "059ce4d0c7c445b68690af382035559f736b5e68";
		//removed 'externalClass.firstMethodInSecondInterface();' in method firstMethodInFirstInterface in FirstClassImpl.java
		public final static String REMOVE_EXTERNAL_CALL = "b700046bc10932b096c1079b98018c634ce64a96";
								
				
		//Branch and commits for testing fine grained changes in a non integrated area on  method modifiers
		public final static String BRANCH_NAME_METHOD_MODIFIERS = "nonIntegratedArea_methodChanges_fineGrained_methodModifiers";
		
		//renamed method firstMethodInFirstInterface to firstMethodInFirstInterfaceRenamed in FirstInterface.java	
		public final static String RENAME_METHOD_IN_INTERFACE = "52721893df036dada0edd669ca3f64c45af6005b";
		//renamed method firstMethodInFirstInterface to firstMethodInFirstInterfaceRenamed in FirstClassImpl.java
		public final static String RENAME_METHOD_IN_CLASS = "eccb4cb1ea76c008ebef5b149c6f186a4c74a9b0";
		//changed return type from void to int in firstMethodInFirstInterfaceRenamed() in FirstInterface.java	
		public final static String CHANGE_RETURN_TYPE_IN_INTERFACE_METHOD = "ffacf0296d33e2a61f277d6bea74cda1ca2f8ac9";
		//changed return type from void to int in firstMethodInFirstInterfaceRenamed() in FirstClassImpl.java
		public final static String CHANGE_RETURN_TYPE_IN_CLASS_METHOD = "f385ac17d685551d7f8dfdc45be5c14764cb7a39";
		//added return 0 in firstMethodInFirstInterfaceRenamed() in FirstClassImpl.java
		public final static String ADD_RETURN_0 = "80d653f79a73a4502032c9a2d62a23d295f5145d";
		//added final modifier to firstMethodInFirstInterfaceRenamed() in FirstClassImpl.java
		public final static String ADD_FINAL_TO_CLASS_METHOD = "a9729f97820446227eff4a0ef1347c97a4d104a5";
		//added parameter 'double parameter'to firstMethodInFirstInterfaceRenamed() in FirstInterface.java	
		public final static String ADD_METHOD_PARAMETER_IN_INTERFACE = "80f473306d65e2125ed2247d0728480d7c0e88cf";
		//added parameter 'double parameter' to firstMethodInFirstInterfaceRenamed() in FirstClassImpl.java
		public final static String ADD_METHOD_PARAMETER_IN_CLASS = "64368a9fc1c1c9db302e605cbc4f2dc3da7de462";
		
}
