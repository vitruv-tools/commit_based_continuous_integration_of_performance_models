package tools.vitruv.applications.pcmjava.modelrefinement.tests.instrumentation;

public class FineGrainedMonitoringSourceCode {

	public static final String sequentialSearchCode = "try {\r\n" + 
			"			 for (int i = 0; i<arr.length; i++) {\r\n" + 
			"		            if (arr[i] == value){\r\n" + 
			"		                return true;\r\n" + 
			"		            }\r\n" + 
			"		        }\r\n" + 
			"		        return false;\r\n" + 
			"		}\r\n" + 
			"		finally{\r\n" + 
			"			\r\n" + 
			"		}";
	
	
	public static final String binarySearchCode = "try {\r\n" + 
			"			if (arr.length == 0) {\r\n" + 
			"	            return false;\r\n" + 
			"	        }\r\n" + 
			"	        int low = 0;\r\n" + 
			"	        int high = arr.length-1;\r\n" + 
			"\r\n" + 
			"	        while(low <= high ) {\r\n" + 
			"	            int middle = (low+high) /2;\r\n" + 
			"	            if (value > arr[middle] ){\r\n" + 
			"	                low = middle +1;\r\n" + 
			"	            } else if (value < arr[middle]){\r\n" + 
			"	                high = middle -1;\r\n" + 
			"	            } else { \r\n" + 
			"	                return true;\r\n" + 
			"	            }\r\n" + 
			"	        }\r\n" + 
			"	        return false;\r\n" + 
			"		}\r\n" + 
			"		finally {\r\n" + 
			"			\r\n" + 
			"		}";
	
	
	public static final String  guiSquentialSearchCode = "	String searchResult = \"\";\r\n" + 
            "		if (values.length != 0) {\r\n" + 
            "			for (int i = 0; i < values.length; i++) {\r\n" + 
            "				boolean isFound = " + FineGrainedMonitoringTestBase.getExternalCallSequentialSearch() + "(arr, values[i]);\r\n" + 
            "				if (isFound) {\r\n" + 
            "					searchResult = addValue(searchResult, values[i]);\r\n" + 
            "				}\r\n" + 
            "			}\r\n" + 
            "		} " +
			"		return searchResult;";
	
	
	public static final String guiBinarySearchCode = "	String searchResult = \"\";\r\n" + 
            "		if (values.length != 0) {\r\n" + 
            "			for (int i = 0; i < values.length; i++) {\r\n" + 
            "				boolean isFound = " + FineGrainedMonitoringTestBase.getExternalCallBinarySearch() + "(arr, values[i]);\r\n" + 
            "				if (isFound) {\r\n" + 
            "					searchResult = addValue(searchResult, values[i]);\r\n" + 
            "				}\r\n" + 
            "			}\r\n" + 
            "		} " +
			"		return searchResult;";
	
	
	public static final String interanlMethodName = "addValue";
	public static final String interanlMethodHeader = "private String addValue(String searchResult, int value) {\n}";
	public static final String internalMethodContent =" return searchResult + \", \" + value;\r\n";
	
	
}
