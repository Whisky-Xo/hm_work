package com.crm.service;

import java.util.List;

import com.crm.exception.EduException;
import com.crm.model.PageConf;

/**
 * 页面自定义配置
 * 
 * @author JingChenglong 2017-02-06 14:44
 *
 */
public interface PageConfService {

	/*-- 获取企业制定请求页面的配置信息 --*/
	public PageConf getPageConfByCompany(Integer companyId, String action) throws EduException;

	/*-- 获取企业页面配置 --*/
	public List<PageConf> getPageConfListByCompany(Integer companyId) throws EduException;
}