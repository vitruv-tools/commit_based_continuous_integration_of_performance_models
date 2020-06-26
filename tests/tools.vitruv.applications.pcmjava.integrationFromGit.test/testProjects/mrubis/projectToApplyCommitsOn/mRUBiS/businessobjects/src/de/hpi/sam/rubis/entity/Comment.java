package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Business object for representing a comment from a buyer to a seller of an
 * item. Currently, only the sellers of items get comments, and not the buyers.
 * For the triple of buyer, seller and item at most one comment exists, i.e., if
 * the buyer has two or more buy-nows and/or successful bids of the same item,
 * the buyer can give at most one comment to the seller!
 * 
 * @author thomas
 * 
 */
public class Comment implements Serializable {

	private static final long serialVersionUID = -7954569294342711811L;
	private Integer id;
	private User fromUser;
	private User toUser;
	private Item item;
	private int rating;
	private Date date;
	private String comment;

	/**
	 * @param id
	 *            identifier of the comment
	 * @param fromUser
	 *            the user who has given the comment
	 * @param toUser
	 *            the user about whom the comment is
	 * @param item
	 *            the item the comments refers to
	 * @param rating
	 *            a rating about the user and item
	 * @param date
	 *            the date the comment has been given
	 * @param comment
	 *            the comments as a text
	 */
	public Comment(int id, User fromUser, User toUser, Item item, int rating,
			Date date, String comment) {
		this.id = new Integer(id);
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.item = item;
		this.rating = rating;
		this.date = date;
		this.comment = comment;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the fromUser
	 */
	public User getFromUser() {
		return this.fromUser;
	}

	/**
	 * @param fromUser
	 *            the fromUser to set
	 */
	void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	/**
	 * @return the toUser
	 */
	public User getToUser() {
		return this.toUser;
	}

	/**
	 * @param toUser
	 *            the toUser to set
	 */
	void setToUser(User toUser) {
		this.toUser = toUser;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return this.item;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	void setItem(Item item) {
		this.item = item;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return this.rating;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return this.comment;
	}

//	/**
//	 * 
//	 * @return a String representation of this comment.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getId() + "\n");
//		sb.append("  From User: " + this.getFromUser().getNickname()
//				+ " (User ID: " + this.getFromUser().getId() + ")\n");
//		sb.append("  To User: " + this.getToUser().getNickname()
//				+ " (User ID: " + this.getToUser().getId() + ")\n");
//		sb.append("  Item: " + this.getItem().getName() + ", "
//				+ this.getItem().getDescription() + " (Item ID:"
//				+ this.getItem().getId() + ")\n");
//		sb.append("  Rating: " + this.getRating() + ", Comment: "
//				+ this.getComment() + ", Date: " + this.getDate() + "\n");
//		sb.append("\n");
//		return sb.toString();
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return (this.getClass().getCanonicalName() + this.id).hashCode();
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
		final Comment other = (Comment) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

}
