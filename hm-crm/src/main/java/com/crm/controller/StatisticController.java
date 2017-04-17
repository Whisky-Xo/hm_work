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
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;
import com.crm.model.ClientInfoStatis;
import com.crm.model.Company;
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
 * 数据统计
 * 
 * @author JingChenglong 2016-12-23 18:45
 *
 */
@Controller
@RequestMapping("/client")
public class StatisticController {

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

	private static final Company QIEIN = new Company();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/*-- 1：电商采集员提报数据顶部统计 --*/
	@RequestMapping(value = "ds_collector_statis", method = RequestMethod.POST)
	@ResponseBody
	public Model dsCollectorStatis(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		/*-- 判定职员身份，获取查询采集员的ID集合 --*/
		String collectorIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				collectorIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					collectorIds += s.getId();
					collectorIds += ",";
				}
				if (collectorIds.length() != 0) {
					collectorIds = collectorIds.substring(0, collectorIds.length() - 1);
				}
			}
		} else {
			collectorIds = staff.getId() + "";
		}

		try {
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("collectorids", collectorIds);
			String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsDsCj");
			JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
			ClientInfoStatis clientInfoStatis = null;
			if (jsStatistics != null) {
				clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
						ClientInfoStatis.class);
			}

			model.addAttribute("clientInfoStatis", clientInfoStatis);// 采集员统计结果
		} catch (EduException e) {
			model.addAttribute("clientInfoStatis", new ClientInfoStatis("null"));
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 2：电商邀约员邀约数据顶部统计 --*/
	@RequestMapping(value = "ds_appointor_statis", method = RequestMethod.POST)
	@ResponseBody
	public Model dsAppointorStatis(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		/*-- 判定职员身份，获取查询邀约员的ID集合 --*/
		String appointIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				appointIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					appointIds += s.getId();
					appointIds += ",";
				}
				if (appointIds.length() != 0) {
					appointIds = appointIds.substring(0, appointIds.length() - 1);
				}
			}
		} else {
			appointIds = staff.getId() + "";
		}

		try {
			/*-- 客资统计 --*/
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("appointids", appointIds);
			String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsDsYy");
			JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
			ClientInfoStatis clientInfoStatis = null;
			if (jsStatistics != null) {
				clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
						ClientInfoStatis.class);
			}

			model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果
		} catch (EduException e) {
			model.addAttribute("clientInfoStatis", new ClientInfoStatis("null"));
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 3：电商邀约员到店数据顶部统计 --*/
	@RequestMapping(value = "ds_come_shop_statis", method = RequestMethod.POST)
	@ResponseBody
	public Model dsComeShopStatis(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		/*-- 判定职员身份，获取查询邀约员的ID集合 --*/
		String appointIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				appointIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					appointIds += s.getId();
					appointIds += ",";
				}
				if (appointIds.length() != 0) {
					appointIds = appointIds.substring(0, appointIds.length() - 1);
				}
			}
		} else {
			appointIds = staff.getId() + "";
		}

		try {
			/*-- 客资统计 --*/
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("appointids", appointIds);
			String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsComeShopDs");
			JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
			ClientInfoStatis clientInfoStatis = null;
			if (jsStatistics != null) {
				clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
						ClientInfoStatis.class);
			}

			model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果
		} catch (EduException e) {
			model.addAttribute("clientInfoStatis", new ClientInfoStatis("null"));
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 4：转介绍邀约员邀约数据顶部统计 --*/
	@RequestMapping(value = "zjs_appointor_statis", method = RequestMethod.POST)
	@ResponseBody
	public Model zjsAppointorStatis(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		/*-- 判定职员身份，获取查询邀约员的ID集合 --*/
		String appointIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				appointIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					appointIds += s.getId();
					appointIds += ",";
				}
				if (appointIds.length() != 0) {
					appointIds = appointIds.substring(0, appointIds.length() - 1);
				}
			}
		} else {
			appointIds = staff.getId() + "";
		}

		try {
			/*-- 客资统计 --*/
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("appointids", appointIds);
			String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsZjsYy");
			JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
			ClientInfoStatis clientInfoStatis = null;
			if (jsStatistics != null) {
				clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
						ClientInfoStatis.class);
			}

			model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果
		} catch (EduException e) {
			model.addAttribute("clientInfoStatis", new ClientInfoStatis("null"));
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 5：转介绍-到店数据统计 --*/
	@RequestMapping(value = "zjs_come_shop_statis", method = RequestMethod.POST)
	@ResponseBody
	public Model zjsComeShopStatis(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		/*-- 判定职员身份，获取查询邀约员的ID集合 --*/
		String appointIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				appointIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					appointIds += s.getId();
					appointIds += ",";
				}
				if (appointIds.length() != 0) {
					appointIds = appointIds.substring(0, appointIds.length() - 1);
				}
			}
		} else {
			appointIds = staff.getId() + "";
		}

		try {
			/*-- 客资统计 --*/
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("appointids", appointIds);
			String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsComeShopZjs");
			JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
			ClientInfoStatis clientInfoStatis = null;
			if (jsStatistics != null) {
				clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
						ClientInfoStatis.class);
			}

			model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果
		} catch (EduException e) {
			model.addAttribute("clientInfoStatis", new ClientInfoStatis("null"));
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}
}