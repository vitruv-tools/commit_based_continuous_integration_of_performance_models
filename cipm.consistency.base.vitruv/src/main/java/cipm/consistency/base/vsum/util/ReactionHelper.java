package cipm.consistency.base.vsum.util;

import org.palladiosimulator.pcm.resourcetype.CommunicationLinkResourceType;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
import org.palladiosimulator.pcm.resourcetype.SchedulingPolicy;

import cipm.consistency.base.shared.pcm.util.PCMUtils;

public class ReactionHelper {
	private static final ResourceRepository DEFAULT_RESOURCE_REPO = PCMUtils.getDefaultResourceRepository();
	private static final ProcessingResourceType CPU_PROC_TYPE = PCMUtils.getElementById(DEFAULT_RESOURCE_REPO,
			ProcessingResourceType.class, "_oro4gG3fEdy4YaaT-RYrLQ");
	private static final SchedulingPolicy CPU_SHARING_POLICY = PCMUtils.getElementById(DEFAULT_RESOURCE_REPO,
			SchedulingPolicy.class, "ProcessorSharing");
	private static final CommunicationLinkResourceType LAN_RES_TYPE = PCMUtils.getElementById(DEFAULT_RESOURCE_REPO,
			CommunicationLinkResourceType.class, "_o3sScH2AEdyH8uerKnHYug");

	public static CommunicationLinkResourceType getLANCommunicationResourceType() {
		return LAN_RES_TYPE;
	}

	public static ProcessingResourceType getCPUProcessingResourceType() {
		return CPU_PROC_TYPE;
	}

	public static SchedulingPolicy getProcessSharingSchedulingPolicy() {
		return CPU_SHARING_POLICY;
	}
}
