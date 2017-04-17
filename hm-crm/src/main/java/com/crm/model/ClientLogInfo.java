package com.crm.model;

import java.io.Serializable;

/**
 * 客资操作记录信息
 * 
 * @author JingChenglong 2016-09-09 09:57
 *
 */
public class ClientLogInfo implements Serializable {

	private static final long serialVersionUID = 1l;

	private String logId;// 日志ID
	private String kzId;// 客资ID
	private Integer operaId;// 操作者ID
	private String operaName;// 操作者姓名
	private String memo;// 操作描述
	private String operaTime;// 操作时间
	private String operaIp;// 操作IP
	private Integer companyId;// 所属企业ID
	private String companyName;// 所属企业名称
	private Boolean isdel;// 删除标识

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getOperaId() {
		return operaId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
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

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getKzId() {
		return kzId;
	}

	public void setKzId(String kzId) {
		this.kzId = kzId;
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

	public Boolean getIsdel() {
		return isdel;
	}

	public void setIsdel(Boolean isdel) {
		this.isdel = isdel;
	}
}