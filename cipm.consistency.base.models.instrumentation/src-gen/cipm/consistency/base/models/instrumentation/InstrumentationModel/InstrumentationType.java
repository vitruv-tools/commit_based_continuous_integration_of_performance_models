/**
 */
package cipm.consistency.base.models.instrumentation.InstrumentationModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Instrumentation Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage#getInstrumentationType()
 * @model
 * @generated
 */
public enum InstrumentationType implements Enumerator {
	/**
	 * The '<em><b>Internal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INTERNAL_VALUE
	 * @generated
	 * @ordered
	 */
	INTERNAL(0, "Internal", "INTERNAL"),

	/**
	 * The '<em><b>Branch</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BRANCH_VALUE
	 * @generated
	 * @ordered
	 */
	BRANCH(1, "Branch", "BRANCH"),

	/**
	 * The '<em><b>Loop</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LOOP_VALUE
	 * @generated
	 * @ordered
	 */
	LOOP(2, "Loop", "LOOP"),

	/**
	 * The '<em><b>External Call</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EXTERNAL_CALL_VALUE
	 * @generated
	 * @ordered
	 */
	EXTERNAL_CALL(3, "ExternalCall", "EXTERNAL_CALL"),

	/**
	 * The '<em><b>Internal Call</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INTERNAL_CALL_VALUE
	 * @generated
	 * @ordered
	 */
	INTERNAL_CALL(4, "InternalCall", "INTERNAL_CALL");

	/**
	 * The '<em><b>Internal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INTERNAL
	 * @model name="Internal" literal="INTERNAL"
	 * @generated
	 * @ordered
	 */
	public static final int INTERNAL_VALUE = 0;

	/**
	 * The '<em><b>Branch</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BRANCH
	 * @model name="Branch" literal="BRANCH"
	 * @generated
	 * @ordered
	 */
	public static final int BRANCH_VALUE = 1;

	/**
	 * The '<em><b>Loop</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LOOP
	 * @model name="Loop" literal="LOOP"
	 * @generated
	 * @ordered
	 */
	public static final int LOOP_VALUE = 2;

	/**
	 * The '<em><b>External Call</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EXTERNAL_CALL
	 * @model name="ExternalCall" literal="EXTERNAL_CALL"
	 * @generated
	 * @ordered
	 */
	public static final int EXTERNAL_CALL_VALUE = 3;

	/**
	 * The '<em><b>Internal Call</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #INTERNAL_CALL
	 * @model name="InternalCall" literal="INTERNAL_CALL"
	 * @generated
	 * @ordered
	 */
	public static final int INTERNAL_CALL_VALUE = 4;

	/**
	 * An array of all the '<em><b>Instrumentation Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final InstrumentationType[] VALUES_ARRAY =
		new InstrumentationType[] {
			INTERNAL,
			BRANCH,
			LOOP,
			EXTERNAL_CALL,
			INTERNAL_CALL,
		};

	/**
	 * A public read-only list of all the '<em><b>Instrumentation Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<InstrumentationType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Instrumentation Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static InstrumentationType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			InstrumentationType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Instrumentation Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static InstrumentationType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			InstrumentationType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Instrumentation Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static InstrumentationType get(int value) {
		switch (value) {
			case INTERNAL_VALUE: return INTERNAL;
			case BRANCH_VALUE: return BRANCH;
			case LOOP_VALUE: return LOOP;
			case EXTERNAL_CALL_VALUE: return EXTERNAL_CALL;
			case INTERNAL_CALL_VALUE: return INTERNAL_CALL;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private InstrumentationType(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //InstrumentationType
