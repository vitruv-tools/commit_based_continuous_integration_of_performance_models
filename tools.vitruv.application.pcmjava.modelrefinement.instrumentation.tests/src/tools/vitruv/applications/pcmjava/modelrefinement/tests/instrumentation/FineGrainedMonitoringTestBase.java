package tools.vitruv.applications.pcmjava.modelrefinement.tests.instrumentation;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.text.edits.InsertEdit;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import tools.vitruv.applications.javaim.modelrefinement.InstrumentationModel;
import tools.vitruv.applications.javaim.modelrefinement.InstrumentationModelImp;
import tools.vitruv.applications.javaim.modelrefinement.Java2ImMethodChangeTransformationSpecification;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.SourceCodeInstrumentation;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.SourceCodeInstrumentationImp;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.probesprovider.ProbesProvider;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.probesprovider.ProbesProviderImp;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmWithSeffstatmantsChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.applications.pcmjava.tests.util.Java2PcmTransformationTest;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.vitruv.models.im.AppProbes;
import tools.vitruv.models.im.Probe;

public class FineGrainedMonitoringTestBase extends Java2PcmTransformationTest{
	
	// interfaces
	protected static final String SearchingAlgo = "SearchingAlgo";
    protected static final String GuiSearchingAlgo = "GuiSearchingAlgo";
    
    // backend services
    protected static final String SequentialSearchMethod = "sequentialSearch";
    protected static final String BinarySearchMethod = "binarySearch";
    
    // GUI services
    protected static final String GuiSequentialSearchMethod = "guiSequentialSearch";
    protected static final String GuiBinarySearchMethod = "guiBinarySearch";
    
    // PCM repository
    protected Repository repository;
    
    // signatures and roles
    protected OperationSignature sequantialSearch;
    protected OperationSignature binarySearch;
    
    protected OperationSignature guiSquantialSearch;
    protected OperationSignature guiBinarySeach;
       
    protected OperationRequiredRole guiSearchAlogRequiredRole;
    
    protected String guiPackageName;
    protected static final String SearchingAloClassName = SearchingAlgo + "Impl";
    protected static final String GuiSearchingAlgoClassName = GuiSearchingAlgo + "Impl";
    
    

	@Override
	protected Iterable<ChangePropagationSpecification> createChangePropagationSpecifications() {
		List<ChangePropagationSpecification> changePropagationSpecififactions = 
    			new ArrayList<ChangePropagationSpecification>();
    	
    	changePropagationSpecififactions.add(new Java2PcmWithSeffstatmantsChangePropagationSpecification());
    	changePropagationSpecififactions.add(new Java2ImMethodChangeTransformationSpecification());
    	
    	return changePropagationSpecififactions;
	}
	
	
	 @Override
	public void beforeTest() {
		super.beforeTest();
	    try {
	    	this.repository = this.createSearchingAlgoViaCode();     	
	    } catch (Throwable t) {
	    	fail("Exception during model creationg");
	    }
	    this.guiPackageName = GuiSearchingAlgo;
	}
	
	 
	 public ResourceDemandingSEFF editSequentialSearchMethod(final String code) throws Throwable, JavaModelException {
	        return this.editMethod(code, SearchingAloClassName, SequentialSearchMethod, true); 
	 }
	 
	 public ResourceDemandingSEFF editBinarySearchMethod(final String code) throws Throwable, JavaModelException {
	        return this.editMethod(code, SearchingAloClassName, BinarySearchMethod, true); 
	 }
	 
	 public ResourceDemandingSEFF editGuiSequentialSearchMethod(final String code) throws Throwable, JavaModelException {
	        return this.editMethod(code, GuiSearchingAlgoClassName, GuiSequentialSearchMethod, true); 
	 }
	 
	 public ResourceDemandingSEFF editGuiBinarySearchMethod(final String code) throws Throwable, JavaModelException {
	        return this.editMethod(code, GuiSearchingAlgoClassName, GuiBinarySearchMethod, true); 
	 }
	 
	 public void addInternalMethodToGUI(final String methodName, final String methodHeader, final String methodContent) throws Throwable {
	        CompilationUnitManipulatorHelper.addMethodToCompilationUnit(GuiSearchingAlgoClassName, methodHeader,
	                this.getCurrentTestProject(), this);
	        this.editMethod(methodContent, GuiSearchingAlgoClassName, methodName, false);
	 }

	 
	 private ResourceDemandingSEFF editMethod(final String code, final String compilationUnitName,
	            final String methodName, final boolean shouldHaveCorrespndingSEFFAfterEdit)
	                    throws Throwable, JavaModelException {
	        final ICompilationUnit iCu = CompilationUnitManipulatorHelper
	                .findICompilationUnitWithClassName(compilationUnitName, this.getCurrentTestProject());
	        final IMethod iMethod = super.findIMethodByName(compilationUnitName, methodName, iCu);
	        int offset = iMethod.getSourceRange().getOffset();
	        offset += iMethod.getSource().length() - 2;
	        final InsertEdit insertEdit = new InsertEdit(offset, code);
	        
	        CompilationUnitManipulatorHelper.editCompilationUnit(iCu, this, insertEdit);
	        
	        final CorrespondenceModel ci = this.getCorrespondenceModel();
	        
	        
	        final Method method = super.findJaMoPPMethodInICU(iCu, methodName);
	
	        
	        final Set<ResourceDemandingSEFF> seffs = CorrespondenceModelUtil.getCorrespondingEObjectsByType(ci, method,
	                ResourceDemandingSEFF.class);
	        
	        if (null == seffs || 0 == seffs.size()) {
	            if (shouldHaveCorrespndingSEFFAfterEdit) {
	                fail("could not find corresponding seff for method " + method);
	    } else {
	        return null;
	    }
	}
	if (!shouldHaveCorrespndingSEFFAfterEdit) {
	    fail("method has a corresponding seff but it should not have one: Method: " + method + " SEFF: "
	                + seffs.iterator().next());
	    }
	    
	    return seffs.iterator().next();
	
	}
	 
	 
	protected Repository createSearchingAlgoViaCode() throws Throwable {
        // create main package
        final Repository repo = super.addRepoContractsAndDatatypesPackage();

        // create packages
        this.addPackageAndImplementingClass(SearchingAlgo);
        this.addPackageAndImplementingClass(GuiSearchingAlgo);

        final String searchingAlgoInterfaceName = "I" + SearchingAlgo;
        final String guiInterfaceName = "I" + GuiSearchingAlgo;

        // create interfaces
        super.createInterfaceInPackageBasedOnJaMoPPPackageWithCorrespondence("contracts", searchingAlgoInterfaceName);
        super.createInterfaceInPackageBasedOnJaMoPPPackageWithCorrespondence("contracts", guiInterfaceName);


        // create interface methods
        this.sequantialSearch = super.addMethodToInterfaceWithCorrespondence(searchingAlgoInterfaceName, SequentialSearchMethod,
        		"int[] arr, int value", "boolean");
        this.binarySearch = super.addMethodToInterfaceWithCorrespondence(searchingAlgoInterfaceName, BinarySearchMethod,
        		"int[] arr, int value", "boolean");

        this.guiSquantialSearch = super.addMethodToInterfaceWithCorrespondence(guiInterfaceName, GuiSequentialSearchMethod,
        		"int[] arr, int[] values", "String");
        this.guiBinarySeach = super.addMethodToInterfaceWithCorrespondence(guiInterfaceName, GuiBinarySearchMethod,
        		"int[] arr, int[] values", "String");

        // create implements
        super.addImplementsCorrespondingToOperationProvidedRoleToClass(SearchingAloClassName, searchingAlgoInterfaceName);
        super.addImplementsCorrespondingToOperationProvidedRoleToClass(GuiSearchingAlgoClassName, guiInterfaceName);

        // create class methods in component implementing classes in order to create SEFF
        // correspondences
        this.addClassMethodToClassThatOverridesInterfaceMethod(SearchingAloClassName, SequentialSearchMethod,
        		"int[] arr, int value", "boolean");
        this.addClassMethodToClassThatOverridesInterfaceMethod(SearchingAloClassName, BinarySearchMethod,
        		"int[] arr, int value", "boolean");
        
        this.addClassMethodToClassThatOverridesInterfaceMethod(GuiSearchingAlgoClassName, GuiSequentialSearchMethod,
        		"int[] arr, int[] values", "String");
        this.addClassMethodToClassThatOverridesInterfaceMethod(GuiSearchingAlgoClassName, GuiBinarySearchMethod,
        		"int[] arr, int[] values", "String");

        // create requiredRole from webgui to IMediaStore
        this.guiSearchAlogRequiredRole = this.addFieldToClassWithName(GuiSearchingAlgoClassName, searchingAlgoInterfaceName,
                "i" + SearchingAlgo, OperationRequiredRole.class);
        return repo;
    }

    protected void setGUIPackageName(final String gUIPackageName) {
        this.guiPackageName = gUIPackageName;
    }
	
    
    public static String getExternalCallSequentialSearch() {
    	return "i" + SearchingAlgo + "." + SequentialSearchMethod;
    }
    
    public static String getExternalCallBinarySearch() {
    	return "i" + SearchingAlgo + "." + BinarySearchMethod;
    }
    
    public void instrumentSourceCode(String iterationNumber) throws CoreException {
    	try {
            
           	IWorkspace workspace = ResourcesPlugin.getWorkspace();  	
            IJavaProject project = (IJavaProject)JavaCore.create((IProject)workspace.getRoot().getProject(getCurrentTestProjectFolder().getName()));
            
            IFile file = project.getProject().getFile("model/instrumentationmodel.im");       
            URI modelUri = URI.createFileURI(file.getLocation().toString());          
        	InstrumentationModel im = new InstrumentationModelImp(modelUri);
        	CorrespondenceModel ci = getCorrespondenceModel();
            ProbesProvider probesProvider = new ProbesProviderImp(ci, im);

            
            SourceCodeInstrumentation test =  new SourceCodeInstrumentationImp(ci, project, probesProvider);
            
            System.out.println(">>> Starting instrumentation process.............");
            test.execute(iterationNumber);
           
            	
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    public void deleteProbesFromInstrumentationModel() {
    	IWorkspace workspace = ResourcesPlugin.getWorkspace();  	
        IJavaProject project = (IJavaProject)JavaCore.create((IProject)workspace.getRoot().getProject(
        		getCurrentTestProjectFolder().getName()));
        
    	IFile file = project.getProject().getFile("model/instrumentationmodel.im");       
        URI modelUri = URI.createFileURI(file.getLocation().toString());          
    	InstrumentationModel im = new InstrumentationModelImp(modelUri);
    	
    	AppProbes appProbes = im.getInstrumentationModelRoot();
    	List<Probe> listProbes = new ArrayList<Probe>();
    	
		for(Probe probe: appProbes.getProbes()) {
			listProbes.add(probe);		
		}
		
		for(Probe probe: listProbes) {
			Resource eResource = probe.eResource();
			EcoreUtil.delete(probe);
			try {
				eResource.save(Collections.EMPTY_MAP);
			} 
			catch (IOException e) {
			}
		}
    	
    }
	

}
