package com.crm.service;

import java.util.List;

import com.crm.exception.EduException;
import com.crm.model.InvitationLog;

/**
 * Service：邀约记录
 * 
 * @author jzl 2016-10-11 17:00
 *
 */
public interface InvitationLogService {

	/*-- 获取指定的邀约记录 --*/
	public List<InvitationLog> listInvitationLog(int receptorId, int companyId) throws Exception;

	/*-- 根据邀约记录ID获取记录详情 --*/
	public InvitationLog getLogById(String logId) throws EduException;
}