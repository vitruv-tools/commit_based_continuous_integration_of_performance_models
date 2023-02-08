package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.correspondence.view.CorrespondenceModelView;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;

public class CorrespondenceUtil {
    public static <C extends EObject> C getCorrespondingEObjectByType(
            CorrespondenceModelView<ReactionsCorrespondence> correspondenceModel, EObject eObj,
            Class<C> clazz) {
        var opt = correspondenceModel.getCorrespondingEObjects(eObj)
            .stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .findAny();
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;

    }

    public static <C extends EObject> List<C> getCorrespondingEObjectsByType(
            CorrespondenceModelView<ReactionsCorrespondence> correspondenceModel, EObject eObj,
            Class<C> clazz) {
        return correspondenceModel.getCorrespondingEObjects(eObj)
            .stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .collect(Collectors.toList());
    }
}
