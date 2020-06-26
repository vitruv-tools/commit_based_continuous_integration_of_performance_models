package de.hpi.sam.rubis.itemmgmt;

import javax.ejb.ApplicationException;

/**
 * Exception indicating a failure in browsing categories and items.
 * 
 * @author thomas
 * 
 */
@ApplicationException(rollback = true)
public class BrowseCategoriesServiceException extends Exception {

	private static final long serialVersionUID = -5373319606928234635L;

	public BrowseCategoriesServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public BrowseCategoriesServiceException(String msg) {
		super(msg);
	}

	public BrowseCategoriesServiceException() {
		super();
	}

}
