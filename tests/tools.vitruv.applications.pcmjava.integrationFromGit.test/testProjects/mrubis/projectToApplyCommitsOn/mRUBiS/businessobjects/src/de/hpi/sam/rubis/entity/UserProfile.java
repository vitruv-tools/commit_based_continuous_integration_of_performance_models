package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Business object for representing a user profile for the logged-in user.
 * 
 * @author thomas
 * 
 */
public class UserProfile implements Serializable {

	private static final long serialVersionUID = 1198723423007786625L;
	private User user;
	// bids the user has done successfully, i.e., the user has won the item with
	// the bid.
	private List<Bid> pastMaxBids;
	// Current bids of the user, which are the highest bids on items-
	private List<Bid> currentMaxBids;

	public UserProfile(User user, List<Bid> pastMaxBids,
			List<Bid> currentMaxBids) {
		this.user = user;
		this.pastMaxBids = pastMaxBids;
		this.currentMaxBids = currentMaxBids;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @return the pastMaxBids
	 */
	public List<Bid> getPastMaxBids() {
		return Collections.unmodifiableList(this.pastMaxBids);
	}

	/**
	 * @return the currentMaxBids
	 */
	public List<Bid> getCurrentMaxBids() {
		return Collections.unmodifiableList(this.currentMaxBids);
	}

//	/**
//	 * 
//	 * @return a String representation of this user.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getUser().getId() + "\n");
//		sb.append("  User: " + this.getUser().infoString());
//
//		sb.append("  Past Max Bids: " + this.getPastMaxBids().size()
//				+ " max bids were given by this user in the past:\n");
//		for (Bid b : this.getPastMaxBids()) {
//			sb.append("    - " + b.infoString());
//		}
//
//		sb.append("  Current Max Bids: " + this.getCurrentMaxBids().size()
//				+ " max bids are currently given by this user:\n");
//		for (Bid b : this.getCurrentMaxBids()) {
//			sb.append("    - " + b.infoString());
//		}
//
//		sb.append("\n");
//		return sb.toString();
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return (this.getClass().getCanonicalName() + this.user.getId())
				.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final UserProfile other = (UserProfile) obj;
		if (this.user.getId() == null) {
			if (other.user.getId() != null)
				return false;
		} else if (!this.user.getId().equals(other.user.getId()))
			return false;
		return true;
	}

}
