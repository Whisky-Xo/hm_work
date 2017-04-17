package com.crm.service;

import java.util.HashMap;
import java.util.List;

import com.crm.model.Cost;
import com.crm.model.CostLog;

/**
 * Service：花费
 * 
 * @author JingChenglong 2016-09-28 20:10
 *
 */
public interface CostService {

	/*-- 新建花费 --*/
	public int createCost(Cost cost);
	
	public int createCostLog(CostLog costLog);

	public int updateCost(Cost cost);
	
	public Cost getCostById(String id);
	
	public Cost getCostByCondition(Cost cost);

	public List<Cost> listCostsBySource(HashMap<String, Object> hashMap);
	
	public List<Cost> listCostsByCostTime(Cost cost);


}
