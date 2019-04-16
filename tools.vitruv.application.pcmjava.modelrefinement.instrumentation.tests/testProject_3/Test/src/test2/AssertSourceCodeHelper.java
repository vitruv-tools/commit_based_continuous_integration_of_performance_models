package test2;

import javax.print.attribute.standard.RequestingUserName;

public class AssertSourceCodeHelper {
	public static String expectedInternalActionInstrumentation(String internalActionResourceId, String internalActionID,
			String serviceId, String internalActionBody) {
		String code ="public void httpDownload() {\r\n" + 
				"		ServiceParamtersFactory serviceParamtersFactory = new ServiceParametersFactoryImp();\r\n" + 
				"		ServiceParameters __serviceParameters_0 = serviceParamtersFactory\r\n" + 
				"				.getServiceParameters(new Object[] {});\r\n" + 
				"		ThreadMonitoringController.getInstance().enterService(\"" + serviceId + "\",\r\n" + 
				"				__serviceParameters_0);\r\n" + 
				"		final long __tin_0 = ThreadMonitoringController.getInstance().getTime();\r\n" + 
				"		" + internalActionBody + "\r\n" + 
				"		ThreadMonitoringController.getInstance().logResponseTime(\"" + internalActionID + "\", "
						+ "\"" + internalActionResourceId + "\",\r\n" + 
				"				__tin_0);\r\n" + 
				"		ThreadMonitoringController.getInstance().exitService();\r\n" + 
				"	}";
		
		return code.replaceAll("\n", "");
	}
	
	
	public static String changeRandomVariableFromInstrumentedCode(String instrumentedCode) {
		// start time variable
		String renameTin = renameRandomVariable("__tin", "__tin_0", instrumentedCode);
		
		// serviceParameter variable
		String renameServiceParameters = renameRandomVariable("__serviceParameters", "__serviceParameters_0", renameTin);
		
		// executed branch id
		// ...
		
		// loop execution count
		// ..
		
		return renameServiceParameters.replaceAll("\n", "");
	}
	
	
	private static String renameRandomVariable(String randomVariableNameStart, String newName, String instrumentedCode) {
		for(String codeWord: instrumentedCode.split(" ")) {
			if(codeWord.startsWith(randomVariableNameStart)) {
				instrumentedCode = instrumentedCode.replaceAll(codeWord, newName);
			}
		}
		return instrumentedCode;
	}
	
	
}
