/**
 */
package org.splevo.diffing.emfcompare.java2kdmdiff.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.compare.diff.metamodel.DifferenceKind;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.gmt.modisco.java.ImportDeclaration;
import org.splevo.diffing.emfcompare.java2kdmdiff.ImportDelete;
import org.splevo.diffing.emfcompare.java2kdmdiff.Java2KDMDiffPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Import Delete</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.splevo.diffing.emfcompare.java2kdmdiff.impl.ImportDeleteImpl#getImportRight <em>Import Right</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ImportDeleteImpl extends ImportDeclarationChangeImpl implements ImportDelete {
	/**
	 * The cached value of the '{@link #getImportRight() <em>Import Right</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImportRight()
	 * @generated
	 * @ordered
	 */
	protected ImportDeclaration importRight;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ImportDeleteImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Java2KDMDiffPackage.Literals.IMPORT_DELETE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImportDeclaration getImportRight() {
		if (importRight != null && importRight.eIsProxy()) {
			InternalEObject oldImportRight = (InternalEObject)importRight;
			importRight = (ImportDeclaration)eResolveProxy(oldImportRight);
			if (importRight != oldImportRight) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, Java2KDMDiffPackage.IMPORT_DELETE__IMPORT_RIGHT, oldImportRight, importRight));
			}
		}
		return importRight;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImportDeclaration basicGetImportRight() {
		return importRight;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImportRight(ImportDeclaration newImportRight) {
		ImportDeclaration oldImportRight = importRight;
		importRight = newImportRight;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Java2KDMDiffPackage.IMPORT_DELETE__IMPORT_RIGHT, oldImportRight, importRight));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case Java2KDMDiffPackage.IMPORT_DELETE__IMPORT_RIGHT:
				if (resolve) return getImportRight();
				return basicGetImportRight();
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
			case Java2KDMDiffPackage.IMPORT_DELETE__IMPORT_RIGHT:
				setImportRight((ImportDeclaration)newValue);
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
			case Java2KDMDiffPackage.IMPORT_DELETE__IMPORT_RIGHT:
				setImportRight((ImportDeclaration)null);
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
			case Java2KDMDiffPackage.IMPORT_DELETE__IMPORT_RIGHT:
				return importRight != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * The difference kind of an import delete is always DifferenceKind.DELETION
	 * <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public DifferenceKind getKind() {
		return DifferenceKind.DELETION;
	}

} //ImportDeleteImpl
