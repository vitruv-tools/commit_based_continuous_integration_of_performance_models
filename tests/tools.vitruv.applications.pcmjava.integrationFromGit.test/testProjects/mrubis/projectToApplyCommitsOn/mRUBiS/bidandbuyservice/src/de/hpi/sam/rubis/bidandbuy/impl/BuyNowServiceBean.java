package de.hpi.sam.rubis.bidandbuy.impl;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.authservice.AuthenticationService;
import de.hpi.sam.rubis.authservice.AuthenticationServiceException;
import de.hpi.sam.rubis.bidandbuy.BuyNowService;
import de.hpi.sam.rubis.bidandbuy.BuyNowServiceException;
import de.hpi.sam.rubis.entity.BuyNow;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.entity.User;
import de.hpi.sam.rubis.inventorymgmt.InventoryService;
import de.hpi.sam.rubis.inventorymgmt.InventoryServiceException;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceService;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceServiceException;
import de.hpi.sam.rubis.queryservice.BasicQueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;

//TODO alternative bid and buy service that does not use the inventory service to buy an item
/**
 * Implementation of the {@link BuyNowService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = BuyNowService.NAME) //  + "_wrapped"
public class BuyNowServiceBean implements BuyNowService {

	@EJB
	private AuthenticationService authenticationService;

	@EJB
	private BasicQueryService basicQueryService;

	@EJB
	private BusinessObjectsPersistenceService persistenceService;

	@EJB
	private InventoryService inventoryService;

    private boolean suppressResults = Boolean.getBoolean("suppressResults");
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BuyNow buyItemNow(int itemId, int quantity, String nickname,
			String password) throws BuyNowServiceException {

		User user = null;
		try {
			user = this.authenticationService.authenticate(nickname, password);
			// does not return null
		} catch (AuthenticationServiceException e) {
			throw new BuyNowServiceException(
					"Failure in buying the item while authenticating the user "
							+ nickname + ".", e);
		}

		Item item = null;
		try {
			item = this.basicQueryService.findItemById(itemId);
		} catch (QueryServiceException e) {
			throw new BuyNowServiceException(
					"Failure in buying the item while searching the item with id = "
							+ itemId + ".", e);
		}
		if (item == null) {
			throw new BuyNowServiceException(
					"Failure in buying the item. The item with id = " + itemId
							+ " cannot be found.");
		}

		// check whether the auction is currently running
		Date currentDate = new Date(System.currentTimeMillis());
		if (currentDate.after(item.getEndDate())) {
			throw new BuyNowServiceException(
					"Failure in buying the item. The auction has already ended on "
							+ item.getEndDate() + ".");
		}
		if (currentDate.before(item.getStartDate())) {
			throw new BuyNowServiceException(
					"Failure in buying the item. The auction has not started yet. It starts on "
							+ item.getStartDate() + ".");
		}

		// buyer and seller must not be the same
		if (user.getId().equals(item.getSellerId())) {
			throw new BuyNowServiceException(
					"Failure in buying the item. You cannot buy items offered by you.");
		}

		// are items still available?
		int availableItemInstances = 0;
		try {
			availableItemInstances = this.inventoryService
					.checkAvailabilityOfItem(item);
		} catch (InventoryServiceException e) {
			throw new BuyNowServiceException(
					"Failure in buying the item. Availability of the item cannot be checked.", e);
		}

		// are items still available, i.e., item.quantity - the sum of all
		// buy-nows > 0
		// int buyNowsQuantity = 0;
		// for (BuyNow buyNow : item.getBuyNows()) {
		// buyNowsQuantity = buyNowsQuantity + buyNow.getQuantity();
		// }
		// int availableItemInstances = item.getInitialQuantity() -
		// buyNowsQuantity;
		if (availableItemInstances <= 0) {
			throw new BuyNowServiceException(
					"Failure in buying the item. All items have been sold by buy-nows.");
		}

		// there are still items available
		boolean itemsReserved = false;
		try {
			itemsReserved = this.inventoryService.reserveItem(item, quantity);
		} catch (InventoryServiceException e) {
			throw new BuyNowServiceException(
					"Failure in buying the item. Availability of the item cannot be checked and the items cannot be reserved.", e);
		}

		if (!itemsReserved) {
			// the requested number of items is not available
			throw new BuyNowServiceException(
					"Failure in buying the item. The requested number of items is not available. There are only "
							+ availableItemInstances + " items available.");
		} else {

			// the requested number of items is available
			try {
				BuyNow buyNow = this.persistenceService.persistBuyNow(user,
						item, quantity, currentDate);
                if (suppressResults) {
                    return null;
                }
                return buyNow;
			} catch (BusinessObjectsPersistenceServiceException e) {
				throw new BuyNowServiceException("Failure in buying the item.",
						e);
			}
		}
	}

}
