package testRepository.WebGUI;

import testRepository.contracts.IWebGUI;
import testRepository.contracts.IMediaStore;

public class WebGUIImpl implements IWebGUI {private IMediaStore iMediaStore;
	public void httpDownload () {

for(int i=0;i<10;i++){
iMediaStore.download();
}
	}

	public void httpUpload () {
	}
 }