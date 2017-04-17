package com.crm.model;

/**
 * 门店
 * 
 * @author JingChenglong 2016-09-08 20:06
 *
 */
public class Shop extends BaseObject {

	private static final long serialVersionUID = 6773428320640909445L;

	private Integer shopId;// 门店主键ID
	private String shopName;// 门店名称
	private String address;// 门店地址
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private String updateTime;// 最后更新时间
	private String updateIp;// 最后更新IP
	private Integer companyId;// 所属企业ID
	private String alipayShopId;// 支付宝门店ID
	private String merchantPid;// 商户ID
	private String merchantShowName;// 口碑显示门店名称
	private String merchantShowPhone;// 口碑显示联系电话
	private String smsPhone;// 接受短信号码
	private Boolean isShow;// 是否开启
	private Boolean isDel;// 删除标识

	public String getSmsPhone() {
		return smsPhone;
	}

	public void setSmsPhone(String smsPhone) {
		this.smsPhone = smsPhone;
	}

	public String getAlipayShopId() {
		return alipayShopId;
	}

	public void setAlipayShopId(String alipayShopId) {
		this.alipayShopId = alipayShopId;
	}

	public String getMerchantPid() {
		return merchantPid;
	}

	public void setMerchantPid(String merchantPid) {
		this.merchantPid = merchantPid;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
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