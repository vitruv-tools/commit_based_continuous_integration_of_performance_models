package cipm.consistency.cpr.luapcm;


/**
 * Configuration for the CPRs
 * 
 * TODO currently this config is static, we should make it adjustable.
 * 
 * @author Lukas Burgey
 */
public class Config {

    public enum ReconstructionType {
        InternalAction, InternalCallAction, ExternalCallAction;
    }

    private static ReconstructionType reconstructionTypeInternalSeffCall = ReconstructionType.InternalAction;
    
    private static boolean trackServeCalls = true;

    /**
     * How do we model calls from one component to SEFFs of its own?
     */
    public static ReconstructionType getReconstructionTypeInternalSeffCall() {
        return reconstructionTypeInternalSeffCall;
    }

    public static void setInternalSeffCallReconstructionType(ReconstructionType type) {
        reconstructionTypeInternalSeffCall = type;
    }

    /**
     * Do we check if serve calls are added / deleted after a declaration is already present?
     */
    public static boolean isTrackServeCallsEnabled() {
        return trackServeCalls;
    }
    
    public static void setIsTrackServeCallsEnabled(boolean enable) {
        trackServeCalls = enable;
    }
}
