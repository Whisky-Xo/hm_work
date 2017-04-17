package com.crm.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
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
import com.crm.common.util.TimeUtils;
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
 * @author JingChenglong 2017-01-17 12:49
 *
 */
@Controller
@RequestMapping("/client")
public class SMSCodeAPI {

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
	@RequestMapping(value = "get_msg_preview", method = RequestMethod.POST)
	@ResponseBody
	public Model getMsgPreview(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请选择客资");
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
		if (!UtilRegex.validateMobile(client.getKzPhone())) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "客户电话号码无效");
			return model;
		}
		if (client.getShopId() == null || client.getShopId() == 0 || StringUtils.isEmpty(client.getAppointTime())) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请提交客资遇到门店信息");
			return model;
		}

		/*-- 获取客资遇到门店信息 --*/
		Shop shop = shopService.getByShopId(client.getShopId());
		if (shop == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "暂无门店信息");
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
			smsPrew = getYyjdSmsPreview(smsCode, client, shop);
		} catch (Exception e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "生成预览短信失败，请联系管理员校验短信模板");
			return model;
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		model.addAttribute("sms", smsPrew);
		model.addAttribute("kzid", kzId);

		return model;
	}

	/*-- 发送预约进店短息 --*/
	@RequestMapping(value = "do_send_msg", method = RequestMethod.POST)
	@ResponseBody
	public Model doSendMsg(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请选择客资");
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
		} finally {
			if (client == null) {
				model.addAttribute("code", "999999");
				model.addAttribute("msg", "未获取到客资信息，请刷新重试。");
				return model;
			}
		}

		if (!UtilRegex.validateMobile(client.getKzPhone())) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "客资电话号码无效");
			return model;
		}
		if (client.getShopId() == null || client.getShopId() == 0 || StringUtils.isEmpty(client.getAppointTime())) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请提交客资预约门店信息");
			return model;
		}

		/*-- 获取客资遇到门店信息 --*/
		Shop shop = shopService.getByShopId(client.getShopId());
		if (shop == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "暂无门店信息");
			return model;
		}

		/*-- 获取短信模板配置 --*/
		SmsCode smsCode;
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
		String appointTime = client.getAppointTime().substring(0, 13) + "点";
		String address = shop.getAddress() + "," + shop.getMerchantShowName();
		try {
			String rst = SMSUtils.sendKzComeShopJinFuRen(client.getKzPhone(), yyCode, appointTime, address,
					shop.getMerchantShowPhone(), smsCode);

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
	public String getYyjdSmsPreview(SmsCode smsCode, ClientInfo info, Shop shop) throws Exception {

		if (smsCode == null || info == null || StringUtil.isEmpty(smsCode.getSpare3())) {
			return "";
		}
		String template = smsCode.getSpare3();
		template = template.replace("${code}", getYyCode(info, smsCode));
		template = template.replace("${time}", info.getAppointTime().substring(0, 13) + "点");
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
		if (StringUtil.isEmpty(client.getAppointTime())) {
			throw new EduException("请提交预约到店时间");
		}
		if (client.getShopId() == null || client.getShopId() == 0) {
			throw new EduException("请提交预约门店");
		}

		String code = StringUtil.nullToStrTrim(smcCode.getSpare1());
		code += TimeUtils.getYmdHHNotDateStr(TimeUtils.stringToDate(client.getAppointTime(), "yyyy-MM-dd HH:mm:ss"))
				.substring(3, 4);
		code += StringUtil.nullToStrTrim(smcCode.getSpare2());
		code += TimeUtils.getYmdHHNotDateStr(TimeUtils.stringToDate(client.getAppointTime(), "yyyy-MM-dd HH:mm:ss"))
				.substring(4, 8);

		String start = client.getAppointTime().substring(0, 10) + " 00:00:00";
		String end = client.getAppointTime().substring(0, 10) + " 23:59:59";

		Integer cnt = null;
		try {
			cnt = clientInfoService.getSmsCmtByShopId(client.getCompanyId(), start, end);
		} catch (Exception e) {
			throw new EduException("生成短信优惠编码失败，请联系管理员。");
		}

		if (cnt != null) {
			cnt++;
		} else {
			cnt = 0;
		}

		String count = cnt.toString();

		while (count.length() < 3) {
			count = "0" + count;
		}

		code += count;

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