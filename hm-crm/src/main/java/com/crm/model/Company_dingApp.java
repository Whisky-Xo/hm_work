package com.crm.model;

/**
 * 公司与应用关系表
 * 
 * @author jzl
 *
 */
public class Company_dingApp extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8130817012234231102L;

	private int companyId; // 公司ID

	private int dingAppId; // 应用ID

	private int status; // 应用启用状态 1 启用 0 关闭

	private int isshow; // 应用是否显示 1显示 0 隐藏

	private int id;

	private int created;

	private int updated;

	private String createTime;

	private String createIp;

	private String updateTime;

	private String updateIp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCreated() {
		return created;
	}

	public void setCreated(int created) {
		this.created = created;
	}

	public int getUpdated() {
		return updated;
	}

	public void setUpdated(int updated) {
		this.updated = updated;
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

	public int getDingAppId() {
		return dingAppId;
	}

	public void setDingAppId(int dingAppId) {
		this.dingAppId = dingAppId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIsshow() {
		return isshow;
	}

	public void setIsshow(int isshow) {
		this.isshow = isshow;
	}

}
