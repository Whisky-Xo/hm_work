package com.crm.model.dao;

import java.util.HashMap;
import java.util.List;

import com.crm.model.SmsLog;

/**
 * DAO : smslog 短信日志
 * 
 * @author sunquan 2016-12-28 15:01
 */

public interface SmsLogDao {

	/*-- 添加短信日志记录 --*/
	public Integer createSmsLog(SmsLog smsLog);

	/*-- 根据不同参数获取短信日志列表 --*/
	public List<SmsLog> listSmsLogByCondition(HashMap<String, Object> hashMap);

}