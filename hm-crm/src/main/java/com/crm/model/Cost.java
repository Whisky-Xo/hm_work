package com.crm.model;

import java.math.BigDecimal;

/**
 * 渠道花费
 * 
 * @author sunquan 2016-11-09 10:14  
 * 
 */
public class Cost extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;//花费id
	private BigDecimal cost;//花费金额
	private Integer srcId;//渠道id
	private String costTime;//日期
	private Integer companyId;//公司id
	private String createTime;//创建时间
	private String updateTime;//修改时间
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	
	
	public Integer getSrcId() {
		return srcId;
	}
	public void setSrcId(Integer srcId) {
		this.srcId = srcId;
	}
	public String getCostTime() {
		return costTime;
	}
	public void setCostTime(String costTime) {
		this.costTime = costTime;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	

}