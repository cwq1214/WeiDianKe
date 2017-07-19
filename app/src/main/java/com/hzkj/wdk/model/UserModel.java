package com.hzkj.wdk.model;

import java.io.Serializable;
import java.util.List;

/**
 * 用户
 * 
 * @author berton
 * 
 */
public class UserModel implements Serializable {
	private static final long serialVersionUID = 1L;
    private String token;
	private String user_id;
	private String incomes;
	private boolean is_fansing;
	private boolean is_signed;
	private String has_signed_days;
	private String baner_image;
	private String banner_url;
	private String user_lv;
	private String available_fans_count;
	private String expiration;
	private String invites;
	private String remains;
	private String weichat_contacts_url;
	private String instruction_url;
	private String invite_des;
	private String payvip_des;
	private String vipbonus_invites_count;
	private String supervipbonus_invites_count;
	private String sign_auth_code;
	private String add_fans_name;
	private String be_added_fans_name;
	private String sign_auth_des;
	private List<VipModel> listViPs;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getIncomes() {
		return incomes;
	}

	public void setIncomes(String incomes) {
		this.incomes = incomes;
	}

	public String getHas_signed_days() {
		return has_signed_days;
	}

	public void setHas_signed_days(String has_signed_days) {
		this.has_signed_days = has_signed_days;
	}

	public String getBaner_image() {
		return baner_image;
	}

	public void setBaner_image(String baner_image) {
		this.baner_image = baner_image;
	}

	public String getBanner_url() {
		return banner_url;
	}

	public void setBanner_url(String banner_url) {
		this.banner_url = banner_url;
	}

	public String getUser_lv() {
		return user_lv;
	}

	public void setUser_lv(String user_lv) {
		this.user_lv = user_lv;
	}

	public String getAvailable_fans_count() {
		return available_fans_count;
	}

	public void setAvailable_fans_count(String available_fans_count) {
		this.available_fans_count = available_fans_count;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getInvites() {
		return invites;
	}

	public void setInvites(String invites) {
		this.invites = invites;
	}

	public String getRemains() {
		return remains;
	}

	public void setRemains(String remains) {
		this.remains = remains;
	}

	public String getWeichat_contacts_url() {
		return weichat_contacts_url;
	}

	public void setWeichat_contacts_url(String weichat_contacts_url) {
		this.weichat_contacts_url = weichat_contacts_url;
	}

	public String getInstruction_url() {
		return instruction_url;
	}

	public void setInstruction_url(String instruction_url) {
		this.instruction_url = instruction_url;
	}

	public List<VipModel> getListViPs() {
		return listViPs;
	}

	public void setListViPs(List<VipModel> listViPs) {
		this.listViPs = listViPs;
	}

	public boolean is_fansing() {
		return is_fansing;
	}

	public void setIs_fansing(boolean is_fansing) {
		this.is_fansing = is_fansing;
	}

	public boolean is_signed() {
		return is_signed;
	}

	public void setIs_signed(boolean is_signed) {
		this.is_signed = is_signed;
	}


	public String getInvite_des() {
		return invite_des;
	}

	public void setInvite_des(String invite_des) {
		this.invite_des = invite_des;
	}

	public String getPayvip_des() {
		return payvip_des;
	}

	public void setPayvip_des(String payvip_des) {
		this.payvip_des = payvip_des;
	}

	public String getVipbonus_invites_count() {
		return vipbonus_invites_count;
	}

	public void setVipbonus_invites_count(String vipbonus_invites_count) {
		this.vipbonus_invites_count = vipbonus_invites_count;
	}

	public String getSupervipbonus_invites_count() {
		return supervipbonus_invites_count;
	}

	public void setSupervipbonus_invites_count(String supervipbonus_invites_count) {
		this.supervipbonus_invites_count = supervipbonus_invites_count;
	}

	public String getSign_auth_code() {
		return sign_auth_code;
	}

	public void setSign_auth_code(String sign_auth_code) {
		this.sign_auth_code = sign_auth_code;
	}

	public String getAdd_fans_name() {
		return add_fans_name;
	}

	public void setAdd_fans_name(String add_fans_name) {
		this.add_fans_name = add_fans_name;
	}

	public String getBe_added_fans_name() {
		return be_added_fans_name;
	}

	public void setBe_added_fans_name(String be_added_fans_name) {
		this.be_added_fans_name = be_added_fans_name;
	}

	public String getSign_auth_des() {
		return sign_auth_des;
	}

	public void setSign_auth_des(String sign_auth_des) {
		this.sign_auth_des = sign_auth_des;
	}
}
