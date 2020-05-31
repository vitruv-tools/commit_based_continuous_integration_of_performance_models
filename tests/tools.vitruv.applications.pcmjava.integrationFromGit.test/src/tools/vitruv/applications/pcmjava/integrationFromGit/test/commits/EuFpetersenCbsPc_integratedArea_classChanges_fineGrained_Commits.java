package tools.vitruv.applications.pcmjava.integrationFromGit.test.commits;

public abstract class EuFpetersenCbsPc_integratedArea_classChanges_fineGrained_Commits {
	
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
	
	
	
}
