package cipm.consistency.base.vsum.manager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionChangeRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.ISpecificModelProvider;
import cipm.consistency.base.core.health.AbstractHealthStateComponent;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.core.health.HealthStateProblemSeverity;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.vsum.domains.java.JavaCorrespondenceModelImpl;
import cipm.consistency.base.vsum.mapping.VsumMappingPersistence;
import lombok.Getter;
import lombok.extern.java.Log;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.extensions.dslsruntime.reactions.helper.ReactionsCorrespondenceHelper;
import tools.vitruv.framework.change.description.VitruviusChangeFactory;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.TypeInferringAtomicEChangeFactory;
import tools.vitruv.framework.change.echange.TypeInferringUnresolvingAtomicEChangeFactory;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.Correspondences;
import tools.vitruv.framework.correspondence.impl.GenericCorrespondenceModelViewImpl;
import tools.vitruv.framework.correspondence.impl.InternalCorrespondenceModelImpl;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;
import tools.vitruv.framework.domains.repository.VitruvDomainRepository;
import tools.vitruv.framework.tuid.TuidResolver;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.util.bridges.EMFBridge;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.uuid.UuidGeneratorAndResolver;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.VirtualModelConfiguration;
import tools.vitruv.framework.vsum.VirtualModelImpl;
import tools.vitruv.framework.vsum.repositories.ModelRepositoryImpl;

@Component
@Log
public class VsumManager extends AbstractHealthStateComponent {
	@Autowired
	private List<VitruvDomainProvider<? extends VitruvDomain>> domainProviders;

	@Autowired
	private List<AbstractReactionsChangePropagationSpecification> reactions;

	@Autowired
	private IPcmModelProvider pcmModelContainer;

	@Autowired
	private ISpecificModelProvider specificModelContainer;

	@Autowired
	private VsumMappingPersistence persistence;

	private InternalVirtualModel vsum;

	@Getter
	private TypeInferringAtomicEChangeFactory atomicFactory;

	@Getter
	private JavaCorrespondenceModelImpl javaCorrespondences;

	@Getter
	private VitruvDomainRepository domainRepository;

	@Getter
	private UuidGeneratorAndResolver uuidGeneratorAndResolver;

	@Getter
	private TuidResolver tuidResolver;

	@Getter
	private Map<String, List<EObject>> fileExtensionPathMapping;

	// pure internal
	private List<File> usedFiles;
	private Map<VsumChangeSource, VURI> vuriMapping;
	private Set<String> currentCorrespondences;
	private ModelRepositoryImpl internalModelRepository;

	public VsumManager() {
		super(HealthStateObservedComponent.VSUM_MANAGER, HealthStateObservedComponent.MODEL_MANAGER);
		this.usedFiles = new ArrayList<>();
		this.vuriMapping = Maps.newHashMap();
		this.currentCorrespondences = Sets.newHashSet();
		this.fileExtensionPathMapping = Maps.newHashMap();
	}

	public void executeTransaction(Callable<Void> cb) {
		vsum.executeCommand(cb);
	}

	public void propagateCompositeChanges(List<EChange> changes, VsumChangeSource source) {
		this.vsum.propagateChange(VitruviusChangeFactory.getInstance().createCompositeChange(changes.stream().map(c -> {
			return VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(c, vuriMapping.get(source));
		}).collect(Collectors.toList())));

		// clean up memory leaks
		this.flushHistories();
		this.internalModelRepository.cleanupRootElements();
	}

	public void propagateChange(EChange change, VsumChangeSource source) {
		this.vsum.propagateChange(
				VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(change, vuriMapping.get(source)));

		// clean up memory leaks
		this.flushHistories();
		this.internalModelRepository.cleanupRootElements();
	}

	public CorrespondenceModel getCorrespondenceModel() {
		if (vsum != null) {
			return vsum.getCorrespondenceModel();
		}
		return null;
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		if (source == HealthStateObservedComponent.MODEL_MANAGER && state == HealthState.WORKING) {
			this.buildVSUM();
		}
	}

	private void flushHistories() {
		this.flushHistory(pcmModelContainer.getRepository());
		this.flushHistory(pcmModelContainer.getResourceEnvironment());
		this.flushHistory(specificModelContainer.getRuntimeEnvironment());
		this.flushHistory(specificModelContainer.getInstrumentation());
		if (vsum.getCorrespondenceModel().getAllCorrespondences().size() > 0) {
			this.flushHistory(vsum.getCorrespondenceModel().getAllCorrespondences().get(0).getParent());
		}
	}

	private void flushHistory(EObject obj) {
		this.flushListeners(obj);
		this.flushListeners(obj.eResource());
	}

	private void flushListeners(Notifier obj) {
		if (obj != null) {
			obj.eAdapters().forEach(adapter -> {
				if (adapter instanceof TransactionChangeRecorder) {
					this.flushListener((TransactionChangeRecorder) adapter);
				}
			});
		}
	}

	private void flushListener(TransactionChangeRecorder changeRecorder) {
		InternalTransactionalEditingDomain domain = changeRecorder.getEditingDomain();
		domain.getCommandStack().flush();
	}

	@PostConstruct
	private void buildVSUM() {
		if (!super.checkPreconditions()) {
			return;
		}

		tearDownVSUM();

		VirtualModelConfiguration vsumConfig = new VirtualModelConfiguration();

		for (VitruvDomainProvider<? extends VitruvDomain> provider : domainProviders) {
			vsumConfig.addMetamodel(provider.getDomain());
		}
		for (AbstractReactionsChangePropagationSpecification reaction : reactions) {
			vsumConfig.addChangePropagationSpecification(reaction);
		}

		File vsumFolder = Files.createTempDir();
		vsumFolder.deleteOnExit();
		usedFiles.add(vsumFolder);

		this.vsum = new VirtualModelImpl(vsumFolder, UserInteractionFactory.instance.createDummyUserInteractor(),
				vsumConfig);

		// load
		loadModels();
		recoverMapping();
		finalizeVsum();

		super.updateState();
	}

	private void finalizeVsum() {
		atomicFactory = new TypeInferringUnresolvingAtomicEChangeFactory(vsum.getUuidGeneratorAndResolver());
		uuidGeneratorAndResolver = vsum.getUuidGeneratorAndResolver();
		updateCorrespondenceChecksums();

		Field domainRepositoryField = FieldUtils.getField(VirtualModelImpl.class, "metamodelRepository", true);
		Field tuidResolverField = FieldUtils.getField(InternalCorrespondenceModelImpl.class, "tuidResolver", true);
		Field cpmDelegate = FieldUtils.getField(GenericCorrespondenceModelViewImpl.class, "correspondenceModelDelegate",
				true);
		Field modelRepositoryField = FieldUtils.getField(VirtualModelImpl.class, "modelRepository", true);

		try {
			Object repository = domainRepositoryField.get(vsum);
			Object delegate = cpmDelegate.get(vsum.getCorrespondenceModel());
			Object resolver = tuidResolverField.get(delegate);
			Object modelRepository = modelRepositoryField.get(vsum);

			if (repository instanceof VitruvDomainRepository && resolver instanceof TuidResolver
					&& modelRepository instanceof ModelRepositoryImpl) {
				this.domainRepository = (VitruvDomainRepository) repository;
				this.tuidResolver = (TuidResolver) resolver;
				this.internalModelRepository = (ModelRepositoryImpl) modelRepository;
				super.removeAllProblems();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.warning("Failed to get fields of the VSUM via reflection.");
			super.reportProblem("Unable to access the fields of the VSUM.", HealthStateProblemSeverity.WARNING);
		}

		vsum.save();
	}

	private void tearDownVSUM() {
		vuriMapping.clear();
		fileExtensionPathMapping.clear();

		if (vsum != null) {
			log.info("Tear down current VSUM.");
			// remove files
			try {
				FileUtils.deleteDirectory(vsum.getFolder());
			} catch (IOException e) {
				log.warning("Could not tear down VSUM successfully.");
			}

			usedFiles.forEach(f -> {
				if (f.exists()) {
					if (f.isDirectory()) {
						try {
							FileUtils.deleteDirectory(f);
						} catch (IOException e) {
							log.warning("Could not remove all temporary used files.");
						}
					} else {
						f.delete();
					}
				}
			});
		}
	}

	private void loadModels() {
		// 1. load repository
		persistVirtual(pcmModelContainer.getRepository(), VsumConstants.REPOSITORY_SUFFIX, VsumChangeSource.REPOSITORY);

		// 2. load resource environment
		persistVirtual(pcmModelContainer.getResourceEnvironment(), VsumConstants.RESOURCEENVIRONMENT_SUFFIX,
				VsumChangeSource.RESURCE_ENVIRONMENT);

		// 3. load runtime environment
		persistVirtual(specificModelContainer.getRuntimeEnvironment(), VsumConstants.RUNTIMEENVIRONMENT_SUFFIX,
				VsumChangeSource.RUNTIME_ENVIRONMENT);

		// 4. load instrumentation model
		persistVirtual(specificModelContainer.getInstrumentation(), VsumConstants.INSTRUMENTATION_SUFFIX,
				VsumChangeSource.INSTRUMENTATION_MODEL);
	}

	private void recoverMapping() {
		// map parents accordingly
		vsum.executeCommand(() -> {
			ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(),
					pcmModelContainer.getRepository(), specificModelContainer.getInstrumentation(), null);

			ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(),
					pcmModelContainer.getResourceEnvironment(), specificModelContainer.getRuntimeEnvironment(), null);

			ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(),
					specificModelContainer.getRuntimeEnvironment(), pcmModelContainer.getResourceEnvironment(), null);

			return null;
		});

		// init java correspondence
		javaCorrespondences = new JavaCorrespondenceModelImpl();

		// reload the mapping from files
		persistence.recoverCorresondences(vsum.getCorrespondenceModel(), specificModelContainer.getCorrespondences(),
				javaCorrespondences);

		// register events
		javaCorrespondences.addListener(v -> persistMapping());
		vsum.addPropagatedChangeListener((a, b, c, d) -> checkCorrespondenceChanges());
	}

	private void checkCorrespondenceChanges() {
		if (correspondenceChecksumsChanged()) {
			persistMapping();
			updateCorrespondenceChecksums();
		}
	}

	private boolean correspondenceChecksumsChanged() {
		if (this.currentCorrespondences.size() == vsum.getCorrespondenceModel().getAllCorrespondences().size()) {
			return vsum.getCorrespondenceModel().getAllCorrespondences().stream().map(c -> getCorrespondenceChecksum(c))
					.anyMatch(checksum -> !currentCorrespondences.contains(checksum));
		} else {
			return true;
		}
	}

	private void updateCorrespondenceChecksums() {
		this.currentCorrespondences.clear();
		vsum.getCorrespondenceModel().getAllCorrespondences().forEach(c -> {
			this.currentCorrespondences.add(getCorrespondenceChecksum(c));
		});
	}

	private String getCorrespondenceChecksum(Correspondence cp) {
		StringBuilder builder = new StringBuilder();
		cp.getATuids().forEach(a -> builder.append("#" + a.toString()));
		cp.getAUuids().forEach(a -> builder.append("#" + a));
		builder.append("||");
		cp.getBTuids().forEach(b -> builder.append("#" + b.toString()));
		cp.getBUuids().forEach(b -> builder.append("#" + b));

		return DigestUtils.sha256Hex(builder.toString());
	}

	private void persistMapping() {
		// build persistables
		Correspondences storable = persistence.buildStorableCorrespondeces(vsum.getCorrespondenceModel(),
				javaCorrespondences);

		// set the correspondences which leads to a flush to the file
		persistence.setCorrespondences(specificModelContainer.getCorrespondences(), storable);
	}

	private synchronized void persistVirtual(EObject model, String suffix, VsumChangeSource source) {
		// backup resource
		Resource backupResource = model.eResource();

		File temp;
		try {
			temp = File.createTempFile("model_vsum", suffix);
		} catch (IOException e) {
			log.severe("Failed to create temporary files for the VSUM models.");
			return;
		}

		// map to extension
		String fileExtension = suffix.substring(1);
		if (fileExtensionPathMapping.containsKey(fileExtension)) {
			fileExtensionPathMapping.get(fileExtension).add(model);
		} else {
			fileExtensionPathMapping.put(fileExtension, Lists.newArrayList(model));
		}

		// write the model to the file
		ModelUtil.saveToFile(model, temp);

		// persist
		VURI vuri = VURI.getInstance(EMFBridge.getEmfFileUriForFile(temp));
		vsum.persistRootElement(vuri, model);
		vuriMapping.put(source, vuri);
		generateUuids(model);

		// recover resource
		vsum.executeCommand(() -> {
			if (backupResource != null) {
				backupResource.getContents().add(model);
			}
			return null;
		});
	}

	private void generateUuids(EObject parent) {
		ModelUtil.getObjects(parent, EObject.class).stream().forEach(el -> {
			if (parent != el) {
				if (!vsum.getUuidGeneratorAndResolver().hasUuid(el)) {
					vsum.getUuidGeneratorAndResolver().generateUuid(el);
				}
			}
		});
	}

	public enum VsumChangeSource {
		RUNTIME_ENVIRONMENT, INSTRUMENTATION_MODEL, REPOSITORY, RESURCE_ENVIRONMENT
	}

}
