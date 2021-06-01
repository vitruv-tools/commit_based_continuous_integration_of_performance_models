/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel.impl;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class InstrumentationModelFactoryImpl extends EFactoryImpl implements InstrumentationModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static InstrumentationModelFactory init() {
		try {
			InstrumentationModelFactory theInstrumentationModelFactory = (InstrumentationModelFactory)EPackage.Registry.INSTANCE.getEFactory(InstrumentationModelPackage.eNS_URI);
			if (theInstrumentationModelFactory != null) {
				return theInstrumentationModelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new InstrumentationModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InstrumentationModelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case InstrumentationModelPackage.INSTRUMENTATION_MODEL: return createInstrumentationModel();
			case InstrumentationModelPackage.SERVICE_INSTRUMENTATION_POINT: return createServiceInstrumentationPoint();
			case InstrumentationModelPackage.INSTRUMENTATION_POINT: return createInstrumentationPoint();
			case InstrumentationModelPackage.ACTION_INSTRUMENTATION_POINT: return createActionInstrumentationPoint();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case InstrumentationModelPackage.INSTRUMENTATION_TYPE:
				return createInstrumentationTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case InstrumentationModelPackage.INSTRUMENTATION_TYPE:
				return convertInstrumentationTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InstrumentationModel createInstrumentationModel() {
		InstrumentationModelImpl instrumentationModel = new InstrumentationModelImpl();
		return instrumentationModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServiceInstrumentationPoint createServiceInstrumentationPoint() {
		ServiceInstrumentationPointImpl serviceInstrumentationPoint = new ServiceInstrumentationPointImpl();
		return serviceInstrumentationPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InstrumentationPoint createInstrumentationPoint() {
		InstrumentationPointImpl instrumentationPoint = new InstrumentationPointImpl();
		return instrumentationPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionInstrumentationPoint createActionInstrumentationPoint() {
		ActionInstrumentationPointImpl actionInstrumentationPoint = new ActionInstrumentationPointImpl();
		return actionInstrumentationPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InstrumentationType createInstrumentationTypeFromString(EDataType eDataType, String initialValue) {
		InstrumentationType result = InstrumentationType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertInstrumentationTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InstrumentationModelPackage getInstrumentationModelPackage() {
		return (InstrumentationModelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static InstrumentationModelPackage getPackage() {
		return InstrumentationModelPackage.eINSTANCE;
	}

} //InstrumentationModelFactoryImpl
