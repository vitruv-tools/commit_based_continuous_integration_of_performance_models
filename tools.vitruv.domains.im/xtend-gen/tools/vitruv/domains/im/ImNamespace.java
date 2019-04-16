package tools.vitruv.domains.im;

import org.eclipse.emf.ecore.EPackage;
import tools.vitruv.models.im.ImPackage;

@SuppressWarnings("all")
public class ImNamespace {
  private ImNamespace() {
  }
  
  public final static String FILE_EXTENSION = "im";
  
  public final static EPackage ROOT_PACKAGE = ImPackage.eINSTANCE;
  
  public final static String METAMODEL_NAMESPACE = ImPackage.eNS_URI;
}
