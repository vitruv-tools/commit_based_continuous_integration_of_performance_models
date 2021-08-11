package cipm.consistency.commitintegration.detection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ModuleConfiguration {
	private final static String SEPARATOR = "/";
	private Path configPath;
	private HashMap<String, ModuleState> moduleClassification = new HashMap<>();
	private HashMap<String, String> subModuleMapping = new HashMap<>();
	
	public ModuleConfiguration(Path configPath) {
		this.configPath = configPath;
		load(configPath);
	}
	
	private void load(Path configPath) {
		if (Files.exists(configPath)) {
			Properties p = new Properties();
			try (BufferedReader reader = Files.newBufferedReader(configPath)) {
				p.load(reader);
				p.propertyNames().asIterator().forEachRemaining(k -> {
					String key = (String) k;
					String val = p.getProperty(key);
					if (val.contains(SEPARATOR)) {
						moduleClassification.put(key, ModuleState.PART_OF_COMPONENT);
						subModuleMapping.put(key, val.split(SEPARATOR)[1]);
					} else {
						moduleClassification.put(key, ModuleState.valueOf(val));
					}
				});
			} catch (IOException e) {
			}
		}
	}
	
	public void clear() {
		moduleClassification.clear();
		subModuleMapping.clear();
	}
	
	public Map<String, ModuleState> getModuleClassification() {
		return moduleClassification;
	}
	
	public Map<String, String> getSubModuleMapping() {
		return subModuleMapping;
	}
	
	public void save() {
		Properties p = new Properties();
		moduleClassification.forEach((k, v) -> {
			if (v == ModuleState.PART_OF_COMPONENT) {
				p.setProperty(k, v.name() + SEPARATOR + subModuleMapping.get(k));
			} else {
				p.setProperty(k, v.name());
			}
		});
		try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
			p.store(writer, null);
		} catch (IOException e) {
		}
	}
}
