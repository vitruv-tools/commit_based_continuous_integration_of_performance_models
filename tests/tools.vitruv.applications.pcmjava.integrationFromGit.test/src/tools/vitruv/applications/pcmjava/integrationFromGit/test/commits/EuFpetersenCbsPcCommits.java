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

}
