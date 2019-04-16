package test2;

public class Test {
	
	public static void main(String[] args) {
		System.out.println("Starting......\n");
		
		String outputCode = "public void httpDownload() {\r\n" + 
				"		ServiceParamtersFactory serviceParamtersFactory = new ServiceParametersFactoryImp();\r\n" + 
				"		ServiceParameters __serviceParameters_0dfjdfdjf = serviceParamtersFactory\r\n" + 
				"				.getServiceParameters(new Object[] {});\r\n\n" + 
				"		ThreadMonitoringController.getInstance().enterService(\"_9oyHcBd4EemTto81V-t5Ag\",\r\n" + 
				"				__serviceParameters_0dfjdfdjf);\r\n" + 
				"		final long __tin_0fhdjhfjds = ThreadMonitoringController.getInstance().getTime();\r\n" + 
				"		final int i = 9;\r\n" + 
				"		ThreadMonitoringController.getInstance().logResponseTime(\"_-UiaMBd4EemTto81V-t5Ag\", \"_-UYCIBd4EemTto81V-t5Ag\",\r\n" + 
				"				__tin_0fhdjhfjds);\r\n" + 
				"		ThreadMonitoringController.getInstance().exitService();\r\n" + 
				"	}";
		
		outputCode = AssertSourceCodeHelper.changeRandomVariableFromInstrumentedCode(outputCode);
		
		String expectedCode = AssertSourceCodeHelper.expectedInternalActionInstrumentation("_-UYCIBd4EemTto81V-t5Ag",
				"_-UiaMBd4EemTto81V-t5Ag", "_9oyHcBd4EemTto81V-t5Ag", "final int i = 9;");
		
		

		if(expectedCode.equals(outputCode)) {
			System.out.println("they are equaly");
		}
		else {
			System.out.println("they are not equal");
		}
		
		
		
		System.out.println("\nEND");
	}

}
