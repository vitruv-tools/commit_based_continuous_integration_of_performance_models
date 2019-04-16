package testRepository.GuiSearchingAlgo;

import testRepository.contracts.IGuiSearchingAlgo;
import testRepository.contracts.ISearchingAlgo;

public class GuiSearchingAlgoImpl implements IGuiSearchingAlgo {private String addValue(String searchResult, int value) { return searchResult + ", " + value;

}private ISearchingAlgo iSearchingAlgo;
	 public String guiBinarySearch (int[] arr, int[] values) {
	String searchResult = "";
		if (values.length != 0) {
			for (int i = 0; i < values.length; i++) {
				boolean isFound = iSearchingAlgo.binarySearch(arr, values[i]);
				if (isFound) {
					searchResult = addValue(searchResult, values[i]);
				}
			}
		} 		return searchResult;	}

	 public String guiSequentialSearch (int[] arr, int[] values) {
	}
 }