package com.crm.model.dao;

import java.util.HashMap;
import java.util.List;

import com.crm.model.News;
import com.crm.model.Staff;

/**
 * DAO : news 消息
 * 
 * @author JingChenglong 2016-10-11 15:01
 *
 */
public interface NewsDao {

	/*-- 新增消息 --*/
	public int addOneNew(News news);

	/*-- 批量新增消息 --*/
	public int addNews(List<News> news);

	/*-- 消息标记为已读 --*/
	public int doUpdateNewsBeRead(News news);

	/*-- 获取当前用户最新的三条消息 --*/
	public List<News> getNewsOnlyThree(Staff staff);

	/*- 获取当前用户所有未读消息  --*/
	public List<News> getNewsNoReadAll(Staff staff);

	/*-- 获取当前用户一个月内所有消息 --*/
	public List<News> getNewsThirtyDays(Staff staff);

	/*- 用户全部消息数量  --*/
	public Integer countNewsAll(Staff staff);
	
	/*-- 用户所有消息分页查询 --*/
	public List<News> getNewsPage(HashMap<String, Object> hashMap);
}