package com.crm.model;

/**
 * 转介绍客资分析
 * 
 * @author JingChenglong 2016-11-21 13:24
 *
 */
public class InnnerAnalyze extends BaseObject {

	private static final long serialVersionUID = -7268449345584466343L;

	private String deptId;// 部门ID
	private String deptName;// 部门名称
	private String staffId;// 职工ID
	private String staffName;// 职工姓名
	private String staffPhone;// 职工电话
	private String allNum;// 总数
	private String yxNum;// 有效数
	private String cgNum;// 成功订单数
	private String tbJj;// 提报奖金
	private String companyId;// 企业ID
	private String startTime;// 提报开始时间
	private String endTime;// 提报结束时间

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getStaffPhone() {
		return staffPhone;
	}

	public void setStaffPhone(String staffPhone) {
		this.staffPhone = staffPhone;
	}

	public String getAllNum() {
		return allNum;
	}

	public void setAllNum(String allNum) {
		this.allNum = allNum;
	}

	public String getYxNum() {
		return yxNum;
	}

	public void setYxNum(String yxNum) {
		this.yxNum = yxNum;
	}

	public String getCgNum() {
		return cgNum;
	}

	public void setCgNum(String cgNum) {
		this.cgNum = cgNum;
	}

	public String getTbJj() {
		return tbJj;
	}

	public void setTbJj(String tbJj) {
		this.tbJj = tbJj;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}