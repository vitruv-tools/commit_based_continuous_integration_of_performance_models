package de.hpi.sam.rubis.inventorymgmt;

import javax.ejb.ApplicationException;

/**
 * Exception indicating a failure in the inventory management.
 * 
 * @author thomas
 * 
 */
@ApplicationException(rollback = true)
public class InventoryServiceException extends Exception {

	private static final long serialVersionUID = -4814395889722050168L;

	public InventoryServiceException() {
		super();
	}

	public InventoryServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public InventoryServiceException(String message) {
		super(message);
	}

}
