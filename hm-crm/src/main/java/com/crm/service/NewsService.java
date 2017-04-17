package com.crm.service;

import java.util.HashMap;
import java.util.List;

import com.crm.exception.EduException;
import com.crm.model.News;
import com.crm.model.Staff;

/**
 * Service：消息
 * 
 * @author JingChenglong 2016-10-11 15:07
 *
 */
public interface NewsService {

	/*-- 批量新增消息 --*/
	public void addNews(List<News> news) throws EduException;

	/*-- 批量消息标记为已读 --*/
	public void doUpdateNewsBeRead(List<News> news) throws EduException;

	/*-- 获取当前用户最新的三条未读消息 --*/
	public List<News> getNewsOnlyThree(Staff staff) throws EduException;

	/*-- 获取当前用户所有未读消息 --*/
	public List<News> getNewsNoReadAll(Staff staff) throws EduException;

	/*-- 获取当前用户一个月内所有已读消息 --*/
	public List<News> getNewsThirtyDays(Staff staff) throws EduException;

	/*- 用户全部消息数量  --*/
	public Integer countNewsAll(Staff staff);
	
	/*-- 用户所有消息分页查询 --*/
	public List<News> getNewsPage(HashMap<String, Object> hashMap) throws EduException;
}