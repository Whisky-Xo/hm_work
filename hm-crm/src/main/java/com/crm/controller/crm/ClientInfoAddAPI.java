package com.crm.controller.crm;

import java.util.HashMap;
import java.util.List;
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
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.MobileLocationUtil;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Company;
import com.crm.model.Source;
import com.crm.model.Staff;
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
 * CRM3.0北斗新星：新增客资接口
 * 
 * @author JingChenglong 2017-01-17 12:48
 *
 */
@Controller
@RequestMapping("/client")
public class ClientInfoAddAPI {

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

	private static final Company QIEIN = new Company();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/** -- 录入客资 -- **/
	@RequestMapping(value = "add")
	@ResponseBody
	public Model add(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {
		// -- 客资录入类型
		String addType = StringUtil.nullToStrTrim(maps.get("add_type"));
		if (Source.DS_PREFIX.equals(addType)) {
			// 电商录入客资
			return dsAdd(maps, model, request);
		} else if (Source.ZJS_PREFIX.equals(addType)) {
			// 转介绍录入客资
			return zjsAdd(maps, model, request);
		} else if (Source.MS_PREFIX.equals(addType)) {
			// 门店录入客资
			return msAdd(maps, model, request);
		} else {
			// 其它录入客资
			return qtAdd(maps, model, request);
		}
	}

	/*-- 电商录入客资 --*/
	private Model dsAdd(Map<String, String> maps, Model model, HttpServletRequest request) {

		Staff staff = getStaffDetail(request);

		// 基础信息
		String name = StringUtil.nullToStrTrim(maps.get("name"));
		String sex = StringUtil.nullToStrTrim(maps.get("sex"));
		String phone = StringUtil.nullToStrTrim(maps.get("phone"));
		String wechat = StringUtil.nullToStrTrim(maps.get("wechat"));
		String qq = StringUtil.nullToStrTrim(maps.get("qq"));
		String address = StringUtil.nullToStrTrim(maps.get("address"));

		// 来源信息
		String sourceId = StringUtil.nullToStrTrim(maps.get("source_id"));
		String zxStyle = StringUtil.nullToStrTrim(maps.get("zx_style"));
		String collectorId = StringUtil.nullToStrTrim(maps.get("collector_id"));

		// 广告信息
		String adId = StringUtil.nullToStrTrim(maps.get("ad_id"));
		String adAddress = StringUtil.nullToStrTrim(maps.get("ad_address"));

		// 咨询信息
		String typeId = StringUtil.nullToStrTrim(maps.get("type_id"));
		String yxLavel = StringUtil.nullToStrTrim(maps.get("yx_lavel"));
		String ysRange = StringUtil.nullToStrTrim(maps.get("ys_range"));
		String marryMemo = StringUtil.nullToStrTrim(maps.get("marry_memo"));
		String ypMemo = StringUtil.nullToStrTrim(maps.get("yp_memo"));

		// 备注
		String remark = StringUtil.nullToStrTrim(maps.get("remark"));

		// 身份信息
		Integer operaId = staff.getId();

		// 参数补全-校验
		if (StringUtil.isEmpty(collectorId)) {
			collectorId = String.valueOf(staff.getId());
		}
		if (StringUtil.isEmpty(address)) {
			// 如果地址信息为空，则取客人手机归属地信息作为地址
			if (StringUtil.isNotEmpty(phone)) {
				address = MobileLocationUtil.getCityStr(phone);
			} else if (StringUtil.isNotEmpty(wechat)) {
				address = MobileLocationUtil.getCityStr(wechat);
			} else if (StringUtil.isNotEmpty(qq)) {
				address = MobileLocationUtil.getCityStr(qq);
			}
		}

		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("operaid", operaId);

		reqContent.put("name", name);
		reqContent.put("sex", sex);
		reqContent.put("phone", phone);
		reqContent.put("qq", qq);
		reqContent.put("wechat", wechat);
		reqContent.put("address", address);

		reqContent.put("sourceid", sourceId);
		reqContent.put("zxstyle", zxStyle);
		reqContent.put("collectorid", collectorId);

		reqContent.put("adid", adId);
		reqContent.put("adaddress", adAddress);

		reqContent.put("typeid", typeId);
		reqContent.put("yxlavel", yxLavel);
		reqContent.put("ysrange", ysRange);
		reqContent.put("marrymemo", marryMemo);
		reqContent.put("ypmemo", ypMemo);

		reqContent.put("remark", remark);
		reqContent.put("ip", WebUtils.getIP(request));

		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientAddDs");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			if ("100000".equals(jsInfo.getString("code"))) {
				String kzId = JsonFmtUtil.strContentToJsonObj(addRstStr).getString("kzId");
				synchronized (this) {
					ClientInfo client = new ClientInfo();
					client.setKzId(kzId);
					client.setCompanyId(staff.getCompanyId());
					client = clientInfoService.getClientInfo(client);
					clientInfoService.doClientPush(client);
				}
			} else if ("130004".equals(jsInfo.getString("code")) || "130005".equals(jsInfo.getString("code"))
					|| "130006".equals(jsInfo.getString("code"))) {
				// 重复客资，给邀约员推送提醒
				ClientInfo client = new ClientInfo();
				if (StringUtil.isNotEmpty(phone)) {
					client.setKzPhone(phone);
				}
				if (StringUtil.isNotEmpty(qq)) {
					client.setKzQq(qq);
				}
				if (StringUtil.isNotEmpty(wechat)) {
					client.setKzWechat(wechat);
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
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/*-- 转介绍录入客资 --*/
	private Model zjsAdd(Map<String, String> maps, Model model, HttpServletRequest request) {
		return null;
	}

	/*-- 门店录入客资 --*/
	private Model msAdd(Map<String, String> maps, Model model, HttpServletRequest request) {
		return null;
	}

	/*-- 其它录入客资 --*/
	private Model qtAdd(Map<String, String> maps, Model model, HttpServletRequest request) {
		return null;
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