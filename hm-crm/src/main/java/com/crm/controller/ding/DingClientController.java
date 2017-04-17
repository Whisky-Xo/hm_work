package com.crm.controller.ding;

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
import com.crm.api.constant.Constants;
import com.crm.api.constant.PageConfConst;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.BeidouClientInfoFilter;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Company;
import com.crm.model.PageConf;
import com.crm.model.ShopSellAnalisys;
import com.crm.model.Staff;
import com.crm.service.ApproveLogService;
import com.crm.service.ClientInfoService;
import com.crm.service.CompanyService;
import com.crm.service.DeptService;
import com.crm.service.DictionaryService;
import com.crm.service.GroupService;
import com.crm.service.InvitationLogService;
import com.crm.service.PageConfService;
import com.crm.service.PermissionService;
import com.crm.service.ShopService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

/**
 * 钉钉端客资信息查询
 * 
 * @author JingChenglong 2017-01-11 20:09
 *
 */
@Controller
@RequestMapping("/ding")
public class DingClientController {

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
	@Autowired
	ApproveLogService approveLogService; /* 审批日志 */
	@Autowired
	PageConfService pageConfService;/* 企业页面配置 */

	private static final Company QIEIN = new Company();
	private static final List<ClientInfo> NO_CLIENT = new ArrayList<ClientInfo>();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/** -- 电商邀约获取客资信息 -- **/
	@RequestMapping(value = "get_client_datas_dsyy", method = RequestMethod.POST)
	@ResponseBody
	public Model getClientDatasDsyy(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "1000" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String searchKey = maps.get("searchkey");
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));

		String appointIds = getAppointIdLimit(staff);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("sortname1", "sts.PRIORITY");
		reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
		reqContent.put("sortname2", "info.UPDATETIME");
		reqContent.put("sorttype2", Constants.ORDERSORT_DESC);
		try {
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("appointids", appointIds);
				if (StringUtil.isValid(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isValid(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isValid(statusId)) {
					reqContent.put("statusids", statusId);
				}
				if (StringUtil.isValid(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
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
			pageInfo.put("pagesize", 1000);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.MOBOLE_DS_YY);
		String content = BeidouClientInfoFilter.doFilterDing(clientList, pageConf.getContent());

		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		return model;
	}

	/** -- 转介绍邀约获取客资信息 -- **/
	@RequestMapping(value = "get_client_datas_zjsyy", method = RequestMethod.POST)
	@ResponseBody
	public Model getClientDatasZjsyy(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "1000" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String searchKey = maps.get("searchkey");
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));

		String appointIds = getAppointIdLimit(staff);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("sortname1", "sts.PRIORITY");
		reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
		reqContent.put("sortname2", "info.UPDATETIME");
		reqContent.put("sorttype2", Constants.ORDERSORT_DESC);
		try {
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("appointids", appointIds);
				if (StringUtil.isValid(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isValid(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isValid(statusId)) {
					reqContent.put("statusids", statusId);
				}
				if (StringUtil.isValid(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
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
			pageInfo.put("pagesize", 1000);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.MOBOLE_ZJS_YY);
		String content = BeidouClientInfoFilter.doFilterDing(clientList, pageConf.getContent());

		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		return model;
	}

	/** -- 门店洽谈获取客资信息 -- **/
	@RequestMapping(value = "get_client_datas_shop", method = RequestMethod.POST)
	@ResponseBody
	public Model getClientDatasShop(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "1000" : maps.get("size"));
		String appointTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String appointTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String searchKey = maps.get("searchkey");

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("sortname1", " info.APPOINTTIME ");
		reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
		try {
			if (StringUtil.isEmpty(searchKey)) {
				if (StringUtil.isValid(appointTimeStart)) {
					reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
				}
				if (StringUtil.isValid(appointTimeEnd)) {
					reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
				}
				reqContent.put("shopids", staff.getShopId());
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
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
			pageInfo.put("pagesize", 1000);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.MOBOLE_SHOP_MEET);
		String content = BeidouClientInfoFilter.doFilterDing(clientList, pageConf.getContent());

		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		return model;
	}

	/*-- 顶顶端：新增客资邀约记录 --*/
	@RequestMapping(value = "add_call_invitation_ding")
	@ResponseBody
	public Model addCallInvitationDing(@RequestParam(value = "kzId") String kzId,
			@RequestParam(value = "statusCode") Integer statusCode,
			@RequestParam(value = "memo", required = false) String memo,
			@RequestParam(value = "remindTime", required = false) String remindTime,
			@RequestParam(value = "arriveTime", required = false) String arriveTime,
			@RequestParam(value = "shopId", required = false) String shopId,
			@RequestParam(value = "address", required = false) String address,
			@RequestParam(value = "marry_time", required = false) String marryTime,
			@RequestParam(value = "tracetime", required = false) String traceTime,
			@RequestParam(value = "amount", required = false) String amount,
			@RequestParam(value = "cj_time", required = false) String cjTime,
			@RequestParam(value = "receiverId", required = false) String receiverId, Model model,
			HttpServletRequest request) throws Exception {

		/*-- 职员信息 --*/
		Staff staff = getStaffDetail(request);

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
			if (StringUtil.isNotEmpty(remindTime) && TimeUtils.validTime(remindTime, "yyyy-MM-dd HH:mm")) {
				reqContent.put("warntime", remindTime);
			}
			if (StringUtil.isNotEmpty(arriveTime) && TimeUtils.validTime(arriveTime, "yyyy-MM-dd HH:mm")) {
				reqContent.put("cometime", arriveTime);
			}
			if (StringUtil.isNotEmpty(traceTime) && TimeUtils.validTime(traceTime, "yyyy-MM-dd HH:mm")) {
				reqContent.put("tracetime", traceTime);
			}
			if (StringUtil.isNotEmpty(shopId) && !"0".equals(shopId)) {
				reqContent.put("shopid", shopId);
			}
			if (StringUtil.isNotEmpty(receiverId) && !"0".equals(receiverId)) {
				reqContent.put("receptorid", receiverId);
			}
			if (StringUtil.isNotEmpty(address)) {
				reqContent.put("address", address);
			}
			if (StringUtil.isNotEmpty(marryTime) && TimeUtils.validTime(marryTime, "yyyy-MM-dd HH:mm")) {
				reqContent.put("marrytime", marryTime);
			}
			if (StringUtil.isNotEmpty(amount)) {
				reqContent.put("amount", amount);
			}
			if (StringUtil.isNotEmpty(cjTime)) {
				reqContent.put("successtime", cjTime);
			}
			reqContent.put("ip", WebUtils.getIP(request));
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

	/*-- 钉钉端：保存门店自然入客客资信息 --*/
	@RequestMapping(value = "add_come_shop_kz_ding")
	@ResponseBody
	public Model addComeShopKzDing(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		String kzName = maps.get("name");// 姓名
		String kzPhone = maps.get("phone");// 电话
		String kzWeChat = maps.get("wechat");// 微信
		String mateName = maps.get("matename");// 配偶姓名
		String matePhone = maps.get("matephone");// 配偶电话
		String amount = maps.get("amount");// 成交金额
		String comeShopTime = maps.get("arrivetime");// 到店时间
		String memo = maps.get("memo");// 接单记录
		String recepterId = maps.get("recepterid");// 接待门市ID
		String rstCode = maps.get("rstcode");// 接待结果
		String runOffCode = maps.get("runoffcode");// 流失原因编码

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzName) || StringUtil.isEmpty(kzPhone) || StringUtil.isEmpty(rstCode)
				|| StringUtil.isEmpty(recepterId) || StringUtil.isEmpty(comeShopTime) || StringUtil.isEmpty(rstCode)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		if (staff == null || staff.getShopId() == null || staff.getShopId() == 0) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "没有获取到cookie中的用户信息");
			return model;
		}

		Integer sourceId = sourceService.getSrcByName("门店自然入客", staff.getCompanyId()).getSrcId();// 自然入客渠道ID

		if (sourceId == null || sourceId == 0) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "未分配自然入客渠道");
			return model;
		}

		/*-- 接口调用，业务执行 --*/
		try {
			reqContent = new HashMap<String, Object>();
			reqContent.put("sourceid", sourceId);
			reqContent.put("statusid", rstCode);
			reqContent.put("name", kzName);
			reqContent.put("phone", kzPhone);
			reqContent.put("wechat", kzWeChat);
			reqContent.put("matename", mateName);
			reqContent.put("matephone", matePhone);
			reqContent.put("memo", memo);
			reqContent.put("amount", StringUtil.isNotEmpty(amount) ? amount.trim() : "0");
			if (ClientInfoConstant.BE_RUN_OFF.equals(rstCode)) {
				// 洽谈结果为流失，提交流失原因编码
				reqContent.put("runoffcode", runOffCode);
			}
			reqContent.put("arrivetime", comeShopTime);
			reqContent.put("receptorid", recepterId);
			reqContent.put("collectorid", staff.getId());
			reqContent.put("shopid", staff.getShopId());
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("ip", WebUtils.getIP(request));

			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoAdd");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			// 客资新增成功，将客资推送给指定邀约员
			if ("100000".equals(jsInfo.getString("code"))) {
				// 添加成功
			} else if ("130004".equals(jsInfo.getString("code")) || "130005".equals(jsInfo.getString("code"))
					|| "130006".equals(jsInfo.getString("code"))) {
				// 重复客资，给邀约员推送提醒
				ClientInfo client = new ClientInfo();
				if (StringUtil.isNotEmpty(kzPhone)) {
					client.setKzPhone(kzPhone);
				}
				if (StringUtil.isNotEmpty(kzWeChat)) {
					client.setKzWechat(kzWeChat);
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
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/*-- 钉钉端--新增客资洽谈记录 --*/
	@RequestMapping(value = "addShopMeetInvitationDing")
	@ResponseBody
	public Model addShopMeetInvitationDing(@RequestParam Map<String, String> maps, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		Staff staff = getStaffDetail(request);

		if (staff == null || staff.getShopId() == null || staff.getShopId() == 0) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "没有获取到cookie中的用户信息");
			return model;
		}

		/*-- 参数提取 --*/
		String kzId = maps.get("kzId");// 客资ID
		String qtRstCode = maps.get("qtRstCode");// 洽谈结果
		String memo = maps.get("memo");// 备注
		String recepterId = maps.get("recepterId");// 接待门市ID
		String amount = maps.get("amount");// 成交金额
		String runOffCode = maps.get("runOffCode");// 流失原因编码
		String runOffLabel = maps.get("runOffLabel");// 流失原因
		String arriveTime = maps.get("arriveTime");// 到店时间

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId) || StringUtil.isEmpty(qtRstCode) || StringUtil.isEmpty(recepterId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}
		if (!TimeUtils.validTime(arriveTime, "yyyy-MM-dd HH:mm")) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请填写客户到店时间");
			return model;
		}

		/*-- 接口调用，业务执行 --*/
		try {
			reqContent = new HashMap<String, Object>();
			reqContent.put("kzid", kzId);
			reqContent.put("rstcode", qtRstCode);
			if (ClientInfoConstant.BE_RUN_OFF.equals(qtRstCode)) {
				// 洽谈结果为流失，提交流失原因编码
				reqContent.put("runoffcode", runOffCode);
			}
			reqContent.put("recepterid", recepterId);
			reqContent.put("operaid", staff.getId());
			reqContent.put("shopid", staff.getShopId());
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("ip", WebUtils.getIP(request));
			if (TimeUtils.validTime(arriveTime, "yyyy-MM-dd HH:mm")) {
				reqContent.put("arrivetime", arriveTime.trim());
			}
			if (StringUtil.isNotEmpty(memo)) {
				reqContent.put("qtemo", memo.trim());
			}
			if (StringUtils.isNotEmpty(amount)) {
				reqContent.put("amount", amount.trim());
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "doAddShopMeetLog");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(clientRstStr);
			if ("100000".equals(js.get("code"))) {
				// 操作成功，消息通知到邀约员和采集员
				ClientInfo client = new ClientInfo();
				client.setKzId(kzId);
				client.setCompanyId(staff.getCompanyId());
				client = clientInfoService.getClientInfo(client);
				// 采集员
				Staff collector = staffService
						.getStaffInfoById(client.getCollectorId() == null ? 0 : client.getCollectorId());
				// 邀约员
				Staff appointor = staffService
						.getStaffInfoById(client.getAppointId() == null ? 0 : client.getAppointId());
				if (ClientInfoConstant.BE_SUCCESS.equals(qtRstCode)) {
					// 成功成交
					if (collector != null) {
						PushUtil.pushSuccessOrder(collector, client);
					}
					if (appointor != null) {
						PushUtil.pushSuccessOrder(appointor, client);
					}
				} else if (ClientInfoConstant.BE_RUN_OFF.equals(qtRstCode)) {
					// 流失
					if (collector != null) {
						PushUtil.pushShopFailMeet(collector, client, runOffLabel);
					}
					if (appointor != null) {
						PushUtil.pushShopFailMeet(appointor, client, runOffLabel);
					}
				}
			}
			model.addAttribute("code", js.get("code"));
			model.addAttribute("msg", js.get("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/*-- 钉钉端：销售分析数据集 --*/
	@RequestMapping(value = "ding_sell_analysis_datas")
	@ResponseBody
	public Model dingSellAnalysisDatas(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		String start = maps.get("start");// 统计开始时间
		String end = maps.get("end");// 统计结束时间
		String shopId = maps.get("shopid");// 门店ID

		/*-- 统计结果集 --*/
		ShopSellAnalisys analysis = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");
		if (StringUtil.isValid(shopId)) {
			reqContent.put("shopid", shopId);
		}

		try {
			String analyzeRstStr = crmBaseApi.doService(reqContent, "dataAnalyseDingSell");
			JSONObject js = JsonFmtUtil.strToJsonObj(analyzeRstStr);
			if (js != null) {
				analysis = JsonFmtUtil.jsonArrToShopSellAnalisys(js.getJSONObject("analysis"));
				String[] amtRinkIds = analysis.getSaleAmountLimit().split(",");
				for (int i = 1; i <= 3; i++) {
					Staff sf = staffService.getStaffInfoById(Integer.valueOf(amtRinkIds[i - 1]));
					model.addAttribute("amt" + i, sf);
				}
				String[] numRinkIds = analysis.getSaleNumLimit().split(",");
				for (int i = 1; i <= 3; i++) {
					Staff sf = staffService.getStaffInfoById(Integer.valueOf(numRinkIds[i - 1]));
					model.addAttribute("num" + i, sf);
				}
			}
		} catch (Exception e) {
			analysis = new ShopSellAnalisys();
		}

		model.addAttribute("analyzes", analysis);

		return model;
	}

	/*-- 钉钉端：销售排名数据集 --*/
	@RequestMapping(value = "ding_sell_ranking_datas")
	@ResponseBody
	public Model dingSellRankingDatas(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		String start = maps.get("start");// 统计开始时间
		String end = maps.get("end");// 统计结束时间
		String shopId = maps.get("shopid");// 门店ID

		/*-- 统计结果集 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");
		if (StringUtil.isValid(shopId)) {
			reqContent.put("shopid", shopId);
		}

		try {
			String analyzeRstStr = crmBaseApi.doService(reqContent, "dataAnalyzeRanking");
			JSONObject js = JsonFmtUtil.strToJsonObj(analyzeRstStr);
			if (js != null) {
				model.addAttribute("amount", js.getJSONArray("amount"));
				model.addAttribute("count", js.getJSONArray("count"));
			}
		} catch (Exception e) {
		}

		return model;
	}

	/*-- 获取邀约员ID集合 --*/
	public String getAppointIdLimit(Staff staff) {

		if (staff == null) {
			return "";
		}

		String appointIds = "";

		if (staff.getIsChief()) {
			// 获取部门下所有员工
			List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(staff.getDeptId(), staff.getCompanyId());
			for (Staff s : sfs) {
				appointIds += s.getId();
				appointIds += Constants.STR_SEPARATOR;
			}
			if (appointIds.length() != 0) {
				appointIds = appointIds.substring(0, appointIds.length() - 1);
			}
		} else {
			// 如果不是主管，只能查询自己的信息
			appointIds = staff.getId() + "";
		}

		return appointIds;
	}

	/*-- 获取登录人详细信息 --*/
	public Staff getStaffDetail(HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return null;
		}
		return staffService.getStaffInfoById(staff.getId());
	}
}