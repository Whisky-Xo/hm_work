package com.crm.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
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

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.common.util.DateUtil;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.SMSUtils;
import com.crm.common.util.StringUtil;
import com.crm.common.util.UtilRegex;
import com.crm.common.util.WebUtils;
import com.crm.model.Company;
import com.crm.model.Shop;
import com.crm.service.CompanyService;
import com.crm.service.ShopService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;

@Controller
@RequestMapping("mobile")
public class MobileController {

	// @Autowired
	// ClientService clientService;

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */

	@Autowired
	SourceService sourceService;

	@Autowired
	CompanyService companyService;

	@Autowired
	ShopService shopService;
	
	
	@Autowired
	StaffService staffService;


	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String editClientInfo(Integer date, Model model,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "mobile", defaultValue = "") String mobile,
			@RequestParam(value = "remark", defaultValue = "") String remark,
			@RequestParam(value = "merchantPid", required = false) String merchantPid,
			@RequestParam(value = "shopId", required = false) String shopId,
			@RequestParam(value = "source", required = false) String source, HttpServletRequest request,
			HttpServletResponse response) {

		// String banner = companyService.getCompanyBanner(merchantPid);
		// banner = banner == null ? "" : banner;

		date = date == null ? DateUtil.toInt(new Date()) : date;
		String dateStr = DateUtil.toStr(date);
		model.addAttribute("today", dateStr);
		model.addAttribute("date", date);
		// model.addAttribute("banner", banner);
		model.addAttribute("merchantPid", merchantPid);
		model.addAttribute("shopId", shopId);
		model.addAttribute("source", source);
		try {
			if (!"".equals(name)) {
				name = URLDecoder.decode(name, "utf-8");
				model.addAttribute("name", name);
			}
			if (!"".equals(mobile)) {
				model.addAttribute("mobile", mobile);
			}
			if (!"".equals(remark)) {
				remark = URLDecoder.decode(remark, "utf-8");
				model.addAttribute("remark", remark);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "mobile/edit";
	}

	@RequestMapping(value = "ClientInfo", method = RequestMethod.POST)
	public String createChannelClientInfo(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "mobile", required = true) String phone,
			@RequestParam(value = "date", required = true) String date,
			// @RequestParam(value = "code", required = true) String code,
			@RequestParam(value = "remark", required = false) String memo,
			@RequestParam(value = "merchantPid", required = false) String merchantPid,
			@RequestParam(value = "source", required = false) String source,
			@RequestParam(value = "shopId", required = false) String shopId, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		/*
		 * Cookie cookie = CookieCompoment.getCookie(request, "CODE"); if
		 * (cookie == null || !cookie.getValue().equals(code.toUpperCase())) {
		 * model.addAttribute("errorCode", "1000");
		 * model.addAttribute("errorMsg", "验证码已过期"); int dateInt =
		 * DateUtil.toInt(date); return editClientInfo(dateInt, model, name,
		 * phone, remark, merchantPid, shopId, request, response); }
		 */

		if (!UtilRegex.validateMobile(phone)) {
			model.addAttribute("errorCode", "1001");
			model.addAttribute("errorMsg", "手机号码不正确");
			return "redirect:/mobile/edit?merchantPid=" + merchantPid + "&shopId=" + shopId;
		}

		if (StringUtil.isBlank(merchantPid)) {
			model.addAttribute("errorCode", "1005");
			model.addAttribute("errorMsg", "商户ID不能为空");
			return "redirect:/mobile/edit?merchantPid=" + merchantPid + "&shopId=" + shopId;
		}

		Shop shop = shopService.getByAlipayShopId(shopId);
		Company company = companyService.getCompanyInfoByMerchantPid(merchantPid);

		String showName = "";
		String showPhone = "";
		String SMSPhone = "";

		if (company == null) {
			model.addAttribute("errorCode", "1004");
			model.addAttribute("errorMsg", "商户ID没有与之对应的企业");
			return "redirect:/mobile/edit?merchantPid=" + merchantPid + "&shopId=" + shopId;
		}
		if (shop == null) {
			showName = company.getMerchantShowName();
			showPhone = company.getMerchantShowPhone();
			SMSPhone = company.getSmsPhone();
		} else {
			showName = shop.getMerchantShowName();
			showPhone = shop.getMerchantShowPhone();
			SMSPhone = shop.getSmsPhone();
		}
		String ip = WebUtils.getIP(request);
		Map<String, Object> reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", company.getCompId());
		reqContent.put("merchantpid", merchantPid);
		reqContent.put("shopid", shop.getShopId());
		reqContent.put("ip", ip);
		reqContent.put("name", name);
		reqContent.put("phone", phone);
		reqContent.put("memo", memo+"（来自"+showName+"）");
		reqContent.put("yptime", DateUtil.format(DateUtil.toDate(date), "yyyy-MM-dd HH:mm:ss"));
		reqContent.put("source", source);
		// source = alipay , wechat , 其他
		String clientRstStr;
		try {
			clientRstStr = crmBaseApi.doService(reqContent, "clientInfoAddFromAlipay");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(clientRstStr);
			String code = js.getString("code");
			if ("100000".equals(code)) {
				// 发短信提醒客资录入
				if (true) { // 验证门店或者商户短信条数
					SMSUtils.sendNewKzAddOrder(SMSPhone, ":" + showName +" "+ name +" "+ phone);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("shop", showName);
		model.addAttribute("date", date);
		model.addAttribute("tel", showPhone);

		return "mobile/success";

	}

	@RequestMapping(value = "calendar", method = RequestMethod.GET)
	public String calendar(Integer date, Model model, @RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "mobile", defaultValue = "") String mobile,
			@RequestParam(value = "remark", defaultValue = "") String remark,
			@RequestParam(value = "merchantPid", required = false) String merchantPid,
			@RequestParam(value = "source", required = false) String source,
			@RequestParam(value = "shopId", required = false) String shopId, HttpServletRequest request,
			HttpServletResponse response) {

		date = date == null ? DateUtil.getStartOfMonth() : DateUtil.getStartOfMonth(date);
		String dateStr = DateUtil.toStr(date);
		int week = DateUtil.getweek_(DateUtil.getWeek(dateStr));
		String month = dateStr.substring(5, 7);
		String year = dateStr.substring(0, 4);
		int month_ = Integer.parseInt(month);

		String next = month.equals("12") ? String.valueOf(Integer.parseInt(year) + 1) + "0101"
				: year + (month_ > 8 ? (String.valueOf(month_ + 1)) : "0" + (String.valueOf(month_ + 1))) + "01";

		String last = month.equals("01") ? String.valueOf(Integer.parseInt(year) - 1) + "1201"
				: year + (month_ > 10 ? (String.valueOf(month_ - 1)) : "0" + (String.valueOf(month_ - 1))) + "01";

		int end = DateUtil.getEndOfMonth(date);
		String endStr = DateUtil.toStr(end);
		String daysStr = endStr.substring(8, 10);
		int days = Integer.parseInt(daysStr);

		int currentDate = DateUtil.toInt(new Date());
		int cdateStart = DateUtil.getStartOfMonth(currentDate);
		int day = DateUtil.getDay(currentDate);

		String mode = date == cdateStart ? "currentDate" : date > cdateStart ? "after" : "ago";

		try {
			if (!"".equals(name)) {
				name = URLDecoder.decode(name, "utf-8");
				model.addAttribute("name", name);
			}
			if (!"".equals(mobile)) {
				model.addAttribute("mobile", mobile);
			}
			if (!"".equals(remark)) {
				remark = URLDecoder.decode(remark, "utf-8");
				model.addAttribute("remark", remark);
			}
			if (!"".equals(merchantPid)) {
				model.addAttribute("merchantPid", merchantPid);
			}
			if (!"".equals(merchantPid)) {
				model.addAttribute("shopId", shopId);
			}
			if (source != null && !"".equals(source)) {
				model.addAttribute("source", source);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		model.addAttribute("mode", mode);
		model.addAttribute("week", week);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
		model.addAttribute("days", days);
		model.addAttribute("day", day);
		model.addAttribute("next", next);
		model.addAttribute("last", last);
		return "mobile/calendar";
	}	
	
}
