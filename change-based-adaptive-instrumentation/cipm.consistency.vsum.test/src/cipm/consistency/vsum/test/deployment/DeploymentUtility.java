package cipm.consistency.vsum.test.deployment;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.seff.AbstractInternalControlFlowAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.palladiosimulator.pcm.seff.seff_performance.SeffPerformanceFactory;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.base.shared.pcm.LocalFilesystemPCM;
import cipm.consistency.commitintegration.ExternalCommandExecutionUtility;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.vsum.test.TeaStoreCITest;

public class DeploymentUtility extends TeaStoreCITest {
	private final static Logger logger = Logger.getLogger("cipm." + DeploymentUtility.class.getSimpleName());
	private Path instrumentationCodeDir;
	private InMemoryPCM preparedPCM;

	@Test
	public void compileAndDeployInstrumentedCode() throws IOException {
//		this.controller.instrumentCode(false);
		instrumentationCodeDir = this.controller.getCommitChangePropagator()
				.getJavaFileSystemLayout().getInstrumentationCopy();
		if (Files.exists(instrumentationCodeDir)) {
			boolean compilationResult = compileInstrumentedCode();
			if (compilationResult) {
				Path deployPath = Paths.get(CommitIntegrationSettingsContainer.getSettingsContainer()
						.getProperty(SettingKeys.DEPLOYMENT_PATH));
				var result = copyArtifacts(deployPath);
				result.forEach(p -> {
					try {
						removeMonitoringClasses(p);
					} catch (IOException e) {
						fail(e);
					}
				});
			} else {
				logger.debug("Could not compile the instrumented code.");
			}
		}
	}
	
	private boolean compileInstrumentedCode() {
		String compileScript = CommitIntegrationSettingsContainer.getSettingsContainer()
			.getProperty(SettingKeys.PATH_TO_COMPILATION_SCRIPT);
		compileScript = new File(compileScript).getAbsolutePath();
		if (Files.exists(instrumentationCodeDir)) {
			logger.debug("Compiling the instrumented code.");
			return ExternalCommandExecutionUtility.runScript(instrumentationCodeDir.toFile(), compileScript);
		} else {
			logger.debug("No instrumented code available.");
		}
		return false;
	}
	
	private List<Path> copyArtifacts(Path deployPath) throws IOException {
		logger.debug("Copying the artifacts to " + deployPath);
		var warFiles = Files.walk(instrumentationCodeDir).filter(Files::isRegularFile)
			.filter(p -> p.getFileName().toString().endsWith(".war"))
			.collect(Collectors.toCollection(ArrayList::new));
		List<String> fileNames = new ArrayList<>();
		for (int idx = 0; idx < warFiles.size(); idx++) {
			String name = warFiles.get(idx).getFileName().toString();
			if (fileNames.contains(name)) {
				warFiles.remove(idx);
				idx--;
			} else {
				fileNames.add(name);
			}
		}
		List<Path> result = new ArrayList<>();
		warFiles.forEach(p -> {
			Path target = deployPath.resolve(p.getFileName());
			try {
				Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
				result.add(target);
			} catch (IOException e) {
				fail(e);
			}
		});
		return result;
	}
	
	private void removeMonitoringClasses(Path file) throws IOException {
		logger.debug("Removing the monitoring classes from the artifacts.");
		Map<String, String> options = new HashMap<>();
		options.put("create", "false");
		try (FileSystem fileSys = FileSystems.newFileSystem(file, options)) {
			String tmcEndPath = "cipm/consistency/bridge/monitoring/controller/ThreadMonitoringController.class";
			String spEndPath = "cipm/consistency/bridge/monitoring/controller/ServiceParameters.class";
			fileSys.getRootDirectories().forEach(root -> {
				try {
					Files.walk(root).filter(p -> {
						String fullPath = p.toString();
						if (fullPath.endsWith(".jar") || fullPath.endsWith(".war")
								|| fullPath.endsWith(".zip")) {
							try {
								removeMonitoringClasses(p);
							} catch (IOException e) {
								fail(e);
							}
						} else if (fullPath.endsWith(tmcEndPath) || fullPath.endsWith(spEndPath)) {
							return true;
						}
						return false;
					}).forEach(p -> {
						try {
							Files.delete(p);
						} catch (IOException e) {
							fail(e);
						}
					});
				} catch (IOException e) {
					fail(e);
				}
			});
		}
	}
	
	@Test
	public void preparePCMModels() {
		preparedPCM = this.controller.getVSUMFacade().getPCMWrapper().copyDeep();
		eliminateDuplicatedInterfaceNames();
		addResourceDemand();
		createSystemModel();
		createUsageModel();
		storePCM();
	}
	
	private void eliminateDuplicatedInterfaceNames() {
		var interfaces = preparedPCM.getRepository().getInterfaces__Repository();
		for (int index = 0; index < interfaces.size(); index++) {
			var inter = interfaces.get(index);
			for (int idx2 = index + 1; idx2 < interfaces.size(); idx2++) {
				var i = interfaces.get(idx2);
				if (inter.getEntityName().equals(i.getEntityName())) {
					i.setEntityName(i.getEntityName() + "2");
				}
			}
		}
	}
	
	private void addResourceDemand() {
		ResourceSet resSet = new ResourceSetImpl();
		Resource res = resSet.getResource(URI.createURI("pathmap://PCM_MODELS/Palladio.resourcetype", true), true);
		ProcessingResourceType cpu = null;
		for (var iter = res.getAllContents(); iter.hasNext();) {
			var next = iter.next();
			if (next instanceof ProcessingResourceType) {
				ProcessingResourceType candidate = (ProcessingResourceType) next;
				if (candidate.getEntityName().equals("CPU")) {
					cpu = candidate;
					break;
				}
			}
		}
		ProcessingResourceType actualCPU = cpu;
		preparedPCM.getRepository().eAllContents().forEachRemaining(obj -> {
			if (obj instanceof AbstractInternalControlFlowAction
					&& !(obj instanceof StartAction) && !(obj instanceof StopAction)) {
				AbstractInternalControlFlowAction action = (AbstractInternalControlFlowAction) obj;
				ParametricResourceDemand resDemand = SeffPerformanceFactory.eINSTANCE.createParametricResourceDemand();
				resDemand.setRequiredResource_ParametricResourceDemand(actualCPU);
				PCMRandomVariable vari = CoreFactory.eINSTANCE.createPCMRandomVariable();
				vari.setSpecification("1");
				resDemand.setSpecification_ParametericResourceDemand(vari);
				action.getResourceDemand_Action().add(resDemand);
			}
		});
	}
	
	private void createSystemModel() {
		var components = preparedPCM.getRepository().getComponents__Repository();
		var system = preparedPCM.getSystem();
		for (int index = 0; index < components.size(); index++) {
			
			var com = components.get(index);
			AssemblyContext nextCtx = CompositionFactory.eINSTANCE.createAssemblyContext();
			nextCtx.setEntityName("System_ctx_" + com.getEntityName() + "_" + index);
			nextCtx.setEncapsulatedComponent__AssemblyContext(com);
			system.getAssemblyContexts__ComposedStructure().add(nextCtx);
			
			var providedRoles = com.getProvidedRoles_InterfaceProvidingEntity();
			for (int provIdx = 0; provIdx < providedRoles.size(); provIdx++) {
				var role = providedRoles.get(provIdx);
				if (role instanceof OperationProvidedRole) {
					OperationProvidedRole opRole = (OperationProvidedRole) role;
					OperationProvidedRole systemRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
					systemRole.setEntityName("System_provides_" + opRole.getProvidedInterface__OperationProvidedRole().getEntityName());
					systemRole.setProvidedInterface__OperationProvidedRole(opRole.getProvidedInterface__OperationProvidedRole());
					system.getProvidedRoles_InterfaceProvidingEntity().add(systemRole);
					
					ProvidedDelegationConnector connector = CompositionFactory.eINSTANCE.createProvidedDelegationConnector();
					connector.setEntityName(systemRole.getEntityName() + " -> " + opRole.getEntityName());
					connector.setAssemblyContext_ProvidedDelegationConnector(nextCtx);
					connector.setInnerProvidedRole_ProvidedDelegationConnector(opRole);
					connector.setOuterProvidedRole_ProvidedDelegationConnector(systemRole);
					system.getConnectors__ComposedStructure().add(connector);
				}
			}
		}
	}
	
	private void createUsageModel() {
		var usageModel = preparedPCM.getUsageModel();
		var components = preparedPCM.getRepository().getComponents__Repository();
		var systemRoles = preparedPCM.getSystem().getProvidedRoles_InterfaceProvidingEntity();
		int counter = 0;
		for (var com : components) {
			if (com instanceof BasicComponent) {
				BasicComponent basicCom = (BasicComponent) com;
				for (var seff : basicCom.getServiceEffectSpecifications__BasicComponent()) {
					if (seff.getDescribedService__SEFF() instanceof OperationSignature) {
						OperationSignature sign = (OperationSignature) seff.getDescribedService__SEFF();
						UsageScenario us = UsagemodelFactory.eINSTANCE.createUsageScenario();
						us.setEntityName("Scenario" + counter);
						ScenarioBehaviour behaviour = UsagemodelFactory.eINSTANCE.createScenarioBehaviour();
						behaviour.setEntityName("Scenario" + counter + "Behaviour");
						Start start = UsagemodelFactory.eINSTANCE.createStart();
						Stop stop = UsagemodelFactory.eINSTANCE.createStop();
						EntryLevelSystemCall call = UsagemodelFactory.eINSTANCE.createEntryLevelSystemCall();
						call.setOperationSignature__EntryLevelSystemCall(sign);
						for (var role : systemRoles) {
							if (role instanceof OperationProvidedRole) {
								OperationProvidedRole provRole = (OperationProvidedRole) role;
								if (provRole.getProvidedInterface__OperationProvidedRole() == sign.getInterface__OperationSignature()) {
									call.setProvidedRole_EntryLevelSystemCall(provRole);
									break;
								}
							}
						}
						call.setPredecessor(start);
						call.setSuccessor(stop);
						behaviour.getActions_ScenarioBehaviour().add(start);
						behaviour.getActions_ScenarioBehaviour().add(call);
						behaviour.getActions_ScenarioBehaviour().add(stop);
						us.setScenarioBehaviour_UsageScenario(behaviour);
						PCMRandomVariable vari = CoreFactory.eINSTANCE.createPCMRandomVariable();
						vari.setSpecification("2000");
						OpenWorkload work = UsagemodelFactory.eINSTANCE.createOpenWorkload();
						work.setInterArrivalTime_OpenWorkload(vari);
						us.setWorkload_UsageScenario(work);
						usageModel.getUsageScenario_UsageModel().add(us);
						counter++;
					}
				}
			}
		}
	}
	
	private void storePCM() {
		LocalFilesystemPCM localPCM = new LocalFilesystemPCM();
		File deployDir = new File(CommitIntegrationSettingsContainer.getSettingsContainer().getProperty(SettingKeys.DEPLOYMENT_PATH));
		deployDir = deployDir.getAbsoluteFile();
		localPCM.setAllocationModelFile(new File(deployDir, "Allocation.allocation"));
		localPCM.setRepositoryFile(new File(deployDir, "Repository.repository"));
		localPCM.setResourceEnvironmentFile(new File(deployDir, "ResourceEnvironment.resourceenvironment"));
		localPCM.setSystemFile(new File(deployDir, "System.system"));
		localPCM.setUsageModelFile(new File(deployDir, "Usage.usagemodel"));
		preparedPCM.saveToFilesystem(localPCM);
	}
}
