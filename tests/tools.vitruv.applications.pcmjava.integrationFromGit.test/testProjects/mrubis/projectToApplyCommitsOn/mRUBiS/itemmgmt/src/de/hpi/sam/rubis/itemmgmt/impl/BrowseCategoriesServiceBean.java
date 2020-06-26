package de.hpi.sam.rubis.itemmgmt.impl;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.entity.Category;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.itemmgmt.BrowseCategoriesService;
import de.hpi.sam.rubis.itemmgmt.BrowseCategoriesServiceException;
import de.hpi.sam.rubis.queryservice.BasicQueryService;
import de.hpi.sam.rubis.queryservice.QueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;

/**
 * Implementation of the {@link BrowseCategoriesService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = BrowseCategoriesService.NAME)
public class BrowseCategoriesServiceBean implements BrowseCategoriesService {

	@EJB
	private QueryService queryService;

	@EJB
	private BasicQueryService basicQueryService;

	private boolean suppressResults = Boolean.getBoolean("suppressResults");
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Category> getAllCategories()
			throws BrowseCategoriesServiceException {

		try {
			List<Category> allCategories = this.basicQueryService
					.findAllCategories();
            if (suppressResults) {
                return Collections.emptyList();
            }
            return allCategories;
		} catch (QueryServiceException e) {
			throw new BrowseCategoriesServiceException(
					"Error in retrieving all categories.", e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Category> getCategoriesInRegion(String regionName)
			throws BrowseCategoriesServiceException {
		try {
			List<Category> categories = this.queryService
					.findCategoriesInRegion(regionName);
            if (suppressResults) {
                return Collections.emptyList();
            }
			return categories;
		} catch (QueryServiceException e) {
			throw new BrowseCategoriesServiceException(
					"Error in retrieving all categories in region "
							+ regionName + ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Category> getCategoriesByName(String categoryName)
			throws BrowseCategoriesServiceException {
		try {
			List<Category> categories = this.basicQueryService
					.findCategoriesByName(categoryName);
            if (suppressResults) {
                return Collections.emptyList();
            }
			return categories;
		} catch (QueryServiceException e) {
			throw new BrowseCategoriesServiceException(
					"Error in retrieving categories with name " + categoryName
							+ ".", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Item> getItemsByName(String itemName)
			throws BrowseCategoriesServiceException {
		try {
			List<Item> items = this.basicQueryService.findItemsByName(itemName);
			items.size();
            if (suppressResults) {
                return Collections.emptyList();
            }
			return items;
		} catch (QueryServiceException e) {
			throw new BrowseCategoriesServiceException(
					"Error in retrieving items with name " + itemName + ".", e);
		}
	}

}
