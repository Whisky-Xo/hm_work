package com.crm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("errors")
public class ErrorController {
	
	@RequestMapping(value = "404", method = RequestMethod.GET)
	public String register(Model model,HttpServletRequest request,
			HttpServletResponse response) {
		return "login/404";
	}
	
}
