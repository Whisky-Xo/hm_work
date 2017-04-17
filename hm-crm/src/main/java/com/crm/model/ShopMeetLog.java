package com.crm.model;

import java.io.Serializable;

/**
 * 门店洽谈记录
 * 
 * @author JingChenglong 2016-11-16 16:16
 *
 */
public class ShopMeetLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String qtLogId;// 洽谈记录ID
	private String kzId;// 客资ID
	private String rstCode;// 洽谈记录编码
	private String rstLabel;// 洽谈记录描述
	private String runOffCode;// 流失原因编码
	private String runOffLabel;// 流失原因描述
	private String operaId;// 操作者ID
	private String operaName;// 操作者姓名
	private String qtMemo;// 洽谈备注
	private String arriveTime;// 到店时间
	private String recepterId;// 接待门市ID
	private String recepterName;// 接待门市姓名
	private String createTime;// 记录创建时间
	private String createIp;// 记录创建IP
	private String companyId;// 企业ID
	private String shopId;// 门店ID
	private String shopName;// 门店名称
	private String amount;// 成交金额

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getQtLogId() {
		return qtLogId;
	}

	public void setQtLogId(String qtLogId) {
		this.qtLogId = qtLogId;
	}

	public String getKzId() {
		return kzId;
	}

	public void setKzId(String kzId) {
		this.kzId = kzId;
	}

	public String getRstCode() {
		return rstCode;
	}

	public void setRstCode(String rstCode) {
		this.rstCode = rstCode;
	}

	public String getRstLabel() {
		return rstLabel;
	}

	public void setRstLabel(String rstLabel) {
		this.rstLabel = rstLabel;
	}

	public String getRunOffCode() {
		return runOffCode;
	}

	public void setRunOffCode(String runOffCode) {
		this.runOffCode = runOffCode;
	}

	public String getRunOffLabel() {
		return runOffLabel;
	}

	public void setRunOffLabel(String runOffLabel) {
		this.runOffLabel = runOffLabel;
	}

	public String getOperaId() {
		return operaId;
	}

	public void setOperaId(String operaId) {
		this.operaId = operaId;
	}

	public String getOperaName() {
		return operaName;
	}

	public void setOperaName(String operaName) {
		this.operaName = operaName;
	}

	public String getQtMemo() {
		return qtMemo;
	}

	public void setQtMemo(String qtMemo) {
		this.qtMemo = qtMemo;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getRecepterId() {
		return recepterId;
	}

	public void setRecepterId(String recepterId) {
		this.recepterId = recepterId;
	}

	public String getRecepterName() {
		return recepterName;
	}

	public void setRecepterName(String recepterName) {
		this.recepterName = recepterName;
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

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
}