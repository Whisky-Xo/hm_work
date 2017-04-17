package com.crm.model;

/**
 * 员工角色关联
 * 
 * @author JingChenglong 2016-09-08 20:39
 *
 */
public class StaffRoleRela extends BaseObject {

	private static final long serialVersionUID = -5548347452221483493L;

	private Integer relaId;// 关联ID
	private Integer staffId;// 员工ID
	private String staffName;// 员工姓名
	private Integer roleId;// 角色ID
	private String roleName;// 角色名称
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private Integer companyId;// 所属企业ID
	private String companyName;// 所属企业名称

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getRelaId() {
		return relaId;
	}

	public void setRelaId(Integer relaId) {
		this.relaId = relaId;
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
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
}