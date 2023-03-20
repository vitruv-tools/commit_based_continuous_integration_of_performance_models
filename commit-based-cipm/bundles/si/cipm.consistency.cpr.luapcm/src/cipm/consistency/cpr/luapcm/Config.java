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
    
    /**
     * If we set our our descriptive names in the CPRs, the resulting model
     * Will not easily match a manually created model. Therefore we can
     * disable the naming using this switch
     */
    private static boolean descriptiveNames = false;

    /**
     * How do we model calls from one component to SEFFs of its own?
     */
    public static ReconstructionType getReconstructionTypeInternalSeffCall() {
        return reconstructionTypeInternalSeffCall;
    }

    public static void setInternalSeffCallReconstructionType(ReconstructionType type) {
        reconstructionTypeInternalSeffCall = type;
    }

    public static boolean descriptiveNames() {
        return descriptiveNames;
    }

    public static void setSetEntityNames(boolean setEntityNames) {
        Config.descriptiveNames = setEntityNames;
    }
}
