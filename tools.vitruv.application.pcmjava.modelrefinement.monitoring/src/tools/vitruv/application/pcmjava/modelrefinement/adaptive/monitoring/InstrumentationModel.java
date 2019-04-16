package tools.vitruv.application.pcmjava.modelrefinement.adaptive.monitoring;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import tools.vitruv.models.im.AppProbes;
import tools.vitruv.models.im.ImPackage;

public class InstrumentationModel {

    private static AppProbes appProbes = null;
    private static final String INSTRUMENTATION_MODEL_PATH = "model/instrumentationmodel.im";
    private static final String INSTRUMENTATION_MODEL_EXTENTION = "im";

    public static AppProbes getAppProbes() {
        if (appProbes == null) {
            URI modelUri = URI.createFileURI(INSTRUMENTATION_MODEL_PATH);
            appProbes = getAppProbes(modelUri);
        }
        return appProbes;
    }

    private static AppProbes getAppProbes(URI modelUri) {
        ImPackage.eINSTANCE.eClass();
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put(INSTRUMENTATION_MODEL_EXTENTION, new XMIResourceFactoryImpl());

        ResourceSet resSet = new ResourceSetImpl();
        try {
            Resource resource = resSet.getResource(modelUri, true);
            return (AppProbes) resource.getContents().get(0);
        } catch (Exception e) {
            return null;
        }

    }

}
