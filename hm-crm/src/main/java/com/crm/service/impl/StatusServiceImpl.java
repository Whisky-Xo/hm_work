package com.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.annotation.RedisCache;
import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;
import com.crm.model.Status;
import com.crm.model.dao.StatusDao;
import com.crm.service.StatusService;

/**
 * Service实现：状态
 * 
 * @author JingChenglong 2016-09-09 15:07
 *
 */
@Service
public class StatusServiceImpl implements StatusService {

	@Autowired
	private StatusDao statusDao;// status 状态持久化

	/*-- 获取客资状态列表：全部 --*/
	@RedisCache(cacheKey = "getStatusInfoAllList", name = "所有渠道列表",expire=10800,type = Status.class)
	public List<Status> getStatusInfoAllList(Integer companyId, String statusType) throws EduException {

		if (companyId == null || companyId == 0 || StringUtil.isEmpty(statusType)) {
			return null;
		}
		return statusDao.getStatusInfoAllList(companyId, statusType);
	}
}