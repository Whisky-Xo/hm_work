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
import org.springframework.web.bind.annotation.ResponseBody;

import com.crm.api.CrmBaseApi;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;
import com.crm.model.Staff;
import com.crm.service.ClientInfoService;
import com.crm.service.CompanyService;
import com.crm.service.DeptService;
import com.crm.service.DictionaryService;
import com.crm.service.GroupService;
import com.crm.service.InvitationLogService;
import com.crm.service.PermissionService;
import com.crm.service.ShopService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

/**
 * 共用行响应ajax请求
 * 
 * @author JingChenglong 2016-12-21 14:08
 *
 */
@Controller
@RequestMapping("/client")
public class PublicController {

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */
	@Autowired
	SourceService sourceService;/* 客资渠道 */
	@Autowired
	StatusService statusService;/* 客资状态 */
	@Autowired
	PermissionService pmsService;/* 权限管理 */
	@Autowired
	StaffService staffService;/* 职工管理 */
	@Autowired
	GroupService groupService;/* 小组管理 */
	@Autowired
	CompanyService companyService;/* 公司管理 */
	@Autowired
	ClientInfoService clientInfoService;// 客资
	@Autowired
	PermissionService permissionService; // 权限
	@Autowired
	DictionaryService dictionaryService;/* 数据字典 */
	@Autowired
	DeptService deptService;/* 数据字典 */
	@Autowired
	InvitationLogService invitationLogService;/* 邀约记录 */
	@Autowired
	ShopService shopService;/* 门店 */

	/*-- 1：标记微信已加/未加 --*/
	@RequestMapping(value = "change_wechat_flag", method = RequestMethod.POST)
	@ResponseBody
	public Model changeWechatFlag(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");
		Boolean flag = "1".equals(maps.get("flag")) ? true : false;

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请选择客资信息");
			return model;
		}

		try {
			clientInfoService.changeWeChatFlag(kzId, flag, staff.getCompanyId());
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "成功");
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "失败");
			e.printStackTrace();
		}

		return model;
	}

	/*-- 2：标记QQ已加/未加 --*/
	@RequestMapping(value = "change_qq_flag", method = RequestMethod.POST)
	@ResponseBody
	public Model changeQqFlag(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");
		Boolean flag = "1".equals(maps.get("flag")) ? true : false;

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请选择客资信息");
			return model;
		}

		try {
			clientInfoService.changeQqFlag(kzId, flag, staff.getCompanyId());
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "成功");
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "失败");
			e.printStackTrace();
		}

		return model;
	}

	/*-- 3：发送闪信 --*/
	@RequestMapping(value = "send_flash_msg", method = RequestMethod.POST)
	@ResponseBody
	public Model sendFlashMsg(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Integer barId = StringUtil.nullToIntZero(maps.get("id"));
		String msg = maps.get("msg");

		/*-- 参数校验 --*/
		if (barId == null || barId == 0) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "采集员非公司员工");
			return model;
		}
		if (StringUtil.isEmpty(msg)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请输入闪信内容");
			return model;
		}

		// 接受者
		Staff bar = staffService.getStaffInfoById(barId);

		// 发送者
		Staff sender = CookieCompoment.getLoginUser(request);
		sender = staffService.getStaffInfoById(sender.getId());

		if (bar == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "接收者信息获取错误");
			return model;
		}

		if (sender == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "发送者信息获取错误");
			return model;
		}

		PushUtil.pushFlashMsg(bar, msg, sender);

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");

		return model;
	}
}