package com.crm.model.dao;

import java.util.HashMap;
import java.util.List;


import com.crm.model.Cost;
import com.crm.model.CostLog;

/**
 * DAO：花费
 * 
 * @author sunquan 2016-11-09 15:51
 *
 */
public interface CostDao {

	/*-- 创建花费 --*/
	public int createCost(Cost cost);
	
	/*-- 创建花费日志 --*/
	public int createCostLog(CostLog costLog);

	/*-- 编辑花费 --*/
	public int updateCost(Cost cost);

	/*-- 根据主键ID获取花费信息 --*/
	public Cost getCostById(String id);
	
	public Cost getCostByCondition(Cost cost);

	public List<Cost> listCostsBySource(HashMap<String, Object> hashMap);
	
	public List<Cost> listCostsByCostTime(Cost cost);


	
}
