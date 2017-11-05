package com.example.hfh5Helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

public class DeviceInfoManager {
	private static TelephonyManager telephonyManager = null;
	private static Display display = null;
	private static ConnectivityManager connectionManager = null;
	private static String lac = null;
	private static String cid = null;

	/**
	 * 执行application后先获取手机的一些基本信息，包括： 1、屏幕大小 2、Imei 3、Imsi 4、Iccid 5、手机的ip地址
	 * 6、手机型号 7、android版本号 8、基站信息（MNC，移动网络号码（移动为0，联通为1，电信为2）；  LAC，位置区域码；）
	 * 9、网络状态 10、省份代码（根据iccid） 11、运营商（根据imsi）
	 * 
	 * @param context
	 * @return DeviceInfo
	 */
	public static DeviceInfo initDeviceInfo(Context context) {
		DeviceInfoManager.telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		DeviceInfoManager.display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		connectionManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		DeviceInfo deviceInfo = DeviceInfo.getInstance();
		deviceInfo.setScreen_width(getWidth());
		TAGS.log("屏幕宽：" + deviceInfo.getScreen_width());
		deviceInfo.setScreen_height(getHeight());
		TAGS.log("屏幕高：" + deviceInfo.getScreen_height());
		getIMSIIMEInfo(context);
		deviceInfo.setImei(getImei());
		TAGS.log("IMEI：" + deviceInfo.getImei());
		
		deviceInfo.setImsi(getImsi(context));
		TAGS.log("IMSI：" + deviceInfo.getImsi());
		deviceInfo.setIccid(getIccid());
		TAGS.log("ICCID：" + deviceInfo.getIccid());
		deviceInfo.setIp(getGprsIpAddress());
		TAGS.log("IP：" + deviceInfo.getIp());
		deviceInfo.setPhone_type(getPhoneType());
		TAGS.log("手机型号：" + deviceInfo.getPhone_type());
		deviceInfo.setAndroid_version(getAndroidVersion());
		TAGS.log("安卓版本：" + deviceInfo.getAndroid_version());
		deviceInfo.setApi_version(getApiVersion());
		TAGS.log("api版本：" + deviceInfo.getApi_version());
		deviceInfo.setCID(getCID());
		//deviceInfo.setCID("");
		//TAGS.log("CID：" + deviceInfo.getCID());
		deviceInfo.setLAC(getLAC());
		TAGS.log("LAC：" + deviceInfo.getLAC());
		String netState = getNetState(context);
		deviceInfo.setNet_state(netState);
		TAGS.log("网络状态：" + deviceInfo.getNet_state());
		deviceInfo.setProvince_code(getProvinceCode(context));
		TAGS.log("省份代码：" + deviceInfo.getProvince_code());
		deviceInfo.setPhone_carrier(getPhoneCarrier(context));
		TAGS.log("手机运营商：" + deviceInfo.getPhone_carrier());
		return deviceInfo;
	}

	private static String getWidth() {
		String width = String.valueOf(display.getWidth());
		return width == null ? "" : width;
	}

	private static String getHeight() {
		String height = String.valueOf(display.getHeight());
		return height == null ? "" : height;
	}

	private static String getIccid() {
		String iccid = telephonyManager.getSimSerialNumber();
		return iccid == null ? "" : iccid;
	}

	private static String getGprsIpAddress() {
		TAGS.log("getGprsIpAddress");
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ip = inetAddress.getHostAddress().toString();
						if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))
							return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			TAGS.log(ex.getMessage());
		}
		return "";
	}

	private static String getPhoneType() {
		String phone_type_ori = android.os.Build.MODEL;
		String phone_type = null;
		if (phone_type_ori != null) {
			phone_type = phone_type_ori.replaceAll("\\s", "");
		}
		return phone_type == null ? "" : phone_type;
	}

	private static String getAndroidVersion() {
		String android_version = android.os.Build.VERSION.RELEASE;
		return android_version == null ? "" : android_version;
	}

	public static String getLAC() {
		if (lac != null) {
			return lac;
		} else {
			TAGS.log("getLAC");
			String LAC = null;
			int phone_type = telephonyManager.getPhoneType();
			if (TelephonyManager.PHONE_TYPE_CDMA == phone_type) {
				CdmaCellLocation location = (CdmaCellLocation) telephonyManager
						.getCellLocation();
				if (location != null) {
					LAC = String.valueOf(location.getNetworkId());
				}
			}
			if (TelephonyManager.PHONE_TYPE_GSM == phone_type) {
				GsmCellLocation location = (GsmCellLocation) telephonyManager
						.getCellLocation();
				if (location != null) {
					LAC = String.valueOf(location.getLac());
				}
			}
			LAC = LAC == null ? "" : LAC;
			lac = LAC;
			DeviceInfo.getInstance().setLAC(lac);
			return LAC;
		}
	}

	public static String getCID() {
		if (cid != null) {
			return cid;
		} else {
			TAGS.log("getCID");
			String CID = null;
			int phone_type = telephonyManager.getPhoneType();
			if (TelephonyManager.PHONE_TYPE_CDMA == phone_type) {
				CdmaCellLocation location = (CdmaCellLocation) telephonyManager
						.getCellLocation();
				int cellIDs = 0;
				if (location != null) {
					cellIDs = location.getBaseStationId();
				}
				CID = String.valueOf(cellIDs / 16);
			}
			if (TelephonyManager.PHONE_TYPE_GSM == phone_type) {
				GsmCellLocation location = (GsmCellLocation) telephonyManager
						.getCellLocation();
				if (location != null) {
					CID = String.valueOf(location.getCid() & 0xffff);
				}
			}
			CID = CID == null ? "" : CID;
			cid = CID;
			DeviceInfo.getInstance().setCID(cid);
			return CID;
		}
	}

	public static String getNetState(Context context) {
		String net_state = NetState.UNKNOWN.name();
		if (connectionManager == null) {
			connectionManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
		String net = null;
		if (networkInfo != null) {
			net = networkInfo.getTypeName();
		} else {
			return NetState.CLOSE.name();
		}
		net = net.toUpperCase();
		if (!networkInfo.isAvailable()) {
			net_state = NetState.CLOSE.name();
		} else if (NetState.WIFI.getState().equals(net)) {
			net_state = NetState.WIFI.name();
		} else if (NetState.MOBILE.getState().equals(net)) {
			net_state = NetState.MOBILE.name();
		}
		return net_state;
	}

	private static String getProvinceCode(Context context) {
		String iccid = getIccid();
		String provinceCode = "";
		if (iccid.length() != 20) {// 标准的iccid长度是20，如果不是，则按照iccid获取省份代码的规则也无效，直接返回空字符串
			return "";
		}
		String phoneCarrier = getPhoneCarrier(context);
		if (phoneCarrier.equals(PhoneCarrier.CMCC.name())) {// 移动
			provinceCode = iccid.substring(8, 10);
		} else if (phoneCarrier.equals(PhoneCarrier.UNICOM.name())) {// 联通
			provinceCode = iccid.substring(9, 11);
		} else if (phoneCarrier.equals(PhoneCarrier.TELECOM.name())) {// 电信
			provinceCode = iccid.substring(10, 13);
		}
		return provinceCode;
	}

	private static String getPhoneCarrier(Context context) {
		String phoneCarrier = "";
		String carrierCode = null;
		String imsi = getImsi(context);
		if (imsi != null && imsi.length() >= 6) {
			carrierCode = imsi.substring(3, 5);
		}
		if (PhoneCarrier.CMCC.getCodes().contains(carrierCode)) {
			phoneCarrier = PhoneCarrier.CMCC.name();
		} else if (PhoneCarrier.TELECOM.getCodes().contains(carrierCode)) {
			phoneCarrier = PhoneCarrier.TELECOM.name();
		} else if (PhoneCarrier.UNICOM.getCodes().contains(carrierCode)) {
			phoneCarrier = PhoneCarrier.UNICOM.name();
		} else {
			phoneCarrier = PhoneCarrier.UNKNOWN.name();
		}
		return phoneCarrier;
	}

	private static String getApiVersion() {
		String api_version = String.valueOf(android.os.Build.VERSION.SDK_INT);
		return api_version == null ? "" : api_version;
	}

	// imsi工具类

	private static Integer simId_1 = 0;
	private static Integer simId_2 = 1;
	private static String chipName = "";
	private static String imsi_1 = "";
	private static String imsi_2 = "";
	private static String imei_1 = "";
	private static String imei_2 = "";

	public static String getImei() {
		return TextUtils.isEmpty(imei_1) ? (TextUtils.isEmpty(imei_2) ? ""
				: imei_2) : imei_1;
	}

	public static String getImsi(Context context) {
		String imsi1 = TextUtils.isEmpty(imsi_1) ? imsi_2 : imsi_1;
		TAGS.log("典游获取的imsi：" + imsi1);

		String imsi2 = getDaMaiImsi(context);
		TAGS.log("大麦获取的imsi：" + imsi2);

		DeviceUtils du = new DeviceUtils(context);
		imsi_1 = du.imsi_1;
		imsi_2 = du.imsi_2;
		String imsi3 = TextUtils.isEmpty(imsi_1) ? imsi_2 : imsi_1;
		TAGS.log("指和获取的imsi：" + imsi3);

		String imsi = "";
		if (imsi1 != null && !"".equals(imsi1)) {
			imsi = imsi1;
		} else if (imsi2 != null && !"".equals(imsi2)) {
			imsi = imsi2;
		} else if (imsi3 != null && !"".equals(imsi3)) {
			imsi = imsi3;
		}
		TAGS.log("最终的imsi：" + imsi);
		return imsi;
	}

	/**
	 * 获取IMSInfo
	 * 
	 * @return
	 */
	public static void getIMSIIMEInfo(Context context) {
		if (initQualcommDoubleSim(context)) {
			TAGS.log("高通芯片");

		} else {
			if (initMtkDoubleSim(context)) {
				TAGS.log("MTK的芯片");

			} else {
				if (initMtkSecondDoubleSim(context)) {
					TAGS.log("MTK的芯片2");

				} else {
					if (initSpreadDoubleSim(context)) {
						TAGS.log("展讯芯片");

					} else {
						getIMSI(context);
						TAGS.log("系统的api");
					}
				}
			}
		}

		if (TextUtils.isEmpty(imsi_1) ? (TextUtils.isEmpty(imsi_2) ? true
				: false) : false) {
			getIMSI(context);
			TAGS.log("系统的api");
		}
	}

	/**
	 * MTK的芯片的判断
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean initMtkDoubleSim(Context mContext) {
		try {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");
			Field fields1 = c.getField("GEMINI_SIM_1");
			fields1.setAccessible(true);
			simId_1 = (Integer) fields1.get(null);
			Field fields2 = c.getField("GEMINI_SIM_2");
			fields2.setAccessible(true);
			simId_2 = (Integer) fields2.get(null);

			Method m = TelephonyManager.class.getDeclaredMethod(
					"getSubscriberIdGemini", int.class);
			imsi_1 = (String) m.invoke(tm, simId_1);
			imsi_2 = (String) m.invoke(tm, simId_2);

			Method m1 = TelephonyManager.class.getDeclaredMethod(
					"getDeviceIdGemini", int.class);
			imei_1 = (String) m1.invoke(tm, simId_1);
			imei_2 = (String) m1.invoke(tm, simId_2);

			chipName = "MTK芯片";
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * MTK的芯片的判断2
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean initMtkSecondDoubleSim(Context mContext) {
		try {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");
			Field fields1 = c.getField("GEMINI_SIM_1");
			fields1.setAccessible(true);
			simId_1 = (Integer) fields1.get(null);
			Field fields2 = c.getField("GEMINI_SIM_2");
			fields2.setAccessible(true);
			simId_2 = (Integer) fields2.get(null);

			Method mx = TelephonyManager.class.getMethod("getDefault",
					int.class);
			TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, simId_1);
			TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);

			imsi_1 = tm1.getSubscriberId();
			imsi_2 = tm2.getSubscriberId();

			imei_1 = tm1.getDeviceId();
			imei_2 = tm2.getDeviceId();

			chipName = "MTK芯片";
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 展讯芯片的判断
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean initSpreadDoubleSim(Context mContext) {
		try {
			Class<?> c = Class
					.forName("com.android.internal.telephony.PhoneFactory");
			Method m = c.getMethod("getServiceName", String.class, int.class);
			String spreadTmService = (String) m.invoke(c,
					Context.TELEPHONY_SERVICE, 1);
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			imsi_1 = tm.getSubscriberId();
			imei_1 = tm.getDeviceId();
			TelephonyManager tm1 = (TelephonyManager) mContext
					.getSystemService(spreadTmService);
			imsi_2 = tm1.getSubscriberId();
			imei_2 = tm1.getDeviceId();

			chipName = "展讯芯片";
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 高通芯片判断
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean initQualcommDoubleSim(Context mContext) {
		try {
			Class<?> cx = Class
					.forName("android.telephony.MSimTelephonyManager");
			Object obj = mContext.getSystemService("phone_msim");
			Method md = cx.getMethod("getDeviceId", int.class);
			Method ms = cx.getMethod("getSubscriberId", int.class);
			imei_1 = (String) md.invoke(obj, simId_1);
			imei_2 = (String) md.invoke(obj, simId_2);
			imsi_1 = (String) ms.invoke(obj, simId_1);
			imsi_2 = (String) ms.invoke(obj, simId_2);
			int statephoneType_2 = 0;
			boolean flag = false;
			try {
				Method mx = cx.getMethod("getPreferredDataSubscription",
						int.class);
				Method is = cx.getMethod("isMultiSimEnabled", int.class);
				statephoneType_2 = (Integer) mx.invoke(obj);
				flag = (Boolean) is.invoke(obj);
			} catch (Exception e) {
				// TODO: handle exception
			}
			chipName = "高通芯片-getPreferredDataSubscription:" + statephoneType_2
					+ ",flag:" + flag;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 系统的api
	 * 
	 * @return
	 */
	public static boolean getIMSI(Context mContext) {
		/**
		 * public static final String READ_PHONE_STATE
		 * 
		 * Added in API level 1 Allows read only access to phone state.
		 * 
		 * Note: If both your minSdkVersion and targetSdkVersion values are set
		 * to 3 or lower, the system implicitly grants your app this permission.
		 * If you don't need this permission, be sure your targetSdkVersion is 4
		 * or higher.
		 * 
		 * Protection level: dangerous
		 * 
		 * Constant Value: "android.permission.READ_PHONE_STATE"
		 */
		try {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			imsi_1 = tm.getSubscriberId();
			imei_1 = tm.getDeviceId();
			chipName = "单卡芯片";
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return false;

	}

	public static String getDaMaiImsi(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		Class<?>[] resources = new Class<?>[] { int.class };
		Integer resourceId = new Integer(1);
		if (imsi == null || "".equals(imsi)) {
			try {
				Method addMethod = tm.getClass().getDeclaredMethod(
						"getSubscriberIdGemini", resources);
				addMethod.setAccessible(true);
				imsi = (String) addMethod.invoke(tm, resourceId);
			} catch (Exception e) {
				imsi = null;
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
		}
		if (imsi == null || "".equals(imsi)) {
			try {
				Class<?> c = Class
						.forName("com.android.internal.telephony.PhoneFactory");
				Method m = c.getMethod("getServiceName", String.class,
						int.class);
				String spreadTmService = (String) m.invoke(c,
						Context.TELEPHONY_SERVICE, 1);
				TelephonyManager tm1 = (TelephonyManager) context
						.getSystemService(spreadTmService);
				imsi = tm1.getSubscriberId();
			} catch (Exception e) {
				imsi = null;
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
		}
		if (imsi == null || "".equals(imsi)) {
			try {
				Method addMethod2 = tm.getClass().getDeclaredMethod(
						"getSimSerialNumber", resources);
				addMethod2.setAccessible(true);
				imsi = (String) addMethod2.invoke(tm, resourceId);
			} catch (Exception e) {
				imsi = null;
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
		}
		return imsi;
	}
}
