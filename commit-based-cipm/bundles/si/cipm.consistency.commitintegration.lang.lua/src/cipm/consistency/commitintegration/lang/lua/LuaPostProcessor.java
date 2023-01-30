package cipm.consistency.commitintegration.lang.lua;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.eclipse.xtext.EcoreUtil2;
import org.xtext.lua.LuaUtil;
import org.xtext.lua.lua.Chunk;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.scoping.LuaLinkingService;

public class LuaPostProcessor {
    private static final Logger LOGGER = Logger.getLogger(LuaPostProcessor.class.getName());


    /*
     * Get all functions that were mocked during the linking of the code model
     */
    private static Map<String, Statement_Function_Declaration> getMockedFunctions(ComponentSet set) {
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

    /*
     * Get all functions that are served in the application
     */
     private static Map<String, Statement_Function_Declaration> getServedFunctionsOfComponentSet(ComponentSet set) {
        Map<String, Statement_Function_Declaration> servedFuncs = new HashMap<>();

        final String serveFunctionName = "Script.serveFunction";

        var directCalls = EcoreUtil2.getAllContentsOfType(set, Expression_Functioncall_Direct.class);

        // iterate over all function calls which may be calls to "Script.serveFunction"
        for (var directCall : directCalls) {
            Consumer<String> logErrorWithCall = (errorMessage) -> {
                LOGGER.error(String.format("Code contains invalid serve call - %s:\n\t%s",
                        "TODO implement resolving function name to declaration", LuaUtil.eObjectToTokenText(directCall)));
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
                var servedName = LuaUtil.expressionStringToString(servedNameExpression);

                if (serveFuncArg instanceof Expression_VariableName serveFuncVar
                        && serveFuncVar.getRef() instanceof Statement_Function_Declaration servedFunctionDeclaration) {
                    servedFuncs.put(servedName, servedFunctionDeclaration);
                } else if (serveFuncArg instanceof Expression_String serveFuncNameExp) {
                    var servedFuncName = LuaUtil.expressionStringToString(serveFuncNameExp);

                    var servedFunctionDeclaration = LuaUtil.getFunctionDeclarationByName(servedFuncName,
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
    public static void postProcessComponentSet(ComponentSet set) {
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
                            LOGGER.trace(
                                    String.format("Replacing ref to mock with served function: %s", served.getKey()));
                            ref.setCalledFunction(servedFunc);
                        }
                    }
                }
            }
        }
    }
}
