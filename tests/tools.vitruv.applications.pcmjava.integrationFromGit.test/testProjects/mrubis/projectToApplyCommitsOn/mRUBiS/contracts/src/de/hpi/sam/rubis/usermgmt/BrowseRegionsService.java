package de.hpi.sam.rubis.usermgmt;

import java.util.List;

import javax.ejb.Remote;

import de.hpi.sam.rubis.entity.Region;

/**
 * Service for browsing the regions.
 * 
 * @author thomas
 * 
 */
@Remote
public interface BrowseRegionsService {

	/**
	 * Mapped name to find the service.
	 */
	public static String NAME = "BrowseRegionsService";

	/**
	 * Retrieves all regions.
	 * 
	 * @return a list of all regions
	 * @throws BrowseRegionsServiceException
	 *             if there is a failure in retrieving all categories.
	 */
	public List<Region> getAllRegions() throws BrowseRegionsServiceException;
}
