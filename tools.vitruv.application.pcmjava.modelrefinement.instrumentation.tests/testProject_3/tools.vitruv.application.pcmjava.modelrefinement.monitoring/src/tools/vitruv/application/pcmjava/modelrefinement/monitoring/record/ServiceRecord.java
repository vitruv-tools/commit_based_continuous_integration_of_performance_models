package tools.vitruv.application.pcmjava.modelrefinement.monitoring.record;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.util.registry.IRegistry;


public class ServiceRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 817493062157586646L;


	public static final int SIZE = (6 * TYPE_SIZE_STRING) + (2 * TYPE_SIZE_LONG);
	
	
	public static final Class<?>[] TYPES = {
			String.class, // RecordWithSession.sessionId
			String.class, // RecordWithSession.serviceExecutionId
			String.class, // ServiceCallRecord.serviceId
			String.class, // ServiceCallRecord.parameters
			String.class, // ServiceCallRecord.callerServiceExecutionId
			String.class, // ServiceCallRecord.callerId
			long.class, // ServiceCallRecord.startTime
			long.class, // ServiceCallRecord.stopTime
		};
		
	
	/** default constants. */
	public static final String SESSION_ID = "<not set>";
	public static final String SERVICE_EXECUTION_ID = "<not set>";
	public static final String SERVICE_ID = "<not set>";
	public static final String PARAMETERS = "<not set>";
	public static final String CALLER_SERVICE_EXECUTION_ID = "<not set>";
	public static final String CALLER_ID = "<not set>";
	
	
	private final String sessionId;
	private final String serviceExecutionId;
	private final String serviceId;
	private final String parameters;
	private final String callerServiceExecutionId;
	private final String callerId;
	private final long startTime;
	private final long stopTime;
	
	
	public ServiceRecord(String sessionId, String serviceExecutionId, String serviceId, String parameters,
			String callerServiceExecutionId, String callerId, long startTime, long stopTime) {
		super();
		this.sessionId = sessionId == null ? SESSION_ID : sessionId;
		this.serviceExecutionId = serviceExecutionId == null ? SERVICE_EXECUTION_ID : serviceExecutionId;
		this.serviceId = serviceId == null ? SERVICE_ID : serviceId;
		this.parameters = parameters == null ? PARAMETERS : parameters;
		this.callerServiceExecutionId = callerServiceExecutionId == null ? CALLER_SERVICE_EXECUTION_ID : callerServiceExecutionId;
		this.callerId = callerId == null ? CALLER_ID : callerId;
		this.startTime = startTime;
		this.stopTime = stopTime;
	}
	
	
	@Deprecated
	public ServiceRecord(final Object[] values) {
		AbstractMonitoringRecord.checkArray(values, TYPES);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.serviceId = (String) values[2];
		this.parameters = (String) values[3];
		this.callerServiceExecutionId = (String) values[4];
		this.callerId = (String) values[5];
		this.startTime = (Long) values[6];
		this.stopTime = (Long) values[7];
	}
	
	
	@Deprecated
	protected ServiceRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		AbstractMonitoringRecord.checkArray(values, valueTypes);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.serviceId = (String) values[2];
		this.parameters = (String) values[3];
		this.callerServiceExecutionId = (String) values[4];
		this.callerId = (String) values[5];
		this.startTime = (Long) values[6];
		this.stopTime = (Long) values[7];
	}
	
	
	public ServiceRecord(final ByteBuffer buffer, final IRegistry<String> stringRegistry) {
		this.sessionId = stringRegistry.get(buffer.getInt());
		this.serviceExecutionId = stringRegistry.get(buffer.getInt());
		this.serviceId = stringRegistry.get(buffer.getInt());
		this.parameters = stringRegistry.get(buffer.getInt());
		this.callerServiceExecutionId = stringRegistry.get(buffer.getInt());
		this.callerId = stringRegistry.get(buffer.getInt());
		this.startTime = buffer.getLong();
		this.stopTime = buffer.getLong();
	}
	
	
	public Object[] toArray() {
		return new Object[] { 
				this.getServiceId(),
				this.getServiceExecutionId(),
				this.getServiceId(),
				this.getParameters(),
				this.getCallerServiceExecutionId(),
				this.getCallerId(),
				this.getStartTime(),
				this.getStopTime(),
				};
	}
	
	
	public void writeBytes(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferOverflowException {
		buffer.putInt(stringRegistry.get(this.getServiceId()));
		buffer.putInt(stringRegistry.get(this.getServiceExecutionId()));
		buffer.putInt(stringRegistry.get(this.getServiceId()));
		buffer.putInt(stringRegistry.get(this.getParameters()));
		buffer.putInt(stringRegistry.get(this.getCallerServiceExecutionId()));
		buffer.putInt(stringRegistry.get(this.getCallerId()));
		buffer.putLong(this.getStartTime());
		buffer.putLong(this.getStopTime());
	}
	
	
	public int getSize() {
		return SIZE;
	}


	public Class<?>[] getValueTypes() {
		return ServiceRecord.TYPES;
	}


	 @Deprecated
    // Will not be used because the record implements IMonitoringRecord.Factory
	public void initFromArray(Object[] arg0) {
		
	}


	@Deprecated
	// Will not be used because the record implements IMonitoringRecord.BinaryFactory
	public void initFromBytes(ByteBuffer arg0, IRegistry<String> arg1) throws BufferUnderflowException {
		
	}


	public String getSessionId() {
		return sessionId;
	}


	public String getServiceExecutionId() {
		return serviceExecutionId;
	}


	public String getServiceId() {
		return serviceId;
	}


	public String getParameters() {
		return parameters;
	}


	public String getCallerServiceExecutionId() {
		return callerServiceExecutionId;
	}


	public String getCallerId() {
		return callerId;
	}


	public long getStartTime() {
		return startTime;
	}


	public long getStopTime() {
		return stopTime;
	}
	
	
	
}
