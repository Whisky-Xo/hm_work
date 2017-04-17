package com.crm.service;

import java.util.List;

import com.crm.exception.EduException;
import com.crm.model.Staff;
import com.crm.model.TimeWarn;

/**
 * Service：定时任务
 * 
 * @author JingChenglong 2016-11-03 17:21
 *
 */
public interface WarnTimerService {

	/*-- 添加定时任务 --*/
	public void addWarnTime(TimeWarn timeWarn) throws EduException;

	/*-- 删除指定定时任务 --*/
	public void removeWarnTime(TimeWarn timeWarn) throws EduException;

	/*-- 获取个人定时任务列表 --*/
	public List<TimeWarn> getWarnTaskList(Staff staff) throws EduException;

	/*-- 执行定时任务 --*/
	public void doWarnTimeTask();
}