package de.hpi.sam.rubis.bidandbuy;

import javax.ejb.ApplicationException;

/**
 * Exception indicating a failure in putting a bid on an item.
 * 
 * @author thomas
 * 
 */
@ApplicationException(rollback = true)
public class BidServiceException extends Exception {

	private static final long serialVersionUID = -7562533875932689014L;

	public BidServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public BidServiceException(String message) {
		super(message);
	}

	public BidServiceException() {
		super();

	}

}
