package cipm.consistency.models.im;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelFactory;
import cipm.consistency.base.shared.FileBackedModelUtil;
import cipm.consistency.models.ModelFacade;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class ImFacade implements ModelFacade {

    private InstrumentationModel im;
    private ImDirLayout dirLayout;

    public ImFacade() {
        dirLayout = new ImDirLayout();
    }

    @Override
    public void initialize(Path rootPath) {
        dirLayout.initialize(rootPath);
        loadOrCreateModelResources();
    }

    public void saveToDisk() {
        FileBackedModelUtil.synchronize(im, dirLayout.getImFilePath()
            .toFile(), InstrumentationModel.class);
        try {
            this.getModel()
                .eResource()
                .save(null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createModel() {
        // build IMM
        im = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();
        saveToDisk();
    }

    private void loadModel() {
        var rs = (new ResourceSetImpl());
        var res = rs.getResource(dirLayout.getImFileUri(), true);
        if (res.getContents()
            .size() > 0) {
            var eobj = res.getContents()
                .get(0);
            if (eobj instanceof InstrumentationModel) {
                im = (InstrumentationModel) eobj;
            }
        }
    }

    public void loadOrCreateModelResources() {
        if (!fileExists()) {
            createModel();
        } else {
            loadModel();
        }
    }

    private boolean fileExists() {
        return dirLayout.getImFilePath()
            .toFile()
            .exists();
    }

    @Override
    public ImDirLayout getDirLayout() {
        return dirLayout;
    }

    public InstrumentationModel getModel() {
        return im;
    }

    @Override
    public List<Resource> getResources() {
        return null;
    }

    @Override
    public Resource getResource() {
        if (im != null) {
            return im.eResource();
        }
        return null;
    }
}
