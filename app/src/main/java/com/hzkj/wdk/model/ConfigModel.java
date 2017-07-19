package com.hzkj.wdk.model;

import java.io.Serializable;
import java.util.List;

/**
 * 用户
 * 
 * @author berton
 * 
 */
public class ConfigModel implements Serializable {

	private String baner_image;
	private String banner_url;
	private String invite_des;
	private String payvip_des;
	private String vipbonus_invites_count;
	private String supervipbonus_invites_count;
	private String sign_auth_code;
	private String add_fans_name;
	private String be_added_fans_name;
	private String sign_auth_des;

	private List<VipModel> listViPs;
	private  List<FunctionModel>listFunction;

	public List<VipModel> getListViPs() {
		return listViPs;
	}

	public void setListViPs(List<VipModel> listViPs) {
		this.listViPs = listViPs;
	}

	public List<FunctionModel> getListFunction() {
		return listFunction;
	}

	public void setListFunction(List<FunctionModel> listFunction) {
		this.listFunction = listFunction;
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
