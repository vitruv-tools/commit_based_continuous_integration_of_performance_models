package tools.vitruv.applications.pcmjava.seffstatements.code2seff

import java.util.Set
import org.emftext.language.java.commons.CommonsPackage
import org.emftext.language.java.members.Method
import org.palladiosimulator.pcm.repository.BasicComponent
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute
import tools.vitruv.change.composite.MetamodelDescriptor
import tools.vitruv.change.correspondence.Correspondence
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView
import tools.vitruv.change.interaction.UserInteractor
import tools.vitruv.change.propagation.ResourceAccess
import tools.vitruv.change.propagation.impl.AbstractChangePropagationSpecification

class Java2PcmMethodBodyChangePreprocessor extends AbstractChangePropagationSpecification {
	val Code2SeffFactory code2SeffFactory;

	new(Code2SeffFactory code2SEFFfactory) {
		this(code2SEFFfactory, MetamodelDescriptor.with(Set.of("http://www.xtext.org/lua/Lua")),
			MetamodelDescriptor.with(Set.of("http://palladiosimulator.org/PalladioComponentModel/5.2")));
	}

	new(Code2SeffFactory code2SEFFfactory, MetamodelDescriptor sourceDomain, MetamodelDescriptor targetDomain) {
		super(sourceDomain, targetDomain)
		this.code2SeffFactory = code2SEFFfactory
	}

	override propagateChange(EChange change, EditableCorrespondenceModelView<Correspondence> correspondenceModelView,
		ResourceAccess resourceAccess) {
		if (doesHandleChange(change, correspondenceModelView)) {
			val attrChange = change as ReplaceSingleValuedEAttribute<?, ?>;
			val meth = attrChange.affectedEObject as Method;
			executeClassMethodBodyChangeRefiner(correspondenceModelView, userInteractor, meth);
		}
	}

	override doesHandleChange(EChange change, EditableCorrespondenceModelView<Correspondence> correspondenceModelView) {
		if (!(change instanceof ReplaceSingleValuedEAttribute)) {
			return false;
		}
		val attrChange = change as ReplaceSingleValuedEAttribute<?, ?>;
		return attrChange.affectedEObject instanceof Method &&
			attrChange.affectedFeature == CommonsPackage.Literals.NAMED_ELEMENT__NAME && !attrChange.newValue.equals("")
	}

	private def void executeClassMethodBodyChangeRefiner(
		EditableCorrespondenceModelView<Correspondence> correspondenceModelView, UserInteractor userInteracting,
		Method newMethod) {
		val basicComponentFinding = code2SeffFactory.createBasicComponentFinding
		val BasicComponent myBasicComponent = basicComponentFinding.findBasicComponentForMethod(newMethod,
			correspondenceModelView);
		val classification = code2SeffFactory.createAbstractFunctionClassificationStrategy(basicComponentFinding,
			correspondenceModelView, myBasicComponent);
		val InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory = code2SeffFactory.
			createInterfaceOfExternalCallFindingFactory(correspondenceModelView, myBasicComponent);
		val ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding = code2SeffFactory.
			createResourceDemandingBehaviourForClassMethodFinding(correspondenceModelView);
		val ClassMethodBodyChangedTransformation methodBodyChanged = createTransformation(newMethod,
			basicComponentFinding, classification, interfaceOfExternalCallFinderFactory,
			resourceDemandingBehaviourForClassMethodFinding);
		methodBodyChanged.execute(correspondenceModelView, userInteracting);
	}

	protected def ClassMethodBodyChangedTransformation createTransformation(Method newMethod,
		BasicComponentFinding basicComponentFinding, AbstractFunctionClassificationStrategy classification,
		InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory,
		ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		return new ClassMethodBodyChangedTransformation(newMethod, basicComponentFinding, classification,
			interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding)
	}
}
