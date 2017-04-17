package com.crm.service;

import java.util.List;

import com.crm.exception.EduException;
import com.crm.model.Status;

/**
 * Service : 状态
 * 
 * @author JingChenglong 2016-09-09 15:04
 *
 */
public interface StatusService {

	/*-- 获取客资状态列表，全部 --*/
	public List<Status> getStatusInfoAllList(Integer companyId, String statusType) throws EduException;
}