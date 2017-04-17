package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Source;
import com.crm.model.SourceStaffRela;
import com.crm.model.Staff;

/**
 * DAO : source 渠道
 * 
 * @author JingChenglong 2016-09-28 10:07
 *
 */
public interface SourceDao {

	/*-- 新建渠道--*/
	public int createSource(Source source);

	/*-- 根据ID更新渠道信息 --*/
	public int updateSource(Source source);

	/*-- 职员获取其关联的渠道集合 --*/
	public List<Source> getSourceListByStaff(Staff staff);

	/*-- 获取企业旗下所有渠道 --*/
	public List<Source> getSrcListOfComp(@Param("companyId") int companyId);

	public List<Source> getSourceListByTypeId(Source source);

	/*-- 采集员获取期拥有的指定渠道类型的渠道集合 --*/
	public List<Source> getSrcListBySrcTypeStaffCj(Staff staff);

	/*-- 获取指定渠道类型的所有渠道 --*/
	public List<Source> getSrcListByType(Source src);

	/*-- 根据渠道名称获取渠道信息--*/
	public Source getSrcByName(Source src);

	/*-- 批量新增渠道与采集员关联 --*/
	public int addSrcStaffCjYyRela(List<SourceStaffRela> relas);

	/*-- 删除渠道与采集员/邀约员关联 --*/
	public int deleteSrcStaffCjYyRela(SourceStaffRela rela);

	/*-- 获取渠道关联的职工信息集合 --*/
	public List<Staff> getSrcRelaStaffs(SourceStaffRela rela);

	/*-- 根据渠道ID获取渠道信息 --*/
	public Source getSourceById(@Param("srcId") int srcId, @Param("companyId") int companyId);
}