package de.hpi.sam.rubis.entity;

import java.io.Serializable;

/**
 * Business object for representing a category.
 * 
 * @author thomas
 * 
 */
public class Category implements Serializable {

	private static final long serialVersionUID = 3611251625120848488L;
	private Integer id;
	private String name;
//	private List<Item> items;

	/**
	 * @param id
	 *            identifier of the category
	 * @param name
	 *            name of the category
	 * @param items
	 *            items of the category
	 */
	public Category(int id, String name /*, List<Item> items */) {
		this.id = new Integer(id);
		this.name = name;
//		this.items = items;
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

// PM: unused
//	/**
//	 * @return the items
//	 */
//	public List<Item> getItems() {
//		if (this.items == null) {
//			return Collections.unmodifiableList(new LinkedList<Item>());
//		} else {
//			return Collections.unmodifiableList(this.items);
//		}
//
//	}

//	/**
//	 * 
//	 * @return a String representation of this category.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getId() + "\n");
//		sb.append("  Name: " + this.getName() + ", Number of items: "
//				+ this.getItems().size() + "\n");
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
		final Category other = (Category) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

}
