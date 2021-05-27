package cipm.consistency.base.vsum.domains;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;

import com.google.common.collect.Maps;

import cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage;
import cipm.consistency.base.vsum.manager.VsumConstants;
import tools.vitruv.domains.emf.builder.VitruviusEmfBuilderApplicator;
import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain;
import tools.vitruv.framework.domains.VitruviusProjectBuilderApplicator;
import tools.vitruv.framework.tuid.AttributeTuidCalculatorAndResolver;

public class RuntimeEnvironmentDomain extends AbstractTuidAwareVitruvDomain {
	public RuntimeEnvironmentDomain() {
		super("REM", REModelPackage.eINSTANCE,
				new AttributeTuidCalculatorAndResolver(REModelPackage.eINSTANCE.getNsURI(), "id", "hostID",
						"entityName"),
				VsumConstants.RUNTIMEENVIRONMENT_SUFFIX.substring(1));
	}

	@Override
	public VitruviusProjectBuilderApplicator getBuilderApplicator() {
		return new VitruviusEmfBuilderApplicator();
	}

	@Override
	public boolean isUserVisible() {
		return false;
	}

	@Override
	public boolean supportsUuids() {
		return true;
	}

	@Override
	public boolean isRelinkUuids() {
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
