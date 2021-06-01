/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel.impl;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;

import de.uka.ipd.sdq.identifier.impl.IdentifierImpl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Instrumentation Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link cipm.consistency.base.models.instrumentation.InstrumentationModel.impl.InstrumentationModelImpl#getPoints <em>Points</em>}</li>
 * </ul>
 *
 * @generated
 */
public class InstrumentationModelImpl extends IdentifierImpl implements InstrumentationModel {
	/**
	 * The cached value of the '{@link #getPoints() <em>Points</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPoints()
	 * @generated
	 * @ordered
	 */
	protected EList<ServiceInstrumentationPoint> points;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InstrumentationModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return InstrumentationModelPackage.Literals.INSTRUMENTATION_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ServiceInstrumentationPoint> getPoints() {
		if (points == null) {
			points = new EObjectContainmentEList<ServiceInstrumentationPoint>(ServiceInstrumentationPoint.class, this, InstrumentationModelPackage.INSTRUMENTATION_MODEL__POINTS);
		}
		return points;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case InstrumentationModelPackage.INSTRUMENTATION_MODEL__POINTS:
				return ((InternalEList<?>)getPoints()).basicRemove(otherEnd, msgs);
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
			case InstrumentationModelPackage.INSTRUMENTATION_MODEL__POINTS:
				return getPoints();
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
			case InstrumentationModelPackage.INSTRUMENTATION_MODEL__POINTS:
				getPoints().clear();
				getPoints().addAll((Collection<? extends ServiceInstrumentationPoint>)newValue);
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
			case InstrumentationModelPackage.INSTRUMENTATION_MODEL__POINTS:
				getPoints().clear();
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
			case InstrumentationModelPackage.INSTRUMENTATION_MODEL__POINTS:
				return points != null && !points.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //InstrumentationModelImpl
