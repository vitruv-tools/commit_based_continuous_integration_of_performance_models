package tools.vitruv.applications.javaim.modelrefinement;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Method;
import tools.vitruv.applications.javaim.modelrefinement.Java2ImMethodChangeTransformationUtil;
import tools.vitruv.domains.im.ImDomainProvider;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.echange.feature.JavaFeatureEChange;
import tools.vitruv.framework.change.description.CompositeTransactionalChange;
import tools.vitruv.framework.change.description.ConcreteChange;
import tools.vitruv.framework.change.description.TransactionalChange;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.processing.impl.AbstractChangePropagationSpecification;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.userinteraction.UserInteractor;
import tools.vitruv.framework.util.command.ResourceAccess;

@SuppressWarnings("all")
public class Java2ImMethodBodyChangeTransformation extends AbstractChangePropagationSpecification {
  public Java2ImMethodBodyChangeTransformation() {
    super(new JavaDomainProvider().getDomain(), new ImDomainProvider().getDomain());
  }
  
  @Override
  public void propagateChange(final TransactionalChange change, final CorrespondenceModel correspondenceModel, final ResourceAccess resourceAccess) {
    boolean _doesHandleChange = this.doesHandleChange(change, correspondenceModel);
    if (_doesHandleChange) {
      final CompositeTransactionalChange compositeChange = ((CompositeTransactionalChange) change);
      this.executeJava2ImTransformation(correspondenceModel, this.getUserInteractor(), compositeChange);
    }
  }
  
  @Override
  public boolean doesHandleChange(final TransactionalChange change, final CorrespondenceModel correspondenceModel) {
    if ((!(change instanceof CompositeTransactionalChange))) {
      return false;
    } else {
      return true;
    }
  }
  
  public void executeJava2ImTransformation(final CorrespondenceModel correspondenceModel, final UserInteractor userInteracting, final CompositeTransactionalChange compositeChange) {
    TransactionalChange _get = compositeChange.getChanges().get(0);
    final ConcreteChange emfChange = ((ConcreteChange) _get);
    EChange _get_1 = emfChange.getEChanges().get(0);
    final JavaFeatureEChange<?, ?> eFeatureChange = ((JavaFeatureEChange<?, ?>) _get_1);
    EObject _oldAffectedEObject = eFeatureChange.getOldAffectedEObject();
    final Method oldMethod = ((Method) _oldAffectedEObject);
    EObject _affectedEObject = eFeatureChange.getAffectedEObject();
    final Method newMethod = ((Method) _affectedEObject);
    Java2ImMethodChangeTransformationUtil.execute(correspondenceModel, oldMethod, newMethod);
  }
}
