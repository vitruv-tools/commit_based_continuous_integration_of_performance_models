package de.hpi.sam.rubis.inventorymgmt.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import de.hpi.sam.rubis.entity.InventoryItem;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.inventorymgmt.InventoryService;
import de.hpi.sam.rubis.inventorymgmt.InventoryServiceException;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceService;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceServiceException;
import de.hpi.sam.rubis.queryservice.QueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;

/**
 * Implementation of the {@link InventoryService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = InventoryService.NAME)
public class InventoryServiceBean implements InventoryService {

	@EJB
	private QueryService queryService;

	@EJB
	private BusinessObjectsPersistenceService persistenceService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int checkAvailabilityOfItem(Item item)
			throws InventoryServiceException {
		try {
			InventoryItem ii = this.queryService
					.retrieveAvailabilityOfItem(item.getId());

//			System.out.println("InventoryServiceBean: "
//					+ ii.getItem().infoString() + " // "
//					+ ii.getAvailableQuantity());

			return ii.getAvailableQuantity();
		} catch (QueryServiceException e) {
			throw new InventoryServiceException(
					"Failure in checking availability of item " + item, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean reserveItem(Item item, int numberOfItems)
			throws InventoryServiceException {
        // PM: redundant check because the call to reduceInventoryItem will return false if not
        // enough instances are available
//		int availableInstances = this.checkAvailabilityOfItem(item);
//		if (numberOfItems > availableInstances) {
//			return false;
//		} else {
			try {
				boolean isReserved = this.persistenceService
						.reduceInventoryItem(item, numberOfItems);
				return isReserved;
			} catch (BusinessObjectsPersistenceServiceException e) {
				throw new InventoryServiceException("Failure in reserving item " + item, e);
//				// Item cannot be reserved
//				return false;
			}
//		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancelReservedItem(Item item, int numberOfItems)
			throws InventoryServiceException {
		try {
			@SuppressWarnings("unused")
			InventoryItem ii = this.persistenceService.increaseInventoryItem(
					item, numberOfItems);
		} catch (BusinessObjectsPersistenceServiceException e) {
			throw new InventoryServiceException(
					"Failure in increasing the instances of item " + item
							+ " in the inventory.", e);
		}
	}

}
