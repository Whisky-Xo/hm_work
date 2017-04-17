package com.crm.service;

import java.util.List;

import com.crm.exception.EduException;
import com.crm.model.ClientInfo;

/**
 * Service：客资
 * 
 * @author JingChenglong 2016-09-28 11:03
 *
 */
public interface ClientInfoService {

	/*-- 客资推送 --*/
	public void doClientPush(ClientInfo clientInfo);

	// /*-- 转介绍推送：将指定渠道的新客资推送给相关邀约员 --*/
	// public void doPullStaffInnerSrcByNewClient(ClientInfo clientInfo);
	//
	// /*-- 电商推送：电商渠道客资录入后推送给相关邀约员 --*/
	// public void doPullStaffEbusSrcByNewClient(ClientInfo clientInfo);

	// /*-- 获取未被接单的客资并重新推送 --*/
	// public void doPullStaffLongTimeNoAccept() throws Exception;

	/*-- 根据客资ID获取客资详细信息 --*/
	public ClientInfo getClientInfo(ClientInfo clientInfo) throws Exception;

	/*-- 客资模糊搜索：姓名/电话/QQ --*/
	public List<ClientInfo> getClientInfoLike(ClientInfo clientInfo) throws Exception;

	/*-- 获取门店当日已短信预约个数 --*/
	public Integer getSmsCmtByShopId(Integer companyId, String start, String end) throws Exception;

	/*-- 微信标记已加/未加 --*/
	public void changeWeChatFlag(String kzId, boolean flag, int companyId) throws EduException;

	/*-- QQ标记已加/未加 --*/
	public void changeQqFlag(String kzId, boolean flag, int companyId) throws EduException;

	// /*-- 无效待审批客资超时自动判定为无效 --*/
	// public int decideOverTimeClient();
}