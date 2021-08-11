package cipm.consistency.commitintegration.detection;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;

public class ModuleCandidates {
	private EnumMap<ModuleState, Map<String, Set<Resource>>> candidates;
	
	public ModuleCandidates() {
		candidates = new EnumMap<>(ModuleState.class);
		for (var value : ModuleState.values()) {
			candidates.put(value, new HashMap<>());
		}
	}
	
	public void addModuleClassifier(ModuleState state, String moduleName, Resource cu) {
		Map<String, Set<Resource>> classMap = getModulesInState(state);
		Set<Resource> classSet;
		if (classMap.containsKey(moduleName)) {
			classSet = classMap.get(moduleName);
		} else {
			classSet = new HashSet<>();
			classMap.put(moduleName, classSet);
		}
		classSet.add(cu);
	}
	
	public Map<String, Set<Resource>> getModulesInState(ModuleState state) {
		return candidates.get(state);
	}
	
	public void updateState(ModuleState oldState, ModuleState newState, String moduleName) {
		Map<String, Set<Resource>> map = getModulesInState(oldState);
		Set<Resource> classes = map.remove(moduleName);
		map = getModulesInState(newState);
		map.put(moduleName, classes);
	}
	
	public void removeModule(ModuleState state, String moduleName) {
		Map<String, Set<Resource>> map = getModulesInState(state);
		map.remove(moduleName);
	}
	
	public ModuleState getStateOfModule(String modName) {
		for (var entry : candidates.entrySet()) {
			for (var subEntry : entry.getValue().entrySet()) {
				if (subEntry.getKey().equals(modName)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
}
