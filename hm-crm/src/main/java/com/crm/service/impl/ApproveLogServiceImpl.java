package com.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crm.common.util.TimeUtils;
import com.crm.model.ApproveLog;
import com.crm.model.dao.ApproveLogDao;
import com.crm.service.ApproveLogService;

/**
 * ServiceImpl：审批日志
 * 
 * @author JingChenglong 2016-11-14 13:46
 *
 */
@Service
public class ApproveLogServiceImpl implements ApproveLogService {

	@Autowired
	ApproveLogDao approveLogDao;/*-- 审批日志持久层 --*/

	/*-- 新建审批日志 --*/
	public int createApproveLog(ApproveLog approveLog) {

		// 删除该客资之前所有无效审批
		approveLogDao.removeApproveLog(approveLog);

		// 新增
		approveLog.setCreateTime(TimeUtils.getSysTime());
		approveLog.setIsDel(false);
		if (1 != approveLogDao.createApproveLog(approveLog)) {
			return 0;
		}
		return 1;
	}
}