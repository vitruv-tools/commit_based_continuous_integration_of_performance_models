package cipm.consistency.commitintegration.lang.lua.appspace;

import org.xtext.lua.lua.Expression_Functioncall_Direct;

public class AppSpaceSemantics {

    private static final String SERVE_FUNCTION_NAME = "Script.serveFunction";
    private static final int SERVE_FUNCTION_ARG_COUNT = 2;
    private static final String REGISTER_FUNCTION_SUFFIX = ".register";
    private static final int REGISTER_FUNCTION_ARG_COUNT = 3;

    public static boolean isServingFunctionCall(Expression_Functioncall_Direct call) {
        var functionName = call.getCalledFunction()
            .getName();
        var argumentCount = call.getCalledFunctionArgs().getArguments().size();

        return (functionName.equals(SERVE_FUNCTION_NAME) && argumentCount == SERVE_FUNCTION_ARG_COUNT)
                || (functionName.endsWith(REGISTER_FUNCTION_SUFFIX) && argumentCount == REGISTER_FUNCTION_ARG_COUNT);
    }

}
