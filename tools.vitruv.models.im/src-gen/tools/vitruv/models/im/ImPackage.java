/**
 */
package tools.vitruv.models.im;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see tools.vitruv.models.im.ImFactory
 * @model kind="package"
 * @generated
 */
public interface ImPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "im";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.example.org/im";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "im";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ImPackage eINSTANCE = tools.vitruv.models.im.impl.ImPackageImpl.init();

	/**
	 * The meta object id for the '{@link tools.vitruv.models.im.impl.AppProbesImpl <em>App Probes</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.models.im.impl.AppProbesImpl
	 * @see tools.vitruv.models.im.impl.ImPackageImpl#getAppProbes()
	 * @generated
	 */
	int APP_PROBES = 0;

	/**
	 * The feature id for the '<em><b>Probes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int APP_PROBES__PROBES = 0;

	/**
	 * The number of structural features of the '<em>App Probes</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int APP_PROBES_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>App Probes</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int APP_PROBES_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link tools.vitruv.models.im.impl.ProbeImpl <em>Probe</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see tools.vitruv.models.im.impl.ProbeImpl
	 * @see tools.vitruv.models.im.impl.ImPackageImpl#getProbe()
	 * @generated
	 */
	int PROBE = 1;

	/**
	 * The feature id for the '<em><b>Abstract Action ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROBE__ABSTRACT_ACTION_ID = 0;

	/**
	 * The feature id for the '<em><b>Is Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROBE__IS_ACTIVE = 1;

	/**
	 * The number of structural features of the '<em>Probe</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROBE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Probe</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROBE_OPERATION_COUNT = 0;

	/**
	 * Returns the meta object for class '{@link tools.vitruv.models.im.AppProbes <em>App Probes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>App Probes</em>'.
	 * @see tools.vitruv.models.im.AppProbes
	 * @generated
	 */
	EClass getAppProbes();

	/**
	 * Returns the meta object for the containment reference list '{@link tools.vitruv.models.im.AppProbes#getProbes <em>Probes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Probes</em>'.
	 * @see tools.vitruv.models.im.AppProbes#getProbes()
	 * @see #getAppProbes()
	 * @generated
	 */
	EReference getAppProbes_Probes();

	/**
	 * Returns the meta object for class '{@link tools.vitruv.models.im.Probe <em>Probe</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Probe</em>'.
	 * @see tools.vitruv.models.im.Probe
	 * @generated
	 */
	EClass getProbe();

	/**
	 * Returns the meta object for the attribute '{@link tools.vitruv.models.im.Probe#getAbstractActionID <em>Abstract Action ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Abstract Action ID</em>'.
	 * @see tools.vitruv.models.im.Probe#getAbstractActionID()
	 * @see #getProbe()
	 * @generated
	 */
	EAttribute getProbe_AbstractActionID();

	/**
	 * Returns the meta object for the attribute '{@link tools.vitruv.models.im.Probe#isIsActive <em>Is Active</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Active</em>'.
	 * @see tools.vitruv.models.im.Probe#isIsActive()
	 * @see #getProbe()
	 * @generated
	 */
	EAttribute getProbe_IsActive();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ImFactory getImFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link tools.vitruv.models.im.impl.AppProbesImpl <em>App Probes</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.models.im.impl.AppProbesImpl
		 * @see tools.vitruv.models.im.impl.ImPackageImpl#getAppProbes()
		 * @generated
		 */
		EClass APP_PROBES = eINSTANCE.getAppProbes();

		/**
		 * The meta object literal for the '<em><b>Probes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference APP_PROBES__PROBES = eINSTANCE.getAppProbes_Probes();

		/**
		 * The meta object literal for the '{@link tools.vitruv.models.im.impl.ProbeImpl <em>Probe</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see tools.vitruv.models.im.impl.ProbeImpl
		 * @see tools.vitruv.models.im.impl.ImPackageImpl#getProbe()
		 * @generated
		 */
		EClass PROBE = eINSTANCE.getProbe();

		/**
		 * The meta object literal for the '<em><b>Abstract Action ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROBE__ABSTRACT_ACTION_ID = eINSTANCE.getProbe_AbstractActionID();

		/**
		 * The meta object literal for the '<em><b>Is Active</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROBE__IS_ACTIVE = eINSTANCE.getProbe_IsActive();

	}

} //ImPackage
