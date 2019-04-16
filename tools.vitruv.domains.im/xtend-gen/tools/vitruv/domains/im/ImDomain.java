package tools.vitruv.domains.im;

import java.util.Collections;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import tools.vitruv.domains.emf.builder.VitruviusEmfBuilderApplicator;
import tools.vitruv.domains.im.ImNamespace;
import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain;
import tools.vitruv.framework.domains.VitruviusProjectBuilderApplicator;
import tools.vitruv.framework.tuid.AttributeTuidCalculatorAndResolver;
import tools.vitruv.framework.tuid.TuidCalculatorAndResolver;
import tools.vitruv.models.im.ImPackage;

@SuppressWarnings("all")
public class ImDomain extends AbstractTuidAwareVitruvDomain {
  public final static String METAMODEL_NAME = "IM";
  
  ImDomain() {
    super(ImDomain.METAMODEL_NAME, ImNamespace.ROOT_PACKAGE, ImDomain.generateTuidCalculator(), ImNamespace.FILE_EXTENSION);
  }
  
  protected static TuidCalculatorAndResolver generateTuidCalculator() {
    String _name = ImPackage.Literals.PROBE__ABSTRACT_ACTION_ID.getName();
    return new AttributeTuidCalculatorAndResolver(ImNamespace.METAMODEL_NAMESPACE, 
      Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList(_name)));
  }
  
  @Override
  public VitruviusProjectBuilderApplicator getBuilderApplicator() {
    return new VitruviusEmfBuilderApplicator();
  }
}
