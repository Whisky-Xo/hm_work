package com.crm.model.dao;

import com.crm.model.ApproveLog;

/**
 * DAO：审批日志
 * 
 * @author JingChenglong 2016-11-14 13:49
 *
 */
public interface ApproveLogDao {

	/*-- 新建审批日志 --*/
	public int createApproveLog(ApproveLog approveLog);

	/*-- 删除客资的审批日志 --*/
	public void removeApproveLog(ApproveLog approveLog);
}