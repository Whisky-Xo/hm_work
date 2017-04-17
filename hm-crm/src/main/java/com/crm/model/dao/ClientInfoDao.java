package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Staff;

/**
 * DAO : cleintInfo 客资
 * 
 * @author JingChenglong 2016-09-28 11:34
 *
 */
public interface ClientInfoDao {

	/*-- 根据客资信息获取应该推送的职员信息 --*/
	public Staff getStaffByClientInfo(ClientInfo clientInfo);

	/*-- 获取状态为等待邀约但未绑定邀约员的客资 --*/
	// public List<ClientInfo> getClientByNoBlindAccept(ClientInfo clientInfo,
	// @Param("tabName") String tabName);

	/*-- 根据客资ID获取客资详细信息 --*/
	public ClientInfo getInfoById(ClientInfo clientInfo);

	/*-- 转介绍：根据客资信息获取应该推送的转介绍职员信息 --*/
	public List<Staff> getStaffByClientInfoZjs(ClientInfo clientInfo);

	/*-- 客资模糊搜索：姓名/电话/QQ --*/
	public List<ClientInfo> getClientInfoLike(ClientInfo clientInfo);

	/*-- 获取指定ID和渠道的邀约员信息 --*/
	public Staff getYyStaffById(Staff staff);

	/*-- 获取门店当日已短信预约个数 --*/
	public Integer getSmsCmtByShopId(@Param("companyId") int companyId, @Param("start") String start,
			@Param("end") String end, @Param("tabName") String tabName);

	/*-- 微信标记已加/未加 --*/
	public void changeWeChatFlag(@Param("kzId") String kzId, @Param("flag") boolean flag,
			@Param("tabName") String tabName) throws EduException;

	/*-- QQ标记已加/未加 --*/
	public void changeQqFlag(@Param("kzId") String kzId, @Param("flag") boolean flag, @Param("tabName") String tabName)
			throws EduException;

	/*-- 获取客资 --*/
	// public List<ClientInfo> getClientInfoList(ClientInfo clientInfo);

	/*-- 邀约员检索：一对多获取邀约员信息 --*/
	public List<Staff> getStaffListOfOneToMore(@Param("srcId") int srcId, @Param("companyId") int companyId);

	/*-- 邀约员检索：一对一获取邀约员信息 --*/
	public Staff getStaffListOfOneToOne(@Param("srcId") int srcId, @Param("companyId") int companyId);
}