package com.hzkj.wdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public static final String ALITAIL_APP_DATA = "SIMIDA_APP_DATA";
	private Context con;

	public SharePreferenceUtil(Context c){
		con=c;
		sp = c.getSharedPreferences(ALITAIL_APP_DATA, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	// string 数据
	public String getStringData(String dataName) {
		return sp.getString(dataName, "");
	}

	public void setStringData(String dataName,String t) {
		editor.putString(dataName, t);
		editor.commit();
	}
	// boolean 数据
	public boolean getBooleanData(String dataName) {
		return sp.getBoolean(dataName, false);
	}

	public void setBooleanData(String dataName,boolean t) {
		editor.putBoolean(dataName, t);
		editor.commit();
	}
	// 注册验证码倒计时
	public long getRegisterLeftTiming() {
		return sp.getLong("register_left_timing", 0);
	}

	public void setRegisterLeftTiming(long t) {
		editor.putLong("register_left_timing", t);
		editor.commit();
	}

	// 注册验证码倒计时离开时间
	public long getRegisterTime() {
		return sp.getLong("register_time", 0L);
	}

	public void setRegisterTime(long t) {
		editor.putLong("register_time", t);
		editor.commit();
	}

	// 找回密码验证码倒计时
	public long getForgetPswLeftTiming() {
		return sp.getLong("forget_psw_left_timing", 0L);
	}

	public void setForgetPswLeftTiming(long t) {
		editor.putLong("forget_psw_left_timing", t);
		editor.commit();
	}

	// 找回密码验证码离开时间
	public long getForgetPswTime() {
		return sp.getLong("forget_psw_time", 0L);
	}

	public void setForgetPswTime(long t) {
		editor.putLong("forget_psw_time", t);
		editor.commit();
	}
	// 顾客验证码倒计时
		public long getCustomerLeftTiming() {
			return sp.getLong("customer_left_timing", 0);
		}

		public void setCustomerLeftTiming(long t) {
			editor.putLong("customer_left_timing", t);
			editor.commit();
		}
		// 是否忽略当前版本
		public void setIgnorCode(String versionCode,boolean isIgnore){
			editor.putBoolean("update" + versionCode, isIgnore);
			editor.commit();
		}
		public boolean getIgnorCode(String versionCode){
			return sp.getBoolean("update"+versionCode,false);
		}
		// 顾客验证码倒计时离开时间
		public long getCustomerTime() {
			return sp.getLong("customer_time", 0L);
		}

		public void setCustomerTime(long t) {
			editor.putLong("customer_time", t);
			editor.commit();
		}
		
		// 裁缝申请验证码倒计时
				public long getAuditLeftTiming() {
					return sp.getLong("audit_left_timing", 0);
				}

				public void setAuditLeftTiming(long t) {
					editor.putLong("audit_left_timing", t);
					editor.commit();
				}

				// 裁缝申请验证码倒计时离开时间
				public long getAuditTime() {
					return sp.getLong("audit_time", 0L);
				}

				public void setAuditTime(long t) {
					editor.putLong("audit_time", t);
					editor.commit();
				}
				
				// 手机绑定验证码倒计时
				public long getBindingLeftTiming() {
					return sp.getLong("binding_left_timing", 0);
				}

				public void setBindingLeftTiming(long t) {
					editor.putLong("binding_left_timing", t);
					editor.commit();
				}

				// 手机绑定验证码倒计时离开时间
				public long getBindingTime() {
					return sp.getLong("binding_time", 0L);
				}

				public void setBindingTime(long t) {
					editor.putLong("binding_time", t);
					editor.commit();
				}
				
				// 手机绑定验证码倒计时
				public long getModifyBindingLeftTiming() {
					return sp.getLong("modify_binding_left_timing", 0);
				}

				public void setModifyBindingLeftTiming(long t) {
					editor.putLong("modify_binding_left_timing", t);
					editor.commit();
				}

				// 手机绑定验证码倒计时离开时间
				public long getModifyBindingTime() {
					return sp.getLong("modify_binding_time", 0L);
				}

				public void setModifyBindingTime(long t) {
					editor.putLong("modify_binding_time", t);
					editor.commit();
				}
				
				// 手机绑定验证码倒计时
				public long getModifyNextBindingLeftTiming() {
					return sp.getLong("modify_next_binding_left_timing", 0);
				}

				public void setModifyNextBindingLeftTiming(long t) {
					editor.putLong("modify_next_binding_left_timing", t);
					editor.commit();
				}

				// 手机绑定验证码倒计时离开时间
				public long getModifyNextBindingTime() {
					return sp.getLong("modify_next_binding_time", 0L);
				}

				public void setModifyNextBindingTime(long t) {
					editor.putLong("modify_next_binding_time", t);
					editor.commit();
				}
				
				// 手机绑定验证码倒计时
				public long getPhoneAuthLeftTiming() {
					return sp.getLong("phone_auth_left_timing", 0);
				}

				public void setPhoneAuthLeftTiming(long t) {
					editor.putLong("phone_auth_left_timing", t);
					editor.commit();
				}

				// 手机绑定验证码倒计时离开时间
				public long getPhoneAuthTime() {
					return sp.getLong("phone_auth_time", 0L);
				}

				public void setPhoneAuthTime(long t) {
					editor.putLong("phone_auth_time", t);
					editor.commit();
				}
				
				// 手机绑定验证码倒计时
				public long getBindBankLeftTiming() {
					return sp.getLong("bind_bank_left_timing", 0);
				}

				public void setBindBankLeftTiming(long t) {
					editor.putLong("bind_bank_left_timing", t);
					editor.commit();
				}

				// 手机绑定验证码倒计时离开时间
				public long getBindBankTime() {
					return sp.getLong("bind_bank_time", 0L);
				}

				public void setBindBankTime(long t) {
					editor.putLong("bind_bank_time", t);
					editor.commit();
				}
	/**
	 * 登录名称
	 * @return
	 */
	public String getLoginName() {
		return sp.getString("login_name", "");
	}

	public void setLoginName(String t) {
		editor.putString("login_name", t);
		editor.commit();
	}
	
	/**
	 * 昵称
	 */
	public String getNickName() {
		return sp.getString("nick_name", "");
	}

	public void setNickName(String t) {
		editor.putString("nick_name", t);
		editor.commit();
	}
	/**
	 * 验证串
	 */
	public String getUserToken() {
		return sp.getString("user_token", "");
	}

	public void setUserToken(String t) {
		editor.putString("user_token", t);
		editor.commit();
	}
	/**
	 * 二维码
	 */
	public String getBarCode() {
		return sp.getString("erweima_url", "");
	}

	public void setBarCode(String t) {
		editor.putString("erweima_url", t);
		editor.commit();
	}
	/**
	 * 头像
	 */
	public String getAvatar() {
		return sp.getString("avatar", "");
	}

	public void setAvatar(String t) {
		editor.putString("avatar", t);
		editor.commit();
	}
	/**
	 * 昵称
	 */
	public String getStatus() {
		return sp.getString("status", "");
	}

	public void setStatus(String t) {
		editor.putString("status", t);
		editor.commit();
	}
	public void setString(String key,String str){
		editor.putString(key, str);
		editor.commit();
	}
	public String getString(String key){
		return sp.getString(key,0+"");
	}
	
	public String getKeFu() {
		return sp.getString("kefu", "");
	}

	public void setKeFu(String t) {
		editor.putString("kefu", t);
		editor.commit();
	}
	/**
	 * 首次进入提示
	 * @return
	 */
	public boolean getFirstPrompt() {
		return sp.getBoolean("firstprompt", true);
	}

	public void setFirstPrompt(boolean t) {
		editor.putBoolean("firstprompt", t);
		editor.commit();
	}
	//每日签到
	public int getForumDay(){
		return sp.getInt("forumday", 0);
	}

	public void setForumDay(int day){
		editor.putInt("forumday", day);
		editor.commit();
	}

	public void setToken(String token){
		editor.putString("token", token);
		editor.commit();
	}

	public String getToken(){
		return sp.getString("token","");
	}

	public void setHome2Url(String str){
		editor.putString("home2url", str);
		editor.commit();
	}

	public String getHome2Url(){
		return sp.getString("home2url","") ;
	}

	public void setHomeSignMoney(String str){
		editor.putString("setHomeSignMoney", str);
		editor.commit();
	}

	public String getHomeSignMoney(){
		return sp.getString("setHomeSignMoney","0.1") ;
	}

	public void setHomeSignDays(String str){
		editor.putString("getHomeSignDays", str);
		editor.commit();
	}

	public String getHomeSignDays(){
		return sp.getString("getHomeSignDays","1") ;
	}

	public void setSignVersion(boolean b){
		editor.putBoolean("signversion",b);
		editor.commit();
	}

	public boolean getSignVersion(){
		return  sp.getBoolean("signversion",false);
	}


	//获取积分
	public String getScore(){
		return 	sp.getString("userScore","0");
	}

	public void setScore(String score){
		editor.putString("userScore",score);
		editor.commit();
	}

	public String getShareLink(){
		return 	sp.getString("shareLink","");
	}

	public void setShareLink(String shareLink){
		editor.putString("shareLink",shareLink);
		editor.commit();
	}

	public String getQrCodeBtnName(){
		return sp.getString("qrcode_btn_name","二维码平台");
	}
	public void setQRCodeBtnName(String name){
		editor.putString("qrcode_btn_name",name);
		editor.commit();
	}

	public String getPersonalCenterBtnName(){
		return sp.getString("person_btn_name","会员中心");
	}

	public void setPersonalCenterBtnName(String name){
		editor.putString("person_btn_name",name);
		editor.commit();
	}
}
