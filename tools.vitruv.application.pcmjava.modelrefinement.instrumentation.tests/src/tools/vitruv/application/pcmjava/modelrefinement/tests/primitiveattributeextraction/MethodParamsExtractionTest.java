package tools.vitruv.application.pcmjava.modelrefinement.tests.primitiveattributeextraction;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import tools.vitruv.application.pcmjava.modelrefinement.monitoring.factory.ServiceParameters;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.factory.ServiceParametersFactory;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.factory.ServiceParametersFactoryImp;

public class MethodParamsExtractionTest {

	@Test
	public void methodPramsExtractionTest() {
		int[] arrParam =  new int[1];
		List<String> collectionParam = new ArrayList<String>();
		int primitiveParam = 1;
		Map<String, String> mapParam =  new HashMap<String, String>();
		
		Object[] listParams = new Object[] {arrParam, collectionParam, primitiveParam, mapParam};
		String[] listParamsNames = new String[] {"arrParam", "collectionParam", "primitiveParam", "mapParam"};
		
		ServiceParametersFactory serviceParametersFactory = new ServiceParametersFactoryImp();
		ServiceParameters serviceParameters = 
				serviceParametersFactory.getServiceParameters(listParams, listParamsNames);
		
		String expectedResult = "{\"mapParam\":0,\"collectionParam\":0,\"arrParam\":1,\"primitiveParam\":1,}";
		
		assertEquals(serviceParameters.toString(), expectedResult);
		
	}
	
}
