package com.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;

/*
 *此controller方法专用于测试 
 * 
 * */


@Controller
@RequestMapping("/test")
public class TestController {
	
	@RequestMapping(value = "index")
	@ResponseBody
	public JSONObject index() {
		
		JSONObject response = new JSONObject();
		System.out.println(System.getProperty("spring.profiles.active"));
		response.put("status",System.getProperty("spring.profiles.active"));
		return response;
	}

}
