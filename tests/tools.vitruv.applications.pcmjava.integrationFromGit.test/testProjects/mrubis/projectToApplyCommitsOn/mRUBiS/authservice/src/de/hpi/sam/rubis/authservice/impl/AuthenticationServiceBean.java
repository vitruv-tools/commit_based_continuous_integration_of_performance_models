package de.hpi.sam.rubis.authservice.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.authservice.AuthenticationService;
import de.hpi.sam.rubis.authservice.AuthenticationServiceException;
import de.hpi.sam.rubis.entity.User;
import de.hpi.sam.rubis.queryservice.BasicQueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;

/**
 * Implementation of the {@link AuthenticationService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = AuthenticationService.NAME)
public class AuthenticationServiceBean implements AuthenticationService {

	@EJB
	private BasicQueryService basicQueryService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User authenticate(String nickname, String password)
			throws AuthenticationServiceException {

		if (nickname == null) {
			throw new AuthenticationServiceException(
					"AuthenticationService: Nickname must not be null.");
		}
		if (password == null) {
			throw new AuthenticationServiceException(
					"AuthenticationService: Password must not be null.");
		}

		User user = null;
		try {
			user = this.basicQueryService.findUserByNickname(nickname);

		} catch (QueryServiceException e) {
			throw new AuthenticationServiceException(
					"Failure in authenticating the user: " + e.getMessage(), e);
		}

		if (user == null) {
			throw new AuthenticationServiceException(
					"Failure in authenticating the user: User with nickname "
							+ nickname + " does not exist.");
		} else {
			String pwd = user.getPassword();
			if (pwd.equals(password)) {
				return user;
			} else {
				throw new AuthenticationServiceException(
						"Failure in authenticating the user: User with nickname "
								+ nickname + " indicated an invalid password.");
			}
		}

	}

}
