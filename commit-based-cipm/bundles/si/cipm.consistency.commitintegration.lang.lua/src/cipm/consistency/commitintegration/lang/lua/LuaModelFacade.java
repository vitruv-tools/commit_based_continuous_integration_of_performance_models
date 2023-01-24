package cipm.consistency.commitintegration.lang.lua;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.xtext.lua.LuaStandaloneSetup;
import org.xtext.lua.lua.Chunk;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.LuaFactory;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.scoping.LuaLinkingService;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import cipm.consistency.commitintegration.lang.detection.ComponentDetector;
import cipm.consistency.commitintegration.lang.detection.ComponentDetectorImpl;
import cipm.consistency.commitintegration.lang.detection.ComponentState;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.models.CodeModelFacade;

public class LuaModelFacade implements CodeModelFacade {
    private static final Logger LOGGER = Logger.getLogger(LuaModelFacade.class.getName());
    private LuaDirLayout dirLayout;
    private ComponentDetector componentDetector;

    // TODO tracking the last component set is a bit ugly here
    private ComponentSet currentComponentSet;
    private Resource currentResource;

    @Inject
    private Provider<XtextResourceSet> resourceSetProvider;

    public LuaModelFacade() {
        Injector injector = new LuaStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);

        this.componentDetector = new ComponentDetectorImpl();
        this.dirLayout = new LuaDirLayout();
    }

    @Override
    public void initialize(Path dirPath) {
        // todo find a way to initialize the work tree
        // this.workTree = workTree;
        this.dirLayout.initialize(dirPath);
    }

    public void setComponentDetectionStrategies(List<ComponentDetectionStrategy> strategies) {
        for (var strat : strategies) {
            this.componentDetector.addComponentDetectionStrategy(strat);
        }
    }

    private XtextResourceSet parseDirToResourceSet(Path sourceCodeDirPath) {
        LOGGER.info("Parsing source code directory");
        // get a resource from the provider
        var resourceSet = resourceSetProvider.get();
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

        var iterator = FileUtils.iterateFiles(sourceCodeDirPath.toFile(), null, true);
        while (iterator.hasNext()) {
            var file = iterator.next();
            var path = file.toPath();

            if (!path.toString()
                .endsWith(".lua")) {
                continue;
            }

            var uri = URI.createFileURI(path.toAbsolutePath()
                .toString());

            Resource res = resourceSet.getResource(uri, true);
            if (res == null) {
                LOGGER.error(String.format("Unable to load resource: %s", uri));
            } else {
                LOGGER.debug(String.format("Loaded resource: %s", uri));
            }
        }

        return resourceSet;
    }

    private Resource getCleanResource(URI uri) {
        var rs = resourceSetProvider.get();

        var path = Path.of(uri.toFileString());
        if (path.toFile()
            .exists()) {
            LOGGER.debug(String.format("Deleting backed up resource: %s", uri));
            var backupPath = Path.of(uri.toFileString() + ".bak");
            try {
                Files.move(path, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
            }
        }

        var res = rs.createResource(uri);
        return res;
    }

    /*
     * Get all functions that were mocked during the linking of the code model
     */
    private Map<String, Statement_Function_Declaration> getMockedFunctions(ComponentSet set) {
        for (var component : set.getComponents()) {
            if (component.getName()
                .equals(LuaLinkingService.MOCK_URI.path())) {
                Map<String, Statement_Function_Declaration> mapping = new HashMap<>();
                var mockedFuncs = EcoreUtil2.getAllContentsOfType(component, Statement_Function_Declaration.class);
                for (var mockedFunc : mockedFuncs) {
                    mapping.put(mockedFunc.getName(), mockedFunc);
                }
                return mapping;
            }
        }
        return null;
    }

    private String eObjectToTokenText(EObject eObj) {
        var node = NodeModelUtils.getNode(eObj);
        if (node != null) {
            return NodeModelUtils.getTokenText(node);
        }
        return "";
    }

    /**
     * Extract the string from a string expression as parsed by the grammar
     * 
     * @param expString
     * @return The extraced String
     */
    // TODO this should be put into a utility class
    // TODO this could be implemented differently in the grammar, so we don't
    // need to strip here
    private String expressionStringToString(Expression_String expString) {
        // this string still contains quotes
        var rawString = expString.getValue();
        if (rawString.length() > 2) {
            return rawString.substring(1, rawString.length() - 1);
        }
        return "";
    }

    /**
     * Find a function declaration by name in a chunk
     * 
     * @param declarationName
     * @param chunk
     * @return
     */
    private Optional<Statement_Function_Declaration> getDeclarationByName(String declarationName, Chunk chunk) {
        var declarations = EcoreUtil2.getAllContentsOfType(chunk, Statement_Function_Declaration.class);
        return declarations.stream()
            .filter((decl) -> decl.getName()
                .equals(declarationName))
            .findFirst();
    }

    /*
     * Get all functions that are served in the application
     */
    private Map<String, Statement_Function_Declaration> getServedFunctionsOfComponentSet(ComponentSet set) {
        Map<String, Statement_Function_Declaration> servedFuncs = new HashMap<>();

        final String serveFunctionName = "Script.serveFunction";

        var directCalls = EcoreUtil2.getAllContentsOfType(set, Expression_Functioncall_Direct.class);

        // iterate over all function calls which may be calls to "Script.serveFunction"
        for (var directCall : directCalls) {
            Consumer<String> logErrorWithCall = (errorMessage) -> {
                LOGGER.error(String.format("Code contains invalid serve call - %s:\n\t%s",
                        "TODO implement resolving function name to declaration", eObjectToTokenText(directCall)));
            };

            // is this a not a call to Script.serveFunction? Then continue with the next
            if (!(directCall.getCalledFunction()
                .getName()
                .equals(serveFunctionName)
                    && directCall.getCalledFunctionArgs()
                        .getArguments()
                        .size() == 2)) {
                continue;
            }

            var serveArgs = directCall.getCalledFunctionArgs()
                .getArguments();
            var serveNameArg = serveArgs.get(0);
            var serveFuncArg = serveArgs.get(1);

            // the name under which the function is served to other apps
            if (serveNameArg instanceof Expression_String servedNameExpression) {
                var servedName = expressionStringToString(servedNameExpression);

                if (serveFuncArg instanceof Expression_VariableName serveFuncVar
                        && serveFuncVar.getRef() instanceof Statement_Function_Declaration servedFunctionDeclaration) {
                    servedFuncs.put(servedName, servedFunctionDeclaration);
                } else if (serveFuncArg instanceof Expression_String serveFuncNameExp) {
                    var servedFuncName = expressionStringToString(serveFuncNameExp);

                    var servedFunctionDeclaration = getDeclarationByName(servedFuncName,
                            EcoreUtil2.getContainerOfType(directCall, Chunk.class));
                    if (servedFunctionDeclaration.isPresent()) {
                        servedFuncs.put(servedName, servedFunctionDeclaration.get());
                    } else {
                        logErrorWithCall.accept("Cannot resolve function name to declaration");
                    }
                } else {
                    logErrorWithCall.accept(
                            "Cannot deduce function declaration from second argument of call to Script.serveFunction");
                }
            } else {
                logErrorWithCall.accept("First argument of call to Script.serveFunction is no Expression_String");
            }
        }

        return servedFuncs;
    }

    /*
     * Resolve crown calls which are actually calls to "serves" of another app
     */
    private void postProcessComponentSet(ComponentSet set) {
        // find functions that were mocked during the linking process, because the were not in scope
        var mockedFuncs = getMockedFunctions(set);

        // find functions that are served by apps to other apps
        var servedFuncs = getServedFunctionsOfComponentSet(set);

        // function calls which were mocked, but are served by another component must be resolved
        // to the actually served function

        if (mockedFuncs != null) {
            for (var served : servedFuncs.entrySet()) {
                var mockedFunc = mockedFuncs.get(served.getKey());
                if (mockedFunc != null) {
                    LOGGER.trace(String.format("MOCKED but SERVED function: %s", served.getKey()));
                    var servedFunc = served.getValue();

                    // TODO Replace references to mockFunc with references to servedFunc

                    var refs = EcoreUtil2.getAllContentsOfType(set, Expression_Functioncall_Direct.class);
                    for (var ref : refs) {
                        if (ref.getCalledFunction()
                            .equals(mockedFunc)) {
                            LOGGER.debug(
                                    String.format("Replacing ref to mock with served function: %s", served.getKey()));
                            ref.setCalledFunction(servedFunc);
                        }
                    }
                }
            }
        }
    }

    /**
     * Merges all chunks of the resource set into one SuperChunk and puts it in a separate resource
     * for propagation
     * 
     * @param rs
     * @param targetUri
     * @return
     * @throws IOException
     */
    private ComponentSet resolveResourceSetToComponents(Path workTree, ResourceSet rs, URI targetUri) {
        LOGGER.debug("Resolving ResourceSet into a ComponentSet");
        var merge = getCleanResource(targetUri);

        // we use this synthetic model class as a container for all the components
        ComponentSet componentSet = LuaFactory.eINSTANCE.createComponentSet();
        merge.getContents()
            .add(componentSet);

        // merge warnings and errors into the new resource
        rs.getResources()
            .forEach(resource -> {
                // also merge errors and warnings into the new resource
                merge.getErrors()
                    .addAll(resource.getErrors());
                merge.getWarnings()
                    .addAll(resource.getWarnings());

                if (resource.getContents()
                    .size() == 0) {
                    LOGGER.error(String.format("Resource has no contents: %s", resource.getURI()));
                    return;
                }
                var eObj = resource.getContents()
                    .get(0);
                if (!(eObj instanceof Chunk)) {
                    LOGGER.error(String.format("Resource does not contain a chunk: %s", resource.getURI()));
                    return;
                }
            });

        // detect components in the resources
        var componentCandidates = componentDetector.detectModules(rs, workTree, dirLayout.getModuleConfigurationPath());
        if (componentCandidates == null) {
            LOGGER.info("No detected components!");
            return componentSet;
        }
        var actualComponents = componentCandidates.getModulesInState(ComponentState.REGULAR_COMPONENT);
        LOGGER.debug(String.format("Detected %d components", actualComponents.size()));

        // Create the detected components and add them to the component set
        actualComponents.forEach((componentName, resources) -> {
            var component = LuaFactory.eINSTANCE.createComponent();
            component.setName(componentName);

            for (var resource : resources) {
                if (resource.getContents()
                    .size() > 0) {
                    var eObj = resource.getContents()
                        .get(0);
                    if (eObj instanceof Chunk) {
                        var namedChunk = LuaFactory.eINSTANCE.createNamedChunk();
                        namedChunk.setChunk((Chunk) eObj);
                        var chunkName = resource.getURI()
                            .lastSegment();
                        namedChunk.setName(chunkName);
                        component.getChunks()
                            .add(namedChunk);
                    }
                }

            }
            componentSet.getComponents()
                .add(component);
        });

        // call possible post process on the component set
        postProcessComponentSet(componentSet);

        // save the component set to the merged resource
        try {
            merge.save(null);
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot write new resource: %s", targetUri));
        }

        return componentSet;
    }

    @Override
    public Resource parseSourceCodeDir(Path sourceCodeDir) {
        LOGGER.info("Propagating the current worktree");
        // parse all lua files into one resource set
        var workTreeResourceSet = parseDirToResourceSet(sourceCodeDir);

        // where the processed resource is stored prior to propagation
        var storeUri = dirLayout.getParsedFileUri();
        currentComponentSet = resolveResourceSetToComponents(sourceCodeDir, workTreeResourceSet, storeUri);

        currentResource = currentComponentSet.eResource();
        return currentResource;
    }

    public boolean existsOnDisk() {
        return dirLayout.getModelFilePath()
            .toFile()
            .exists();
    }

    @Override
    public LuaDirLayout getDirLayout() {
        return dirLayout;
    }

    public ComponentSet getCurrentComponentSet() {
        return currentComponentSet;
    }

    @Override
    public List<Resource> getResources() {
        return null;
    }

    @Override
    public Resource getResource() {
        return currentResource;
    }
}
