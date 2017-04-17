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
public class ExcelDsController {

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

	/*
	 * 
	 * 电商邀约excel导出
	 * 
	 */

	@RequestMapping(value = "/export_ds_yaoyue_kz", method = RequestMethod.GET)
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

		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("电商邀约客资（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 电商婚期临近客资excel
	 * 
	 */

	@RequestMapping(value = "/export_ds_wedding_near", method = RequestMethod.GET)
	public void exportDsWeddingNear(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String searchKey = maps.get("search_key");
		String departId = maps.get("dept_id");// 小组ID
		String staffId = maps.get("staff_id");// 职员ID

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);

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
		reqContent.put("sortname1", "MARRYTIME");// 根据婚期时间排序
		reqContent.put("sorttype1", "ASC");// 婚期时间升序
		reqContent.put("pagesize", pageSize);
		reqContent.put("currentpage", currentPage);
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
			clientList = new ArrayList<>();
		}

		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("电商婚期临近客资（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 电商预计到店客资excel
	 * 
	 */

	@RequestMapping(value = "/export_ds_to_shop_kz", method = RequestMethod.GET)
	public void exportDsToShopKz(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String searchKey = maps.get("search_key");
		String appointTimeStart = StringUtil.isEmpty(maps.get("appoint_time_start")) ? TimeUtils.getCurrentymd()
				: maps.get("appoint_time_start");
		String appointTimeEnd = StringUtil.isEmpty(maps.get("appoint_time_end")) ? TimeUtils.getCurrentymd()
				: maps.get("appoint_time_end");
		String shopId = maps.get("shop_id");
		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);

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
		reqContent.put("sortname1", "APPOINTTIME");// 根据预约到店时间排序
		reqContent.put("sorttype1", "ASC");// 预约到店时间升序
		reqContent.put("pagesize", pageSize);
		reqContent.put("currentpage", currentPage);
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
			clientList = new ArrayList<>();
		}

		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("电商预计到店客资（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 我的成功到店客资excel
	 * 
	 */
	@RequestMapping(value = "/export_ds_to_shop_success", method = RequestMethod.GET)
	public void exportDsToShopSuccess(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String searchKey = maps.get("search_key");
		String actualTimeStart = StringUtil.isEmpty(maps.get("actual_time_start")) ? TimeUtils.getCurrentymd()
				: maps.get("actual_time_start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("actual_time_end")) ? TimeUtils.getCurrentymd()
				: maps.get("actual_time_end");
		String shopId = maps.get("shop_id");
		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);

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
		reqContent.put("sortname1", "ACTUALTIME");// 根据实际到店时间排序
		reqContent.put("sorttype1", "DESC");// 实际到店时间降序
		reqContent.put("pagesize", pageSize);
		reqContent.put("currentpage", currentPage);
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
			clientList = new ArrayList<>();
		}

		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("我的到店客资（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 4：邀约失败客户excel
	 */
	@RequestMapping(value = "/export_ds_fail_shop_kz", method = RequestMethod.GET)
	public void exporyDsFailShopKz(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String searchKey = maps.get("search_key");
		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);

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
		reqContent.put("sortname1", "CREATETIME");// 根据创建时间排序
		reqContent.put("sorttype1", "DESC");// 创建时间倒序
		reqContent.put("pagesize", pageSize);
		reqContent.put("currentpage", currentPage);
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
			clientList = new ArrayList<>();
		}

		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("邀约失败客资（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 5：长期不联系客户excel
	 */
	@RequestMapping(value = "/export_ds_long_no_kz")
	public void exportDsLongNoKz(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");
		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);

		/*-- 参数范围固定 --*/
		// 状态固定
		if (StringUtil.isEmpty(statusId) || "0".equals(statusId)) {
			statusId = ClientInfoConstant.BE_HAVE_MAKE_ORDER + "," + ClientInfoConstant.BE_WAITING_CALL_A + ","
					+ ClientInfoConstant.BE_WAITING_CALL_B + "," + ClientInfoConstant.BE_WAITING_CALL_C + ","
					+ ClientInfoConstant.BE_COMFIRM + "," + ClientInfoConstant.BE_TRACK;
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
			reqContent.put("sortname1", "CREATETIME");// 根据创建时间排序
			reqContent.put("sorttype1", "DESC");// 创建时间倒序
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("appointids", appointIds);
				reqContent.put("statusids", statusId);
				reqContent.put("updatetimeend", TimeUtils.getBackTime("30"));
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
		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("长期不联系客资（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 电商：客资邀约调配excel
	 * 
	 */
	@RequestMapping(value = "export_do_kz_yy_mix_ds", method = RequestMethod.GET)
	public void exportDoKzYyMixDs(@RequestParam Map<String, String> maps, HttpServletRequest request,
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
		String appointTimeEnd = maps.get("appoint_time");
		String actualTimeEnd = maps.get("actual_time");

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
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
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

		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("电商邀约员调配客资（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 我的客资列表excel
	 * 
	 */
	@RequestMapping(value = "/export_list_new", method = RequestMethod.GET)
	public void exportListNew(@RequestParam Map<String, String> maps, HttpServletRequest request,
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

		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("我的客资列表（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 6：待确定无效客资excel
	 * 
	 */
	@RequestMapping(value = "export_ds_invalid_be_check", method = RequestMethod.GET)
	public void exportDsInvalidBeCheck(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
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
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
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
			clientList = new ArrayList<>();
		}

		try {

			String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名", "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
					"区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 25, 17, 16, 16, 25, 19, 20, 25, 60, 25, 15, 15, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("电商待确定无效客资（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// 提报日期 提报部门 提报人 新客姓名 新客电话 老客姓名 老客电话 区域 到店日期 状态 定单金额 客户备注
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();
				objects[1] = client.getSource();
				objects[2] = client.getCollectorName() + client.getCollector();
				objects[3] = client.getKzName();
				objects[4] = client.getKzPhone();
				objects[5] = client.getOldKzName();
				objects[6] = client.getOldKzPhone();
				objects[7] = client.getKzWechat();
				objects[8] = client.getMemo();
				objects[9] = client.getAddress();
				objects[10] = client.getActualTime();
				objects[11] = client.getStatus();
				objects[12] = client.getAmount();
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}