/**
 */
package tools.vitruv.models.im;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Probe</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.models.im.Probe#getAbstractActionID <em>Abstract Action ID</em>}</li>
 *   <li>{@link tools.vitruv.models.im.Probe#isIsActive <em>Is Active</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.models.im.ImPackage#getProbe()
 * @model
 * @generated
 */
public interface Probe extends EObject {
	/**
	 * Returns the value of the '<em><b>Abstract Action ID</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Abstract Action ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Abstract Action ID</em>' attribute.
	 * @see #setAbstractActionID(String)
	 * @see tools.vitruv.models.im.ImPackage#getProbe_AbstractActionID()
	 * @model default=""
	 * @generated
	 */
	String getAbstractActionID();

	/**
	 * Sets the value of the '{@link tools.vitruv.models.im.Probe#getAbstractActionID <em>Abstract Action ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Abstract Action ID</em>' attribute.
	 * @see #getAbstractActionID()
	 * @generated
	 */
	void setAbstractActionID(String value);

	/**
	 * Returns the value of the '<em><b>Is Active</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Active</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Active</em>' attribute.
	 * @see #setIsActive(boolean)
	 * @see tools.vitruv.models.im.ImPackage#getProbe_IsActive()
	 * @model default="true"
	 * @generated
	 */
	boolean isIsActive();

	/**
	 * Sets the value of the '{@link tools.vitruv.models.im.Probe#isIsActive <em>Is Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Active</em>' attribute.
	 * @see #isIsActive()
	 * @generated
	 */
	void setIsActive(boolean value);

} // Probe
