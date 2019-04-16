package testRepository.WebGUI;

import testRepository.contracts.IWebGUI;
import testRepository.contracts.IMediaStore;

public class WebGUIImpl implements IWebGUI {
	private IMediaStore iMediaStore;

	public void httpDownload() {

		ServiceParamtersFactory serviceParamtersFactory = new ServiceParametersFactoryImp();

		ServiceParameters __serviceParametersee84e6fc_8099_4a09_afaf_50e97521cd95 = serviceParamtersFactory
				.getServiceParameters(new Object[] {});

		ThreadMonitoringController.getInstance().enterService("__yjfYBiAEemhHsLMcQrn0A",
				__serviceParametersee84e6fc_8099_4a09_afaf_50e97521cd95);
		String __executedBranch_276a4f2e_f7e8_4c15_ab5d_213b9c501003 = null;

		if (i < 10) {
			__executedBranch_276a4f2e_f7e8_4c15_ab5d_213b9c501003 = "_ASlqUBiBEemhHsLMcQrn0A";
			ThreadMonitoringController.setCurrentCallerId("_ASm4chiBEemhHsLMcQrn0A");
			iMediaStore.download();
		}
		ThreadMonitoringController.getInstance().logBranchExecution("_ASj1IBiBEemhHsLMcQrn0A",
				__executedBranch_276a4f2e_f7e8_4c15_ab5d_213b9c501003);
		ThreadMonitoringController.getInstance().exitService();
	}

	public void httpUpload() {
	}
}