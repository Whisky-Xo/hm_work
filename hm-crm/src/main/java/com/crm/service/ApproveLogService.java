package com.crm.service;

import com.crm.model.ApproveLog;

/**
 * Service：审批日志
 * 
 * @author JingChenglong 2016-11-14 13:45
 *
 */
public interface ApproveLogService {

	/*-- 新建审批日志 --*/
	public int createApproveLog(ApproveLog approveLog);
}