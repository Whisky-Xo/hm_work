package com.crm.model;

/**
 * 部门小组关联
 * 
 * @author JingChenglong 2016-09-08 20:12
 *
 */
public class GroupStaffRela extends BaseObject {

	private static final long serialVersionUID = 4107854915412010530L;

	private Integer relaId;// 关联ID
	private Integer groupId;// 小组ID
	private String groupName;// 小组名称
	private Integer staffId;// 职员ID
	private String staffName;// 职员名称
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private Integer companyId;// 所属企业ID
	private String companyName;// 所属企业名称

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
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

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
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