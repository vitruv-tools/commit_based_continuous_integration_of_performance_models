package de.hpi.sam.rubis.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.JoinFetch;

// TODO change sql scripts for EItem and EInventory

/**
 * Entity class for the inventory of items by maintaining the number of
 * available instances of the items.
 * 
 * @author thomas
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "findInventoryItemByItem", query = "SELECT i FROM EInventoryItem i WHERE i.item = :item") })
@Table(name = "inventory_items")
public class EInventoryItem implements Serializable {

	private static final long serialVersionUID = -6743251026834624027L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@Version
	@Column(name = "version", nullable = false)
	private int version;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "item_id", nullable = false)
    @JoinFetch // PM: added annotation
	private EItem item;

	@Column(name = "available_quantity", nullable = false)
	private int availableQuantity;

	/**
	 * 
	 * @param item
	 * @param availableQuantity
	 */
	public EInventoryItem(EItem item, int availableQuantity) {
		super();
		this.item = item;
		this.availableQuantity = availableQuantity;
	}

	/**
	 * 
	 */
	public EInventoryItem() {

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
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
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
	 * @return the availableQuantity
	 */
	public int getAvailableQuantity() {
		return availableQuantity;
	}

	/**
	 * @param availableQuantity
	 *            the availableQuantity to set
	 */
	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	/**
	 * Converts this entity instance of an inventory item to a business object
	 * representing the same item.
	 * 
	 * @param dtoHelper
	 *            conversion helper
	 * @return the item business object of this entity instance.
	 */
	public InventoryItem convertToDTO() {
        Item item = this.getItem().convertToDTO();
        InventoryItem inventoryItem = new InventoryItem(this.getId(), item, this.getAvailableQuantity());

        return inventoryItem;
	}

}
