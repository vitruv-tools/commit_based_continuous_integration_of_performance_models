package cipm.consistency.commitintegration.settings;

public class SettingKeys {
	private final static String KEY_BASE = "cipm.consistency.settings.";
	public final static String PERFORM_FULL_INSTRUMENTATION = KEY_BASE + "instrumentation.full";
	public final static String PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION = KEY_BASE + "reconstruction.finegrained";
	public final static String USE_PCM_IM_CPRS = KEY_BASE + "cpr.pcmim";
	public final static String JAVA_PARSER_EXCLUSION_PATTERNS = KEY_BASE + "parser.excludes";
	public final static String PATH_TO_PREPROCESSING_SCRIPT = KEY_BASE + "path.preprocess";
	public final static String PATH_TO_COMPILATION_SCRIPT = KEY_BASE + "path.compile";
	public final static String DEPLOYMENT_PATH = KEY_BASE + "path.deployment";
}
