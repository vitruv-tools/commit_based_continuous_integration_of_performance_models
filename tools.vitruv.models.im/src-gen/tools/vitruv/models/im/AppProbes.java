/**
 */
package tools.vitruv.models.im;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>App Probes</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.models.im.AppProbes#getProbes <em>Probes</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.models.im.ImPackage#getAppProbes()
 * @model
 * @generated
 */
public interface AppProbes extends EObject {
	/**
	 * Returns the value of the '<em><b>Probes</b></em>' containment reference list.
	 * The list contents are of type {@link tools.vitruv.models.im.Probe}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Probes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Probes</em>' containment reference list.
	 * @see tools.vitruv.models.im.ImPackage#getAppProbes_Probes()
	 * @model containment="true"
	 * @generated
	 */
	EList<Probe> getProbes();

} // AppProbes
