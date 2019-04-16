package tools.vitruv.application.pcmjava.modelrefinement.monitoring.primitiveattributeextraction;

import java.util.Map;

public interface PrimitiveAttributeExtracter {
	Map<String, Object> getObjectPrimitiveAttributeValues(Object[] listObjects, int depth);
}
