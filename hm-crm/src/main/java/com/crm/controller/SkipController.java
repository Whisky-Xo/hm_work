package com.crm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;
import com.crm.model.InvitationLog;
import com.crm.service.InvitationLogService;

/**
 * 共用行响应页面跳转
 * 
 * @author JingChenglong 2016-12-25 17:57
 *
 */
@Controller
@RequestMapping("/client")
public class SkipController {

	@Autowired
	InvitationLogService invitationLogService;/* 邀约记录 */

	/** -- 1.跳转至附件详情 -- **/
	@RequestMapping(value = "/to_file_show", method = RequestMethod.GET)
	public String kzAdd(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		String logId = maps.get("logid");

		InvitationLog log = null;
		if (StringUtil.isNotEmpty(logId)) {
			log = invitationLogService.getLogById(logId);
		} else {
			log = new InvitationLog();
		}

		model.addAttribute("log", log);

		return "skip/show_img";
	}
}