package com.crm.controller.crm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.crm.common.util.StringUtil;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.Company;
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
 * CRM3.0北斗新星：修改客资信息接口
 * 
 * @author JingChenglong 2017-01-17 12:48
 *
 */
@Controller
@RequestMapping("/client")
public class ClientInfoEditAPI {

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

	/** -- 修改客资基础信息 -- **/
	@RequestMapping(value = "edit_base")
	@ResponseBody
	public Model edidInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
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

		// 推广信息
		String sourceId = StringUtil.nullToStrTrim(maps.get("source_id"));
		String zxStyle = StringUtil.nullToStrTrim(maps.get("zx_style"));
		String collectorId = StringUtil.nullToStrTrim(maps.get("collector_id"));

		// 广告信息
		String adAddress = StringUtil.nullToStrTrim(maps.get("ad_address"));
		String adId = StringUtil.nullToStrTrim(maps.get("ad_id"));
		String remark = StringUtil.nullToStrTrim(maps.get("remark"));

		// 身份信息
		Integer operaId = staff.getId();

		reqContent = new HashMap<String, Object>();
		reqContent.put("id", kzId);
		reqContent.put("companyid", staff.getCompanyId());
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

		reqContent.put("operaid", operaId);
		reqContent.put("remark", remark);
		reqContent.put("ip", WebUtils.getIP(request));

		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientEditBaseDs");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/** -- 修改客资详细信息 -- **/
	@RequestMapping(value = "edit_detail")
	@ResponseBody
	public Model edidDetail(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		String kzId = maps.get("kzid");
		String typeId = maps.get("typeid");
		if (StringUtil.isEmpty(kzId) || StringUtil.isEmpty(typeId)) {
			// TODO 客资ID获取错误
		}

		// 基础信息
		String name = StringUtil.nullToStrTrim(maps.get("name"));
		String sex = StringUtil.nullToStrTrim(maps.get("sex"));
		String phone = StringUtil.nullToStrTrim(maps.get("phone"));
		String wechat = StringUtil.nullToStrTrim(maps.get("wechat"));
		String qq = StringUtil.nullToStrTrim(maps.get("qq"));
		String mateName = StringUtil.nullToStrTrim(maps.get("matename"));
		String matePhone = StringUtil.nullToStrTrim(maps.get("matephone"));
		String mateWechat = StringUtil.nullToStrTrim(maps.get("matewechat"));
		String mateQq = StringUtil.nullToStrTrim(maps.get("mateqq"));
		String address = StringUtil.nullToStrTrim(maps.get("address"));

		String birthTime = StringUtil.nullToStrTrim(maps.get("birthtime"));
		String age = StringUtil.nullToStrTrim(maps.get("age"));
		String idNum = StringUtil.nullToStrTrim(maps.get("idnum"));
		String job = StringUtil.nullToStrTrim(maps.get("job"));
		String edu = StringUtil.nullToStrTrim(maps.get("edu"));
		String earn = StringUtil.nullToStrTrim(maps.get("earn"));
		String carhouse = StringUtil.nullToStrTrim(maps.get("carhouse"));
		String marryMemo = StringUtil.nullToStrTrim(maps.get("marrymemo"));
		String ypMemo = StringUtil.nullToStrTrim(maps.get("ypmemo"));
		String marryTime = StringUtil.nullToStrTrim(maps.get("marrytime"));
		String ypTime = StringUtil.nullToStrTrim(maps.get("yptime"));

		String remark = StringUtil.nullToStrTrim(maps.get("remark"));

		// 身份信息
		Integer operaId = staff.getId();

		reqContent = new HashMap<String, Object>();
		reqContent.put("id", kzId);
		reqContent.put("typeid", typeId);
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("name", name);
		reqContent.put("sex", sex);
		reqContent.put("phone", phone);
		reqContent.put("qq", qq);
		reqContent.put("wechat", wechat);
		reqContent.put("address", address);
		reqContent.put("matename", mateName);
		reqContent.put("matephone", matePhone);
		reqContent.put("matewechat", mateWechat);
		reqContent.put("mateqq", mateQq);

		reqContent.put("birthtime", birthTime);
		reqContent.put("age", age);
		reqContent.put("idnum", idNum);
		reqContent.put("job", job);
		reqContent.put("edu", edu);
		reqContent.put("earn", earn);
		reqContent.put("carhouse", carhouse);

		reqContent.put("marrymemo", marryMemo);
		reqContent.put("ypmemo", ypMemo);

		reqContent.put("marrytime", marryTime);
		reqContent.put("yptime", ypTime);
		reqContent.put("remark", remark);

		reqContent.put("operaid", operaId);
		reqContent.put("ip", WebUtils.getIP(request));

		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientEditDetail");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/** -- 删除客资 -- **/
	@RequestMapping(value = "remove")
	@ResponseBody
	public Model remove(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		String kzIds = maps.get("kzids");

		if (StringUtils.isEmpty(kzIds)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}
		while (true) {
			if (kzIds.endsWith(",")) {
				kzIds = kzIds.substring(0, kzIds.length() - 1);
			} else {
				break;
			}
		}

		// 身份信息
		Integer operaId = staff.getId();

		reqContent = new HashMap<String, Object>();
		reqContent.put("ids", kzIds);
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("operaid", operaId);
		reqContent.put("ip", WebUtils.getIP(request));

		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientRemove");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/** -- 转移客资 -- **/
	@RequestMapping(value = "mix")
	@ResponseBody
	public Model mix(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		String kzIds = maps.get("kzids");
		String type = maps.get("type");
		String staffId = maps.get("staffid");

		if (StringUtils.isEmpty(kzIds) || StringUtils.isEmpty(type) || StringUtils.isEmpty(staffId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		// 身份信息
		Integer operaId = staff.getId();

		reqContent = new HashMap<String, Object>();
		reqContent.put("ids", kzIds);
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("operaid", operaId);
		reqContent.put("barstaffid", staffId);
		reqContent.put("type", type);
		reqContent.put("ip", WebUtils.getIP(request));

		try {
			String addRstStr = crmBaseApi.doService(reqContent, "clientMix");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
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