package de.hpi.sam.rubis.queryservice;

import java.util.List;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.BuyNowHistory;
import de.hpi.sam.rubis.entity.Category;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.entity.Region;
import de.hpi.sam.rubis.entity.User;

/*
 * Queries of this service requires to scan only one table (no joins, etc.).
 */
/**
 * Service to retrieve basic information from the database.
 * 
 * @author thomas
 * 
 */
@Remote
public interface BasicQueryService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "BasicQueryService";

	/**
	 * Finds all users in the database.
	 * 
	 * @return the list of all users.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving all users from the
	 *             database.
	 */
	public List<User> findAllUsers() throws QueryServiceException;

	/**
	 * Finds a user by her identifier.
	 * 
	 * @param userId
	 *            the identifier of the user.
	 * @return the user with the identifier <code>userId</code>, or
	 *         <code>null</code> if there is no user with the specified
	 *         identifier.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the user.
	 */
	public User findUserById(int userId) throws QueryServiceException;

	/**
	 * Finds the user with the specified nickname.
	 * 
	 * @param nickname
	 *            the nickname of the user.
	 * @return the user with specified nickname, or <code>null</code> if there
	 *         is no user with the specified nickname.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the user.
	 */
	public User findUserByNickname(String nickname)
			throws QueryServiceException;

	/**
	 * Finds all categories in the database.
	 * 
	 * @return the list of all categories.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving all categories from the
	 *             database.
	 */
	public List<Category> findAllCategories() throws QueryServiceException;

	/**
	 * Finds a category by its identifier.
	 * 
	 * @param categoryId
	 *            the identifier of the category.
	 * @return the category with the specified identifier, or <code>null</code>
	 *         if there is no category with the specified identifier.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the category.
	 */
	public Category findCategoryById(int categoryId)
			throws QueryServiceException;

	/**
	 * Finds the category with the exactly specified name.
	 * 
	 * @param categoryName
	 *            the name of the category.
	 * @return the category with specified name, or <code>null</code> if there
	 *         is no category with exactly the given name.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the category.
	 */
	public Category findCategoryByName(String categoryName)
			throws QueryServiceException;

	/**
	 * Finds the categories whose names contain the specified
	 * <code>categoryName</code> as a sub-string.
	 * 
	 * @param categoryName
	 *            the pattern to search for in category names.
	 * @return the categories whose names contain the specified pattern. The
	 *         list might be empty.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the categories.
	 */
	public List<Category> findCategoriesByName(String categoryName)
			throws QueryServiceException;

	/**
	 * Finds all regions in the database.
	 * 
	 * @return the list of all regions.
	 * @throws QueryServiceException
	 *             if there is a failure in retrieving all regions from the
	 *             database.
	 */
	public List<Region> findAllRegions() throws QueryServiceException;

	/**
	 * Finds a region by its identifier.
	 * 
	 * @param regionId
	 *            the identifier of the region.
	 * @return the region with the specified identifier, or <code>null</code> if
	 *         there is no region with the specified identifier.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the region.
	 */
	public Region findRegionById(int regionId) throws QueryServiceException;

	/**
	 * Finds the region with the exactly specified name.
	 * 
	 * @param regionName
	 *            the name of the region
	 * @return the region with specified name, or <code>null</code> if there is
	 *         no region with exactly the given name.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the region.
	 */
	public Region findRegionByName(String regionName)
			throws QueryServiceException;

	/**
	 * Finds the regions whose names contain the specified
	 * <code>regionName</code> as a sub-string.
	 * 
	 * @param regionName
	 *            the pattern to search for in region names.
	 * @return the categories whose names contain the specified pattern. The
	 *         list might be empty.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the categories.
	 */
	public List<Region> findRegionsByName(String regionName)
			throws QueryServiceException;

	/**
	 * Finds all items whose names contain <code>itemName</code> as a
	 * sub-string.
	 * 
	 * @param itemName
	 *            the pattern to search for in item names.
	 * @return the list of items whose names contain the pattern
	 *         <code>itemName</code>. The list might be empty.
	 * @throws QueryServiceException
	 *             if there is a failure in searching the items.
	 */
	public List<Item> findItemsByName(String itemName)
			throws QueryServiceException;

	/**
	 * Finds an item by its identifier.
	 * 
	 * @param itemId
	 *            the identifier of the item.
	 * @return the item with the specified identifier, or <code>null</code> if
	 *         there is no item with the specified identifier.
	 * @throws QueryServiceException
	 *             if there is a failure in finding the item.
	 */
	public Item findItemById(int itemId) throws QueryServiceException;
	
    /**
     * Finds the buy now history for the item specified by its identifier.
     * 
     * @param itemId
     *            the identifier of the item.
     * @return the item's buy now history
     * @throws QueryServiceException
     *             if there is a failure in finding the item.
     */
    public BuyNowHistory findItemBuyNowsById(int itemId) throws QueryServiceException;

	/**
	 * Checks whether a nickname is already used by a user.
	 * 
	 * @param nickname
	 *            the nickname to be checked.
	 * @return <code>true</code> if the nickname is used by a user, else
	 *         <code>false</code>.
	 * @throws QueryServiceException
	 *             if there is a failure in checking the nickname.
	 */
	public boolean isUserNicknameUsed(String nickname)
			throws QueryServiceException;

}
