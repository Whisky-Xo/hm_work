package com.crm.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.ClientInfoConstant;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.common.util.ExcelUtils;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.common.util.UtilRegex;
import com.crm.common.util.WebUtils;
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

/**
 * 客资管理：mini版本
 * 
 * @author JingChenglong 2016-09-12 15:04
 *
 */
@Controller
@RequestMapping("/client")
public class ClientInfoController {

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

	/**
	 * 1：获取客资：录入客资--我的客资列表
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** status_id 客资状态ID
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listClientInfoMini(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStrDate("30") : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		Integer statusId = StringUtil.nullToIntZero(maps.get("status_id"));
		String sortname1 = StringUtil.isEmpty(maps.get("sortname1")) ? "" : maps.get("sortname1");
		String sorttype1 = StringUtil.isEmpty(maps.get("sorttype1")) ? "" : maps.get("sorttype1");
		String sort = StringUtil.isEmpty(maps.get("sort")) ? "false" : maps.get("sort");
		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusList;
		try {
			statusList = statusService.getStatusInfoAllList(1, "/");
		} catch (EduException e1) {
			statusList = new ArrayList<Status>();
		}
		model.addAttribute("statuses", statusList);

		/*-- 支付宝口碑渠道 --*/
		Source srcAliPay = sourceService.getSrcByName(Source.SRC_ALIPAY, staff.getCompanyId());

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		Map<String, Object> reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("sortname1", "CREATETIME");// 根据创建时间排序
		reqContent.put("sorttype1", "DESC");// 创建时间倒序
		if (StringUtil.isNotEmpty(createTimeStart)) {
			reqContent.put("createtimestart", createTimeStart + " 00:00:00");
		}
		if (StringUtil.isNotEmpty(createTimeEnd)) {
			reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
		}
		if (statusId != null && statusId != 0) {
			reqContent.put("statusids", statusId);
		}
		reqContent.put("sourceids", srcAliPay.getSrcId());
		reqContent.put("pagesize", pageSize);
		reqContent.put("currentpage", currentPage);

		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("companyName", company.getWebSite());
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQueryMini");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
			model.addAttribute("company", company);
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			clientList = new ArrayList<ClientInfo>();
		}

		model.addAttribute("clients", clientList);

		/*-- 客资统计 --*/
		model.addAttribute("allToatal", pageInfo.get("totalcount"));// 总客资
		/*-- 获取今日搜集客资数 --*/
		Map<String, Object> reqContentTody = new HashMap<String, Object>();
		reqContentTody.put("companyid", staff.getCompanyId());
		reqContentTody.put("createtimestart", TimeUtils.getSysdateTimeStart());
		reqContentTody.put("createtimeend", TimeUtils.getSysdateTimeEnd());
		reqContentTody.put("pagesize", "1");
		reqContentTody.put("currentpage", "1000000");

		try {
			String clientTodyRstStr = crmBaseApi.doService(reqContentTody, "clientInfoQueryMini");
			JSONObject jsTody = JsonFmtUtil.strToJsonObj(clientTodyRstStr);
			if (jsTody != null) {
				model.addAttribute("todayTotal", jsTody.getJSONObject("pageInfo").get("totalcount"));// 今日搜集客资
			}
		} catch (Exception e) {
			model.addAttribute("todayTotal", "0");// 今日搜集客资
		}

		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 分页信息 --*/
		model.addAttribute("total", pageInfo.get("totalcount"));// 总记录数
		model.addAttribute("totalPage", pageInfo.get("totalpage"));// 总页数
		model.addAttribute("size", pageInfo.get("pagesize"));// 分页大小
		model.addAttribute("page", pageInfo.get("currentpage"));// 当前页码

		/*-- 筛选信息 --*/
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("startStr", createTimeStart);// 录入起始时间
		model.addAttribute("endStr", createTimeEnd);// 录入截止时间
		model.addAttribute("sortname1Str", sortname1);
		model.addAttribute("sorttype1Str", sorttype1);
		model.addAttribute("sortStr", sort.equals("false") ? "true" : "false");
		return "client/list";
	}

	/**
	 * 2：获取客资：无效客资--无效客资列表
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 */
	@RequestMapping(value = "/invalid", method = RequestMethod.GET)
	public String invalidClientInfosMini(@RequestParam Map<String, String> maps, Model model,
			HttpServletRequest request, HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStrDate("30") : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);

		/*-- 客资状态下拉菜单 --*/
		Status status = new Status();
		status.setIsShow(true);
		status.setCompanyId(staff.getCompanyId());
		List<Status> statusList;
		try {
			statusList = statusService.getStatusInfoAllList(1, "/");
		} catch (EduException e1) {
			statusList = new ArrayList<Status>();
		}
		model.addAttribute("statuses", statusList);

		/*-- 支付宝口碑渠道 --*/
		Source srcAliPay = sourceService.getSrcByName(Source.SRC_ALIPAY, staff.getCompanyId());

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		Map<String, Object> reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(createTimeStart)) {
			reqContent.put("createtimestart", createTimeStart + " 00:00:00");
		}
		if (StringUtil.isNotEmpty(createTimeEnd)) {
			reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
		}
		reqContent.put("sourceids", srcAliPay.getSrcId());
		reqContent.put("statusids", ClientInfoConstant.BE_INVALID);
		reqContent.put("pagesize", pageSize);
		reqContent.put("currentpage", currentPage);

		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("companyName", company.getWebSite());
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQueryMini");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
			model.addAttribute("company", company);
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			clientList = new ArrayList<ClientInfo>();
		}

		model.addAttribute("clients", clientList);
		model.addAttribute("statusId", ClientInfoConstant.BE_INVALID);

		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 分页信息 --*/
		model.addAttribute("total", pageInfo.get("totalcount"));// 总记录数
		model.addAttribute("totalPage", pageInfo.get("totalpage"));// 总页数
		model.addAttribute("size", pageInfo.get("pagesize"));// 分页大小
		model.addAttribute("page", pageInfo.get("currentpage"));// 当前页码

		/*-- 筛选信息 --*/
		model.addAttribute("startStr", createTimeStart);// 录入起始时间
		model.addAttribute("endStr", createTimeEnd);// 录入截止时间
		return "client/invalid";
	}

	/**
	 * 3：新增客资
	 * 
	 * *** name (必传) 客资姓名 *** *** source_id (必传) 渠道ID *** *** phone (必传) 电话号码
	 * *** *** wechat 微信号 *** *** qq QQ *** *** address 地址 *** *** remark 备注 ***
	 * *** yptime 约排时间
	 * 
	 * ***
	 */
	@RequestMapping(value = "add_new_client_mini", method = RequestMethod.POST)
	@ResponseBody
	public Model createNewClientInfoMini(@RequestParam Map<String, String> maps, Model model,
			HttpServletRequest request, HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		String kzName = maps.get("name");
		String kxPhone = maps.get("phone");
		String kzWeChat = maps.get("wechat");
		String kzQq = maps.get("qq");
		String kxSex = maps.get("sex");
		String address = maps.get("address");
		String memo = maps.get("remark");
		String ypTime = maps.get("yptime");
		String mateName = maps.get("matename");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzName)) {
			model.addAttribute("code", 1002);
			model.addAttribute("msg", "请输入姓名");
			return model;
		}
		if (!UtilRegex.validateMobile(kxPhone)) {
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

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 支付宝口碑渠道 --*/
		Source srcAliPay = sourceService.getSrcByName(Source.SRC_ALIPAY, staff.getCompanyId());

		Map<String, Object> reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("sourceid", srcAliPay.getSrcId());

		reqContent.put("shopid", staff.getShopId());
		reqContent.put("name", kzName);
		reqContent.put("phone", kxPhone);
		reqContent.put("qq", kzQq);
		reqContent.put("wechat", kzWeChat);
		reqContent.put("address", address);
		reqContent.put("sex", kxSex);
		reqContent.put("yptime", ypTime);
		reqContent.put("merchantpid", staff.getMerchantPid());
		reqContent.put("memo", memo);
		reqContent.put("collectorid", staff.getId());
		reqContent.put("ip", WebUtils.getIP(request));
		if (StringUtil.isNotEmpty(mateName)) {
			reqContent.put("matename", mateName);
		}

		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoAdd");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/**
	 * 4.删除客资
	 * 
	 * *** client_id (必传) 客资ID ***
	 * 
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public Model deleteClientInfoMini(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzId = maps.get("client_id");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(kzId)) {
			model.addAttribute("code", 9999);
			model.addAttribute("msg", "客资ID不能为空");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		Map<String, Object> reqContent = new HashMap<String, Object>();
		reqContent.put("ids", kzId);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));
		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoDelete");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/**
	 * 5.编辑客资
	 */
	@RequestMapping(value = "update_client_info_mini", method = RequestMethod.POST)
	@ResponseBody
	public Model updateClientInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzId = maps.get("id");
		String kzName = maps.get("name");
		String kzPhone = maps.get("phone");
		String kzQq = maps.get("qq");
		String address = maps.get("address");
		String kzWeChat = maps.get("wechat");
		String statusId = maps.get("status_id");

		/*--  参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", 1004);
			model.addAttribute("msg", "请传入id值");
			return model;
		}
		if (StringUtil.isEmpty(kzName)) {
			model.addAttribute("code", 1002);
			model.addAttribute("msg", "请输入姓名");
			return model;
		}
		if (!UtilRegex.validateMobile(kzPhone)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的手机号");
			return model;
		}
		if (StringUtil.isNotEmpty(kzQq) && !UtilRegex.validateQq(kzQq)) {
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

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		Map<String, Object> reqContent = new HashMap<String, Object>();
		reqContent.put("id", kzId);
		reqContent.put("name", kzName);
		reqContent.put("phone", kzPhone);
		reqContent.put("statusid", statusId);
		reqContent.put("qq", kzQq);
		reqContent.put("wechat", kzWeChat);
		reqContent.put("address", address);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));
		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoEditById");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/**
	 * 6.导出客资列表Excel
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** status_id 客资状态ID ***
	 * 
	 * *** is_valid 是否是无效客资 ***
	 * 
	 */
	@RequestMapping(value = "/export_clientinfo_list", method = RequestMethod.GET)
	@ResponseBody
	public void exportWithdrawList(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStrDate("30") : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		Integer statusId = StringUtil.nullToIntZero(maps.get("status_id"));
		String sortname1 = StringUtil.isEmpty(maps.get("sortname1")) ? "" : maps.get("sortname1");
		String sorttype1 = StringUtil.isEmpty(maps.get("sorttype1")) ? "" : maps.get("sorttype1");

		Boolean isValid = Boolean.valueOf(maps.get("is_valid"));

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		try {
			int companyId = staff.getCompanyId();
			String[] titles = new String[] { "客户姓名", "电话", "qq", "微信", "预拍时间", "地区", "追踪备注", "渠道", "创建时间", "最后跟进时间",
					"状态" };
			int[] length = new int[] { 20, 30, 25, 20, 20, 30, 30, 15, 20, 20, 15 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode("客资信息.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();

			List<ClientInfo> clientList = null;
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", companyId);
			if (StringUtil.isNotEmpty(createTimeStart)) {
				reqContent.put("createtimestart", createTimeStart + " 00:00:00");
			}
			if (StringUtil.isNotEmpty(createTimeEnd)) {
				reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
			}
			if (isValid) {
				reqContent.put("statusids", ClientInfoConstant.BE_INVALID);
			} else if (statusId != null && statusId != 0) {
				reqContent.put("statusids", statusId);
			}
			if (StringUtil.isNotEmpty("sortname1")) {
				reqContent.put("sortname1", sortname1);
			}
			if (StringUtil.isNotEmpty("sorttype1")) {
				reqContent.put("sorttype1", sorttype1);
			}
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);

			try {
				String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQueryMini");
				JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
				if (js != null) {
					clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
				}
			} catch (Exception e) {
			}
			if (clientList.isEmpty()) {
				return;
			}
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				Object[] objects = new Object[titles.length];
				objects[0] = client.getKzName();
				objects[1] = client.getKzPhone();
				objects[2] = client.getKzQq();
				objects[3] = client.getKzWechat();
				objects[4] = client.getYpTime();
				objects[5] = client.getAddress();
				objects[6] = client.getMemo();
				objects[7] = client.getSource();
				objects[8] = client.getCreateTime();
				objects[9] = client.getUpdateTime();
				objects[10] = client.getStatus();
				datas.add(objects);
			}
			eu.export(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}