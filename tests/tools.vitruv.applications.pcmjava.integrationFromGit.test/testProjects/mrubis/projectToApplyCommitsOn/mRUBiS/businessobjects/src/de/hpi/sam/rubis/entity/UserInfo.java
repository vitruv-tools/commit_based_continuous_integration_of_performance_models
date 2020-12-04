package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Business object for representing information about a user. This information
 * is about a different user than the user who is logged in. This information
 * about a user is presented to a logged-in user.
 * 
 * @author thomas
 * 
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = -7387270621193091515L;
	private Integer userId;
	private String nickname;
	private String email;
	private int rating;
	private Date creationDate;
	private String regionName;
	private Integer regionId;
	private List<UserInfoComment> receivedComments;

	/**
	 * @param userId
	 * @param nickname
	 * @param email
	 * @param rating
	 * @param creationDate
	 * @param regionName
	 * @param regionId
	 * @param receivedComments
	 */
	public UserInfo(Integer userId, String nickname, String email, int rating,
			Date creationDate, String regionName, Integer regionId,
			List<UserInfoComment> receivedComments) {
		super();
		this.userId = userId;
		this.nickname = nickname;
		this.email = email;
		this.rating = rating;
		this.creationDate = creationDate;
		this.regionName = regionName;
		this.regionId = regionId;
		this.receivedComments = receivedComments;
	}

	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @return the regionName
	 */
	public String getRegionName() {
		return regionName;
	}

	/**
	 * @return the regionId
	 */
	public Integer getRegionId() {
		return regionId;
	}

	/**
	 * @return the receivedComments
	 */
	public List<UserInfoComment> getReceivedComments() {
		return receivedComments;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

//	/**
//	 * 
//	 * @return a String representation of this user info.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ "\n");
//		sb.append("  Name: " + this.getNickname() + ", Email: "
//				+ this.getEmail() + " (User ID: " + this.getUserId() + ")\n");
//		sb.append("  Rating: " + this.getRating() + ", Registration Date: "
//				+ this.getCreationDate() + "\n");
//		sb.append("  Region: " + this.getRegionName() + "(Region ID: "
//				+ this.getRegionId() + ")\n");
//
//		sb.append("  Comments: " + this.getReceivedComments().size()
//				+ " comments were given to this user:\n");
//		for (UserInfoComment c : this.getReceivedComments()) {
//			sb.append("    - " + c.infoString());
//		}
//		sb.append("\n");
//		return sb.toString();
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return (this.getClass().getCanonicalName() + this.userId).hashCode();
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
		final UserInfo other = (UserInfo) obj;
		if (this.userId == null) {
			if (other.userId != null)
				return false;
		} else if (!this.userId.equals(other.userId))
			return false;
		return true;
	}

}
