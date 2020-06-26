package de.hpi.sam.rubis.queryservice.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.eclipse.persistence.config.QueryHints;

import de.hpi.sam.rubis.RubisNameSchema;
import de.hpi.sam.rubis.entity.Bid;
import de.hpi.sam.rubis.entity.BidHistory;
import de.hpi.sam.rubis.entity.BuyNow;
import de.hpi.sam.rubis.entity.Category;
import de.hpi.sam.rubis.entity.Comment;
import de.hpi.sam.rubis.entity.EBid;
import de.hpi.sam.rubis.entity.EBuyNow;
import de.hpi.sam.rubis.entity.ECategory;
import de.hpi.sam.rubis.entity.EComment;
import de.hpi.sam.rubis.entity.EInventoryItem;
import de.hpi.sam.rubis.entity.EItem;
import de.hpi.sam.rubis.entity.InventoryItem;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.queryservice.QueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;

/*
 * TODO test queries
 */
/**
 * Implementation of the {@link QueryService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = QueryService.NAME)
public class QueryServiceBean implements QueryService {

	@PersistenceContext(unitName = RubisNameSchema.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Category> findCategoriesInRegion(String regionName)
			throws QueryServiceException {
	    // TODO test if this query works as expected
	    String queryString = "SELECT DISTINCT i.category FROM EItem i "
	            + "WHERE i.seller.region.name = :regionName";
		try {
			Query query = this.em.createQuery(queryString);
			query.setParameter("regionName", regionName);
			List<ECategory> categoriesInRegion = query.getResultList();
			
			List<Category> categories = new ArrayList<Category>();
			for (ECategory eCategory : categoriesInRegion) {
			     categories.add(eCategory.convertToDTO());
			}
			return categories;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving all categories in region "
							+ regionName + ".", e);
		}
	}

	/**
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Item> findCurrentItemsInCategory(int categoryId, int maxResult)
			throws QueryServiceException {
		String queryString = "SELECT i FROM EItem i "
				+ "WHERE i.category.id = :categoryId "
				+ "AND i.endDate > CURRENT_TIMESTAMP "
				+ "ORDER BY i.endDate ASC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("categoryId", categoryId);
			if (maxResult > 0) {
				query = query.setMaxResults(maxResult);
			}
			List<EItem> eItems = query.getResultList();

			List<Item> items = new LinkedList<Item>();
			for (EItem eItem : eItems) {
				items.add(eItem.convertToDTO());
			}
			return items;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving current items of category "
							+ categoryId + ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Item> findCurrentItemsInCategoryAndRegion(int categoryId,
			int regionId, int maxResult) throws QueryServiceException {
		String queryString = "SELECT item FROM EItem item "
				+ "WHERE item.category.id = :categoryId "
				+ "AND item.seller.region.id = :regionId "
				+ "AND item.endDate > CURRENT_TIMESTAMP "
				+ "ORDER BY item.endDate ASC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("categoryId", categoryId);
			query = query.setParameter("regionId", regionId);
			if (maxResult > 0) {
				query = query.setMaxResults(maxResult);
			}
			List<EItem> eItems = query.getResultList();

			List<Item> items = new LinkedList<Item>();
			for (EItem eItem : eItems) {
				items.add(eItem.convertToDTO());
			}

			return items;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving all current items of category "
							+ categoryId + " that are offered in region "
							+ regionId, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Bid> findItemBidHistory(int itemId, int maxToCollect)
			throws QueryServiceException {
		String queryString = "SELECT b FROM EBid b "
				+ "WHERE b.item.id = :itemId ORDER BY b.date DESC";
		try {
			Query query = this.em.createQuery(queryString);
//			query.setHint(QueryHints.FETCH, "b.user");
//			query.setHint(QueryHints.FETCH, "b.user.region");
//			query.setHint(QueryHints.FETCH, "b.item");
//			query.setHint(QueryHints.FETCH, "b.item.seller");
//			query.setHint(QueryHints.FETCH, "b.item.seller.region");
//			query.setHint(QueryHints.FETCH, "b.item.category");
//			query.setHint(QueryHints.BATCH_TYPE, BatchFetchType.EXISTS);
//            query.setHint(QueryHints.BATCH, "b.user");
//            query.setHint(QueryHints.BATCH, "b.user.region");
//            query.setHint(QueryHints.BATCH, "b.item");
//            query.setHint(QueryHints.BATCH, "b.item.seller");
//            query.setHint(QueryHints.BATCH, "b.item.seller.region");
//            query.setHint(QueryHints.BATCH, "b.item.category");
			query = query.setParameter("itemId", itemId);
			query.setMaxResults(maxToCollect);
			List<EBid> eBids = query.getResultList();

			List<Bid> bids = new LinkedList<Bid>();
			for (EBid eBid : eBids) {
				bids.add(eBid.convertToDTO());
			}
			return bids;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving all bids on item " + itemId
							+ " order by the bidding date.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Bid findItemMaxBid(int itemId) throws QueryServiceException {
		String queryString = "SELECT bid FROM EBid bid "
				+ "WHERE bid.item.id = :itemId AND bid.bidPrice IN "
				+ "(SELECT MAX(bidPrice) FROM EBid "
				+ "WHERE bid.item.id = :itemId GROUP BY bid.item.id) "
				+ "ORDER BY bid.date ASC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("itemId", itemId);
			List<EBid> eBids = query.getResultList();

			Bid bid = null;
			if (!eBids.isEmpty()) {
				bid = eBids.get(0).convertToDTO();
			}
			return bid;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the highest bid on item " + itemId
							+ ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Bid> findItemMaxBids(int itemId, int maxToCollect)
			throws QueryServiceException {
		String queryString = "SELECT b FROM EBid b "
				+ "WHERE b.item.id = :itemId ORDER BY b.bidPrice DESC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("itemId", itemId);
			if (maxToCollect > 0) {
				query = query.setMaxResults(maxToCollect);
			}
			List<EBid> eBids = query.getResultList();

			List<Bid> bids = new LinkedList<Bid>();
			for (EBid eBid : eBids) {
				bids.add(eBid.convertToDTO());
			}
			return bids;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the highest " + maxToCollect
							+ " bids on item " + itemId + ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Item> findPastItemsInCategory(int categoryId, int maxResult)
			throws QueryServiceException {
		String queryString = "SELECT i FROM EItem i "
				+ "WHERE i.category.id = :categoryId "
				+ "AND i.endDate < CURRENT_TIMESTAMP "
				+ "ORDER BY i.endDate DESC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("categoryId", categoryId);
			if (maxResult > 0) {
				query = query.setMaxResults(maxResult);
			}
			List<EItem> eItems = query.getResultList();

			List<Item> items = new LinkedList<Item>();
			for (EItem eItem : eItems) {
				items.add(eItem.convertToDTO());
			}
			return items;
		} catch (Exception e) {
			throw new QueryServiceException("Failure in retrieving the latest "
					+ maxResult + " items of category " + categoryId
					+ " and whose auctions have ended.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Item> findPastItemsInCategoryAndRegion(int categoryId,
			int regionId, int maxResult) throws QueryServiceException {
		String queryString = "SELECT item FROM EItem item "
				+ "WHERE item.category.id = :categoryId "
				+ "AND item.seller.region.id = :regionId "
				+ "AND item.endDate < CURRENT_TIMESTAMP "
				+ "ORDER BY item.endDate DESC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("categoryId", categoryId);
			query = query.setParameter("regionId", regionId);
			if (maxResult > 0) {
				query = query.setMaxResults(maxResult);
			}
			List<EItem> eItems = query.getResultList();

			List<Item> items = new LinkedList<Item>();
			for (EItem eItem : eItems) {
				items.add(eItem.convertToDTO());
			}

			return items;
		} catch (Exception e) {
			throw new QueryServiceException("Failure in retrieving the latest "
					+ maxResult + " items of category " + categoryId
					+ " that have been offered in region " + regionId, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<BuyNow> findUserBuyNows(int userId, int days)
			throws QueryServiceException {
		String queryString = "SELECT b FROM EBuyNow b "
				+ "WHERE b.buyer.id = :userId " + "AND b.date >= :dateLimit "
				+ "ORDER BY b.date DESC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("userId", userId);

			if (days <= 0) {
				days = 365;
			}
			long daysInMillis = days * 24 * 60 * 60 * 1000;
			long threshold = System.currentTimeMillis() - daysInMillis;
			Date thresholdDate = new Date(threshold);
			query = query.setParameter("dateLimit", thresholdDate);

			List<EBuyNow> eBuyNows = query.getResultList();

			List<BuyNow> buyNows = new LinkedList<BuyNow>();
			for (EBuyNow eBuyNow : eBuyNows) {
				buyNows.add(eBuyNow.convertToDTO());
			}

			return buyNows;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the buy-nows of user " + userId
							+ " in the last " + days + " days.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Native queries working with Apache Derby. JPA Query Language does not
	 * support a SELECT statements, i.e., sub queries, in the FROM clause.
	 * 
	 * Open Issue: How to automatically map a result set to an entity using
	 * SqlResultSetMapping
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Bid> findUserCurrentMaxBids(int userId)
			throws QueryServiceException {
		String queryString = "SELECT b.id, b.user_id, b.item_id, b.quantity, b.bid_price, b.max_bid_price, b.bid_date "
				+ "FROM BIDS b, "
				+ "(SELECT item_id AS item_max, MAX(bid_price) as maxprice FROM BIDS GROUP BY item_id) maxbids, "
				+ "ITEMS i "
				+ "WHERE b.item_id = maxbids.item_max AND i.id = b.item_id AND b.bid_price = maxbids.maxprice "
				+ "AND b.user_id = "
				+ userId
				+ " AND i.end_date > CURRENT_TIMESTAMP";

		try {

			Query query = this.em.createNativeQuery(queryString);
			List<Vector> resultSet = query.getResultList();

			List<Bid> currentMaxBids = new LinkedList<Bid>();

			for (Vector row : resultSet) {
				int bidId = (Integer) row.elementAt(0);
				EBid eBid = this.em.find(EBid.class, bidId);
				if (eBid != null) {
					currentMaxBids.add(eBid.convertToDTO());
				}
			}

			return currentMaxBids;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the current max bids of the user "
							+ userId + ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Item> findUserCurrentSellings(int userId)
			throws QueryServiceException {
		String queryString = "SELECT i FROM EItem i "
				+ "WHERE i.seller.id = :userId "
				+ "AND i.endDate >= CURRENT_TIMESTAMP "
				+ "ORDER BY i.endDate ASC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("userId", userId);
			List<EItem> eItems = query.getResultList();

			List<Item> items = new LinkedList<Item>();
			for (EItem eItem : eItems) {
				items.add(eItem.convertToDTO());
			}

			return items;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the currently offered sellings of user "
							+ userId + ".", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Item> findUserPastSellings(int userId, int days)
			throws QueryServiceException {
		String queryString = "SELECT i FROM EItem i "
				+ "WHERE i.seller.id = :userId "
				+ "AND i.endDate <= CURRENT_TIMESTAMP "
				+ "AND i.endDate >= :dateLimit " + "ORDER BY i.endDate DESC";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("userId", userId);

			if (days <= 0) {
				days = 365;
			}
			long daysInMillis = days * 24 * 60 * 60 * 1000;
			long threshold = System.currentTimeMillis() - daysInMillis;
			Date thresholdDate = new Date(threshold);
			query = query.setParameter("dateLimit", thresholdDate);

			List<EItem> eItems = query.getResultList();

			List<Item> items = new LinkedList<Item>();
			for (EItem eItem : eItems) {
				items.add(eItem.convertToDTO());
			}

			return items;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the offered sellings of user "
							+ userId + " in the past " + days + " days.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Native queries working with Apache Derby. JPA Query Language does not
	 * support a SELECT statements, i.e., sub queries, in the FROM clause.
	 * 
	 * Open Issue: How to automatically map a result set to an entity using
	 * SqlResultSetMapping
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Bid> findUserPastWinningBids(int userId)
			throws QueryServiceException {
		String queryString = "SELECT b.id, b.user_id, b.item_id, b.quantity, b.bid_price, b.max_bid_price, b.bid_date "
				+ "FROM BIDS b, "
				+ "(SELECT item_id AS item_max, MAX(bid_price) as maxprice FROM BIDS GROUP BY item_id) maxbids, "
				+ "ITEMS i "
				+ "WHERE b.item_id = maxbids.item_max AND i.id = b.item_id AND b.bid_price = maxbids.maxprice "
				+ "AND b.user_id = "
				+ userId
				+ " AND i.end_date <= CURRENT_TIMESTAMP";

		try {

			Query q = this.em.createNativeQuery(queryString);

			List<Vector> resultSet = q.getResultList();

			List<Bid> pastMaxBids = new LinkedList<Bid>();

			for (Vector row : resultSet) {
				int bidId = (Integer) row.elementAt(0);
				EBid eBid = this.em.find(EBid.class, bidId);
				if (eBid != null) {
					pastMaxBids.add(eBid.convertToDTO());
				}
			}

			return pastMaxBids;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Error in retrieving the past max bids of the user: "
							+ e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfBidsForItem(int itemId) throws QueryServiceException {
		 String queryString = "SELECT COUNT(b) FROM EBid b WHERE b.item.id = :itemId";
		try {
//			EItem eItem = this.em.find(EItem.class, itemId);
//			if (eItem == null) {
//				throw new QueryServiceException("Item with id " + itemId
//						+ " does not exist.");
//			}
			 Query query = this.em.createQuery(queryString);
			query = query.setParameter("itemId", itemId);
			int count = (Integer) query.getSingleResult();
//			int count = eItem.getBids().size();
			return count;
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the number of bids for item with id "
							+ itemId + ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BidHistory findSuccessfulBidsForItem(int itemId)
			throws QueryServiceException {

		EItem eItem = null;
		try {
			eItem = this.em.find(EItem.class, itemId);
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the successful bids for item with id = "
							+ itemId + ".", e);
		}
		if (eItem == null) {
			throw new QueryServiceException(
					"Failure in retrieving the successful bids for item with id = "
							+ itemId + " because the item cannot be found.");
		}

		Date currentDate = new Date(System.currentTimeMillis());
		if (currentDate.before(eItem.getEndDate())) {
			throw new QueryServiceException(
					"The successful bids for item with id = "
							+ itemId
							+ " are not determined because the auction has not ended yet.");
		}

		// auction has ended

		// how many item instances have been bought by buy-nows?
		// how many instances are available for bids?
		InventoryItem ii = this.retrieveAvailabilityOfItem(eItem.getId());
		int availableItemInstancesForBids = ii.getAvailableQuantity();

		if (availableItemInstancesForBids < 0) {
			throw new QueryServiceException(
					"Failure in retrieving the successful bids. The number of item instances avalibale for bids is negative.");
		} else if (availableItemInstancesForBids == 0) {
			return BidHistory.EMPTY;
		} else {
			// availableItemInstancesForBids > 0
			int stillAvailableItems = availableItemInstancesForBids;
			List<Bid> succssfulBids = new LinkedList<Bid>();

			try {
				// at most <code>availableItemInstancesForBids</code> bids are
				// successful if each bid just buys one item instance. The
				// item instances available for bids are distributed among the
				// bids, from highest to lowest bid prices
				List<Bid> maxBids = this.findItemMaxBids(itemId,
						availableItemInstancesForBids);
				Iterator<Bid> maxBidsIterator = maxBids.iterator();
				while (stillAvailableItems > 0 && maxBidsIterator.hasNext()) {
					Bid maxBid = maxBidsIterator.next();
					stillAvailableItems = stillAvailableItems
							- maxBid.getQuantity();
					succssfulBids.add(maxBid);
				}
				return new BidHistory(succssfulBids);
			} catch (QueryServiceException e) {
				throw new QueryServiceException(
						"Failure in retrieving the successful bids.", e);
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Comment findComment(int fromUserId, int toUserId, int itemId)
			throws QueryServiceException {
		String queryString = "SELECT c FROM EComment c "
				+ "WHERE c.fromUser.id = :fromUserId "
				+ "AND c.toUser.id = :toUserId " + "AND c.item.id = :itemId";
		try {
			Query query = this.em.createQuery(queryString);
			query = query.setParameter("fromUserId", fromUserId);
			query = query.setParameter("toUserId", toUserId);
			query = query.setParameter("itemId", itemId);

			try {
				EComment eComment = (EComment) query.getSingleResult();
				return eComment.convertToDTO();
			} catch (NoResultException nre) {
				return null;
			} catch (NonUniqueResultException nure) {
				throw new QueryServiceException(
						"Failure in retrieving the unique comment from user with id = "
								+ fromUserId
								+ " to user with id = "
								+ toUserId
								+ " about item with id = "
								+ itemId
								+ ". There are multiple comments, which is not allowed.",
						nure);
			} catch (Exception e) {
				throw new QueryServiceException(
						"Failure in retrieving the unique comment from user with id = "
								+ fromUserId + " to user with id = " + toUserId
								+ " about item with id = " + itemId + ".", e);
			}

		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the comment from user with id = "
							+ fromUserId + " to user with id = " + toUserId
							+ " about item with id = " + itemId + ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InventoryItem retrieveAvailabilityOfItem(int itemId)
			throws QueryServiceException {

		EItem eItem = null;
		try {
			eItem = this.em.find(EItem.class, itemId);
		} catch (Exception e) {
			throw new QueryServiceException(
					"Failure in retrieving the number of available instances of item with id = "
							+ itemId + ". Item cannot be retrieved", e);
		}

		if (eItem == null) {
			throw new QueryServiceException(
					"Failure in retrieving the number of available instances of item with id = "
							+ itemId
							+ ". There is no item with this id in the database.");
		} else {
			try {
				Query query = this.em
						.createNamedQuery("findInventoryItemByItem");
				query.setParameter("item", eItem);
				query.setLockMode(LockModeType.valueOf(System.getProperty("lockmode")));
				try {
					EInventoryItem eInventoryItem = (EInventoryItem) query
							.getSingleResult();
					return eInventoryItem.convertToDTO();
				} catch (NoResultException nre) {
					throw new QueryServiceException(
							"Failure in retrieving the number of available instances of item with id = "
									+ itemId
									+ ". There is no item with this id in the inventory.");
				} catch (NonUniqueResultException nure) {
					throw new QueryServiceException(
							"Failure in retrieving the number of available instances of item with id = "
									+ itemId
									+ ". The item is not uniquely identifiable in the inventory.");
				}
			} catch (Exception e) {
				throw new QueryServiceException(
						"Failure in retrieving the number of available instances of item with id = "
								+ itemId + ".", e);
			}
		}

	}
}
