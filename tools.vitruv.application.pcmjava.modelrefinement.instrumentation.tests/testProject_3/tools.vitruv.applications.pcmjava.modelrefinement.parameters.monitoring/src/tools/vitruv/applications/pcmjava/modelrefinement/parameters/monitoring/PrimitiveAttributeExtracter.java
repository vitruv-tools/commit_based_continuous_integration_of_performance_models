package tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring;

import java.util.Map;

public interface PrimitiveAttributeExtracter {
	Map<String, Object> getObjectPrimitiveAttributeValues(Object[] listObjects, int depth);
}
