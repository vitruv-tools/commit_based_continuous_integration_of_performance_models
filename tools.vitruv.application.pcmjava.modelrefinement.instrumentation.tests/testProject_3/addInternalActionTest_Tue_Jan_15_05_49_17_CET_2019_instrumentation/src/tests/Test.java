package tests;

import testRepository.WebGUI.WebGUIImpl;
import testRepository.contracts.IWebGUI;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
         IWebGUI test = new WebGUIImpl();
         for(int i = 0;i<20; i++) {
        	 test.httpDownload();
         }
	}

}
