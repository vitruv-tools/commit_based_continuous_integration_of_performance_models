//package de.hpi.sam.rubis.bidandbuy.impl;
//
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//
//import de.hpi.sam.rubis.bidandbuy.BuyNowService;
//import de.hpi.sam.rubis.bidandbuy.BuyNowServiceException;
//import de.hpi.sam.rubis.entity.BuyNow;
//
//@Stateless(mappedName = BuyNowService.MAPPED_NAME)
//public class BuyNowServiceBeanWrapper implements BuyNowService {
//
//	@EJB(mappedName = BuyNowService.MAPPED_NAME + "_wrapped")
//	private BuyNowService delegatee;
//
//	@Override
//	public BuyNow buyItemNow(int itemId, int quantity, String nickname, String password) throws BuyNowServiceException {
//		return delegatee.buyItemNow(itemId, quantity, nickname, password);
//	}
//
//}
