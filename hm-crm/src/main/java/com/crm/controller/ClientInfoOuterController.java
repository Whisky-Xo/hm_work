package com.crm.controller;

import java.util.HashMap;
import java.util.List;
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
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.UtilRegex;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Staff;
import com.crm.service.ClientInfoService;
import com.crm.service.CompanyService;
import com.crm.service.DictionaryService;
import com.crm.service.GroupService;
import com.crm.service.PermissionService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

/**
 * 转介绍客资信息
 * 
 * @author JingChenglong 2016-10-17 11:31
 *
 */
@Controller
@RequestMapping("/client")
public class ClientInfoOuterController {

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
	DictionaryService dictionaryService;/* 数据字典 */

	/**
	 * 1：钉钉端：内部转介绍渠道--新增客资
	 * 
	 * *** kzname (必传) 客资姓名 *** *** sourceid (必传) 渠道ID *** *** kzphone (必传)
	 * 电话/微信 *** *** remark 备注 *** *** yptime 预拍时间 *** *** staffname (必传) 员工姓名
	 * *** *** staffphone (必传) 员工电话/微信 *** *** oldkzname 老客户姓名 *** ***
	 * oldkzphone 老客户电话/微信
	 * 
	 * @throws EduException
	 *             ***
	 */
	@RequestMapping(value = "ding_add_client", method = RequestMethod.POST)
	@ResponseBody
	public Model dingAddClient(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		String kzName = maps.get("kzname");
		String sourceId = maps.get("sourceid");
		String kzPhone = maps.get("kzphone");
		String memo = maps.get("remark");
		String ypTime = maps.get("yptime");
		String staffName = maps.get("staffname");
		String staffPhone = maps.get("staffphone");
		String oldKzName = maps.get("oldkzname");
		String oldKzPhone = maps.get("oldkzphone");
		String mateName = maps.get("matename");
		String matePhone = maps.get("matephone");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzName)) {
			model.addAttribute("code", 1002);
			model.addAttribute("msg", "请输入新客姓名");
			return model;
		}
		if (StringUtil.isEmpty(kzPhone)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入客户联系方式");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 录入人信息 --*/
		Staff collector = new Staff();
		collector.setCompanyId(staff.getCompanyId());
		collector.setPhone(staffPhone);
		collector.setName(staffName);
		collector = staffService.getStaffByPhone(collector);

		/*-- 接口调用，业务执行 --*/
		try {
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("sourceid", sourceId);
			reqContent.put("name", kzName);
			if (!UtilRegex.validateMobile(kzPhone)) {
				reqContent.put("wechat", kzPhone);
			} else {
				reqContent.put("phone", kzPhone);
			}
			if (StringUtil.isNotEmpty(ypTime)) {
				reqContent.put("yptime", ypTime);
			}
			reqContent.put("memo", memo);
			if (collector != null) {
				reqContent.put("collectorid", collector.getId());
			} else {
				if (StringUtil.isNotEmpty(staffName)) {
					reqContent.put("collectorname", staffName);
				}
				if (StringUtil.isNotEmpty(staffPhone)) {
					reqContent.put("collectorphone", staffPhone);
				}
			}
			if (StringUtil.isNotEmpty(oldKzName)) {
				reqContent.put("oldkzname", oldKzName);
			}
			if (StringUtil.isNotEmpty(oldKzPhone)) {
				reqContent.put("oldkzphone", oldKzPhone);
			}
			if (StringUtil.isNotEmpty(mateName)) {
				reqContent.put("matename", mateName);
			}
			if (StringUtil.isNotEmpty(matePhone)) {
				reqContent.put("matephone", matePhone);
			}
			reqContent.put("ip", WebUtils.getIP(request));
			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoAdd");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			// 客资新增成功，将客资推送给指定邀约员
			if ("100000".equals(jsInfo.getString("code"))) {
				String kzId = JsonFmtUtil.strContentToJsonObj(addRstStr).getString("kzId");
				synchronized (this) {
					ClientInfo client = new ClientInfo();
					client.setKzId(kzId);
					client.setCompanyId(staff.getCompanyId());
					client = clientInfoService.getClientInfo(client);
					clientInfoService.doClientPush(client);
				}
			} else if ("130004".equals(jsInfo.getString("code")) || "130005".equals(jsInfo.getString("code"))
					|| "130006".equals(jsInfo.getString("code"))) {
				// 重复客资，给邀约员推送提醒
				ClientInfo client = new ClientInfo();
				if (!UtilRegex.validateMobile(kzPhone)) {
					client.setKzWechat(kzPhone);
				} else {
					client.setKzPhone(kzPhone);
				}
				client.setCompanyId(staff.getCompanyId());

				List<ClientInfo> clientList = clientInfoService.getClientInfoLike(client);
				if (collector == null) {
					collector = new Staff();
					collector.setName(staffName);
				}
				if (clientList != null) {
					Staff appointer = new Staff();
					for (ClientInfo c : clientList) {
						if (c.getAppointId() == null || c.getAppointId() == 0) {
							continue;
						}
						appointer.setCompanyId(c.getCompanyId());
						appointer.setId(c.getAppointId());
						appointer = staffService.getStaffInfoById(appointer.getId());
						if (appointer != null) {
							PushUtil.pushReClient(c, collector, appointer);
						}
					}
				}
			}
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/**
	 * 2：钉钉端：电商渠道--新增客资
	 * 
	 * *** kzname (必传) 客资姓名 *** *** sourceid (必传) 渠道ID *** *** kzphone (必传)
	 * 电话/微信 *** *** remark 备注 *** *** yptime 预拍时间 *** *** staffname (必传) 员工姓名
	 * *** *** staffphone (必传) 员工电话/微信 *** *** oldkzname 老客户姓名 *** ***
	 * oldkzphone 老客户电话/微信
	 * 
	 * @throws EduException
	 *             ***
	 */
	@RequestMapping(value = "ding_add_client_ds", method = RequestMethod.POST)
	@ResponseBody
	public Model dingAddClientDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		String kzName = maps.get("kzname");
		String sourceId = maps.get("sourceid");
		String kzPhone = maps.get("kzphone");
		String memo = maps.get("remark");
		String ypTime = maps.get("yptime");
		String staffName = maps.get("staffname");
		String staffPhone = maps.get("staffphone");
		String kzWeChat = maps.get("kzWeChat");
		String kzQq = maps.get("kzQq");

		if (StringUtil.isEmpty(kzPhone) && StringUtil.isEmpty(kzWeChat) && StringUtil.isEmpty(kzQq)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入客户联系方式");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 录入人信息 --*/
		Staff collector = new Staff();
		collector.setCompanyId(staff.getCompanyId());
		collector.setPhone(staffPhone);
		collector.setName(staffName);
		collector = staffService.getStaffByPhone(collector);

		/*-- 接口调用，业务执行 --*/
		try {
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("sourceid", sourceId);
			reqContent.put("name", kzName);
			reqContent.put("wechat", kzWeChat);
			reqContent.put("phone", kzPhone);
			reqContent.put("qq", kzQq);
			if (StringUtil.isNotEmpty(ypTime)) {
				reqContent.put("yptime", ypTime);
			}
			reqContent.put("memo", memo);
			if (collector != null) {
				reqContent.put("collectorid", collector.getId());
			} else {
				if (StringUtil.isNotEmpty(staffName)) {
					reqContent.put("collectorname", staffName);
				}
				if (StringUtil.isNotEmpty(staffPhone)) {
					reqContent.put("collectorphone", staffPhone);
				}
			}
			reqContent.put("ip", WebUtils.getIP(request));
			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoAdd");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			// 客资新增成功，将客资推送给指定邀约员
			if ("100000".equals(jsInfo.getString("code"))) {
				String kzId = JsonFmtUtil.strContentToJsonObj(addRstStr).getString("kzId");
				synchronized (this) {
					ClientInfo client = new ClientInfo();
					client.setKzId(kzId);
					client.setCompanyId(staff.getCompanyId());
					client = clientInfoService.getClientInfo(client);
					clientInfoService.doClientPush(client);
				}
			} else if ("130004".equals(jsInfo.getString("code")) || "130005".equals(jsInfo.getString("code"))
					|| "130006".equals(jsInfo.getString("code"))) {
				// 重复客资，给邀约员推送提醒
				ClientInfo client = new ClientInfo();

				if (!UtilRegex.validateMobile(kzPhone)) {
					client.setKzWechat(kzPhone);
				} else {
					client.setKzPhone(kzPhone);
				}
				client.setCompanyId(staff.getCompanyId());

				List<ClientInfo> clientList = clientInfoService.getClientInfoLike(client);
				if (clientList != null) {
					Staff appointer = new Staff();
					for (ClientInfo c : clientList) {
						if (c.getAppointId() == null || c.getAppointId() == 0) {
							continue;
						}
						appointer.setCompanyId(c.getCompanyId());
						appointer.setId(c.getAppointId());
						appointer = staffService.getStaffInfoById(appointer.getId());
						if (appointer != null) {
							PushUtil.pushReClient(c, staff, appointer);
						}
					}
				}
			}
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}
}