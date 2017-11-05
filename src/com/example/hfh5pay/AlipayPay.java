package com.example.hfh5pay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.hfh5Helper.DeviceInfo;
import com.example.hfh5Helper.HttpUtils;
import com.example.hfh5Helper.TAGS;
import com.example.hfh5Helper.Util;
import com.example.hfh5demo.DYH5Activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class AlipayPay extends Pay {

	public AlipayPay(IPayListener listener) {
		super(listener);
	}
	
	@Override
	protected void payBefore(Activity activity) {
		TAGS.log("--------------------------Ö§¸¶±¦Ö§¸¶------------------------------");
		payBeforeSendMessage(PAY_TYPE_ALIPAY);
	}


	@Override
	protected void payDoing(final Activity activity) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				String ipUrl = getHost() + "/get/clientip";
				String ipData = HttpUtils.sendGet(ipUrl);
				
				String ip = "";
				
				try {
					JSONObject jsonObj = new JSONObject(ipData);
					ip = jsonObj.getString("ip");
					if(Util.isEmpty(ip)) {
						payFinishedSendMessage(activity, false, "cannot get ip from \"" + ipUrl + "\"");
						throw new IllegalArgumentException("get ip cannot be null.");
					}
					
				} catch (JSONException e) {
					ip = DeviceInfo.getInstance().getIp();
				}
				
				
				pay_channel = "alipay";
				
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("mch_id", mch_id);
				
				params.put("pay_channel", pay_channel);
				params.put("trade_type", trade_type);
				params.put("nonce_str", nonce_str);
				params.put("detail", detail);
				params.put("app_name", app_name);
				params.put("bundle", bundle);
				params.put("out_trade_no", out_trade_no);
				params.put("total_fee", total_fee);
				params.put("notify_url", notify_url);
				params.put("spbill_create_ip", ip);
				params.put("app_id", app_id);
				
				List<String> keyList = new ArrayList<String>(params.keySet());
				Collections.sort(keyList, new Comparator<String>() {

					@Override
					public int compare(String lhs, String rhs) {
						return lhs.compareTo(rhs);
					}
				});
				
				StringBuffer paramsBuffer = new StringBuffer();
				for(String k : keyList) {
					String v = params.get(k);
					paramsBuffer.append(k + "=" + v);
					paramsBuffer.append("&");
				}
				paramsBuffer.append("key=" + key);
				
				Log.i("INFO", "paramsBuffer: " + paramsBuffer.toString());
				
				String sign = Util.md5(paramsBuffer.toString()).toUpperCase();
				params.put("sign", sign);
				
				String result = HttpUtils.sendPostUTF8(getHost() + "/pay/unifiedorder", params, false);
				
				try {
					JSONObject jsonObj = new JSONObject(result);
					String code = jsonObj.getString("code");
					if("success".equalsIgnoreCase(code)) {
						JSONObject dataObj = new JSONObject(jsonObj.getString("data"));
						String mweb_url = dataObj.getString("mweb_url");
						DYH5Activity.setPayListener(listener);
						Intent it = new Intent(activity, DYH5Activity.class);
						it.putExtra(DYH5Activity.WEBVIEW_URL, mweb_url);
						it.putExtra(DYH5Activity.PAGE_SEE, DYH5Activity.PAGE_ALIPAY);
						it.putExtra(DYH5Activity.WEBVIEW_METHOD, DYH5Activity.GET);
						activity.startActivity(it);
						
						
						payFinishedSendMessage(activity, true, result);
						
						return;
					} else if("order_exist".equals(code)) {
						
						return;
					}
					
				} catch (Exception e) {
					Log.i("INFO", Log.getStackTraceString(e));
				}
				
				payFinishedSendMessage(activity, false, result);
				
			}
			
		}).start();
	}


}
