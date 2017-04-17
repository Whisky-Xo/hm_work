package com.crm.model;

/**
 * 角色权限关联
 * 
 * @author JingChenglong 2016-09-08 20:36
 *
 */
public class RolePmsRela extends BaseObject {

	private static final long serialVersionUID = -5812696394411654914L;

	private Integer relaId;// 关联ID
	private Integer roleId;// 角色ID
	private String roleName;// 角色名称
	private Integer pmsId;// 权限ID
	private String pmsName;// 权限名称
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private Integer companyId;// 所属企业ID
	private String companyName;// 所属企业名称

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getPmsName() {
		return pmsName;
	}

	public void setPmsName(String pmsName) {
		this.pmsName = pmsName;
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

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getPmsId() {
		return pmsId;
	}

	public void setPmsId(Integer pmsId) {
		this.pmsId = pmsId;
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