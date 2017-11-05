package com.example.hfh5demo;



import com.example.hfh5Helper.DeviceInfoManager;
import com.example.hfh5Helper.Util;
import com.example.hfh5pay.AlipayPay;
import com.example.hfh5pay.CashierPay;
import com.example.hfh5pay.Pay;
import com.example.hfh5pay.WechatPay;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements cn.okpay.android.sdkpay.IPayListener, com.example.hfh5pay.IPayListener, OnClickListener {
	
	private TextView tvLog;
	
	private TextView tvInfo;
	
	private EditText et_mch_id, et_key, et_app_id, et_notify_url, et_api_host;
	
	private FrameLayout logLay;
	
	private int logCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvInfo = (TextView) findViewById(R.id.tv_info);
		tvLog = (TextView) findViewById(R.id.tv_log);
		et_mch_id = (EditText) findViewById(R.id.et_mch_id);
		et_key = (EditText) findViewById(R.id.et_key);
		et_app_id = (EditText) findViewById(R.id.et_app_id);
		et_notify_url = (EditText) findViewById(R.id.et_notify_url);
		et_api_host = (EditText) findViewById(R.id.et_api_host);
		logLay = (FrameLayout) findViewById(R.id.id_log_lay);
		
		LinearLayout scrollbarLay = (LinearLayout) findViewById(R.id.id_downscroll);
		scrollbarLay.setOnClickListener(this);
		
		
		tvInfo.setTextIsSelectable(true);
		
		DeviceInfoManager.initDeviceInfo(this);
	}
	
	
	@Override
	protected void onResume() {
		//Util.setCursorToLast(et_mch_id);
		//Util.setCursorToLast(et_key);
		//Util.setCursorToLast(et_app_id);
		//Util.setCursorToLast(et_notify_url);
		super.onResume();
	}
	
	private void sdkPay(String trade_type, int payType) {
		boolean isHasParam = valideEdit();
		if(!isHasParam) return;
		String mch_id = et_mch_id.getText().toString();
		String key = et_key.getText().toString();
		String app_id = et_app_id.getText().toString();
		String notify_url = et_notify_url.getText().toString();
		String nonce_str = cn.okpay.android.sdkhelper.Util.getRandomString(13);
		String out_trade_no = cn.okpay.android.sdkhelper.Util.md5("fdfd" + System.currentTimeMillis())
				.substring(1, 11);
		
		cn.okpay.android.sdkpay.Pay pay = new cn.okpay.android.sdkpay.Pay.Builder().buildListener(this).buildHost(getHost()).buildParam(mch_id, key, app_id, trade_type, nonce_str, "aa", "bb", "cc", out_trade_no, "1", notify_url).build(payType);
		pay.pay(this);
	}
	
	
	public void onSdkAlipayPay(View v) {
		
		String trade_type = "wap";
		
		sdkPay(trade_type, cn.okpay.android.sdkpay.Pay.PAY_TYPE_ALIPAY);
	}
	
	
	public void onSdkWechatPay(View v) {
		String trade_type = "wap";
		
		sdkPay(trade_type, cn.okpay.android.sdkpay.Pay.PAY_TYPE_WEIXIN);
		
	}
	
	public void onSdkWapCashierPay(View v) {
		
		String trade_type = "wap";
		
		sdkPay(trade_type, cn.okpay.android.sdkpay.Pay.PAY_TYPE_WAP_CASHIER);
	}
	
	public void onSdkProtoCashierPay(View v) {
		String trade_type = "wap";
		sdkPay(trade_type, cn.okpay.android.sdkpay.Pay.PAY_TYPE_PROTO_CASHIER);
	
	}
	//com.eg.android.AlipayGphone
	public void onApiAlipayPay(View v) {
		boolean isLaunchPay = Util.isPackageAvilible(this, "com.eg.android.AlipayGphone");
		if(!isLaunchPay) {
			Toast.makeText(this, "支付前请先安装支付宝客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		boolean isHasParam = valideEdit();
		if(!isHasParam) return;
		Pay pay = new AlipayPay(this);
		setApiParams(pay);
		pay.pay(this);
	}
	
	private String getHost() {
		return et_api_host.getText().toString();
	}
	
	private boolean valideEdit() {
		
		if(TextUtils.isEmpty(et_api_host.getText())) {
			Toast.makeText(this, "请输入api的主机地址", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(TextUtils.isEmpty(et_mch_id.getText())) {
			Toast.makeText(this, "请输入商户号", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(TextUtils.isEmpty(et_key.getText())) {
			Toast.makeText(this, "请输入key", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(et_app_id.getText())) {
			Toast.makeText(this, "请输入app_id", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(et_notify_url.getText())) {
			Toast.makeText(this, "请输入notify_url", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private void setApiParams(Pay pay) {
		
		String mch_id = et_mch_id.getText().toString();
		String key = et_key.getText().toString();
		String app_id = et_app_id.getText().toString();
		String notify_url = et_notify_url.getText().toString();
		String out_trade_no = Util.md5("fdfd" + System.currentTimeMillis())
				.substring(1, 11);;
		String total_fee = "1";
		
		pay.setParams(mch_id, key, app_id, out_trade_no, total_fee, notify_url);
		pay.setHost(getHost());
	}
	
	
	public void onApiWapCashierPay(View v) {
		boolean isHasParam = valideEdit();
		if(!isHasParam) return;
		Pay pay = new CashierPay(this);
		setApiParams(pay);
		pay.pay(this);
	}
	
	public void onApiWechatPay(View v) {
		boolean isLaunchPay = Util.isPackageAvilible(this, "com.tencent.mm");
		if(!isLaunchPay) {
			Toast.makeText(this, "支付前请先安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		boolean isHasParam = valideEdit();
		if(!isHasParam) return;
		Pay pay = new WechatPay(this);
		setApiParams(pay);
		pay.pay(MainActivity.this);
	}
	
	public void onClearLog(View v) {
		tvInfo.setText("");
		logCount = 0;
		
		tvLog.setVisibility(View.VISIBLE);
	}
	
	private void addShowInfo(String msg) {
		Log.i("INFO", "addShowInfo: start");
		if(msg == null || "".equals(msg.trim())) return;
		
		String tag = ++logCount + "";
		
		String fore = "log " + tag + ":";
		
		String content = fore + msg;
		
		int totalLen = content.length();
		int prefixStartIndex = fore.length() - 1;
		int tagfixStartIndex = prefixStartIndex - tag.length() - 1;
		int prefixEndIndex = totalLen;
		
		SpannableStringBuilder builder = new SpannableStringBuilder(content);
		ForegroundColorSpan backColorSpan = new ForegroundColorSpan(Color.parseColor("#FFFF0000"));
		ForegroundColorSpan foreColorSpan = new ForegroundColorSpan(Color.parseColor("#FF000000"));
		ForegroundColorSpan tagColorSpan = new ForegroundColorSpan(Color.parseColor("#FF0000FF"));
		builder.setSpan(backColorSpan, 0, totalLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(tagColorSpan, tagfixStartIndex, prefixEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(foreColorSpan, prefixStartIndex, prefixEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		tvInfo.append(builder);
		
		tvInfo.append("\n");
		
		tvLog.setVisibility(View.GONE);
		
		Log.i("INFO", "addShowInfo: end");
	}

	@Override
	public void payStart(int payType) {
		addShowInfo("正在拉去数据，请稍后，少年......O_O");
	}

	@Override
	public void payFinished(boolean isSuccess, String info) {
		if(info != null && !info.trim().equals("")) {
			addShowInfo(info);
		}
		if(isSuccess) {
			addShowInfo("支付流程正常，如有疑问请咨询好付相关技术人员!");
		} else {
			addShowInfo("支付流程不正常，请修改!");
		}
		
	}
	
	private int urlCount = 0;

	@Override
	public void payBack(String arg0) {
		Toast.makeText(this, "back->订单号: " + arg0 , 0).show();
	}

	@Override
	public void doThing(String info) {
		addShowInfo("webview->url" + (++urlCount) + ": " + info);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.id_downscroll)
			downscroll();
		
	}
	
	private boolean isScrollDown = false;

	private void downscroll() {
		if(isScrollDown) {
			logLay.setVisibility(View.VISIBLE);
		} else {
			logLay.setVisibility(View.GONE);
		}
		
		isScrollDown = !isScrollDown;
		
	}

}
