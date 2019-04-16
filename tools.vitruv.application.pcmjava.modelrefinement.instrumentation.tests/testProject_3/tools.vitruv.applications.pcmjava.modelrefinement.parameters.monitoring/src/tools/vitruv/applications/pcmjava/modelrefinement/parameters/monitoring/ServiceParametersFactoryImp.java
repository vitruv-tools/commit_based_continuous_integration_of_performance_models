package tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring;

import java.util.Map;
import java.util.Map.Entry;

public class ServiceParametersFactoryImp implements ServiceParamtersFactory {
    private PrimitiveAttributeExtracter extracter;

    public ServiceParametersFactoryImp() {
        extracter = new PrimitiveAttributeExtracterImp();
    }

    public ServiceParameters getServiceParameters(Object[] listObjects) {
        ServiceParameters serviceParameters = new ServiceParameters();
        Map<String, Object> serviceParametersValues = this.extracter.getObjectPrimitiveAttributeValues(listObjects, 5);
        for (Entry<String, Object> entry : serviceParametersValues.entrySet()) {
            serviceParameters.addValue(entry.getKey().toString(), entry.getValue());
        }
        return serviceParameters;
    }
}
