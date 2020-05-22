package tools.vitruv.applications.pcmjava.integrationFromGit.test.commits;

public abstract class EuFpetersenCbsPcCommits {
	
	public final static String BRANCH_NAME = "nonIntegratedArea_ChangeClassTest";
	
	
	//initial commit
	public final static String INIT = "9fdb7d20a919cfa88fb51db47a20657073dfce92";
	//created nonIntegratedPackage in src folder
	public final static String ADD_NON_INTEGRATED_PACKAGE = "903740daaf041e4713eff026e2f031cd5ffe4602";
	//created packages contracts and datatypes in nonIntegratedPackage
	public final static String ADD_CONTRACTS_DATATYPES = "bd52d081cdf49efb1f2487d8559cc512e2c8f78f";
	//created package FirstClass in nonIntegratedPackage
	public final static String ADD_FIRST_CLASS_PACKAGE = "db466e3de429ee686b45d894c05f426536c8c2ea";
	//created FirstClassImpl.java in package FirstClass
	public final static String ADD_FIRST_CLASS_IMPL = "9a921b4d64d749f36d5e8508aaa28150ca7fc46e";
	
	
	//added class annotation in FirstClassImpl
	public final static String ADD_CLASS_ANNOTATION = "129937e30d0e0bedab7912a52ef268e7b58d521b";
	//changed class annotation
	public final static String CHANGE_CLASS_ANNOTATION = "2666f83f722be7f1bd621146a2575185882360cd";
	//removed class annotation
	public final static String REMOVE_CLASS_ANNOTATION = "ea951df52439151061a0385da468d79e91d5f012";
	
	
	//added import statement in FirstClassImpl
	public final static String ADD_IMPORT = "a2c455c49a8531179549a489bc6d1a5184c8f0d6";
	//changed import statement in FirstClassImpl
	public final static String CHANGE_IMPORT = "5d39543bb01a572a9cbb635ce24ea1d71b566e80";
	//removed import statement in FirstClassImpl
	public final static String REMOVE_IMPORT = "4f39900dbb805f79b8a2383bcb01892039c1bbf4";
	
	
	//removed class modifier 'public' in FirstClassImpl
	public final static String REMOVE_PUBLIC = "08dabadc8eeb24154eb2f72867eae19bf808b876";
	//added class modifier 'final' in FirstClassImpl
	public final static String ADD_FINAL = "cf3a99ee46cfc847060d04fe75a1d8dc32d6daad";
	//changed class modifier 'final' to 'abstract' in FirstClassImpl
	public final static String CHANGE_FINAL_TO_ABSTRACT = "d9b666cfaff952f7df2460c473d1a7ff42e6e7a1";
	//changed class modifier 'abstract' to 'public' in FirstClassImpl
	public final static String CHANGE_ABSTRACT_TO_PUBLIC = "96e8bd2cfd78b25c5faed783dcc309888144cbc9";
	
	
	//added 'import eu.fpetersen.cbs.pc.data.Frame' in FirstClassImpl.java
	public final static String ADD_FIRST_IMPORT_FOR_EXTENDS = "73ce2224095c74cbafe5fd1ec1e05540e7b2ac04";
	//added 'extends Frame' in FirstClassImpl
	public final static String ADD_EXTENDS = "19b2a5c7f53d1234d0b62cd99384beff4f2a33ea";
	//added 'import eu.fpetersen.cbs.pc.graphics.GraphicsCard' in FirstClassImpl.java
	public final static String ADD_SECOND_IMPORT_FOR_EXTENDS = "df9955f38461e2ccc29e972ca1c87b20ea8d07c1";
	//changed 'extends Frame' to 'extends GraphicsCard' in FirstClassImpl.java
	public final static String CHANGE_EXTENDS = "5f297c5e661d074b295ee23084aa1ae09547120c";
	//removed 'extends GraphicsCard' in FirstClassImpl.java
	public final static String REMOVE_EXTENDS = "e556255e3d0b6260b6deedc658d6cbc7f03f08f5";
	//removed 'import eu.fpetersen.cbs.pc.graphics.GraphicsCard;' in FirstClassImpl.java
	public final static String REMOVE_SECOND_IMPORT_FOR_EXTENDS = "a4177b058176e4e6607d83fe2c9913a10dd4cec9";
	//removed 'import eu.fpetersen.cbs.pc.data.Frame;' in FirstClassImpl.java
	public final static String REMOVE_FIRST_IMPORT_FOR_EXTENDS = "bf92263945bf6c10b8c87bc4a66bb7229ed1decb";
	
	//added FirstInterface.java in NonIntegratedPackage.contracts
	public final static String ADD_INTERFACE_FOR_IMPLEMENTS = "be1c282154b1267b6b183da9c35840746fa4f3c1";
	//added  'void firstMethodInFirstInterface()' in FirstInterface.java
	public final static String ADD_METHOD_IN_INTERFACE_FOR_IMPLEMENTS = "93dfc6f1b100f128e22308e39414e849c74f5c26";
	//added 'import nonIntegratedPackage.contracts.FirstInterface;' in FirstClassImpl.java
	public final static String ADD_FIRST_IMPORT_FOR_IMPLEMENTS = "0fe67a0a2d25850a1d122b6cd00ccf9af212dcd0";
	//added 'implements FirstInterface' and 'public void firstMethodInFirstInterface() {}' in FirstClassImpl.java
	public final static String ADD_IMPLEMENTS_AND_METHOD = "57e7c95ed0c936b353b50c869e2e2309a656f461";

}
