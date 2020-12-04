package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Date;

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
 * Entity class for items.
 * 
 * @author thomas
 * 
 */
@Entity
@Table(name = "items")
public class EItem implements Serializable {

    private static final long serialVersionUID = 7455029805910727975L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 2048)
    private String description;

    @Column(name = "initial_price", nullable = false)
    private float initialPrice;

    @Column(name = "initial_quantity", nullable = false)
    private int initialQuantity;

    @Column(name = "reserve_price")
    private float reservePrice = 0;

    @Column(name = "buy_now_price")
    private float buyNowPrice = 0;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    @JoinFetch // PM: added annotation
    private EUser seller;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JoinFetch // PM: added annotation
    private ECategory category;

    /**
     * default constructor.
     */
    public EItem() {

    }

    /**
     * @param id
     * @param name
     * @param description
     * @param initialPrice
     * @param initialQuantity
     * @param reservePrice
     * @param buyNowPrice
     * @param startDate
     * @param endDate
     * @param seller
     * @param category
     * @param bids
     * @param buyNows
     * @param comments
     */
    public EItem(int id, String name, String description, float initialPrice, int initialQuantity, float reservePrice,
            float buyNowPrice, Date startDate, Date endDate, EUser seller, ECategory category) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.initialPrice = initialPrice;
        this.initialQuantity = initialQuantity;
        this.reservePrice = reservePrice;
        this.buyNowPrice = buyNowPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.seller = seller;
        this.category = category;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the initialPrice
     */
    public float getInitialPrice() {
        return initialPrice;
    }

    /**
     * @param initialPrice
     *            the initialPrice to set
     */
    public void setInitialPrice(float initialPrice) {
        this.initialPrice = initialPrice;
    }

    /**
     * @return the initialQuantity
     */
    public int getInitialQuantity() {
        return initialQuantity;
    }

    /**
     * @param initialQuantity
     *            the initialQuantity to set
     */
    public void setInitialQuantity(int initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    /**
     * @return the reservePrice
     */
    public float getReservePrice() {
        return reservePrice;
    }

    /**
     * @param reservePrice
     *            the reservePrice to set
     */
    public void setReservePrice(float reservePrice) {
        this.reservePrice = reservePrice;
    }

    /**
     * @return the buyNowPrice
     */
    public float getBuyNowPrice() {
        return buyNowPrice;
    }

    /**
     * @param buyNowPrice
     *            the buyNowPrice to set
     */
    public void setBuyNowPrice(float buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the seller
     */
    public EUser getSeller() {
        return seller;
    }

    /**
     * @param seller
     *            the seller to set
     */
    public void setSeller(EUser seller) {
        this.seller = seller;
    }

    /**
     * @return the category
     */
    public ECategory getCategory() {
        return category;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory(ECategory category) {
        this.category = category;
    }
    
    /**
     * Converts this entity instance of an item to a business object representing the same item.
     * 
     * @param dtoHelper
     *            conversion helper
     * @return the item business object of this entity instance.
     */
    public Item convertToDTO() {
            Item item = new Item(this.getId(), this.getName(), this.getDescription(), this.getInitialPrice(),
                    this.getInitialQuantity(), this.getReservePrice(), this.getBuyNowPrice(), this.getStartDate(),
                    this.getEndDate(), null, null);

            Category category = this.getCategory().convertToDTO();
            item.setCategory(category);

            // seller of the item
            // TODO PM: why converting to DTO when only id is needed and the DTO is thrown away?
            User seller = this.getSeller().convertToDTO();

            item.setSellerId(seller.getId());
            
        return item;

    }

}
