package com.example.hfh5Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
	
	private boolean isEnable = true;
	
	public NetworkChangeReceiver(IOnNetworkChangeListener onNetworkChangeListener) {
		listener = onNetworkChangeListener;
	}
	
	public NetworkChangeReceiver() {};

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	
			if (connectivityManager == null) {
				return;
			}


	
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();

			if(info != null && info.isAvailable()) {
				Log.i("chaochao", "ÓÐÍøÂç Ë¢ÐÂ");
				if(listener != null)
					listener.openNetwork();
			} else {
				Log.i("chaochao", "ÎÞÍøÂç close");
				if(listener != null) {
					listener.closeNetwork();
				}
			}
			/*ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if(gprs.isConnected()) {
				if(!isEnable) {
					if(listener != null)
						listener.openNetwork();
				}
				isEnable = true;
			}
			
			if(wifi.isConnected()) {
				if(!isEnable) {
					if(listener != null)
						listener.openNetwork();
				}
				isEnable = true;
			}
			
			if(!gprs.isConnected() || !wifi.isConnected()) {
				if(isEnable) {
					if(listener != null)
						listener.closeNetwork();
				}
				isEnable = false;
			}*/
		}
	}
	
	private IOnNetworkChangeListener listener;
	
	public void setOnNetworkChangeListener(IOnNetworkChangeListener onNetworkChangeListener) {
		listener = onNetworkChangeListener;
	}
	public interface IOnNetworkChangeListener {
		void openNetwork();
		void closeNetwork();
	}

}
