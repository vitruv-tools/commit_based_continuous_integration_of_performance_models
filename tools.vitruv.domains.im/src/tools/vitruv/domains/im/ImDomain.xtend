package tools.vitruv.domains.im

import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain
import tools.vitruv.framework.tuid.TuidCalculatorAndResolver
import tools.vitruv.framework.tuid.AttributeTuidCalculatorAndResolver
import tools.vitruv.domains.emf.builder.VitruviusEmfBuilderApplicator
import tools.vitruv.models.im.ImPackage

class ImDomain extends AbstractTuidAwareVitruvDomain{
	
	public static final String METAMODEL_NAME = "IM"
	
	package new() {
		super(METAMODEL_NAME, ImNamespace.ROOT_PACKAGE, generateTuidCalculator(), ImNamespace.FILE_EXTENSION);
	}
	
	
	def protected static TuidCalculatorAndResolver generateTuidCalculator() {
		return new AttributeTuidCalculatorAndResolver(ImNamespace.METAMODEL_NAMESPACE, 
			#[ImPackage.Literals.PROBE__ABSTRACT_ACTION_ID.name] // FIXME 
		);
	}
	
	override getBuilderApplicator() {
		return new VitruviusEmfBuilderApplicator();
	}
	
}