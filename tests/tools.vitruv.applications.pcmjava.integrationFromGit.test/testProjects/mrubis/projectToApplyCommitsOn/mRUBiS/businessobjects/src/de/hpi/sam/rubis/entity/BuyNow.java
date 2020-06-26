package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Business object for representing a buy-now.
 * 
 * @author thomas
 * 
 */
public class BuyNow implements Serializable {

	private static final long serialVersionUID = 8933630328709404298L;
	private Integer id;
	private User buyer;
	private Item item;
	private int quantity;
	private Date date;

	/**
	 * @param id
	 *            identifier of the buy-now
	 * @param buyer
	 *            the user that bought the item
	 * @param item
	 *            the item the user has bought
	 * @param quantity
	 *            the number of items the user has bought
	 * @param date
	 *            the day the user has bought the item
	 */
	public BuyNow(int id, User buyer, Item item, int quantity, Date date) {
		this.id = new Integer(id);
		this.buyer = buyer;
		this.item = item;
		this.quantity = quantity;
		this.date = date;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the buyer
	 */
	public User getBuyer() {
		return this.buyer;
	}

	/**
	 * @param buyer
	 *            the buyer to set
	 */
	void setBuyer(User buyer) {
		this.buyer = buyer;
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
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

//	/**
//	 * 
//	 * @return a String representation of this buy-now.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getId() + "\n	");
//		sb.append("  Buying user: " + this.getBuyer().getNickname()
//				+ " (User ID: " + this.getBuyer().getId() + ")\n");
//		sb.append("  Item: " + this.getItem().getName() + ", "
//				+ this.getItem().getDescription() + " (Item ID:"
//				+ this.getItem().getId() + ")\n");
//		sb.append("  Quantity: " + this.getQuantity() + ", Buy Now Price: "
//				+ this.getItem().getBuyNowPrice() + ", Date: " + this.getDate()
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
		final BuyNow other = (BuyNow) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

}
