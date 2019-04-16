package tools.vitruv.application.pcmjava.modelrefinement.monitoring.record;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.util.registry.IRegistry;

public class LoopRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2628986149339276656L;


	public static final int SIZE = (1 * TYPE_SIZE_STRING) + (1 * TYPE_SIZE_LONG);
	

	public static final Class<?>[] TYPES = {
			String.class, // RecordWithSession.sessionId
			String.class, // RecordWithSession.serviceExecutionId
			String.class, // LoopRecord.loopId
			long.class, // LoopRecord.loopIterationCount
		};
	
		
	/** default constants. */
	public static final String SESSION_ID = "<not set>";
	public static final String SERVICE_EXECUTION_ID = "<not set>";
	public static final String LOOP_ID = "<not set>";

	
	// attributes that will store the customized records
	private final String sessionId;
	private final String serviceExecutionId;
	private final String loopId;
	private final long loopIterationCount;
	
	
	public LoopRecord(String sessionId, String serviceExecutionId, String loopId, long loopIterationCount) {
		super();
		this.sessionId = sessionId == null ? SESSION_ID : sessionId;
		this.serviceExecutionId = serviceExecutionId == null ? SERVICE_EXECUTION_ID : serviceExecutionId;
		this.loopId = loopId == null ? LOOP_ID : loopId;
		this.loopIterationCount = loopIterationCount;
	}
	
	
	public LoopRecord(final Object[] values) {
    	AbstractMonitoringRecord.checkArray(values, LoopRecord.TYPES);
    	
    	this.sessionId = (String) values[0];
    	this.serviceExecutionId = (String) values[1];
    	this.loopId = (String) values[2];
    	this.loopIterationCount = (Long) values[3];
    }
	
	
	public LoopRecord(final ByteBuffer buffer, final IRegistry<String> stringRegistry) {
    	this.sessionId = stringRegistry.get(buffer.getInt());
    	this.serviceExecutionId = stringRegistry.get(buffer.getInt());
    	this.loopId = stringRegistry.get(buffer.getInt());
    	this.loopIterationCount = buffer.getLong();;
    }
	
	
	@Deprecated
    // Will not be used because the record implements IMonitoringRecord.Factory
	public void initFromArray(Object[] arg0) {
		
	}


	@Deprecated
	// Will not be used because the record implements IMonitoringRecord.BinaryFactory
	public void initFromBytes(ByteBuffer arg0, IRegistry<String> arg1) throws BufferUnderflowException {
		
	}
	
	
	public Object[] toArray() {
		return new Object[] { 
				this.getSessionId(),
				this.getServiceExecutionId(),
				this.getLoopId(),
				this.getLoopIterationCount(),
				};
	}
	
	
	public void writeBytes(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferOverflowException {
		buffer.putInt(stringRegistry.get(this.getSessionId()));
		buffer.putInt(stringRegistry.get(this.getServiceExecutionId()));
		buffer.putInt(stringRegistry.get(this.getLoopId()));
		buffer.putLong(this.getLoopIterationCount());		
	}



	public int getSize() {
		return SIZE;
	}

	@Override
	public Class<?>[] getValueTypes() {
		// TODO Auto-generated method stub
		return LoopRecord.TYPES;
	}
	


	public String getSessionId() {
		return sessionId;
	}


	public String getServiceExecutionId() {
		return serviceExecutionId;
	}


	public String getLoopId() {
		return loopId;
	}


	public long getLoopIterationCount() {
		return loopIterationCount;
	}
	

}
