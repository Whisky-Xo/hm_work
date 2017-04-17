package com.crm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.Constants;
import com.crm.api.constant.DicConts;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.UtilRegex;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Dictionary;
import com.crm.model.Staff;
import com.crm.service.ClientInfoService;
import com.crm.service.CompanyService;
import com.crm.service.DictionaryService;
import com.crm.service.StaffService;

/**
 * 金夫人：外部渠道客资录入页面
 * 
 * @author JingChenglong 2016-10-17 11:09
 * 
 *         *** 企业外部转介绍客资提报 ***
 *
 */
@Controller
@RequestMapping("/outer")
public class OuterSourceController {

	@Autowired
	DictionaryService dictionaryService;/* 数据字典 */

	@Autowired
	ClientInfoService clientInfoService;/*-- 客资 --*/

	@Autowired
	StaffService staffService;/* 职工管理 */

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */

	@Autowired
	CompanyService companyService;/* 公司管理 */

	/**
	 * 1：离职员工 / 子品牌新客资信息提报页面
	 */
	@RequestMapping(value = "/leave_brand_index", method = RequestMethod.GET)
	public String leaveBrandIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 获取渠道配置集合 --*/
		List<Dictionary> srcList = dictionaryService.getDictionaryListByType(Constants.COMP_ID_JINFUREN,
				DicConts.LZ_ZPP_SRC);
		model.addAttribute("srcList", srcList);

		/*-- 金夫人子品牌类型 --*/
		List<Dictionary> zppList = dictionaryService.getDictionaryListByType(Constants.COMP_ID_JINFUREN,
				DicConts.ZPP_JINFUREN);
		model.addAttribute("zppList", zppList);

		return "outer/leave_brand_index";
	}

	/**
	 * 2：金粉俱乐部 / 异业新客资信息提报页面
	 */
	@RequestMapping(value = "/jinfenvip_yy_index", method = RequestMethod.GET)
	public String jinfenvipYyIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 获取渠道配置集合 --*/
		List<Dictionary> srcList = dictionaryService.getDictionaryListByType(Constants.COMP_ID_JINFUREN,
				DicConts.JINFEN_YY_SRC);
		model.addAttribute("srcList", srcList);

		/*-- 金夫人异业类型 --*/
		List<Dictionary> yyList = dictionaryService.getDictionaryListByType(Constants.COMP_ID_JINFUREN,
				DicConts.YY_JINFUREN);
		model.addAttribute("yyList", yyList);

		return "outer/jinfenvip_yy_index";
	}

	/**
	 * 3：网页端：外部渠道--新增客资
	 * 
	 * *** sourceid (必传) 渠道ID *** *** sparesrc 渠道附属信息 *** *** kzname (必传) 客资姓名
	 * *** *** kzphone (必传) 电话/微信 *** *** remark 备注 *** *** yptime 预拍时间 *** ***
	 * myname (必传) 我的姓名 *** *** myphone (必传) 我的电话/微信 *** *** oldkzname 老客户姓名 ***
	 * *** oldkzphone 老客户电话/微信 *** *** vipcardno VIP会员卡号
	 * 
	 * @throws EduException
	 *             ***
	 */
	@RequestMapping(value = "outer_add_client", method = RequestMethod.POST)
	@ResponseBody
	public Model outerAddClient(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		// 防注入校验
		String ip = WebUtils.getIP(request);

		/*-- 参数提取 --*/
		String sourceId = maps.get("sourceid");
		String spareSrc = maps.get("sparesrc");
		String kzName = maps.get("kzname");
		String kzPhone = maps.get("kzphone");
		String ypTime = maps.get("yptime");
		String memo = maps.get("remark");
		String myName = maps.get("myname");
		String myPhone = maps.get("myphone");
		String oldKzName = maps.get("oldkzname");
		String oldKzPhone = maps.get("oldkzphone");
		String vipCardNo = maps.get("vipcardno");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzName)) {
			model.addAttribute("code", 1002);
			model.addAttribute("msg", "请输入新客姓名");
			return model;
		}
		if (StringUtil.isEmpty(kzPhone)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入客户联系方式");
			return model;
		}

		/*-- 录入人信息 --*/
		Staff collector = new Staff();
		collector.setCompanyId(Constants.COMP_ID_JINFUREN);
		collector.setPhone(myPhone);
		collector.setName(myName);
		collector = staffService.getStaffByPhone(collector);

		/*-- 接口调用，业务执行 --*/
		try {
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("ip", ip);
			reqContent.put("companyid", Constants.COMP_ID_JINFUREN);
			reqContent.put("sourceid", sourceId);
			reqContent.put("name", kzName);
			if (!UtilRegex.validateMobile(kzPhone)) {
				reqContent.put("wechat", kzPhone);
			} else {
				reqContent.put("phone", kzPhone);
			}
			if (StringUtil.isNotEmpty(ypTime)) {
				reqContent.put("yptime", ypTime);
			}
			reqContent.put("memo", memo);
			if (collector != null) {
				reqContent.put("collectorid", collector.getId());
			} else {
				if (StringUtil.isNotEmpty(myName)) {
					reqContent.put("collectorname", myName);
				}
				if (StringUtil.isNotEmpty(myPhone)) {
					reqContent.put("collectorphone", myPhone);
				}
			}
			if (StringUtil.isNotEmpty(oldKzName)) {
				reqContent.put("oldkzname", oldKzName);
			}
			if (StringUtil.isNotEmpty(oldKzPhone)) {
				reqContent.put("oldkzphone", oldKzPhone);
			}
			if (StringUtil.isNotEmpty(spareSrc)) {
				reqContent.put("sourcespare", spareSrc);
			}
			if (StringUtil.isNotEmpty(vipCardNo)) {
				reqContent.put("oldkzvipno", vipCardNo);
			}
			reqContent.put("ip", WebUtils.getIP(request));
			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoAdd");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			// 客资新增成功，将客资推送给指定邀约员
			if ("100000".equals(jsInfo.getString("code"))) {
				String kzId = JsonFmtUtil.strContentToJsonObj(addRstStr).getString("kzId");
				synchronized (this) {
					ClientInfo client = new ClientInfo();
					client.setKzId(kzId);
					client.setCompanyId(Constants.COMP_ID_JINFUREN);
					client = clientInfoService.getClientInfo(client);
					clientInfoService.doClientPush(client);
				}
			} else if ("130004".equals(jsInfo.getString("code")) || "130005".equals(jsInfo.getString("code"))
					|| "130006".equals(jsInfo.getString("code"))) {
				// 重复客资，给邀约员推送提醒
				ClientInfo client = new ClientInfo();
				if (!UtilRegex.validateMobile(kzPhone)) {
					client.setKzWechat(kzPhone);
				} else {
					client.setKzPhone(kzPhone);
				}
				client.setCompanyId(Constants.COMP_ID_JINFUREN);

				List<ClientInfo> clientList = clientInfoService.getClientInfoLike(client);
				if (collector == null) {
					collector = new Staff();
					collector.setName(myName);
				}
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
							PushUtil.pushReClient(c, collector, appointer);
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

	/**
	 * 6:客资 接单---钉钉
	 * 
	 * *** kzid (必传) 客资ID *** *** staffid (必传) 职工ID *** ***
	 * 职工接单：修改客资邀约员ID（APPOINTID）为职工的ID ***
	 * 
	 * *** 暂时不提供拒单情况 ***
	 */
	@RequestMapping(value = "do_kz_make_order_ding", method = RequestMethod.GET)
	public String doKzMakeOrderDing(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");
		Integer staffId = Integer.valueOf(maps.get("staffid"));

		model.addAttribute("kzId", kzId);
		model.addAttribute("staffId", staffId);

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return "mobile/skip/ding_jiedan_fail";
		}

		/*-- 职工信息 --*/
		Staff staff = staffService.getStaffInfoById(staffId);

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
					return "mobile/skip/ding_jiedan_fail";
				}
				if (client.getAppointId() != null && client.getAppointId() != 0) {
					model.addAttribute("code", "999999");
					if (!client.getAppointId().toString().equals(staff.getId().toString())) {
						model.addAttribute("msg", "该客资已被其他邀约员接单");
					} else {
						model.addAttribute("msg", "客资已接单，无需重复接单");
					}
					return "mobile/skip/ding_jiedan_fail";
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		/*-- 接口调用 ，业务执行 --*/
		Map<String, Object> reqContent = new HashMap<String, Object>();
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
			if (!"100000".equals(jsInfo.getString("code"))) {
				return "mobile/skip/ding_jiedan_fail";
			}
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
			return "mobile/skip/ding_jiedan_fail";
		}

		return "mobile/skip/ding_jiedan_success";
	}
}