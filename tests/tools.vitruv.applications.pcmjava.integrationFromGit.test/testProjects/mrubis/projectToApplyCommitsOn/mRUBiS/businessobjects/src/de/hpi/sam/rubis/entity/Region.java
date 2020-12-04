package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Business object for representing a region.
 * 
 * @author thomas
 * 
 */
public class Region implements Serializable {

	private static final long serialVersionUID = 3529739035223229617L;
	private Integer id;
	private String name;
	private List<User> users;

	/**
	 * Creates a region.
	 * 
	 * @param id
	 *            identifier of the region
	 * @param name
	 *            name of the region
	 * @param users
	 *            list of user in the region
	 */
	public Region(int id, String name, List<User> users) {
		this.id = new Integer(id);
		this.name = name;
		this.users = users;
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
	 * @return the users
	 */
	public List<User> getUsers() {
		if (this.users == null) {
			return Collections.unmodifiableList(new LinkedList<User>());
		} else {
			return Collections.unmodifiableList(this.users);
		}
	}

//	/**
//	 * 
//	 * @return a String representation of this region.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getId() + "\n");
//		sb.append("  Name: " + this.getName() + ", Number of users: "
//				+ this.getUsers().size() + "\n");
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
		final Region other = (Region) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

}
