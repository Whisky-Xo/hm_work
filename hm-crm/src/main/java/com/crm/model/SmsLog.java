package com.crm.model;

import java.io.Serializable;

/**
 * 短信日志
 * 
 * @author SunQuan 2016-01-04 16:34
 *
 */
public class SmsLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;// 主键
	private String smsType;// 短信类型
	private String tarPhone;// 发送的手机号
	private String templateId;// 阿里大于模板ID
	private String content;// 短信内容
	private String operaId;//操作者id
	private String createTime;//创建时间
	private Integer companyId;// 企业ID
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSmsType() {
		return smsType;
	}
	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}
	public String getTarPhone() {
		return tarPhone;
	}
	public void setTarPhone(String tarPhone) {
		this.tarPhone = tarPhone;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOperaId() {
		return operaId;
	}
	public void setOperaId(String operaId) {
		this.operaId = operaId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
	
	
	
	

	
}