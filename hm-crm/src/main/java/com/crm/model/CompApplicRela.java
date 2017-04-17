package com.crm.model;

/**
 * 企业应用关联
 * 
 * @author JingChenglong 2016-09-08 20:21
 *
 */
public class CompApplicRela extends BaseObject {

	private static final long serialVersionUID = -8426574155572823299L;

	private Integer relaId;// 关联ID
	private Integer companyId;// 公司ID
	private String companyName;// 所属企业名称
	private Integer applicId;// 应用ID
	private String applicName;// 应用名称
	private String createTime;// 创建时间
	private String createIp;// 创建IP

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getApplicName() {
		return applicName;
	}

	public void setApplicName(String applicName) {
		this.applicName = applicName;
	}

	public Integer getRelaId() {
		return relaId;
	}

	public void setRelaId(Integer relaId) {
		this.relaId = relaId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getApplicId() {
		return applicId;
	}

	public void setApplicId(Integer applicId) {
		this.applicId = applicId;
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
}