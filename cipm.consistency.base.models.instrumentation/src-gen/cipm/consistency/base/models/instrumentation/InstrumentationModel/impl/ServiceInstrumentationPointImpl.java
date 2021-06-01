/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel.impl;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Service Instrumentation Point</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link cipm.consistency.base.models.instrumentation.InstrumentationModel.impl.ServiceInstrumentationPointImpl#getService <em>Service</em>}</li>
 *   <li>{@link cipm.consistency.base.models.instrumentation.InstrumentationModel.impl.ServiceInstrumentationPointImpl#getActionInstrumentationPoints <em>Action Instrumentation Points</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ServiceInstrumentationPointImpl extends InstrumentationPointImpl implements ServiceInstrumentationPoint {
	/**
	 * The cached value of the '{@link #getService() <em>Service</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getService()
	 * @generated
	 * @ordered
	 */
	protected ResourceDemandingSEFF service;

	/**
	 * The cached value of the '{@link #getActionInstrumentationPoints() <em>Action Instrumentation Points</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActionInstrumentationPoints()
	 * @generated
	 * @ordered
	 */
	protected EList<ActionInstrumentationPoint> actionInstrumentationPoints;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ServiceInstrumentationPointImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return InstrumentationModelPackage.Literals.SERVICE_INSTRUMENTATION_POINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceDemandingSEFF getService() {
		if (service != null && ((EObject)service).eIsProxy()) {
			InternalEObject oldService = (InternalEObject)service;
			service = (ResourceDemandingSEFF)eResolveProxy(oldService);
			if (service != oldService) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__SERVICE, oldService, service));
			}
		}
		return service;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResourceDemandingSEFF basicGetService() {
		return service;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setService(ResourceDemandingSEFF newService) {
		ResourceDemandingSEFF oldService = service;
		service = newService;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__SERVICE, oldService, service));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ActionInstrumentationPoint> getActionInstrumentationPoints() {
		if (actionInstrumentationPoints == null) {
			actionInstrumentationPoints = new EObjectContainmentEList<ActionInstrumentationPoint>(ActionInstrumentationPoint.class, this, InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS);
		}
		return actionInstrumentationPoints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS:
				return ((InternalEList<?>)getActionInstrumentationPoints()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__SERVICE:
				if (resolve) return getService();
				return basicGetService();
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS:
				return getActionInstrumentationPoints();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__SERVICE:
				setService((ResourceDemandingSEFF)newValue);
				return;
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS:
				getActionInstrumentationPoints().clear();
				getActionInstrumentationPoints().addAll((Collection<? extends ActionInstrumentationPoint>)newValue);
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
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__SERVICE:
				setService((ResourceDemandingSEFF)null);
				return;
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS:
				getActionInstrumentationPoints().clear();
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
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__SERVICE:
				return service != null;
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT__ACTION_INSTRUMENTATION_POINTS:
				return actionInstrumentationPoints != null && !actionInstrumentationPoints.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ServiceInstrumentationPointImpl
