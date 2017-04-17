package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.InvitationLog;

/**
 * DAO : 邀约记录
 * 
 * @author jzl 2016-10-11 17:11
 *
 */
public interface InvitationLogDao {

	public List<InvitationLog> listInvitationLog(@Param("receptorId") int receptorId,
			@Param("companyId") int companyId);

	/*-- 根据ID获取邀约记录信息 --*/
	public InvitationLog getLogById(@Param("logId") String logId);
}