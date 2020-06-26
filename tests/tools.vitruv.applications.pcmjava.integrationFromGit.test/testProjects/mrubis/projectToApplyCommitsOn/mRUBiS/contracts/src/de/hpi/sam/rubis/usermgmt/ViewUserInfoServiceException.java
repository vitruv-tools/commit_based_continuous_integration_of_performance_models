package de.hpi.sam.rubis.usermgmt;

import javax.ejb.ApplicationException;

/**
 * Exception indicating a failure in retrieving information about any user for
 * the currently logged in user.
 * 
 * @author thomas
 * 
 */
@ApplicationException(rollback = true)
public class ViewUserInfoServiceException extends Exception {

	private static final long serialVersionUID = 958329754592847767L;

	public ViewUserInfoServiceException() {
		super();
	}

	public ViewUserInfoServiceException(String msg) {
		super(msg);
	}

	public ViewUserInfoServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
