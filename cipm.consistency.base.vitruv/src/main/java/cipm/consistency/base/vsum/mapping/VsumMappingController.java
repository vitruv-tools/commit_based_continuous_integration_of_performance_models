package cipm.consistency.base.vsum.mapping;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cipm.consistency.base.vsum.manager.VsumManager;
import tools.vitruv.extensions.dslsruntime.reactions.helper.ReactionsCorrespondenceHelper;

@Component
public class VsumMappingController {
	@Autowired
	private VsumManager manager;

	public <T> List<T> getCorrespondingElements(EObject obj, Class<T> type) {
		return this.getCorrespondingElements(obj, null, type);
	}

	public <T> List<T> getCorrespondingElements(EObject obj, String tag, Class<T> type) {
		return ReactionsCorrespondenceHelper.getCorrespondingModelElements(obj, type, tag, (f) -> true,
				manager.getCorrespondenceModel());
	}

	public <T> Optional<T> getCorrespondingElement(EObject obj, Class<T> type) {
		return this.getCorrespondingElement(obj, null, type);
	}

	public <T> Optional<T> getCorrespondingElement(EObject obj, String tag, Class<T> type) {
		List<T> els = this.getCorrespondingElements(obj, tag, type);
		if (els == null || els.size() == 0) {
			return Optional.empty();
		} else {
			return els.stream().findFirst();
		}
	}

}
