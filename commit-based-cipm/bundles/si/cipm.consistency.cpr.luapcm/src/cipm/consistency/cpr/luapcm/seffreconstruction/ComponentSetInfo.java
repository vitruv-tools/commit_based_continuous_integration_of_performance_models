package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_Functioncall;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Statement_Function_Declaration;
import org.xtext.lua.scoping.LuaLinkingService;

import cipm.consistency.commitintegration.lang.lua.appspace.AppSpaceSemantics;

/**
 * This class contains information about a ComponentSet and its contents which is needed during SEFF
 * reconstruction
 * 
 * @author Lukas Burgey
 */
public class ComponentSetInfo {

    private static final Logger LOGGER = Logger.getLogger(ComponentSetInfo.class.getName());

    private ComponentSet componentSet;

    private List<String> servedFunctionNames;

    private Map<Expression_Functioncall, AbstractAction> functionCallToAction;

    /**
     * Initialize the component set info.
     * 
     * This causes some calculation (served function names).
     * 
     * @param componentSet
     *            The component set for which this class will contain infos
     */
    public ComponentSetInfo(ComponentSet componentSet) {
        LOGGER.debug("Initializing ComponentSetInfo for " + componentSet.toString());
        this.componentSet = componentSet;
        servedFunctionNames = generateServedFunctionNames(componentSet);
        functionCallToAction = new HashMap<>();
    }

    /**
     * Served function are "exported" an can be called by other appspace apps
     * 
     * @param root
     * @return
     */
    private static List<String> generateServedFunctionNames(EObject root) {
        // TODO we can assume that functions that end with '.register' and have 2 / 3 arguments are
        // registerring a function
        // so we don't have to hardcode so much
        List<String> servedNames = new ArrayList<>();

        var functionCalls = EcoreUtil2.getAllContentsOfType(root, Expression_Functioncall_Direct.class);
        for (var functionCall : functionCalls) {
            if (!AppSpaceSemantics.isServingFunctionCall(functionCall)) {
                continue;
            }

            var args = functionCall.getCalledFunctionArgs()
                .getArguments();
            if (args.size() == 2 || args.size() == 3) {
                var nameIndex = args.size() - 1;
                var funcName = args.get(nameIndex);
                if (funcName instanceof Expression_String funcNameExpString) {
                    servedNames.add(funcNameExpString.getValue());
                } else if (funcName instanceof Expression_VariableName funcNameExpVar) {
                    servedNames.add(funcNameExpVar.getRef()
                        .getName());
                } else {
                    throw new IllegalStateException("Invalid Script.serveFunction call: Arguments are of invalid type");
                }
            } else {
                throw new IllegalStateException("Invalid Script.serveFunction call: Must have 2 or 3 arguments");
            }
        }
        return servedNames;
    }

    /**
     * Determines if a function declaration requires SEFF reconstruction
     * 
     * 
     * @param declaration
     * @return boolean
     */
    public boolean needsSeffReconstruction(Statement_Function_Declaration declaration) {
        // TODO Is being served sufficient to determine if a seff reconstruction is needed?
        return servedFunctionNames.contains(declaration.getName());
    }

    // TODO this is currently only implemented for direct functioncalls
    private AbstractAction classifyFunctionCall(Expression_Functioncall call) {
        if (call instanceof Expression_Functioncall_Direct directCall) {
            var calledFunction = directCall.getCalledFunction();
            if (calledFunction == null) {
                return null;
            }

            var callingComponent = EcoreUtil2.getContainerOfType(directCall, Component.class);
            var functionComponent = EcoreUtil2.getContainerOfType(calledFunction, Component.class);

            if (callingComponent.equals(functionComponent)) {
                // internal call
                LOGGER.trace(String.format("Function classification: INTERNAL %s ", calledFunction.getName()));
                var action = SeffFactory.eINSTANCE.createInternalCallAction();
                action.setEntityName(calledFunction.getName());
                return action;
            }

            // external or library call
            if (functionComponent.getName()
                .equals(LuaLinkingService.MOCK_URI.path())) {
                // library call
                LOGGER.trace(String.format("Function classification: LIBRARY %s ", calledFunction.getName()));
                return SeffFactory.eINSTANCE.createInternalAction();
            } else {
                // external call
                LOGGER.trace(String.format("Function classification: EXTERNAL %s ", calledFunction.getName()));
                return SeffFactory.eINSTANCE.createExternalCallAction();
            }
        } else {
            LOGGER.error("Function classification for non-direct function calls is not yet implemented");
        }
        return null;
    }

    /**
     * Returns the classification of a function call
     * 
     * 
     * @param call
     *            The function call that is classified
     * @return The class of action into which the call was classified. Possible classifications are:
     *         - ExternalCallAction for calls to another component - InternalCall
     */
    public AbstractAction getFunctionCallClassification(Expression_Functioncall call) {
        // we cache results
        if (functionCallToAction.containsKey(call)) {
            return functionCallToAction.get(call);
        }

        // classify the function
        var action = classifyFunctionCall(call);
        functionCallToAction.put(call, action);
        return action;
    }
}
