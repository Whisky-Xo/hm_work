package com.crm.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.ClientInfoConstant;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.ExcelUtils;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Company;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.model.Status;
import com.crm.service.CompanyService;
import com.crm.service.DeptService;
import com.crm.service.InvitationLogService;
import com.crm.service.PermissionService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

@Controller
@RequestMapping("/client")
public class ExcelNewController {

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */

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
	DeptService deptService;/* 公司管理 */

	@Autowired
	InvitationLogService invitationLogService;/* 邀约记录 */

	private static final Company QIEIN = new Company();
	private static final List<ClientInfo> NO_CLIENT = new ArrayList<ClientInfo>();
	private static final List<Status> NO_STATUS = new ArrayList<Status>();
	private static final List<Source> NO_SOURCE = new ArrayList<Source>();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	private final int pageNum = 9999;

	
	
	/**
	 * 我的客资列表excel
	 * 
	 */
	@RequestMapping(value = "/export_kz_add")
	public void exportKzAdd(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");

		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID

		String sort = maps.get("sort");
		int code = StringUtil.nullToIntZero(maps.get("code"));
		code = code == 0 ? 1 : 0;

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);

		/*-- 渠道范围限定 --*/
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}

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

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		Map<String, Object> reqContent = new HashMap<String, Object>();
		try {
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 录入客资--默认按照创建时间降序
				reqContent.put("sortname1", "info.CREATETIME");
				reqContent.put("sorttype1", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("collectorids", collectorIds);
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
				} else {
					reqContent.put("sourceids", sourceIds);
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
			clientList = new ArrayList<ClientInfo>();
		}
		
		excelControl(response, clientList, staff, "我的客资列表");
	}
	
	
	
	/**
	 * 6.导出客资列表Excel
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** status_id 客资状态ID ***
	 * 
	 * *** is_valid 是否是无效客资
	 * 
	 * @throws EduException
	 *             ***
	 * 
	 */
	@RequestMapping(value = "/export_yaoyue_zjs", method = RequestMethod.GET)
	@ResponseBody
	public void exportYaoyueZjs(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");
		String appointTimeStart = maps.get("appoint_time_start");
		String appointTimeEnd = maps.get("appoint_time_end");
		String actualTimeEnd = maps.get("actual_time");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int code = StringUtil.nullToIntZero(maps.get("code"));
		code = code == 0 ? 1 : 0;

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_INTRODUCE);

		/*-- 参数范围固定 --*/
		// 渠道范围
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}

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

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		try {
			reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 转介绍客资首页--默认根据客资状态升序，最后跟进时间降序
				reqContent.put("sortname1", "sts.PRIORITY");
				reqContent.put("sorttype1", "ASC");
				reqContent.put("sortname2", "info.UPDATETIME");
				reqContent.put("sorttype2", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("appointids", appointIds);
				if (StringUtil.isNotEmpty(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(appointTimeStart)) {
					reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
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
				} else {
					reqContent.put("sourceids", sourceIds);
				}

			} else {
				reqContent.put("searchkey", searchKey.trim());
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
			clientList = new ArrayList<>();
		}

		excelControl(response, clientList, staff, "转介绍邀约客资");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/export_promote_zjs", method = RequestMethod.GET)
	@ResponseBody
	public void exportPromoteZjs(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String searchKey = maps.get("searchkey");

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int sortCode = StringUtil.nullToIntZero(maps.get("sortcode"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			sortCode = sortCode == 0 ? 1 : 0;
		}

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_INTRODUCE);

		/*-- 渠道范围限定 --*/
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}

		/*-- 判定职员身份，获取查询邀约员的ID集合 --*/
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
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? "ASC" : "DESC");
		} else {
			// 电商推广客资--默认根据客资状态升序，最后跟进时间降序
			reqContent.put("sortname1", "sts.PRIORITY");
			reqContent.put("sorttype1", "ASC");
			reqContent.put("sortname2", "info.UPDATETIME");
			reqContent.put("sorttype2", "DESC");
			sort = "sts.PRIORITY";
			sortCode = 1;
		}
		try {
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("promoterids", promoterIds);
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
				} else {
					reqContent.put("sourceids", sourceIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				statusId = "0";
				sourceId = "0";
				staffId = "0";
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
			pageInfo.put("pagesize", 20);
			pageInfo.put("currentpage", 1);
			clientList = new ArrayList<>();
		}
		
		excelControl(response, clientList, staff, "转介绍推广客资");

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 
	 * 电商邀约excel导出
	 * 
	 */

	@RequestMapping(value = "/export_yaoyue_ds", method = RequestMethod.GET)
	public void exportDsYaoyueKz(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");
		String appointTimeStart = maps.get("appoint_time_start");
		String appointTimeEnd = maps.get("appoint_time_end");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);

		/*-- 参数范围固定 --*/
		// 渠道范围
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}

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

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		try {
			reqContent.put("sortname1", "CREATETIME");// 根据创建时间排序
			reqContent.put("sorttype1", "DESC");// 创建时间倒序
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("appointids", appointIds);
				if (StringUtil.isNotEmpty(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(appointTimeStart)) {
					reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(appointTimeEnd)) {
					reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(statusId) && !"0".equals(statusId)) {
					reqContent.put("statusids", statusId);
				}
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				} else {
					reqContent.put("sourceids", sourceIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("currentpage", currentPage);
				reqContent.put("pagesize", pageSize);
				reqContent.put("searchkey", searchKey.trim());
				statusId = "0";
				sourceId = "0";
				staffId = "0";
				createTimeStart = "";
				createTimeEnd = "";
				appointTimeEnd = "";
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
			clientList = new ArrayList<ClientInfo>();
		}

		
		excelControl(response, clientList, staff, "电商邀约客资");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
					srcList = sourceService.getSrcListByStaffYy(staff.getId());
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
	
	
	
	
	
		public void excelControl(HttpServletResponse response, List<ClientInfo> clientList, Staff staff, String excelName) {

			try {
				String[] titles = new String[]{"提报时间","提报人","提报渠道","新客姓名","新客电话","新客微信","新客QQ",
									"配偶姓名","配偶电话/微信","婚期","地址","追踪备注","当前状态","邀约员",
									"推广员","跟进时间","到店时间","接待门店","接待门市","优惠编码","成交时间",
									"成交金额","老客姓名","老客电话/微信"};	   				        	                           	  	
//				String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
//						"区域", "到店日期", "状态", "订单金额" };
				int[] length = new int[] { 19, 12, 12, 15, 18, 18, 18, 15, 15, 22, 25, 63, 15,
						15, 17, 26, 26, 15, 19, 20, 25, 20, 15, 25};
				// 导出
				response.setContentType("application/x-msdownload;charset=gbk");
				response.setCharacterEncoding("UTF-8");
				String fileNameTemp;
				fileNameTemp = URLEncoder.encode(excelName + "（" + staff.getName() + "）.xls", "UTF-8");
				response.setHeader("Content-Disposition",
						"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
				OutputStream os = response.getOutputStream();
				ExcelUtils eu = new ExcelUtils();
				List<Object[]> datas = new ArrayList<Object[]>();
				for (ClientInfo client : clientList) {
//					"","","","","","","",
//					"","","","","","","",
//					"","","到店时间","接待门店","接待门市","优惠编码","成交时间",
//					"成交金额","老客姓名","老客电话/微信"
					Object[] objects = new Object[titles.length];
					objects[0] = client.getCreateTime();//提报时间
					objects[1] = client.getCollectorName() + client.getCollector();//提报人
					objects[2] = client.getSource();//提报渠道
					objects[3] = client.getKzName();//新客姓名
					objects[4] = client.getKzPhone();//新客电话
					objects[5] = client.getKzWechat();//新客微信
					objects[6] = client.getKzQq();//新客QQ
					objects[7] = client.getMateName();//配偶姓名
					objects[8] = client.getMatePhone();//配偶电话/微信
					objects[9] = client.getMarryTime();//婚期
					objects[10] = client.getAddress();//地址
					objects[11] = client.getMemo();//追踪备注
					objects[12] = client.getStatus()+","+client.getStsColor();//当前状态
					objects[13] = client.getAppoint();//邀约员
					objects[14] = client.getPromoter();//推广员
					objects[15] = client.getUpdateTime();//跟进时间
					objects[16] = client.getComeShopTime();//到店时间
					objects[17] = client.getShopName();//接待门店
					objects[18] = client.getReceptor();//接待门市
					objects[19] = client.getSmsCode();//优惠编码
					objects[20] = client.getSuccessTime();//成交时间
					objects[21] = client.getAmount();//成交金额
					objects[22] = client.getOldKzName();//老客姓名
					objects[23] = client.getOldKzPhone();//老客电话/微信
					datas.add(objects);
				}
				eu.exportColor(os, titles, length, datas);
				os.flush();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	
	
	
	
	
	
	
	
	
	

}