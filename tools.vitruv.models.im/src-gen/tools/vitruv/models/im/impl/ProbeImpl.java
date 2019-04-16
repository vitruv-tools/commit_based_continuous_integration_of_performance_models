/**
 */
package tools.vitruv.models.im.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import tools.vitruv.models.im.ImPackage;
import tools.vitruv.models.im.Probe;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Probe</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.models.im.impl.ProbeImpl#getAbstractActionID <em>Abstract Action ID</em>}</li>
 *   <li>{@link tools.vitruv.models.im.impl.ProbeImpl#isIsActive <em>Is Active</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProbeImpl extends MinimalEObjectImpl.Container implements Probe {
	/**
	 * The default value of the '{@link #getAbstractActionID() <em>Abstract Action ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAbstractActionID()
	 * @generated
	 * @ordered
	 */
	protected static final String ABSTRACT_ACTION_ID_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getAbstractActionID() <em>Abstract Action ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAbstractActionID()
	 * @generated
	 * @ordered
	 */
	protected String abstractActionID = ABSTRACT_ACTION_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #isIsActive() <em>Is Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsActive()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_ACTIVE_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isIsActive() <em>Is Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsActive()
	 * @generated
	 * @ordered
	 */
	protected boolean isActive = IS_ACTIVE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProbeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ImPackage.Literals.PROBE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAbstractActionID() {
		return abstractActionID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAbstractActionID(String newAbstractActionID) {
		String oldAbstractActionID = abstractActionID;
		abstractActionID = newAbstractActionID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ImPackage.PROBE__ABSTRACT_ACTION_ID,
					oldAbstractActionID, abstractActionID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsActive() {
		return isActive;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsActive(boolean newIsActive) {
		boolean oldIsActive = isActive;
		isActive = newIsActive;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ImPackage.PROBE__IS_ACTIVE, oldIsActive, isActive));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ImPackage.PROBE__ABSTRACT_ACTION_ID:
			return getAbstractActionID();
		case ImPackage.PROBE__IS_ACTIVE:
			return isIsActive();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ImPackage.PROBE__ABSTRACT_ACTION_ID:
			setAbstractActionID((String) newValue);
			return;
		case ImPackage.PROBE__IS_ACTIVE:
			setIsActive((Boolean) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ImPackage.PROBE__ABSTRACT_ACTION_ID:
			setAbstractActionID(ABSTRACT_ACTION_ID_EDEFAULT);
			return;
		case ImPackage.PROBE__IS_ACTIVE:
			setIsActive(IS_ACTIVE_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ImPackage.PROBE__ABSTRACT_ACTION_ID:
			return ABSTRACT_ACTION_ID_EDEFAULT == null ? abstractActionID != null
					: !ABSTRACT_ACTION_ID_EDEFAULT.equals(abstractActionID);
		case ImPackage.PROBE__IS_ACTIVE:
			return isActive != IS_ACTIVE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (abstractActionID: ");
		result.append(abstractActionID);
		result.append(", isActive: ");
		result.append(isActive);
		result.append(')');
		return result.toString();
	}

} //ProbeImpl
