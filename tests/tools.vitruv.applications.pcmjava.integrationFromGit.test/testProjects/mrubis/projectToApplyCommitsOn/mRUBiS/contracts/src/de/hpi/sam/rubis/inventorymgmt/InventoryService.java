package de.hpi.sam.rubis.inventorymgmt;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.Item;

/**
 * Service for managing the inventory.
 * 
 * @author thomas
 * 
 */
@Remote
public interface InventoryService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "InventoryService";

	/**
	 * Retrieves the number of available instances of the item.
	 * 
	 * @param item
	 *            the item for which the availability should be checked.
	 * @return the number of available instances of the item
	 * @throws InventoryServiceException
	 *             if there is a failure in checking the availability
	 */
	public int checkAvailabilityOfItem(Item item)
			throws InventoryServiceException;

	/**
	 * Checks for the availability of items in the inventory and reserves the
	 * requested number of items if possible. It decreases the available
	 * quantity of item instances.
	 * 
	 * @param item
	 *            the item that should be reserved
	 * @param numberOfItems
	 *            the number of items that should be reserved
	 * @return <code>true</code> if the requested <code>numberOfItems</code> are
	 *         available and could be reserved.
	 * @throws InventoryServiceException
	 *             if there is a failure in reserving the items.
	 */
	public boolean reserveItem(Item item, int numberOfItems)
			throws InventoryServiceException;

	/**
	 * Cancels the reservation of the <code>numberOfItems</code> instances of
	 * the item.
	 * 
	 * @param item
	 *            the item for whose instances the reservations should be
	 *            canceled.
	 * @param numberOfItems
	 *            the number of item instances for which the reservation should
	 *            be canceled.
	 * @throws InventoryServiceException
	 *             if there is a failure in canceling the reservation
	 */
	public void cancelReservedItem(Item item, int numberOfItems)
			throws InventoryServiceException;

}
