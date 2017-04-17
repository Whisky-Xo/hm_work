package com.crm.model;

/**
 * 清莹产品应用信息
 * 
 * @author JingChenglong 2016-09-08 20:14  
 * 
 */
public class Application extends BaseObject {

	private static final long serialVersionUID = -6454554308035110440L;

	private Integer applicId;// 应用主键ID
	private String applicName;// 应用名称
	private String url;// URL地址
	private String img;// 图片地址
	private String priority;// 序号优先级
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private String updateTime;// 最后更新时间
	private String updateIp;// 最后更新IP
	private Boolean isShow;// 是否开启
	private Boolean isDel;// 删除标识

	public Integer getApplicId() {
		return applicId;
	}

	public void setApplicId(Integer applicId) {
		this.applicId = applicId;
	}

	public String getApplicName() {
		return applicName;
	}

	public void setApplicName(String applicName) {
		this.applicName = applicName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
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