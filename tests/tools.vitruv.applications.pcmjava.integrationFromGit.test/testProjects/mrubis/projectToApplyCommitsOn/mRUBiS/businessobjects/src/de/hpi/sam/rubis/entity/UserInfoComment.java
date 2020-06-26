package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Business object for representing information about a comment. This object is
 * used by objects of type {@link UserInfo}.
 * 
 * @author thomas
 * 
 */
public class UserInfoComment implements Serializable {

	private static final long serialVersionUID = 523358451382682708L;
	private Integer commentId;
	private String commentTxt;
	private int rating;
	private Integer givingUserId;
	private String givingUserName;
	private Date commentDate;
	private Integer itemId;
	private String itemName;
	private String itemDescription;

	/**
	 * @param commentId
	 * @param commentTxt
	 * @param rating
	 * @param givingUserId
	 * @param givingUserName
	 * @param commentDate
	 * @param itemId
	 * @param itemName
	 * @param itemDescription
	 */
	public UserInfoComment(Integer commentId, String commentTxt, int rating,
			Integer givingUserId, String givingUserName, Date commentDate,
			Integer itemId, String itemName, String itemDescription) {
		super();
		this.commentId = commentId;
		this.commentTxt = commentTxt;
		this.rating = rating;
		this.givingUserId = givingUserId;
		this.givingUserName = givingUserName;
		this.commentDate = commentDate;
		this.itemId = itemId;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
	}

	/**
	 * @return the commentTxt
	 */
	public String getCommentTxt() {
		return commentTxt;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @return the givingUserId
	 */
	public Integer getGivingUserId() {
		return givingUserId;
	}

	/**
	 * @return the givingUserName
	 */
	public String getGivingUserName() {
		return givingUserName;
	}

	/**
	 * @return the commentDate
	 */
	public Date getCommentDate() {
		return commentDate;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}

	/**
	 * @return the commentId
	 */
	public Integer getCommentId() {
		return commentId;
	}

	/**
	 * @return the itemId
	 */
	public Integer getItemId() {
		return itemId;
	}

//	/**
//	 * 
//	 * @return a String representation of this user info comment.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ "\n");
//		sb.append("  Comment: " + this.getCommentTxt() + "(Comment ID: "
//				+ this.getCommentId() + ")\n");
//		sb.append("  Rating: " + this.getRating() + ", Comment Date: "
//				+ this.getCommentDate() + "\n");
//		sb.append("  From User: " + this.getGivingUserName() + "(User ID: "
//				+ this.getGivingUserId() + ")\n");
//		sb.append("  Item: " + this.getItemName() + ", "
//				+ this.getItemDescription() + "(Item ID: " + this.getItemId()
//				+ ")\n");
//		sb.append("\n");
//		return sb.toString();
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return (this.getClass().getCanonicalName() + this.commentId).hashCode();
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
		final UserInfoComment other = (UserInfoComment) obj;
		if (this.commentId == null) {
			if (other.commentId != null)
				return false;
		} else if (!this.commentId.equals(other.commentId))
			return false;
		return true;
	}

}
