package testRepository.SearchingAlgo;

import testRepository.contracts.ISearchingAlgo;

public class SearchingAlgoImpl implements ISearchingAlgo {
	 public boolean binarySearch (int[] arr, int value) {
try {
			if (arr.length == 0) {
	            return false;
	        }
	        int low = 0;
	        int high = arr.length-1;

	        while(low <= high ) {
	            int middle = (low+high) /2;
	            if (value > arr[middle] ){
	                low = middle +1;
	            } else if (value < arr[middle]){
	                high = middle -1;
	            } else { 
	                return true;
	            }
	        }
	        return false;
		}
		finally {
			
		}	}

	 public boolean sequentialSearch (int[] arr, int value) {
try {
			 for (int i = 0; i<arr.length; i++) {
		            if (arr[i] == value){
		                return true;
		            }
		        }
		        return false;
		}
		finally{
			
		}	}
 }