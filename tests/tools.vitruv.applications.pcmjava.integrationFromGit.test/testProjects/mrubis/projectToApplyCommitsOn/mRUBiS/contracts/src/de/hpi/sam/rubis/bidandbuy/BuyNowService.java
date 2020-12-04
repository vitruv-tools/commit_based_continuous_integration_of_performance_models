package de.hpi.sam.rubis.bidandbuy;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.BuyNow;

/**
 * Service to buy-now items.
 * 
 * @author thomas
 * 
 */
@Remote
public interface BuyNowService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "BuyNowService";

	/**
	 * Buys now an item.
	 * 
	 * @param itemId
	 *            the identifier of the item to buy
	 * @param quantity
	 *            the number of item instances to buy
	 * @param nickname
	 *            the nickname of the user buying the item
	 * @param password
	 *            the password of the user buying the item
	 * @return the buy-now business object
	 * @throws BuyNowServiceException
	 *             if there is a failure in buying the item.
	 */
	public BuyNow buyItemNow(int itemId, int quantity, String nickname,
			String password) throws BuyNowServiceException;

}
