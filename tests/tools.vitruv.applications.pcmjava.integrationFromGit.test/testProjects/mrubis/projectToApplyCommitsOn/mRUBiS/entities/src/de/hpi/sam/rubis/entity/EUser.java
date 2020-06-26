package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.JoinFetch;

/**
 * Entity class for users.
 * 
 * @author thomas
 * 
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "findUserByNickName", query = "SELECT DISTINCT u FROM EUser u WHERE u.nickname = :nickname"),
		@NamedQuery(name = "findAllUsers", query = "SELECT u FROM EUser u") })
@Table(name = "users")
public class EUser implements Serializable {

	private static final long serialVersionUID = -5544296251845461684L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@Column(name = "firstname", length = 20)
	private String firstname;

	@Column(name = "lastname", length = 20)
	private String lastname;

	@Column(name = "nickname", length = 20, nullable = false, unique = true)
	private String nickname;

	@Column(name = "password", length = 20, nullable = false)
	private String password;

	@Column(name = "email", length = 50, nullable = false)
	private String email;

	@Column(name = "rating")
	private int rating;

	@Column(name = "balance")
	private float balance;

	@Column(name = "creation_date")
	@Temporal(TemporalType.DATE)
	private Date creationDate;

	@Column(name = "userclass", length = 20)
	private String userClass;

	@ManyToOne
	@JoinColumn(name = "region_id", nullable = false)
    @JoinFetch // PM: added annotation
	private ERegion region;

	/**
	 * default constructor.
	 */
	public EUser() {

	}

	/**
	 * @param id
	 * @param firstname
	 * @param lastname
	 * @param nickname
	 * @param password
	 * @param email
	 * @param rating
	 * @param balance
	 * @param creationDate
	 * @param region
	 * @param offeredItems
	 * @param bids
	 * @param buyNows
	 * @param givenComments
	 * @param receivedComments
	 */
	public EUser(int id, String firstname, String lastname, String nickname,
			String password, String email, int rating, float balance,
			Date creationDate, ERegion region, String userClass) {
		super();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.nickname = nickname;
		this.password = password;
		this.email = email;
		this.rating = rating;
		this.balance = balance;
		this.creationDate = creationDate;
		this.region = region;
		this.userClass = userClass;
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
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname
	 *            the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname
	 *            the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @param rating
	 *            the rating to set
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}

	/**
	 * @return the balance
	 */
	public float getBalance() {
		return balance;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(float balance) {
		this.balance = balance;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the region
	 */
	public ERegion getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(ERegion region) {
		this.region = region;
	}

	/**
	 * @return the userClass
	 */
	public String getUserClass() {
		return userClass;
	}

	/**
	 * @param userClass
	 *            the userClass to set
	 */
	public void setUserClass(String userClass) {
		this.userClass = userClass;
	}
	
	/**
	 * Converts this entity instance of a user to a business object representing
	 * the same user.
	 * 
	 * @param dtoHelper
	 *            conversion helper
	 * @return the user business object of this entity instance.
	 */
    public User convertToDTO() {
        List<Item> items = null;
        List<Comment> givenComments = null;
        List<Comment> receivedComments = null;

        User user = new User(this.getId(), this.getFirstname(), this.getLastname(), this.getNickname(),
                this.getPassword(), this.getEmail(), this.getRating(), this.getBalance(), this.getCreationDate(), null,
                items, givenComments, receivedComments, this.getUserClass());

        // Region
        Region region = this.getRegion().convertToDTO();
        user.setRegion(region);

        return user;
    }

}
