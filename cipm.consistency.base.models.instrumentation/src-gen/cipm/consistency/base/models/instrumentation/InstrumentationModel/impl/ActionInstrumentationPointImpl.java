/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel.impl;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationType;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Action Instrumentation Point</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link cipm.consistency.base.models.instrumentation.InstrumentationModel.impl.ActionInstrumentationPointImpl#getAction <em>Action</em>}</li>
 *   <li>{@link cipm.consistency.base.models.instrumentation.InstrumentationModel.impl.ActionInstrumentationPointImpl#getType <em>Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActionInstrumentationPointImpl extends InstrumentationPointImpl implements ActionInstrumentationPoint {
	/**
	 * The cached value of the '{@link #getAction() <em>Action</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAction()
	 * @generated
	 * @ordered
	 */
	protected AbstractAction action;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final InstrumentationType TYPE_EDEFAULT = InstrumentationType.INTERNAL;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected InstrumentationType type = TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ActionInstrumentationPointImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return InstrumentationModelPackage.Literals.ACTION_INSTRUMENTATION_POINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AbstractAction getAction() {
		if (action != null && ((EObject)action).eIsProxy()) {
			InternalEObject oldAction = (InternalEObject)action;
			action = (AbstractAction)eResolveProxy(oldAction);
			if (action != oldAction) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__ACTION, oldAction, action));
			}
		}
		return action;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractAction basicGetAction() {
		return action;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAction(AbstractAction newAction) {
		AbstractAction oldAction = action;
		action = newAction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__ACTION, oldAction, action));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InstrumentationType getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setType(InstrumentationType newType) {
		InstrumentationType oldType = type;
		type = newType == null ? TYPE_EDEFAULT : newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__ACTION:
				if (resolve) return getAction();
				return basicGetAction();
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__TYPE:
				return getType();
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
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__ACTION:
				setAction((AbstractAction)newValue);
				return;
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__TYPE:
				setType((InstrumentationType)newValue);
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
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__ACTION:
				setAction((AbstractAction)null);
				return;
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__TYPE:
				setType(TYPE_EDEFAULT);
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
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__ACTION:
				return action != null;
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT__TYPE:
				return type != TYPE_EDEFAULT;
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
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (type: ");
		result.append(type);
		result.append(')');
		return result.toString();
	}

} //ActionInstrumentationPointImpl
