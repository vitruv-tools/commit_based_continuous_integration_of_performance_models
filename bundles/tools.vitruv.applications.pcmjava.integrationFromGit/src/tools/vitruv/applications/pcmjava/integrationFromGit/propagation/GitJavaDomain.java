package tools.vitruv.applications.pcmjava.integrationFromGit.propagation;

import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.domains.java.JamoppLibraryHelper;
import tools.vitruv.domains.java.JavaDomain;
import tools.vitruv.domains.java.JavaNamespace;
//import tools.vitruv.domains.java.JavaSourceOrClassFileResourceFactoryImpl;
import org.emftext.language.java.resource.JavaSourceOrClassFileResourceFactoryImpl;
//import tools.vitruv.domains.java.VitruviusJavaBuilderApplicator;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilderApplicator;
import tools.vitruv.domains.java.tuid.JavaTuidCalculatorAndResolver;
import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain;
import tools.vitruv.framework.domains.StateBasedChangeResolutionStrategy;
import tools.vitruv.framework.domains.VitruviusProjectBuilderApplicator;

@SuppressWarnings("all")
public final class GitJavaDomain extends AbstractTuidAwareVitruvDomain {
  private static final String METAMODEL_NAME = "Java";
  
  private boolean shouldTransitivelyPropagateChanges = false;
  
  private StateBasedChangeResolutionStrategy stateBasedChangeResolutionStrategy;
  
  public GitJavaDomain() {
    super(GitJavaDomain.METAMODEL_NAME, JavaNamespace.ROOT_PACKAGE, GitJavaDomain.generateTuidCalculator(), new String[] { JavaNamespace.FILE_EXTENSION });
    Map<String, Object> _extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
    JavaSourceOrClassFileResourceFactoryImpl _javaSourceOrClassFileResourceFactoryImpl = new JavaSourceOrClassFileResourceFactoryImpl();
    _extensionToFactoryMap.put("java", _javaSourceOrClassFileResourceFactoryImpl);
    Map<String, Object> _extensionToFactoryMap_1 = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
    JavaSourceOrClassFileResourceFactoryImpl _javaSourceOrClassFileResourceFactoryImpl_1 = new JavaSourceOrClassFileResourceFactoryImpl();
    _extensionToFactoryMap_1.put("class", _javaSourceOrClassFileResourceFactoryImpl_1);
    JamoppLibraryHelper.registerStdLib();
    
    stateBasedChangeResolutionStrategy = new GitStateBasedChangeResolutionStrategy();
  }
  
  public void setStateBasedChangeResolutionStrategy(StateBasedChangeResolutionStrategy stateBasedChangeResolutionStrategy) {
	  this.stateBasedChangeResolutionStrategy = stateBasedChangeResolutionStrategy;
  }
  
  @Override
  public StateBasedChangeResolutionStrategy getStateChangePropagationStrategy() {
	    return this.stateBasedChangeResolutionStrategy;
  }
  
  protected static JavaTuidCalculatorAndResolver generateTuidCalculator() {
    return new JavaTuidCalculatorAndResolver();
  }
  
  @Override
  public VitruviusProjectBuilderApplicator getBuilderApplicator() {
    return new VitruviusJavaBuilderApplicator();
  }
  
  @Override
  public boolean shouldTransitivelyPropagateChanges() {
    return this.shouldTransitivelyPropagateChanges;
  }
  
  /**
   * Calling this methods enable the per default disabled transitive change propagation.
   * Should only be called for test purposes!
   */
  public boolean enableTransitiveChangePropagation() {
    return this.shouldTransitivelyPropagateChanges = true;
  }
  
  @Override
  public boolean supportsUuids() {
    return false;
  }
}