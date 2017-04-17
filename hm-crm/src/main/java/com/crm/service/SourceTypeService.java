package com.crm.service;

import java.util.List;

import com.crm.model.SourceType;

/**
 * Service：渠道
 * 
 * @author JingChenglong 2016-09-27 10:42
 *
 */
public interface SourceTypeService {

	/*-- 新增渠道类型 --*/
	public int createSourceType(SourceType sourceType);

	/*-- 根据ID更新渠道类型信息 --*/
	public int updateSourceType(SourceType sourceType);

	/*-- 根据ID获取渠道类型详细信息 --*/
	public SourceType getSourceTypeById(SourceType sourceType);

	/*-- 根据企业ID获取企业渠道类型及其类型下所有渠道信息 --*/
	public List<SourceType> getSrcListDetailByCompId(int compId);
}