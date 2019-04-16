package tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.records;


import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.IValueDeserializer;

/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public final class ResponseTimeRecordFactory implements IRecordFactory<ResponseTimeRecord> {
	
	
	@Override
	public ResponseTimeRecord create(final IValueDeserializer deserializer) throws RecordInstantiationException {
		return new ResponseTimeRecord(deserializer);
	}
	
	@Override
	@Deprecated
	public ResponseTimeRecord create(final Object[] values) {
		return new ResponseTimeRecord(values);
	}
	
	public int getRecordSizeInBytes() {
		return ResponseTimeRecord.SIZE;
	}
}
