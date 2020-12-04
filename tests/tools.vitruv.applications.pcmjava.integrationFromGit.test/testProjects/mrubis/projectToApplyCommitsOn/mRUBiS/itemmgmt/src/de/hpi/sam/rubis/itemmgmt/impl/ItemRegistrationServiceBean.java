package de.hpi.sam.rubis.itemmgmt.impl;

import java.util.Collections;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.authservice.AuthenticationService;
import de.hpi.sam.rubis.authservice.AuthenticationServiceException;
import de.hpi.sam.rubis.entity.Category;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.entity.User;
import de.hpi.sam.rubis.itemmgmt.ItemRegistrationService;
import de.hpi.sam.rubis.itemmgmt.ItemRegistrationServiceException;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceService;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceServiceException;
import de.hpi.sam.rubis.queryservice.BasicQueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;

/**
 * Implementation of the {@link ItemRegistrationService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = ItemRegistrationService.NAME)
public class ItemRegistrationServiceBean implements ItemRegistrationService {

	@EJB
	private BusinessObjectsPersistenceService businessObjectsPersistenceService;

	@EJB
	private AuthenticationService authenticationService;

	@EJB
	private BasicQueryService basicQueryService;
	
    private boolean suppressResults = Boolean.getBoolean("suppressResults");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item registerItem(String name, String description,
			int intitialQuantity, float initialPrice, float buyNowPrice,
			float reservcePrice, Date startDate, Date endDate, int sellerId,
			String password, int categoryId)
			throws ItemRegistrationServiceException {

		long currentTimeMillis = System.currentTimeMillis();
		long oneHour = 1000 * 60 * 60;

		if (name == null || name.equals("") || description == null
				|| description.equals("") || intitialQuantity <= 0
				|| initialPrice < 0 || buyNowPrice < 0 || reservcePrice < 0
				|| startDate.before(new Date(currentTimeMillis - oneHour))
				|| endDate.before(new Date(currentTimeMillis + oneHour))
				|| sellerId <= 0 || password == null || password.equals("")
				|| categoryId <= 0) {

			String pwd = "";
			if (password == null || password.equals("")) {
				pwd = "NOT INDICATED";
			} else {
				for (int i = 0; i < password.length(); i++) {
					pwd = pwd + "*";
				}
			}

			throw new ItemRegistrationServiceException(
					"Failure in registering an item. Some information are not properly set: name: "
							+ name + ", description: " + description
							+ ", intitial quantity: " + intitialQuantity
							+ ", initialPrice: " + initialPrice
							+ ", buyNowPrice: " + buyNowPrice
							+ ", reservcePrice: " + reservcePrice
							+ ", startDate: " + startDate + ", endDate: "
							+ endDate + ", seller: " + sellerId
							+ ", password: " + pwd + ", category: "
							+ categoryId);
		}

		User seller = null;
		try {
			seller = this.basicQueryService.findUserById(sellerId);
			User authenticatedUser = this.authenticationService.authenticate(
					seller.getNickname(), password);
			if (authenticatedUser.getId().intValue() != seller.getId()
					.intValue()) {
				throw new ItemRegistrationServiceException(
						"Failure in registering the item: user/seller with id = "
								+ seller.getId().intValue() + "/"
								+ authenticatedUser.getId().intValue()
								+ " and nickname = " + seller.getNickname()
								+ "/" + authenticatedUser.getNickname()
								+ " cannot be authenticated.");
			}

		} catch (QueryServiceException e) {
			throw new ItemRegistrationServiceException(
					"Failure in registering the item: user/seller with id = "
							+ sellerId + " cannot be retrieved.", e);
		} catch (AuthenticationServiceException e) {
			throw new ItemRegistrationServiceException(
					"Failure in registering the item: user/seller with id = "
							+ sellerId + " cannot be authenticated.", e);
		}

		Category category = null;
		try {
			category = this.basicQueryService.findCategoryById(categoryId);
		} catch (QueryServiceException e) {
			throw new ItemRegistrationServiceException(
					"Failure in registering the item: category with id = "
							+ categoryId + " cannot be retrieved.", e);
		}

		try {
			Item item = this.businessObjectsPersistenceService.persistItem(
					name, description, intitialQuantity, initialPrice,
					buyNowPrice, reservcePrice, startDate, endDate, seller,
					category);
            if (suppressResults) {
                return null;
            }
            return item;
		} catch (BusinessObjectsPersistenceServiceException e) {
			throw new ItemRegistrationServiceException(
					"Failure in registering the item: " + e.getMessage(), e);
		}
	}
}
