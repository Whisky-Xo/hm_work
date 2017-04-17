package com.crm.model;

/**
 * 定时任务
 * 
 * @author JingChengInteger 2016-11-03 16:49
 *
 */
public class TimeWarn extends BaseObject {

	private static final long serialVersionUID = -1694563054924866920L;

	private Integer id;// 主键
	private String type;// 提醒类型
	private Integer warnTime;// 提醒时间
	private Integer targetId;// 目标职工ID
	private String msg; // 消息
	private String spare1;// 附属1
	private String spare2;// 附属2
	private String spare3;// 附属3
	private Integer createId;// 时间创建者
	private String createTime;// 创建时间
	private Integer companyId;// 企业ID
	private Boolean isDel;// 删除标识

	private Integer warnTimeStart;// 提醒时间开始
	private Integer warnTimeEnd;// 提醒时间结束

	public Integer getWarnTimeStart() {
		return warnTimeStart;
	}

	public void setWarnTimeStart(Integer warnTimeStart) {
		this.warnTimeStart = warnTimeStart;
	}

	public Integer getWarnTimeEnd() {
		return warnTimeEnd;
	}

	public void setWarnTimeEnd(Integer warnTimeEnd) {
		this.warnTimeEnd = warnTimeEnd;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getWarnTime() {
		return warnTime;
	}

	public void setWarnTime(Integer warnTime) {
		this.warnTime = warnTime;
	}

	public Integer getTargetId() {
		return targetId;
	}

	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSpare1() {
		return spare1;
	}

	public void setSpare1(String spare1) {
		this.spare1 = spare1;
	}

	public String getSpare2() {
		return spare2;
	}

	public void setSpare2(String spare2) {
		this.spare2 = spare2;
	}

	public String getSpare3() {
		return spare3;
	}

	public void setSpare3(String spare3) {
		this.spare3 = spare3;
	}

	public Integer getCreateId() {
		return createId;
	}

	public void setCreateId(Integer createId) {
		this.createId = createId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Boolean getIsDel() {
		return isDel;
	}

	public void setIsDel(Boolean isDel) {
		this.isDel = isDel;
	}
}