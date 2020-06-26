package de.hpi.sam.rubis.bidandbuy;

import javax.ejb.ApplicationException;

/**
 * Exception indicating a failure in buying an item.
 * 
 * @author thomas
 * 
 */
@ApplicationException(rollback = true)
public class BuyNowServiceException extends Exception {

	private static final long serialVersionUID = 6684745163366754542L;

	public BuyNowServiceException() {
		super();
	}

	public BuyNowServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuyNowServiceException(String message) {
		super(message);
	}

}
