package de.hpi.sam.rubis.usermgmt.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.authservice.AuthenticationService;
import de.hpi.sam.rubis.authservice.AuthenticationServiceException;
import de.hpi.sam.rubis.entity.Bid;
import de.hpi.sam.rubis.entity.User;
import de.hpi.sam.rubis.entity.UserProfile;
import de.hpi.sam.rubis.queryservice.QueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;
import de.hpi.sam.rubis.usermgmt.AboutMeService;
import de.hpi.sam.rubis.usermgmt.AboutMeServiceException;

/**
 * Implementation of the {@link AboutMeService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = AboutMeService.NAME)
public class AboutMeServiceBean implements AboutMeService {

	@EJB
	private AuthenticationService authenticationService;

	@EJB
	private QueryService queryService;

    private boolean suppressResults = Boolean.getBoolean("suppressResults");
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserProfile getAboutMe(String username, String password)
			throws AboutMeServiceException {

		if (username == null || password == null || username.equals("")
				|| password.equals("")) {
			throw new AboutMeServiceException(
					"Username or password must be indicated.");
		}

		User user = null;
		try {
			user = this.authenticationService.authenticate(username, password);
		} catch (AuthenticationServiceException e) {
			throw new AboutMeServiceException(
					"Failure in authenticating the user: " + e.getMessage(), e);
		}

		try {
			List<Bid> pastMaxBids = this.queryService
					.findUserPastWinningBids(user.getId());
			List<Bid> currentMaxBids = this.queryService
					.findUserCurrentMaxBids(user.getId());
			UserProfile userProfile = new UserProfile(user, pastMaxBids,
					currentMaxBids);

            if (suppressResults) {
                return null;
            }
            return userProfile;
		} catch (QueryServiceException e) {
			throw new AboutMeServiceException(
					"Failure in retrieving your profile information: "
							+ e.getMessage(), e);
		}

	}

	/*
	
	@Deprecated
	@SuppressWarnings("unused")
	private String listComments(User user) {
		StringBuffer result = new StringBuffer("<br/>");
		List<Comment> commentList = user.getReceivedComments();

		if (commentList.isEmpty()) {
			result
					.append(this
							.printHTMLHighlighted("<h3>There is no comment yet for this user.</h3>"));
		} else {
			result
					.append(this
							.printHTMLHighlighted("<h3>Comments for this user</h3><br />"));
			result.append(this.printCommentHeader());
			for (Comment comment : commentList) {
				User commentingUser = comment.getFromUser();
				result
						.append("<DT><b><BIG><a href=\""
								+ RubisNameSchema.WEB_CONTAINER_CONTEXT
								+ "/servlet/edu.rice.rubis.beans.servlets.ViewUserInfo?userId="
								+ commentingUser.getId() + "\">"
								+ commentingUser.getNickname()
								+ "</a></BIG></b>" + " wrote the "
								+ comment.getDate() + "<DD><i>"
								+ comment.getComment() + "</i><p>\n");
			}
			result.append(this.printCommentFooter());
		}

		return result.toString();
	}

	@Deprecated
	private String printCommentHeader() {
		return "<DL>\n";
	}

	@Deprecated
	private String printCommentFooter() {
		return "</DL>\n";
	}

	@Deprecated
	@SuppressWarnings("unused")
	private String listBids(User user, String username, String password) {
		StringBuffer result = new StringBuffer();
		List<Bid> bidList = user.getBids();// this.queryService.getUserBids(user.getId());

		if (bidList.size() == 0) {
			result.append(this
					.printHTMLHighlighted("<h3>You didn't put any bid.</h3>"));
		} else {
			result.append(this.printUserBidsHeader());
			for (Bid bid : bidList) {
				Item item = bid.getItem();
				int itemId = item.getId();
				User seller = item.getSeller();

				try {
					result
							.append("<TR><TD><a href=\""
									+ RubisNameSchema.WEB_CONTAINER_CONTEXT
									+ "/servlet/edu.rice.rubis.beans.servlets.ViewItem?itemId="
									+ itemId
									+ "\">"
									+ item.getName()
									+ "<TD>"
									+ item.getInitialPrice()
									+ "<TD>"
									+ this.queryService.findItemMaxBid(itemId)
									+ "<TD>"
									+ bid.getMaxBidPrice()
									+ "<TD>"
									+ item.getQuantity()
									+ "<TD>"
									+ item.getStartDate()
									+ "<TD>"
									+ item.getEndDate()
									+ "<TD><a href=\""
									+ RubisNameSchema.WEB_CONTAINER_CONTEXT
									+ "/servlet/edu.rice.rubis.beans.servlets.ViewUserInfo?userId="
									+ seller.getId()
									+ "\">"
									+ seller.getNickname()
									+ "<TD><a href=\""
									+ RubisNameSchema.WEB_CONTAINER_CONTEXT
									+ "/servlet/edu.rice.rubis.beans.servlets.PutBid?itemId="
									+ itemId
									+ "&nickname="
									+ URLEncoder.encode(username)
									+ "&password="
									+ URLEncoder.encode(password)
									+ "\"><IMG SRC=\""
									+ RubisNameSchema.WEB_CONTAINER_CONTEXT
									+ "/ejb_rubis_web/bid_now.jpg\" height=22 width=90></a>\n");
				} catch (QueryServiceException e) {
					e.printStackTrace();
				}
			}
			result.append(this.printItemFooter());
		}

		return result.toString();
	}

	@Deprecated
	private String printUserBidsHeader() {
		return "<br/>"
				+ this
						.printHTMLHighlighted("<p><h3>Items you have bid on.</h3>\n")
				+ "<TABLE border=\"1\" summary=\"Items You've bid on\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Your max bid<TH>Quantity"
				+ "<TH>Start Date<TH>End Date<TH>Seller<TH>Put a new bid\n"
				+ "<TBODY>\n";
	}

	@Deprecated
	@SuppressWarnings("unused")
	private String listWonItems(User user) {
		StringBuffer result = new StringBuffer();
		List<Item> wonList = new LinkedList<Item>(); 
		// this.queryService.getUserWonItems(user.getId());

		if (wonList.size() == 0) {
			result
					.append("<br/><h3>You didn't win any item in the last 30 days.</h3><br/>");
		} else {
			result.append(this.printUserWonItemHeader());
			for (Item item : wonList) {
				int itemId = item.getId();
				User seller = item.getSeller();
				try {
					result
							.append("<TR><TD><a href=\""
									+ RubisNameSchema.WEB_CONTAINER_CONTEXT
									+ "/servlet/edu.rice.rubis.beans.servlets.ViewItem?itemId="
									+ itemId
									+ "\">"
									+ item.getName()
									+ "</a>\n"
									+ "<TD>"
									+ this.queryService.findItemMaxBid(itemId)
									+ "\n"
									+ "<TD><a href=\""
									+ RubisNameSchema.WEB_CONTAINER_CONTEXT
									+ "/servlet/edu.rice.rubis.beans.servlets.ViewUserInfo?userId="
									+ seller.getId() + "\">"
									+ seller.getNickname() + "</a>\n");
				} catch (QueryServiceException e) {
					e.printStackTrace();
				}
			}
			result.append(this.printItemFooter());
		}

		return result.toString();
	}

	@Deprecated
	private String printUserWonItemHeader() {
		return "<br/>"
				+ this
						.printHTMLHighlighted("<p><h3>Items you won in the past 30 days.</h3>\n")
				+ "<TABLE border=\"1\" summary=\"List of items\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Price you bought it<TH>Seller"
				+ "<TBODY>\n";
	}

	@Deprecated
	@SuppressWarnings("unused")
	private String listBoughtItems(User user) {
		StringBuffer result = new StringBuffer();
		List<BuyNow> buyList;
		// try {
		buyList = user.getBuyNows();// this.queryService.getUserBuyNow(user.getId());

		if (buyList.size() == 0) {
			result
					.append("<br/><h3>You did not buy any item in the last 30 days.</h3><br/>");
		} else {
			result.append(this.printUserBoughtItemHeader());
			for (BuyNow buyNow : buyList) {
				Item item = buyNow.getItem();
				result
						.append("<TR><TD><a href=\""
								+ RubisNameSchema.WEB_CONTAINER_CONTEXT
								+ "/servlet/edu.rice.rubis.beans.servlets.ViewItem?itemId="
								+ item.getId()
								+ "\">"
								+ item.getName()
								+ "</a>\n"
								+ "<TD>"
								+ buyNow.getQuantity()
								+ "\n"
								+ "<TD>"
								+ item.getBuyNowPrice()
								+ "\n"
								+ "<TD><a href=\""
								+ RubisNameSchema.WEB_CONTAINER_CONTEXT
								+ "/servlet/edu.rice.rubis.beans.servlets.ViewUserInfo?userId="
								+ item.getSeller().getId() + "\">"
								+ item.getSeller().getNickname() + "</a>\n");
			}
			result.append(printItemFooter());
		}
		return result.toString();
		// } catch (QueryServiceException e) {
		// e.printStackTrace();
		// return null;
		// }
	}

	@Deprecated
	private String printUserBoughtItemHeader() {
		return "<br/>"
				+ this
						.printHTMLHighlighted("<p><h3>Items you bouhgt in the past 30 days.</h3>\n")
				+ "<TABLE border=\"1\" summary=\"List of items\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Quantity<TH>Price you bought it<TH>Seller"
				+ "<TBODY>\n";
	}

	@Deprecated
	@SuppressWarnings("unused")
	private String listItems(User user) {
		StringBuffer result = new StringBuffer();
		try {
			int userId = user.getId();
			List<Item> currentSellings = this.queryService
					.findUserCurrentSellings(userId);

			List<Item> pastSellings = this.queryService.findUserPastSellings(
					userId, 365);

			if (currentSellings.size() == 0) {
				result
						.append("<br/><h3>You are currently selling no items.</h3>");
			} else {
				result.append(this
						.printSellHeader("Items you are currently selling."));
				for (Item item : currentSellings) {
					result
							.append("<TR><TD><a href=\""
									+ RubisNameSchema.WEB_CONTAINER_CONTEXT
									+ "/servlet/edu.rice.rubis.beans.servlets.ViewItem?itemId="
									+ item.getId()
									+ "\">"
									+ item.getName()
									+ "<TD>"
									+ item.getInitialPrice()
									+ "<TD>"
									+ this.queryService.findItemMaxBid(item
											.getId()) + "<TD>"
									+ item.getQuantity() + "<TD>"
									+ item.getReservePrice() + "<TD>"
									+ item.getBuyNowPrice() + "<TD>"
									+ item.getStartDate() + "<TD>"
									+ item.getEndDate() + "\n");

				}
				result.append(printItemFooter());
			}

			if (pastSellings.size() == 0) {
				result.append("<br/><h3>You didn't sell any item.</h3>");
			} else {
				result.append("<br/>");
				result.append(this
						.printSellHeader("Items you sold in the past."));
				for (Item item : pastSellings) {
					result
							.append("<TR><TD><a href=\""
									+ RubisNameSchema.WEB_CONTAINER_CONTEXT
									+ "/servlet/edu.rice.rubis.beans.servlets.ViewItem?itemId="
									+ item.getId()
									+ "\">"
									+ item.getName()
									+ "<TD>"
									+ item.getInitialPrice()
									+ "<TD>"
									+ this.queryService.findItemMaxBid(item
											.getId()) + "<TD>"
									+ item.getQuantity() + "<TD>"
									+ item.getReservePrice() + "<TD>"
									+ item.getBuyNowPrice() + "<TD>"
									+ item.getStartDate() + "<TD>"
									+ item.getEndDate() + "\n");

				}
				result.append(printItemFooter());
			}
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Deprecated
	private String printHTMLHighlighted(String msg) {
		return "<TABLE width=\"100%\" bgcolor=\"#CCCCFF\">\n<TR><TD align=\"center\" width=\"100%\"><FONT size=\"4\" color=\"#000000\"><B>"
				+ msg + "</B></FONT></TD></TR>\n</TABLE><p>\n";
	}

	@Deprecated
	private String printSellHeader(String title) {
		return this.printHTMLHighlighted("<p><h3>" + title + "</h3>\n")
				+ "<TABLE border=\"1\" summary=\"List of items\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Quantity<TH>ReservePrice<TH>Buy Now"
				+ "<TH>Start Date<TH>End Date\n" + "<TBODY>\n";
	}

	@Deprecated
	private String printItemFooter() {
		return "</TABLE>\n";
	}

	*/
}
