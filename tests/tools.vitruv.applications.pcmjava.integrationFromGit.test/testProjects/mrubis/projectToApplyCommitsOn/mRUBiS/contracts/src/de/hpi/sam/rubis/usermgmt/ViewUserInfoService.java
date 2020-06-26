package de.hpi.sam.rubis.usermgmt;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.UserInfo;

/**
 * Retrieves information about any user for the currently logged in user.
 * 
 * @author thomas
 * 
 */
@Remote
public interface ViewUserInfoService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "ViewUserInfoService";

	/**
	 * Retrieves information about any user for the currently logged in user.
	 * 
	 * @param userId
	 *            the identifier of the user about whom information should be
	 *            retrieved.
	 * @return information about the user with the identifier
	 *         <code>userId</code>.
	 * @throws ViewUserInfoServiceException
	 *             if there is a failure in retrieving the information about the
	 *             user or if there is no user with the specified identifier.
	 */
	public UserInfo getUserInfo(int userId) throws ViewUserInfoServiceException;

}
