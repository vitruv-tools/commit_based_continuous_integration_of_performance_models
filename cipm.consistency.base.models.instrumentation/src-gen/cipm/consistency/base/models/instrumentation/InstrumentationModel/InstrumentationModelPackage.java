/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.impl.InstrumentationModelPackageImpl;
import de.uka.ipd.sdq.identifier.IdentifierPackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelFactory
 * @model kind="package"
 * @generated
 */
public interface InstrumentationModelPackage extends EPackage {
	/**
	 * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNAME = "InstrumentationMetamodel";

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_URI = "http://www.dmodel.com/InstrumentationMetamodel";

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_PREFIX = "imm";

	/**
	 * The singleton instance of the package. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	InstrumentationModelPackage eINSTANCE = InstrumentationModelPackageImpl.init();

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelImpl
	 * <em>Instrumentation Model</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelImpl
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getInstrumentationModel()
	 * @generated
	 */
	int INSTRUMENTATION_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int INSTRUMENTATION_MODEL__ID = IdentifierPackage.IDENTIFIER__ID;

	/**
	 * The feature id for the '<em><b>Points</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int INSTRUMENTATION_MODEL__POINTS = IdentifierPackage.IDENTIFIER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Instrumentation Model</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int INSTRUMENTATION_MODEL_FEATURE_COUNT = IdentifierPackage.IDENTIFIER_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Instrumentation Model</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int INSTRUMENTATION_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationPointImpl
	 * <em>Instrumentation Point</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationPointImpl
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getInstrumentationPoint()
	 * @generated
	 */
	int INSTRUMENTATION_POINT = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int INSTRUMENTATION_POINT__ID = IdentifierPackage.IDENTIFIER__ID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int INSTRUMENTATION_POINT__ACTIVE = IdentifierPackage.IDENTIFIER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Instrumentation Point</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int INSTRUMENTATION_POINT_FEATURE_COUNT = IdentifierPackage.IDENTIFIER_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Instrumentation Point</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int INSTRUMENTATION_POINT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.impl.ServiceInstrumentationPointImpl
	 * <em>Service Instrumentation Point</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.ServiceInstrumentationPointImpl
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getServiceInstrumentationPoint()
	 * @generated
	 */
	int SERVICE_INSTRUMENTATION_POINT = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SERVICE_INSTRUMENTATION_POINT__ID = INSTRUMENTATION_POINT__ID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SERVICE_INSTRUMENTATION_POINT__ACTIVE = INSTRUMENTATION_POINT__ACTIVE;

	/**
	 * The feature id for the '<em><b>Service</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SERVICE_INSTRUMENTATION_POINT__SERVICE = INSTRUMENTATION_POINT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Action Instrumentation Points</b></em>'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS = INSTRUMENTATION_POINT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Service Instrumentation
	 * Point</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SERVICE_INSTRUMENTATION_POINT_FEATURE_COUNT = INSTRUMENTATION_POINT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Service Instrumentation Point</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SERVICE_INSTRUMENTATION_POINT_OPERATION_COUNT = INSTRUMENTATION_POINT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.impl.ActionInstrumentationPointImpl
	 * <em>Action Instrumentation Point</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.ActionInstrumentationPointImpl
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getActionInstrumentationPoint()
	 * @generated
	 */
	int ACTION_INSTRUMENTATION_POINT = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTION_INSTRUMENTATION_POINT__ID = INSTRUMENTATION_POINT__ID;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTION_INSTRUMENTATION_POINT__ACTIVE = INSTRUMENTATION_POINT__ACTIVE;

	/**
	 * The feature id for the '<em><b>Action</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTION_INSTRUMENTATION_POINT__ACTION = INSTRUMENTATION_POINT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTION_INSTRUMENTATION_POINT__TYPE = INSTRUMENTATION_POINT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Action Instrumentation
	 * Point</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTION_INSTRUMENTATION_POINT_FEATURE_COUNT = INSTRUMENTATION_POINT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Action Instrumentation Point</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTION_INSTRUMENTATION_POINT_OPERATION_COUNT = INSTRUMENTATION_POINT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType
	 * <em>Instrumentation Type</em>}' enum. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getInstrumentationType()
	 * @generated
	 */
	int INSTRUMENTATION_TYPE = 4;

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel
	 * <em>Instrumentation Model</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for class '<em>Instrumentation Model</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel
	 * @generated
	 */
	EClass getInstrumentationModel();

	/**
	 * Returns the meta object for the containment reference list
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel#getPoints
	 * <em>Points</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Points</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModel#getPoints()
	 * @see #getInstrumentationModel()
	 * @generated
	 */
	EReference getInstrumentationModel_Points();

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint
	 * <em>Service Instrumentation Point</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Service Instrumentation Point</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint
	 * @generated
	 */
	EClass getServiceInstrumentationPoint();

	/**
	 * Returns the meta object for the reference
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint#getService
	 * <em>Service</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Service</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint#getService()
	 * @see #getServiceInstrumentationPoint()
	 * @generated
	 */
	EReference getServiceInstrumentationPoint_Service();

	/**
	 * Returns the meta object for the containment reference list
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint#getActionInstrumentationPoints
	 * <em>Action Instrumentation Points</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Action
	 *         Instrumentation Points</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint#getActionInstrumentationPoints()
	 * @see #getServiceInstrumentationPoint()
	 * @generated
	 */
	EReference getServiceInstrumentationPoint_ActionInstrumentationPoints();

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationPoint
	 * <em>Instrumentation Point</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for class '<em>Instrumentation Point</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationPoint
	 * @generated
	 */
	EClass getInstrumentationPoint();

	/**
	 * Returns the meta object for the attribute
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationPoint#isActive
	 * <em>Active</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Active</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationPoint#isActive()
	 * @see #getInstrumentationPoint()
	 * @generated
	 */
	EAttribute getInstrumentationPoint_Active();

	/**
	 * Returns the meta object for class
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint
	 * <em>Action Instrumentation Point</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Action Instrumentation Point</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint
	 * @generated
	 */
	EClass getActionInstrumentationPoint();

	/**
	 * Returns the meta object for the reference
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint#getAction
	 * <em>Action</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Action</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint#getAction()
	 * @see #getActionInstrumentationPoint()
	 * @generated
	 */
	EReference getActionInstrumentationPoint_Action();

	/**
	 * Returns the meta object for the attribute
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint#getType
	 * <em>Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint#getType()
	 * @see #getActionInstrumentationPoint()
	 * @generated
	 */
	EAttribute getActionInstrumentationPoint_Type();

	/**
	 * Returns the meta object for enum
	 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType
	 * <em>Instrumentation Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for enum '<em>Instrumentation Type</em>'.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType
	 * @generated
	 */
	EEnum getInstrumentationType();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	InstrumentationModelFactory getInstrumentationModelFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each operation of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelImpl
		 * <em>Instrumentation Model</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelImpl
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getInstrumentationModel()
		 * @generated
		 */
		EClass INSTRUMENTATION_MODEL = eINSTANCE.getInstrumentationModel();

		/**
		 * The meta object literal for the '<em><b>Points</b></em>' containment
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference INSTRUMENTATION_MODEL__POINTS = eINSTANCE.getInstrumentationModel_Points();

		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.impl.ServiceInstrumentationPointImpl
		 * <em>Service Instrumentation Point</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.ServiceInstrumentationPointImpl
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getServiceInstrumentationPoint()
		 * @generated
		 */
		EClass SERVICE_INSTRUMENTATION_POINT = eINSTANCE.getServiceInstrumentationPoint();

		/**
		 * The meta object literal for the '<em><b>Service</b></em>' reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference SERVICE_INSTRUMENTATION_POINT__SERVICE = eINSTANCE.getServiceInstrumentationPoint_Service();

		/**
		 * The meta object literal for the '<em><b>Action Instrumentation
		 * Points</b></em>' containment reference list feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS = eINSTANCE
				.getServiceInstrumentationPoint_ActionInstrumentationPoints();

		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationPointImpl
		 * <em>Instrumentation Point</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationPointImpl
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getInstrumentationPoint()
		 * @generated
		 */
		EClass INSTRUMENTATION_POINT = eINSTANCE.getInstrumentationPoint();

		/**
		 * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute INSTRUMENTATION_POINT__ACTIVE = eINSTANCE.getInstrumentationPoint_Active();

		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.impl.ActionInstrumentationPointImpl
		 * <em>Action Instrumentation Point</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.ActionInstrumentationPointImpl
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getActionInstrumentationPoint()
		 * @generated
		 */
		EClass ACTION_INSTRUMENTATION_POINT = eINSTANCE.getActionInstrumentationPoint();

		/**
		 * The meta object literal for the '<em><b>Action</b></em>' reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference ACTION_INSTRUMENTATION_POINT__ACTION = eINSTANCE.getActionInstrumentationPoint_Action();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute ACTION_INSTRUMENTATION_POINT__TYPE = eINSTANCE.getActionInstrumentationPoint_Type();

		/**
		 * The meta object literal for the
		 * '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType
		 * <em>Instrumentation Type</em>}' enum. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType
		 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.impl.InstrumentationModelPackageImpl#getInstrumentationType()
		 * @generated
		 */
		EEnum INSTRUMENTATION_TYPE = eINSTANCE.getInstrumentationType();

	}

} // InstrumentationModelPackage
