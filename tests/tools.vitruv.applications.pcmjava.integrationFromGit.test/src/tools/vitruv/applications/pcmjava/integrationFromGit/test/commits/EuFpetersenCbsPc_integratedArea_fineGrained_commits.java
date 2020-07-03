package tools.vitruv.applications.pcmjava.integrationFromGit.test.commits;

/**
 * Contains commits hashes and branch names for tests on the project tools.vitruv.applications.pcmjava.integrationFromGit.test/testProjects/petersen/projectWithCommits/eu.fpetersen.cbs.pc.withGit
 * for atomic changes in integrated areas. 
 * 
 * @author Ilia Chupakhin
 *
 */
public abstract class EuFpetersenCbsPc_integratedArea_fineGrained_commits {
	
	//initial commit from master branch	
	public final static String INIT = "9fdb7d20a919cfa88fb51db47a20657073dfce92";
	
	////Branch and commits for testing creation and deleting a method in an integrated area
	//****************************************************************************************************************
	public final static String METHOD_CREATE_DELETE_BRANCH_NAME = "integratedArea_fineGrained_methodCreateDelete";
	
	//added newMethodInIDisplay in IDisplay.java
	public final static String ADD_METHOD_TO_INTERFACE = "e516255876d6a5dda276ea68cc68d150c63436d2";
	//added newMethodInIDisplay in Display.java
	public final static String ADD_METHOD_TO_CLASS = "f8c1f953d017b06ace2c380a112c8cb50a68a300";
	//removed newMethodInIDisplay in Display.java
	public final static String REMOVE_METHOD_IN_CLASS = "84deeb1d40a1c32c733e912d370f17424629b190";
	//removed newMethodInIDisplay in IDisplay.java
	public final static String REMOVE_METHOD_IN_INTERFACE = "b67bb3992a573ff7bf5ae22f409f56de45463d40";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on a method header
	//****************************************************************************************************************
	public final static String METHOD_HEADER_BRANCH_NAME = "integratedArea_fineGrained_methodHeader";

	//renamed method drawFrame to drawFrameRenamed in IDisplay.java	
	public final static String RENAME_METHOD_IN_INTERFACE = "0810491a0787ae71a8c48e815ed3534b7c6f0165";
	//renamed method drawFrame to drawFrameRenamed in Display.java
	public final static String RENAME_METHOD_IN_CLASS = "30a71949891ede035856a919be39e37c1ef31fb0";
	//changed return type from void to int in drawFrameRenamed() in IDisplay.java	
	public final static String CHANGE_RETURN_TYPE_IN_INTERFACE_METHOD = "b042d0d70e02e8d9a54940cfee6012cec8a44d7c";
	//changed return type from void to int in drawFrameRenamed() in Display.java
	public final static String CHANGE_RETURN_TYPE_IN_CLASS_METHOD = "f84e5fe1b3e8c048d5c19723c6de1c7942daebe6";
	//added return 0 in drawFrameRenamed() in Display.java
	public final static String ADD_RETURN_0 = "1445e7214d8cbd1626b2cf2cf319e7c3e89029df";
	//added final modifier to drawFrameRenamed() in Display.java
	public final static String ADD_FINAL_TO_CLASS_METHOD = "4ea481c55b531575f1a17ec4ef4f44e996937e92";
	//added parameter to drawFrameRenamed() in IDisplay.java	
	public final static String ADD_METHOD_PARAMETER_IN_INTERFACE = "03d65f373d2aec83f037a1c34ed25206ba7a3617";
	//added parameter to drawFrameRenamed() in Display.java
	public final static String ADD_METHOD_PARAMETER_IN_CLASS = "88165a0ef246618d5861bca6789c24c4a3b99853";

	
	//Branch and commits for testing fine grained changes in an integrated area on a class field
	//****************************************************************************************************************
	public final static String FIELD_BRANCH_NAME = "integratedArea_fineGrained_field";

	//added 'import eu.fpetersen.cbs.pc.display.IDisplay;' in GraphicsCard.java
	public final static String ADD_IMPORT_FOR_FILED = "704c360055b6d040a5a49f282dc59df95e15a12f";
	//added field 'IDisplay field' in GraphicsCard.java	
	public final static String ADD_FIELD = "92012453fd7854c97b41d20e17a35db8c2c35ed3";
	//renamed field 'IDisplay field' to 'IDisplay fieldRenamed' in GraphicsCard.java
	public final static String RENAME_FIELD = "24bccdeddc88e970b97246b48983d4691fa26fc7";
	//added public modifier to fieldRenamed in GraphicsCard.java
	public final static String ADD_FIELD_MODIFIER = "9953af1c9450d97cfc92122840564125f0b1ce49";
	//changed modifier of fieldRenamed from public to private in GraphicsCard.java
	public final static String CHANGE_FIELD_MODIFIER = "b77e5c524325bfd621e1d3085bd1cf8fbf3ffe73";
	//changed field type from 'IDisplay' to 'String' in GraphicsCard.java
	public final static String CHANGE_FIELD_TYPE = "1ca7e601515303f811f09b4f5e90657e076d414d";
	//removed field 'fieldRenamed' in GraphicsCard.java
	public final static String REMOVE_FIELD = "5912a84f3d5f7fe3402a89404b7507d953808d83";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on a method implementation
	//****************************************************************************************************************
	public final static String METHOD_IMPLEMENTATION_BRANCH_NAME = "integratedArea_fineGrained_methodImplementation";

	//added internal action in drawFrame() in Display.java
	public final static String  ADD_INTERNAL_ACTION= "85c29e3264c92d8f41410067c8f69449f720fb7a";
	//added for-loop with external call in drawFrame() in Display.java	
	public final static String ADD_FOR = "432a0d709659cad509fd8529c7477cd5eeb9eb6c";
	//added if-else branch with internal action and external call in drawFrame() in Display.java
	public final static String ADD_IF_ELSE = "c5f8a46936800caf912d9f25d3d56ca382aced5c";
	//removed if-else branch with internal action and external call in drawFrame() in Display.java
	public final static String REMOVE_IF_ELSE = "cc8db997fb878c85d869d650a2101ca396be7b16";
	//removed for-loop with external call in drawFrame() in Display.java
	public final static String REMOVE_FOR = "11b6b0caf0b635ec04547321d7102bacc854b0a0";
	//removed internal action in drawFrame() in Display.java
	public final static String REMOVE_INTERNAL_ACTION = "cb932e8295c1699e3f1eeb2cdf7c6bb7efb83f10";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on class annotation
	//****************************************************************************************************************
	public final static String CLASS_ANNOTATION_BRANCH_NAME = "integratedArea_fineGrained_classAnnotation";

	//added class annotation to Display.java
	public final static String ADD_CLASS_ANNOTATION= "1c05e30f6140bbadebc01298f9b8a0930a4798d6";
	//changed class annotation to Display.java
	public final static String CHANGE_CLASS_ANNOTATION = "9053f8ca6074e6d3adcaf63ab280501eebcbaece";
	//removed class annotation in Display.java
	public final static String REMOVE_CLASS_ANNOTATION = "25c5d6031e6479480ccba6392daf43ff5cd7bccb";
	

	//Branch and commits for testing creation and deleting a class in an integrated area
	//****************************************************************************************************************
	public final static String CLASS_CREATE_DELETE_BRANCH_NAME = "integratedArea_fineGrained_classCreateDelete";

	//added NewClass.java in package eu.fpetersen.cbs.pc.display
	public final static String ADD_CLASS = "e4a6fa493b44f618414ded418c393847662d0357";
	//added NewInterface.java in package eu.fpetersen.cbs.pc.display
	public final static String ADD_INTERFACE = "15df9b7cde009f067e0829b69513aba3506dde26";
	//removed NewClass.java in package eu.fpetersen.cbs.pc.display
	public final static String REMOVE_CLASS = "7603cc5cb26e88658656064f16dd7f6909d6082c";
	//removed NewInterface.java in package eu.fpetersen.cbs.pc.display
	public final static String REMOVE_INTERFACE = "bda841448ab3d0de8ab4394d797322f9910cb40c";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on implements
	//****************************************************************************************************************
	public final static String IMPLEMENTS_BRANCH_NAME = "integratedArea_fineGrained_implements";

	//added 'import eu.fpetersen.cbs.pc.display.IDisplay;' in GraphicsCard.java
	public final static String ADD_IMPORT_FOR_IMPLEMENTS= "629dc746b020ab9d05b1668921920105a43452f5";
	//added 'implements IDisplay' in GraphicsCard.java
	public final static String ADD_IMPLEMENTS = "5e3d9e0945a537ba3f0da0ffbc1af4a154e2e97a";
	//removed 'implements IDisplay' in GraphicsCard.java
	public final static String REMOVE_IMPLEMENTS = "5a583690df0afe76d292813c406e408ffb13bbed";

	
	//Branch and commits for testing fine grained changes in an integrated area for creation, rename and removing field
	//****************************************************************************************************************
	public final static String CREATE_DELETE_FIELD_BRANCH_NAME = "integratedArea_fineGrained_fieldCreateDelete";

	//added 'import eu.fpetersen.cbs.pc.display.IDisplay;' in GraphicsCard.java
	public final static String ADD_IMPORT_FOR_CREATE_DELETE_FILED = "b0f43b11d987cb8921d17b22a3ecb44c2677ec9c";
	//added field 'IDisplay field' in GraphicsCard.java	
	public final static String ADD_FIELD_FOR_CREATE_DELETE_FILED = "c61e21b38e7ac50e741c6b801a682fd9d3b9bc41";
	//renamed field 'IDisplay field' to 'IDisplay fieldRenamed' in GraphicsCard.java
	public final static String RENAME_FIELD_FOR_CREATE_DELETE_FILED = "359ae2f1272abcf339e104862ae996f103f3406c";
	//removed field fieldRenamed in GraphicsCard.java
	public final static String REMOVE_CREATED_FIELD_FOR_CREATE_DELETE_FILED = "8bab5d46af07279cab7e12cfe6bf86c12f614f05";
	//removed field 'private IGraphicsCard graphicsCard' in Display.java
	public final static String REMOVE_INTEGRATED_FIELD_FOR_CREATE_DELETE_FILED = "07879d9e9116b88d27735a1e5262ba32a15dd67d";
	
	
	//Branch and commits for testing changes in an integrated area on non-java files
	//****************************************************************************************************************
	public final static String NON_JAVA_FILES_BRANCH_NAME = "integratedArea_fineGrained_nonJavaFiles";
	
	//created folder 'nonJavaFilesFolder' with 'nonJavaFile.txt'
	public final static String ADD_FOLDER_AND_FILE = "95e5a989d71bc404d44ded14ec50adc92ae9d73b";
	//renamed file nonJavaFile.txt to nonJavaFileRenamed.txt
	public final static String RENAME_FILE = "04843ee3073a53bfd23cc21066e68197f90b0f30";
	//changed content in nonJavaFileRenamed.txt
	public final static String CHANGE_FILE_CONTENT = "f71a8c27b942f5d6c39d77ceb8ca7b691861a2ee";
	//copied file nonJavaFileRenamed.txt and pasted it into src folder
	public final static String COPY_FILE = "1bae1a57b9956f51aba40bcdc2126a51993df11a";
	//deleted file nonJavaFileRenamed.txt in src folder
	public final static String REMOVE_FILE = "de7193b323f53e50202cbfb023c6a480c91b31b4";
	

	//Branch and commits for testing fine grained changes in an integrated area on a class header
	//****************************************************************************************************************
	public final static String CLASS_HEADER_BRANCH_NAME = "integratedArea_fineGrained_classHeader";
	
	//added 'abstract' to the class 'Display.java'
	public final static String ADD_ABSTRACT_TO_CLASS = "399fd6ef0f004f65b85b32bf6cc432aa998debae";
	//changed 'abstract' to 'final' for the class 'Display.java'
	public final static String CHANGE_ABSTRACT_TO_FINAL_IN_CLASS = "4775adf7314647e77f88afa103fcbc46c2f42b87";
	//renamed 'GraphicsCard.java' to 'GraphicsCardRenamed.java'
	public final static String RENAME_CLASS = "77d134e667e2cff5f98a52f2427b8b45ff6b2659";


	//Branch and commits for testing creation and deleting a package in an integrated area
	//****************************************************************************************************************
	public final static String PACKAGE_CREATE_DELETE_BRANCH_NAME = "integratedArea_fineGrained_packageCreateDelete";

	//added package eu.fpetersen.cbs.pc.newPackage
	public final static String ADD_PACKAGE = "cb405004c513d23c63b98eee00503a0dac77d01d";
	//renamed package 'eu.fpetersen.cbs.pc.newPackage' to 'eu.fpetersen.cbs.pc.newPackageRenamed'
	public final static String RENAME_ADDED_PACKAGE = "4fc66a4186eaf66e696b4340144346bf9ab9fcdd";
	//removed package eu.fpetersen.cbs.pc.newPackageRenamed
	public final static String REMOVE_PACKAGE = "14fda1ddcd53a5a50c457699b269aca8de69c4e0";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on extends
	//****************************************************************************************************************
	public final static String EXTENDS_BRANCH_NAME = "integratedArea_fineGrained_extends";

	//added 'import eu.fpetersen.cbs.pc.display.Display;' in GraphicsCard.java
	public final static String ADD_IMPORT_FOR_EXTENDS= "3aa6c2c6081282df67e5fc6c8d8c7dba5d01b775";
	//added 'extends Display' to GraphicsCard.java
	public final static String ADD_EXTENDS = "363e923e34b9166e682da1710c689a3d4cd401db";
	//removed 'extends Display' from GraphicsCard.java
	public final static String REMOVE_EXTENDS = "60144aac7ae9f0bd0d3ade5f374c0339073d0bfc";

	
	//Branch and commits for testing fine grained changes in an integrated area on implements
	//****************************************************************************************************************
	public final static String RENAMES_BRANCH_NAME = "integratedArea_fineGrained_renames";
	//TODO
}
