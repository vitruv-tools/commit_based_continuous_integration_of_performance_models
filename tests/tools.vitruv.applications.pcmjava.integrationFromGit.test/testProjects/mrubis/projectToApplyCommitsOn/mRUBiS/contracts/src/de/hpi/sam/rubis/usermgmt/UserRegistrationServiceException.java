package de.hpi.sam.rubis.usermgmt;

import javax.ejb.ApplicationException;

/**
 * Exception indicating a failure in registering a user.
 * 
 * @author thomas
 * 
 */
@ApplicationException(rollback = true)
public class UserRegistrationServiceException extends Exception {

	private static final long serialVersionUID = 3980292106557959186L;

	public UserRegistrationServiceException() {
		super();
	}

	public UserRegistrationServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserRegistrationServiceException(String message) {
		super(message);
	}

}
