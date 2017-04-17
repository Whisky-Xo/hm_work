package com.crm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.DateUtil;
import com.crm.model.Cost;
import com.crm.model.CostLog;
import com.crm.model.dao.CostDao;
import com.crm.service.CostService;

/**
 * ServiceImpl：花费
 * 
 * @author JingChenglong 2016-09-28 20:11
 *
 */
@Service
public class CostServiceImpl implements CostService {

	@Autowired
	CostDao costDao;/*-- 花费持久层 --*/

	/*-- 新建花费 --*/
	public int createCost(Cost cost) {

		String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		cost.setCreateTime(now);
		cost.setUpdateTime(now);
		if (1 != costDao.createCost(cost)) {
			return 0;
		}
		return 1;
	}


	
	@Override
	public int createCostLog(CostLog costLog) {
		if(1!=costDao.createCostLog(costLog)){
			return 0;
		};
		return 1;
	}
	
	

	@Override
	public Cost getCostById(String id) {
		return costDao.getCostById(id);
	}


	@Override
	public Cost getCostByCondition(Cost cost) {
		
		return costDao.getCostByCondition(cost);
	}
	
	

	@Override
	public int updateCost(Cost cost) {
		String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		cost.setUpdateTime(now);
		if (1 != costDao.updateCost(cost)) {
			return 0;
		}
		return 1;
	}



	@Override
	public List<Cost> listCostsBySource(HashMap<String, Object> hashMap){
		return costDao.listCostsBySource(hashMap);
	}
	
	
	@Override
	public List<Cost> listCostsByCostTime(Cost cost){
		return costDao.listCostsByCostTime(cost);
	}



	


}