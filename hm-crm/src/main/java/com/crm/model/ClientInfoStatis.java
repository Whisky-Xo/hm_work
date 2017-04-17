package com.crm.model;

import java.io.Serializable;

/**
 * 邀约员客资统计
 * 
 * @author JingChenglong 2016-10-31 10:21
 *
 */
public class ClientInfoStatis implements Serializable {

	private static final long serialVersionUID = 1l;

	private String companyId;// 企业ID
	private String sourceId;// 渠道ID
	private String shopId;// 门店ID

	/*-- 采集统计 --*/
	private String collectorIds;// 采集员ID（多个时","分割）

	private Integer numCollAllMonth;// 月采集客资总数
	private Integer numCollValidMonth;// 月采集客资有效数
	private String numCollValidRateMonth;// 月采集客资有效率

	private Integer numCollAllDay;// 日采集客资总数
	private Integer numCollValidDay;// 日采集客资有效数
	private String numCollValidRateDay;// 日采集客资有效率

	private Integer numCollAllHistory;// 历史采集客资总数
	private Integer numCollValidHistory;// 历史采集客资有效总数
	private String numCollValidRateHistory;// 历史采集客资有效率

	private Integer collSuccessNumMonth;// 月成功订单个数
	private String collSuccessRateMonth;// 月转化率

	private Integer collSuccessNumHistory;// 历史成功订单个数
	private String collSuccessRateHistory;// 历史转化率

	private String collectorMonthBonus;// 月客资提报奖金
	private String collectorHistoryBonus;// 历史客资提报奖金

	/*-- 邀约统计 --*/
	private String appointIds;// 邀约员ID（多个时逗号","分隔）

	private Integer numAllMonth;// 月客资总数
	private Integer numValidMonth;// 月有效客资
	private String numValidRateMonth;// 月客资有效率

	private Integer numAllDay;// 日客资总数
	private Integer numValidDay;// 日有效客资
	private String numValidRateDay;// 日客资有效率

	private Integer beCallNumDay;// 今日待联系客资总数

	private Integer comeShopNumAllMonth;// 月总入店量
	private Integer comeShopNumInvalidMonth;// 月有效入店量
	private String comeShopRateMonth;// 月咨询入店率
	private Integer comeShopNumAllToday;// 日总入店量
	private Integer comeShopNumInvalidToday;// 日有效入店量

	private Integer successNumMonth;// 月成交量
	private Integer successNumToday;// 日成交量
	private String successRateMonth;// 月成交率

	private Integer weddingNearNum;// 婚期临近客资数
	private Integer todayComeShopNum;// 预计今日到店客资数
	private Integer yyFailNumDay;// 今日邀约失败客资数
	private Integer yyFailNumMonth;// 月邀约失败客资数
	private String yyFailRateDay;// 日邀约失败率
	private String yyFailRateMonth;// 月邀约失败率
	private Integer longNoConnNum;// 长期不联系客资数

	private Integer beCheckNum;// 待确定无效客资个数

	/*-- 门店统计 --*/
	private Integer rdRcNumToday;// 日入店量（入店客资人次数）
	private Integer rdNumToday;// 日入店量（入店客资个数）
	private Integer rdNewKzNumToday;// 入店新客资数
	private Integer cjNumToday;// 日成交量
	private String cjRateToday;// 日成交率
	private Integer lsNumToday;// 日流失量

	public ClientInfoStatis() {

	}

	public ClientInfoStatis(String type) {
		this.numCollValidRateMonth = "-";
		this.cjRateToday = "-";
		this.successRateMonth = "-";
		this.comeShopRateMonth = "-";
		this.numValidRateDay = "-";
		this.collSuccessRateMonth = "-";
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public Integer getRdRcNumToday() {
		return rdRcNumToday;
	}

	public void setRdRcNumToday(Integer rdRcNumToday) {
		this.rdRcNumToday = rdRcNumToday;
	}

	public Integer getRdNumToday() {
		return rdNumToday;
	}

	public void setRdNumToday(Integer rdNumToday) {
		this.rdNumToday = rdNumToday;
	}

	public Integer getRdNewKzNumToday() {
		return rdNewKzNumToday;
	}

	public void setRdNewKzNumToday(Integer rdNewKzNumToday) {
		this.rdNewKzNumToday = rdNewKzNumToday;
	}

	public Integer getCjNumToday() {
		return cjNumToday;
	}

	public void setCjNumToday(Integer cjNumToday) {
		this.cjNumToday = cjNumToday;
	}

	public String getCjRateToday() {
		return cjRateToday;
	}

	public void setCjRateToday(String cjRateToday) {
		this.cjRateToday = cjRateToday;
	}

	public Integer getLsNumToday() {
		return lsNumToday;
	}

	public void setLsNumToday(Integer lsNumToday) {
		this.lsNumToday = lsNumToday;
	}

	public Integer getSuccessNumMonth() {
		return successNumMonth;
	}

	public void setSuccessNumMonth(Integer successNumMonth) {
		this.successNumMonth = successNumMonth;
	}

	public Integer getSuccessNumToday() {
		return successNumToday;
	}

	public void setSuccessNumToday(Integer successNumToday) {
		this.successNumToday = successNumToday;
	}

	public String getSuccessRateMonth() {
		return successRateMonth;
	}

	public void setSuccessRateMonth(String successRateMonth) {
		this.successRateMonth = successRateMonth;
	}

	public Integer getComeShopNumAllToday() {
		return comeShopNumAllToday;
	}

	public void setComeShopNumAllToday(Integer comeShopNumAllToday) {
		this.comeShopNumAllToday = comeShopNumAllToday;
	}

	public Integer getComeShopNumInvalidToday() {
		return comeShopNumInvalidToday;
	}

	public void setComeShopNumInvalidToday(Integer comeShopNumInvalidToday) {
		this.comeShopNumInvalidToday = comeShopNumInvalidToday;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getCollectorIds() {
		return collectorIds;
	}

	public void setCollectorIds(String collectorIds) {
		this.collectorIds = collectorIds;
	}

	public Integer getNumCollAllMonth() {
		return numCollAllMonth;
	}

	public void setNumCollAllMonth(Integer numCollAllMonth) {
		this.numCollAllMonth = numCollAllMonth;
	}

	public Integer getNumCollValidMonth() {
		return numCollValidMonth;
	}

	public void setNumCollValidMonth(Integer numCollValidMonth) {
		this.numCollValidMonth = numCollValidMonth;
	}

	public String getNumCollValidRateMonth() {
		return numCollValidRateMonth;
	}

	public void setNumCollValidRateMonth(String numCollValidRateMonth) {
		this.numCollValidRateMonth = numCollValidRateMonth;
	}

	public Integer getNumCollAllDay() {
		return numCollAllDay;
	}

	public void setNumCollAllDay(Integer numCollAllDay) {
		this.numCollAllDay = numCollAllDay;
	}

	public Integer getNumCollValidDay() {
		return numCollValidDay;
	}

	public void setNumCollValidDay(Integer numCollValidDay) {
		this.numCollValidDay = numCollValidDay;
	}

	public String getNumCollValidRateDay() {
		return numCollValidRateDay;
	}

	public void setNumCollValidRateDay(String numCollValidRateDay) {
		this.numCollValidRateDay = numCollValidRateDay;
	}

	public Integer getNumCollAllHistory() {
		return numCollAllHistory;
	}

	public void setNumCollAllHistory(Integer numCollAllHistory) {
		this.numCollAllHistory = numCollAllHistory;
	}

	public Integer getNumCollValidHistory() {
		return numCollValidHistory;
	}

	public void setNumCollValidHistory(Integer numCollValidHistory) {
		this.numCollValidHistory = numCollValidHistory;
	}

	public String getNumCollValidRateHistory() {
		return numCollValidRateHistory;
	}

	public void setNumCollValidRateHistory(String numCollValidRateHistory) {
		this.numCollValidRateHistory = numCollValidRateHistory;
	}

	public Integer getCollSuccessNumMonth() {
		return collSuccessNumMonth;
	}

	public void setCollSuccessNumMonth(Integer collSuccessNumMonth) {
		this.collSuccessNumMonth = collSuccessNumMonth;
	}

	public String getCollSuccessRateMonth() {
		return collSuccessRateMonth;
	}

	public void setCollSuccessRateMonth(String collSuccessRateMonth) {
		this.collSuccessRateMonth = collSuccessRateMonth;
	}

	public Integer getCollSuccessNumHistory() {
		return collSuccessNumHistory;
	}

	public void setCollSuccessNumHistory(Integer collSuccessNumHistory) {
		this.collSuccessNumHistory = collSuccessNumHistory;
	}

	public String getCollSuccessRateHistory() {
		return collSuccessRateHistory;
	}

	public void setCollSuccessRateHistory(String collSuccessRateHistory) {
		this.collSuccessRateHistory = collSuccessRateHistory;
	}

	public String getCollectorMonthBonus() {
		return collectorMonthBonus;
	}

	public void setCollectorMonthBonus(String collectorMonthBonus) {
		this.collectorMonthBonus = collectorMonthBonus;
	}

	public String getCollectorHistoryBonus() {
		return collectorHistoryBonus;
	}

	public void setCollectorHistoryBonus(String collectorHistoryBonus) {
		this.collectorHistoryBonus = collectorHistoryBonus;
	}

	public String getAppointIds() {
		return appointIds;
	}

	public void setAppointIds(String appointIds) {
		this.appointIds = appointIds;
	}

	public Integer getNumAllMonth() {
		return numAllMonth;
	}

	public void setNumAllMonth(Integer numAllMonth) {
		this.numAllMonth = numAllMonth;
	}

	public Integer getNumValidMonth() {
		return numValidMonth;
	}

	public void setNumValidMonth(Integer numValidMonth) {
		this.numValidMonth = numValidMonth;
	}

	public String getNumValidRateMonth() {
		return numValidRateMonth;
	}

	public void setNumValidRateMonth(String numValidRateMonth) {
		this.numValidRateMonth = numValidRateMonth;
	}

	public Integer getNumAllDay() {
		return numAllDay;
	}

	public void setNumAllDay(Integer numAllDay) {
		this.numAllDay = numAllDay;
	}

	public Integer getNumValidDay() {
		return numValidDay;
	}

	public void setNumValidDay(Integer numValidDay) {
		this.numValidDay = numValidDay;
	}

	public String getNumValidRateDay() {
		return numValidRateDay;
	}

	public void setNumValidRateDay(String numValidRateDay) {
		this.numValidRateDay = numValidRateDay;
	}

	public Integer getBeCallNumDay() {
		return beCallNumDay;
	}

	public void setBeCallNumDay(Integer beCallNumDay) {
		this.beCallNumDay = beCallNumDay;
	}

	public Integer getComeShopNumAllMonth() {
		return comeShopNumAllMonth;
	}

	public void setComeShopNumAllMonth(Integer comeShopNumAllMonth) {
		this.comeShopNumAllMonth = comeShopNumAllMonth;
	}

	public Integer getComeShopNumInvalidMonth() {
		return comeShopNumInvalidMonth;
	}

	public void setComeShopNumInvalidMonth(Integer comeShopNumInvalidMonth) {
		this.comeShopNumInvalidMonth = comeShopNumInvalidMonth;
	}

	public String getComeShopRateMonth() {
		return comeShopRateMonth;
	}

	public void setComeShopRateMonth(String comeShopRateMonth) {
		this.comeShopRateMonth = comeShopRateMonth;
	}

	public Integer getWeddingNearNum() {
		return weddingNearNum;
	}

	public void setWeddingNearNum(Integer weddingNearNum) {
		this.weddingNearNum = weddingNearNum;
	}

	public Integer getTodayComeShopNum() {
		return todayComeShopNum;
	}

	public void setTodayComeShopNum(Integer todayComeShopNum) {
		this.todayComeShopNum = todayComeShopNum;
	}

	public Integer getYyFailNumDay() {
		return yyFailNumDay;
	}

	public void setYyFailNumDay(Integer yyFailNumDay) {
		this.yyFailNumDay = yyFailNumDay;
	}

	public Integer getYyFailNumMonth() {
		return yyFailNumMonth;
	}

	public void setYyFailNumMonth(Integer yyFailNumMonth) {
		this.yyFailNumMonth = yyFailNumMonth;
	}

	public String getYyFailRateDay() {
		return yyFailRateDay;
	}

	public void setYyFailRateDay(String yyFailRateDay) {
		this.yyFailRateDay = yyFailRateDay;
	}

	public String getYyFailRateMonth() {
		return yyFailRateMonth;
	}

	public void setYyFailRateMonth(String yyFailRateMonth) {
		this.yyFailRateMonth = yyFailRateMonth;
	}

	public Integer getLongNoConnNum() {
		return longNoConnNum;
	}

	public void setLongNoConnNum(Integer longNoConnNum) {
		this.longNoConnNum = longNoConnNum;
	}

	public Integer getBeCheckNum() {
		return beCheckNum;
	}

	public void setBeCheckNum(Integer beCheckNum) {
		this.beCheckNum = beCheckNum;
	}
}