package com.example.hfh5Helper;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class DEncodingUtils {
	public static String encoding(String str, String charsetName) {
		try {
			str = URLEncoder.encode(str, charsetName);
		} catch(Exception e) {
		}
		return str;
	}
	
	public static String decoding(String str, String charsetName) {
		try {
			str = URLDecoder.decode(str, charsetName);
		} catch(Exception e) {
		}
		return str;
	}
}
