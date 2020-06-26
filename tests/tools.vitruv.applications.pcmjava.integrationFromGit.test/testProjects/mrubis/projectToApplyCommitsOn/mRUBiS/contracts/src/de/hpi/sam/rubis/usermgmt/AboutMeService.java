package de.hpi.sam.rubis.usermgmt;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.UserProfile;

/**
 * Service to retrieve information about the currently logged in user.
 * 
 * @author thomas
 * 
 */
@Remote
public interface AboutMeService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "AboutMeService";

	/**
	 * Authenticates the user and gets information about the user.
	 * 
	 * @param username
	 *            the user's nickname
	 * @param password
	 *            the user's password
	 * @return comprehensive information about the authenticated user
	 * @throws AboutMeServiceException
	 *             if there is a failure in authenticating the user or in
	 *             retrieving information about the user.
	 */
	public UserProfile getAboutMe(String username, String password)
			throws AboutMeServiceException;

}
