package com.crm.model;

/**
 * 数据字典
 * 
 * @author JingChenglong 2016-09-23 14:17
 *
 */
public class Dictionary extends BaseObject {

	private static final long serialVersionUID = 1l;

	private Integer dicId;// 字典ID
	private String dicType;// 字典类型
	private String dicCode;// 编码
	private String dicName;// 名称
	private String memo;// 备注
	private Integer companyId;// 企业ID
	private Boolean isDel;// 删除标识
	private String spare;// 备用字段

	public String getSpare() {
		return spare;
	}

	public void setSpare(String spare) {
		this.spare = spare;
	}

	public Dictionary() {

	}

	public Dictionary(Integer companyId, String dicType) {
		this.dicType = dicType;
		this.companyId = companyId;
		this.isDel = false;
	}

	public Integer getDicId() {
		return dicId;
	}

	public void setDicId(Integer dicId) {
		this.dicId = dicId;
	}

	public String getDicType() {
		return dicType;
	}

	public void setDicType(String dicType) {
		this.dicType = dicType;
	}

	public String getDicCode() {
		return dicCode;
	}

	public void setDicCode(String dicCode) {
		this.dicCode = dicCode;
	}

	public String getDicName() {
		return dicName;
	}

	public void setDicName(String dicName) {
		this.dicName = dicName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Boolean getIsDel() {
		return isDel;
	}

	public void setIsDel(Boolean isDel) {
		this.isDel = isDel;
	}
}