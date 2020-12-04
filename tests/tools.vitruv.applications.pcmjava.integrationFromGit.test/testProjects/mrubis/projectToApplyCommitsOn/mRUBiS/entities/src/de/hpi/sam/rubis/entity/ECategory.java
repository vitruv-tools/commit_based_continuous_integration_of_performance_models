package de.hpi.sam.rubis.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entity class for categories.
 * 
 * @author thomas
 * 
 */
@Entity
@NamedQueries( {
		@NamedQuery(name = "findCategoryByName", query = "SELECT DISTINCT c FROM ECategory c WHERE c.name = :name"),
		@NamedQuery(name = "findAllCategories", query = "SELECT c FROM ECategory c")})
@Table(name = "categories")
//@Cacheable
public class ECategory implements Serializable {

	private static final long serialVersionUID = -106289370321078166L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@Column(name = "name", length = 50)
	private String name;

	/**
	 * default constructor.
	 */
	public ECategory() {

	}

	/**
	 * @param id
	 * @param name
	 * @param items
	 */
	public ECategory(int id, String name) {
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
     * Converts this entity instance of a category to a business object representing the same
     * category.
     * 
     * @param dtoHelper
     *            conversion helper
     * @return the category business object of this entity instance.
     */
    public Category convertToDTO() {
        Category category = new Category(this.getId(), this.getName());
        return category;
    }

}
