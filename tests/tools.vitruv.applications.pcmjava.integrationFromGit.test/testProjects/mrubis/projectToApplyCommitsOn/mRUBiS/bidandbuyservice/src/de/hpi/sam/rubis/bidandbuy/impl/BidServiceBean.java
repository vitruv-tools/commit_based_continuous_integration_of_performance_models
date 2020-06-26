package de.hpi.sam.rubis.bidandbuy.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.authservice.AuthenticationService;
import de.hpi.sam.rubis.authservice.AuthenticationServiceException;
import de.hpi.sam.rubis.bidandbuy.BidService;
import de.hpi.sam.rubis.bidandbuy.BidServiceException;
import de.hpi.sam.rubis.entity.Bid;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.entity.User;
import de.hpi.sam.rubis.inventorymgmt.InventoryService;
import de.hpi.sam.rubis.inventorymgmt.InventoryServiceException;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceService;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceServiceException;
import de.hpi.sam.rubis.queryservice.BasicQueryService;
import de.hpi.sam.rubis.queryservice.QueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;

/**
 * Implementation of the {@link BidService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = BidService.NAME)
public class BidServiceBean implements BidService {

	@EJB
	private AuthenticationService authService;

	@EJB
	private BasicQueryService basicQueryService;

	@EJB
	private BusinessObjectsPersistenceService persistenceService;

	@EJB
	private QueryService queryService;

	@EJB
	private InventoryService inventoryService;
	
    private boolean suppressResults = Boolean.getBoolean("suppressResults");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bid bidOnItem(int itemId, float bidPrice, float maxBidPrice,
			int quantity, String nickname, String password)
			throws BidServiceException {

		User user = null;
		try {
			user = this.authService.authenticate(nickname, password);
			// does not return null
		} catch (AuthenticationServiceException e) {
			throw new BidServiceException(
					"Failure in bidding on the item while authenticating the bidding user "
							+ nickname + ".", e);
		}

		Item item = null;
		try {
			item = this.basicQueryService.findItemById(itemId);
		} catch (QueryServiceException e) {
			throw new BidServiceException(
					"Failure in bidding on the item while searching the item with id = "
							+ itemId + ".", e);
		}
		if (item == null) {
			throw new BidServiceException(
					"Failure in bidding on the item. The item with id = "
							+ itemId + " cannot be found.");
		}

		// check whether the auction is currently running
		Date currentDate = new Date(System.currentTimeMillis());
		if (currentDate.after(item.getEndDate())) {
			throw new BidServiceException(
					"Failure in bidding on the item. The auction has already ended on "
							+ item.getEndDate() + ".");
		}
		if (currentDate.before(item.getStartDate())) {
			throw new BidServiceException("Failure in bidding on the item "
					+ item.getId()
					+ ". The auction has not started yet. It starts on "
					+ item.getStartDate() + ".");
		}

		// bidding user and seller must not be the same
		if (user.getId().equals(item.getSellerId())) {
			throw new BidServiceException("Failure in bidding on the item "
					+ item.getId()
					+ ". You cannot bid on items offered by you.");
		}

		// are items still available?
		int availableItemInstances = 0;
		try {
			availableItemInstances = this.inventoryService
					.checkAvailabilityOfItem(item);
		} catch (InventoryServiceException e) {
			throw new BidServiceException("Failure in bidding on the item "
					+ item.getId() + ". Availability of the item "
					+ item.getId() + " cannot be checked.", e);
		}

		// are items still available, i.e., item.quantity - the sum of all
		// buy-nows > 0
		// int buyNowsQuantity = 0;
		// for (BuyNow buyNow : item.getBuyNows()) {
		// buyNowsQuantity = buyNowsQuantity + buyNow.getQuantity();
		// }
		// int availableItemInstances = item.getInitialQuantity()
		// - buyNowsQuantity;

		if (availableItemInstances <= 0) {
			throw new BidServiceException("Failure in bidding on the item "
					+ item.getId() + ". All items have been sold by buy-nows.");
		}

		// there are still items available

		if (quantity > availableItemInstances) {
			// the requested number of items is not available
			throw new BidServiceException(
					"Failure in bidding on the item "
							+ item.getId()
							+ ". The requested number of items are not available. There are only "
							+ availableItemInstances + " items available.");
		}

		// the requested number of items is available;
		// bids are accepted regardless the bid prices. Should this be
		// changed? No, since bids with higher prices might be canceled
		// afterwards. Canceling bids is currently (2011-12-13) not supported.

		try {
			Bid bid = this.persistenceService.persistBid(user, item, bidPrice,
					maxBidPrice, quantity, currentDate);
            if (suppressResults) {
                return null;
            }
            return bid;
		} catch (BusinessObjectsPersistenceServiceException e) {
			throw new BidServiceException("Failure in bidding on the item "
					+ item.getId() + ".", e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Bid> getItemBidHistory(int itemId, int maxToCollect) throws BidServiceException {
		try {
			List<Bid> bidHistory = this.queryService.findItemBidHistory(itemId, maxToCollect);
            if (suppressResults) {
                return Collections.emptyList();
            }
            return bidHistory;
		} catch (QueryServiceException e) {
			throw new BidServiceException(
					"Failure in obtaining the bid history of item with id "
							+ itemId + ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Bid> getItemBidHistorySortByBidPrice(int itemId, int maxToCollect)
			throws BidServiceException {
		try {
			List<Bid> bidHistory = this.queryService.findItemMaxBids(itemId, maxToCollect);
            if (suppressResults) {
                return Collections.emptyList();
            }
            return bidHistory;
		} catch (QueryServiceException e) {
			throw new BidServiceException(
					"Failure in obtaining the bid history of item with id "
							+ itemId + ".", e);
		}
	}

}
