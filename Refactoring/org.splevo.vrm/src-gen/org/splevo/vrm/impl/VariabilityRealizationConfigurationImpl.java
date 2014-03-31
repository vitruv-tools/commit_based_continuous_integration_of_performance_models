/**
 * Copyright (c) 2014
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Benjamin Klatt
 */
package org.splevo.vrm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.splevo.vpm.variability.VariationPoint;
import org.splevo.vrm.VariabilityMechanism;
import org.splevo.vrm.VariabilityRealizationConfiguration;
import org.splevo.vrm.vrmPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Variability Realization Configuration</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.splevo.vrm.impl.VariabilityRealizationConfigurationImpl#getVariationPoint <em>
 * Variation Point</em>}</li>
 * <li>{@link org.splevo.vrm.impl.VariabilityRealizationConfigurationImpl#getMechanism <em>Mechanism
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class VariabilityRealizationConfigurationImpl extends MinimalEObjectImpl.Container implements
        VariabilityRealizationConfiguration {
    /**
     * The cached value of the '{@link #getVariationPoint() <em>Variation Point</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getVariationPoint()
     * @generated
     * @ordered
     */
    protected VariationPoint variationPoint;

    /**
     * The cached value of the '{@link #getMechanism() <em>Mechanism</em>}' reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMechanism()
     * @generated
     * @ordered
     */
    protected VariabilityMechanism mechanism;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected VariabilityRealizationConfigurationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return vrmPackage.Literals.VARIABILITY_REALIZATION_CONFIGURATION;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public VariationPoint getVariationPoint() {
        if (variationPoint != null && variationPoint.eIsProxy()) {
            InternalEObject oldVariationPoint = (InternalEObject) variationPoint;
            variationPoint = (VariationPoint) eResolveProxy(oldVariationPoint);
            if (variationPoint != oldVariationPoint) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__VARIATION_POINT, oldVariationPoint,
                            variationPoint));
            }
        }
        return variationPoint;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public VariationPoint basicGetVariationPoint() {
        return variationPoint;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setVariationPoint(VariationPoint newVariationPoint) {
        VariationPoint oldVariationPoint = variationPoint;
        variationPoint = newVariationPoint;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__VARIATION_POINT, oldVariationPoint,
                    variationPoint));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public VariabilityMechanism getMechanism() {
        if (mechanism != null && mechanism.eIsProxy()) {
            InternalEObject oldMechanism = (InternalEObject) mechanism;
            mechanism = (VariabilityMechanism) eResolveProxy(oldMechanism);
            if (mechanism != oldMechanism) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__MECHANISM, oldMechanism, mechanism));
            }
        }
        return mechanism;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public VariabilityMechanism basicGetMechanism() {
        return mechanism;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMechanism(VariabilityMechanism newMechanism) {
        VariabilityMechanism oldMechanism = mechanism;
        mechanism = newMechanism;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__MECHANISM, oldMechanism, mechanism));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__VARIATION_POINT:
            if (resolve)
                return getVariationPoint();
            return basicGetVariationPoint();
        case vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__MECHANISM:
            if (resolve)
                return getMechanism();
            return basicGetMechanism();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__VARIATION_POINT:
            setVariationPoint((VariationPoint) newValue);
            return;
        case vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__MECHANISM:
            setMechanism((VariabilityMechanism) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__VARIATION_POINT:
            setVariationPoint((VariationPoint) null);
            return;
        case vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__MECHANISM:
            setMechanism((VariabilityMechanism) null);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__VARIATION_POINT:
            return variationPoint != null;
        case vrmPackage.VARIABILITY_REALIZATION_CONFIGURATION__MECHANISM:
            return mechanism != null;
        }
        return super.eIsSet(featureID);
    }

} // VariabilityRealizationConfigurationImpl
