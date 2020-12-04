package de.hpi.sam.rubis.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Business object for representing a user.
 * 
 * @author thomas
 * 
 */
public class User implements Serializable {

	private static final long serialVersionUID = -4542342651086556026L;
	private Integer id;
	private String firstname;
	private String lastname;
	private String nickname;
	private String password;
	private String email;
	private int rating;
	private float balance;
	private Date creationDate;
	private Region region;
	private List<Item> offeredItems;
	private List<Comment> givenComments;
	private List<Comment> receivedComments;
	private CustomerClass customerClass;

	/**
	 * @param id
	 *            identifier of the user
	 * @param firstname
	 *            first name of the user
	 * @param lastname
	 *            last name of the user
	 * @param nickname
	 *            nickname of the user
	 * @param password
	 *            password of the user
	 * @param email
	 *            e-mail of the user
	 * @param rating
	 *            rating of the user
	 * @param balance
	 *            balance of the user
	 * @param creationDate
	 *            date the user account has been created
	 * @param region
	 *            the region the user lives
	 * @param offeredItems
	 *            the items the user offers and offered
	 * @param bids
	 *            the bids the user has given
	 * @param buyNows
	 *            the buy-nows the user has done
	 * @param givenComments
	 *            the comments the user has given
	 * @param receivedComments
	 *            the comments the user has received
	 * @param customerClass
	 *            the class of the user
	 */
	public User(int id, String firstname, String lastname, String nickname,
			String password, String email, int rating, float balance,
			Date creationDate, Region region, List<Item> offeredItems,
			List<Comment> givenComments,
			List<Comment> receivedComments, String customerClass) {
		this.id = new Integer(id);
		this.firstname = firstname;
		this.lastname = lastname;
		this.nickname = nickname;
		this.password = password;
		this.email = email;
		this.rating = rating;
		this.balance = balance;
		this.creationDate = creationDate;
		this.region = region;
		this.offeredItems = offeredItems;
		this.givenComments = givenComments;
		this.receivedComments = receivedComments;
		this.customerClass = CustomerClass
				.getCustomerClassByString(customerClass);
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return this.firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return this.lastname;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return this.rating;
	}

	/**
	 * @return the balance
	 */
	public float getBalance() {
		return this.balance;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * @return the region
	 */
	public Region getRegion() {
		return this.region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	void setRegion(Region region) {
		this.region = region;
	}

	/**
	 * @return the offeredItems
	 */
	public List<Item> getOfferedItems() {
		if (this.offeredItems == null) {
			return Collections.unmodifiableList(new LinkedList<Item>());
		} else {
			return Collections.unmodifiableList(this.offeredItems);
		}
	}

	/**
	 * @return the givenComments
	 */
	public List<Comment> getGivenComments() {
		if (this.givenComments == null) {
			return Collections.unmodifiableList(new LinkedList<Comment>());
		} else {
			return Collections.unmodifiableList(this.givenComments);
		}
	}

	/**
	 * @return the receivedComments
	 */
	public List<Comment> getReceivedComments() {
		if (this.receivedComments == null) {
			return Collections.unmodifiableList(new LinkedList<Comment>());
		} else {
			return Collections.unmodifiableList(this.receivedComments);
		}
	}

	/**
	 * @return the customerClass
	 */
	public CustomerClass getCustomerClass() {
		return customerClass;
	}

	/**
	 * @param customerClass
	 *            the customerClass to set
	 */
	public void setCustomerClass(CustomerClass customerClass) {
		this.customerClass = customerClass;
	}

	/**
	 * @param customerClass
	 *            the customerClass to set
	 */
	public void setCustomerClass(String customerClass) {
		this.customerClass = CustomerClass
				.getCustomerClassByString(customerClass);
	}

//	/**
//	 * 
//	 * @return a String representation of this user.
//	 */
//	public String infoString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()
//				+ ": ID: " + this.getId() + "\n");
//		sb.append("  Name: " + this.getNickname() + " (" + this.getFirstname()
//				+ " " + this.getLastname() + "), Email: " + this.getEmail()
//				+ "\n");
//		sb.append("  Rating: " + this.getRating() + ", Balance: "
//				+ this.getBalance() + ", Registration Date: "
//				+ this.getCreationDate() + ", Customer Class: "
//				+ this.getCustomerClass() + "\n");
//		sb.append("  Region: " + this.getRegion().infoString() + "\n");
//
//		sb.append("  Offered Items: " + this.getOfferedItems().size()
//				+ " items are offered by this user:\n");
//		for (Item i : this.getOfferedItems()) {
//			sb.append("    - " + i.infoString());
//		}
//
//		sb.append("  Bids: " + this.getBids().size()
//				+ " bids have been issued by this user:\n");
//		for (Bid b : this.getBids()) {
//			sb.append("    - " + b.infoString());
//		}
//
//		sb.append("  Buy-Nows: " + this.getBuyNows().size()
//				+ " buy-nows have been done by this user:\n");
//		for (BuyNow b : this.getBuyNows()) {
//			sb.append("    - " + b.infoString());
//		}
//
//		sb.append("  Comments: " + this.getGivenComments().size()
//				+ " comments were given by this user:\n");
//		for (Comment c : this.getGivenComments()) {
//			sb.append("    - " + c.infoString());
//		}
//
//		sb.append("  Comments: " + this.getReceivedComments().size()
//				+ " comments were given to this user:\n");
//		for (Comment c : this.getReceivedComments()) {
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
		final User other = (User) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Retrieves the list of currently offered items by this user.
	 * 
	 * @return the user's currently offered items.
	 */
	public List<Item> getCurrentSellings() {
		List<Item> currentSellings = new LinkedList<Item>();

		Date currentDate = new Date(System.currentTimeMillis());

		for (Item selling : this.getOfferedItems()) {
			if (selling.getEndDate().after(currentDate)) {
				currentSellings.add(selling);
			}
		}
		return Collections.unmodifiableList(currentSellings);
	}

	/**
	 * Retrieves the list of items offered by this user in the past.
	 * 
	 * @return the user's offered items in the past.
	 */
	public List<Item> getPastSellings() {
		List<Item> pastSellings = new LinkedList<Item>();

		Date currentDate = new Date(System.currentTimeMillis());

		for (Item selling : this.getOfferedItems()) {
			if (selling.getEndDate().before(currentDate)) {
				pastSellings.add(selling);
			}
		}
		return Collections.unmodifiableList(pastSellings);
	}

	/**
	 * Returns a string displaying general information about the user. The
	 * string contains HTML tags.
	 * 
	 * @return string containing general user information
	 */
	public String getHTMLGeneralUserInformation() {
		String result = new String();

		result = "<h2>Information about " + this.getNickname() + "<br /></h2>";
		result = result + "Real life name : " + this.getFirstname() + " "
				+ this.getLastname() + "<br />";
		result = result + "Email address  : " + this.getEmail() + "<br />";
		result = result + "User since     : "
				+ this.getCreationDate().toString() + "<br />";
		result = result + "Current rating : <b>" + this.getRating()
				+ "</b><br />";
		return result;
	}

}
