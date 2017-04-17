package com.crm.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crm.common.util.DateUtil;
import com.crm.common.util.DigestUtils;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Shop;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.ShopService;

@Controller
@RequestMapping("/paikong")
public class PaikongWebController {

	@Autowired
	private ShopService shopService;

	@Autowired
	private CompanyService companyService;

	@RequestMapping("createInformation")
	@ResponseBody
	public Model createInformation(@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "companyName", defaultValue = "") String companyName,
			@RequestParam(value = "phone", defaultValue = "") String phone,
			@RequestParam(value = "website", defaultValue = "") String webSite,
			@RequestParam(value = "merchantPid", defaultValue = "") String merchantPid,
			@RequestParam(value = "str", defaultValue = "") String str,
			@RequestParam(value = "type", defaultValue = "0") int type,
			@RequestParam(value = "album", defaultValue = "0") int album,
			@RequestParam(value = "shopId", defaultValue = "") String shopId,
			@RequestParam(value = "shopName", defaultValue = "") String shopName, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			// if(str == null || !"adsl1984325".equals(str)){
			// throw new BizException(200, "确认码输出有误!");
			// }
			//
			// if(companyService.getByName(companyName) != null){
			// throw new BizException(200, "企业名已经有人使用!");
			// };
			// if(staffService.getByPhone(phone) != null){
			// throw new BizException(200, "该手机已经注册!");
			// }
			// Company company_ = companyService.getByWebsite(website);
			// if(company_ != null){
			// throw new BizException(200, "企业标识已经有人使用!");
			// }
			// company_ = companyService.getByMerchantPid(merchant_pid);
			// if(company_ != null){
			// throw new BizException(200, "商户ID已经使用过!");
			// }
			String ip = WebUtils.getIP(request);

			Company comp = new Company();
			comp.setCompName(companyName);
			// comp.setCompDistrict(compDistrict);
			comp.setCompTypeId(type);
			comp.setWebSite(webSite);
			// comp.setBanner(banner);
			// comp.setLogo(logoUrl);
			// comp.setMemo(memo);
			// comp.setCorpId(corpId);
			comp.setMerchantPid(merchantPid);
			comp.setMerchantShowName(companyName);
			comp.setMerchantShowPhone(phone);
			comp.setSmsPhone(phone);
			comp.setCreateIp(ip);
			comp.setUpdateIp(ip);
			comp.setIsTip(true);

			int companyId = companyService.createCompany(comp).getCompId();

			shopId = shopId.replaceAll(" ", "");
			if (shopId.length() <= 0) {
				shopId = ",";
			}

			shopName = shopName.replaceAll(" ", "");
			if (shopName.length() <= 0) {
				shopName = ",";
			}

			String[] shopIds = shopId.split(",");
			String[] shopNames = shopName.split(",");

			Shop shop = new Shop();
			for (int i = 0; i < shopIds.length; i++) {
				shop.setAlipayShopId(shopIds[i]);
				shop.setShopName(shopNames[i]);
				shop.setMerchantPid(merchantPid);
				shop.setMerchantShowName(shopNames[i]);
				shop.setMerchantShowPhone(phone);
				shop.setSmsPhone(phone);
				shop.setCompanyId(companyId);
				shop.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				shop.setUpdateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				shop.setIsShow(true);
				shop.setIsDel(false);
				shop.setUpdateIp(ip);
				shop.setCreateIp(ip);
				shopService.createShop(shop);
			}

			Staff staff = new Staff();
			staff.setName(name);
			staff.setPassword(DigestUtils.hash("123456"));
			staff.setUsername(phone);
			staff.setPhone(phone);
			staff.setRole(0);
			staff.setCompanyId(companyId);
			staff.setMerchantPid(merchantPid);
			staff.setIsLock(false);
			staff.setIsShow(true);
			staff.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			staff.setCompanyName(companyName);
			staff.setCreateIp(ip);
			// staffService.createStaff(staff);

			model.addAttribute("code", 100);

		} catch (EduException e) {
			model.addAttribute("code", 100);
			model.addAttribute("msg", e.getMessage());
		}

		return model;
	}
}
