package tools.vitruv.application.pcmjava.modelrefinement.monitoring.record;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.util.registry.IRegistry;

public class InternalActionRecord  extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4594491630478837144L;


	public static final int SIZE = (4 * TYPE_SIZE_STRING) + (2 * TYPE_SIZE_LONG);
	
	
	public static final Class<?>[] TYPES = {
			String.class, // RecordWithSession.sessionId
			String.class, // RecordWithSession.serviceExecutionId
			String.class, // ResponseTimeRecord.internalActionId
			String.class, // ResponseTimeRecord.resourceId
			long.class, // ResponseTimeRecord.startTime
			long.class, // ResponseTimeRecord.stopTime
		};
	
		
	/** default constants. */
	public static final String SESSION_ID = "<not set>";
	public static final String SERVICE_EXECUTION_ID = "<not set>";
	public static final String INTERNAL_ACTION_ID = "<not set>";
	public static final String RESOURCE_ID = "<not set>";
	
	
	private final String sessionId;
	private final String serviceExecutionId;
	private final String internalActionId;
	private final String resourceId;
	private final long startTime;
	private final long stopTime;
	
	
	public InternalActionRecord(String sessionId, String serviceExecutionId, String internalActionId, String resourceId,
			long startTime, long stopTime) {
		super();
		this.sessionId = sessionId == null ? SESSION_ID : sessionId;
		this.serviceExecutionId = serviceExecutionId == null ? SERVICE_EXECUTION_ID : serviceExecutionId;
		this.internalActionId = internalActionId == null ? INTERNAL_ACTION_ID : internalActionId;
		this.resourceId = resourceId == null ? RESOURCE_ID : resourceId;
		this.startTime = startTime;
		this.stopTime = stopTime;
	}
	
	
	@Deprecated
	public InternalActionRecord(final Object[] values) {
		AbstractMonitoringRecord.checkArray(values, TYPES);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.internalActionId = (String) values[2];
		this.resourceId = (String) values[3];
		this.startTime = (Long) values[4];
		this.stopTime = (Long) values[5];
	}
	
	
	@Deprecated
	protected InternalActionRecord(final Object[] values, final Class<?>[] valueTypes) {
		AbstractMonitoringRecord.checkArray(values, valueTypes);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.internalActionId = (String) values[2];
		this.resourceId = (String) values[3];
		this.startTime = (Long) values[4];
		this.stopTime = (Long) values[5];
	}
	

	public InternalActionRecord(final ByteBuffer buffer, final IRegistry<String> stringRegistry) {
		this.sessionId = stringRegistry.get(buffer.getInt());
		this.serviceExecutionId = stringRegistry.get(buffer.getInt());
		this.internalActionId = stringRegistry.get(buffer.getInt());
		this.resourceId = stringRegistry.get(buffer.getInt());
		this.startTime = buffer.getLong();
		this.stopTime = buffer.getLong();
	}
	
	
	public Object[] toArray() {
		return new Object[] { 
				this.getSessionId(),
				this.getServiceExecutionId(),
				this.getInternalActionId(),
				this.getResourceId(),
				this.getStartTime(),
				this.getStopTime(),
				};
	}
	
	
	public void writeBytes(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferOverflowException {
		buffer.putInt(stringRegistry.get(this.getSessionId()));
		buffer.putInt(stringRegistry.get(this.getServiceExecutionId()));
		buffer.putInt(stringRegistry.get(this.getInternalActionId()));
		buffer.putInt(stringRegistry.get(this.getResourceId()));
		buffer.putLong(this.getStartTime());
		buffer.putLong(this.getStopTime());
		
	}
	
	

	 @Deprecated
    // Will not be used because the record implements IMonitoringRecord.Factory
	public void initFromArray(Object[] arg0) {
		
	}


	@Deprecated
	// Will not be used because the record implements IMonitoringRecord.BinaryFactory
	public void initFromBytes(ByteBuffer arg0, IRegistry<String> arg1) throws BufferUnderflowException {
		
	}

	
	public int getSize() {
		return SIZE;
	}


	public Class<?>[] getValueTypes() {
		return InternalActionRecord.TYPES;
	}


	public String getSessionId() {
		return sessionId;
	}


	public String getServiceExecutionId() {
		return serviceExecutionId;
	}


	public String getInternalActionId() {
		return internalActionId;
	}


	public String getResourceId() {
		return resourceId;
	}


	public long getStartTime() {
		return startTime;
	}


	public long getStopTime() {
		return stopTime;
	}
	
	

	
	
	
}
