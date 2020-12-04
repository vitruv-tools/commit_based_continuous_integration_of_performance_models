package de.hpi.sam.rubis.usermgmt.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.entity.CustomerClass;
import de.hpi.sam.rubis.entity.Region;
import de.hpi.sam.rubis.entity.User;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceService;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceServiceException;
import de.hpi.sam.rubis.queryservice.BasicQueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;
import de.hpi.sam.rubis.usermgmt.UserRegistrationService;
import de.hpi.sam.rubis.usermgmt.UserRegistrationServiceException;

/**
 * Implementation of the {@link UserRegistrationService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = UserRegistrationService.NAME)
public class UserRegistrationServiceBean implements UserRegistrationService {

	@EJB
	private BusinessObjectsPersistenceService persistenceService;

	@EJB
	private BasicQueryService basicQueryService;

    private boolean suppressResults = Boolean.getBoolean("suppressResults");
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public User registerUser(String firstname, String lastname,
			String nickname, String email, String password, String regionName,
			CustomerClass customerClass)
			throws UserRegistrationServiceException {

		Region region;
		User user;

		if (regionName == null || regionName.equals("")) {
			throw new UserRegistrationServiceException(
					"User can not be registered: Region is not specified.");
		}

		try {
			region = this.basicQueryService.findRegionByName(regionName);
		} catch (QueryServiceException e) {
			throw new UserRegistrationServiceException(
					"Failure in user registration: " + e.getMessage(), e);
		}

		boolean nicknameUsed = true;

		try {
			nicknameUsed = this.basicQueryService.isUserNicknameUsed(nickname);
		} catch (QueryServiceException e) {
			throw new UserRegistrationServiceException(
					"Failure in user registration: " + e.getMessage(), e);
		}

		if (nicknameUsed) {
			throw new UserRegistrationServiceException(
					"Failure in user registration: Nickname " + nickname
							+ " is already used.");
		} else {
			try {
				user = this.persistenceService.persistUser(firstname, lastname,
						nickname, email, password, region, customerClass);
			} catch (BusinessObjectsPersistenceServiceException e) {
				throw new UserRegistrationServiceException(
						"Failure in user registration: " + e.getMessage(), e);
			}
		}

        if (suppressResults) {
            return null;
        }
        return user;
	}

}
