package com.crm.model;

import java.io.Serializable;

/**
 * 短信配置
 * 
 * @author JingChenglong 2016-12-28 10:30
 *
 */
public class SmsCode implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String YYJD = "yyjd";// 预约进店

	private Integer id;// 主键
	private String smsType;// 短信类型
	private String sign;// 签名
	private String templateId;// 阿里大于模板ID
	private String memo;// 描述
	private String spare1;// 备用字段1
	private String spare2;// 备用字段2
	private String spare3;// 备用字段3
	private Integer companyId;// 企业ID
	private Boolean isShow;// 是否开启
	private String url;// 大于url
	private String appKey;// 大于appkey
	private String secret;// 大于秘钥

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

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

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getSpare1() {
		return spare1;
	}

	public void setSpare1(String spare1) {
		this.spare1 = spare1;
	}

	public String getSpare2() {
		return spare2;
	}

	public void setSpare2(String spare2) {
		this.spare2 = spare2;
	}

	public String getSpare3() {
		return spare3;
	}

	public void setSpare3(String spare3) {
		this.spare3 = spare3;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}
}