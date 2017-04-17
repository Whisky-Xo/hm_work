package com.crm.model;

/**
 * 客资状态
 * 
 * @author JingChenglong 2016-09-09 10:32
 *
 */
public class Status extends BaseObject {

	private static final long serialVersionUID = 1l;

	public static final String STATUS_TYPE_DS = "/ds/";
	public static final String STATUS_TYPE_ZJS = "/zjs/";

	private Integer id;// 主键ID
	private Integer statusId;// 状态ID
	private String statusName;// 状态名称
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private String updateTime;// 最后更新时间
	private String updateIp;// 最后更新IP
	private Integer priority;// 显示顺序优先级
	private Boolean isShow;// 是否启用
	private Boolean isDel;// 删除标识
	private String statusType;// 状态类型
	private Integer companyId;// 企业ID

	public Status(Integer companyId, String statusType) {
		this.companyId = companyId;
		this.statusType = statusType;
	}

	public Status() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
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

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateIp() {
		return updateIp;
	}

	public void setUpdateIp(String updateIp) {
		this.updateIp = updateIp;
	}

	public Boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	public Boolean getIsDel() {
		return isDel;
	}

	public void setIsDel(Boolean isDel) {
		this.isDel = isDel;
	}
}