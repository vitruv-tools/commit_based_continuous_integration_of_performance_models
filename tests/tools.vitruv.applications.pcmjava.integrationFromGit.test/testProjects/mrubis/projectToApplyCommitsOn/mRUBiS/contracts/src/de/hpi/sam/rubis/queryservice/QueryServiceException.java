package de.hpi.sam.rubis.queryservice;

import javax.ejb.ApplicationException;

/**
 * Exception indicating a failure in querying the business objects from
 * persistence storage.
 * 
 * @author thomas
 * 
 */
@ApplicationException(rollback = true)
public class QueryServiceException extends Exception {

	private static final long serialVersionUID = 1731430661343621479L;

	public QueryServiceException() {
		super();
	}

	public QueryServiceException(String msg) {
		super(msg);
	}

	public QueryServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
