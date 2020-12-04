package de.hpi.sam.rubis.reputationservice;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.Comment;

/**
 * Service for the user reputation system.
 * 
 * @author thomas
 * 
 */
@Remote
public interface ReputationService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "ReputationService";

	/**
	 * The user having the nickname <code>nickname</code> gives a comment and a
	 * rating to the user with the identifier <code>toUserId</code> after having
	 * bought the item with identifier <code>itemId</code> from this user.
	 * 
	 * @param nickname
	 *            the nickname of the user giving the comment
	 * @param password
	 *            the password of the user giving the comment
	 * @param toUserId
	 *            identifier of the user receiving the comment
	 * @param itemId
	 *            identifier of the corresponding item
	 * @param comment
	 *            the comment as text
	 * @param rating
	 *            a rating of the user or item
	 * @return the created comment
	 * @throws ReputationServiceException
	 *             if there is a failure in giving the comment
	 */
	public Comment giveComment(String nickname, String password, int toUserId,
			int itemId, String comment, int rating)
			throws ReputationServiceException;

}
