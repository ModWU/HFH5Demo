package com.example.hfh5demo;


import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.example.hfh5Helper.ProgressWebView;
import com.example.hfh5Helper.ResourceUtils;
import com.example.hfh5Helper.Util;
import com.example.hfh5pay.IPayListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.okpay.android.sdkhelper.ParamNames;


public class DYH5Activity extends Activity {
	
	private ProgressWebView webview;
	
	private TextView webview_bar;
	
	private LinearLayout webViewLay;
	
	private String webViewUrl = "https://www.baidu.com", webViewTitle = "典支付";
	
	public static final String WEBVIEW_URL = "URL";
	public static final String WEBVIEW_METHOD = "METHOD";
	public static final String PAGE_SEE = "page_see";
	public static final String LOAD_WAY = "load_way";
	
	private static final String LAYOUT_NAME = "dian_payweb_activity";
	private static final String WIEW_BAR_RES_NAME = "dianyou_advert_webviewbar_style";
	
	
	public static final int PAGE_ALIPAY = 1;
	public static final int PAGE_WEIXIN = 2;
	public static final int PAGE_CASHIER = 3;
	
	
	public static final String POST = "POST";
	public static final String GET = "GET";
	
	
	private String method = GET;
	private int page_see = 0;
	
	private boolean isUrlWay = true;
	
	private static IPayListener mPayListener;
	
	private String out_trade_no = "";
	
	
	private static StringBuffer buffer = new StringBuffer();
	
	
	public static void setPayListener(IPayListener listener) {
		mPayListener = listener;
	}
	
	public static void addHtmlData(String htmlData) {
		buffer.delete(0, buffer.length());
		buffer.append(htmlData);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int layoutId = ResourceUtils.getLayoutId(this, LAYOUT_NAME);
		if(layoutId == 0) {
			Log.i("INFO", "Not find the resource id of layout \"" + LAYOUT_NAME + "\".");
			Toast.makeText(this, "webview的布局文件没有找到,请放入到您的工程中!", Toast.LENGTH_LONG).show();
			finish();
		} else {
			setContentView(layoutId);
			initViews();
			setData();
			setWebView();
		}
		
	}	

	private void setData() {
		Intent intent = getIntent();
		
		String url = intent.getStringExtra(WEBVIEW_URL);
		
		String methd = intent.getStringExtra(WEBVIEW_METHOD);
		
		isUrlWay = intent.getBooleanExtra(LOAD_WAY, true);
		
		page_see = intent.getIntExtra(PAGE_SEE, 0);
		if(url != null)
			webViewUrl = url;
		
		if(methd != null)
			method = methd;
		
		out_trade_no = intent.getStringExtra(ParamNames.OUT_TRADE_NO);
		
		webview_bar.setText(webViewTitle);
		
		
		
	}
	
	private void setWebView() {
		WebSettings webSettings = webview.getSettings();
		webSettings.setBuiltInZoomControls(false);
		 webSettings.setUseWideViewPort(true);
		 webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setAppCacheEnabled(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1)
			webSettings.setDomStorageEnabled(true);
		webview.requestFocus();  
		webview.setScrollBarStyle(0);
		webview.addJavascriptInterface(new JSObject(), "jsClose");
		
		webview.setWebViewClient(new WebViewClient() {
			
			@SuppressLint("NewApi")
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
				return doThingBeforeIntercept(view, request.getUrl().toString());
			}
			
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				return doThingBeforeIntercept(view, url);
			}
			
			private boolean isDoThingBeforeIntercept() {
				if(page_see == PAGE_WEIXIN || page_see == PAGE_CASHIER) {
					return true;
				}
				return false;
			}
			
			
			
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			private WebResourceResponse doThingBeforeIntercept(WebView view, String url) {
				Log.i("INFO", "..doThingBeforeIntercept->url:" + url);
				if(isDoThingBeforeIntercept()) {

					boolean weixin = url.startsWith("https://wx.") || url.startsWith("http://wx.");
					if(weixin) {
						try { 
							Log.i("INFO", "doThingBeforeIntercept-->enter");
							URL mUrl=new URL(url); 
							HttpURLConnection connection= (HttpURLConnection) mUrl.openConnection(); 
							connection.setDoInput(true); 
							connection.setDoOutput(true); 
							connection.setUseCaches(false); 
							connection.setRequestMethod("GET"); 
							connection.setRequestProperty("Referer", getReferer()); 
							DataOutputStream os=new DataOutputStream(connection.getOutputStream()); 
							os.flush(); 
							return new WebResourceResponse("text/html", connection.getContentEncoding(), connection.getInputStream()); 
						} catch (Exception e) { 
							Log.i("INFO", "doThingBeforeIntercept-->ex: " + Log.getStackTraceString(e));
						}
					}
				}
				return super.shouldInterceptRequest(view, url);	
			}
			
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(mPayListener != null) mPayListener.doThing(url);
				if(page_see == PAGE_WEIXIN) {
					boolean weixin = url.startsWith("weixin://wap/pay?");
					 if(url.startsWith("http:") || url.startsWith("https:")) {
							Map<String, String> extraHeaders = new HashMap<String, String>();
							extraHeaders.put("Referer", getReferer());
					        view.loadUrl(url, extraHeaders);
					        tryCloseFromLoadUrl(url);
					        return true;
					} else if(weixin) {
						 Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
						 DYH5Activity.this.startActivity(intent);
						 return true;
					}
				} else {
				
					boolean weixin = url.startsWith("weixin://wap/pay?");
					boolean alipay = url.startsWith("alipays://platformapi/startApp?");
					if(weixin || alipay) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						DYH5Activity.this.startActivity(intent);
						return true;
					} 
				}
				
				Log.i("INFO", "默认->最下面");
				view.loadUrl(url);
				
				//tryCloseFromLoadUrl(url);
				
				return true;
			}
			
			
			

		});
		
		webview.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String arg1, String arg2, String arg3, long arg4) {
				if (url != null && url.startsWith("http://"))
	                   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				
			}
		});
		
		if(isUrlWay) {
			loadUrl();
		} else {
			loadData();
		}
		
		
	}
	
	private void tryCloseFromLoadUrl(String url) {
		//尝试在支付完成之后关闭
		if(url.startsWith("http://pay.yunbee.cn")) {
			Toast.makeText(this, "支付完成", Toast.LENGTH_SHORT).show();
			DYH5Activity.this.finish();
		}
	}
	
	
	
	private String getReferer() {
		if(page_see == PAGE_WEIXIN || page_see == PAGE_CASHIER) {
			return "http://pay.yunbee.cn";
		} else {
			return "";
		}
	}
	
	private void loadUrl() {
		if(POST.equals(method)) {
			int index_ = webViewUrl.indexOf("?");
			String url = webViewUrl;
			String params = "";
			if(index_ > 0) {
				url = webViewUrl.substring(0, index_);
				params = webViewUrl.substring(index_ + 1, webViewUrl.length());
			}
			
			Log.i("INFO", "setWebView-->params old: " + params);
			
			byte[] paramData = new byte[0];
			try {
				paramData = params.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				Log.i("INFO", "setWebView-->UnsupportedEncodingException: " + e.toString());
			}
			
			Log.i("INFO", "setWebView-->params new: " + params);
			
			Log.i("INFO", "setWebView-->url: " + url);
			Log.i("INFO", "setWebView-->paramData: " + paramData);
			
			webview.postUrl(url, paramData);
		} else {
			Log.i("INFO", "....page_see:...." + page_see);
			if(page_see == PAGE_WEIXIN) {
				Log.i("INFO", "走微信流程" + page_see);
				Map<String, String> extraHeaders = new HashMap<String, String>();
				extraHeaders.put("Referer", "http://pay.yunbee.cn");
				webview.loadUrl(webViewUrl, extraHeaders);
			}else {
				webview.loadUrl(webViewUrl);
			}
			
		}
	}
	
	private void loadData() {
		webview.loadData(buffer.toString(), "text/html", "utf-8");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	

	private void initViews() {
		int webviewbarId = ResourceUtils.getId(this, "webview_bar");
		int webviewlayid = ResourceUtils.getId(this, "id_webview_lay");
		int webviewStyleId = ResourceUtils.getDrawableId(this, WIEW_BAR_RES_NAME);
		webview_bar = (TextView) findViewById(webviewbarId);
		
		webViewLay = (LinearLayout) findViewById(webviewlayid);
		webview = new ProgressWebView(getApplicationContext());
		LinearLayout.LayoutParams webview_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		webview.setLayoutParams(webview_lp);
		if(webviewStyleId != 0)
			webview.setBackgroundResource(webviewStyleId);
		else
			webview.setBackgroundColor(Color.WHITE);
		webViewLay.addView(webview);
	}
	
	//private boolean isBackFromUrl = false;
	
	class JSObject {
		@JavascriptInterface
		public void back() {
			DYH5Activity.this.finish();
		}
		
		@JavascriptInterface
		public void JsCallAndroid(String url) {
		}
	}
	
	private boolean isDirectBack() {
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (!isDirectBack() && webview.canGoBack()) {
					webview.goBack();
				} else {
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void finish() {
		if(mPayListener != null) 
			mPayListener.payBack(out_trade_no);
		super.finish();
	}
	
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void performBack(View v) {
		if (!isDirectBack() && webview.canGoBack()) {
			webview.goBack();
			return;
		}
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
			super.onBackPressed();
		else 
			finish();
	}
	
	public void openLink(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(this.webViewUrl));
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Util.releaseAllWebViewCallback();
		if(webview != null) {
			webview.removeAllViews();
			webViewLay.removeView(webview);
			webview.destroy();
			webview.setVisibility(View.GONE);
			webview = null;
		}
		
	}
	

}

