package tools.vitruv.applications.pcmjava.seffstatements.code2seff;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementsPackage;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.ClassMethodBodyChangedTransformation;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Code2SeffFactory;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.echange.feature.JavaFeatureEChange;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.framework.change.description.CompositeTransactionalChange;
import tools.vitruv.framework.change.description.ConcreteChange;
import tools.vitruv.framework.change.description.TransactionalChange;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;
import tools.vitruv.framework.change.echange.feature.reference.RemoveEReference;
import tools.vitruv.framework.change.echange.feature.reference.UpdateReferenceEChange;
import tools.vitruv.framework.change.processing.impl.AbstractChangePropagationSpecification;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.userinteraction.UserInteractor;
import tools.vitruv.framework.util.command.ResourceAccess;

@SuppressWarnings("all")
public class Java2PcmMethodBodyChangePreprocessor extends AbstractChangePropagationSpecification {
  private final Code2SeffFactory code2SeffFactory;
  
  public Java2PcmMethodBodyChangePreprocessor(final Code2SeffFactory code2SEFFfactory) {
    super(new JavaDomainProvider().getDomain(), new PcmDomainProvider().getDomain());
    this.code2SeffFactory = code2SEFFfactory;
  }
  
  @Override
  public void propagateChange(final TransactionalChange change, final CorrespondenceModel correspondenceModel, final ResourceAccess resourceAccess) {
    boolean _doesHandleChange = this.doesHandleChange(change, correspondenceModel);
    if (_doesHandleChange) {
      final CompositeTransactionalChange compositeChange = ((CompositeTransactionalChange) change);
      this.executeClassMethodBodyChangeRefiner(correspondenceModel, this.getUserInteractor(), compositeChange);
    }
  }
  
  @Override
  public boolean doesHandleChange(final TransactionalChange change, final CorrespondenceModel correspondenceModel) {
    if ((!(change instanceof CompositeTransactionalChange))) {
      return false;
    }
    final ArrayList<JavaFeatureEChange<?, ?>> eChanges = new ArrayList<JavaFeatureEChange<?, ?>>();
    List<EChange> _eChanges = change.getEChanges();
    for (final EChange eChange : _eChanges) {
      if ((eChange instanceof UpdateReferenceEChange<?>)) {
        boolean _isContainment = ((UpdateReferenceEChange<?>)eChange).isContainment();
        if (_isContainment) {
          if ((eChange instanceof JavaFeatureEChange<?, ?>)) {
            final JavaFeatureEChange<?, ?> typedChange = ((JavaFeatureEChange<?, ?>) eChange);
            eChanges.add(typedChange);
          }
        }
      }
    }
    int _size = eChanges.size();
    int _size_1 = change.getEChanges().size();
    boolean _notEquals = (_size != _size_1);
    if (_notEquals) {
      return false;
    }
    final JavaFeatureEChange<?, ?> firstChange = eChanges.get(0);
    if (((!(firstChange.getOldAffectedEObject() instanceof Method)) || (!(firstChange.getAffectedEObject() instanceof Method)))) {
      return false;
    }
    final Function1<JavaFeatureEChange<?, ?>, Boolean> _function = (JavaFeatureEChange<?, ?> it) -> {
      EStructuralFeature _affectedFeature = it.getAffectedFeature();
      EReference _statementListContainer_Statements = StatementsPackage.eINSTANCE.getStatementListContainer_Statements();
      return Boolean.valueOf(Objects.equal(_affectedFeature, _statementListContainer_Statements));
    };
    boolean _forall = IterableExtensions.<JavaFeatureEChange<?, ?>>forall(eChanges, _function);
    boolean _not = (!_forall);
    if (_not) {
      return false;
    }
    final Function1<JavaFeatureEChange<?, ?>, Boolean> _function_1 = (JavaFeatureEChange<?, ?> it) -> {
      EObject _affectedEObject = it.getAffectedEObject();
      EObject _affectedEObject_1 = firstChange.getAffectedEObject();
      return Boolean.valueOf(Objects.equal(_affectedEObject, _affectedEObject_1));
    };
    boolean _forall_1 = IterableExtensions.<JavaFeatureEChange<?, ?>>forall(eChanges, _function_1);
    boolean _not_1 = (!_forall_1);
    if (_not_1) {
      return false;
    }
    final Function1<JavaFeatureEChange<?, ?>, Boolean> _function_2 = (JavaFeatureEChange<?, ?> it) -> {
      EObject _oldAffectedEObject = it.getOldAffectedEObject();
      EObject _oldAffectedEObject_1 = firstChange.getOldAffectedEObject();
      return Boolean.valueOf(Objects.equal(_oldAffectedEObject, _oldAffectedEObject_1));
    };
    boolean _forall_2 = IterableExtensions.<JavaFeatureEChange<?, ?>>forall(eChanges, _function_2);
    boolean _not_2 = (!_forall_2);
    if (_not_2) {
      return false;
    }
    final ArrayList<RemoveEReference<?, ?>> deleteChanges = new ArrayList<RemoveEReference<?, ?>>();
    final Consumer<JavaFeatureEChange<?, ?>> _function_3 = (JavaFeatureEChange<?, ?> it) -> {
      if ((it instanceof RemoveEReference<?, ?>)) {
        final RemoveEReference<?, ?> typedChange_1 = ((RemoveEReference<?, ?>) it);
        deleteChanges.add(typedChange_1);
      }
    };
    eChanges.forEach(_function_3);
    final ArrayList<InsertEReference<?, ?>> addChanges = new ArrayList<InsertEReference<?, ?>>();
    final Consumer<JavaFeatureEChange<?, ?>> _function_4 = (JavaFeatureEChange<?, ?> it) -> {
      if ((it instanceof InsertEReference<?, ?>)) {
        final InsertEReference<?, ?> typedChange_1 = ((InsertEReference<?, ?>) it);
        addChanges.add(typedChange_1);
      }
    };
    eChanges.forEach(_function_4);
    final Function1<RemoveEReference<?, ?>, Boolean> _function_5 = (RemoveEReference<?, ?> it) -> {
      EObject _oldValue = it.getOldValue();
      return Boolean.valueOf((_oldValue instanceof Statement));
    };
    boolean _forall_3 = IterableExtensions.<RemoveEReference<?, ?>>forall(deleteChanges, _function_5);
    boolean _not_3 = (!_forall_3);
    if (_not_3) {
      return false;
    }
    final Function1<InsertEReference<?, ?>, Boolean> _function_6 = (InsertEReference<?, ?> it) -> {
      EObject _newValue = it.getNewValue();
      return Boolean.valueOf((_newValue instanceof Statement));
    };
    boolean _forall_4 = IterableExtensions.<InsertEReference<?, ?>>forall(addChanges, _function_6);
    boolean _not_4 = (!_forall_4);
    if (_not_4) {
      return false;
    }
    return true;
  }
  
  private void executeClassMethodBodyChangeRefiner(final CorrespondenceModel correspondenceModel, final UserInteractor userInteracting, final CompositeTransactionalChange compositeChange) {
    TransactionalChange _get = compositeChange.getChanges().get(0);
    final ConcreteChange emfChange = ((ConcreteChange) _get);
    EChange _get_1 = emfChange.getEChanges().get(0);
    final JavaFeatureEChange<?, ?> eFeatureChange = ((JavaFeatureEChange<?, ?>) _get_1);
    EObject _oldAffectedEObject = eFeatureChange.getOldAffectedEObject();
    final Method oldMethod = ((Method) _oldAffectedEObject);
    EObject _affectedEObject = eFeatureChange.getAffectedEObject();
    final Method newMethod = ((Method) _affectedEObject);
    final BasicComponentFinding basicComponentFinding = this.code2SeffFactory.createBasicComponentFinding();
    final BasicComponent myBasicComponent = basicComponentFinding.findBasicComponentForMethod(newMethod, correspondenceModel);
    final AbstractFunctionClassificationStrategy classification = this.code2SeffFactory.createAbstractFunctionClassificationStrategy(basicComponentFinding, correspondenceModel, myBasicComponent);
    final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory = this.code2SeffFactory.createInterfaceOfExternalCallFindingFactory(correspondenceModel, myBasicComponent);
    final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding = this.code2SeffFactory.createResourceDemandingBehaviourForClassMethodFinding(correspondenceModel);
    final ClassMethodBodyChangedTransformation methodBodyChanged = new ClassMethodBodyChangedTransformation(oldMethod, newMethod, basicComponentFinding, classification, interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding);
    methodBodyChanged.execute(correspondenceModel, userInteracting);
  }
}
