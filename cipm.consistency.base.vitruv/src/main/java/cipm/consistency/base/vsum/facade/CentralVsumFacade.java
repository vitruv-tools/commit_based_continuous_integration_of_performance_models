package cipm.consistency.base.vsum.facade;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.vsum.manager.VsumManager;
import cipm.consistency.base.vsum.manager.VsumManager.VsumChangeSource;
import cipm.consistency.base.vsum.mapping.VsumMappingController;
import tools.vitruv.framework.change.echange.eobject.CreateEObject;
import tools.vitruv.framework.change.echange.eobject.DeleteEObject;

@Component
public class CentralVsumFacade implements ISpecificVsumFacade {
	@Autowired
	private VsumManager manager;

	@Autowired
	private VsumMappingController mapping;

	private Map<String, Optional<ResourceContainer>> runtimeContainerMappingCache;
	private Map<ServiceEffectSpecification, Optional<ServiceInstrumentationPoint>> seffMappingCache;

	public CentralVsumFacade() {
		this.runtimeContainerMappingCache = Maps.newHashMap();
		this.seffMappingCache = Maps.newHashMap();
	}

	@Override
	public <T> Optional<T> resolveGenericCorrespondence(EObject obj, String tag, Class<T> type) {
		return mapping.getCorrespondingElement(obj, tag, type);
	}

	@Override
	public <T> List<T> resolveGenericCorrespondences(EObject obj, String tag, Class<T> type) {
		return mapping.getCorrespondingElements(obj, tag, type);
	}

	@Override
	public <T extends EObject> void createdObject(T obj, VsumChangeSource source) {
		manager.executeTransaction(() -> {
			CreateEObject<T> change = manager.getAtomicFactory().createCreateEObjectChange(obj);
			change.setAffectedEObject(obj);
			manager.propagateChange(change, source);

			return null;
		});
	}

	@Override
	public <T extends EObject> void deletedObject(T obj, VsumChangeSource source) {
		manager.executeTransaction(() -> {
			DeleteEObject<T> change = manager.getAtomicFactory().createDeleteEObjectChange(obj);
			change.setAffectedEObject(obj);
			manager.propagateChange(change, source);

			return null;
		});
	}

	@Override
	public Optional<ResourceContainer> getCorrespondingResourceContainer(RuntimeResourceContainer rrc) {
		if (runtimeContainerMappingCache.containsKey(rrc.getHostID())) {
			return runtimeContainerMappingCache.get(rrc.getHostID());
		} else {
			Optional<ResourceContainer> result = mapping.getCorrespondingElement(rrc, ResourceContainer.class);
			runtimeContainerMappingCache.put(rrc.getHostID(), result);
			return result;
		}
	}

	@Override
	public Optional<ServiceInstrumentationPoint> getCorrespondingInstrumentationPoint(ServiceEffectSpecification seff) {
		if (seffMappingCache.containsKey(seff)) {
			return seffMappingCache.get(seff);
		} else {
			Optional<ServiceInstrumentationPoint> result = mapping.getCorrespondingElement(seff,
					ServiceInstrumentationPoint.class);
			seffMappingCache.put(seff, result);
			return result;
		}
	}

	@Override
	public void reset(boolean hard) {
		if (hard) {
			runtimeContainerMappingCache.clear();
			seffMappingCache.clear();
		}
	}

}
