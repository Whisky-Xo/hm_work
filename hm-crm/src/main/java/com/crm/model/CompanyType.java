package com.crm.model;

/**
 * 企业类型
 * 
 * @author JingChenglong 2016-09-08 20:04
 *
 */
public class CompanyType extends BaseObject {

	private static final long serialVersionUID = 8340561892041412322L;

	private Integer typeId;// 主键类型ID
	private String typeName;// 类型名称
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private String updateTime;// 最后更新时间
	private String updateIp;// 最后更新IP
	private String memo;// 类型说明
	private Boolean isShow;// 是否开启
	private Boolean isDel;// 删除标识

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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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