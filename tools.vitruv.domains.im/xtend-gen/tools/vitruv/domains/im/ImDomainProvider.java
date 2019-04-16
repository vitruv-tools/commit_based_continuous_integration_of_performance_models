package tools.vitruv.domains.im;

import tools.vitruv.domains.im.ImDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;

@SuppressWarnings("all")
public class ImDomainProvider implements VitruvDomainProvider<ImDomain> {
  private static ImDomain instance;
  
  @Override
  public ImDomain getDomain() {
    if ((ImDomainProvider.instance == null)) {
      ImDomain _imDomain = new ImDomain();
      ImDomainProvider.instance = _imDomain;
    }
    return ImDomainProvider.instance;
  }
}
