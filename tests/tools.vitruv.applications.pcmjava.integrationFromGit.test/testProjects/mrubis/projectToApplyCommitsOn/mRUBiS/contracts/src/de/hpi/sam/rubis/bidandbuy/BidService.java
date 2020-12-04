package de.hpi.sam.rubis.bidandbuy;

import java.util.List;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.Bid;

/**
 * Service for bidding on items.
 * 
 * @author thomas
 * 
 */
@Remote
public interface BidService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "BidService";

	/**
	 * Puts a bid on an item.
	 * 
	 * @param itemId
	 *            the identifier of the item to put a bid on.
	 * @param bidPrice
	 *            the bid price for one item instance.
	 * @param maxBidPrice
	 *            the maximum bid price for one item instance.
	 * @param quantity
	 *            the number of item instances to bid for.
	 * @param nickname
	 *            the nickname of the user putting the bid.
	 * @param password
	 *            the password of the user putting the bid.
	 * @return the bid business object
	 * @throws BidServiceException
	 *             if there is a failure in putting the bid.
	 */
	public Bid bidOnItem(int itemId, float bidPrice, float maxBidPrice,
			int quantity, String nickname, String password)
			throws BidServiceException;

	/**
	 * Retrieves the bids for the specified item sorted by the date the bid was
	 * put.
	 * 
	 * @param itemId
	 *            identifier of the item
     * @param maxToCollect
     *            number of bids to collect
	 * @return the list of bid put on the item with the identifier
	 *         <code>itemId</code>.
	 * @throws BidServiceException
	 *             if there is a failure in retrieving the bids.
	 */
	public List<Bid> getItemBidHistory(int itemId, int maxToCollect) throws BidServiceException;

	/**
	 * Retrieves the bids for the specified item sorted by the bid price
	 * (decreasing).
	 * 
	 * @param itemId
	 *            identifier of the item
     * @param maxToCollect
     *            number of bids to collect
	 * @return the list of bid put on the item with the identifier
	 *         <code>itemId</code>.
	 * @throws BidServiceException
	 *             if there is a failure in retrieving the bids.
	 */
	public List<Bid> getItemBidHistorySortByBidPrice(int itemId, int maxToCollect)
			throws BidServiceException;

}
