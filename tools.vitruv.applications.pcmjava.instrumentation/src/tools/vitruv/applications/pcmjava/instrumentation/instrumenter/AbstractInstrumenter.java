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
	protected final static ConcreteClassifier threadMonitoringControllerClassifier;
	protected final static Method getInstanceMethod;
	protected final static Method enterInternalActionMethod;
	protected final static Method exitInternalActionMethod;
	protected final static Method setExternalCallIdMethod;
	protected final static Method enterBranchMethod;
	protected final static ConcreteClassifier serviceParametersClassifier;
	protected LocalVariable threadMonitoringVariable;
	
	static {
		atomicIntegerClassifier = (ConcreteClassifier) EcoreUtil.resolve(
				JavaClasspath.get().getConcreteClassifier(AtomicInteger.class.getCanonicalName()),
				(EObject) null);
		stringClassifier = (ConcreteClassifier) EcoreUtil.resolve(
				JavaClasspath.get().getConcreteClassifier(String.class.getCanonicalName()),
				(EObject) null);
		
		CompilationUnit cu = ContainersFactory.eINSTANCE.createCompilationUnit();
		cu.getNamespaces().addAll(Arrays.asList(namespaces));
		threadMonitoringControllerClassifier = ClassifiersFactory.eINSTANCE.createClass();
		threadMonitoringControllerClassifier.setName(threadMonitoringControllerName);
		cu.getClassifiers().add(threadMonitoringControllerClassifier);
		
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
		
		serviceParametersClassifier = ClassifiersFactory.eINSTANCE.createClass();
		serviceParametersClassifier.setName(serviceParametersName);
		cu.getClassifiers().add(serviceParametersClassifier);
	}
	
	private static Block createNullReturningBlock() {
		Block body = StatementsFactory.eINSTANCE.createBlock();
		Return state = StatementsFactory.eINSTANCE.createReturn();
		state.setReturnValue(LiteralsFactory.eINSTANCE.createNullLiteral());
		body.getStatements().add(state);
		return body;
	}
	
	private static OrdinaryParameter createStringParameter(String paramName) {
		OrdinaryParameter param = ParametersFactory.eINSTANCE.createOrdinaryParameter();
		param.setName(paramName);
		ClassifierReference ref = TypesFactory.eINSTANCE.createClassifierReference();
		ref.setTarget(stringClassifier);
		param.setTypeReference(ref);
		return param;
	}
	
	protected void createAndAddStringArgument(MethodCall call, String s) {
		StringReference ref = ReferencesFactory.eINSTANCE.createStringReference();
		ref.setValue(s);
		call.getArguments().add(ref);
	}
}
