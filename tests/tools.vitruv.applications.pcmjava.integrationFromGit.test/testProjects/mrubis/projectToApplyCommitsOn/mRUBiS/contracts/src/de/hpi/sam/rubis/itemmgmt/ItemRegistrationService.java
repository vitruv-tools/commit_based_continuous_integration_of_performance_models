package de.hpi.sam.rubis.itemmgmt;

import java.util.Date;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.Item;

/**
 * Service for registering new items.
 * 
 * @author thomas
 * 
 */
@Remote
public interface ItemRegistrationService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "ItemRegistrationService";

	/**
	 * 
	 * @param name
	 *            the name of the item
	 * @param description
	 *            a description of the item
	 * @param initialQuantity
	 *            the initial quantity of the item that will be sold
	 * @param initialPrice
	 *            the initial price of the item
	 * @param buyNowPrice
	 *            the price to buy the item
	 * @param reservcePrice
	 *            the reserve price of the item
	 * @param startDate
	 *            the start date of the auction
	 * @param endDate
	 *            the end date of the auction
	 * @param sellerId
	 *            the identifier of the user selling the item. The seller must
	 *            be the user who is logged in.
	 * @param password
	 *            the password of the logged-in user
	 * @param categoryId
	 *            the identifier of the category the item belongs to
	 * @return the newly registered item
	 * @throws ItemRegistrationServiceException
	 *             if there is a failure in registering the item
	 */
	public Item registerItem(String name, String description,
			int initialQuantity, float initialPrice, float buyNowPrice,
			float reservcePrice, Date startDate, Date endDate, int sellerId,
			String password, int categoryId)
			throws ItemRegistrationServiceException;

}
