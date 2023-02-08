package cipm.consistency.cpr.luapcm.seffreconstruction;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Statement_Function_Declaration;

public class SeffHelper {
    private static final Logger LOGGER = Logger.getLogger(SeffHelper.class.getName());

    public static boolean needsSeffReconstruction(Refble declaration) {
        if (declaration == null) {
            return false;
        }

        var infos = ComponentSetInfoRegistry.getInfosForComponentSet(declaration);
        var needsSeff = infos.needsSeffReconstruction(declaration);
        if (needsSeff) {
            LOGGER.trace(String.format("Statement_Function_Declaration needs SEFF reconstruction: %s",
                    declaration.getName()));
        }
        return needsSeff;
    }

    public static boolean needsSeffReconstruction(EObject eObj) {
        var parentDeclaration = EcoreUtil2.getContainerOfType(eObj, Statement_Function_Declaration.class);
        return needsSeffReconstruction(parentDeclaration);
    }
}
