package com.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.crm.common.util.RedisUtil;

@Controller
public class HomeController {

	//系统启动自动跳转到首页
	@RequestMapping(value = "/")
	public  ModelAndView  home() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login/login");
		return mav;
	}
	
	
	//清除缓存
	@RequestMapping(value = "/cleanCache")
	@ResponseBody
	public  String  cleanCache() {
		RedisUtil rt = new RedisUtil();
		String ss=rt.fulshDB();
		return ss;
	}
	
}