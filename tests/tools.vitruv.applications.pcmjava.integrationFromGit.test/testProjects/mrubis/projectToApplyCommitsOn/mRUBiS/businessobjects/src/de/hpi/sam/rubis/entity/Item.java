package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Business object for representing an item.
 * 
 * @author thomas
 * 
 */
public class Item implements Serializable {

	private static final long serialVersionUID = 2459866242487110233L;
	private Integer id;
	private String name;
	private String description;
	private float initialPrice;
	private int initialQuantity;
	private float reservePrice = 0;
	private float buyNowPrice = 0;
	private Date startDate;
	private Date endDate;
	private Integer sellerId;
	private Category category;
//	private List<Bid> bids;
//	private List<Comment> comments;

	/**
	 * @param id
	 *            identifier of the item
	 * @param name
	 *            name of the item
	 * @param description
	 *            description of the item
	 * @param initialPrice
	 *            the initial price of the item
	 * @param initialQuantity
	 *            the initial quantity of the item
	 * @param reservePrice
	 *            the price for which an instance of the item is reserved
	 * @param buyNow
	 *            the price for which the item can be immediately bought
	 * @param startDate
	 *            the date the bidding starts
	 * @param endDate
	 *            the date the bidding ends
	 * @param sellerId
	 *            the id of the user selling this item
	 * @param category
	 *            the category of the item
	 * @param bids
	 *            all the bids users have given to the item
	 * @param buyNows
	 *            all the buy-nows users have given to the item
	 * @param comments
	 *            The comments buyers have given for this item and for the
	 *            seller
	 */
	public Item(int id, String name, String description, float initialPrice,
			int initialQuantity, float reservePrice, float buyNowPrice,
			Date startDate, Date endDate, Integer sellerId, Category category) {
		super();
		this.id = new Integer(id);
		this.name = name;
		this.description = description;
		this.initialPrice = initialPrice;
		this.initialQuantity = initialQuantity;
		this.reservePrice = reservePrice;
		this.buyNowPrice = buyNowPrice;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sellerId = sellerId;
		this.category = category;
//		this.bids = bids;
//		this.buyNows = buyNows;
//		this.comments = comments;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the initialPrice
	 */
	public float getInitialPrice() {
		return this.initialPrice;
	}

	/**
	 * @return the initialQuantity
	 */
	public int getInitialQuantity() {
		return this.initialQuantity;
	}

	/**
	 * @return the reservePrice
	 */
	public float getReservePrice() {
		return this.reservePrice;
	}

	/**
	 * @return the buyNowPrice
	 */
	public float getBuyNowPrice() {
		return this.buyNowPrice;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * @return the sellerId
	 */
	public Integer getSellerId() {
		return this.sellerId;
	}

	/**
	 * @param sellerId
	 *            the sellerId to set
	 */
	void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return this.category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	void setCategory(Category category) {
		this.category = category;
	}

// PM: unused
//	/**
//	 * @return the bids
//	 */
//	public List<Bid> getBids() {
//		if (this.bids == null) {
//			return Collections.unmodifiableList(new LinkedList<Bid>());
//		} else {
//			return Collections.unmodifiableList(this.bids);
//		}
//	}

// PM: unused
//	/**
//	 * @return the comments
//	 */
//	public List<Comment> getComments() {
//		if (this.comments == null) {
//			return Collections.unmodifiableList(new LinkedList<Comment>());
//		} else {
//			return Collections.unmodifiableList(this.comments);
//		}
//	}



//	/**
//	 * 
//	 * @return a String representation of this item.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getId() + "\n");
//		sb.append("  Name: " + this.getName() + ", " + this.getDescription()
//				+ "\n");
//		sb.append("  Initial quantity: " + this.getInitialQuantity()
//				+ ", Initial Price: " + this.getInitialPrice()
//				+ ", Reserve Price: " + this.getReservePrice()
//				+ ", Buy-Now Price: " + this.getBuyNowPrice() + "\n");
//		sb.append("  Start Date: " + this.getStartDate() + ", End Date: "
//				+ this.getEndDate() + "\n");
//		sb.append("  Selling User: Id = " + this.getSellerId() + ")\n");
//		sb.append("  Category: " + this.getCategory().getName()
//				+ " (Category ID: " + this.getCategory().getId() + ")\n");
//
//		sb.append("  Bids: " + this.getBids().size() + " bids on this item:\n");
//		for (Bid b : this.getBids()) {
//			sb.append("    - " + b.infoString());
//		}
//
//		sb.append("  Buy-Nows: " + this.getBuyNows().size()
//				+ " buy-nows for this item:\n");
//		for (BuyNow b : this.getBuyNows()) {
//			sb.append("    - " + b.infoString());
//		}
//
//		sb.append("  Comments: " + this.getComments().size()
//				+ " comments for this item:\n");
//		for (Comment c : this.getComments()) {
//			sb.append("    - " + c.infoString());
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
		final Item other = (Item) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

}
