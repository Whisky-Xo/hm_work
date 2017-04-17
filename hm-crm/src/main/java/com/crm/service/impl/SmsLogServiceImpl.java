package com.crm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.model.SmsLog;
import com.crm.model.dao.SmsLogDao;
import com.crm.service.SmsLogService;

/**
 * Service 实现类：短信模版
 * 
 * @author sunquan 2016-12-28 11:25
 *
 */
@Service
public class SmsLogServiceImpl implements SmsLogService {
	
	@Autowired
	private SmsLogDao smsLogDao;

	/*-- 添加短信日志记录 --*/
	public Integer createSmsLog(SmsLog smsLog){
//		smsLog.setCreateTime(createTime);
		return smsLogDao.createSmsLog(smsLog);
	}
	
	/*-- 根据不同参数获取短信日志列表 --*/
	public List<SmsLog> listSmsLogByCondition(HashMap<String, Object> hashMap){
		List<SmsLog> smsLogList = new ArrayList<>();
		smsLogList = smsLogDao.listSmsLogByCondition(hashMap);
		return smsLogList;
	}



}