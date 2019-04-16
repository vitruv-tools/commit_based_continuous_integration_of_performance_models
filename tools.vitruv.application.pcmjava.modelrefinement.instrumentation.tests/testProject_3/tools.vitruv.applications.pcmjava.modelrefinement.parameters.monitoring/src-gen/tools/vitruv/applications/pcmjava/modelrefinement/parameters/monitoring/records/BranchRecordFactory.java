package tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.records;


import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.IValueDeserializer;

/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public final class BranchRecordFactory implements IRecordFactory<BranchRecord> {
	
	
	@Override
	public BranchRecord create(final IValueDeserializer deserializer) throws RecordInstantiationException {
		return new BranchRecord(deserializer);
	}
	
	@Override
	@Deprecated
	public BranchRecord create(final Object[] values) {
		return new BranchRecord(values);
	}
	
	public int getRecordSizeInBytes() {
		return BranchRecord.SIZE;
	}
}
