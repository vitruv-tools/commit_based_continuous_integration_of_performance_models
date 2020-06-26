package de.hpi.sam.rubis.usermgmt.impl;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.hpi.sam.rubis.entity.Region;
import de.hpi.sam.rubis.queryservice.BasicQueryService;
import de.hpi.sam.rubis.queryservice.QueryServiceException;
import de.hpi.sam.rubis.usermgmt.BrowseRegionsService;
import de.hpi.sam.rubis.usermgmt.BrowseRegionsServiceException;

/**
 * Implementation of the {@link BrowseRegionsService}.
 * 
 * @author thomas
 * 
 */
@Stateless(name = BrowseRegionsService.NAME)
public class BrowseRegionsServiceBean implements BrowseRegionsService {

	@EJB
	private BasicQueryService basicQueryService;

    private boolean suppressResults = Boolean.getBoolean("suppressResults");
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Region> getAllRegions() throws BrowseRegionsServiceException {

		try {
			List<Region> regions = this.basicQueryService.findAllRegions();
            if (suppressResults) {
                return Collections.emptyList();
            }
            return regions;
		} catch (QueryServiceException e) {
			throw new BrowseRegionsServiceException(
					"Error in retrieving all regions.", e);
		}

	}

}
