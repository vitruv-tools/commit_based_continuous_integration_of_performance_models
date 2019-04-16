package tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.records;


import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.IValueDeserializer;

/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public final class LoopRecordFactory implements IRecordFactory<LoopRecord> {
	
	
	@Override
	public LoopRecord create(final IValueDeserializer deserializer) throws RecordInstantiationException {
		return new LoopRecord(deserializer);
	}
	
	@Override
	@Deprecated
	public LoopRecord create(final Object[] values) {
		return new LoopRecord(values);
	}
	
	public int getRecordSizeInBytes() {
		return LoopRecord.SIZE;
	}
}
