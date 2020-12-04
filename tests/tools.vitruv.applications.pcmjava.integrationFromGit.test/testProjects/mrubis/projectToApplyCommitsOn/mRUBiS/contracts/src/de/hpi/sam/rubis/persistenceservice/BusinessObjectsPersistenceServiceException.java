package de.hpi.sam.rubis.persistenceservice;

import javax.ejb.ApplicationException;

/**
 * Exception indicating a failure in persisting business objects.
 * 
 * @author thomas
 * 
 */
@ApplicationException(rollback = true)
public class BusinessObjectsPersistenceServiceException extends Exception {

	private static final long serialVersionUID = 8417959955422529463L;

	public BusinessObjectsPersistenceServiceException() {
		super();
	}

	public BusinessObjectsPersistenceServiceException(String message,
			Throwable cause) {
		super(message, cause);
	}

	public BusinessObjectsPersistenceServiceException(String message) {
		super(message);
	}

}
