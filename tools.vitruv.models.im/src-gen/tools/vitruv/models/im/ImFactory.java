/**
 */
package tools.vitruv.models.im;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.models.im.ImPackage
 * @generated
 */
public interface ImFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ImFactory eINSTANCE = tools.vitruv.models.im.impl.ImFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>App Probes</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>App Probes</em>'.
	 * @generated
	 */
	AppProbes createAppProbes();

	/**
	 * Returns a new object of class '<em>Probe</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Probe</em>'.
	 * @generated
	 */
	Probe createProbe();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ImPackage getImPackage();

} //ImFactory
