package com.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.api.constant.PageConfConst;
import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;
import com.crm.model.PageConf;
import com.crm.model.dao.PageConfDao;
import com.crm.service.PageConfService;

/**
 * Service 实现类：页面配置
 * 
 * @author sunquan 2016-12-21 14:34
 *
 */
@Service
public class PageConfServiceImpl implements PageConfService {

	@Autowired
	private PageConfDao pageConfDao;// pageConf 页面配置

	@Override
	// @RedisCache(cacheKey = "getPageConfByCompany",expire=10800, name =
	// "获取公司页面配置", type = PageConf.class)
	public PageConf getPageConfByCompany(Integer companyId, String action) {

		if (companyId == null) {
			return null;
		}

		if (StringUtil.isEmpty(action)) {
			action = PageConfConst.TAB_ALL;
		}

		return pageConfDao.getPageConfByCompany(new PageConf(companyId, action));
	}

	// 获取企业页面配置信息
	public List<PageConf> getPageConfListByCompany(Integer companyId) throws EduException {
		if (companyId == null) {
			return null;
		}

		return pageConfDao.getPageConfListByCompany(companyId);
	}
}
