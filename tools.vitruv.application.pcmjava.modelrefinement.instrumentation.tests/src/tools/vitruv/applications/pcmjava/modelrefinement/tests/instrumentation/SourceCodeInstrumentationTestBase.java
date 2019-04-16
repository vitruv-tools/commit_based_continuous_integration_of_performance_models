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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.text.edits.InsertEdit;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
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
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.jamoppplaincodeparser.JamoppPlainCodeParser;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.probesprovider.ProbesProvider;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.probesprovider.ProbesProviderImp;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.projectmanager.ProjectManager;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmWithSeffstatmantsChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.applications.pcmjava.tests.util.Java2PcmTransformationTest;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;

public class SourceCodeInstrumentationTestBase extends Java2PcmTransformationTest{

	protected static final String MEDIA_STORE = "MediaStore";
    protected static final String WEBGUI = "WebGUI";
    protected static final String UPLOAD = "upload";
    protected static final String DOWNLOAD = "download";
 
    protected Repository repository;
    protected OperationSignature httpDownloadOpSig;
    protected OperationSignature httpUploadOpSig;
    protected OperationSignature uploadOpSig;
    protected OperationSignature downloadOpSig;
    protected OperationRequiredRole webGUIRequiresIMediaStoreRole;
    protected String webGUIPackageName;
    protected static final String MEDIA_STORE_CLASSNAME = MEDIA_STORE + "Impl";
    protected static final String WEBGUI_CLASSNAME = WEBGUI + "Impl";
    
	@Override
    protected List<ChangePropagationSpecification> createChangePropagationSpecifications() {
    	List<ChangePropagationSpecification> changePropagationSpecififactions = 
    			new ArrayList<ChangePropagationSpecification>();
    	
    	changePropagationSpecififactions.add(new Java2PcmWithSeffstatmantsChangePropagationSpecification());
    	//changePropagationSpecififactions.add(new Java2ImMethodChangeTransformationSpecification());
    	
    	return changePropagationSpecififactions;
    }
	
	
	  /**
     * Set up simple media store, which can be used for the tests. It consists of two components
     * (WebGUI and MediaStore) two Interfaces (IWebGUI and IMediastore). The interfaces have two
     * methods each ((web)download and (web)upload).
     */
    @Override
    public void beforeTest() {
    	super.beforeTest();
        try {
        	this.repository = this.createMediaStoreViaCode();     	
        } catch (Throwable t) {
        	fail("Exception during model creationg");
        }
        this.webGUIPackageName = WEBGUI;
    }
    
   
    
    public void instrumentSourceCode() throws CoreException {
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
            test.execute();
           
            	
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    public String getExternalCallCode() {
        final String code = "i" + MEDIA_STORE + "." + DOWNLOAD + "();";
        return code;
    }
    
    
    public IMethod getWebGuiDowloadMethodFromIstumentedProject() throws JavaModelException {
    	final String compilationUnitName = WEBGUI + "Impl";
        final String methodName = "httpDownload";
    	IWorkspace workspace = ResourcesPlugin.getWorkspace();  	
        IProject iProject = workspace.getRoot().getProject(getCurrentTestProjectFolder().getName() + ProjectManager.instrumentationProjectLabel);
        if(iProject != null) {
        	final ICompilationUnit iCu = CompilationUnitManipulatorHelper
                    .findICompilationUnitWithClassName(compilationUnitName, iProject);
            final IMethod iMethod = super.findIMethodByName(compilationUnitName, methodName, iCu);
            return iMethod;
        }
        else {
        	return null;
        }
    }
    
    
    public IMethod getWebGUIDownloadMethod() throws JavaModelException {
    	final String compilationUnitName = WEBGUI + "Impl";
        final String methodName = "httpDownload";
        final ICompilationUnit iCu = CompilationUnitManipulatorHelper
                .findICompilationUnitWithClassName(compilationUnitName, this.getCurrentTestProject());
        final IMethod iMethod = super.findIMethodByName(compilationUnitName, methodName, iCu);
        return iMethod;
    }
    
    
    public ResourceDemandingSEFF editWebGUIDownloadMethod(final String code) throws Throwable, JavaModelException {
        final String compilationUnitName = WEBGUI + "Impl";
        final String methodName = "httpDownload";
        return this.editMethod(code, compilationUnitName, methodName, true); 
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
    
    
    /**
     * @param code: source code to add
     * @param statementIndex: the index of the statement, after which the source code will be added
     * @return
     * @throws Throwable
     */
    public ResourceDemandingSEFF modifyWebGuiDownloadMethod(String code, int statementIndex) throws Throwable {
    	final String compilationUnitName = WEBGUI + "Impl";
        final String methodName = "httpDownload";
        return this.modifyMethodSourceCode(code, methodName, compilationUnitName, statementIndex);
    }
    
    
    /**
     * @param code: source code to add
     * @param methodName: the method in which the source code will be added
     * @param compilationUnitName: class name
     * @param statementIndex: the index of the statement, after which the source code will be added
     * @return
     * @throws Throwable
     */
    private ResourceDemandingSEFF modifyMethodSourceCode(String code, String methodName,
    		String compilationUnitName, int statementIndex) throws Throwable {
    	       
    	final ICompilationUnit iCu = CompilationUnitManipulatorHelper
                .findICompilationUnitWithClassName(compilationUnitName, this.getCurrentTestProject());
    	
        final Method method = super.findJaMoPPMethodInICU(iCu, methodName);
        
        List<Statement> methodStatements = ((ClassMethod)method).getStatements();

        if(methodStatements.size() != 0) {
        	JamoppPlainCodeParser.addAfterContainingStatement(methodStatements.get(statementIndex), code);
        }
        
        // save changes
		Resource resource = method.eResource();
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * we call this method only to make Vitruvius see the changes, because Vitruvius does not 
		 * recongnize changes done by JaMoPP
		 */
    	return this.editWebGUIDownloadMethod("");
    }
    
    
    protected Repository createMediaStoreViaCode() throws Throwable {
        // create main package
        final Repository repo = super.addRepoContractsAndDatatypesPackage();

        // create packages
        this.addPackageAndImplementingClass(MEDIA_STORE);
        this.addPackageAndImplementingClass(WEBGUI);

        final String webGuiInterfaceName = "I" + WEBGUI;
        final String mediaStoreInterfaceName = "I" + MEDIA_STORE;

        // create interfaces
        super.createInterfaceInPackageBasedOnJaMoPPPackageWithCorrespondence("contracts", webGuiInterfaceName);
        super.createInterfaceInPackageBasedOnJaMoPPPackageWithCorrespondence("contracts", mediaStoreInterfaceName);

        final String uploadMethodName = "upload";
        final String downloadMethodName = "download";

        final String httpDownloadMethodName = "httpDownload";
        final String httpUploadMethodName = "httpUpload";

        // create interface methods

        this.httpDownloadOpSig = super.addMethodToInterfaceWithCorrespondence(webGuiInterfaceName,
                httpDownloadMethodName);
        this.httpUploadOpSig = super.addMethodToInterfaceWithCorrespondence(webGuiInterfaceName, httpUploadMethodName);
        this.uploadOpSig = super.addMethodToInterfaceWithCorrespondence(mediaStoreInterfaceName, uploadMethodName);
        this.downloadOpSig = super.addMethodToInterfaceWithCorrespondence(mediaStoreInterfaceName, downloadMethodName);

        // create implements
        super.addImplementsCorrespondingToOperationProvidedRoleToClass(WEBGUI_CLASSNAME, webGuiInterfaceName);
        super.addImplementsCorrespondingToOperationProvidedRoleToClass(MEDIA_STORE_CLASSNAME, mediaStoreInterfaceName);

        // create class methods in component implementing classes in order to create SEFF
        // correspondences
        this.addClassMethodToClassThatOverridesInterfaceMethod(WEBGUI_CLASSNAME, httpUploadMethodName);
        this.addClassMethodToClassThatOverridesInterfaceMethod(WEBGUI_CLASSNAME, httpDownloadMethodName);
        this.addClassMethodToClassThatOverridesInterfaceMethod(MEDIA_STORE_CLASSNAME, uploadMethodName);
        this.addClassMethodToClassThatOverridesInterfaceMethod(MEDIA_STORE_CLASSNAME, downloadMethodName);

        // create requiredRole from webgui to IMediaStore
        this.webGUIRequiresIMediaStoreRole = this.addFieldToClassWithName(WEBGUI_CLASSNAME, mediaStoreInterfaceName,
                "i" + MEDIA_STORE, OperationRequiredRole.class);
        return repo;
    }

    protected void setWebGUIPackageName(final String webGUIPackageName) {
        this.webGUIPackageName = webGUIPackageName;
    }
	
	

}
