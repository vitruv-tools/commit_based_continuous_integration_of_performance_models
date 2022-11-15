package cipm.consistency.commitintegration.lang.detection.java.teammates;

import cipm.consistency.commitintegration.lang.detection.ComponentState;
import cipm.consistency.commitintegration.lang.detection.java.PackageBasedComponentDetectionStrategy;

/**
 * A component detection strategy for TEAMMATES.
 * 
 * @author Martin Armbruster
 */
public class TEAMMATESComponentDetectionStrategy extends PackageBasedComponentDetectionStrategy {
	@Override
	protected void initializeMappings() {
		this.addPackageModuleMapping("teammates\\.common\\..*?", "teammates.common", ComponentState.REGULAR_COMPONENT);
		this.addPackageModuleMapping("teammates\\.logic\\.api\\..*?", "teammates.logic", ComponentState.REGULAR_COMPONENT);
		this.addPackageModuleMapping("teammates\\.storage\\.api\\..*?", "teammates.storage", ComponentState.REGULAR_COMPONENT);
		// TODO: Replace Microservice component with Servlet-based component.
		this.addPackageModuleMapping("teammates\\.ui\\..*?", "teammates.ui", ComponentState.MICROSERVICE_COMPONENT);
	}
}
