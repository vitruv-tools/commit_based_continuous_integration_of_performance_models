package de.hpi.sam.rubis.client.main;

import java.util.List;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;

import de.hpi.sam.rubis.entity.Item;
import de.hpi.sam.rubis.itemmgmt.BrowseCategoriesService;

public class ClientRunnable implements Runnable {

	private static Logger logger = Logger
			.getLogger("de.hpi.sam.rubis.client.main");

	private long id;

	public ClientRunnable(long id) {
		this.id = id;
	}

	@Override
	public void run() {
		try {
			Context ctx = new InitialContext();
			BrowseCategoriesService browseCategoriesService = (BrowseCategoriesService) ctx
					.lookup(BrowseCategoriesService.class.getCanonicalName());

			List<Item> result = browseCategoriesService.getItemsByName("the");

			logger.info("Thread " + this.id + " has retrieved "
					+ result.size() + " items.");

		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
