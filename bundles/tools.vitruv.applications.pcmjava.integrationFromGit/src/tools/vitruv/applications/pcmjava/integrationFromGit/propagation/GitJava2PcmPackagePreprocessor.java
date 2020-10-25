package tools.vitruv.applications.pcmjava.integrationFromGit.propagation;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.echange.feature.attribute.JavaReplaceSingleValuedEAttribute;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.framework.change.description.ConcreteChange;
import tools.vitruv.framework.change.description.TransactionalChange;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.root.InsertRootEObject;
import tools.vitruv.framework.change.processing.impl.AbstractChangePropagationSpecification;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.util.command.ResourceAccess;
import tools.vitruv.framework.util.datatypes.VURI;

@SuppressWarnings("all")
public class GitJava2PcmPackagePreprocessor extends AbstractChangePropagationSpecification {
  
	public GitJava2PcmPackagePreprocessor() {
    super(new GitJavaDomain(), new PcmDomainProvider().getDomain());
  }
  
  @Override
  public boolean doesHandleChange(final TransactionalChange change, final CorrespondenceModel correspondenceModel) {
    if (((change instanceof ConcreteChange) && (change.getEChanges().size() == 1))) {
      final EChange eChange = change.getEChanges().get(0);
      return ((eChange instanceof InsertRootEObject<?>) || (eChange instanceof JavaReplaceSingleValuedEAttribute<?, ?>));
    }
    return false;
  }
  
  private void prepareRenamePackageInfos(final JavaReplaceSingleValuedEAttribute<?, ?> updateSingleValuedEAttribute, final VURI vuri) {
    if (((updateSingleValuedEAttribute.getOldAffectedEObject() instanceof org.emftext.language.java.containers.Package) && (updateSingleValuedEAttribute.getAffectedEObject() instanceof org.emftext.language.java.containers.Package))) {
      EObject _oldAffectedEObject = updateSingleValuedEAttribute.getOldAffectedEObject();
      final org.emftext.language.java.containers.Package oldPackage = ((org.emftext.language.java.containers.Package) _oldAffectedEObject);
      EObject _affectedEObject = updateSingleValuedEAttribute.getAffectedEObject();
      final org.emftext.language.java.containers.Package newPackage = ((org.emftext.language.java.containers.Package) _affectedEObject);
      this.attachPackageToResource(oldPackage, vuri);
      String newVURIKey = vuri.toString();
      final String oldPackagePath = oldPackage.getName().replace(".", "/");
      final String newPackagePath = newPackage.getName().replace(".", "/");
      newVURIKey = newVURIKey.replace(oldPackagePath, newPackagePath);
      final VURI newVURI = VURI.getInstance(newVURIKey);
      this.attachPackageToResource(newPackage, newVURI);
    }
  }
  
  private void attachPackageToResource(final EObject eObject, final VURI vuri) {
    if ((eObject instanceof org.emftext.language.java.containers.Package)) {
      final org.emftext.language.java.containers.Package newPackage = ((org.emftext.language.java.containers.Package) eObject);
      final ResourceSet resourceSet = new ResourceSetImpl();
      final Resource resource = resourceSet.createResource(vuri.getEMFUri());
      resource.getContents().add(newPackage);
    }
  }
  
  /**
   * Special treatment for packages: we have to use the package-info file as input for the
   * transformation and make sure that the packages have resources attached
   * 
   * @param change
   *            the change that may contain the newly created package
   */
  @Override
  public void propagateChange(final TransactionalChange change, final CorrespondenceModel correspondenceModel, final ResourceAccess resourceAccess) {
    boolean _doesHandleChange = this.doesHandleChange(change, correspondenceModel);
    if (_doesHandleChange) {
      final EChange eChange = change.getEChanges().get(0);
      if ((eChange instanceof InsertRootEObject<?>)) {
        EObject _newValue = ((InsertRootEObject<?>)eChange).getNewValue();
        this.attachPackageToResource(((EObject) _newValue), change.getURI());
      } else {
        if ((eChange instanceof JavaReplaceSingleValuedEAttribute<?, ?>)) {
          this.prepareRenamePackageInfos(((JavaReplaceSingleValuedEAttribute<?, ?>)eChange), change.getURI());
        }
      }
    }
  }
}
