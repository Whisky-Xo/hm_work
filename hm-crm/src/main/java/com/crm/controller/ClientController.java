package com.crm.controller;

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
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ApproveLog;
import com.crm.model.ClientInfo;
import com.crm.model.ClientLogInfo;
import com.crm.model.ClientYaoYueLog;
import com.crm.model.Company;
import com.crm.model.ShopMeetLog;
import com.crm.model.Staff;
import com.crm.service.ApproveLogService;
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
 * 客资公共操作
 * 
 * @author JingChenglong 2016-12-23 20:05
 *
 */
@Controller
@RequestMapping("/client")
public class ClientController {

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

	private static final Company QIEIN = new Company();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/** -- 1.客资审核：通过/驳回 -- **/
	@RequestMapping(value = "do_kz_invalid_check", method = RequestMethod.POST)
	@ResponseBody
	public Model doKzInvalidCheck(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		String kzIds = maps.get("kzids");
		String code = maps.get("code");
		String memo = maps.get("memo");
		String invalidLabel = maps.get("invalid_label");

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 接口调用 ，业务执行 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("kzids", kzIds);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));
		if ("1".equals(code)) {
			reqContent.put("statusid", ClientInfoConstant.INVALID_BE_STAY);
			invalidLabel += " , ";
			invalidLabel += memo;
			reqContent.put("memo", invalidLabel);
		} else {
			reqContent.put("statusid", ClientInfoConstant.BE_INVALID_REJECT);
			reqContent.put("memo", memo);
		}

		/*-- 接口调用，业务执行 --*/
		try {
			String bindRstStr = crmBaseApi.doService(reqContent, "doClientEditStatus");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(bindRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/** -- 2.客资审批：确定无效/驳回 -- **/
	@RequestMapping(value = "do_kz_be_invalid", method = RequestMethod.POST)
	@ResponseBody
	public Model doKzBeInvalid(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		String kzIds = maps.get("kzids");
		String code = maps.get("code");
		String memo = maps.get("memo");
		String invalidCode = maps.get("invalid_code");
		String invalidLabel = maps.get("invalid_label");

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 接口调用 ，业务执行 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("kzids", kzIds);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));
		if ("1".equals(code)) {
			reqContent.put("statusid", ClientInfoConstant.BE_INVALID);
			invalidLabel += " , ";
			invalidLabel += memo;
			reqContent.put("memo", invalidLabel);
		} else {
			reqContent.put("statusid", ClientInfoConstant.BE_INVALID_REJECT);
			reqContent.put("memo", memo);
		}

		/*-- 接口调用，业务执行 --*/
		try {
			String bindRstStr = crmBaseApi.doService(reqContent, "doClientEditStatus");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(bindRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));

			// 消息通知
			String[] ids = kzIds.split(",");
			ClientInfo client = new ClientInfo();
			client.setCompanyId(staff.getCompanyId());
			Staff sf = new Staff();
			sf.setCompanyId(staff.getCompanyId());
			for (String id : ids) {
				client.setKzId(id);
				try {
					client = clientInfoService.getClientInfo(client);
					if (client != null) {
						if ("1".equals(code)) {
							// 如果客资被判定无效，则通知采集员，推送客资无效信息
							sf.setId(client.getCollectorId());
							sf = staffService.getStaffInfoById(sf.getId());

							PushUtil.pushCjValid(sf, client, invalidLabel);

							ApproveLog log = new ApproveLog();
							log.setKzId(id);
							log.setCreateIp(WebUtils.getIP(request));
							log.setCompanyId(staff.getCompanyId());
							log.setOperaId(staff.getId());
							log.setApproveType(ApproveLog.INVALID_KZ);
							log.setCode(Integer.valueOf(invalidCode));

							approveLogService.createApproveLog(log);
						} else {
							// 如果客资无效被驳回，则通知邀约员，推送无效驳回信息
							sf.setId(client.getAppointId());
							sf = staffService.getStaffInfoById(sf.getId());

							PushUtil.pushYyValidReject(sf, client, memo);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/** -- 3.保存采集调配操作 -- **/
	@RequestMapping(value = "sava_kz_cj_mix", method = RequestMethod.POST)
	@ResponseBody
	public Model savaKzCjMix(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzIds = maps.get("kzids");
		String collectorId = maps.get("collectorid");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzIds) || StringUtil.isEmpty(collectorId)) {
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
		reqContent.put("collectorid", collectorId);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));

		/*-- 接口调用，业务执行 --*/
		try {
			String bindRstStr = crmBaseApi.doService(reqContent, "doClientCollectorMix");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(bindRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/** --4.保存邀约调配操作 -- **/
	@RequestMapping(value = "sava_kz_yy_mix", method = RequestMethod.POST)
	@ResponseBody
	public Model savaKzYyMix(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzIds = maps.get("kzids");
		String appointId = maps.get("appointid");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzIds) || StringUtil.isEmpty(appointId)) {
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
		reqContent.put("appointid", appointId);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));

		/*-- 接口调用，业务执行 --*/
		try {
			String bindRstStr = crmBaseApi.doService(reqContent, "doClientAppointMix");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(bindRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/** --5.保存推广调配操作 -- **/
	@RequestMapping(value = "sava_kz_tg_mix", method = RequestMethod.POST)
	@ResponseBody
	public Model savaKzTgMix(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzIds = maps.get("kzids");
		String promoterId = maps.get("promoterid");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzIds) || StringUtil.isEmpty(promoterId)) {
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
		reqContent.put("promoterid", promoterId);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));

		/*-- 接口调用，业务执行 --*/
		try {
			String bindRstStr = crmBaseApi.doService(reqContent, "doClientPromoterMix");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(bindRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/*
	 * 新增客资邀约记录
	 * 
	 * *** kzId (必传) 客资ID *** *** statusCode (必传) 邀约结果 *** *** memo 邀约备注 *** ***
	 * picId1 附件1 *** *** picId2 附件2 *** *** picId3 附件3 *** *** remindTime 提醒时间
	 * *** *** arrivelTime 到店时间 *** *** shopId 到店门店ID *** *** receiverId 门店接待ID
	 * ***
	 */
	@RequestMapping(value = "addCallInvitation")
	@ResponseBody
	public Model addCallInvitation(@RequestParam(value = "kzId") String kzId,
			@RequestParam(value = "statusCode") Integer statusCode,
			@RequestParam(value = "memo", required = false) String memo,
			@RequestParam(value = "picId1", required = false) String picId1,
			@RequestParam(value = "picId2", required = false) String picId2,
			@RequestParam(value = "picId3", required = false) String picId3,
			@RequestParam(value = "picId4", required = false) String picId4,
			@RequestParam(value = "picId5", required = false) String picId5,
			@RequestParam(value = "picId6", required = false) String picId6,
			@RequestParam(value = "picId7", required = false) String picId7,
			@RequestParam(value = "picId8", required = false) String picId8,
			@RequestParam(value = "picId9", required = false) String picId9,
			@RequestParam(value = "picId10", required = false) String picId10,
			@RequestParam(value = "remindTime", required = false) String remindTime,
			@RequestParam(value = "arriveTime", required = false) String arriveTime,
			@RequestParam(value = "shopId", required = false) String shopId,
			@RequestParam(value = "address", required = false) String address,
			@RequestParam(value = "marry_time", required = false) String marryTime,
			@RequestParam(value = "tracetime", required = false) String traceTime,
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
			if (StringUtil.isNotEmpty(picId4)) {
				reqContent.put("file4", picId4);
			}
			if (StringUtil.isNotEmpty(picId5)) {
				reqContent.put("file5", picId5);
			}
			if (StringUtil.isNotEmpty(picId6)) {
				reqContent.put("file6", picId6);
			}
			if (StringUtil.isNotEmpty(picId7)) {
				reqContent.put("file7", picId7);
			}
			if (StringUtil.isNotEmpty(picId8)) {
				reqContent.put("file8", picId8);
			}
			if (StringUtil.isNotEmpty(picId9)) {
				reqContent.put("file9", picId9);
			}
			if (StringUtil.isNotEmpty(picId10)) {
				reqContent.put("file10", picId10);
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

	/**
	 * 6:客资 接单
	 * 
	 * *** kzid (必传) 客资ID ***
	 * 
	 * *** 职工接单：修改客资邀约员ID（APPOINTID）为职工的ID ***
	 * 
	 * *** 暂时不提供拒单情况 ***
	 */
	@RequestMapping(value = "do_kz_make_order", method = RequestMethod.POST)
	@ResponseBody
	public Model doKzMakeOrder(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 检验是否已接单 --*/
		try {
			String[] ids = kzId.split(",");
			ClientInfo client = new ClientInfo();
			client.setCompanyId(staff.getCompanyId());
			for (String id : ids) {
				client.setKzId(id);
				client = clientInfoService.getClientInfo(client);
				if (client == null) {
					model.addAttribute("code", "999999");
					model.addAttribute("msg", "客资不存在");
					return model;
				}
				if (client.getAppointId() != null && client.getAppointId() != 0) {
					model.addAttribute("code", "999999");
					if (!client.getAppointId().toString().equals(staff.getId().toString())) {
						model.addAttribute("msg", "该客资已被其他邀约员接单");
					} else {
						model.addAttribute("msg", "客资已接单，无需重复接单");
					}
					return model;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		/*-- 接口调用 ，业务执行 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("ids", kzId);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));
		reqContent.put("code", "1");// "1"为接单标识，否则为拒单（拒单暂不做,接口端对拒单暂不提供任何操作）

		/*-- 接口调用，业务执行 --*/
		try {
			String bindRstStr = crmBaseApi.doService(reqContent, "doClientBindYyStaff");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(bindRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/**
	 * 7：删除客资
	 */
	@RequestMapping(value = "do_remove_kz", method = RequestMethod.POST)
	@ResponseBody
	public Model doRemoveInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		String kzId = maps.get("kzid");
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请选择客资");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		reqContent = new HashMap<String, Object>();
		reqContent.put("ids", kzId);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("ip", WebUtils.getIP(request));

		/*-- 接口调用，业务执行 --*/
		try {
			String rstStr = crmBaseApi.doService(reqContent, "clientInfoDelete");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(rstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/**
	 * 7：根据客资ID获取客资详细信息
	 * 
	 * *** id (必传) 客资ID ***
	 * 
	 */
	@RequestMapping(value = "get_ke_detail_by_id", method = RequestMethod.GET)
	@ResponseBody
	public Model getKeDetailById(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzId = maps.get("id");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 接口调用，业务执行 --*/
		try {
			ClientInfo clientInfo = null;
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("id", kzId);
			String getRstStr = crmBaseApi.doService(reqContent, "clientInfoGetById");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(getRstStr);

			if ("100000".equals(jsInfo.getString("code"))) {
				JSONObject jsContent = JsonFmtUtil.strContentToJsonObj(getRstStr);
				clientInfo = JsonFmtUtil.jsonToClientInfo(jsContent.getJSONObject("clientInfo"));
				List<ClientLogInfo> logList = JsonFmtUtil.jsonArrToClientLogInfo(jsContent.getJSONArray("logList"));
				clientInfo.setLogList(logList);
				List<ClientYaoYueLog> yyLogList = JsonFmtUtil
						.jsonArrToClientYaoYueLogInfo(jsContent.getJSONArray("yyLogList"));
				clientInfo.setYyLogList(yyLogList);
				List<ShopMeetLog> qtLogList = JsonFmtUtil
						.jsonArrToClientShopMeetLogInfo(jsContent.getJSONArray("qtLogList"));
				clientInfo.setQtLogList(qtLogList);

				model.addAttribute("clientInfo", clientInfo);
			}
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/*
	 * 保存门店自然入客客资洽谈记录
	 */
	@RequestMapping(value = "add_come_shop_kz")
	@ResponseBody
	public Model addComeShopKz(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		/*-- 参数提取 --*/
		String kzName = maps.get("name");// 姓名
		String kzPhone = maps.get("phone");// 电话
		String kzWeChat = maps.get("wechat");// 微信
		String kzSex = maps.get("sex");// 性别
		String kzQq = maps.get("qq");// QQ
		String address = maps.get("address");// 地址
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

		/*-- 职员信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

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
			reqContent.put("sex", kzSex);
			reqContent.put("phone", kzPhone);
			reqContent.put("qq", kzQq);
			reqContent.put("wechat", kzWeChat);
			reqContent.put("address", address);
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
				if (StringUtil.isNotEmpty(kzQq)) {
					client.setKzQq(kzQq);
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

	/*
	 * 新增客资洽谈记录
	 */
	@RequestMapping(value = "addShopMeetInvitation")
	@ResponseBody
	public Model addShopMeetInvitation(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

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
		if (!ClientInfoConstant.COME_SHOP_FAIL.equals(qtRstCode)
				&& !TimeUtils.validTime(arriveTime, "yyyy-MM-dd HH:mm")) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请填写客户到店时间");
			return model;
		}

		/*-- 职员信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		if (staff == null || staff.getShopId() == null || staff.getShopId() == 0) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "没有获取到cookie中的用户信息");
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
}