package tools.vitruv.applications.pcmjava.instrumentation.instrumenter;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.members.MembersFactory;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.parameters.ParametersFactory;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.references.StringReference;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypesFactory;
import org.emftext.language.java.variables.LocalVariable;

import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;

/**
 * An abstract instrumenter.
 * 
 * @author Martin Armbruster
 */
public abstract class AbstractInstrumenter {
	protected final static String[] namespaces = {"cipm", "consistency", "bridge", "monitoring", "controller"};
	protected final static String threadMonitoringControllerName = "ThreadMonitoringController";
	protected final static String serviceParametersName = "ServiceParameters";
	protected final static ConcreteClassifier stringClassifier;
	protected final static ConcreteClassifier atomicIntegerClassifier;
	protected final static ConcreteClassifier objectClassifier;
	protected final static Method getAndIncrementMethod;
	protected final static Method getMethod;
	protected final static ConcreteClassifier threadMonitoringControllerClassifier;
	protected final static Method getInstanceMethod;
	protected final static Method enterInternalActionMethod;
	protected final static Method exitInternalActionMethod;
	protected final static Method setExternalCallIdMethod;
	protected final static Method enterBranchMethod;
	protected final static Method exitLoopMethod;
	protected final static Method enterServiceMethod;
	protected final static Method exitServiceMethod;
	protected final static ConcreteClassifier serviceParametersClassifier;
	protected final static Method addParameterValueMethod;
	protected LocalVariable threadMonitoringVariable;
	
	static {
		atomicIntegerClassifier = (ConcreteClassifier) EcoreUtil.resolve(
				JavaClasspath.get().getConcreteClassifier(AtomicInteger.class.getCanonicalName()),
				(EObject) null);
		getAndIncrementMethod = findMethod(atomicIntegerClassifier,
				ApplicationProjectInstrumenterNamespace.METHOD_ATOMIC_INTEGER_INCREMENT, 0);
		getMethod = findMethod(atomicIntegerClassifier,
				ApplicationProjectInstrumenterNamespace.METHOD_ATOMIC_INTEGER_GET, 0);
		stringClassifier = (ConcreteClassifier) EcoreUtil.resolve(
				JavaClasspath.get().getConcreteClassifier(String.class.getCanonicalName()),
				(EObject) null);
		objectClassifier = (ConcreteClassifier) EcoreUtil.resolve(
				JavaClasspath.get().getConcreteClassifier(Object.class.getCanonicalName()),
				(EObject) null);
		
		CompilationUnit cu = ContainersFactory.eINSTANCE.createCompilationUnit();
		cu.getNamespaces().addAll(Arrays.asList(namespaces));
		threadMonitoringControllerClassifier = ClassifiersFactory.eINSTANCE.createClass();
		threadMonitoringControllerClassifier.setName(threadMonitoringControllerName);
		cu.getClassifiers().add(threadMonitoringControllerClassifier);
		
		serviceParametersClassifier = ClassifiersFactory.eINSTANCE.createClass();
		serviceParametersClassifier.setName(serviceParametersName);
		cu.getClassifiers().add(serviceParametersClassifier);
		
		addParameterValueMethod = MembersFactory.eINSTANCE.createClassMethod();
		addParameterValueMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ADD_PARAMETER_VALUE);
		addParameterValueMethod.makePublic();
		addParameterValueMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
		addParameterValueMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
		addParameterValueMethod.getParameters().add(createStringParameter("param1"));
		addParameterValueMethod.getParameters().add(createOrdinaryParameter("param2",
				createClassifierReference(objectClassifier)));
		serviceParametersClassifier.getMembers().add(addParameterValueMethod);
		
		getInstanceMethod = MembersFactory.eINSTANCE.createClassMethod();
		getInstanceMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_GET_INSTANCE);
		getInstanceMethod.makePublic();
		getInstanceMethod.getModifiers().add(ModifiersFactory.eINSTANCE.createStatic());
		ClassifierReference ref = TypesFactory.eINSTANCE.createClassifierReference();
		ref.setTarget(threadMonitoringControllerClassifier);
		getInstanceMethod.setTypeReference(ref);
		getInstanceMethod.setStatement(createNullReturningBlock());
		threadMonitoringControllerClassifier.getMembers().add(getInstanceMethod);
		
		enterInternalActionMethod = MembersFactory.eINSTANCE.createClassMethod();
		enterInternalActionMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ENTER_INTERNAL_ACTION);
		enterInternalActionMethod.makePublic();
		enterInternalActionMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
		enterInternalActionMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
		enterInternalActionMethod.getParameters().add(createStringParameter("param1"));
		enterInternalActionMethod.getParameters().add(createStringParameter("param2"));
		threadMonitoringControllerClassifier.getMembers().add(enterInternalActionMethod);
		
		exitInternalActionMethod = MembersFactory.eINSTANCE.createClassMethod();
		exitInternalActionMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_EXIT_INTERNAL_ACTION);
		exitInternalActionMethod.makePublic();
		exitInternalActionMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
		exitInternalActionMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
		exitInternalActionMethod.getParameters().add(createStringParameter("param1"));
		exitInternalActionMethod.getParameters().add(createStringParameter("param2"));
		threadMonitoringControllerClassifier.getMembers().add(exitInternalActionMethod);
		
		setExternalCallIdMethod = MembersFactory.eINSTANCE.createClassMethod();
		setExternalCallIdMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_BEFORE_EXTERNAL_CALL);
		setExternalCallIdMethod.makePublic();
		setExternalCallIdMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
		setExternalCallIdMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
		setExternalCallIdMethod.getParameters().add(createStringParameter("param1"));
		threadMonitoringControllerClassifier.getMembers().add(setExternalCallIdMethod);
		
		enterBranchMethod = MembersFactory.eINSTANCE.createClassMethod();
		enterBranchMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ENTER_BRANCH);
		enterBranchMethod.makePublic();
		enterBranchMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
		enterBranchMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
		enterBranchMethod.getParameters().add(createStringParameter("param1"));
		threadMonitoringControllerClassifier.getMembers().add(enterBranchMethod);
		
		exitLoopMethod = MembersFactory.eINSTANCE.createClassMethod();
		exitLoopMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_EXIT_LOOP);
		exitLoopMethod.makePublic();
		exitLoopMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
		exitLoopMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
		exitLoopMethod.getParameters().add(createStringParameter("param1"));
		exitLoopMethod.getParameters().add(createOrdinaryParameter("param2", TypesFactory.eINSTANCE.createLong()));
		threadMonitoringControllerClassifier.getMembers().add(exitLoopMethod);
		
		enterServiceMethod = MembersFactory.eINSTANCE.createClassMethod();
		enterServiceMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ENTER_SERVICE);
		enterServiceMethod.makePublic();
		enterServiceMethod.getAnnotationsAndModifiers().add(ModifiersFactory.eINSTANCE.createSynchronized());
		enterServiceMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
		enterServiceMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
		enterServiceMethod.getParameters().add(createStringParameter("param1"));
		enterServiceMethod.getParameters().add(createOrdinaryParameter("param2",
				createClassifierReference(objectClassifier)));
		enterServiceMethod.getParameters().add(createOrdinaryParameter("param3",
				createClassifierReference(serviceParametersClassifier)));
		threadMonitoringControllerClassifier.getMembers().add(enterServiceMethod);
		
		exitServiceMethod = MembersFactory.eINSTANCE.createClassMethod();
		exitServiceMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_EXIT_SERVICE);
		exitServiceMethod.makePublic();
		exitServiceMethod.getAnnotationsAndModifiers().add(ModifiersFactory.eINSTANCE.createSynchronized());
		exitServiceMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
		exitServiceMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
		exitServiceMethod.getParameters().add(createStringParameter("param1"));
		threadMonitoringControllerClassifier.getMembers().add(exitServiceMethod);
	}
	
	private static Method findMethod(ConcreteClassifier classifier, String methName, int paramNumber) {
		return classifier.getMembers().stream()
				.filter(m -> m instanceof Method
						&& m.getName().equals(methName))
				.map(m -> (Method) m)
				.filter(m -> m.getParameters().size() == paramNumber)
				.findFirst().get();
	}
	
	private static Block createNullReturningBlock() {
		Block body = StatementsFactory.eINSTANCE.createBlock();
		Return state = StatementsFactory.eINSTANCE.createReturn();
		state.setReturnValue(LiteralsFactory.eINSTANCE.createNullLiteral());
		body.getStatements().add(state);
		return body;
	}
	
	private static OrdinaryParameter createStringParameter(String paramName) {
		return createOrdinaryParameter(paramName, createClassifierReference(stringClassifier));
	}
	
	private static OrdinaryParameter createOrdinaryParameter(String paramName, TypeReference type) {
		OrdinaryParameter param = ParametersFactory.eINSTANCE.createOrdinaryParameter();
		param.setName(paramName);
		param.setTypeReference(type);
		return param;
	}
	
	private static ClassifierReference createClassifierReference(ConcreteClassifier classifier) {
		ClassifierReference ref = TypesFactory.eINSTANCE.createClassifierReference();
		ref.setTarget(classifier);
		return ref;
	}
	
	protected void createAndAddStringArgument(MethodCall call, String s) {
		StringReference ref = ReferencesFactory.eINSTANCE.createStringReference();
		ref.setValue(s);
		call.getArguments().add(ref);
	}
	
	protected NamespaceClassifierReference createTypeReference(ConcreteClassifier classifier) {
		NamespaceClassifierReference result = TypesFactory.eINSTANCE.createNamespaceClassifierReference();
		result.getNamespaces().addAll(classifier.getContainingCompilationUnit().getNamespaces());
		ClassifierReference actualRef = TypesFactory.eINSTANCE.createClassifierReference();
		actualRef.setTarget(classifier);
		result.getClassifierReferences().add(actualRef);
		return result;
	}
}
