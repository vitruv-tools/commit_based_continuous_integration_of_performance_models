package tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.records;


import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.IValueDeserializer;

/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public final class ResourceUtilizationRecordFactory implements IRecordFactory<ResourceUtilizationRecord> {
	
	
	@Override
	public ResourceUtilizationRecord create(final IValueDeserializer deserializer) throws RecordInstantiationException {
		return new ResourceUtilizationRecord(deserializer);
	}
	
	@Override
	@Deprecated
	public ResourceUtilizationRecord create(final Object[] values) {
		return new ResourceUtilizationRecord(values);
	}
	
	public int getRecordSizeInBytes() {
		return ResourceUtilizationRecord.SIZE;
	}
}
