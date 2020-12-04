//package de.hpi.sam.rubis.client.main;
//
//import java.util.Date;
//import java.util.List;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//
//import de.hpi.sam.rubis.bidandbuy.BidService;
//import de.hpi.sam.rubis.bidandbuy.BidServiceException;
//import de.hpi.sam.rubis.bidandbuy.BuyNowService;
//import de.hpi.sam.rubis.bidandbuy.BuyNowServiceException;
//import de.hpi.sam.rubis.entity.Bid;
//import de.hpi.sam.rubis.entity.BuyNow;
//import de.hpi.sam.rubis.entity.Category;
//import de.hpi.sam.rubis.entity.Comment;
//import de.hpi.sam.rubis.entity.CustomerClass;
//import de.hpi.sam.rubis.entity.Item;
//import de.hpi.sam.rubis.entity.Region;
//import de.hpi.sam.rubis.entity.User;
//import de.hpi.sam.rubis.entity.UserInfo;
//import de.hpi.sam.rubis.entity.UserProfile;
//import de.hpi.sam.rubis.itemmgmt.BrowseCategoriesService;
//import de.hpi.sam.rubis.itemmgmt.BrowseCategoriesServiceException;
//import de.hpi.sam.rubis.itemmgmt.ItemRegistrationService;
//import de.hpi.sam.rubis.itemmgmt.ItemRegistrationServiceException;
//import de.hpi.sam.rubis.reputationservice.ReputationService;
//import de.hpi.sam.rubis.reputationservice.ReputationServiceException;
//import de.hpi.sam.rubis.usermgmt.AboutMeService;
//import de.hpi.sam.rubis.usermgmt.AboutMeServiceException;
//import de.hpi.sam.rubis.usermgmt.BrowseRegionsService;
//import de.hpi.sam.rubis.usermgmt.BrowseRegionsServiceException;
//import de.hpi.sam.rubis.usermgmt.UserRegistrationService;
//import de.hpi.sam.rubis.usermgmt.UserRegistrationServiceException;
//import de.hpi.sam.rubis.usermgmt.ViewUserInfoService;
//import de.hpi.sam.rubis.usermgmt.ViewUserInfoServiceException;
//
//public class ClientSession {
//
//	private BrowseRegionsService browseRegionsService;
//	private UserRegistrationService userRegistrationService;
//	private AboutMeService aboutMeService;
//	private BrowseCategoriesService browseCategoriesService;
//	private ItemRegistrationService itemRegistrationService;
//	private BidService bidService;
//	private ViewUserInfoService viewUserInfoService;
//	private BuyNowService buyNowService;
//	private ReputationService reputationService;
//
//	private String itemName;
//	private int itemId;
//	private String buyerNickname;
//	private String buyerPassword;
//
//	public ClientSession() {
//		this.initialize();
//	}
//
//	/**
//	 * Initializes references to the RUBiS services.
//	 */
//	private void initialize() {
//
//		try {
//			Context ctx = new InitialContext();
//
//			this.browseRegionsService = (BrowseRegionsService) ctx
//					.lookup(BrowseRegionsService.MAPPED_NAME + "#"
//							+ BrowseRegionsService.class.getCanonicalName());
//
//			this.userRegistrationService = (UserRegistrationService) ctx
//					.lookup(UserRegistrationService.MAPPED_NAME + "#"
//							+ UserRegistrationService.class.getCanonicalName());
//
//			this.aboutMeService = (AboutMeService) ctx
//					.lookup(AboutMeService.MAPPED_NAME + "#"
//							+ AboutMeService.class.getCanonicalName());
//
//			this.browseCategoriesService = (BrowseCategoriesService) ctx
//					.lookup(BrowseCategoriesService.MAPPED_NAME + "#"
//							+ BrowseCategoriesService.class.getCanonicalName());
//
//			this.itemRegistrationService = (ItemRegistrationService) ctx
//					.lookup(ItemRegistrationService.MAPPED_NAME + "#"
//							+ ItemRegistrationService.class.getCanonicalName());
//
//			this.bidService = (BidService) ctx.lookup(BidService.MAPPED_NAME
//					+ "#" + BidService.class.getCanonicalName());
//
//			this.viewUserInfoService = (ViewUserInfoService) ctx
//					.lookup(ViewUserInfoService.MAPPED_NAME + "#"
//							+ ViewUserInfoService.class.getCanonicalName());
//
//			this.buyNowService = (BuyNowService) ctx
//					.lookup(BuyNowService.MAPPED_NAME + "#"
//							+ BuyNowService.class.getCanonicalName());
//
//			this.reputationService = (ReputationService) ctx
//					.lookup(ReputationService.MAPPED_NAME + "#"
//							+ ReputationService.class.getCanonicalName());
//
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public void initiliazeSellerSide() {
//		try {
//			System.out.println("== Seller Side ==");
//			System.out.println("Selling User:");
//
//			List<Region> regions = this.browseRegionsService.getAllRegions();
//			assert regions.size() > 0;
//			Region region = regions.get(0);
//			String regionName = region.getName();
//			User seller = this.userRegistrationService.registerUser("Ryan",
//					"Terry", "ryan", "Ryan.Terry@mail.com", "PAE20CZC2TL",
//					regionName, CustomerClass.GOLD);
//			System.out.println(seller.infoString());
//			UserProfile sellerUserProfile = this.aboutMeService.getAboutMe(
//					seller.getNickname(), seller.getPassword());
//			System.out.println(sellerUserProfile.infoString());
//
//			System.out.println("Item to be sold:");
//			List<Category> categories = this.browseCategoriesService
//					.getAllCategories();
//			assert categories.size() > 0;
//			Category category = null;
//			for (Category c : categories) {
//				if (c.getName().equals("Music")) {
//					category = c;
//					break;
//				}
//			}
//			assert category != null;
//
//			Item item = this.itemRegistrationService
//					.registerItem(
//							"The National - Trouble Will Find Me",
//							"Trouble Will Find Me is the highly anticipated sixth album of The National.",
//							1, 9.99f, 12.99f, 11.50f,
//							new Date(System.currentTimeMillis()), new Date(
//									System.currentTimeMillis()
//											+ (1000 * 60 * 60 * 2)), seller
//									.getId(), seller.getPassword(), category
//									.getId());
//			System.out.println(item.infoString());
//
//			this.itemId = item.getId();
//			this.itemName = item.getName();
//
//		} catch (BrowseRegionsServiceException e) {
//			e.printStackTrace();
//		} catch (UserRegistrationServiceException e) {
//			e.printStackTrace();
//		} catch (AboutMeServiceException e) {
//			e.printStackTrace();
//		} catch (BrowseCategoriesServiceException e) {
//			e.printStackTrace();
//		} catch (ItemRegistrationServiceException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public void initializeBuyerSide() {
//		try {
//			System.out.println("== Buyer Side ==");
//			System.out.println("Buying User:");
//			List<Region> regions = this.browseRegionsService.getAllRegions();
//			assert regions.size() > 0;
//			Region region = regions.get(0);
//			String regionName = region.getName();
//
//			User buyer = this.userRegistrationService.registerUser("Emma",
//					"Bailey", "emma", "Emma.Bailey@mail.com", "QFC45DAW7NC",
//					regionName, CustomerClass.SILVER);
//			System.out.println(buyer.infoString());
//			UserProfile buyerUserProfile = this.aboutMeService.getAboutMe(
//					buyer.getNickname(), buyer.getPassword());
//			System.out.println(buyerUserProfile.infoString());
//
//			this.buyerNickname = buyer.getNickname();
//			this.buyerPassword = buyer.getPassword();
//
//		} catch (BrowseRegionsServiceException e) {
//			e.printStackTrace();
//		} catch (UserRegistrationServiceException e) {
//			e.printStackTrace();
//		} catch (AboutMeServiceException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void bidOrBuyItem() {
//		try {
//			List<Item> items = this.browseCategoriesService
//					.getItemsByName(this.itemName);
//			Item itemToBuy = null;
//			for (Item i : items) {
//				if (i.getName().equals(this.itemName)
//						&& i.getId() == this.itemId) {
//					itemToBuy = i;
//					break;
//				}
//			}
//			assert itemToBuy != null;
//
//			int sellerId = itemToBuy.getSellerId();
//			System.out.println("SellerId : " + sellerId);
//
//			UserInfo remoteSeller = this.viewUserInfoService
//					.getUserInfo(sellerId);
//			System.out.println(remoteSeller.infoString());
//
//			List<Bid> bidsOnItemToBuy = this.bidService
//					.getItemBidHistorySortyByBidPrice(itemToBuy.getId());
//
//			float currentMaxPrice = itemToBuy.getInitialPrice();
//			if (bidsOnItemToBuy.size() > 0) {
//				currentMaxPrice = bidsOnItemToBuy.get(0).getBidPrice();
//			}
//			System.out.println("Bids on Item " + itemToBuy.infoString());
//			for (Bid b : bidsOnItemToBuy) {
//				System.out.println("    - " + b.infoString());
//			}
//
//			System.out.println("Bid on item ...");
//			Bid bid = this.bidService.bidOnItem(itemToBuy.getId(),
//					currentMaxPrice + 1f, currentMaxPrice + 2f, 1,
//					this.buyerNickname, this.buyerPassword);
//			System.out.println(bid.infoString());
//
//			// local item object is not updated with the bid that has just been
//			// issued
//			// System.out.println("Bids on Item " + itemToBuy.infoString());
//			// -> retrieve bids from server
//			bidsOnItemToBuy = this.bidService
//					.getItemBidHistorySortyByBidPrice(itemToBuy.getId());
//			for (Bid b : bidsOnItemToBuy) {
//				System.out.println("    - " + b.infoString());
//			}
//
//			// buy item
//			BuyNow buyNow = this.buyNowService.buyItemNow(itemToBuy.getId(), 1,
//					this.buyerNickname, this.buyerPassword);
//			System.out.println("bought item ... : " + buyNow.infoString());
//
//			// add comment
//			Comment comment = this.reputationService.giveComment(
//					this.buyerNickname, this.buyerPassword, sellerId,
//					itemToBuy.getId(), "Everything perfect!", 10);
//			System.out.println("Comment: " + comment.infoString());
//
//		} catch (BrowseCategoriesServiceException e) {
//			e.printStackTrace();
//		} catch (BidServiceException e) {
//			e.printStackTrace();
//		} catch (ViewUserInfoServiceException e) {
//			e.printStackTrace();
//		} catch (BuyNowServiceException e) {
//			e.printStackTrace();
//		} catch (ReputationServiceException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void main(String[] args) {
//
//		ClientSession cs = new ClientSession();
//		cs.initiliazeSellerSide();
//		cs.initializeBuyerSide();
//		cs.bidOrBuyItem();
//
//	}
//}
