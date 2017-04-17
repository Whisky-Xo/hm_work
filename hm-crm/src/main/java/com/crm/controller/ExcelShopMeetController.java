package com.crm.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.ClientInfoConstant;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.ExcelUtils;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.DeptService;
import com.crm.service.InvitationLogService;
import com.crm.service.PermissionService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

@Controller
@RequestMapping("/client")
public class ExcelShopMeetController {

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

	private static Map<String, Object> reqContent;

	private final int pageNum = 9999;

	@RequestMapping(value = "/export_to_shop_kz", method = RequestMethod.GET)
	@ResponseBody
	public void exportToShopKz(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = 9999;
		Integer sourceId = StringUtil.nullToIntZero(maps.get("source_id"));
		String statusId = maps.get("status_id");
		String appointTimeStart = maps.get("appoint_time_start");
		String appointTimeEnd = maps.get("appoint_time_end");
		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID
		String searchKey = maps.get("search_key");

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

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
			fileNameTemp = URLEncoder.encode("网络预约到店（" + staff.getName() + "）.xls", "UTF-8");
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
	 * 2：门店洽谈：自然入客
	 * 
	 */
	@RequestMapping(value = "/export_nature_come_shop", method = RequestMethod.GET)
	public void exportNatureComeShop(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {
		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		String statusId = maps.get("status_id");
		String actualTimeStart = StringUtil.isEmpty(maps.get("actual_time_start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("actual_time_start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("actual_time_end")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_end");
		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID
		String searchKey = maps.get("search_key");

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

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
			fileNameTemp = URLEncoder.encode("自然入客（" + staff.getName() + "）.xls", "UTF-8");
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
	 * 3：门店洽谈：成功成交客资
	 * 
	 * ************************************
	 * *** 客资状态为	成功成交				***
	 * ************************************
	 */
	@RequestMapping(value = "/export_avi_shop", method = RequestMethod.GET)
	public void exportAviShop(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {
		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		Integer sourceId = StringUtil.nullToIntZero(maps.get("source_id"));
		String actualTimeStart = StringUtil.isEmpty(maps.get("actual_time_start")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("actual_time_end")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_end");
		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID
		String searchKey = maps.get("search_key");

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

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
			fileNameTemp = URLEncoder.encode("顺利成交（" + staff.getName() + "）.xls", "UTF-8");
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
	 * 4：门店洽谈：到店流失
	 * 
	 * ****************************
	 * *** 客资状态		流失		***
	 * ****************************
	 */
	@RequestMapping(value = "/export_to_shop_no", method = RequestMethod.GET)
	public void exportToShopNo(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = pageNum;
		Integer sourceId = StringUtil.nullToIntZero(maps.get("source_id"));
		String actualTimeStart = StringUtil.isEmpty(maps.get("actual_time_start")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("actual_time_end")) ? TimeUtils.getSysdate()
				: maps.get("actual_time_end");
		String departId = maps.get("dept_id");// 部门ID
		String staffId = maps.get("staff_id");// 职员ID
		String searchKey = maps.get("search_key");

		String runOffCode = StringUtil.nullToZeroStr(maps.get("runoff_code"));

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

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
			fileNameTemp = URLEncoder.encode("到店流失（" + staff.getName() + "）.xls", "UTF-8");
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

}