package com.crm.model;

/**
 * 企业（婚庆店）
 * 
 * @author JingChenglong 2016-09-08 19:55
 *
 */
public class Company extends BaseObject {

	private static final long serialVersionUID = 1L;

	private Integer compId;// 企业主键ID
	private String compName;// 企业名称
	private String compDistrict;// 企业区域
	private Integer compTypeId;// 企业类型ID
	private String compType;// 企业类型
	private String webSite;// 企业网址
	private String banner;// 企业标语
	private String logo;// LOGO图片地址
	private String memo;// 备注
	private String color;// 顶栏背景色
	private String corpId;// 钉钉关联ID
	private String merchantPid;// 口碑商户ID
	private String merchantShowName;// 商铺显示名称
	private String merchantShowPhone;// 商铺联系电话
	private String smsPhone;// 短信接收电话
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private String updateTime;// 最后更新时间
	private String updateIp;// 最后更新IP
	private Boolean isTip;// 提示标识
	private Boolean isDel;// 删除标识
	private String templatenum;
	private String corpSecret;
	private String ssoSecret;
	private String channelSecret;
	private String agentId;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getCompTypeId() {
		return compTypeId;
	}

	public void setCompTypeId(Integer compTypeId) {
		this.compTypeId = compTypeId;
	}

	public String getSmsPhone() {
		return smsPhone;
	}

	public void setSmsPhone(String smsPhone) {
		this.smsPhone = smsPhone;
	}

	public Integer getCompId() {
		return compId;
	}

	public void setCompId(Integer compId) {
		this.compId = compId;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getCompDistrict() {
		return compDistrict;
	}

	public void setCompDistrict(String compDistrict) {
		this.compDistrict = compDistrict;
	}

	public String getCompType() {
		return compType;
	}

	public void setCompType(String compType) {
		this.compType = compType;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getMerchantPid() {
		return merchantPid;
	}

	public void setMerchantPid(String merchantPid) {
		this.merchantPid = merchantPid;
	}

	public String getMerchantShowName() {
		return merchantShowName;
	}

	public void setMerchantShowName(String merchantShowName) {
		this.merchantShowName = merchantShowName;
	}

	public String getMerchantShowPhone() {
		return merchantShowPhone;
	}

	public void setMerchantShowPhone(String merchantShowPhone) {
		this.merchantShowPhone = merchantShowPhone;
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

	public Boolean getIsTip() {
		return isTip;
	}

	public void setIsTip(Boolean isTip) {
		this.isTip = isTip;
	}

	public Boolean getIsDel() {
		return isDel;
	}

	public void setIsDel(Boolean isDel) {
		this.isDel = isDel;
	}

	public String getCorpSecret() {
		return corpSecret;
	}

	public void setCorpSecret(String corpSecret) {
		this.corpSecret = corpSecret;
	}

	public String getSsoSecret() {
		return ssoSecret;
	}

	public void setSsoSecret(String ssoSecret) {
		this.ssoSecret = ssoSecret;
	}

	public String getChannelSecret() {
		return channelSecret;
	}

	public void setChannelSecret(String channelSecret) {
		this.channelSecret = channelSecret;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}


	public String getTemplatenum() {
		return templatenum;
	}

	public void setTemplatenum(String templatenum) {
		this.templatenum = templatenum;
	}
}