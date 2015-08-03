package com.beautifulyears;

import com.beautifulyears.constants.DiscussConstants;

public class Util {

	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}
	
	public static String truncateText(String text){
		if(text.length() > DiscussConstants.DISCUSS_TRUNCATION_LENGTH){
			int max = DiscussConstants.DISCUSS_TRUNCATION_LENGTH;
			int end = text.lastIndexOf(' ', max - 3);

		    // Just one long word. Chop it off.
		    if (end == -1){
		    	text = text.substring(0, max-3) + "...";
		    }
		    else{
		    	text = text.substring(0, end) + "...";
		    }
		}
		return text;
	}
}
