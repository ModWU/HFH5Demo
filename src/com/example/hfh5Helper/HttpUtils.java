package com.example.hfh5Helper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
	private static OkHttpClient mOkHttpClient;
	
	static {
		try {
			if(mOkHttpClient == null)
				mOkHttpClient = new OkHttpClient();
		} catch(Exception e) {
				TAGS.log("HttpUtils-->static->ex:" + e.toString());
		} catch (NoClassDefFoundError e) {
				TAGS.log("HttpUtils-->static->NoClassDefFoundError:" + e.toString());
		} catch (Error e) {
				TAGS.log("HttpUtils-->static->Error:" + e.toString());
		}
	}
	
	/*public static String sendPostUTF8(String url, Map<String, String> params) {
		String result = null;
		try {
			TAGS.log("HttpUtils-->sendPostUTF8->url: " + url);
			TAGS.log("HttpUtils-->sendPostUTF8->params: " + params.toString());
			Response response = DYHttpUtils.getInstance().post().url(url).paramJSONType("utf-8").params(params).build().execute();
			
			if(response.isSuccessful()) {
				result = DEncodingUtils.decoding(response.body().string(), "utf-8");
			} else {
				TAGS.log("HttpUtils-->sendPostUTF8: request is not Successful!");
			}
		} catch (Exception e) {
			TAGS.log("HttpUtils--sendPostUTF8->ex:" + e.toString());
		}
		return result;
		
	}*/
	
	public static String sendPostUTF8(String url, Map<String, String> params) {
		return sendPostUTF8(url, params, true);
	}
	
	public static String sendPostUTF8(String url, Map<String, String> params, boolean isUtf8Decode) {
		String data = handleToJson(params);
		return sendPostDataUTF8(url, data, isUtf8Decode);
	}
	
	public static String handleToJson(Map<String, String> params) {
		if(params == null || params.size() <= 0) return "";
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			buffer.append("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append("}");
		return buffer.toString();
	}

	public static String sendPostDataUTF8(String url, String data) {
		return sendPostDataUTF8(url, data, true);
	}
	
	
	public static String sendPostDataUTF8(String url, String data, boolean isUtf8Decode) {
		String result = null;
		try {
			TAGS.log("HttpUtils-->sendPostData->url:" + url);
			TAGS.log("HttpUtils-->sendPostData->data:" + data);
			//OkHttpClient okHttpClient = DYHttpUtils.getInstance().getOkHttpClient();
			RequestBody formBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), data);
			Request request = new Request.Builder().url(url).post(formBody).build();
			Response response = mOkHttpClient.newCall(request).execute();
			result = response.body().string();
			TAGS.log("HttpUtils-->sendPostData->before->result: " + result);
			if(isUtf8Decode)
				result = DEncodingUtils.decoding(result, "utf-8");
			if(response.isSuccessful()) {
				TAGS.log("HttpUtils-->sendPostData->request is Success!");
			} else {
				TAGS.log("HttpUtils-->sendPostData->request is fail!");
			}
		} catch(Exception e) {
			TAGS.log("HttpUtils-->sendPostData->ex:" + e.toString());
		} catch (NoClassDefFoundError e) {
			TAGS.log("HttpUtils-->sendPostData->NoClassDefFoundError:" + e.toString());
		} catch (Error e) {
			TAGS.log("HttpUtils-->sendPostData->Error:" + e.toString());
		}
		TAGS.log("HttpUtils-->sendPostData->after->result: " + result);
		TAGS.log("");
		return result;
	}
	
	public static String sendGet(String url) {
		String result = null;
		try {
			TAGS.log("HttpUtils-->sendGet->url:" + url);
			//OkHttpClient okHttpClient = DYHttpUtils.getInstance().getOkHttpClient();
			Request request = new Request.Builder().url(url).get().build();
			Response response = mOkHttpClient.newCall(request).execute();
			if(response.isSuccessful()) {
				result = response.body().string();
				result = DEncodingUtils.decoding(result, "utf-8");
			} else {
				TAGS.log("HttpUtils-->sendGet->request is fail!");
			}
		} catch(Exception e) {
			TAGS.log("HttpUtils-->sendGet->ex:" + e.toString());
		} catch (NoClassDefFoundError e) {
			TAGS.log("HttpUtils-->sendGet->NoClassDefFoundError:" + e.toString());
		} catch (Error e) {
			TAGS.log("HttpUtils-->sendGet->Error:" + e.toString());
		}
		TAGS.log("HttpUtils-->sendGet->result: " + result);
		return result;
	}
}
