package com.crm.service;

import java.util.HashMap;

import com.crm.annotation.RedisCache;
import com.crm.exception.EduException;
import com.crm.model.SmsCode;

/**
 * Service：短信模版
 * 
 * @author sunquan 2016-12-28 15:07
 *
 */
public interface SmsCodeService {

	/*-- 根据参数获取短信模版 --*/

	public SmsCode getSmsCodeByCondition(HashMap<String, Object> hashMap) throws EduException;

}