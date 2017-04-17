package com.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;
import com.crm.model.InvitationLog;
import com.crm.model.dao.InvitationLogDao;
import com.crm.service.InvitationLogService;

/**
 * Service：邀约记录
 * 
 * @author jzl 2016-10-11 17:00
 *
 */
@Service
public class InvitationLogServiceImpl implements InvitationLogService {

	@Autowired
	private InvitationLogDao invitationLogDao;

	/*-- 获取指定的邀约记录 --*/
	public List<InvitationLog> listInvitationLog(int receptorId, int companyId) {
		return invitationLogDao.listInvitationLog(receptorId, companyId);
	}

	/*-- 根据邀约记录ID获取记录详情 --*/
	public InvitationLog getLogById(String logId) throws EduException {

		if (StringUtil.isEmpty(logId)) {
			return null;
		}

		return invitationLogDao.getLogById(logId);
	};
}