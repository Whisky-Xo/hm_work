package com.crm.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.News;
import com.crm.model.Staff;
import com.crm.model.dao.NewsDao;
import com.crm.service.NewsService;

/**
 * Service 实现类：消息
 * 
 * @author JingChenglong 2016-10-11 15:11
 *
 */
@Service
public class NewsServiceImpl implements NewsService {

	@Autowired
	private NewsDao newsDao;// news 消息持久化

	/*-- 批量新增消息 --*/
	public void addNews(List<News> news) throws EduException {

		if (news == null || news.size() == 0) {
			return;
		}

		for (int i = 0; i < news.size(); i++) {
			news.get(i).setIsRead(false);
			news.get(i).setCreateTime(TimeUtils.getSysTime());
		}

		if (news.size() != newsDao.addNews(news)) {
			throw new EduException(999999);
		}
	}

	/*-- 批量消息标记为已读 --*/
	public void doUpdateNewsBeRead(List<News> news) throws EduException {

		if (news == null || news.size() == 0) {
			return;
		}

		for (int i = 0; i < news.size(); i++) {
			if (news.get(i).getId() == null || news.get(i).getCompanyId() == null) {
				throw new EduException(999998);
			}
			newsDao.doUpdateNewsBeRead(news.get(i));
		}

	}

	/*-- 获取当前用户最新的三条消息 --*/
	public List<News> getNewsOnlyThree(Staff staff) throws EduException {

		return newsDao.getNewsOnlyThree(staff);
	}

	/*-- 获取当前用户所有未读消息 --*/
	public List<News> getNewsNoReadAll(Staff staff) throws EduException {

		return newsDao.getNewsNoReadAll(staff);
	}

	/*-- 获取当前用户一个月内所有消息 --*/
	public List<News> getNewsThirtyDays(Staff staff) throws EduException {

		return newsDao.getNewsThirtyDays(staff);
	}

	
	/*- 用户全部消息数量  --*/
	public Integer countNewsAll(Staff staff){
		return newsDao.countNewsAll(staff);
	}
	
	/*-- 用户所有消息分页查询 --*/
	public List<News> getNewsPage(HashMap<String, Object> hashMap) throws EduException {
		return newsDao.getNewsPage(hashMap);
	}
}