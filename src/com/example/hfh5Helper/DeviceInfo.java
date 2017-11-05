package com.example.hfh5Helper;
	/**
	 * 设备信息类，在调用DeviceInfoGetter.initDeviceInfo后会被初始化
	 * @author Administrator
	 *
	 */
public class DeviceInfo {
	private static volatile DeviceInfo deviceInfo;
	private String screen_width;
	private String screen_height;
	private String imei;
	private String imsi;
	private String iccid;
	private String ip;
	private String phone_type;
	private String android_version;
	private String api_version;
	private String CID;
	private String LAC;
	private String net_state;
	private String province_code;
	private String phone_carrier;
	private String phone_number;
	private DeviceInfo(){
		super();
	}
	
	public static DeviceInfo getInstance(){
		if(deviceInfo == null){
			synchronized (DeviceInfo.class) {
				if(deviceInfo == null)
					deviceInfo = new DeviceInfo();
			}
			
		}
		return deviceInfo;
	}
	
	public String getScreen_width() {
		return screen_width;
	}
	public void setScreen_width(String screen_width) {
		this.screen_width = screen_width;
	}
	public String getScreen_height() {
		return screen_height;
	}
	public void setScreen_height(String screen_height) {
		this.screen_height = screen_height;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getIccid() {
		return iccid;
	}
	public void setIccid(String iccid) {
		this.iccid = iccid;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPhone_type() {
		return phone_type;
	}
	public void setPhone_type(String phone_type) {
		this.phone_type = phone_type;
	}
	public String getAndroid_version() {
		return android_version;
	}
	public void setAndroid_version(String android_version) {
		this.android_version = android_version;
	}
	public String getApi_version() {
		return api_version;
	}
	public void setApi_version(String api_version) {
		this.api_version = api_version;
	}
	public String getLAC() {
		return LAC;
	}
	public void setLAC(String lAC) {
		LAC = lAC;
	}
	public String getNet_state() {
		return net_state;
	}
	public void setNet_state(String net_state) {
		this.net_state = net_state;
	}
	public String getProvince_code() {
		return province_code;
	}
	public void setProvince_code(String province_code) {
		this.province_code = province_code;
	}
	public String getPhone_carrier() {
		return phone_carrier;
	}
	public void setPhone_carrier(String phone_carrier) {
		this.phone_carrier = phone_carrier;
	}
	public String getCID() {
		return CID;
	}
	public void setCID(String cID) {
		CID = cID;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	
}
