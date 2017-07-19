package com.hzkj.wdk.model;

import java.util.List;

/**
 * 奖品
 */
public class JiangPinModel {
	private Integer normal;
	private Integer press;
	private boolean check=false;
	private String add_time;
	private String bonus_type;
	private String bonus_id;

	public Integer getNormal() {
		return normal;
	}

	public void setNormal(Integer normal) {
		this.normal = normal;
	}

	public Integer getPress() {
		return press;
	}

	public void setPress(Integer press) {
		this.press = press;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getBonus_type() {
		return bonus_type;
	}

	public void setBonus_type(String bonus_type) {
		this.bonus_type = bonus_type;
	}

	public String getBonus_id() {
		return bonus_id;
	}

	public void setBonus_id(String bonus_id) {
		this.bonus_id = bonus_id;
	}
}
