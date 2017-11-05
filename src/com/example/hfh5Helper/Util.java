package com.example.hfh5Helper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

public class Util {
	
	public static void setCursorToLast(EditText et) {
		if(et != null) {
			CharSequence text = et.getText();
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());// 将光标移动到最后
			}
		}
	}
	
	public static String getHash(String message, String algorithm)
	  {
	    try {
	      byte[] buffer = message.getBytes();
	      MessageDigest md = MessageDigest.getInstance(algorithm);
	      md.update(buffer);
	      byte[] digest = md.digest();
	      String hex = "";
	      for (int i = 0; i < digest.length; i++) {
	        int b = digest[i] & 0xFF;
	        if (Integer.toHexString(b).length() == 1) {
	          hex = hex + "0";
	        }

	        hex = hex + Integer.toHexString(b);
	      }
	      return hex;
	    } catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }
	    return null;
	  }
	
	public static String convertFromByteToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			String tmp = Integer.toHexString(bytes[i] & 0xFF);
			if (tmp.length() == 1) {
				result += "0" + tmp;
			} else {
				result += tmp;
			}
		}
		return result;
	}
	
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String md5(String content) {
		String result = null;
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = Util
				.convertFromByteToHexString(md5.digest(content.getBytes()));
		return result;
	}

	public static String isFileExist(String filename) {
		File file = new File(filename);
		if (file.exists())
			return file.getAbsolutePath();
		return null;
	}

	public static String SystemPropertiesGet(String key) {
		String ret = "";
		try {
			// 通过反射获取到sdk隐藏的服务
			Method method = Class.forName("android.os.SystemProperties")
					.getMethod("get", String.class);
			ret = (String) method.invoke(null, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String escapeExprSpecialWord(String keyword) {
		String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?",
				"^", "{", "}", "|" };
		for (String key : fbsArr) {
			if (keyword.contains(key)) {
				keyword = keyword.replace(key, "\\" + key);
			}
		}
		return keyword;
	}

	public static PackageInfo getApplicationInfo(Context context, Uri packageURI) {
		final String archiveFilePath = packageURI.getPath();
		PackageInfo info = null;
		try {
			PackageManager pm = context.getPackageManager();
			info = pm.getPackageArchiveInfo(archiveFilePath,
					PackageManager.GET_ACTIVITIES);
			
			
			
			if (info != null) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return info;
	}

	public static boolean hasSim(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int state = tm.getSimState();
		TAGS.log("sim卡state" + state);
		if (state == TelephonyManager.SIM_STATE_READY) {
			TAGS.log("有sim卡");
			return true;
		} else {
			TAGS.log("没有sim卡");
			return false;
		}
	}
	
	/*
	 * 获取当前日期
	 */
	public static String getCurrentDayString() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		return String.valueOf(year) + String.valueOf(month)
				+ String.valueOf(day);
	}
	
	
	public static String jxhy_MD5sign(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes("UTF-8");
			// 鑾峰緱MD5鎽樿绠楁硶鐨� MessageDigest 瀵硅薄
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 浣跨敤鎸囧畾鐨勫瓧鑺傛洿鏂版憳瑕�
			mdInst.update(btInput);
			// 鑾峰緱瀵嗘枃
			byte[] md = mdInst.digest();
			// 鎶婂瘑鏂囪浆鎹㈡垚鍗佸叚杩涘埗鐨勫瓧绗︿覆褰㈠紡
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void showToastAtMainThread(Toast toast, String message) {
		
		final Toast finalToast = toast;
		
		final String finalMsg = message;
		
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				if(finalToast != null) {
					finalToast.setText(finalMsg);
					finalToast.show();
				}
			}
			
		});
	}
	
	public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        return df.format(new Date());
    }
	
	public static Toast getToast(Context context) {
		final Toast[] tmpToast = new Toast[1];
		final Context tmpContext = context;
		final boolean[] isGet = new boolean[1];
		if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
			tmpToast[0] = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		} else {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					synchronized (isGet) {
						tmpToast[0] = Toast.makeText(tmpContext, "", Toast.LENGTH_SHORT);
						isGet[0] = true;
						isGet.notify();
					}
					
				}
				
			});
			
			if(!isGet[0]) {
				synchronized (isGet) {
					while(!isGet[0]) {
						try {
							isGet.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}
		
		return tmpToast[0];
	}
	
	public static boolean isPackageAvilible(Context context, String packName) {
		if(packName == null) return false;
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packName)) {
                    return true;
                }
            }
        }
        return false;
    }
	
	public static void replaceClassLoader_api17(ClassLoader loader, Activity activity) {  
	    try {  
	    	if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
		        Class clazz_Ath = Class.forName("android.app.ActivityThread");  
		        Class clazz_LApk = Class.forName("android.app.LoadedApk");  
		        Object currentActivityThread = clazz_Ath.getMethod("currentActivityThread").invoke(null);  
		        Field field1 = clazz_Ath.getDeclaredField("mPackages");  
		        field1.setAccessible(true);  
		        Map mPackages = (Map) field1.get(currentActivityThread);  
		        String packageName = activity.getPackageName();  
		        WeakReference ref = (WeakReference) mPackages.get(packageName);  
		        Field field2 = clazz_LApk.getDeclaredField("mClassLoader");  
		        field2.setAccessible(true);  
		        field2.set(ref.get(), loader); 
	    	}
	        
	        Log.i("INFO", "replaceClassLoader successful!");
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        Log.i("INFO", "replaceClassLoader-->ex: " + e.toString());
	    }  
	} 
	
	public static void replaceClassLoader(ClassLoader loader, Activity activity) {  
	    try {  
	    	
		    Class clazz_Ath = Class.forName("android.app.ActivityThread");  
		    Class clazz_LApk = Class.forName("android.app.LoadedApk");  
		    Object currentActivityThread = clazz_Ath.getMethod("currentActivityThread").invoke(null);  
		    Field field1 = clazz_Ath.getDeclaredField("mPackages");  
		    field1.setAccessible(true);  
		    Map mPackages = (Map) field1.get(currentActivityThread);  
		    String packageName = activity.getPackageName();  
		    WeakReference ref = (WeakReference) mPackages.get(packageName);  
		    Field field2 = clazz_LApk.getDeclaredField("mClassLoader");  
		    field2.setAccessible(true);  
		    field2.set(ref.get(), loader); 
	    	
	        
	        Log.i("INFO", "replaceClassLoader successful!");
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        Log.i("INFO", "replaceClassLoader-->ex: " + e.toString());
	    }  
	} 
	
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}
	
	
	@TargetApi(Build.VERSION_CODES.DONUT)
	public static void releaseAllWebViewCallback() {
		int currentApiLevel = 1;
		try {
			currentApiLevel = android.os.Build.VERSION.SDK_INT;
		} catch(Exception e) {
		} catch(Error e) {}
		
		
		if (currentApiLevel < 16) {
			try {
				Field field = WebView.class.getDeclaredField("mWebViewCore");
				field = field.getType().getDeclaredField("mBrowserFrame");
				field = field.getType().getDeclaredField("sConfigCallback");
				field.setAccessible(true);
				field.set(null, null);
			} catch (NoSuchFieldException e) {
			} catch (IllegalAccessException e) {
			}
		} else {
			try {
				Field sConfigCallback = Class.forName("android.webkit.BrowserFrame")
						.getDeclaredField("sConfigCallback");
				if (sConfigCallback != null) {
					sConfigCallback.setAccessible(true);
					sConfigCallback.set(null, null);
				}
			} catch (NoSuchFieldException e) {
			} catch (ClassNotFoundException e) {
			} catch (IllegalAccessException e) {
			}
		}
	}
	
	


}
