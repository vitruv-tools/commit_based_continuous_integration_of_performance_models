package tools.vitruv.applications.pcmjava.integrationFromGit.test.commits;

public abstract class EuFpetersenCbsPc_integratedArea_fineGrained_commits {
	
	//initial commit from master branch	
	public final static String INIT = "9fdb7d20a919cfa88fb51db47a20657073dfce92";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on a method header
	//****************************************************************************************************************
	public final static String METHOD_HEADER_BRANCH_NAME = "integratedArea_fineGrained_methodHeader";

	//added 'public void newMethod() {}' in Frame.java	
	public final static String ADD_METHOD = "85848d333c6e3de7c439044691c0ef3620d824c6";
	//renamed method 'newMethod' to 'newMethodRenamed' in Frame.java	
	public final static String RENAME_METHOD = "9154264cab20daab691a3a1f5e2b916c4c315ce7";
	//changed modifier for method 'newMethodRenamed' from public to private in Frame.java
	public final static String CHANGE_MODIFIER_METHOD = "1fe0584b94437eb92c1797b6989f90359fa11f04";
	//added method parameter 'int methodParameter' in method 'newMethodRenamed' in Frame.java	
	public final static String ADD_PARAMETER_METHOD = "7ca90e436b51ae917e95c1a81b3ce033f636d7a6";
	//changed method parameter type from int to String in method 'newMethodRenamed' in Frame.java
	public final static String CHANGE_PARAMETER_METHOD = "cd64371959d9a9c492f2c8effcc5cd1fe0cb73fd";
	//removed method newMethodRenamed in Frame.java	
	public final static String REMOVE_METHOD = "bd3f955740cbd0de85960032ef1cb8c9276febe5";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on a class field
	//****************************************************************************************************************
	public final static String FIELD_BRANCH_NAME = "integratedArea_fineGrained_field";

	//added field  'String field' in Frame.java
	public final static String ADD_FIELD = "f3fbcb3fdad037f4e236bc75249c1c2d61be76f3";
	//renamed field from 'field' to 'fieldRenamed' in Frame.java	
	public final static String RENAME_FIELD = "d661cdc747f42524031dcdca02b6d37585890d73";
	//added field modifier 'public' to 'fieldRenamed' in Frame.java
	public final static String ADD_FIELD_MODIFIER = "b9ec3f3e7dd334b3080f9b3975cef083481ccb60";
	//changed field modifier from 'public' to 'private' for 'fieldRenamed' in Frame.java
	public final static String CHANGE_FIELD_MODIFIER = "77a8043041d7aac743ffc07b61122c10f5c5d0c5";
	//changed field type from String to int for fieldRenamed in Frame.java
	public final static String CHANGE_FIELD_TYPE_TO_INT = "b79056621eb8106114214008b15ac47990b5dd11";
	//added import 'eu.fpetersen.cbs.pc.display.Display' in Frame.java
	public final static String ADD_IMPORT_FOR_FILED = "745256f757e7a35d1c4787ef1ace9a5d6f225166";
	//changed field type from int to Display for fieldRenamed in Frame.java
	public final static String CHANGE_FIELD_TYPE_TO_DISPLAY = "f628ce8f71b51edff133743426ada3fd1ce58e3f";
	//removed field 'fieldRenamed' in Frame.java
	public final static String REMOVE_FIELD = "4f548ba54414aa440a57ecd70f0378e207c74907";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on a method implementation
	//****************************************************************************************************************
	public final static String METHOD_IMPLEMENTATION_BRANCH_NAME = "integratedArea_fineGrained_methodImplementation";

	//added import 'eu.fpetersen.cbs.pc.graphics.GraphicsCard' in Frame.java
	public final static String  ADD_IMPORT_FOR_METHOD_IMPL= "f7e1973a9a0454a5b30a11a441f117ad8387fbdb";
	//added method 'newMethod' in Frame.java	
	public final static String ADD_METHOD_FOR_METHOD_IMPL = "f579f8fddf9cb5836f8844421ff6cf6bab646e34";
	//added external call in method newMethod in Frame.java
	public final static String ADD_EXTERNAL_CALL = "da8aac0e75b5f7923bda417e871d12ca5b342d79";
	//added internal action in method newMethod in Frame.java
	public final static String ADD_INTERNAL_ACTION = "a4207f4db9a124187c48a2159eabedc0373744ae";
	//added for-loop with external action in method newMethod in Frame.java
	public final static String ADD_FOR = "90d48ad4c4651299829166790d0ab883d7cd1d43";
	//added if-else-branch with one internal action and one external call in method newMethod in Frame.java
	public final static String ADD_IF_ELSE = "87197b9d57fdcbd226b896d7791871365ea0f25c";
	//removed if-else-branch with one internal action and one external call in method newMethod in Frame.java
	public final static String REMOVE_IF_ELSE = "ac1c3b6f7fb1fe1263d3a54c13587332acd992eb";
	//removed for-loop with external action in method newMethod in Frame.java
	public final static String REMOVE_FOR = "5edd04c2e4a8d20dfbeb59bee2604a03d16312f9";
	//removed internal action in method newMethod in Frame.java
	public final static String REMOVE_INTERNAL_ACTION = "6b97ef13c3d4ed6cff23ebf3c475d5e5dd39fc0d";
	//removed external call in method newMethod in Frame.java
	public final static String REMOVE_EXTERNAL_CALL = "58bc2e9a3b84d3cd8200348389e5a666495896f1";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on class annotation
	//****************************************************************************************************************
	public final static String CLASS_ANNOTATION_BRANCH_NAME = "integratedArea_fineGrained_classAnnotation";

	//added class annotation
	public final static String ADD_CLASS_ANNOTATION= "f5f9b8486ee6c88353b0d5ce32070e63018d16d8";
	//changed class annotation
	public final static String CHANGE_CLASS_ANNOTATION = "973cf0509732d815d4b737fe39f6f081e0a11080";
	//removed class annotation
	public final static String REMOVE_CLASS_ANNOTATION = "8732dc9cfdb3a9914d3178f922bbaf5a65e49404";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on implements
	//****************************************************************************************************************
	public final static String IMPLEMENTS_BRANCH_NAME = "integratedArea_fineGrained_implements";

	//added import 'eu.fpetersen.cbs.pc.display.IDisplay' in Frame.java
	public final static String ADD_IMPORT_FOR_IMPLEMENTS= "2d10a7558370e611fd64da067d3e5394f73b2b2f";
	//added implements IDisplay and method in Frame.java
	public final static String ADD_IMPLEMENTS_AND_METHOD = "2e2a2d3333afb951cf91dbb2d2e9b7b3f4c2bfc1";
	//removed implements and method in Frame.java
	public final static String REMOVE_IMPLEMENTS_AND_METHOD = "bed6ba5734789417ef1b10eebc02c6e3f9f637c2";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on extends
	//****************************************************************************************************************
	public final static String EXTENDS_BRANCH_NAME = "integratedArea_fineGrained_extends";

	//add import 'eu.fpetersen.cbs.pc.graphics.GraphicsCard' in Frame.java
	public final static String ADD_IMPORT_FOR_EXTENDS= "0fca674c6a6ea8f68648df6c9e0bc0fd67f23547";
	//added 'extends GraphicsCard' in Frame.java
	public final static String ADD_EXTENDS = "36b55eb90feacd39b26bb210079b51ff3472b18c";
	//removed 'extends GraphicsCard' in Frame.java
	public final static String REMOVE_EXTENDS = "9c0b29be22d64713421f61099b47acc8322f9d8b";
	
	
	//Branch and commits for testing fine grained changes in an integrated area on a class header
	//****************************************************************************************************************
	public final static String CLASS_HEADER_BRANCH_NAME = "integratedArea_fineGrained_classHeader";

	//renamed class Frame to FrameRenamed
	public final static String RENAME_CLASS = "f018ed06c1a161e9ce469e314d131b1576b400ab";
	//added abstract modifier to class FrameRenamed
	public final static String ADD_ABSTRACT_TO_CLASS = "9ba8eaf819db85d898af4a48da71c9a4ebfc3853";
	//changed abstract modifier to final in class FrameRenamed
	public final static String CHANGE_ABSTRACT_TO_FINAL_IN_CLASS = "46133716a15a8ccb5862791adc7a63974561d2dc";
	
	
	//Branch and commits for testing creation and deleting a class in an integrated area
	//****************************************************************************************************************
	public final static String CLASS_CREATE_DELETE_BRANCH_NAME = "integratedArea_fineGrained_classCreateDelete";

	//added NewClass.java in package eu.fpetersen.cbs.pc.data
	public final static String ADD_CLASS = "a72a03ab3c4235951a70e03c1fceee6f92b50aec";
	//renamed NewClass to NewClassRenamed
	public final static String RENAME_ADDED_CLASS = "55cf8449cdaea7760ce974b5999adb7463a28d45";
	//removed class NewClassRenamed.java
	public final static String REMOVE_CLASS = "18b76f5341c8751c605a1acac72cc83c2302a462";
	
	
	//Branch and commits for testing creation and deleting a package in an integrated area
	//****************************************************************************************************************
	public final static String PACKAGE_CREATE_DELETE_BRANCH_NAME = "integratedArea_fineGrained_packageCreateDelete";

	//added package eu.fpetersen.cbs.pc.newPackage
	public final static String ADD_PACKAGE = "cb405004c513d23c63b98eee00503a0dac77d01d";
	//renamed package 'eu.fpetersen.cbs.pc.newPackage' to 'eu.fpetersen.cbs.pc.newPackageRenamed'
	public final static String RENAME_ADDED_PACKAGE = "4fc66a4186eaf66e696b4340144346bf9ab9fcdd";
	//removed package eu.fpetersen.cbs.pc.newPackageRenamed
	public final static String REMOVE_PACKAGE = "14fda1ddcd53a5a50c457699b269aca8de69c4e0";
	
}
