package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.PageConf;

/**
 * DAO : PageConf
 * 
 * @author JingChenglong 2016-09-23 14:22
 *
 */
public interface PageConfDao {

	/*-- 获取指定类型的字典数据集合 --*/
	public PageConf getPageConfByCompany(PageConf pageConf);

	/*-- 获取企业页面配置信息 --*/
	public List<PageConf> getPageConfListByCompany(@Param("companyId") Integer companyId);
}