package com.crm.service;

import java.util.List;

import com.crm.exception.EduException;
import com.crm.model.Source;
import com.crm.model.SourceStaffRela;
import com.crm.model.Staff;


/**
 * Service：渠道
 * 
 * @author JingChenglong 2016-09-28 10:05
 *
 */
public interface SourceService {

	/*-- 新建渠道--*/
	public int createSource(Source source) throws EduException;

	/*-- 根据ID更新渠道信息 --*/
	public int updateSource(Source source) throws EduException;
	
	/*-- 电商：采集员获取其拥有的采集渠道集合 --*/
	public List<Source> getSrcListDsByStaffCj(Integer id) throws EduException;

	/*-- 邀约员：获取其拥有的邀约渠道集合 --*/
	public List<Source> getSrcListByStaffYy(Integer id) throws EduException;
	
	public List<Source> getSourceListByTypeId(Source source);

	/*-- 推广员：获取其拥有的推广渠道集合 --*/
	public List<Source> getSrcListByStaffTg(Integer id) throws EduException;

	/*-- 内部转介绍：采集员获取期拥有的指定渠道类型的渠道集合 --*/
	public List<Source> getSrcListInnerTypeByStaffCj(Integer id) throws EduException;

	/*-- 获取指定渠道类型的所有渠道 --*/
	public List<Source> getSrcListByType(Integer companyId, String srcType) throws EduException;

	/*-- 获取指定渠道名称的渠道 --*/
	public Source getSrcByName(String srcName, Integer compantId) throws EduException;

	/*-- 获取企业所有渠道 --*/
	public List<Source> getSrcListOfComp(Integer companyId) throws EduException;
	
	
	/*-- 批量新增渠道与采集员关联 --*/
	public int addSrcStaffCjYyRela(List<SourceStaffRela> relas) throws EduException;

	/*-- 删除渠道与采集员/邀约员关联 --*/
	public int deleteSrcStaffCjYyRela(SourceStaffRela rela) throws EduException;

	/*-- 获取渠道关联的职工信息集合 --*/
	public List<Staff> getSrcRelaStaffs(SourceStaffRela rela) throws EduException;
	
}