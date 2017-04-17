package com.crm.model;

/**
 * 页面显示信息配置
 * 
 * @author JingChenglong 2016-12-21 09:51
 *
 */
public class PageConf extends BaseObject {

	private static final long serialVersionUID = 1L;

	private Integer id;// ID
	private String action;// 请求action
	private String memo;// 描述
	private String titleTxt;// 头部标题配置
	private String content;// 内容配置
	private String createTime;// 创建时间
	private Integer companyId;// 企业ID
	private Boolean isDel;// 删除标识

	public PageConf(Integer companyId, String action) {
		super();
		this.companyId = companyId;
		this.action = action;
	}

	public PageConf() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTitleTxt() {
		return titleTxt;
	}

	public void setTitleTxt(String titleTxt) {
		this.titleTxt = titleTxt;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public Boolean getIsDel() {
		return isDel;
	}

	public void setIsDel(Boolean isDel) {
		this.isDel = isDel;
	}
}