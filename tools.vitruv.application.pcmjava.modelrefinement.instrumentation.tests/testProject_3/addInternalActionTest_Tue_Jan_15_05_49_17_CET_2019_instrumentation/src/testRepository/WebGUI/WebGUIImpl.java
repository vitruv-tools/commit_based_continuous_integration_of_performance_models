package testRepository.WebGUI;

import testRepository.contracts.IWebGUI;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.ServiceParameters;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.ServiceParametersFactoryImp;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.ServiceParamtersFactory;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.ThreadMonitoringController;
import testRepository.MediaStore.MediaStoreImpl;
import testRepository.contracts.IMediaStore;

public class WebGUIImpl implements IWebGUI {
	private IMediaStore iMediaStore;
	
	public WebGUIImpl() {
		iMediaStore =  new MediaStoreImpl();
	}

	public void httpDownload() {

		ServiceParamtersFactory serviceParamtersFactory = new ServiceParametersFactoryImp();

		ServiceParameters __serviceParameters0faf2d88_52db_48c1_b3fc_b1192e1dad08 = serviceParamtersFactory
				.getServiceParameters(new Object[] {});

		ThreadMonitoringController.getInstance().enterService("_88LvMBiAEemhHsLMcQrn0A",
				__serviceParameters0faf2d88_52db_48c1_b3fc_b1192e1dad08);

		final long __tin_9ca71a9d_7132_4d71_8ceb_b54a7d1cf7be = ThreadMonitoringController.getInstance().getTime();
		final int i = 5;
		ThreadMonitoringController.getInstance().logResponseTime("_9iObgBiAEemhHsLMcQrn0A", "_9iSs8BiAEemhHsLMcQrn0A",
				__tin_9ca71a9d_7132_4d71_8ceb_b54a7d1cf7be);
		ThreadMonitoringController.getInstance().exitService();
	}

	public void httpUpload() {
	}
}