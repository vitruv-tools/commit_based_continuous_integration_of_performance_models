/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel.util;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.*;

import de.uka.ipd.sdq.identifier.Identifier;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage
 * @generated
 */
public class InstrumentationModelSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static InstrumentationModelPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InstrumentationModelSwitch() {
		if (modelPackage == null) {
			modelPackage = InstrumentationModelPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case InstrumentationModelPackage.INSTRUMENTATION_MODEL: {
				InstrumentationModel instrumentationModel = (InstrumentationModel)theEObject;
				T result = caseInstrumentationModel(instrumentationModel);
				if (result == null) result = caseIdentifier(instrumentationModel);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT: {
				ServiceInstrumentationPoint serviceInstrumentationPoint = (ServiceInstrumentationPoint)theEObject;
				T result = caseServiceInstrumentationPoint(serviceInstrumentationPoint);
				if (result == null) result = caseInstrumentationPoint(serviceInstrumentationPoint);
				if (result == null) result = caseIdentifier(serviceInstrumentationPoint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case InstrumentationModelPackage.INSTRUMENTATION_POINT: {
				InstrumentationPoint instrumentationPoint = (InstrumentationPoint)theEObject;
				T result = caseInstrumentationPoint(instrumentationPoint);
				if (result == null) result = caseIdentifier(instrumentationPoint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT: {
				ActionInstrumentationPoint actionInstrumentationPoint = (ActionInstrumentationPoint)theEObject;
				T result = caseActionInstrumentationPoint(actionInstrumentationPoint);
				if (result == null) result = caseInstrumentationPoint(actionInstrumentationPoint);
				if (result == null) result = caseIdentifier(actionInstrumentationPoint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Instrumentation Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Instrumentation Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInstrumentationModel(InstrumentationModel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Service Instrumentation Point</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Service Instrumentation Point</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseServiceInstrumentationPoint(ServiceInstrumentationPoint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Instrumentation Point</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Instrumentation Point</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInstrumentationPoint(InstrumentationPoint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Instrumentation Point</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Instrumentation Point</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionInstrumentationPoint(ActionInstrumentationPoint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Identifier</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Identifier</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIdentifier(Identifier object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //InstrumentationModelSwitch
