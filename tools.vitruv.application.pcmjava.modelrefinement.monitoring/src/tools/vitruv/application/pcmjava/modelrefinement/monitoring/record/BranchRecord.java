package tools.vitruv.application.pcmjava.modelrefinement.monitoring.record;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.util.registry.IRegistry;

/**
 * @author Generic Kieker API compatibility: Kieker 1.13.0
 * 
 * @since 1.13
 */
public class BranchRecord extends AbstractMonitoringRecord
        implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = -6809601359255812413L;


	public static final int SIZE = (4 * TYPE_SIZE_STRING);
	

    public static final Class<?>[] TYPES = {
            String.class, // RecordWithSession.sessionId
            String.class, // RecordWithSession.serviceExecutionId
            String.class, // BranchRecord.branchId
            String.class, // BranchRecord.executedBranchId
    };

    
    /** default constants. */
    public static final String SESSION_ID = "<not set>";
    public static final String SERVICE_EXECUTION_ID = "<not set>";
    public static final String BRANCH_ID = "<not set>";
    public static final String EXECUTED_BRANCH_ID = "<not set>";

    
    
    // attributes that will store the customized records
    private final String sessionId;
    private final String serviceExecutionId;
    private final String branchId;
    private final String executedBranchId;
    
    
	public BranchRecord(String sessionId, String serviceExecutionId, String branchId, String executedBranchId) {
		super();
		this.sessionId = sessionId == null ? SESSION_ID : sessionId;
		this.serviceExecutionId = serviceExecutionId == null ? SERVICE_EXECUTION_ID : serviceExecutionId;
		this.branchId = branchId == null ? BRANCH_ID : branchId;
		this.executedBranchId = executedBranchId == null ? EXECUTED_BRANCH_ID : executedBranchId;
	}
	
	
    public BranchRecord(final Object[] values) {
    	AbstractMonitoringRecord.checkArray(values, BranchRecord.TYPES);
    	
    	this.sessionId = (String) values[0];
    	this.serviceExecutionId = (String) values[1];
    	this.branchId = (String) values[2];
    	this.executedBranchId = (String) values[3];
    }
    
    
    public BranchRecord(final ByteBuffer buffer, final IRegistry<String> stringRegistry) {
    	this.sessionId = stringRegistry.get(buffer.getInt());
    	this.serviceExecutionId = stringRegistry.get(buffer.getInt());
    	this.branchId = stringRegistry.get(buffer.getInt());
    	this.executedBranchId = stringRegistry.get(buffer.getInt());
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
				this.getBranchId(),
				this.getExecutedBranchId(),
				};
	}
	

	public void writeBytes(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferOverflowException {
		buffer.putInt(stringRegistry.get(this.getSessionId()));
		buffer.putInt(stringRegistry.get(this.getServiceExecutionId()));
		buffer.putInt(stringRegistry.get(this.getBranchId()));
		buffer.putInt(stringRegistry.get(this.getExecutedBranchId()));		
	}
	
	
	public int getSize() {
		return SIZE;
	}

	public Class<?>[] getValueTypes() {
		return BranchRecord.TYPES;
	}


	public String getSessionId() {
		return sessionId;
	}


	public String getServiceExecutionId() {
		return serviceExecutionId;
	}


	public String getBranchId() {
		return branchId;
	}


	public String getExecutedBranchId() {
		return executedBranchId;
	}
	
	
}
