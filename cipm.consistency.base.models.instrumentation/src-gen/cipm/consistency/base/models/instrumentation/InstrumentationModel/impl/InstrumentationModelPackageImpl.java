/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel.impl;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelFactory;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationType;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;

import de.uka.ipd.sdq.identifier.IdentifierPackage;

import de.uka.ipd.sdq.probfunction.ProbfunctionPackage;

import de.uka.ipd.sdq.stoex.StoexPackage;

import de.uka.ipd.sdq.units.UnitsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.palladiosimulator.pcm.PcmPackage;

import org.palladiosimulator.pcm.seff.SeffPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class InstrumentationModelPackageImpl extends EPackageImpl implements InstrumentationModelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass instrumentationModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serviceInstrumentationPointEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass instrumentationPointEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionInstrumentationPointEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum instrumentationTypeEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private InstrumentationModelPackageImpl() {
		super(eNS_URI, InstrumentationModelFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link InstrumentationModelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static InstrumentationModelPackage init() {
		if (isInited) return (InstrumentationModelPackage)EPackage.Registry.INSTANCE.getEPackage(InstrumentationModelPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredInstrumentationModelPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		InstrumentationModelPackageImpl theInstrumentationModelPackage = registeredInstrumentationModelPackage instanceof InstrumentationModelPackageImpl ? (InstrumentationModelPackageImpl)registeredInstrumentationModelPackage : new InstrumentationModelPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		IdentifierPackage.eINSTANCE.eClass();
		PcmPackage.eINSTANCE.eClass();
		ProbfunctionPackage.eINSTANCE.eClass();
		StoexPackage.eINSTANCE.eClass();
		UnitsPackage.eINSTANCE.eClass();
		EcorePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theInstrumentationModelPackage.createPackageContents();

		// Initialize created meta-data
		theInstrumentationModelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theInstrumentationModelPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(InstrumentationModelPackage.eNS_URI, theInstrumentationModelPackage);
		return theInstrumentationModelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInstrumentationModel() {
		return instrumentationModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInstrumentationModel_Points() {
		return (EReference)instrumentationModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getServiceInstrumentationPoint() {
		return serviceInstrumentationPointEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServiceInstrumentationPoint_Service() {
		return (EReference)serviceInstrumentationPointEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServiceInstrumentationPoint_ActionInstrumentationPoints() {
		return (EReference)serviceInstrumentationPointEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInstrumentationPoint() {
		return instrumentationPointEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInstrumentationPoint_Active() {
		return (EAttribute)instrumentationPointEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActionInstrumentationPoint() {
		return actionInstrumentationPointEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionInstrumentationPoint_Action() {
		return (EReference)actionInstrumentationPointEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActionInstrumentationPoint_Type() {
		return (EAttribute)actionInstrumentationPointEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getInstrumentationType() {
		return instrumentationTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InstrumentationModelFactory getInstrumentationModelFactory() {
		return (InstrumentationModelFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		instrumentationModelEClass = createEClass(INSTRUMENTATION_MODEL);
		createEReference(instrumentationModelEClass, INSTRUMENTATION_MODEL__POINTS);

		serviceInstrumentationPointEClass = createEClass(SERVICE_INSTRUMENTATION_POINT);
		createEReference(serviceInstrumentationPointEClass, SERVICE_INSTRUMENTATION_POINT__SERVICE);
		createEReference(serviceInstrumentationPointEClass, SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS);

		instrumentationPointEClass = createEClass(INSTRUMENTATION_POINT);
		createEAttribute(instrumentationPointEClass, INSTRUMENTATION_POINT__ACTIVE);

		actionInstrumentationPointEClass = createEClass(ACTION_INSTRUMENTATION_POINT);
		createEReference(actionInstrumentationPointEClass, ACTION_INSTRUMENTATION_POINT__ACTION);
		createEAttribute(actionInstrumentationPointEClass, ACTION_INSTRUMENTATION_POINT__TYPE);

		// Create enums
		instrumentationTypeEEnum = createEEnum(INSTRUMENTATION_TYPE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		IdentifierPackage theIdentifierPackage = (IdentifierPackage)EPackage.Registry.INSTANCE.getEPackage(IdentifierPackage.eNS_URI);
		SeffPackage theSeffPackage = (SeffPackage)EPackage.Registry.INSTANCE.getEPackage(SeffPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		instrumentationModelEClass.getESuperTypes().add(theIdentifierPackage.getIdentifier());
		serviceInstrumentationPointEClass.getESuperTypes().add(this.getInstrumentationPoint());
		instrumentationPointEClass.getESuperTypes().add(theIdentifierPackage.getIdentifier());
		actionInstrumentationPointEClass.getESuperTypes().add(this.getInstrumentationPoint());

		// Initialize classes, features, and operations; add parameters
		initEClass(instrumentationModelEClass, InstrumentationModel.class, "InstrumentationModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInstrumentationModel_Points(), this.getServiceInstrumentationPoint(), null, "points", null, 0, -1, InstrumentationModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(serviceInstrumentationPointEClass, ServiceInstrumentationPoint.class, "ServiceInstrumentationPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getServiceInstrumentationPoint_Service(), theSeffPackage.getResourceDemandingSEFF(), null, "service", null, 1, 1, ServiceInstrumentationPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServiceInstrumentationPoint_ActionInstrumentationPoints(), this.getActionInstrumentationPoint(), null, "actionInstrumentationPoints", null, 0, -1, ServiceInstrumentationPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(instrumentationPointEClass, InstrumentationPoint.class, "InstrumentationPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getInstrumentationPoint_Active(), ecorePackage.getEBoolean(), "active", null, 0, 1, InstrumentationPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionInstrumentationPointEClass, ActionInstrumentationPoint.class, "ActionInstrumentationPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActionInstrumentationPoint_Action(), theSeffPackage.getAbstractAction(), null, "action", null, 1, 1, ActionInstrumentationPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getActionInstrumentationPoint_Type(), this.getInstrumentationType(), "type", null, 1, 1, ActionInstrumentationPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(instrumentationTypeEEnum, InstrumentationType.class, "InstrumentationType");
		addEEnumLiteral(instrumentationTypeEEnum, InstrumentationType.INTERNAL);
		addEEnumLiteral(instrumentationTypeEEnum, InstrumentationType.BRANCH);
		addEEnumLiteral(instrumentationTypeEEnum, InstrumentationType.LOOP);
		addEEnumLiteral(instrumentationTypeEEnum, InstrumentationType.EXTERNAL_CALL);
		addEEnumLiteral(instrumentationTypeEEnum, InstrumentationType.INTERNAL_CALL);

		// Create resource
		createResource(eNS_URI);
	}

} //InstrumentationModelPackageImpl
