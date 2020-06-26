package de.hpi.sam.rubis.usermgmt;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.CustomerClass;
import de.hpi.sam.rubis.entity.User;

/**
 * Service for registering a new user.
 * 
 * @author thomas
 * 
 */
@Remote
public interface UserRegistrationService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "UserRegistrationService";

	/**
	 * 
	 * Creates a new user.
	 * 
	 * @param firstname
	 *            user's first name
	 * @param lastname
	 *            user's last name
	 * @param nickname
	 *            user's nick name
	 * @param email
	 *            user's email
	 * @param password
	 *            user's password
	 * @param regionName
	 *            name of the region where the user live
	 * @param customerClass
	 *            the class of the user
	 * @return the newly registered user.
	 * @throws UserRegistrationServiceException
	 *             if there is a failure in registering the user
	 */
	public User registerUser(String firstname, String lastname,
			String nickname, String email, String password, String regionName,
			CustomerClass customerClass)
			throws UserRegistrationServiceException;

}
