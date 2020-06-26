package de.hpi.sam.rubis.entity;

import java.io.Serializable;

/**
 * Business object for representing an item in the inventory.
 * 
 * @author thomas
 * 
 */
public class InventoryItem implements Serializable {

	private static final long serialVersionUID = 4443579795040702274L;

	private Integer id;
	private Item item;
	private int availableQuantity;

	/**
	 * 
	 * @param id
	 *            identifier of the inventory item
	 * @param item
	 *            the related item
	 * @param availableQuantity
	 *            the available instances of the related item
	 */
	public InventoryItem(Integer id, Item item, int availableQuantity) {
		super();
		this.id = id;
		this.item = item;
		this.availableQuantity = availableQuantity;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return this.item;
	}

	/**
	 * @return the availableQuantity
	 */
	public int getAvailableQuantity() {
		return this.availableQuantity;
	}

//	/**
//	 * 
//	 * @return a String representation of this item.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getId() + "\n");
//		sb.append("Item: " + this.getItem().infoString());
//		sb.append("Available quantity: " + this.getAvailableQuantity());
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
		final InventoryItem other = (InventoryItem) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

}
