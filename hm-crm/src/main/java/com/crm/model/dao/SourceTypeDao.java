package com.crm.model.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.crm.model.SourceType;



/**
 * DAO：渠道类型
 * 
 * @author JingChenglong 2016-09-27 10:33
 *
 */
public interface SourceTypeDao {


	/*-- 新增渠道类型 --*/
	public int createSourceType(SourceType sourceType);

	/*-- 根据ID更新渠道类型信息 --*/
	public int updateSourceType(SourceType sourceType);

	/*-- 根据ID获取渠道类型详细信息 --*/
	public SourceType getSourceTypeById(SourceType sourceType);

	/*-- 根据渠道名称和企业ID获取渠道信息 --*/
	public SourceType getSourceTypeByName(SourceType sourceType);

	/*-- 根据企业ID获取企业渠道类型及其类型下所有渠道信息 --*/
	public List<SourceType> getSrcListDetailByCompId(int compId);
}