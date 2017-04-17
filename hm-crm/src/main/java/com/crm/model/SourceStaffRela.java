package com.crm.model;

/**
 * 渠道职工关联
 * 
 * @author JingChenglong 2016-10-10 19:33
 *
 */
public class SourceStaffRela extends BaseObject {

	private static final long serialVersionUID = -2660193432975103919L;

	private Integer srcId;// 渠道ID
	private String srcName;// 渠道名称
	private String relaType;// 关联类型
	private Integer staffId;// 职工ID
	private String staffName;// 职工姓名
	private Integer typeId;// 类型ID
	private String typeName;// 类型名称
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private Integer creatorId;// 创建者ID
	private String creatorName;// 创建者姓名
	private Integer companyId;// 企业ID
	private String companyName;// 企业名称

	public Integer getSrcId() {
		return srcId;
	}

	public void setSrcId(Integer srcId) {
		this.srcId = srcId;
	}

	public String getSrcName() {
		return srcName;
	}

	public void setSrcName(String srcName) {
		this.srcName = srcName;
	}

	public String getRelaType() {
		return relaType;
	}

	public void setRelaType(String relaType) {
		this.relaType = relaType;
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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
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

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}