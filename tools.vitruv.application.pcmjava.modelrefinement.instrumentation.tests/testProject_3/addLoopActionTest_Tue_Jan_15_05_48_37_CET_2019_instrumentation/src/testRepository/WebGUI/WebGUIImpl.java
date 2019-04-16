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
		iMediaStore = new MediaStoreImpl();
	}
	
	public void httpDownload() {
		ServiceParamtersFactory serviceParamtersFactory = new ServiceParametersFactoryImp();

		ServiceParameters __serviceParametersd846225b_34c1_4d90_8a23_8f37c92bc7c1 = serviceParamtersFactory
				.getServiceParameters(new Object[] {});

		ThreadMonitoringController.getInstance().enterService("_5P1ZQBiAEemhHsLMcQrn0A",
				__serviceParametersd846225b_34c1_4d90_8a23_8f37c92bc7c1);
		long __counter_bf0b4011_c157_46bc_8a49_a587312e706e = 0;

		for (int i = 0; i < 10; i++) {
			__counter_bf0b4011_c157_46bc_8a49_a587312e706e++;
			ThreadMonitoringController.getInstance().setCurrentCallerId("_52LnkBiAEemhHsLMcQrn0A");
			iMediaStore.download();
		}
		ThreadMonitoringController.getInstance().logLoopIterationCount("_52B2kBiAEemhHsLMcQrn0A",
				__counter_bf0b4011_c157_46bc_8a49_a587312e706e);
		ThreadMonitoringController.getInstance().exitService();
	}

	public void httpUpload() {
	}
}