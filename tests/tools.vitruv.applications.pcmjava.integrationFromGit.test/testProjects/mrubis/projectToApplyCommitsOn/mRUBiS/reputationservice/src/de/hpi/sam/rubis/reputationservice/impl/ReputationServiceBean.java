package de.hpi.sam.rubis.reputationservice.impl;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.authservice.AuthenticationService;
import de.hpi.sam.rubis.authservice.AuthenticationServiceException;
import de.hpi.sam.rubis.entity.Bid;
import de.hpi.sam.rubis.entity.BidHistory;
import de.hpi.sam.rubis.entity.BuyNow;
import de.hpi.sam.rubis.entity.BuyNowHistory;
import de.hpi.sam.rubis.entity.Comment;
import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.entity.User;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceService;
import de.hpi.sam.rubis.persistenceservice.BusinessObjectsPersistenceServiceException;
import de.hpi.sam.rubis.queryservice.BasicQueryService;
import de.hpi.sam.rubis.queryservice.QueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;
import de.hpi.sam.rubis.reputationservice.ReputationService;
import de.hpi.sam.rubis.reputationservice.ReputationServiceException;

/**
 * Implementation of the {@link ReputationService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = ReputationService.NAME)
public class ReputationServiceBean implements ReputationService {

	@EJB
	private AuthenticationService authService;

	@EJB
	private BasicQueryService basicQueryService;

	@EJB
	private QueryService queryService;

	@EJB
	private BusinessObjectsPersistenceService persistenceService;

    private boolean suppressResults = Boolean.getBoolean("suppressResults");
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Comment giveComment(String nickname, String password, int toUserId,
			int itemId, String comment, int rating)
			throws ReputationServiceException {

		User fromUser = null;
		try {
			fromUser = this.authService.authenticate(nickname, password);
			// does not return null
		} catch (AuthenticationServiceException e) {
			throw new ReputationServiceException(
					"Failure in giving the comment while authenticating the commenting user "
							+ nickname + ".", e);
		}

		Item item = null;
		try {
			item = this.basicQueryService.findItemById(itemId);
		} catch (QueryServiceException e) {
			throw new ReputationServiceException(
					"Failure in giving the comment while searching the item with id = "
							+ itemId + ".", e);
		}
		if (item == null) {
			throw new ReputationServiceException(
					"Failure in giving the comment. The item with id = "
							+ itemId + " cannot be found.");
		}

		User toUser = null;
		try {
			toUser = this.basicQueryService.findUserById(toUserId);
		} catch (QueryServiceException e) {
			throw new ReputationServiceException(
					"Failure in giving the comment while searching the user with id = "
							+ toUserId + ".", e);
		}
		if (toUser == null) {
			throw new ReputationServiceException(
					"Failure in giving the comment. The user with id = "
							+ toUserId + " cannot be found.");
		}

		// check whether fromUser has bought or successfully bid on the item
		// that was offered by toUser

		// is toUser the seller of the item?
		if (!item.getSellerId().equals(toUser.getId())) {
			// seller of the item and toUser are not the same, but they must be.
			throw new ReputationServiceException(
					"Failure in giving the comment. The user with id = "
							+ toUserId
							+ " is not the seller of the item with id = "
							+ itemId + ".");
		}

		Date currentDate = new Date(System.currentTimeMillis());

		// did fromUser has a successful bid on the item?
		boolean isFromUserAnAuctionWinner = false;
		if (currentDate.after(item.getEndDate())) {
			// auction has already ended
			try {
				BidHistory successfulBids = this.queryService
						.findSuccessfulBidsForItem(itemId);
				for (Bid successfulBid : successfulBids.getBids()) {
					if (fromUser.equals(successfulBid.getUser())) {
						isFromUserAnAuctionWinner = true;
						break;
					}
				}
			} catch (QueryServiceException e) {
				throw new ReputationServiceException(
						"Failure in giving the comment while finding successful bids for item with id "
								+ itemId + ".", e);
			}

		}
//		System.out.println("==============");
//		System.out.println("no of bids " + item.getBids().size());
//		System.out.println("no of buy-nows " + item.getBuyNows().size());
//		System.out.println("FROM USER: " + fromUser.infoString());

		// did fromUser bought the item?
		boolean isFromUserABuyer = false;
		BuyNowHistory buyNows;
        try {
            buyNows = this.basicQueryService.findItemBuyNowsById(item.getId());
        } catch (QueryServiceException e1) {
            throw new ReputationServiceException(e1);
        }
		for (BuyNow buyNow : buyNows.getBuyNows()) {
//			System.out.println("Buyer: " + buyNow.getBuyer().infoString());
			if (fromUser.equals(buyNow.getBuyer())) {
				isFromUserABuyer = true;
				break;
			}
		}

//		System.out.println(isFromUserAnAuctionWinner);
//		System.out.println(isFromUserABuyer);
//		System.out.println(!isFromUserAnAuctionWinner & !isFromUserABuyer);
//		System.out.println("==============");

		if (!isFromUserAnAuctionWinner & !isFromUserABuyer) {
			throw new ReputationServiceException(
					"Failure in giving the comment. The user with nickname = "
							+ nickname + " and id = " + fromUser.getId()
							+ " has neither bought nor won the item with id = "
							+ itemId + ".");
		}

		// fromUser is a buyer of the item that is sold by toUser
		// is there already a comment for the triple fromUser, toUser, item?
		// for the triple, at most one comment may exist.
		Comment commentObject = null;
		try {
			commentObject = this.queryService.findComment(fromUser.getId(),
					toUserId, itemId);
		} catch (QueryServiceException e) {
			throw new ReputationServiceException(
					"Failure in giving the comment. It cannot be checked whether the user "
							+ nickname
							+ " has already given a comment to the user "
							+ toUser.getNickname() + " regarding the item "
							+ item.getName() + ".", e);
		}

		if (commentObject != null) {
			// there exists already a comment for the triple fromUser, toUser,
			// item
			throw new ReputationServiceException(
					"Failure in giving the comment. The user " + nickname
							+ " has already given a comment to the user "
							+ toUser.getNickname() + " regarding the item "
							+ item.getName() + ". The comment is: "
							+ commentObject.getComment());
		} else {
			// comment can be created and persisted
			try {
				commentObject = this.persistenceService.persistComment(
						fromUser, toUser, item, comment, rating, currentDate);
                if (suppressResults) {
                    return null;
                }
                return commentObject;
			} catch (BusinessObjectsPersistenceServiceException e) {
				throw new ReputationServiceException(
						"Failure in giving the comment from user " + nickname
								+ " to user " + toUser.getNickname()
								+ " regarding the item " + item.getName()
								+ ". The comment cannot be persisted.", e);
			}
		}

	}
}
