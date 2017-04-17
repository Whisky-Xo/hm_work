package com.crm.model.dao;

import java.util.List;
import com.crm.model.TimeWarn;

/**
 * DAO：定时任务
 * 
 * @author JingChenglong 2016-11-03 17:04
 *
 */
public interface TimeWarnDao {

	/*-- 创建定时任务 --*/
	public int createTimeWarn(TimeWarn timeWarn);

	/*-- 删除指定定时任务 --*/
	public void removeWarnTime(TimeWarn timeWarn);

	/*-- 获取个人定时任务列表 --*/
	public List<TimeWarn> getWarnTaskList(TimeWarn timeWarn);

	/*-- 获取时间段内的任务 --*/
	public List<TimeWarn> getTimeWarnBetween(TimeWarn timeWarn);
}