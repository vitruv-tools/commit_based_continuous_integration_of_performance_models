package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.JoinFetch;

/**
 * Entity class for bids.
 * 
 * @author thomas
 * 
 */
@Entity
@Table(name = "bids")
public class EBid implements Serializable {

	private static final long serialVersionUID = 3070733295882359096L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
    @JoinFetch // PM: added annotation
	private EUser user;

	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
    @JoinFetch // PM: added annotation
	private EItem item;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "bid_price", nullable = false)
	private float bidPrice;

	@Column(name = "max_bid_price", nullable = false)
	private float maxBidPrice;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "bid_date")
	private Date date;

	/**
	 * default constructor.
	 */
	public EBid() {

	}

	/**
	 * @param id
	 * @param user
	 * @param item
	 * @param quantity
	 * @param bidPrice
	 * @param maxBidPrice
	 * @param date
	 */
	public EBid(int id, EUser user, EItem item, int quantity, float bidPrice,
			float maxBidPrice, Date date) {
		super();
		this.id = id;
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
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the user
	 */
	public EUser getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(EUser user) {
		this.user = user;
	}

	/**
	 * @return the item
	 */
	public EItem getItem() {
		return item;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(EItem item) {
		this.item = item;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the bidPrice
	 */
	public float getBidPrice() {
		return bidPrice;
	}

	/**
	 * @param bidPrice
	 *            the bidPrice to set
	 */
	public void setBidPrice(float bidPrice) {
		this.bidPrice = bidPrice;
	}

	/**
	 * @return the maxBidPrice
	 */
	public float getMaxBidPrice() {
		return maxBidPrice;
	}

	/**
	 * @param maxBidPrice
	 *            the maxBidPrice to set
	 */
	public void setMaxBidPrice(float maxBidPrice) {
		this.maxBidPrice = maxBidPrice;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Converts this entity instance of a bid to a business object representing
	 * the same bid.
	 * 
	 * @param dtoHelper
	 *            conversion helper
	 * @return the bid business object of this entity instance.
	 */
	public Bid convertToDTO() {
        Bid bid = new Bid(this.getId(), null, null, this.getQuantity(), this.getBidPrice(), this.getMaxBidPrice(),
                this.getDate());

        User biddingUser = this.getUser().convertToDTO();
        bid.setUser(biddingUser);

        Item item = this.getItem().convertToDTO();
        bid.setItem(item);

        return bid;
	}
	
    public static BidHistory convertToDTO(List<EBid> entities) {
        List<Bid> history = new ArrayList<Bid>();
        for (EBid eBid : entities) {
            history.add(eBid.convertToDTO());
        }
        return new BidHistory(history);
    }

}
