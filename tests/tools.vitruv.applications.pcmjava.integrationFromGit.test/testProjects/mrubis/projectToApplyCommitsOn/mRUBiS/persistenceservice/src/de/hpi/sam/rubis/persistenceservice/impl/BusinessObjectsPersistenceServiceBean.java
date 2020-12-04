package de.hpi.sam.rubis.persistenceservice.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.hpi.sam.rubis.RubisNameSchema;
import de.hpi.sam.rubis.entity.Bid;
import de.hpi.sam.rubis.entity.BuyNow;
import de.hpi.sam.rubis.entity.Category;
import de.hpi.sam.rubis.entity.Comment;
import de.hpi.sam.rubis.entity.CustomerClass;
import de.hpi.sam.rubis.entity.EBid;
import de.hpi.sam.rubis.entity.EBuyNow;
import de.hpi.sam.rubis.entity.ECategory;
import de.hpi.sam.rubis.entity.EComment;
import de.hpi.sam.rubis.entity.EInventoryItem;
import de.hpi.sam.rubis.entity.EItem;
import de.hpi.sam.rubis.entity.ERegion;
import de.hpi.sam.rubis.entity.EUser;
import de.hpi.sam.rubis.entity.InventoryItem;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.entity.Region;
import de.hpi.sam.rubis.entity.User;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceService;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceServiceException;

/**
 * Implementation of the {@link BusinessObjectsPersistenceService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = BusinessObjectsPersistenceService.NAME)
public class BusinessObjectsPersistenceServiceBean implements
		BusinessObjectsPersistenceService {

	@PersistenceContext(unitName = RubisNameSchema.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item persistItem(String name, String description,
			int initialQuantity, float initialPrice, float buyNowPrice,
			float reservcePrice, Date startDate, Date endDate, User seller,
			Category category)
			throws BusinessObjectsPersistenceServiceException {

		int categoryId = category.getId();
		ECategory eCategory = null;
		try {
			eCategory = this.em.find(ECategory.class, categoryId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the item while searching for the category with id = "
							+ categoryId + ".", e);
		}
		if (eCategory == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the item: No category with id = "
							+ categoryId + " found.");
		}

		int sellerId = seller.getId();
		EUser eSeller = null;
		try {
			eSeller = this.em.find(EUser.class, sellerId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the item while searching for the user with id = "
							+ sellerId + ".", e);
		}
		if (eSeller == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the item: No user with id = "
							+ sellerId + " found.");
		}

		EItem eItem = new EItem();
		eItem.setName(name);
		eItem.setDescription(description);
		eItem.setInitialQuantity(initialQuantity);
		eItem.setInitialPrice(initialPrice);
		eItem.setBuyNowPrice(buyNowPrice);
		eItem.setReservePrice(reservcePrice);
		eItem.setStartDate(startDate);
		eItem.setEndDate(endDate);
		eItem.setSeller(eSeller);
		eItem.setCategory(eCategory);

		EInventoryItem eInventory = new EInventoryItem(eItem, initialQuantity);

		try {
			this.em.persist(eInventory);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the item.", e);
		}

		return eItem.convertToDTO();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User persistUser(String firstname, String lastname, String nickname,
			String email, String password, Region region,
			CustomerClass customerClass)
			throws BusinessObjectsPersistenceServiceException {

		// Region has an identifier as the region was retrieved from the
		// database!
		int regionId = region.getId();
		ERegion eRegion = null;
		try {
			eRegion = this.em.find(ERegion.class, regionId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the user while searching for the region with id = "
							+ regionId + ".", e);
		}
		if (eRegion == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the user: No region with id = "
							+ regionId + " found.");
		}

		EUser eUser = new EUser();
		eUser.setFirstname(firstname);
		eUser.setLastname(lastname);
		eUser.setNickname(nickname);
		eUser.setEmail(email);
		eUser.setPassword(password);
		eUser.setRegion(eRegion);
		eUser.setUserClass(customerClass.name());
		eUser.setCreationDate(new Date(System.currentTimeMillis()));

		try {
			this.em.persist(eUser);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the user.", e);
		}

		return eUser.convertToDTO();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bid persistBid(User biddingUser, Item item, float bidPrice,
			float maxBidPrice, int quantity, Date date)
			throws BusinessObjectsPersistenceServiceException {

		// User and item are already persisted objects. Hence, they have
		// identifier and they can be retrieved from the database.
		int userId = biddingUser.getId();
		EUser eUser = null;
		try {
			eUser = this.em.find(EUser.class, userId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the bid while searching for the user with id = "
							+ userId + ".", e);
		}
		if (eUser == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the bid: No user with id = "
							+ userId + " found.");
		}

		int itemId = item.getId();
		EItem eItem = null;
		try {
			eItem = this.em.find(EItem.class, itemId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the bid while searching for the item with id = "
							+ itemId + ".", e);
		}
		if (eItem == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the bid: No item with id = "
							+ itemId + " found.");
		}

		EBid eBid = new EBid();
		eBid.setBidPrice(bidPrice);
		eBid.setMaxBidPrice(maxBidPrice);
		eBid.setQuantity(quantity);
		eBid.setDate(date);
		eBid.setUser(eUser);
		eBid.setItem(eItem);

		try {
			this.em.persist(eBid);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the bid.", e);
		}

		return eBid.convertToDTO();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BuyNow persistBuyNow(User buyer, Item item, int quantity,
			Date buyingDate) throws BusinessObjectsPersistenceServiceException {
		// user and item are already persisted objects and they have identifiers
		int buyerId = buyer.getId();
		EUser eBuyer = null;
		try {
			eBuyer = this.em.find(EUser.class, buyerId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the buy-now while searching for the user with id = "
							+ buyerId + ".", e);
		}
		if (eBuyer == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the buy-now: No user with id = "
							+ buyerId + " found.");
		}

		int itemId = item.getId();
		EItem eItem = null;
		try {
			eItem = this.em.find(EItem.class, itemId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the buy-now while searching for the item with id = "
							+ itemId + ".", e);
		}
		if (eItem == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the buy-now: No item with id = "
							+ itemId + " found.");
		}

		EBuyNow eBuyNow = new EBuyNow();
		eBuyNow.setBuyer(eBuyer);
		eBuyNow.setDate(buyingDate);
		eBuyNow.setItem(eItem);
		eBuyNow.setQuantity(quantity);

		try {
			this.em.persist(eBuyNow);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the buy-now.", e);
		}

		return eBuyNow.convertToDTO();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Comment persistComment(User fromUser, User toUser, Item item,
			String comment, int rating, Date commentDate)
			throws BusinessObjectsPersistenceServiceException {

		// both users and the item are already persisted objects and they have
		// identifiers.
		int fromUserId = fromUser.getId();
		EUser eFromUser = null;
		try {
			eFromUser = this.em.find(EUser.class, fromUserId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the comment while searching for the user with id = "
							+ fromUserId + ".", e);
		}
		if (eFromUser == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the comment: No user with id = "
							+ fromUserId + " found.");
		}

		int toUserId = toUser.getId();
		EUser eToUser = null;
		try {
			eToUser = this.em.find(EUser.class, toUserId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the comment while searching for the user with id = "
							+ toUserId + ".", e);
		}
		if (eToUser == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the comment: No user with id = "
							+ toUserId + " found.");
		}

		int itemId = item.getId();
		EItem eItem = null;
		try {
			eItem = this.em.find(EItem.class, itemId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the comment while searching for the item with id = "
							+ itemId + ".", e);
		}
		if (eItem == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the comment: No item with id = "
							+ itemId + " found.");
		}

		EComment eComment = new EComment();
		eComment.setComment(comment);
		eComment.setDate(commentDate);
		eComment.setRating(rating);
		eComment.setToUser(eToUser);
		eComment.setFromUser(eFromUser);
		eComment.setItem(eItem);

		try {
			this.em.persist(eComment);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in persisting the comment.", e);
		}

		return eComment.convertToDTO();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean reduceInventoryItem(Item item, int numberOfRequestedInstances)
			throws BusinessObjectsPersistenceServiceException {
		try {
			EInventoryItem eInventoryItem = this.findInventoryItemForItem(item
					.getId());
			int availableQuantity = eInventoryItem.getAvailableQuantity();
			if (numberOfRequestedInstances > availableQuantity) {
				return false;
			} else {
				eInventoryItem.setAvailableQuantity(availableQuantity
						- numberOfRequestedInstances);
// PM			this.em.flush();
				return true;
			}
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in updating the inventory to reduce the available instances for item " + item + ".", e);
//			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InventoryItem increaseInventoryItem(Item item,
			int numberOfReturnedInstances)
			throws BusinessObjectsPersistenceServiceException {
		try {
			EInventoryItem eInventoryItem = this.findInventoryItemForItem(item
					.getId());
			eInventoryItem.setAvailableQuantity(eInventoryItem
					.getAvailableQuantity() + numberOfReturnedInstances);
			this.em.flush();
			return eInventoryItem.convertToDTO();
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in updating the inventory to increase the available instances for item "
							+ item + ".", e);
		}
	}

	/**
	 * Retrieves the inventory item for item with the identifier
	 * <code>itemId</code>.
	 * 
	 * @param itemId
	 *            identifier of the item.
	 * @return the inventory item
	 * @throws BusinessObjectsPersistenceServiceException
	 *             if there is a failure in retrieving the inventory item
	 */
	private EInventoryItem findInventoryItemForItem(int itemId)
			throws BusinessObjectsPersistenceServiceException {
		EItem eItem = null;
		try {
			eItem = this.em.find(EItem.class, itemId);
		} catch (Exception e) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in updating the inventory. Item with id = "
							+ itemId + " cannot be retrieved", e);
		}

		if (eItem == null) {
			throw new BusinessObjectsPersistenceServiceException(
					"Failure in updating the inventory. There is no item with id = "
							+ itemId + " in the database.");
		} else {
			try {
				Query query = this.em
						.createNamedQuery("findInventoryItemByItem");
				query.setLockMode(LockModeType.valueOf(System.getProperty("lockmode")));
				query.setParameter("item", eItem);
				try {
					EInventoryItem eInventoryItem = (EInventoryItem) query
							.getSingleResult();
					return eInventoryItem;
				} catch (NoResultException nre) {
					throw new BusinessObjectsPersistenceServiceException(
							"Failure in updating the inventory. There is no item with id = "
									+ itemId + " in the inventory.");
				} catch (NonUniqueResultException nure) {
					throw new BusinessObjectsPersistenceServiceException(
							"Failure in updating the inventory. Theitem with id = "
									+ itemId
									+ " is not uniquely identifiable in the inventory.");
				}
			} catch (Exception e) {
				throw new BusinessObjectsPersistenceServiceException(
						"Failure in updating the inventory for item with id = "
								+ itemId + ".");
			}
		}
	}

}
