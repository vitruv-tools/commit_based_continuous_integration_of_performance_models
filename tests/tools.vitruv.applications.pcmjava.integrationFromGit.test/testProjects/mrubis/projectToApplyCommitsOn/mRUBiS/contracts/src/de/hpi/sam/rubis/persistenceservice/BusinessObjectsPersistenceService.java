package de.hpi.sam.rubis.persistenceservice;

import java.util.Date;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.Bid;
import de.hpi.sam.rubis.entity.BuyNow;
import de.hpi.sam.rubis.entity.Category;
import de.hpi.sam.rubis.entity.Comment;
import de.hpi.sam.rubis.entity.CustomerClass;
import de.hpi.sam.rubis.entity.InventoryItem;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.entity.Region;
import de.hpi.sam.rubis.entity.User;

/**
 * Service for persisting business objects.
 * 
 * @author thomas
 * 
 */
@Remote
public interface BusinessObjectsPersistenceService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "BusinessObjectsPersistenceService";

	/**
	 * Creates and persists a user.
	 * 
	 * @param firstname
	 *            the first name of the user
	 * @param lastname
	 *            the last name of the user
	 * @param nickname
	 *            the nickname of the user
	 * @param email
	 *            the e-mail address of the user
	 * @param password
	 *            the password of the user
	 * @param region
	 *            the region the user lives
	 * @param userClass
	 *            class of the user
	 * @return the newly created and persisted user as a business object
	 * @throws BusinessObjectsPersistenceServiceException
	 *             the there is a failure in persisting the user
	 */
	public User persistUser(String firstname, String lastname, String nickname,
			String email, String password, Region region,
			CustomerClass userClass)
			throws BusinessObjectsPersistenceServiceException;

	/**
	 * Creates and persists an item.
	 * 
	 * @param name
	 *            the name of the item
	 * @param description
	 *            the description of the item
	 * @param initialQuantity
	 *            the number of offered item instances
	 * @param initialPrice
	 *            the initial price of an item instance
	 * @param buyNowPrice
	 *            the buy-now price of an item instance
	 * @param reservcePrice
	 *            the reserve prices of an item instance
	 * @param startDate
	 *            the start date of the auction
	 * @param endDate
	 *            the end date of the auction
	 * @param seller
	 *            the user selling the item instances
	 * @param category
	 *            the category the item belongs to
	 * @return the newly created and persisted item
	 * @throws BusinessObjectsPersistenceServiceException
	 *             the there is a failure in persisting the item
	 */
	public Item persistItem(String name, String description,
			int initialQuantity, float initialPrice, float buyNowPrice,
			float reservcePrice, Date startDate, Date endDate, User seller,
			Category category)
			throws BusinessObjectsPersistenceServiceException;

	/**
	 * Creates and persists a bid.
	 * 
	 * @param biddingUser
	 *            the user putting a bid
	 * @param item
	 *            the item to put the bid on
	 * @param bidPrice
	 *            the price of the bid
	 * @param maxBidPrice
	 *            the maximum price of the bid
	 * @param quantity
	 *            the quantity of item instances
	 * @param date
	 *            time stamp the bid was put
	 * @return the newly created and persisted bid
	 * @throws BusinessObjectsPersistenceServiceException
	 *             if there is a failure in persisting the bid.
	 */
	public Bid persistBid(User biddingUser, Item item, float bidPrice,
			float maxBidPrice, int quantity, Date date)
			throws BusinessObjectsPersistenceServiceException;

	/**
	 * Creates and persists a buy-now.
	 * 
	 * @param buyer
	 *            the user buying the item
	 * @param item
	 *            the item that has been bought
	 * @param quantity
	 *            the number of item instances that have been bought
	 * @param buyingDate
	 *            the date of the buying
	 * @return the newly created and persisted buy-now.
	 * @throws BusinessObjectsPersistenceServiceException
	 *             if there is a failure in persisting the buy-now.
	 */
	public BuyNow persistBuyNow(User buyer, Item item, int quantity,
			Date buyingDate) throws BusinessObjectsPersistenceServiceException;

	/**
	 * Creates and persists a comment.
	 * 
	 * @param fromUser
	 *            the user giving the comment
	 * @param toUser
	 *            the user receiving the comment
	 * @param item
	 *            the item the comment refers to
	 * @param comment
	 *            the comment's message
	 * @param rating
	 *            the rating of the <code>toUser</code> or <code>item</code>
	 * @param commentDate
	 *            the date the comment is created
	 * @return the newly created and persisted comment
	 * @throws BusinessObjectsPersistenceServiceException
	 *             if there is a failure in persisting the comment.
	 */
	public Comment persistComment(User fromUser, User toUser, Item item,
			String comment, int rating, Date commentDate)
			throws BusinessObjectsPersistenceServiceException;

	/**
	 * Reduces the number of available instances of item <code>item</code> in
	 * the inventory by the amount of <code>numberOfRequestedInstances</code>.
	 * 
	 * @param item
	 *            the item whose instances should be reduced in the inventory.
	 * @param numberOfRequestedInstances
	 *            the number of item instances to be removed from the inventory
	 *            as they are not available any more.
	 * @return <code>true</code> if the requested instances are available and
	 *         could be taken from the inventory, else <code>false</code>
	 * @throws BusinessObjectsPersistenceServiceException
	 *             if there is a failure in updating the item in the inventory
	 */
	public boolean reduceInventoryItem(Item item, int numberOfRequestedInstances)
			throws BusinessObjectsPersistenceServiceException;

	/**
	 * Increases the number of available instances of item <code>item</code> in
	 * the inventory by the amount of <code>numberOfReturnedInstances</code>.
	 * 
	 * @param item
	 *            the item whose instances should be increased in the inventory.
	 * @param numberOfReturnedInstances
	 *            the number of item instances that are added to the inventory
	 *            as they are available now.
	 * @return the current inventory item
	 * @throws BusinessObjectsPersistenceServiceException
	 *             if there is a failure in updating the item in the inventory
	 */
	public InventoryItem increaseInventoryItem(Item item,
			int numberOfReturnedInstances)
			throws BusinessObjectsPersistenceServiceException;

}
