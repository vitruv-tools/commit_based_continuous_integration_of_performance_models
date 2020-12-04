package de.hpi.sam.rubis.queryservice;

import java.util.List;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.Bid;
import de.hpi.sam.rubis.entity.BidHistory;
import de.hpi.sam.rubis.entity.BuyNow;
import de.hpi.sam.rubis.entity.Category;
import de.hpi.sam.rubis.entity.Comment;
import de.hpi.sam.rubis.entity.InventoryItem;
import de.hpi.sam.rubis.entity.Item;

/**
 * Service to retrieve advanced information from the database.
 * 
 * @author thomas
 * 
 */
@Remote
public interface QueryService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "QueryService";

	/**
	 * Finds all the items that match a specific category and that are still to
	 * sell (auction end date is not passed). The items are sorted according to
	 * the end date of the auction in ascending order.
	 * 
	 * @param categoryId
	 *            identifier of the category the items should be part of.
	 * @param maxResult
	 *            maximum number of items to retrieve.
	 * 
	 * @return List of the retrieved items.
	 * @throws QueryServiceException
	 *             if the is a failure in retrieving the items or if there does
	 *             not exist a category with the specified identifier.
	 */
	public List<Item> findCurrentItemsInCategory(int categoryId, int maxResult)
			throws QueryServiceException;

	/**
	 * Finds all the items that match a specific category and whose auctions
	 * already ended (auction end date has passed). The items are sorted
	 * according to the end date of the auction in descending order.
	 * 
	 * @param categoryId
	 *            identifier of the category the items should be part of.
	 * @param maxResult
	 *            maximum number of items to retrieve.
	 * 
	 * @return List of the retrieved items.
	 * @throws QueryServiceException
	 *             if the is a failure in retrieving the items or if there does
	 *             not exist a category with the specified identifier.
	 */
	public List<Item> findPastItemsInCategory(int categoryId, int maxResult)
			throws QueryServiceException;

	/**
	 * Finds all the items that match a specific category and region, and that
	 * are still to sell (auction end date is not passed). The items are sorted
	 * according to the end date of the auction in ascending order.
	 * 
	 * @param categoryId
	 *            identifier of the category the items should be part of.
	 * @param regionId
	 *            identifier of the region the seller of the items lives.
	 * @param maxResult
	 *            maximum number of items to retrieve
	 * 
	 * @return List of the retrieved items.
	 * @throws QueryServiceException
	 *             if the is a failure in retrieving the items or if there does
	 *             not exist a category or region with the specified
	 *             identifiers.
	 */
	public List<Item> findCurrentItemsInCategoryAndRegion(int categoryId,
			int regionId, int maxResult) throws QueryServiceException;

	/**
	 * Finds all the items that match a specific category and region, and whose
	 * auctions have ended (auctions end dates have passed). The items are
	 * sorted according to the end date of the auction in descending order.
	 * 
	 * @param categoryId
	 *            identifier of the category the items should be part of.
	 * @param regionId
	 *            identifier of the region the seller of the items lives.
	 * @param maxResult
	 *            maximum number of items to retrieve
	 * 
	 * @return List of the retrieved items.
	 * @throws QueryServiceException
	 *             if the is a failure in retrieving the items or if there does
	 *             not exist a category or region with the specified
	 *             identifiers.
	 */
	public List<Item> findPastItemsInCategoryAndRegion(int categoryId,
			int regionId, int maxResult) throws QueryServiceException;

	/**
	 * Get the maximum bid (winning bid) for an item. The auction of the item
	 * might have ended or it might still be running.
	 * 
	 * @param itemId
	 *            the identifier of the item.
	 * 
	 * @return the bid having the maximum bid price, or <code>null</code> if
	 *         there are no bids for the specific item.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the maximum bid, or if
	 *             there is no item with the specified identifier.
	 */
	public Bid findItemMaxBid(int itemId) throws QueryServiceException;

	/**
	 * Get the first <code>maxToCollect</code> bids for an item sorted from the
	 * maximum to the minimum bid prices. The auction of the corresponding item
	 * might have ended or it might still be running.
	 * 
	 * @param itemId
	 *            identifier of the item
	 * @param maxToCollect
	 *            number of bids to collect
	 * 
	 * @return List of the requested bids. The list might be empty.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the bids or if there is
	 *             no item with the specified identifier.
	 */
	public List<Bid> findItemMaxBids(int itemId, int maxToCollect)
			throws QueryServiceException;

	/**
	 * Get the bid history for an item sorted from the last bid to the first bid
	 * (oldest one).
	 * 
	 * @param itemId
	 *            identifier of the item
	 * @param maxToCollect
     *            number of bids to collect
	 * 
	 * @return List of the requested bids.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the bids or if there is
	 *             no item with the specified identifier.
	 */
	public List<Bid> findItemBidHistory(int itemId, int maxToCollect)
			throws QueryServiceException;

	/**
	 * Finds the bids that successfully bought the item. These are the bids that
	 * won the finished auction by buying the items that have not been bought by
	 * buy-nows before.
	 * 
	 * @param itemId
	 *            the identifier of the item
	 * @return the successful bids. The list might be empty if all item
	 *         instances have been bought by buy-nows.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the successful bids.
	 */
	public BidHistory findSuccessfulBidsForItem(int itemId)
			throws QueryServiceException;

	/**
	 * Get all the buy-nows the user bought on in the last <code>days</code>
	 * days.
	 * 
	 * @param userId
	 *            identifier of the user
	 * @param days
	 *            the number of days in the past for which the buy-nows are
	 *            retrieved.
	 * 
	 * @return List of the requested buy-nows.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the buy-nows or if there
	 *             is no user with the specified identifier.
	 */
	public List<BuyNow> findUserBuyNows(int userId, int days)
			throws QueryServiceException;

	/**
	 * Finds all the items the user is currently selling.
	 * 
	 * @param userId
	 *            identifier of the user.
	 * 
	 * @return List of items that the user is currently selling.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the items or if there is
	 *             no user with the specified identifier.
	 */
	public List<Item> findUserCurrentSellings(int userId)
			throws QueryServiceException;

	/**
	 * Get all the items the user sold in the last <code>days</code> days.
	 * 
	 * @param userId
	 *            identifier of the user.
	 * @param days
	 *            number of days in the past that is searched for the sellings
	 *            of the user.
	 * 
	 * @return List of items the user sold in the past <code>days</code> days.
	 *         The list might be empty.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the items or if there is
	 *             no user with the specified identifier.
	 */
	public List<Item> findUserPastSellings(int userId, int days)
			throws QueryServiceException;

	/**
	 * Finds all the winning bids the user has done in the past, i.e., the bids
	 * that won the auctions.
	 * 
	 * @param userId
	 *            the identifier of the user.
	 * @return list of the winning bids, which might be empty.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the bids or if there is
	 *             no user with the specified identifier.
	 */
	public List<Bid> findUserPastWinningBids(int userId)
			throws QueryServiceException;

	/**
	 * Finds all the bids of the user, which have currently the highest bid
	 * prices for the items.
	 * 
	 * @param userId
	 *            the identifier of the user.
	 * @return List of bids for currently running auctions, which belong to the
	 *         user and which have the highest bid prices for the items. The
	 *         list might be empty.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the bids or if there is
	 *             no user with the specified identifier.
	 */
	public List<Bid> findUserCurrentMaxBids(int userId)
			throws QueryServiceException;

	/**
	 * Finds categories in which items are currently offered by users living in
	 * the specified region.
	 * 
	 * @param regionName
	 *            the name of the region.
	 * @return the list of categories that contain currently offered items by
	 *         user living in the specified region. The list might be empty.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the categories or if
	 *             there is no region with the specified name.
	 */
	public List<Category> findCategoriesInRegion(String regionName)
			throws QueryServiceException;

	/**
	 * Returns the number of bids one item got.
	 * 
	 * @param itemId
	 *            the identifier of the item.
	 * @return the number of bids one item got.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the number of the bids or
	 *             if there is no item with the specified identifier.
	 */
	public int getNumberOfBidsForItem(int itemId) throws QueryServiceException;

	/**
	 * Finds the unique comment from the user (buyer) with id
	 * <code>fromUserId</code> to the user (seller) with id
	 * <code>toUserId</code> about the item with id <code>itemId</code>. There
	 * exists at most one comment for the triple <code>fromUserId</code>,
	 * <code>toUserId</code> and <code>itemId</code>, i.e., a buyer cannot give
	 * more than one comment to a seller regarding the same item, even if the
	 * buyer has more than one buy-nows and/or successful bids.
	 * 
	 * @param fromUserId
	 *            the identifier of the user giving the comment.
	 * @param toUserId
	 *            the identifier of the user receiving the comment.
	 * @param itemId
	 *            the identifier of the corresponding item
	 * @return the unique comment for the triple or <code>null</code> if the
	 *         comment does not exist yet.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the comment.
	 */
	public Comment findComment(int fromUserId, int toUserId, int itemId)
			throws QueryServiceException;

	/**
	 * Retrieves the inventory item containing the number of available instances
	 * of the item with the identifier <code>itemId</code>.
	 * 
	 * @param itemId
	 *            identifier of the item
	 * @return the inventory item with number of available instances of the item
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving the number of available
	 *             instances.
	 */
	public InventoryItem retrieveAvailabilityOfItem(int itemId)
			throws QueryServiceException;

}
