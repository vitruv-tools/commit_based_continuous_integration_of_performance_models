/**
 */
package tools.vitruv.models.im.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import tools.vitruv.models.im.AppProbes;
import tools.vitruv.models.im.ImPackage;
import tools.vitruv.models.im.Probe;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>App Probes</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.models.im.impl.AppProbesImpl#getProbes <em>Probes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AppProbesImpl extends MinimalEObjectImpl.Container implements AppProbes {
	/**
	 * The cached value of the '{@link #getProbes() <em>Probes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProbes()
	 * @generated
	 * @ordered
	 */
	protected EList<Probe> probes;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AppProbesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ImPackage.Literals.APP_PROBES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Probe> getProbes() {
		if (probes == null) {
			probes = new EObjectContainmentEList<Probe>(Probe.class, this, ImPackage.APP_PROBES__PROBES);
		}
		return probes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ImPackage.APP_PROBES__PROBES:
			return ((InternalEList<?>) getProbes()).basicRemove(otherEnd, msgs);
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
		case ImPackage.APP_PROBES__PROBES:
			return getProbes();
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
		case ImPackage.APP_PROBES__PROBES:
			getProbes().clear();
			getProbes().addAll((Collection<? extends Probe>) newValue);
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
		case ImPackage.APP_PROBES__PROBES:
			getProbes().clear();
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
		case ImPackage.APP_PROBES__PROBES:
			return probes != null && !probes.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //AppProbesImpl
