package cipm.consistency.domains.pcm;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;

import com.google.common.collect.Maps;

import tools.vitruv.domains.pcm.PcmNamespace;
import tools.vitruv.framework.domains.AbstractVitruvDomain;

public class ExtendedPcmDomain extends AbstractVitruvDomain {
	private static final String METAMODEL_NAME = "PCMExtended";

	private static final String RESOURCEENVIRONMENT_FILE_EXTENSION = "resourceenvironment";
	private static final String ALLOCATION_FILE_EXTENSION = "allocation";
	private static final String USAGEMODEL_FILE_EXTENSION = "usagemodel";
	
	private boolean shouldTransitivelyPropagateChanges = false;

	public ExtendedPcmDomain() {
		super(METAMODEL_NAME, PcmNamespace.ROOT_PACKAGE, generateExtensionList());
	}

	private static String[] generateExtensionList() {
		return new String[] { PcmNamespace.REPOSITORY_FILE_EXTENSION, PcmNamespace.SYSTEM_FILE_EXTENSION,
				RESOURCEENVIRONMENT_FILE_EXTENSION, ALLOCATION_FILE_EXTENSION, USAGEMODEL_FILE_EXTENSION };
	}

	@Override
	public Map<Object, Object> getDefaultLoadOptions() {
		Map<Object, Object> loadOptions = Maps.newHashMap();
		loadOptions.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
		loadOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
		loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.TRUE);
		loadOptions.put(XMLResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl());
		loadOptions.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<>());
		return loadOptions;
	}
	
	@Override
	public boolean shouldTransitivelyPropagateChanges() {
		return this.shouldTransitivelyPropagateChanges;
	}
	
	/**
	 * Enables the transitive change propagation.
	 */
	public void enableTransitiveChangePropagation() {
		this.shouldTransitivelyPropagateChanges = true;
	}
}
