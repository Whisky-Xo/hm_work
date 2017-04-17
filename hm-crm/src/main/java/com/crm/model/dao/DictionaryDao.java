package com.crm.model.dao;

import java.util.List;

import com.crm.model.Dictionary;

/**
 * DAO : dictionary 数据字典
 * 
 * @author JingChenglong 2016-09-23 14:22
 *
 */
public interface DictionaryDao {

	/*-- 获取指定类型的字典数据集合 --*/
	public List<Dictionary> getDictionaryListByType(Dictionary dictionary);

}