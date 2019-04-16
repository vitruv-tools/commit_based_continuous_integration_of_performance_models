package tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.records;


import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.IValueDeserializer;

/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public final class ServiceCallRecordFactory implements IRecordFactory<ServiceCallRecord> {
	
	
	@Override
	public ServiceCallRecord create(final IValueDeserializer deserializer) throws RecordInstantiationException {
		return new ServiceCallRecord(deserializer);
	}
	
	@Override
	@Deprecated
	public ServiceCallRecord create(final Object[] values) {
		return new ServiceCallRecord(values);
	}
	
	public int getRecordSizeInBytes() {
		return ServiceCallRecord.SIZE;
	}
}
