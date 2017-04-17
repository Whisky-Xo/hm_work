package com.crm.controller.crm;

import java.util.HashMap;
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
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.SMSUtils;
import com.crm.common.util.StringUtil;
import com.crm.common.util.UtilRegex;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Company;
import com.crm.model.Shop;
import com.crm.model.SmsCode;
import com.crm.model.Staff;
import com.crm.service.ClientInfoService;
import com.crm.service.ShopService;
import com.crm.service.StaffService;
import com.crm.service.impl.SmsCodeServiceImpl;
import com.taobao.api.ApiException;

/**
 * CRM3.0北斗新星：短信接口
 * 
 * @author JingChenglong 2017-02-15 11:19
 *
 */
@Controller
@RequestMapping("/client")
public class BeiDouSMSCodeAPI {

	@Autowired
	StaffService staffService;/* 职工管理 */
	@Autowired
	ShopService shopService;/* 门店 */
	@Autowired
	ClientInfoService clientInfoService;/* 客资 */
	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */
	@Autowired
	SmsCodeServiceImpl smsCodeServiceImpl;/* 短信模板 */

	private static final Company QIEIN = new Company();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/*-- 短信预览--预约进店短信 --*/
	@RequestMapping(value = "msg_preview", method = RequestMethod.POST)
	@ResponseBody
	public Model msgPreview(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		String kzId = StringUtil.nullToStrTrim(maps.get("kzid"));
		String yyShopId = StringUtil.nullToStrTrim(maps.get("shopid"));
		String yyTime = StringUtil.nullToStrTrim(maps.get("yytime"));
		String phone = StringUtil.nullToStrTrim(maps.get("phone"));

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId) || StringUtil.isEmpty(yyShopId) || StringUtil.isEmpty(yyTime)
				|| StringUtil.isEmpty(phone)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 获取客资详细信息 --*/
		ClientInfo client = new ClientInfo();
		client.setKzId(kzId);
		client.setCompanyId(staff.getCompanyId());
		try {
			client = clientInfoService.getClientInfo(client);
		} catch (Exception e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "客资信息获取失败");
			return model;
		}

		/*-- 客资校验 --*/
		if (client == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "客资不存在");
			return model;
		}
		if (!UtilRegex.validateMobile(phone)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "客户电话号码无效");
			return model;
		}

		/*-- 获取客资遇到门店信息 --*/
		Shop shop = shopService.getByShopId(Integer.valueOf(yyShopId));
		if (shop == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "未获取到门店信息");
			return model;
		}

		// 获取短信模板配置
		SmsCode smsCode = null;
		try {
			smsCode = getSmsTemplate(staff.getCompanyId(), SmsCode.YYJD);
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", e.getMessage());
			return model;
		}

		// 生成预览短信
		String smsPrew = null;
		try {
			smsPrew = getYyjdSmsPreview(smsCode, client, shop, phone, yyTime);
		} catch (Exception e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "生成预览短信失败，请联系管理员校验短信模板");
			return model;
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		model.addAttribute("sms", smsPrew);
		return model;
	}

	/*-- 发送预约进店短息 --*/
	@RequestMapping(value = "send_msg", method = RequestMethod.POST)
	@ResponseBody
	public Model sendMsg(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		String kzId = StringUtil.nullToStrTrim(maps.get("kzid"));
		String yyShopId = StringUtil.nullToStrTrim(maps.get("shopid"));
		String yyTime = StringUtil.nullToStrTrim(maps.get("yytime"));
		String phone = StringUtil.nullToStrTrim(maps.get("phone"));

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId) || StringUtil.isEmpty(yyShopId) || StringUtil.isEmpty(yyTime)
				|| StringUtil.isEmpty(phone)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 获取客资详细信息 --*/
		ClientInfo client = new ClientInfo();
		client.setKzId(kzId);
		client.setCompanyId(staff.getCompanyId());
		try {
			client = clientInfoService.getClientInfo(client);
		} catch (Exception e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "客资信息获取失败");
			return model;
		}

		/*-- 客资校验 --*/
		if (client == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "客资不存在");
			return model;
		}
		if (!UtilRegex.validateMobile(phone)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "客户电话号码无效");
			return model;
		}

		/*-- 获取客资遇到门店信息 --*/
		Shop shop = shopService.getByShopId(Integer.valueOf(yyShopId));
		if (shop == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "未获取到门店信息");
			return model;
		}

		// 获取短信模板配置
		SmsCode smsCode = null;
		try {
			smsCode = getSmsTemplate(staff.getCompanyId(), SmsCode.YYJD);
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", e.getMessage());
			return model;
		}

		String yyCode;
		try {
			yyCode = getYyCode(client, smsCode);
		} catch (Exception e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", e.getMessage());
			return model;
		}

		/*-- 发送短息 --*/
		String address = shop.getAddress() + "," + shop.getMerchantShowName();
		try {
			String rst = SMSUtils.sendKzComeShopJinFuRen(phone, yyCode, yyTime, address, shop.getMerchantShowPhone(),
					smsCode);

			JSONObject json = JSONObject.parseObject(rst);
			if (!"0".equals(json.getJSONObject("alibaba_aliqin_fc_sms_num_send_response").getJSONObject("result")
					.getString("err_code"))) {
				model.addAttribute("code", "999999");
				model.addAttribute("msg", "短信发送失败，请稍后重试。");
				return model;
			}
		} catch (ApiException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "短信发送失败，请稍后重试。");
			return model;
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "短信发送失败，请稍后重试。");
			return model;
		} catch (Exception e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "短信发送失败，请稍后重试。");
			return model;

		}

		/*-- 修改状态，日志记录 --*/
		reqContent = new HashMap<String, Object>();
		reqContent.put("ip", WebUtils.getIP(request));
		reqContent.put("kzids", kzId);
		reqContent.put("operaid", staff.getId());
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("code", yyCode);
		try {
			crmBaseApi.doService(reqContent, "doAddSendSmsLog");
		} catch (EduException e) {
			e.printStackTrace();
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "发送成功");

		return model;
	}

	// 根据短信模板过滤生成预览短信-预约进店
	public String getYyjdSmsPreview(SmsCode smsCode, ClientInfo info, Shop shop, String phone, String yyTime)
			throws Exception {

		if (smsCode == null || info == null || StringUtil.isEmpty(smsCode.getSpare3())) {
			return "";
		}
		String template = smsCode.getSpare3();
		template = template.replace("${code}", getYyCode(info, smsCode));
		template = template.replace("${time}", yyTime);
		template = template.replace("${address}",
				StringUtil.nullToStr((shop.getAddress())) + "," + shop.getMerchantShowName());
		template = template.replace("${telno}", shop.getMerchantShowPhone());
		template = "【" + smsCode.getSign() + "】" + template;

		return template;
	}

	// 获取预约进店编码
	public String getYyCode(ClientInfo client, SmsCode smcCode) throws EduException {

		if (client == null) {
			throw new EduException("客资信息获取失败");
		}

		String code = StringUtil.nullToStrTrim(smcCode.getSpare1());
		code += client.getKzNum();
		code += StringUtil.nullToStrTrim(smcCode.getSpare2());

		return code;
	}

	// 获取短信模板
	public SmsCode getSmsTemplate(Integer companyId, String smsType) throws EduException {

		if (companyId == null || companyId == 0) {
			throw new EduException("企业信息不能为空");
		}
		if (StringUtil.isEmpty(smsType)) {
			throw new EduException("短信模板类型不能为空");
		}

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("companyId", companyId);
		param.put("smsType", smsType);
		SmsCode smsCode = null;
		try {
			smsCode = smsCodeServiceImpl.getSmsCodeByCondition(param);
		} catch (EduException e) {
			throw new EduException("短信模板获取失败");
		} finally {
			if (smsCode == null) {
				throw new EduException("企业未配置该短信模板，请联系管理员。");
			}
			if (StringUtil.isEmpty(smsCode.getUrl()) || StringUtil.isEmpty(smsCode.getAppKey())
					|| StringUtil.isEmpty(smsCode.getSecret())) {
				throw new EduException("企业短信参数不完整，请联系管理员。");
			}
		}

		if (!smsCode.getIsShow()) {
			throw new EduException("预约进店短信功能被管理员限定，请联系管理员。");
		}

		return smsCode;
	}

	// 获取登录人详细信息
	public Staff getStaffDetail(HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return null;
		}
		return staffService.getStaffInfoById(staff.getId());
	}
}