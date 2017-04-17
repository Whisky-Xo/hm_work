package com.crm.model;


/**
 * 渠道花费
 * 
 * @author sunquan 2016-11-09 10:14  
 * 
 */
public class CostLog extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;//日志id
	private String costId;//花费id
	private Integer operaId;// 操作者ID
	private String operaName;// 操作者姓名
	private String memo;// 操作描述
	private String operaTime;// 操作时间
	private String operaIp;// 操作IP
	private Integer companyId;// 所属企业ID
	private String companyName;// 所属企业名称
	private Boolean isdel;// 删除标识
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCostId() {
		return costId;
	}
	public void setCostId(String costId) {
		this.costId = costId;
	}
	public Integer getOperaId() {
		return operaId;
	}
	public void setOperaId(Integer operaId) {
		this.operaId = operaId;
	}
	public String getOperaName() {
		return operaName;
	}
	public void setOperaName(String operaName) {
		this.operaName = operaName;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getOperaTime() {
		return operaTime;
	}
	public void setOperaTime(String operaTime) {
		this.operaTime = operaTime;
	}
	public String getOperaIp() {
		return operaIp;
	}
	public void setOperaIp(String operaIp) {
		this.operaIp = operaIp;
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
	public Boolean getIsdel() {
		return isdel;
	}
	public void setIsdel(Boolean isdel) {
		this.isdel = isdel;
	}
	
	
	
	
	
	
	


}