package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Business object for representing a bid.
 * 
 * @author thomas
 * 
 */
public class Bid implements Serializable {

	private static final long serialVersionUID = 9050425337883489987L;
	private Integer id;
	private User user;
	private Item item;
	private int quantity;
	private float bidPrice;
	private float maxBidPrice;
	private Date date;

	/**
	 * @param id
	 *            identifier of the bid
	 * @param user
	 *            the user who gave the bid
	 * @param item
	 *            the item for which the bid has been given
	 * @param quantity
	 *            the quantity of items the bid is about
	 * @param bidPrice
	 *            the price of the actual bid
	 * @param maxBidPrice
	 *            the upper bound of the bid
	 * @param date
	 *            the date the bid has been given
	 */
	public Bid(int id, User user, Item item, int quantity, float bidPrice,
			float maxBidPrice, Date date) {
		this.id = new Integer(id);
		this.user = user;
		this.item = item;
		this.quantity = quantity;
		this.bidPrice = bidPrice;
		this.maxBidPrice = maxBidPrice;
		this.date = date;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	void setUser(User user) {
		this.user = user;
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
	 * @return the quantity
	 */
	public int getQuantity() {
		return this.quantity;
	}

	/**
	 * @return the bid
	 */
	public float getBidPrice() {
		return this.bidPrice;
	}

	/**
	 * @return the maxBid
	 */
	public float getMaxBidPrice() {
		return this.maxBidPrice;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

//	/**
//	 * 
//	 * @return a String representation of this bid.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getId() + "\n");
//		sb.append("  Bidding user: " + this.getUser().getNickname()
//				+ " (User ID: " + this.getUser().getId() + ")\n");
//		sb.append("  Item: " + this.getItem().getName() + ", "
//				+ this.getItem().getDescription() + " (Item ID:"
//				+ this.getItem().getId() + ")\n");
//		sb.append("  Bid Quantity: " + this.getQuantity() + ", Bid Price: "
//				+ this.getBidPrice() + ", Max Bid Price: "
//				+ this.getMaxBidPrice() + ", Bid Date: " + this.getDate()
//				+ "\n");
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
		final Bid other = (Bid) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

}
