package com.crm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.crm.model.ClientInfo;
import com.crm.model.ClientInfoStatis;
import com.crm.model.Company;
import com.crm.model.Dept;
import com.crm.model.Dictionary;
import com.crm.model.Permission;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.model.Status;
import com.crm.service.ClientInfoService;
import com.crm.service.CompanyService;
import com.crm.service.DeptService;
import com.crm.service.DictionaryService;
import com.crm.service.GroupService;
import com.crm.service.InvitationLogService;
import com.crm.service.PermissionService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

/**
 * 门店洽谈
 * 
 * @author JingChenglong 2016-11-07 14:09
 *
 */
@Controller
@RequestMapping("/client")
public class ShopMeetController {

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
	ClientInfoService clientInfoService;/* 客资 */

	@Autowired
	PermissionService permissionService; /* 权限 */

	@Autowired
	DictionaryService dictionaryService;/* 数据字典 */

	@Autowired
	DeptService deptService;/* 部门 */

	@Autowired
	InvitationLogService invitationLogService;/* 邀约记录 */

	private static final Company QIEIN = new Company();
	private static final List<ClientInfo> NO_CLIENT = new ArrayList<ClientInfo>();
	private static final List<Dictionary> NO_DIC = new ArrayList<Dictionary>();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/**
	 * 1：门店洽谈：网络预约到店客资
	 * 
	 */
	@RequestMapping(value = "/to_shop_kz", method = RequestMethod.GET)
	public String listTo_ShopInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		Integer sourceId = StringUtil.nullToIntZero(maps.get("source_id"));
		String statusId = maps.get("status_id");
		String appointTimeStart = maps.get("appoint_time_start");
		String appointTimeEnd = maps.get("appoint_time_end");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("search_key");

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

		if (staff.getShopId() == null || 0 == staff.getShopId()) {
			// 错误页面，当前职工未分配门店信息
		}

		/*-- 状态锁定 --*/
		String stsIds = ClientInfoConstant.BE_COMFIRM;
		stsIds += ",";
		stsIds += ClientInfoConstant.BE_SUCCESS;
		stsIds += ",";
		stsIds += ClientInfoConstant.BE_RUN_OFF;
		stsIds += ",";
		stsIds += ClientInfoConstant.COME_SHOP_FAIL;

		/*-- 判定职员身份，获取查询门市的ID集合 --*/
		String receptorIds = "";
		if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
			// 定点查询某职工
			receptorIds = staffId;
		} else {
			if (StringUtil.isNotEmpty(departId) && !"0".equals(departId)) {
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					receptorIds += s.getId();
					receptorIds += ",";
				}
				if (receptorIds.length() != 0) {
					receptorIds = receptorIds.substring(0, receptorIds.length() - 1);
				}
			}
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
		} else {
			// 网络预约到店客资--默认按照客人预约到店时间升序
			reqContent.put("sortname1", "info.APPOINTTIME");
			reqContent.put("sorttype1", "ASC");
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("shopids", staff.getShopId());
			if (StringUtil.isNotEmpty(appointTimeStart)) {
				reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
			}
			if (StringUtil.isNotEmpty(appointTimeEnd)) {
				reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
			}
			if (StringUtil.isNotEmpty(statusId) && !"0".equals(statusId)) {
				reqContent.put("statusids", statusId);
			} else {
				reqContent.put("statusids", stsIds);// 状态固定
			}
			if (sourceId != null && sourceId != 0) {
				reqContent.put("sourceids", sourceId);
			}
			if (StringUtils.isNotEmpty(staffId) && !"0".equals(staffId) || (StringUtil.isNotEmpty(receptorIds)
					&& !"0".equals(receptorIds) && !staff.getDeptId().equals(departId))) {
				reqContent.put("receptorids", receptorIds);
			}
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
		} else {
			reqContent.put("searchkey", searchKey);
			sourceId = 0;
			appointTimeStart = "";
			appointTimeEnd = "";
		}

		try {
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

		/*-- 客资统计 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("shopids", staff.getShopId());
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsShop");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}
		model.addAttribute("clientInfoStatis", clientInfoStatis);// 门店数据统计结果

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = sourceService.getSrcListOfComp(staff.getCompanyId());
		model.addAttribute("sources", sourceList);

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll;
		List<Status> statusList = new ArrayList<Status>();
		try {
			statusAll = statusService.getStatusInfoAllList(staff.getCompanyId(), "/");
			for (Status sts : statusAll) {
				if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_COMFIRM)
						|| sts.getStatusId().toString().equals(ClientInfoConstant.COME_SHOP_FAIL)
						|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_SUCCESS)
						|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_RUN_OFF)) {
					statusList.add(sts);
				}
			}
		} catch (EduException e1) {
			e1.printStackTrace();
		}
		model.addAttribute("statuses", statusList);

		/*-- 网页系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 门店洽谈结果集 --*/
		List<Dictionary> qtRstList = null;
		try {
			qtRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.SHOP_MEET_QT_RST);
		} catch (EduException e2) {
			qtRstList = NO_DIC;
		}
		model.addAttribute("qtRstList", qtRstList);

		/*-- 流失原因结果集 --*/
		List<Dictionary> runOffList = null;
		try {
			runOffList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.RUN_OFF_REASON);
		} catch (EduException e2) {
			runOffList = NO_DIC;
		}
		model.addAttribute("runOffList", runOffList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("appoint_time_start", appointTimeStart);
		model.addAttribute("appoint_time_end", appointTimeEnd);
		model.addAttribute("searchkey", searchKey);
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/shopmeet/to_shop_kz";
	}

	/**
	 * 2：门店洽谈：预计今日到店
	 * 
	 */
	@RequestMapping(value = "/to_shop_today", method = RequestMethod.GET)
	public String toShopToday(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		Integer sourceId = StringUtil.nullToIntZero(maps.get("source_id"));
		String statusId = maps.get("status_id");
		String appointTimeStart = StringUtil.isNotEmpty(maps.get("appoint_time_start")) ? maps.get("appoint_time_start")
				: TimeUtils.getCurrentymd();
		String appointTimeEnd = StringUtil.isNotEmpty(maps.get("appoint_time_end")) ? maps.get("appoint_time_end")
				: TimeUtils.getCurrentymd();
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("search_key");

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

		if (staff.getShopId() == null || 0 == staff.getShopId()) {
			// 错误页面，当前职工未分配门店信息
		}

		/*-- 状态锁定 --*/
		String stsIds = ClientInfoConstant.BE_COMFIRM;
		stsIds += ",";
		stsIds += ClientInfoConstant.BE_SUCCESS;
		stsIds += ",";
		stsIds += ClientInfoConstant.BE_RUN_OFF;
		stsIds += ",";
		stsIds += ClientInfoConstant.COME_SHOP_FAIL;

		/*-- 判定职员身份，获取查询门市的ID集合 --*/
		String receptorIds = "";
		if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
			// 定点查询某职工
			receptorIds = staffId;
		} else {
			if (StringUtil.isNotEmpty(departId) && !"0".equals(departId)) {
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					receptorIds += s.getId();
					receptorIds += ",";
				}
				if (receptorIds.length() != 0) {
					receptorIds = receptorIds.substring(0, receptorIds.length() - 1);
				}
			}
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
		} else {
			// 网络预约到店客资--默认按照客人预约到店时间升序
			reqContent.put("sortname1", "info.APPOINTTIME");
			reqContent.put("sorttype1", "ASC");
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("shopids", staff.getShopId());
			if (StringUtil.isNotEmpty(appointTimeStart)) {
				reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
			}
			if (StringUtil.isNotEmpty(appointTimeEnd)) {
				reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
			}
			if (StringUtil.isNotEmpty(statusId) && !"0".equals(statusId)) {
				reqContent.put("statusids", statusId);
			} else {
				reqContent.put("statusids", stsIds);// 状态固定
			}
			if (sourceId != null && sourceId != 0) {
				reqContent.put("sourceids", sourceId);
			}
			if (StringUtils.isNotEmpty(staffId) && !"0".equals(staffId) || (StringUtil.isNotEmpty(receptorIds)
					&& !"0".equals(receptorIds) && !staff.getDeptId().equals(departId))) {
				reqContent.put("receptorids", receptorIds);
			}
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
		} else {
			reqContent.put("searchkey", searchKey);
			sourceId = 0;
			appointTimeStart = "";
			appointTimeEnd = "";
		}

		try {
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

		/*-- 客资统计 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("shopids", staff.getShopId());
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsShop");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}
		model.addAttribute("clientInfoStatis", clientInfoStatis);// 门店数据统计结果

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = sourceService.getSrcListOfComp(staff.getCompanyId());
		model.addAttribute("sources", sourceList);

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll;
		List<Status> statusList = new ArrayList<Status>();
		try {
			statusAll = statusService.getStatusInfoAllList(staff.getCompanyId(), "/");
			for (Status sts : statusAll) {
				if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_COMFIRM)
						|| sts.getStatusId().toString().equals(ClientInfoConstant.COME_SHOP_FAIL)
						|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_SUCCESS)
						|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_RUN_OFF)) {
					statusList.add(sts);
				}
			}
		} catch (EduException e1) {
			e1.printStackTrace();
		}
		model.addAttribute("statuses", statusList);

		/*-- 网页系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 门店洽谈结果集 --*/
		List<Dictionary> qtRstList = null;
		try {
			qtRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.SHOP_MEET_QT_RST);
		} catch (EduException e2) {
			qtRstList = NO_DIC;
		}
		model.addAttribute("qtRstList", qtRstList);

		/*-- 流失原因结果集 --*/
		List<Dictionary> runOffList = null;
		try {
			runOffList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.RUN_OFF_REASON);
		} catch (EduException e2) {
			runOffList = NO_DIC;
		}
		model.addAttribute("runOffList", runOffList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("appoint_time_start", appointTimeStart);
		model.addAttribute("appoint_time_end", appointTimeEnd);
		model.addAttribute("searchkey", searchKey);
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/shopmeet/to_shop_today";
	}

	/**
	 * 3：门店洽谈：自然入客
	 * 
	 */
	@RequestMapping(value = "/nature_come_shop", method = RequestMethod.GET)
	public String natureComeShop(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {
		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String statusId = maps.get("status_id");
		String actualTimeStart = StringUtil.isEmpty(maps.get("actual_time_start")) ? TimeUtils.getCurrentymd()
				: maps.get("actual_time_start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("actual_time_end")) ? TimeUtils.getCurrentymd()
				: maps.get("actual_time_end");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("search_key");

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

		if (staff.getShopId() == null || 0 == staff.getShopId()) {
			// 错误页面，当前职工未分配门店信息
		}

		/*-- 判定职员身份，获取查询门市的ID集合 --*/
		String receptorIds = "";
		if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
			// 定点查询某职工
			receptorIds = staffId;
		} else {
			if (StringUtil.isNotEmpty(departId) && !"0".equals(departId)) {
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					receptorIds += s.getId();
					receptorIds += ",";
				}
				if (receptorIds.length() != 0) {
					receptorIds = receptorIds.substring(0, receptorIds.length() - 1);
				}
			}
		}

		Integer sourceId = sourceService.getSrcByName("门店自然入客", staff.getCompanyId()).getSrcId();// 自然入客渠道ID

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
		} else {
			// 门店自然到店客资--默认按照客人到店时间降序
			reqContent.put("sortname1", "info.ACTUALTIME");
			reqContent.put("sorttype1", "DESC");
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("shopids", staff.getShopId());
			if (StringUtil.isNotEmpty(statusId) && !"0".equals(statusId)) {
				reqContent.put("statusids", statusId);
			}
			reqContent.put("actualtimestart", actualTimeStart + " 00:00:00");
			reqContent.put("actualttimeend", actualTimeEnd + " 23:59:59");
			reqContent.put("sourceids", sourceId);
			if (StringUtils.isNotEmpty(staffId) && !"0".equals(staffId) || (StringUtil.isNotEmpty(receptorIds)
					&& !"0".equals(receptorIds) && !staff.getDeptId().equals(departId))) {
				reqContent.put("receptorids", receptorIds);
			}
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
		} else {
			reqContent.put("searchkey", searchKey);
			actualTimeStart = "";
			actualTimeEnd = "";
		}

		try {
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

		/*-- 客资统计 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("shopids", staff.getShopId());
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsShop");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}
		model.addAttribute("clientInfoStatis", clientInfoStatis);// 门店数据统计结果

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll;
		List<Status> statusList = new ArrayList<Status>();
		try {
			statusAll = statusService.getStatusInfoAllList(staff.getCompanyId(), "/");
			for (Status sts : statusAll) {
				if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_SUCCESS)
						|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_RUN_OFF)) {
					statusList.add(sts);
				}
			}
		} catch (EduException e1) {
			e1.printStackTrace();
		}
		model.addAttribute("statuses", statusList);

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = sourceService.getSrcListOfComp(staff.getCompanyId());
		model.addAttribute("sources", sourceList);

		/*-- 网页系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 门店洽谈结果集 --*/
		List<Dictionary> qtRstList = null;
		try {
			qtRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.SHOP_MEET_QT_RST);
		} catch (EduException e2) {
			qtRstList = NO_DIC;
		}
		model.addAttribute("qtRstList", qtRstList);

		/*-- 流失原因结果集 --*/
		List<Dictionary> runOffList = null;
		try {
			runOffList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.RUN_OFF_REASON);
		} catch (EduException e2) {
			runOffList = NO_DIC;
		}
		model.addAttribute("runOffList", runOffList);

		/*-- 门店自然入客结果集 --*/
		List<Dictionary> rkRstList = null;
		try {
			rkRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.SHOP_MEET_RK_RST);
		} catch (EduException e2) {
			rkRstList = NO_DIC;
		}
		model.addAttribute("rkRstList", rkRstList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("statusId", statusId);
		model.addAttribute("actual_time_start", actualTimeStart);
		model.addAttribute("actual_time_end", actualTimeEnd);
		model.addAttribute("searchkey", searchKey);
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "shopmeet/nature_come_shop";

	}

	/**
	 * 4：门店洽谈：成功成交客资
	 * 
	 * ************************************ *** 客资状态为 成功成交 ***
	 * ************************************
	 */
	@RequestMapping(value = "/avi_shop", method = RequestMethod.GET)
	public String listShopInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {
		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		Integer sourceId = StringUtil.nullToIntZero(maps.get("source_id"));
		String actualTimeStart = StringUtil.isEmpty(maps.get("actual_time_start")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("actual_time_end")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_end");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("search_key");

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

		if (staff.getShopId() == null || 0 == staff.getShopId()) {
			// 错误页面，当前职工未分配门店信息
		}

		/*-- 判定职员身份，获取查询门市的ID集合 --*/
		String receptorIds = "";
		if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
			// 定点查询某职工
			receptorIds = staffId;
		} else {
			if (StringUtil.isNotEmpty(departId) && !"0".equals(departId)) {
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					receptorIds += s.getId();
					receptorIds += ",";
				}
				if (receptorIds.length() != 0) {
					receptorIds = receptorIds.substring(0, receptorIds.length() - 1);
				}
			}
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
		} else {
			// 门店成功成交客资--默认按照客人到店时间降序
			reqContent.put("sortname1", "info.ACTUALTIME");
			reqContent.put("sorttype1", "DESC");
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("shopids", staff.getShopId());
			reqContent.put("statusids", ClientInfoConstant.BE_SUCCESS);
			reqContent.put("actualtimestart", actualTimeStart + " 00:00:00");
			reqContent.put("actualttimeend", actualTimeEnd + " 23:59:59");
			if (sourceId != null && sourceId != 0) {
				reqContent.put("sourceids", sourceId);
			}
			if (StringUtils.isNotEmpty(staffId) && !"0".equals(staffId) || (StringUtil.isNotEmpty(receptorIds)
					&& !"0".equals(receptorIds) && !staff.getDeptId().equals(departId))) {
				reqContent.put("receptorids", receptorIds);
			}
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
		} else {
			reqContent.put("searchkey", searchKey);
			sourceId = 0;
			actualTimeStart = "";
			actualTimeEnd = "";
		}

		try {
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

		/*-- 客资统计 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("shopids", staff.getShopId());
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsShop");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}
		model.addAttribute("clientInfoStatis", clientInfoStatis);// 门店数据统计结果

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = sourceService.getSrcListOfComp(staff.getCompanyId());
		model.addAttribute("sources", sourceList);

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll;
		List<Status> statusList = new ArrayList<Status>();
		try {
			statusAll = statusService.getStatusInfoAllList(staff.getCompanyId(), "/");
			for (Status sts : statusAll) {
				if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_SUCCESS)) {
					statusList.add(sts);
				}
			}
		} catch (EduException e1) {
			e1.printStackTrace();
		}
		model.addAttribute("statuses", statusList);

		/*-- 网页系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 门店洽谈结果集 --*/
		List<Dictionary> qtRstList = null;
		try {
			qtRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.SHOP_MEET_QT_RST);
		} catch (EduException e2) {
			qtRstList = NO_DIC;
		}
		model.addAttribute("qtRstList", qtRstList);

		/*-- 流失原因结果集 --*/
		List<Dictionary> runOffList = null;
		try {
			runOffList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.RUN_OFF_REASON);
		} catch (EduException e2) {
			runOffList = NO_DIC;
		}
		model.addAttribute("runOffList", runOffList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("actual_time_start", actualTimeStart);
		model.addAttribute("actual_time_end", actualTimeEnd);
		model.addAttribute("searchkey", searchKey);
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "shopmeet/avi_shop";

	}

	/**
	 * 5：门店洽谈：到店流失
	 * 
	 * **************************** *** 客资状态 流失 *** ****************************
	 */
	@RequestMapping(value = "/to_shop_no", method = RequestMethod.GET)
	public String listShop_NoInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		Integer sourceId = StringUtil.nullToIntZero(maps.get("source_id"));
		String actualTimeStart = StringUtil.isEmpty(maps.get("actual_time_start")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("actual_time_end")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_end");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("search_key");

		String runOffCode = StringUtil.nullToZeroStr(maps.get("runoff_code"));

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

		if (staff.getShopId() == null || 0 == staff.getShopId()) {
			// 错误页面，当前职工未分配门店信息
		}

		/*-- 判定职员身份，获取查询门市的ID集合 --*/
		String receptorIds = "";
		if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
			// 定点查询某职工
			receptorIds = staffId;
		} else {
			if (StringUtil.isNotEmpty(departId) && !"0".equals(departId)) {
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					receptorIds += s.getId();
					receptorIds += ",";
				}
				if (receptorIds.length() != 0) {
					receptorIds = receptorIds.substring(0, receptorIds.length() - 1);
				}
			}
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
		} else {
			// 门店流失客资--默认按照客人到店时间降序
			reqContent.put("sortname1", "info.ACTUALTIME");
			reqContent.put("sorttype1", "DESC");
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("shopids", staff.getShopId());
			reqContent.put("statusids", ClientInfoConstant.BE_RUN_OFF);
			reqContent.put("actualtimestart", actualTimeStart + " 00:00:00");
			reqContent.put("actualtimeend", actualTimeEnd + " 23:59:59");
			if (sourceId != null && sourceId != 0) {
				reqContent.put("sourceids", sourceId);
			}
			if (StringUtils.isNotEmpty(staffId) && !"0".equals(staffId) || (StringUtil.isNotEmpty(receptorIds)
					&& !"0".equals(receptorIds) && !staff.getDeptId().equals(departId))) {
				reqContent.put("receptorids", receptorIds);
			}
			if (StringUtil.isNotEmpty(runOffCode) && !"0".equals(runOffCode)) {
				reqContent.put("runoffcode", runOffCode);
			}
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
		} else {
			reqContent.put("searchkey", searchKey);
			sourceId = 0;
			actualTimeStart = "";
			actualTimeEnd = "";
		}

		try {
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQueryRunOff");
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

		/*-- 客资统计 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("shopids", staff.getShopId());
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsShop");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}
		model.addAttribute("clientInfoStatis", clientInfoStatis);// 门店数据统计结果

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = sourceService.getSrcListOfComp(staff.getCompanyId());
		model.addAttribute("sources", sourceList);

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll;
		List<Status> statusList = new ArrayList<Status>();
		try {
			statusAll = statusService.getStatusInfoAllList(staff.getCompanyId(), "/");
			for (Status sts : statusAll) {
				if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_RUN_OFF)) {
					statusList.add(sts);
				}
			}
		} catch (EduException e1) {
			e1.printStackTrace();
		}
		model.addAttribute("statuses", statusList);

		/*-- 流失原因结果集 --*/
		List<Dictionary> runOffRsnList = null;
		try {
			runOffRsnList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.RUN_OFF_REASON);
		} catch (EduException e2) {
			runOffRsnList = new ArrayList<Dictionary>();
		}
		model.addAttribute("runOffRsnList", runOffRsnList);

		/*-- 网页系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 门店洽谈结果集 --*/
		List<Dictionary> qtRstList = null;
		try {
			qtRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.SHOP_MEET_QT_RST);
		} catch (EduException e2) {
			qtRstList = NO_DIC;
		}
		model.addAttribute("qtRstList", qtRstList);

		/*-- 流失原因结果集 --*/
		List<Dictionary> runOffList = null;
		try {
			runOffList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.RUN_OFF_REASON);
		} catch (EduException e2) {
			runOffList = NO_DIC;
		}
		model.addAttribute("runOffList", runOffList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("actual_time_start", actualTimeStart);
		model.addAttribute("actual_time_end", actualTimeEnd);
		model.addAttribute("searchkey", searchKey);
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/shopmeet/to_shop_no";
	}

	/*
	 * 门店：保存接待门市调配操作
	 */
	@RequestMapping(value = "sava_kz_jdms_mix", method = RequestMethod.POST)
	@ResponseBody
	public Model savaKzJdmsMix(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzIds = maps.get("kzids");
		String recepterId = maps.get("receptorid");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzIds) || StringUtil.isEmpty(recepterId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}
		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 接口调用 ，业务执行 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("kzids", kzIds);
		reqContent.put("receptorid", recepterId);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));

		/*-- 接口调用，业务执行 --*/
		try {
			String bindRstStr = crmBaseApi.doService(reqContent, "doClientReceptorMix");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(bindRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
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