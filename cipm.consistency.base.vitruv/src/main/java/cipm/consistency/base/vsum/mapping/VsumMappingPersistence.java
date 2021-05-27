package cipm.consistency.base.vsum.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.eclipse.emf.ecore.EObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cipm.consistency.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import cipm.consistency.base.vsum.manager.VsumManager;
import lombok.extern.java.Log;
import tools.vitruv.extensions.dslsruntime.reactions.helper.ReactionsCorrespondenceHelper;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceFactory;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.Correspondences;
import tools.vitruv.framework.domains.AbstractTuidAwareVitruvDomain;
import tools.vitruv.framework.domains.TuidAwareVitruvDomain;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;
import tools.vitruv.framework.domains.repository.VitruvDomainRepository;
import tools.vitruv.framework.tuid.HierarchicalTuidCalculatorAndResolver;
import tools.vitruv.framework.tuid.Tuid;
import tools.vitruv.framework.tuid.TuidCalculatorAndResolver;
import tools.vitruv.framework.util.VitruviusConstants;
import tools.vitruv.framework.uuid.UuidGeneratorAndResolver;

@Service
@Log
// REVIEW AND MAYBE REFACTOR
public class VsumMappingPersistence {
	private static final String JAVA_PREFIX = "java:";

	private static final String BRANCH_PREFIX = "branch:";
	private static final String LOOP_PREFIX = "loop:";
	private static final String EXTERNAL_CALL_PREFIX = "call:";
	private static final String SEFF_PREFIX = "method:";
	private static final String INTERNAL_PREFIX = "statement:";

	private static final String FILE_PREFIX = "file:";
	private static final String TUID_DELEMITER = VitruviusConstants.getTuidSegmentSeperator();

	@Autowired
	private VsumManager vsumManager;

	@Autowired
	private List<VitruvDomainProvider<? extends VitruvDomain>> domainProviders;

	public void recoverCorresondences(CorrespondenceModel correspondenceModel, Correspondences correspondences,
			IJavaPCMCorrespondenceModel javaCorrespondence) {
		int correspondencesBefore = correspondenceModel.getAllCorrespondences().size();
		for (Correspondence correspondence : correspondences.getCorrespondences()) {
			if (correspondence.getAUuids().size() > 0 && correspondence.getBUuids().size() > 0) {
				boolean isJavaCorrespondence = correspondence.getAUuids().stream()
						.allMatch(a -> a.startsWith(JAVA_PREFIX));
				if (isJavaCorrespondence) {
					recoverJavaCorrespondence(correspondence, javaCorrespondence);
				} else {
					recoverCommonCorrespondence(correspondence, correspondenceModel);
				}
			}
		}

		log.info("Recovered " + (correspondenceModel.getAllCorrespondences().size() - correspondencesBefore)
				+ " mapping(s) from file.");
	}

	public Correspondences buildStorableCorrespondeces(CorrespondenceModel model,
			IJavaPCMCorrespondenceModel javaCorrespondence) {
		Correspondences output = CorrespondenceFactory.eINSTANCE.createCorrespondences();

		// common correspondences
		model.getAllCorrespondences().forEach(cp -> {
			boolean hasAllA = cp.getAUuids().stream()
					.allMatch(a -> vsumManager.getUuidGeneratorAndResolver().hasEObject(a));
			boolean hasAllB = cp.getBUuids().stream()
					.allMatch(b -> vsumManager.getUuidGeneratorAndResolver().hasEObject(b));

			if (hasAllA && hasAllB) {
				List<Tuid> aTuids = this.calculateTuidsFromUuids(cp.getAUuids(),
						vsumManager.getUuidGeneratorAndResolver(), vsumManager.getDomainRepository());
				List<Tuid> bTuids = this.calculateTuidsFromUuids(cp.getBUuids(),
						vsumManager.getUuidGeneratorAndResolver(), vsumManager.getDomainRepository());

				createCorrespondenceTuid(output, aTuids, bTuids, cp.getTag());
			}
		});

		// java correspondences
		javaCorrespondence.getBranchCorrespondences().forEach(b -> {
			createCorrespondence(output, Lists.newArrayList(JAVA_PREFIX + BRANCH_PREFIX + b.getLeft()),
					Lists.newArrayList(b.getRight()), "");
		});

		javaCorrespondence.getExternalCallCorrespondences().forEach(e -> {
			createCorrespondence(output, Lists.newArrayList(JAVA_PREFIX + EXTERNAL_CALL_PREFIX + e.getLeft()),
					Lists.newArrayList(e.getRight()), "");
		});

		javaCorrespondence.getLoopCorrespondences().forEach(l -> {
			createCorrespondence(output, Lists.newArrayList(JAVA_PREFIX + LOOP_PREFIX + l.getLeft()),
					Lists.newArrayList(l.getRight()), "");
		});

		javaCorrespondence.getSeffCorrespondences().forEach(s -> {
			createCorrespondence(output, Lists.newArrayList(JAVA_PREFIX + SEFF_PREFIX + s.getLeft()),
					Lists.newArrayList(s.getRight()), "");
		});

		javaCorrespondence.getInternalActionCorrespondences().forEach(i -> {
			createCorrespondence(output,
					Lists.newArrayList(JAVA_PREFIX + INTERNAL_PREFIX + i.getLeft().getLeft(),
							JAVA_PREFIX + INTERNAL_PREFIX + i.getLeft().getRight()),
					Lists.newArrayList(i.getRight()), "");
		});

		return output;
	}

	public void integrateCorrespondences(Correspondences parent, Correspondences child) {
		List<Correspondence> copiedReferences = new ArrayList<>(child.getCorrespondences());
		for (Correspondence c : copiedReferences) {
			parent.getCorrespondences().add(c);
		}
	}

	public void setCorrespondences(Correspondences parent, Correspondences child) {
		parent.getCorrespondences().clear();
		integrateCorrespondences(parent, child);
	}

	private void createCorrespondence(Correspondences output, List<String> aUids, List<String> bUids, String tag) {
		Correspondence copy = CorrespondenceFactory.eINSTANCE.createManualCorrespondence();
		copy.getAUuids().addAll(aUids);
		copy.getBUuids().addAll(bUids);
		copy.setTag(tag);

		output.getCorrespondences().add(copy);
	}

	private void createCorrespondenceTuid(Correspondences output, List<Tuid> aTuids, List<Tuid> bTuids, String tag) {
		Correspondence copy = CorrespondenceFactory.eINSTANCE.createManualCorrespondence();
		copy.getAUuids().addAll(aTuids.stream().map(a -> a.toString()).collect(Collectors.toList()));
		copy.getBUuids().addAll(bTuids.stream().map(b -> b.toString()).collect(Collectors.toList()));
		copy.setTag(tag);

		output.getCorrespondences().add(copy);
	}

	private void recoverCommonCorrespondence(Correspondence correspondence, CorrespondenceModel correspondenceModel) {
		if (correspondence.getAUuids().size() > 0 && correspondence.getBUuids().size() > 0) {
			List<EObject> resolvedAs = correspondence.getAUuids().stream().map(a -> this.resolveObjectFromTuid(a))
					.collect(Collectors.toList());
			List<EObject> resolvedBs = correspondence.getBUuids().stream().map(b -> this.resolveObjectFromTuid(b))
					.collect(Collectors.toList());

			boolean noNulls = resolvedAs.stream().allMatch(a -> a != null)
					&& resolvedBs.stream().allMatch(b -> b != null);
			if (noNulls) {
				for (EObject aObj : resolvedAs) {
					for (EObject bObj : resolvedBs) {
						vsumManager.executeTransaction(() -> {
							ReactionsCorrespondenceHelper.addCorrespondence(correspondenceModel, aObj, bObj,
									correspondence.getTag());
							return null;
						});
					}
				}
			}
		}
	}

	private void recoverJavaCorrespondence(Correspondence correspondence,
			IJavaPCMCorrespondenceModel javaCorrespondence) {
		List<String> trimmedAs = correspondence.getAUuids().stream().map(a -> a.substring(JAVA_PREFIX.length()))
				.collect(Collectors.toList());

		if (trimmedAs.size() == 2 && correspondence.getBUuids().size() == 1) {
			// internal action
			if (trimmedAs.stream().allMatch(a -> a.startsWith(INTERNAL_PREFIX))) {
				List<String> innerTrimmed = trimmedAs.stream().map(a -> a.substring(INTERNAL_PREFIX.length()))
						.collect(Collectors.toList());
				javaCorrespondence.addInternalActionCorrespondence(innerTrimmed.get(0), innerTrimmed.get(1),
						correspondence.getBUuids().get(0));
			}
		} else if (trimmedAs.size() == 1 && correspondence.getBUuids().size() == 1) {
			// all other
			String innerA = trimmedAs.get(0);
			String innerB = correspondence.getBUuids().get(0);

			if (innerA.startsWith(LOOP_PREFIX)) {
				javaCorrespondence.addLoopCorrespondence(innerA.substring(LOOP_PREFIX.length()), innerB);
			} else if (innerA.startsWith(BRANCH_PREFIX)) {
				javaCorrespondence.addBranchCorrespondence(innerA.substring(BRANCH_PREFIX.length()), innerB);
			} else if (innerA.startsWith(EXTERNAL_CALL_PREFIX)) {
				javaCorrespondence.addExternalCallCorrespondence(innerA.substring(EXTERNAL_CALL_PREFIX.length()),
						innerB);
			} else if (innerA.startsWith(SEFF_PREFIX)) {
				javaCorrespondence.addSeffCorrespondence(innerA.substring(SEFF_PREFIX.length()), innerB);
			}
		}

	}

	private EObject resolveObjectFromTuid(String tuidString) {
		String[] splitUuid = tuidString.split(TUID_DELEMITER);
		String fileExtension = FilenameUtils.getExtension(splitUuid[1].substring(FILE_PREFIX.length()));
		String[] idSegment = new String[splitUuid.length - 3];
		System.arraycopy(splitUuid, 3, idSegment, 0, splitUuid.length - 3);

		List<EObject> possibleFiles = vsumManager.getFileExtensionPathMapping().get(fileExtension);

		if (possibleFiles != null) {
			for (VitruvDomainProvider<? extends VitruvDomain> domain : domainProviders) {
				if (domain.getDomain() instanceof TuidAwareVitruvDomain) {
					TuidAwareVitruvDomain typedDomain = (TuidAwareVitruvDomain) domain.getDomain();
					if (typedDomain.getNsUris().contains(splitUuid[0])) {
						TuidCalculatorAndResolver resolver = getTuidCalculatorAndResolver(typedDomain);

						if (resolver != null && resolver instanceof HierarchicalTuidCalculatorAndResolver<?>) {
							for (EObject possibleFile : possibleFiles) {
								try {
									EObject resolved = getIdentifiedEObjectWithinRoot(
											(HierarchicalTuidCalculatorAndResolver<?>) resolver, possibleFile,
											idSegment);
									if (resolved != null) {
										return resolved;
									}
								} catch (Exception e) {
									// nothing to do here
									log.fine("Failed to resolve a TUID in a specific model.");
								}
							}
						}
					}
				}
			}
		}

		log.warning("Failed to resolve element with TUID '" + tuidString + "'. Some mappings may be corrupted!");

		return null;
	}

	private EObject getIdentifiedEObjectWithinRoot(HierarchicalTuidCalculatorAndResolver<?> resolver, EObject root,
			String[] idSegment) {
		try {
			EObject result = (EObject) MethodUtils.invokeMethod(resolver, true,
					"getIdentifiedEObjectWithinRootEObjectInternal", root, idSegment);
			return result;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return null;
		}
	}

	private TuidCalculatorAndResolver getTuidCalculatorAndResolver(TuidAwareVitruvDomain typedDomain) {
		Field resolverField = FieldUtils.getDeclaredField(AbstractTuidAwareVitruvDomain.class,
				"tuidCalculatorAndResolver", true);
		try {
			return (TuidCalculatorAndResolver) resolverField.get(typedDomain);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}

	private List<Tuid> calculateTuidsFromUuids(List<String> uuids, UuidGeneratorAndResolver resolver,
			VitruvDomainRepository repository) {
		return uuids.stream().map(u -> resolver.getEObject(u)).map(e -> calculateTuidFromEObject(e, repository))
				.collect(Collectors.toList());
	}

	private Tuid calculateTuidFromEObject(EObject eObject, VitruvDomainRepository repository) {
		VitruvDomain metamodel = repository.getDomain(eObject);
		if (null == metamodel || !(metamodel instanceof TuidAwareVitruvDomain)) {
			return null;
		}
		TuidAwareVitruvDomain typedDomain = (TuidAwareVitruvDomain) metamodel;
		return typedDomain.calculateTuid(eObject);
	}

}
