/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel;

import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action Instrumentation Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint#getAction <em>Action</em>}</li>
 *   <li>{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getActionInstrumentationPoint()
 * @model
 * @generated
 */
public interface ActionInstrumentationPoint extends InstrumentationPoint {
	/**
	 * Returns the value of the '<em><b>Action</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action</em>' reference.
	 * @see #setAction(AbstractAction)
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getActionInstrumentationPoint_Action()
	 * @model required="true"
	 * @generated
	 */
	AbstractAction getAction();

	/**
	 * Sets the value of the '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint#getAction <em>Action</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Action</em>' reference.
	 * @see #getAction()
	 * @generated
	 */
	void setAction(AbstractAction value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType
	 * @see #setType(InstrumentationType)
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationModelPackage#getActionInstrumentationPoint_Type()
	 * @model required="true"
	 * @generated
	 */
	InstrumentationType getType();

	/**
	 * Sets the value of the '{@link dmodel.base.models.inmodel.InstrumentationMetamodel.ActionInstrumentationPoint#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see dmodel.base.models.inmodel.InstrumentationMetamodel.InstrumentationType
	 * @see #getType()
	 * @generated
	 */
	void setType(InstrumentationType value);

} // ActionInstrumentationPoint
