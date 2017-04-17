package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Status;

/**
 * DAO : status 状态
 * 
 * @author JingChenglong 2016-12-21 13:07
 *
 */
public interface StatusDao {

	/*-- 获取客资状态列表：全部 --*/
	public List<Status> getStatusInfoAllList(@Param("companyId") Integer companyId,
			@Param("statusType") String statusType);
}