package com.crm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.ClientInfoConstant;
import com.crm.api.constant.DicConts;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.OSSUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.ClientInfoStatis;
import com.crm.model.Company;
import com.crm.model.Dept;
import com.crm.model.Dictionary;
import com.crm.model.Permission;
import com.crm.model.Shop;
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
 * 电商：客资邀约
 * 
 * @author JingChenglong 2016-10-26 15:43
 *
 */
@Controller
@RequestMapping("/client")
public class DsController {

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
	private PermissionService permissionService; // 权限

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
	 * 1：电商邀约客资
	 * 
	 * ************************************ *** page 当前页码 *** *** size 分页大小 ***
	 * *** start 录入开始时间 *** *** end 录入截止时间 *** *** status_id 客资状态ID *** ***
	 * source_id 客资渠道ID *** *** search_key 查询关键字 *** *** appoint_time 预计到店时间(止)
	 * *** *** actual_time 实际到店时间(止) *** *** deptid 小组ID *** *** staffid 职员ID
	 * *** *******************************************************************
	 * *** 电商邀约员客资搜索： *** 默认显示客资录入时间在今日的客资集合，创建时间可选 *** 预计到店时间默认不做限制
	 * *******************************************************************
	 */
	@RequestMapping(value = "/ds_yaoyue_kz", method = RequestMethod.GET)
	public String dsYaoyueKz(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");
		String appointTimeStart = maps.get("appoint_time_start");
		String appointTimeEnd = maps.get("appoint_time_end");
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

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

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
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
		} else {
			// 电商邀约客资--默认根据客资状态升序，最后跟进时间降序
			reqContent.put("sortname1", "sts.PRIORITY");
			reqContent.put("sorttype1", "ASC");
			reqContent.put("sortname2", "info.UPDATETIME");
			reqContent.put("sorttype2", "DESC");
		}
		try {
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
			clientList = NO_CLIENT;
		}
		model.addAttribute("clients", clientList);

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
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
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
		model.addAttribute("appoint_time_start", appointTimeStart);// 预约到店时间起
		model.addAttribute("appoint_time_end", appointTimeEnd);// 预约到店时间止
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细

		/*-- 临时接口数据 --*/
		// 邀约员---该部门下所有员工
		List<Staff> yyStaffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("yy_staffs", yyStaffs);
		// 提报员---公司所有员工
		List<Staff> tb_staffs = staffService.getByDeptId("0-1-2-4-", "0-1-2-4", staff.getCompanyId());
		model.addAttribute("tb_staffs", tb_staffs);
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/dsyaoyue/ds_yaoyue_kz";
	}

	/**
	 * 2：婚期临近客资
	 * 
	 * ************************************ *** page 当前页码 *** *** size 分页大小 ***
	 * *** status_id 状态ID *** *** source_id 渠道ID *** *** search_key 查询关键字 ***
	 * *** deptid 小组ID *** *** staffid 职员ID ***
	 * ******************************************************************* ***
	 * 电商婚期临近客资搜索： *** 显示婚期时间在未来180天之内的客资信息，重点抓紧追踪 ***
	 * 客资状态只显示：已接单，待邀约，确定意向，客资追踪，四个状态下的客资
	 * *******************************************************************
	 */
	@RequestMapping(value = "/ds_wedding_near", method = RequestMethod.GET)
	public String dsWeddingNear(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String searchKey = maps.get("search_key");
		String departId = maps.get("deptid");// 小组ID
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

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

		/*-- 参数固定范围 --*/
		// 渠道范围固定
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}
		// 状态范围固定
		if (StringUtil.isEmpty(statusId) || "0".equals(statusId)) {
			statusId = ClientInfoConstant.BE_HAVE_MAKE_ORDER + "," + ClientInfoConstant.BE_WAITING_CALL_A + ","
					+ ClientInfoConstant.BE_WAITING_CALL_B + "," + ClientInfoConstant.BE_WAITING_CALL_C + ","
					+ ClientInfoConstant.BE_COMFIRM + "," + ClientInfoConstant.BE_TRACK;
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
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
		} else {
			// 婚期临近客资--默认按照婚期时间升序
			reqContent.put("sortname1", "info.MARRYTIME");
			reqContent.put("sorttype1", "ASC");
		}
		try {
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("appointids", appointIds);
				reqContent.put("marrytimestart", TimeUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				reqContent.put("marrytimeend", TimeUtils.getFutureTime("180"));
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

		/*-- 客资统计 --*/
		reqContent.clear();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("appointids", appointIds);
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsMarry");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}

		model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Status> statusList = new ArrayList<Status>();
		for (Status sts : statusAll) {
			if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_HAVE_MAKE_ORDER)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_WAITING_CALL_A)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_WAITING_CALL_B)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_WAITING_CALL_C)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_COMFIRM)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_TRACK)) {
				statusList.add(sts);
			}
		}
		model.addAttribute("statuses", statusList);

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 页面系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/dsyaoyue/ds_wedding_near";
	}

	/**
	 * 3：预计到店
	 * 
	 * ******************************************** *** page 当前页码 *** *** size
	 * 分页大小 *** *** appoint_time_start 到店时间_起始 *** *** appoint_time_end 到店时间_截止
	 * *** *** status_id 状态ID *** *** source_id 渠道ID *** *** shop_id 门店ID ***
	 * *** search_key 查询关键字 *** *** deptid 小组ID *** *** staffid 职员ID ***
	 * ******************************************************************* ***
	 * 电商预约到店客资搜索： *** 显示邀约员预约成功到店的客资信息，包含短信预览，发送 *** 客资状态限定：确定意向，成功成交，流失，未到店。
	 * *** 客资必须包含预到的门店信息，切需要有预约到店时间信息。
	 * *******************************************************************
	 */
	@RequestMapping(value = "/ds_to_shop_kz", method = RequestMethod.GET)
	public String dsToShopKz(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String searchKey = maps.get("search_key");
		String appointTimeStart = StringUtil.isEmpty(maps.get("appoint_time_start")) ? TimeUtils.getCurrentymd()
				: maps.get("appoint_time_start");
		String appointTimeEnd = StringUtil.isEmpty(maps.get("appoint_time_end")) ? TimeUtils.getCurrentymd()
				: maps.get("appoint_time_end");
		String shopId = maps.get("shop_id");
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

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

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

		/*-- 参数定范围 --*/
		// 状态
		if (StringUtil.isEmpty(statusId) || "0".equals(statusId)) {
			statusId = ClientInfoConstant.BE_COMFIRM + "," + ClientInfoConstant.BE_SUCCESS + ","
					+ ClientInfoConstant.BE_RUN_OFF + "," + ClientInfoConstant.COME_SHOP_FAIL;
		}
		// 渠道
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
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
			// 预计到店--默认按照客人预计到店时间升序
			reqContent.put("sortname1", "info.APPOINTTIME");
			reqContent.put("sorttype1", "ASC");
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("appointids", appointIds);
			reqContent.put("statusids", statusId);// 定点状态
			reqContent.put("sparesql", "info.SHOPID is not null AND info.SHOPID != '' AND info.SHOPID != 0 ");// 客资门店ID不能为空
			if (StringUtil.isNotEmpty(appointTimeStart)) {
				reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
			}
			if (StringUtil.isNotEmpty(appointTimeEnd)) {
				reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
			}
			if (StringUtil.isNotEmpty(shopId) && !"0".equals(shopId)) {
				reqContent.put("shopids", shopId);
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
			shopId = "0";
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
		reqContent.clear();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("appointids", appointIds);
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsComeShop");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}

		model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Status> statusList = new ArrayList<Status>();
		for (Status sts : statusAll) {
			if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_COMFIRM)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_SUCCESS)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_RUN_OFF)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.COME_SHOP_FAIL)) {
				statusList.add(sts);
			}
		}
		model.addAttribute("statuses", statusList);

		/*-- 门店下拉菜单 --*/
		List<Shop> shopList = shopService.listShops(staff.getCompanyId());
		model.addAttribute("shops", shopList);

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 页面系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 筛选信息 --*/
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("shopId", shopId);// 门店ID
		model.addAttribute("appoint_time_start", appointTimeStart);// 预约到店时间起
		model.addAttribute("appoint_time_end", appointTimeEnd);// 预约到店时间止
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/dsyaoyue/ds_to_shop_kz";
	}

	/**
	 * 4：我的到店
	 * 
	 * ******************************************** *** page 当前页码 *** *** size
	 * 分页大小 *** *** actual_time_start 到店时间_起始 *** *** actual_time_end 到店时间_截止
	 * *** *** status_id 状态ID *** *** source_id 渠道ID *** *** shop_id 门店ID ***
	 * *** search_key 查询关键字 *** *** deptid 小组ID *** *** staffid 职员ID ***
	 * ******************************************************************* ***
	 * 电商实际到店客资搜索： *** 显示邀约员成功到店的客资信息 *** 默认显示今日到店的客人信息 *** 客资状态限定：成功成交，流失 ***
	 * 客资必须包含预到的门店信息，切需要有实际到店时间信息。
	 * *******************************************************************
	 */
	@RequestMapping(value = "/ds_to_shop_success", method = RequestMethod.GET)
	public String dsToShopSuccess(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String searchKey = maps.get("search_key");
		String actualTimeStart = StringUtil.isEmpty(maps.get("actual_time_start")) ? TimeUtils.getCurrentymd()
				: maps.get("actual_time_start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("actual_time_end")) ? TimeUtils.getCurrentymd()
				: maps.get("actual_time_end");
		String shopId = maps.get("shop_id");
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

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

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

		/*-- 参数定范围 --*/
		// 状态
		if (StringUtil.isEmpty(statusId) || "0".equals(statusId)) {
			statusId = ClientInfoConstant.BE_SUCCESS + "," + ClientInfoConstant.BE_RUN_OFF;
		}
		// 渠道
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
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
			// 我的到店客资--默认按照客人到店时间降序
			reqContent.put("sortname1", "info.ACTUALTIME");
			reqContent.put("sorttype1", "DESC");
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("appointids", appointIds);
			reqContent.put("statusids", statusId);// 定点状态
			reqContent.put("sparesql", "info.SHOPID is not null AND info.SHOPID != '' AND info.SHOPID != 0 ");// 客资门店ID不能为空
			if (StringUtil.isNotEmpty(actualTimeStart)) {
				reqContent.put("actualtimestart", actualTimeStart + " 00:00:00");
			}
			if (StringUtil.isNotEmpty(actualTimeEnd)) {
				reqContent.put("actualtimeend", actualTimeEnd + " 23:59:59");
			}
			if (StringUtil.isNotEmpty(shopId) && !"0".equals(shopId)) {
				reqContent.put("shopids", shopId);
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
			shopId = "0";
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
		reqContent.clear();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("appointids", appointIds);
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsComeShop");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}

		model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Status> statusList = new ArrayList<Status>();
		for (Status sts : statusAll) {
			if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_SUCCESS)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_RUN_OFF)) {
				statusList.add(sts);
			}
		}
		model.addAttribute("statuses", statusList);

		/*-- 门店下拉菜单 --*/
		List<Shop> shopList = shopService.listShops(staff.getCompanyId());
		model.addAttribute("shops", shopList);

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", QIEIN);
		}

		/*-- 页面系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 筛选信息 --*/
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("shopId", shopId);// 门店ID
		model.addAttribute("actual_time_start", actualTimeStart);// 实际到店时间起
		model.addAttribute("actual_time_end", actualTimeEnd);// 实际到店时间止
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/dsyaoyue/ds_to_shop_success";
	}

	/**
	 * 4：邀约失败客户
	 */
	@RequestMapping(value = "/ds_fail_shop_kz", method = RequestMethod.GET)
	public String dsFailShopKz(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
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

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

		/*-- 参数范围固定 --*/
		// 状态
		if (StringUtil.isEmpty(statusId) || "0".equals(statusId)) {
			statusId = ClientInfoConstant.INVALID_BE_STAY + "," + ClientInfoConstant.BE_INVALID;
		}
		// 渠道
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
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
		} else {
			// 邀约失败--默认按照最后跟进时间降序
			reqContent.put("sortname1", "info.UPDATETIME");
			reqContent.put("sorttype1", "DESC");
		}

		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("appointids", appointIds);
			reqContent.put("statusids", statusId);
			if (StringUtil.isNotEmpty(createTimeStart)) {
				reqContent.put("createtimestart", createTimeStart + " 00:00:00");
			}
			if (StringUtil.isNotEmpty(createTimeEnd)) {
				reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
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
		reqContent.clear();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("appointids", appointIds);
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsYaoFail");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}

		model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果

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

		/*-- 页面系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Status> statusList = new ArrayList<Status>();
		for (Status sts : statusAll) {
			if (sts.getStatusId().toString().equals(ClientInfoConstant.INVALID_BE_STAY)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_INVALID)) {
				statusList.add(sts);
			}
		}
		model.addAttribute("statuses", statusList);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("startStr", createTimeStart);// 录入起始时间
		model.addAttribute("endStr", createTimeEnd);// 录入截止时间
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/dsyaoyue/ds_fail_shop_kz";
	}

	/**
	 * 5：长期不联系客户
	 */
	@RequestMapping(value = "/ds_long_no_kz")
	public String dsLongNoKz(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
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

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

		/*-- 参数范围固定 --*/
		// 状态固定
		if (StringUtil.isEmpty(statusId) || "0".equals(statusId)) {
			statusId = ClientInfoConstant.BE_HAVE_MAKE_ORDER + "," + ClientInfoConstant.BE_COMFIRM + ","
					+ ClientInfoConstant.BE_TRACK;
		}
		// 渠道固定
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
		try {
			reqContent.put("companyid", staff.getCompanyId());
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 长期不联系--默认按照最后跟进时间降序
				reqContent.put("sortname1", "info.UPDATETIME");
				reqContent.put("sorttype1", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("appointids", appointIds);
				reqContent.put("statusids", statusId);
				reqContent.put("updatetimeend", TimeUtils.getBackTime("30"));
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

		/*-- 客资统计 --*/
		reqContent.clear();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("appointids", appointIds);
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsLongLoss");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}

		model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 页面系统时间 --*/
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

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusAll = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Status> statusList = new ArrayList<Status>();
		for (Status sts : statusAll) {
			if (sts.getStatusId().toString().equals(ClientInfoConstant.BE_HAVE_MAKE_ORDER)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_WAITING_CALL_A)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_WAITING_CALL_B)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_WAITING_CALL_C)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_COMFIRM)
					|| sts.getStatusId().toString().equals(ClientInfoConstant.BE_TRACK)) {
				statusList.add(sts);
			}
		}
		model.addAttribute("statuses", statusList);

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 筛选信息 --*/
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "/dsyaoyue/ds_long_no_kz";
	}

	/**
	 * 6：待确定无效客资
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** source_id 客资渠道ID *** *** search_key 查询关键字 *** *** groupid 小组ID
	 * *** *** staffid 职员ID
	 * 
	 */
	@RequestMapping(value = "ds_invalid_be_check", method = RequestMethod.GET)
	public String dsInvalidBeCheck(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
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

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

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
		reqContent = new HashMap<String, Object>();
		try {
			reqContent.put("companyid", staff.getCompanyId());
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 待确定无效客资--默认按照客资的最后跟进时间降序排列
				reqContent.put("sortname1", "info.UPDATETIME");
				reqContent.put("sorttype1", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("collectorids", collectorIds);
				reqContent.put("statusids", ClientInfoConstant.INVALID_BE_STAY);
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				} else {
					reqContent.put("sourceids", sourceIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				sourceId = "0";
				staffId = "0";
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
		return "/dsyaoyue/ds_invalid_be_check";
	}

	/**
	 * 电商：客资邀约调配
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** source_id 客资渠道ID *** *** search_key 查询关键字 *** *** deptid 部门ID ***
	 * *** staffid 职员ID ***
	 * 
	 */
	@RequestMapping(value = "do_kz_yy_mix_ds", method = RequestMethod.GET)
	public String doKzYyMixDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
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

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

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
		try {
			reqContent.put("companyid", staff.getCompanyId());
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 电商邀约调配--默认按照状态升序，最后跟进时间降序
				reqContent.put("sortname1", "sts.PRIORITY");
				reqContent.put("sorttype1", "ASC");
				reqContent.put("sortname2", "info.UPDATETIME");
				reqContent.put("sorttype2", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				if (!staff.getDeptId().equals(departId) || StringUtil.isNotEmpty(staffId)) {
					reqContent.put("appointids", appointIds);
				}
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
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
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
		return "client/kz_yy_mix_ds";
	}

	/**
	 * 电商：客资采集调配
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** source_id 客资渠道ID *** *** search_key 查询关键字 *** *** deptid 部门ID ***
	 * *** staffid 职员ID ***
	 * 
	 */
	@RequestMapping(value = "do_kz_cj_mix_ds", method = RequestMethod.GET)
	public String doKzCjMixDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
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

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

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
		reqContent = new HashMap<String, Object>();
		try {
			reqContent.put("companyid", staff.getCompanyId());
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 电商采集调配--默认按照状态升序，最后跟进时间降序
				reqContent.put("sortname1", "sts.PRIORITY");
				reqContent.put("sorttype1", "ASC");
				reqContent.put("sortname2", "info.UPDATETIME");
				reqContent.put("sorttype2", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("collectorids", collectorIds);
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
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
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
		return "client/kz_cj_mix_ds";
	}

	@RequestMapping(value = "uploadPicture_ds")
	@ResponseBody
	public JSONObject uploadPicture(@RequestParam(value = "hideaddfile", required = false) MultipartFile hideaddfile,
			MultipartHttpServletRequest request) throws Exception {
		JSONObject jsonObject = new JSONObject();

		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			jsonObject.put("success", false);
			jsonObject.put("msg", "没有获取到cookie中的用户信息");
			return jsonObject;
		}

		jsonObject.put("success", false);
		jsonObject.put("msg", "上传失败");
		jsonObject.put("picUrl", "没有路径");

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart == false) {
			jsonObject.put("msg", "提交的数据中不包含文件");
			return jsonObject;
		}

		MultipartFile file = null;
		Iterator<String> iterator = request.getFileNames();
		System.out.println(iterator.hasNext());
		while (iterator.hasNext()) {
			// 取得上传文件
			file = request.getFile(iterator.next());
		}

		if (hideaddfile != null && !hideaddfile.isEmpty()) {
			file = hideaddfile;
		}

		if (file == null) {
			jsonObject.put("success", false);
			jsonObject.put("msg", "上传失败，没有获取到文件");
			return jsonObject;
		}

		String fileName = file.getOriginalFilename();
		String fileSuffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());

		UUID uuid = UUID.randomUUID();

		String name = "yy_" + uuid.toString() + fileSuffix;
		Boolean ss = OSSUtil.putOssObject(name, file.getInputStream());

		jsonObject.put("success", ss);
		jsonObject.put("msg", "上传成功");
		jsonObject.put("picId", name);
		jsonObject.put("picUrl", "http://qieinoa.oss-cn-hangzhou.aliyuncs.com/" + name);
		return jsonObject;
	}

	/*
	 * 新增客资邀约记录
	 * 
	 * *** kzId (必传) 客资ID *** *** statusCode (必传) 邀约结果 *** *** memo 邀约备注 *** ***
	 * picId1 附件1 *** *** picId2 附件2 *** *** picId3 附件3 *** *** remindTime 提醒时间
	 * *** *** arrivelTime 到店时间 *** *** shopId 到店门店ID *** *** receiverId 门店接待ID
	 * ***
	 */
	@RequestMapping(value = "addCallInvitation_ds")
	@ResponseBody
	public Model addDsCallInvitation(@RequestParam(value = "kzId") String kzId,
			@RequestParam(value = "statusCode") Integer statusCode,
			@RequestParam(value = "memo", required = false) String memo,
			@RequestParam(value = "picId1", required = false) String picId1,
			@RequestParam(value = "picId2", required = false) String picId2,
			@RequestParam(value = "picId3", required = false) String picId3,
			@RequestParam(value = "remindTime", required = false) String remindTime,
			@RequestParam(value = "arriveTime", required = false) String arriveTime,
			@RequestParam(value = "shopId", required = false) String shopId,
			@RequestParam(value = "receiverId", required = false) String receiverId, Model model,
			HttpServletRequest request) throws Exception {

		/*-- 职员信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "没有获取到cookie中的用户信息");
			return model;
		}

		/*-- 接口调用，业务执行 --*/
		try {
			reqContent = new HashMap<String, Object>();
			reqContent.put("id", kzId);
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("rstcode", statusCode);
			reqContent.put("staffid", staff.getId());
			if (StringUtil.isNotEmpty(memo)) {
				reqContent.put("memo", memo);
			}
			if (StringUtil.isNotEmpty(picId1)) {
				reqContent.put("file1", picId1);
			}
			if (StringUtil.isNotEmpty(picId2)) {
				reqContent.put("file2", picId2);
			}
			if (StringUtil.isNotEmpty(picId3)) {
				reqContent.put("file3", picId3);
			}
			if (StringUtil.isNotEmpty(remindTime) && TimeUtils.validTime(remindTime, "yyyy-MM-dd HH:mm")) {
				reqContent.put("warntime", remindTime);
			}
			if (StringUtil.isNotEmpty(arriveTime) && TimeUtils.validTime(remindTime, "yyyy-MM-dd HH:mm")) {
				reqContent.put("cometime", arriveTime);
			}
			if (StringUtil.isNotEmpty(shopId)) {
				reqContent.put("shopid", shopId);
			}
			if (StringUtil.isNotEmpty(receiverId)) {
				reqContent.put("receptorid", receiverId);
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "doAddInvitationLog");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(clientRstStr);
			model.addAttribute("code", js.get("code"));
			model.addAttribute("msg", js.get("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	@RequestMapping(value = "listResultDictionary_ds")
	@ResponseBody
	public JSONObject listResultDictionary(HttpServletRequest request) throws Exception {
		JSONObject reply = new JSONObject();
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			reply.put("success", false);
			reply.put("msg", "没有获取到cookie中的用户信息");
			return reply;
		}

		/*-- 邀约结果集 --*/
		List<Dictionary> callRstList = null;
		try {
			callRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.DIC_TYPE_YYRST_DS);
		} catch (EduException e2) {
			callRstList = new ArrayList<Dictionary>();
		}
		reply.put("callRstList", callRstList);

		return reply;
	}

	@RequestMapping(value = "listReceiverByShop_ds")
	@ResponseBody
	public JSONObject listReceiverByShop(HttpServletRequest request) throws Exception {
		JSONObject reply = new JSONObject();
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			reply.put("success", false);
			reply.put("msg", "没有获取到cookie中的用户信息");
			return reply;
		}

		List<Shop> shopList = shopService.listOpeningShops(staff.getCompanyId());
		List<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < shopList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			Staff newStaff = new Staff();
			newStaff.setCompanyId(staff.getCompanyId());
			newStaff.setShopId(shopList.get(i).getShopId());
			List<Staff> staffList = staffService.listStaffByCondition(newStaff);
			map.put("shopId", shopList.get(i).getShopId());
			map.put("shopName", shopList.get(i).getShopName());
			map.put("staffList", staffList);
			hashMaps.add(map);
		}
		reply.put("staffList", hashMaps);
		return reply;
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