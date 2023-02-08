package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Statement_Function_Declaration;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import cipm.consistency.commitintegration.lang.lua.appspace.AppSpaceSemantics;

/**
 * This class contains information about a ComponentSet and its contents which is needed during SEFF
 * reconstruction
 * 
 * @author Lukas Burgey
 */
public class ComponentSetInfo {

    private static final Logger LOGGER = Logger.getLogger(ComponentSetInfo.class.getName());

    private List<String> servedFunctionNames;

    // we track which Statement_Function_Declaration are called in an external call action
    private ListMultimap<Statement_Function_Declaration, ExternalCallAction> declarationToCallingActions;

    // map a component to component it depends upon (because it has external calls to it)
    private ListMultimap<Component, Component> componentToRequiredComponents;

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
        servedFunctionNames = generateServedFunctionNames(componentSet);

        declarationToCallingActions = ArrayListMultimap.create();
        componentToRequiredComponents = ArrayListMultimap.create();
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
    public boolean needsSeffReconstruction(Refble declaration) {
        // TODO Is being served sufficient to determine if a seff reconstruction is needed?
        return servedFunctionNames.contains(declaration.getName());
    }

    public ListMultimap<Statement_Function_Declaration, ExternalCallAction> getDeclarationToCallingActions() {
        return declarationToCallingActions;
    }

    public ListMultimap<Component, Component> getComponentToRequiredComponents() {
        return componentToRequiredComponents;
    }

//    // TODO document
//    public void externalCallActionCallsDeclaration(ExternalCallAction callAction,
//            Statement_Function_Declaration calledDeclaration) {
//        declarationToCallingActions.put(calledDeclaration, callAction);
//    }
//
//    // TODO document
//    public List<ExternalCallAction> getCallingActions(Statement_Function_Declaration declaration) {
//        return declarationToCallingActions.get(declaration);
//    }
//
//    // TODO document
//    public void addComponentDependency(Component requiringComponent, Component providingComponent) {
//        componentToRequiredComponents.put(requiringComponent, providingComponent);
//    }
//    
//
//    // TODO document
//    public List<Component> getCallingActions(Component requiringComponent) {
//        return componentToRequiredComponents.get(requiringComponent);
//    }
}
