package cipm.consistency.domains.im;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;

import com.google.common.collect.Maps;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import tools.vitruv.framework.domains.AbstractVitruvDomain;

public class InstrumentationModelDomain extends AbstractVitruvDomain {
	public final static String INSTRUMENTATION_SUFFIX = ".imm";
	
	public InstrumentationModelDomain() {
		super("IMM", InstrumentationModelPackage.eINSTANCE,
				INSTRUMENTATION_SUFFIX.substring(1));
	}

	@Override
	public boolean isUserVisible() {
		return false;
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
}
