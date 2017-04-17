package com.crm.controller;

import java.util.HashMap;
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

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.UtilRegex;
import com.crm.common.util.WebUtils;
import com.crm.model.Company;
import com.crm.model.Staff;
import com.crm.service.ApproveLogService;
import com.crm.service.ClientInfoService;
import com.crm.service.CompanyService;
import com.crm.service.DeptService;
import com.crm.service.DictionaryService;
import com.crm.service.PermissionService;
import com.crm.service.ShopService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

/**
 * 临时接口
 * 
 * @author JingChenglong 2016-11-24 19:39
 *
 */
@Controller
@RequestMapping("/client")
public class TempController {

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */

	@Autowired
	ClientInfoService clientInfoService;// 客资

	@Autowired
	StatusService statusService;/* 客资状态 */

	@Autowired
	SourceService sourceService;/* 客资渠道 */

	@Autowired
	PermissionService pmsService;/* 权限管理 */

	@Autowired
	StaffService staffService;/* 职工管理 */

	@Autowired
	CompanyService companyService;/* 公司管理 */

	@Autowired
	DictionaryService dictionaryService;/* 数据字典 */

	@Autowired
	ShopService shopService; /* 门店 */

	@Autowired
	DeptService deptService; /* 部门 */

	@Autowired
	ApproveLogService approveLogService; /* 审批日志 */

	private static final Company QIEIN = new Company();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/**
	 * 1：客资录入接口：老数据临时导入
	 */
	@RequestMapping(value = "kz_add_temp", method = RequestMethod.POST)
	@ResponseBody
	public Model createNewClientInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzName = maps.get("name");
		String kzPhone = maps.get("phone");
		String kzWeChat = maps.get("wechat");
		String kzQq = maps.get("qq");
		String memo = maps.get("memo");

		String sourceId = maps.get("source_id");
		String statusId = maps.get("status_id");

		String collectorId = maps.get("collectorid");
		String collectorName = maps.get("collectorname");

		String appointId = maps.get("appointId");
		String createTime = maps.get("createtime");
		String validTime = maps.get("validtime");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzName)) {
			model.addAttribute("code", 1002);
			model.addAttribute("msg", "请输入姓名");
			return model;
		}
		if (!StringUtil.isEmpty(kzPhone) && !UtilRegex.validateMobile(kzPhone)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的手机号");
			return model;
		}
		if (!StringUtil.isEmpty(kzQq) && !UtilRegex.validateQq(kzQq)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的qq号");
			return model;
		}
		// 因为微信添加好友时可以通过手机号、QQ号搜索，因此手机号、QQ号也可以通过校验
		if (StringUtil.isNotEmpty(kzWeChat) && !UtilRegex.validateWechat(kzWeChat)
				&& !UtilRegex.validateMobile(kzWeChat) && !UtilRegex.validateQq(kzWeChat)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的微信号");
			return model;
		}
		if (StringUtil.isEmpty(validTime)) {
			model.addAttribute("code", 1002);
			model.addAttribute("msg", "请选择客资有效时间");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 接口调用，业务执行 --*/
		try {
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("statusid", statusId);
			reqContent.put("sourceid", sourceId);
			reqContent.put("name", kzName);
			reqContent.put("phone", kzPhone);
			reqContent.put("qq", kzQq);
			reqContent.put("wechat", kzWeChat);
			reqContent.put("memo", memo);
			reqContent.put("collectorid", collectorId);
			reqContent.put("collectorname", collectorName);
			reqContent.put("appointid", appointId);
			reqContent.put("createtime", createTime);
			reqContent.put("validtime", validTime);
			reqContent.put("ip", WebUtils.getIP(request));

			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoAddTemp");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}
}