package com.example.hfh5Helper;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class ProgressWebView extends WebView {
	
	private ProgressBar progressbar;
	
	private float mDensity;
	
	private static final String WEBVIEW_BAR_STYLE_NAME = "dianyou_advert_webviewbar_style";
	
	public ProgressWebView(Context context) {
		this(context, null);
	}
	

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        obtainScreenInfo(context);
        
        int webbarstyleId = ResourceUtils.getDrawableId(context, WEBVIEW_BAR_STYLE_NAME);
        
        
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        
        if(webbarstyleId != 0) {
        	progressbar.setBackgroundResource(webbarstyleId);
        } else {
        	//Toast.makeText(context, "webview的样式文件没有找到,请放入到您的工程中!", Toast.LENGTH_LONG).show();
        	progressbar.setBackgroundColor(Color.parseColor("#ff0088ee"));
        }
        
        final int pbH = (int) (3 * mDensity);
        
        progressbar.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, pbH));
        addView(progressbar);
        setWebChromeClient(new WebChromeClient2());
    }

    private void obtainScreenInfo(Context context) {
    	mDensity = context.getResources().getDisplayMetrics().density;
	}

	public class WebChromeClient2 extends WebChromeClient {
		
		@Override
		public void onProgressChanged(WebView arg0, int newProgress) {
			if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
			super.onProgressChanged(arg0, newProgress);
		}

    }

    public void hideProgressBar() {
    	if(progressbar != null) {
    		progressbar.setProgress(100);
    		progressbar.setVisibility(View.GONE);
    	}
    }
    
    public void showProgressBar() {
    	if(progressbar != null) {
    		progressbar.setProgress(0);
    		progressbar.setVisibility(View.VISIBLE);
    	}
    }
    
    
    
    
}
