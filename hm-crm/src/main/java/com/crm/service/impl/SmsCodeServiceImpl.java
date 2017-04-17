package com.crm.service.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.exception.EduException;
import com.crm.model.SmsCode;
import com.crm.model.dao.SmsCodeDao;
import com.crm.service.SmsCodeService;

/**
 * Service 实现类：短信模版
 * 
 * @author sunquan 2016-12-28 11:25
 *
 */
@Service
public class SmsCodeServiceImpl implements SmsCodeService {
	@Autowired
	private SmsCodeDao smsCodeDao;

	/*-- 根据参数获取短信模版 --*/
	public SmsCode getSmsCodeByCondition(HashMap<String, Object> hsahMap) throws EduException {
		return smsCodeDao.getSmsCodeByCondition(hsahMap);
	}

}