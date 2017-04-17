package com.crm.model.dao;


import java.util.HashMap;

import com.crm.model.SmsCode;

/**
 * DAO : smscode 消息
 * @author sunquan 2016-12-28 15:01
 */

public interface SmsCodeDao {

	/*-- 根据参数获取短信模版 --*/
	public SmsCode getSmsCodeByCondition(HashMap<String, Object> hashMap);
	
}