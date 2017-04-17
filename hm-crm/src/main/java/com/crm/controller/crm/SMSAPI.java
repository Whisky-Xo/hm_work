package com.crm.controller.crm;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crm.service.SmsCodeService;

/**
 * 短信管理
 * 
 * @author JingChenglong 2017-02-20 14:08
 *
 */
@Controller
@RequestMapping("/client")
public class SMSAPI {

	@Autowired
	SmsCodeService SmsCodeService;/* 短信事务层 */

	/*-- 0：短信发送日志记录写入--*/
	@RequestMapping(value = "/smsLogSave", method = RequestMethod.GET)
	public void smsLogSave(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) {
		

	}

	/*-- 1：短信功能首页-账户管理 --*/

	/*-- 2:短信记录查询 --*/

}