package com.crm.model;

/**
 * 消息
 * 
 * @author JingChenglong 2016-10-11 14:56
 *
 */
public class News extends BaseObject {

	private static final long serialVersionUID = 6587040112661729954L;

	private Integer id;// 消息ID
	private String type;// 消息类型
	private String title;// 标题
	private String spare1;// 备用字段1
	private String spare2;// 备用字段2
	private String spare3;// 备用字段3
	private String spare4;// 备用字段4
	private String spare5;// 备用字段5
	private String spare6;// 备用字段6
	private Integer staffId;// 职工ID
	private String staffName;// 职工姓名
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private Integer companyId;// 企业ID
	private Boolean isRead;// 是否已读

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getSpare4() {
		return spare4;
	}

	public void setSpare4(String spare4) {
		this.spare4 = spare4;
	}

	public String getSpare5() {
		return spare5;
	}

	public void setSpare5(String spare5) {
		this.spare5 = spare5;
	}

	public String getSpare6() {
		return spare6;
	}

	public void setSpare6(String spare6) {
		this.spare6 = spare6;
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateIp() {
		return createIp;
	}

	public void setCreateIp(String createIp) {
		this.createIp = createIp;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
}