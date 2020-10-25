package tools.vitruv.applications.pcmjava.integrationFromGit.propagation;

import org.eclipse.emf.ecore.EObject;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.echange.feature.JavaFeatureEChange;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.processing.impl.AbstractEChangePropagationSpecification;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.tuid.TuidManager;
import tools.vitruv.framework.util.command.ResourceAccess;

@SuppressWarnings("all")
public class GitTuidUpdatePreprocessor extends AbstractEChangePropagationSpecification {
  public GitTuidUpdatePreprocessor() {
    super(new GitJavaDomain(), new PcmDomainProvider().getDomain());
  }
  
  public boolean doesHandleChange(final EChange change, final CorrespondenceModel correspondenceModel) {
    return (change instanceof JavaFeatureEChange<?, ?>);
  }
  
  public void propagateChange(final EChange change, final CorrespondenceModel correspondenceModel, final ResourceAccess resourceAccess) {
    if ((change instanceof JavaFeatureEChange<?, ?>)) {
      EObject _oldAffectedEObject = ((JavaFeatureEChange<?, ?>)change).getOldAffectedEObject();
      final EObject oldAffectedEObject = ((EObject) _oldAffectedEObject);
      EObject _affectedEObject = ((JavaFeatureEChange<?, ?>)change).getAffectedEObject();
      final EObject newAffectedEObject = ((EObject) _affectedEObject);
      if (((null != oldAffectedEObject) && (null != newAffectedEObject))) {
        TuidManager.getInstance().updateTuid(oldAffectedEObject, newAffectedEObject);
      }
    }
  }
}
