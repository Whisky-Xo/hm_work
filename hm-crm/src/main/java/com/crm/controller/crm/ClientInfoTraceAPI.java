package com.crm.controller.crm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.ClientInfoConstant;
import com.crm.api.constant.NewsConts;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Company;
import com.crm.model.Staff;
import com.crm.model.TimeWarn;
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
import com.crm.service.WarnTimerService;

/**
 * CRM3.0北斗新星：客资追踪接口
 * 
 * @author JingChenglong 2017-02-16 14:26
 *
 */
@Controller
@RequestMapping("/client")
public class ClientInfoTraceAPI {

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
	PageConfService pageConfService;/* 企业页面配置 */
	@Autowired
	WarnTimerService warnTimerService;/* 定时任务管理 */

	private static final Company QIEIN = new Company();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/** -- 保存邀约记录 -- **/
	@RequestMapping(value = "invite")
	@ResponseBody
	public Model invite(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		String kzId = maps.get("kzid");
		if (StringUtil.isEmpty(kzId)) {
			// TODO 客资ID获取错误
		}

		// 基础信息
		String typeId = StringUtil.nullToStrTrim(maps.get("type_id"));
		String yxLavel = StringUtil.nullToStrTrim(maps.get("yx_lavel"));
		String ysRange = StringUtil.nullToStrTrim(maps.get("ys_range"));
		String marryMemo = StringUtil.nullToStrTrim(maps.get("marry_memo"));
		String ypMemo = StringUtil.nullToStrTrim(maps.get("yp_memo"));
		String name = StringUtil.nullToStrTrim(maps.get("name"));
		String sex = StringUtil.nullToStrTrim(maps.get("sex"));
		String phone = StringUtil.nullToStrTrim(maps.get("phone"));
		String wechat = StringUtil.nullToStrTrim(maps.get("wechat"));
		String qq = StringUtil.nullToStrTrim(maps.get("qq"));
		String address = StringUtil.nullToStrTrim(maps.get("address"));

		// 邀约结果
		String yyRst = StringUtil.nullToStrTrim(maps.get("yy_rst"));
		String invalidCode = StringUtil.nullToStrTrim(maps.get("invalid_code"));
		String invalidLabel = StringUtil.nullToStrTrim(maps.get("invalid_label"));
		String invalidMemo = StringUtil.nullToStrTrim(maps.get("invalid_memo"));
		String traceTime = StringUtil.nullToStrTrim(maps.get("trace_time"));
		String shopId = StringUtil.nullToStrTrim(maps.get("shopid"));
		String shopName = StringUtil.nullToStrTrim(maps.get("shopname"));
		String yyTime = StringUtil.nullToStrTrim(maps.get("yy_time"));
		String yyMemo = StringUtil.nullToStrTrim(maps.get("yy_memo"));

		// 身份信息
		Integer operaId = staff.getId();

		reqContent = new HashMap<String, Object>();
		reqContent.put("id", kzId);
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("typeid", typeId);
		reqContent.put("yxlavel", yxLavel);
		reqContent.put("ysrange", ysRange);
		reqContent.put("marrymemo", marryMemo);
		reqContent.put("ypmemo", ypMemo);
		reqContent.put("name", name);
		reqContent.put("sex", sex);
		reqContent.put("phone", phone);
		reqContent.put("qq", qq);
		reqContent.put("wechat", wechat);
		reqContent.put("address", address);
		reqContent.put("yyrst", yyRst);
		reqContent.put("invalidcode", invalidCode);
		reqContent.put("invalidmemo", invalidMemo);
		reqContent.put("invalidlabel", invalidLabel);
		reqContent.put("tracetime", traceTime);
		reqContent.put("shopid", shopId);
		reqContent.put("shopname", shopName);
		reqContent.put("yytime", yyTime);
		reqContent.put("yymemo", yyMemo);
		reqContent.put("operaid", operaId);
		reqContent.put("ip", WebUtils.getIP(request));

		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientInvite");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			if ("100000".equals(jsInfo.getString("code"))) {
				// 保存成功
				ClientInfo client = new ClientInfo();
				client.setKzId(kzId);
				client.setCompanyId(staff.getCompanyId());
				client = clientInfoService.getClientInfo(client);

				Staff collector = new Staff();
				collector.setCompanyId(staff.getCompanyId());
				collector.setId(client.getCollectorId());
				collector = staffService.getStaffInfoById(collector.getId());
				if (ClientInfoConstant.BE_INVALID.equals(yyRst)) {
					// 客资被判定为无效---给采集员推送无效通知消息
					invalidLabel += " ; ";
					invalidLabel += invalidMemo;
					PushUtil.pushCjValid(collector, client, invalidLabel);

				} else if (ClientInfoConstant.BE_TRACK.equals(yyRst)) {
					// 客资被判定为待追踪---添加追踪消息提醒记录
					TimeWarn task = new TimeWarn();
					task.setCompanyId(staff.getCompanyId());
					task.setCreateId(staff.getId());
					task.setType(NewsConts.TYPE_NEW_WARN);
					String msg = "待追踪客资：姓名：";
					msg += client.getKzName();
					msg += " ; 电话: ";
					msg += client.getKzPhone();
					msg += " ; 微信: ";
					msg += client.getKzWechat();
					msg += " ; QQ: ";
					msg += client.getKzQq();
					task.setMsg(msg);
					task.setWarnTime(TimeUtils.getIntTime(TimeUtils.format(traceTime, "yyyy/MM/dd HH:mm")));
					task.setTargetId(staff.getId());
					task.setSpare1(traceTime);
					warnTimerService.addWarnTime(task);
				}
			}
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
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