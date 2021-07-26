package cipm.consistency.commitintegration.diff.util;

import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.ecore.resource.Resource;

public class ResourceListFilteringComparisonScope extends DefaultComparisonScope {
	public ResourceListFilteringComparisonScope(Notifier left, Notifier right,
			List<Resource> newResources, List<Resource> currentResources) {
		super(left, right, null);
		this.setResourceSetContentFilter(r -> {
			if (newResources != null && currentResources != null) {
				return r.getResourceSet() == left && newResources.contains(r)
						|| r.getResourceSet() == right && currentResources.contains(r);
			}
			return true;
		});
	}
}
