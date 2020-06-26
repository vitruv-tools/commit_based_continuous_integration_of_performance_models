package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entity class for regions.
 * 
 * @author thomas
 * 
 */
@Entity
@NamedQueries( {
		@NamedQuery(name = "findRegionByName", query = "SELECT DISTINCT r FROM ERegion r WHERE r.name = :name"),
		@NamedQuery(name = "findAllRegions", query = "SELECT r FROM ERegion r") })
@Table(name = "regions")
//@Cacheable
public class ERegion implements Serializable {

	private static final long serialVersionUID = -7978192906981600256L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@Column(name = "name", length = 25)
	private String name;

	/**
	 * default constructor.
	 */
	public ERegion() {

	}

	/**
	 * @param id
	 * @param name
	 * @param users
	 */
	public ERegion(int id, String name) {
		super();
		this.id = id;
		this.name = name;
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
	 * Converts this entity instance of a region to a business object
	 * representing the same region.
	 * 
	 * @param dtoHelper
	 *            conversion helper
	 * @param includeUsers
	 *            PM: true, if the constructed DTO shall contain all users in
	 *            this region (expensive)
	 * @return the region business object of this entity instance.
	 */
	public Region convertToDTO() {
			List<User> users = new LinkedList<User>();
			Region region = new Region(this.getId(), this.getName(), users);

// PM: practically unused (apart from infoString() methods), but high performance impact
//			for (EUser eUser : this.getUsers()) {
//				User user = eUser.convertToDTO(dtoHelper);
//				users.add(user);
//				user.setRegion(region);
//			}

		return region;
	}

}
