package com.example.hfh5pay;

import com.example.hfh5Helper.Util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class Pay {
	
	protected IPayListener listener;
	
	/*
	 * 支付类型
	 */
	public static final int PAY_TYPE_WEIXIN = 0;
	public static final int PAY_TYPE_ALIPAY = 1;
	public static final int PAY_TYPE_CASHIER = 2;
	
	/*
	 * 消息类别
	 */
	public static final int MESSAGE_START_PAY = 0;
	public static final int MESSAGE_PAY_FINISHED = 1;
	public static final int MESSAGE_PAY_FAIL_ORDERID_EXIST = 2;
	
	
	/*
	 * 消息传送的数据标记
	 */
	protected static final String TAG_IS_SUCCESS = "tag_is_success";
	protected static final String TAG_INFO = "tag_info";
	
	private String host = "http://pay.yunbee.cn";
	
	protected Activity mActivity;
	
	/*
	 * 
	 */
	
	String key = "fcxerm0wvswvhbr";
	
	String mch_id = "BB9T7OVE8TMBPS1";
	String pay_channel = "wechat";
	String trade_type = "wap";
	String nonce_str = Util.getRandomString(13);
	String detail = "吴超超";
	String app_name = "app_name";
	String bundle = "bundle";
	String out_trade_no = Util.md5("fdfd" + System.currentTimeMillis())
			.substring(1, 11);
	String total_fee = "1";
	String notify_url = "43";
	String app_id = "2983444";
	
	public Pay() {
		this(null);
	}
	
	public Pay(IPayListener listener) {
		assertUiThread();
		this.listener = listener;
	}
	
	public void setParams(String mch_id, String key, String app_id, String out_trade_no, String total_fee, String notify_url) {
		this.mch_id = mch_id;
		this.key = key;
		this.app_id = app_id;
		this.out_trade_no = out_trade_no;
		this.total_fee = total_fee;
		this.notify_url = notify_url;
	}
	
	protected void assertUiThread() {
		if(!isOnMainThread()) {
			throw new IllegalArgumentException("You cannot call this method on a background thread");
		}
		
	}
	
	private boolean isOnMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	protected Handler mListenerHandler = new Handler(Looper.getMainLooper()) {
		
		
		private void payFinished(Message msg) {
			Bundle data = msg.getData();
			if(data == null) data = new Bundle();
			boolean isSuccess = data.getBoolean(TAG_IS_SUCCESS, false);
			String info = data.getString(TAG_INFO);
			listener.payFinished(isSuccess, info);
		}
		
		public void handleMessage(android.os.Message msg) {
			if(listener == null)
				return;
			
			switch(msg.what) {
			case MESSAGE_START_PAY:
				listener.payStart(msg.arg1);
				break;
				
			case MESSAGE_PAY_FINISHED:
				payFinished(msg);
				break;
				
			case MESSAGE_PAY_FAIL_ORDERID_EXIST:
				payFinished(msg);
				if(mActivity != null) 
					mActivity.finish();
				break;
			}
		};
	};
	
	protected void payBeforeSendMessage(int payType) {
		Message msg = obtainMessage(MESSAGE_START_PAY, null);
		msg.arg1 = payType;
		sendMessage(msg);
	}
	
	protected void payFinishedSendMessage(Activity activity, boolean isSuccess, String result) {
		Bundle data = new Bundle();
		data.putBoolean(TAG_IS_SUCCESS, isSuccess);
		data.putString(TAG_INFO, result);
		sendMessage(obtainMessage(MESSAGE_PAY_FINISHED, data));
	}
	
	protected void payFailOrderIdExist(Activity activity, boolean isSuccess, String result) {
		Bundle data = new Bundle();
		data.putBoolean(TAG_IS_SUCCESS, false);
		data.putString(TAG_INFO, result);
		sendMessage(obtainMessage(MESSAGE_PAY_FAIL_ORDERID_EXIST, data));
	}
	
	public void setListener(IPayListener listener) {
		this.listener = listener;
	}
	
	protected void sendMessage(Message msg) {
		if(msg != null) {
			msg.setTarget(mListenerHandler);
			msg.sendToTarget();
		}
	}
	
	protected Message obtainMessage(int what, Bundle data) {
		Message tmp = Message.obtain();
		tmp.what = what;
		tmp.setData(data);
		return tmp;
	}
	
	public void pay(final Activity activity) {
		assertUiThread();
		mActivity = activity;
		payBefore(activity);
		payDoing(activity);
	}
	
	protected abstract void payDoing(Activity activity);
	protected abstract void payBefore(Activity activity);
	
	protected String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
}
