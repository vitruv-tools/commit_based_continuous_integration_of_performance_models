package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.xtext.lua.LuaStandaloneSetup;
import org.xtext.lua.lua.Chunk;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

@SuppressWarnings("restriction")
public class LuaCommitChangePropagator extends CommitChangePropagator {

    @Inject
    ParseHelper<Chunk> chunkParseHelper;
    @Inject
    Provider<XtextResourceSet> resourceSetProvider;

    public LuaCommitChangePropagator(VsumFacade vsumFacade, GitRepositoryWrapper repoWrapper,
            LanguageFileSystemLayout fileLayout) {
        super(vsumFacade, repoWrapper, fileLayout);

        // TODO Is this the correct way of doing the injecting? I heard that standalone was not
        // ideal, when within eclipse
        Injector injector = new LuaStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);
    }

    @Override
    public boolean propagateCurrentCheckout() {
        LOGGER.error("Trying to propagate the current checkout!");

        var rs = new ResourceSetImpl();
        var iterator = FileUtils.iterateFiles(repoWrapper.getWorkTree(), null, true);
        while (iterator.hasNext()) {
            var file = iterator.next();
            var path = file.toPath();

            if (!path.toString().endsWith(".lua")) 
                continue;

            var uri = URI.createFileURI(path.toAbsolutePath().toString());
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(file);
                chunkParseHelper.parse(inputStream, uri, null, rs);
            } catch (FileNotFoundException e) {
            }
        }
        
        
        var modelUri = URI.createFileURI(getFileSystemLayout().getModelFile().toAbsolutePath().toString());
        var modelResource = new ResourceImpl();
        modelResource.setURI(modelUri);

        // store the complete resource set in one resource and 
        rs.getResources().forEach(resource -> {
            modelResource.getContents().add(resource.getContents().get(0));
        });

        // store it at modelUri
        try {
            modelResource.save(null);
            this.vsum.getModelInstance(modelUri);
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
