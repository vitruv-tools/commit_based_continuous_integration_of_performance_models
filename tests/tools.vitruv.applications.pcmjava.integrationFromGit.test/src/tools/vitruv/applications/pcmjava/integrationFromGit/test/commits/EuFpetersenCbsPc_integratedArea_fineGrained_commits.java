package tools.vitruv.applications.pcmjava.integrationFromGit.test.commits;

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
	
//Changed ^
	
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

//Changed ^
	
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
	//changed field type of fieldRenamed from IDisplay to Frame in GraphicsCard.java
	public final static String CHANGE_FIELD_TYPE = "ba0497e7be5ce6f36cf51ea201e06db30b0b97e3";
	//removed field fieldRenamed in GraphicsCard.java
	public final static String REMOVE_FIELD = "a48c5db642a597f9c5c8970a3d1dfa44e814a63f";
	
//Changed ^
	
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
	
//Changed ^	
	
	//Branch and commits for testing fine grained changes in an integrated area on class annotation
	//****************************************************************************************************************
	public final static String CLASS_ANNOTATION_BRANCH_NAME = "integratedArea_fineGrained_classAnnotation";

	//added class annotation to Display.java
	public final static String ADD_CLASS_ANNOTATION= "1c05e30f6140bbadebc01298f9b8a0930a4798d6";
	//changed class annotation to Display.java
	public final static String CHANGE_CLASS_ANNOTATION = "9053f8ca6074e6d3adcaf63ab280501eebcbaece";
	//removed class annotation in Display.java
	public final static String REMOVE_CLASS_ANNOTATION = "25c5d6031e6479480ccba6392daf43ff5cd7bccb";
	
//Changed ^		
	

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
	
	

//Changed ^		
	
	
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
