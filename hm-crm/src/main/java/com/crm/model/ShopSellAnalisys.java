package com.crm.model;

import java.io.Serializable;

/**
 * 门店销售分析
 * 
 * @author JingChenglong 2016-11-29 18:34
 *
 */
public class ShopSellAnalisys implements Serializable {

	private static final long serialVersionUID = 1L;

	private String start;// 起始时间
	private String end;// 截止时间
	private String shopId;// 门店ID
	private String companyId;// 企业ID
	private String staffId;// 职工ID

	/*-- 合计 --*/
	private Integer kzNumYy;// 预约总数
	private Integer kzNumAll;// 客资总量
	private Integer cjNumAll;// 总成交量
	private String cjRateAll;// 总成交率
	private String amountAll;// 总销售总额
	private String amountAvgAll;// 总销售均价

	/*-- 网络渠道 --*/
	private Integer kzNumNet;// 网络渠道入客量
	private Integer cjNumNet;// 网络渠道成交量
	private String cjRateNet;// 网络渠道成交率
	private String amountNet;// 网络渠道销售总额
	private String amountAvgNet;// 网络渠道销售均价

	/*-- 门市指明渠道 --*/
	private Integer kzNumSale;// 门市指明渠道入客量
	private Integer cjNumSale;// 门市指明渠道成交量
	private String cjRateSale;// 门市指明渠道成交率
	private String amountSale;// 门市指明渠道销售总额
	private String amountAvgSale;// 门市指明渠道销售均价

	/*-- 员工转介绍渠道 --*/
	private Integer kzNumZjs;// 员工转介绍渠道入客量
	private Integer cjNumZjs;// 员工转介绍渠道成交量
	private String cjRateZjs;// 员工转介绍渠道成交率
	private String amountZjs;// 员工转介绍渠道销售总额
	private String amountAvgZjs;// 员工转介绍渠道销售均价

	/*--  自然入客渠道 --*/
	private Integer kzNumNature;// 自然入客渠道入客量
	private Integer cjNumNature;// 自然入客渠道成交量
	private String cjRateNature;// 自然入客渠道成交率
	private String amountNature;// 自然入客渠道销售总额
	private String amountAvgNature;// 自然入客渠道销售均价

	/*-- 排行榜 --*/
	private String saleAmountLimit;// 销售金额排行前三名ID,","分割
	private String saleNumLimit;// 销售单量排行前三名ID,","分割

	public Integer getKzNumYy() {
		return kzNumYy;
	}

	public void setKzNumYy(Integer kzNumYy) {
		this.kzNumYy = kzNumYy;
	}

	public String getSaleAmountLimit() {
		return saleAmountLimit;
	}

	public void setSaleAmountLimit(String saleAmountLimit) {
		this.saleAmountLimit = saleAmountLimit;
	}

	public String getSaleNumLimit() {
		return saleNumLimit;
	}

	public void setSaleNumLimit(String saleNumLimit) {
		this.saleNumLimit = saleNumLimit;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public Integer getKzNumAll() {
		return kzNumAll;
	}

	public void setKzNumAll(Integer kzNumAll) {
		this.kzNumAll = kzNumAll;
	}

	public Integer getCjNumAll() {
		return cjNumAll;
	}

	public void setCjNumAll(Integer cjNumAll) {
		this.cjNumAll = cjNumAll;
	}

	public String getCjRateAll() {
		return cjRateAll;
	}

	public void setCjRateAll(String cjRateAll) {
		this.cjRateAll = cjRateAll;
	}

	public String getAmountAll() {
		return amountAll;
	}

	public void setAmountAll(String amountAll) {
		this.amountAll = amountAll;
	}

	public String getAmountAvgAll() {
		return amountAvgAll;
	}

	public void setAmountAvgAll(String amountAvgAll) {
		this.amountAvgAll = amountAvgAll;
	}

	public Integer getKzNumNet() {
		return kzNumNet;
	}

	public void setKzNumNet(Integer kzNumNet) {
		this.kzNumNet = kzNumNet;
	}

	public Integer getCjNumNet() {
		return cjNumNet;
	}

	public void setCjNumNet(Integer cjNumNet) {
		this.cjNumNet = cjNumNet;
	}

	public String getCjRateNet() {
		return cjRateNet;
	}

	public void setCjRateNet(String cjRateNet) {
		this.cjRateNet = cjRateNet;
	}

	public String getAmountNet() {
		return amountNet;
	}

	public void setAmountNet(String amountNet) {
		this.amountNet = amountNet;
	}

	public String getAmountAvgNet() {
		return amountAvgNet;
	}

	public void setAmountAvgNet(String amountAvgNet) {
		this.amountAvgNet = amountAvgNet;
	}

	public Integer getKzNumSale() {
		return kzNumSale;
	}

	public void setKzNumSale(Integer kzNumSale) {
		this.kzNumSale = kzNumSale;
	}

	public Integer getCjNumSale() {
		return cjNumSale;
	}

	public void setCjNumSale(Integer cjNumSale) {
		this.cjNumSale = cjNumSale;
	}

	public String getCjRateSale() {
		return cjRateSale;
	}

	public void setCjRateSale(String cjRateSale) {
		this.cjRateSale = cjRateSale;
	}

	public String getAmountSale() {
		return amountSale;
	}

	public void setAmountSale(String amountSale) {
		this.amountSale = amountSale;
	}

	public String getAmountAvgSale() {
		return amountAvgSale;
	}

	public void setAmountAvgSale(String amountAvgSale) {
		this.amountAvgSale = amountAvgSale;
	}

	public Integer getKzNumZjs() {
		return kzNumZjs;
	}

	public void setKzNumZjs(Integer kzNumZjs) {
		this.kzNumZjs = kzNumZjs;
	}

	public Integer getCjNumZjs() {
		return cjNumZjs;
	}

	public void setCjNumZjs(Integer cjNumZjs) {
		this.cjNumZjs = cjNumZjs;
	}

	public String getCjRateZjs() {
		return cjRateZjs;
	}

	public void setCjRateZjs(String cjRateZjs) {
		this.cjRateZjs = cjRateZjs;
	}

	public String getAmountZjs() {
		return amountZjs;
	}

	public void setAmountZjs(String amountZjs) {
		this.amountZjs = amountZjs;
	}

	public String getAmountAvgZjs() {
		return amountAvgZjs;
	}

	public void setAmountAvgZjs(String amountAvgZjs) {
		this.amountAvgZjs = amountAvgZjs;
	}

	public Integer getKzNumNature() {
		return kzNumNature;
	}

	public void setKzNumNature(Integer kzNumNature) {
		this.kzNumNature = kzNumNature;
	}

	public Integer getCjNumNature() {
		return cjNumNature;
	}

	public void setCjNumNature(Integer cjNumNature) {
		this.cjNumNature = cjNumNature;
	}

	public String getCjRateNature() {
		return cjRateNature;
	}

	public void setCjRateNature(String cjRateNature) {
		this.cjRateNature = cjRateNature;
	}

	public String getAmountNature() {
		return amountNature;
	}

	public void setAmountNature(String amountNature) {
		this.amountNature = amountNature;
	}

	public String getAmountAvgNature() {
		return amountAvgNature;
	}

	public void setAmountAvgNature(String amountAvgNature) {
		this.amountAvgNature = amountAvgNature;
	}
}