package com.crm.controller;

import java.util.ArrayList;
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
import com.crm.api.constant.ClientInfoConstant;
import com.crm.api.constant.DicConts;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ApproveLog;
import com.crm.model.ClientInfo;
import com.crm.model.Company;
import com.crm.model.Dept;
import com.crm.model.Dictionary;
import com.crm.model.Permission;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.model.Status;
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
 * 转介绍：客资推广
 * 
 * @author JingChenglong 2016-11-24 17:16
 *
 */
@Controller
@RequestMapping("/client")
public class CallExpandController {

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */

	@Autowired
	ClientInfoService clientInfoService;/* 客资 */

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
	PermissionService permissionService; /* 权限 */

	@Autowired
	ShopService shopService; /* 门店 */

	@Autowired
	DeptService deptService; /* 部门 */

	@Autowired
	ApproveLogService approveLogService; /* 审批日志 */

	private static final Company QIEIN = new Company();
	private static final List<ClientInfo> NO_CLIENT = new ArrayList<ClientInfo>();
	private static final List<Status> NO_STATUS = new ArrayList<Status>();
	private static final List<Source> NO_SOURCE = new ArrayList<Source>();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/**
	 * 1：转介绍推广，我的客资
	 * 
	 * *** 获取该邀约员负责的渠道下全部的未标记邀约员的客资集合 *** *** page 当前页码 *** *** size 分页大小 ***
	 */
	@RequestMapping(value = "/zjs_expand_index", method = RequestMethod.GET)
	public String bantchJdIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int code = StringUtil.nullToIntZero(maps.get("code"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			code = code == 0 ? 1 : 0;
		}

		/*-- 参数补全 --*/
		if (StringUtil.isEmpty(statusId)) {
			statusId = "0";
		}
		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 判定职员身份，获取查询推广员的ID集合 --*/
		String promoterIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				promoterIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					promoterIds += s.getId();
					promoterIds += ",";
				}
				if (promoterIds.length() != 0) {
					promoterIds = promoterIds.substring(0, promoterIds.length() - 1);
				}
			}
		} else {
			promoterIds = staff.getId() + "";
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		try {
			if (StringUtil.isEmpty(promoterIds) || "0".equals(promoterIds)) {
				throw new Exception();
			}
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 转介绍推广客资--默认按照状态升序，最后跟进时间降序
				reqContent.put("sortname1", "sts.PRIORITY");
				reqContent.put("sorttype1", "ASC");
				reqContent.put("sortname2", "info.UPDATETIME");
				reqContent.put("sorttype2", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("promoterids", promoterIds);// 推广员ID
				if (StringUtil.isNotEmpty(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(statusId) && !"0".equals(statusId)) {
					reqContent.put("statusids", statusId);
				}
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
			} else {
				reqContent.put("searchkey", searchKey);
				statusId = "0";
				sourceId = "0";
				createTimeStart = "";
				createTimeEnd = "";
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}
		model.addAttribute("clients", clientList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 分页信息 --*/
		model.addAttribute("total", pageInfo.get("totalcount"));// 总记录数
		model.addAttribute("totalPage", pageInfo.get("totalpage"));// 总页数
		model.addAttribute("size", pageInfo.get("pagesize"));// 分页大小
		model.addAttribute("page", pageInfo.get("currentpage"));// 当前页码

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_INTRODUCE);

		model.addAttribute("sources", sourceList);

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 客资状态下拉菜单 --*/
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_ZJS);
		model.addAttribute("statuses", statusList);

		/*-- 网页系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 筛选信息 --*/
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("startStr", createTimeStart);// 录入起始时间
		model.addAttribute("endStr", createTimeEnd);// 录入截止时间
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/client/my_zhuan_tui";
	}

	/**
	 * 2：待确定无效客资
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** source_id 客资渠道ID *** *** search_key 查询关键字 *** *** deptid 部门ID ***
	 * *** staffid 职员ID ***
	 * 
	 */
	@RequestMapping(value = "invalid_be_check", method = RequestMethod.GET)
	public String beInvalidCheck(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int code = StringUtil.nullToIntZero(maps.get("code"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			code = code == 0 ? 1 : 0;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 判定职员身份，获取查询推广员的ID集合 --*/
		String promoterIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				promoterIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					promoterIds += s.getId();
					promoterIds += ",";
				}
				if (promoterIds.length() != 0) {
					promoterIds = promoterIds.substring(0, promoterIds.length() - 1);
				}
			}
		} else {
			promoterIds = staff.getId() + "";
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		try {
			if (StringUtil.isEmpty(promoterIds) || "0".equals(promoterIds)) {
				throw new Exception();
			}
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 待确定无效客资--默认按照最后跟进时间降序
				reqContent.put("sortname1", "info.UPDATETIME");
				reqContent.put("sorttype1", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("promoterids", promoterIds);// 推广员
				reqContent.put("statusids", ClientInfoConstant.INVALID_BE_STAY);
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}

			} else {
				reqContent.put("searchkey", searchKey);
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		model.addAttribute("clients", clientList);

		/*-- 分页信息 --*/
		model.addAttribute("total", pageInfo.get("totalcount"));// 总记录数
		model.addAttribute("totalPage", pageInfo.get("totalpage"));// 总页数
		model.addAttribute("size", pageInfo.get("pagesize"));// 分页大小
		model.addAttribute("page", pageInfo.get("currentpage"));// 当前页码

		/*-- 系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_INTRODUCE);
		model.addAttribute("sources", sourceList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 客资无效原因结果集 --*/
		List<Dictionary> invalidReason = null;
		try {
			invalidReason = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.INVALID_REASON);
		} catch (EduException e2) {
			invalidReason = new ArrayList<Dictionary>();
		}
		model.addAttribute("invalidReasonList", invalidReason);

		/*-- 筛选信息 --*/
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "client/invalid_be_check";
	}

	/**
	 * 3：转介绍：客资推广调配
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** source_id 客资渠道ID *** *** search_key 查询关键字 *** *** deptid 部门ID ***
	 * *** staffid 职员ID ***
	 * 
	 */
	@RequestMapping(value = "do_kz_tg_mix", method = RequestMethod.GET)
	public String doKzTgMix(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");
		String appointTimeEnd = maps.get("appoint_time");
		String actualTimeEnd = maps.get("actual_time");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int code = StringUtil.nullToIntZero(maps.get("code"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			code = code == 0 ? 1 : 0;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 判定职员身份，获取查询推广员的ID集合 --*/
		String promoterIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				promoterIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					promoterIds += s.getId();
					promoterIds += ",";
				}
				if (promoterIds.length() != 0) {
					promoterIds = promoterIds.substring(0, promoterIds.length() - 1);
				}
			}
		} else {
			promoterIds = staff.getId() + "";
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		try {
			if (StringUtil.isEmpty(promoterIds) || "0".equals(promoterIds)) {
				throw new Exception();
			}
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 转介绍推广调配---默认按照状态升序，最后跟进时间降序
				reqContent.put("sortname1", "sts.PRIORITY");
				reqContent.put("sorttype1", "ASC");
				reqContent.put("sortname2", "info.UPDATETIME");
				reqContent.put("sorttype2", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("promoterids", promoterIds);// 推广员ID
				if (StringUtil.isNotEmpty(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(appointTimeEnd)) {
					reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(actualTimeEnd)) {
					reqContent.put("actualtimeend", actualTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(statusId) && !"0".equals(statusId)) {
					reqContent.put("statusids", statusId);
				}
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}

			} else {
				reqContent.put("searchkey", searchKey);
				statusId = "0";
				sourceId = "0";
				staffId = "0";
				createTimeStart = "";
				createTimeEnd = "";
				appointTimeEnd = "";
				actualTimeEnd = "";
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}
		model.addAttribute("clients", clientList);

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_INTRODUCE);
		model.addAttribute("sources", sourceList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 分页信息 --*/
		model.addAttribute("total", pageInfo.get("totalcount"));// 总记录数
		model.addAttribute("totalPage", pageInfo.get("totalpage"));// 总页数
		model.addAttribute("size", pageInfo.get("pagesize"));// 分页大小
		model.addAttribute("page", pageInfo.get("currentpage"));// 当前页码

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 客资状态下拉菜单 --*/
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_ZJS);
		model.addAttribute("statuses", statusList);

		/*-- 网页系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 筛选信息 --*/
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("startStr", createTimeStart);// 录入起始时间
		model.addAttribute("endStr", createTimeEnd);// 录入截止时间
		model.addAttribute("appoint_time", appointTimeEnd);// 预约到店时间
		model.addAttribute("actual_time", actualTimeEnd);// 实际到店时间
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "client/kz_tg_mix";
	}

	// /*
	// * 客资确定无效/驳回
	// *
	// * *** kzids (必传) 客资ID *** *** code (必传) 1:确定无效 2:驳回 *** *** memo 备注 ***
	// */
	// @RequestMapping(value = "do_kz_be_invalid", method = RequestMethod.POST)
	// @ResponseBody
	// public Model doKzMakeOrder(@RequestParam Map<String, String> maps, Model
	// model, HttpServletRequest request,
	// HttpServletResponse response) throws EduException {
	//
	// /*-- 参数提取 --*/
	// String kzIds = maps.get("kzids");
	// String code = maps.get("code");
	// String memo = maps.get("memo");
	// String invalidCode = maps.get("invalid_code");
	// String invalidLabel = maps.get("invalid_label");
	//
	// if (StringUtil.isNotEmpty(invalidLabel)) {
	// invalidLabel = invalidLabel.trim();
	// }
	// if (StringUtil.isNotEmpty(memo)) {
	// memo = memo.trim();
	// }
	//
	// /*-- 职工信息 --*/
	// Staff staff = CookieCompoment.getLoginUser(request);
	// staff = staffService.getStaffInfoById(staff.getId());
	//
	// /*-- 接口调用 ，业务执行 --*/
	// reqContent = new HashMap<String, Object>();
	// reqContent.put("kzids", kzIds);
	// reqContent.put("operaid", staff.getId());
	// reqContent.put("companyid", staff.getCompanyId());
	// reqContent.put("ip", WebUtils.getIP(request));
	// if ("1".equals(code)) {
	// reqContent.put("statusid", ClientInfoConstant.BE_INVALID);
	// invalidLabel += " , ";
	// invalidLabel += memo;
	// reqContent.put("memo", invalidLabel);
	// } else {
	// reqContent.put("statusid", ClientInfoConstant.BE_TRACK);
	// reqContent.put("memo", memo);
	// }
	//
	// /*-- 接口调用，业务执行 --*/
	// try {
	// String bindRstStr = crmBaseApi.doService(reqContent,
	// "doClientEditStatus");
	// JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(bindRstStr);
	// model.addAttribute("code", jsInfo.getString("code"));
	// model.addAttribute("msg", jsInfo.getString("msg"));
	//
	// // 消息通知
	// String[] ids = kzIds.split(",");
	// ClientInfo client = new ClientInfo();
	// client.setCompanyId(staff.getCompanyId());
	// Staff sf = new Staff();
	// sf.setCompanyId(staff.getCompanyId());
	// for (String id : ids) {
	// client.setKzId(id);
	// try {
	// client = clientInfoService.getClientInfo(client);
	// if (client != null) {
	// if ("1".equals(code)) {
	// // 如果客资被判定无效，则通知采集员，推送客资无效信息
	// sf.setId(client.getCollectorId());
	// sf = staffService.getStaffInfoById(sf.getId());
	//
	// PushUtil.pushCjValid(sf, client, invalidLabel);
	//
	// ApproveLog log = new ApproveLog();
	// log.setKzId(id);
	// log.setCreateIp(WebUtils.getIP(request));
	// log.setCompanyId(staff.getCompanyId());
	// log.setOperaId(staff.getId());
	// log.setApproveType(ApproveLog.INVALID_KZ);
	// log.setCode(Integer.valueOf(invalidCode));
	//
	// approveLogService.createApproveLog(log);
	// } else {
	// // 如果客资无效被驳回，则通知邀约员，推送无效驳回信息
	// sf.setId(client.getAppointId());
	// sf = staffService.getStaffInfoById(sf.getId());
	//
	// PushUtil.pushYyValidReject(sf, client, memo);
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// } catch (EduException e) {
	// model.addAttribute("code", "999999");
	// model.addAttribute("msg", "网络错误");
	// }
	//
	// return model;
	// }

	// 获取页面渠道下拉菜单
	public List<Source> getSrcList(Staff staff, String srcType) {

		if (staff == null || srcType == null || "".equals(srcType)) {
			return NO_SOURCE;
		}

		List<Source> srcList = null;
		try {
			if (staff.getIsChief()) {
				// 如果是主管，获取指定渠道类型的所有渠道
				srcList = sourceService.getSrcListByType(staff.getCompanyId(), srcType);
			} else {
				// 非主管，只显示自己所负责的渠道
				srcList = sourceService.getSrcListByStaffTg(staff.getId());
			}

			if (srcList == null) {
				srcList = NO_SOURCE;
			}
		} catch (EduException e) {
			srcList = NO_SOURCE;
			e.printStackTrace();
		}

		return srcList;
	}

	// 获取企业状态列表
	public List<Status> getStatusList(Integer companyId, String statusType) {

		if (companyId == null || companyId == 0 || StringUtil.isEmpty(statusType)) {
			return null;
		}

		List<Status> statusList = null;

		try {
			statusList = statusService.getStatusInfoAllList(companyId, statusType);
		} catch (EduException e) {
			statusList = NO_STATUS;
			e.printStackTrace();
		}

		return statusList;
	}

	public Map<String, String> pageControl(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Staff staff = CookieCompoment.getLoginUser(request);
		int staffId = staff.getId();
		List<Permission> permissions = permissionService.getByPermissionByStaffId(staffId);
		for (Permission permission : permissions) {
			if ("true".equals(permission.getValue())) {
				map.put("P" + permission.getPermissionId(), permission.getValue());
			}
		}
		return map;
	}
}