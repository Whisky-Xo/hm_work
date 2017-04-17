package com.crm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.WebUtils;
import com.crm.exception.BizException;
import com.crm.model.Company;
import com.crm.model.Shop;
import com.crm.service.CompanyService;
import com.crm.service.ShopService;

@Controller
@RequestMapping("shop")
public class ShopController {

	@Autowired
	ShopService shopService;

	@Autowired
	private CompanyService companyService;/*-- 企业 --*/

	// private Integer shopId;// 门店主键ID
	// private String ;// 门店名称
	// private String ;// 门店地址
	// private String createTime;// 创建时间
	// private String createIp;// 创建IP
	// private String updateTime;// 最后更新时间
	// private String updateIp;// 最后更新IP
	// private Integer ;// 所属企业ID
	// private String ;// 企业名称
	// private String ;// 支付宝门店ID
	// private String ;// 商户ID
	// private Boolean isShow;// 是否开启
	// private Boolean isDel;// 删除标识

	/**
	 * 1：新增员工
	 * 
	 * *** shopName		(必传)	员工姓名			***
	 * *** address		(必传)	员工手机号			***
	 * *** companyId		(必传)	员工用户名			***
	 * *** companyName		(必传)	密码				***
	 * *** alipayShopId				钉钉关联ID		***
	 * *** merchantPid			口碑商户ID		***
	 */
	// @RequestMapping(value = "/add_shop_info", method = RequestMethod.GET)
	// @ResponseBody
	// public JSONObject addShopInfo(
	// @RequestParam(value = "shopName") String shopName,
	// @RequestParam(value = "address") String address,
	// @RequestParam(value = "companyId") String companyId,
	// @RequestParam(value = "companyName") String companyName,
	// @RequestParam(value = "alipayShopId") String alipayShopId,
	// @RequestParam(value = "merchantPid") String merchantPid,
	// HttpServletRequest request,
	// HttpServletResponse response) {
	// JSONObject reply = new JSONObject();
	// reply.put("code", 140001);
	// reply.put("msg","内部错误");
	// Shop old = shopService.getShopInfoByAccount(username);
	// if (old!=null) {
	// reply.put("msg","存在相同的用户名，请更换用户名");
	// return reply;
	// }
	//
	// /*-- 参数提取 */
	// Shop shop = new Shop();
	// shop.setName(name);
	// shop.setPhone(phone);
	// shop.setUsername(username);
	// shop.setPassword(password);
	// shop.setCorpId(corpId);
	// shop.setShopName(shopName);
	// shop.setShopId(shopId);
	// shop.setShopName(shopName);
	// shop.setCreateIp(WebUtils.getIP(request));
	// shop.setLastIp(WebUtils.getIP(request));
	//
	// /*-- 业务执行 --*/
	// Integer num=shopService.createShop(shop);
	// if (num!=null&&num!=0) {
	// reply.put("code", 100000);
	// reply.put("msg","创建成功");
	// }
	//
	// return reply;
	// }

	/**
	 * 2：根据ID删除员工信息
	 * 
	 * *** id		(必传)	员工ID		***
	 */
	@RequestMapping(value = "/remove_shop_byid", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject removeShopById(@RequestParam(value = "id") Integer id, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject reply = new JSONObject();
		reply.put("code", 140001);
		reply.put("msg", "内部错误");

		/*-- 参数提取 */
		// Shop shop = new Shop();
		// shop.setId(id);
		//
		// /*-- 业务执行 --*/
		// Integer num=shopService.removeShopById(shop);
		// if (num!=null&&num>0) {
		// reply.put("code", 100000);
		// reply.put("msg","删除成功");
		// }

		return reply;
	}

	/**
	 * 3：根据ID编辑员工信息
	 * *** id			(必传)	员工id			***
	 * *** name					员工姓名			***
	 * *** phone				员工手机号			***
	 * *** corpId				钉钉关联ID		***
	 * *** merchantPid			口碑商户ID		***
	 * *** shopId				所属公司id		***
	 * *** shopName			公司名称			***
	 * *** shopId				门店ID			***
	 * *** shopName				门店名字			***
	 */
	// @RequestMapping(value = "/edit_shop_info_byid", method =
	// RequestMethod.GET)
	// @ResponseBody
	// public JSONObject editShopInfoById(
	// @RequestParam(value = "id") Integer id,
	// @RequestParam(value = "name",required=false) String name,
	// @RequestParam(value = "phone",required=false) String phone,
	// @RequestParam(value = "corpId",required=false) String corpId,
	// @RequestParam(value = "merchantPid",required=false) String merchantPid,
	// @RequestParam(value = "shopId",required=false) String shopId,
	// @RequestParam(value = "shopName",required=false) String shopName,
	// @RequestParam(value = "shopId",required=false) Integer shopId,
	// @RequestParam(value = "shopName",required=false) String shopName,
	// Model model, HttpServletRequest request,
	// HttpServletResponse response) {
	//
	// JSONObject reply = new JSONObject();
	// reply.put("code", 140001);
	// reply.put("msg","内部错误");
	//
	//
	// /*-- 参数提取 */
	// Shop shop = new Shop();
	// shop.setId(id);
	// shop.setName(name);
	// shop.setPhone(phone);
	// shop.setCorpId(corpId);
	// shop.setShopName(shopName);
	// shop.setShopId(shopId);
	// shop.setShopName(shopName);
	// shop.setLastIp(WebUtils.getIP(request));
	//
	// /*-- 业务执行 --*/
	// Integer num=shopService.editShopById(shop);
	// if (num!=null&&num>0) {
	// reply.put("code", 100000);
	// reply.put("msg","删除成功");
	// }
	//
	// return reply;
	// }

	/**
	 * 4：根据ID获取企业信息
	 * 
	 * *** id		(必传)	企业ID		***
	 */
	// @RequestMapping(value = "/get_shop_info_byid", method =
	// RequestMethod.GET)
	// @ResponseBody
	// public JSONObject getShopInfoByid(
	// @RequestParam(value = "id") Integer id,
	// HttpServletRequest request,
	// HttpServletResponse response) {
	//
	// JSONObject reply = new JSONObject();
	// reply.put("code", 140001);
	// reply.put("msg","内部错误");
	//
	// Shop shop = shopService.getShopInfoById(id);
	// if (shop==null) {
	// reply.put("msg","没有找到员工");
	// }else {
	// reply.put("code", 100000);
	// reply.put("msg","查询成功");
	// }
	//
	// return reply;
	// }

	/**
	 * 新建门店
	 */
	@RequestMapping(value = "createShop", method = RequestMethod.POST)
	@ResponseBody
	public Model createShop(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String smsPhone = maps.get("smsPhone");
		String merchantShowPhone = maps.get("merchantShowPhone");
		String address = maps.get("address");
		String name = maps.get("name");
		String companyId = maps.get("companyId");
		String alipayShopId = maps.get("alipayShopId");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(smsPhone) || StringUtils.isEmpty(merchantShowPhone) || StringUtils.isEmpty(name)
				|| StringUtils.isEmpty(companyId)) {
			model.addAttribute("code", "200");
			model.addAttribute("msg", "请填写门店信息");
			return model;
		}

		// Staff staff = CookieCompoment.getLoginUser(request);
		// int companyId = staff.getCompanyId();
		String ip = WebUtils.getIP(request);
		int id = Integer.parseInt(companyId);
		try {
			Company company = companyService.getCompanyInfoById(id);
			Shop store = new Shop();
			store.setAddress(address);
			store.setAlipayShopId(alipayShopId);
			store.setCompanyId(id);
			store.setMerchantPid(company.getMerchantPid());
			store.setCreateIp(ip);
			store.setUpdateIp(ip);
			store.setIsDel(false);
			store.setIsShow(true);
			store.setMerchantShowName(name);
			store.setShopName(name);
			store.setSmsPhone(smsPhone);
			store.setMerchantShowPhone(merchantShowPhone);
			shopService.createShop(store);

			model.addAttribute("code", 100);
			model.addAttribute("msg", "新建门店成功!");
		} catch (Exception e) {
			model.addAttribute("code", 200);
			model.addAttribute("msg", "新建门店失败!");
		}
		return model;
	}

	/**
	 * 编辑门店
	 */
	@RequestMapping("updateShop")
	@ResponseBody
	public Model updateShop(@RequestParam(value = "id", defaultValue = "0") int shopId,
			@RequestParam(value = "smsPhone", defaultValue = "") String smsPhone,
			@RequestParam(value = "merchantShowPhone", defaultValue = "") String merchantShowPhone,
			@RequestParam(value = "address", defaultValue = "") String address,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "alipayShopId", defaultValue = "") String alipayShopId, Model model,
			HttpServletRequest request) {
		// Staff staff_ = CookieCompoment.getLoginUser(request);
		try {
			if (shopId <= 0) {
				throw new BizException(200, "非法参数");
			}
			Shop store = shopService.getByShopId(shopId);

			if (!StringUtils.isEmpty(name)) {
				store.setShopName(name);
			}
			if (!StringUtils.isEmpty(address)) {
				store.setAddress(address);
			}
			if (!StringUtils.isEmpty(smsPhone)) {
				store.setSmsPhone(smsPhone);
			}
			if (!StringUtils.isEmpty(merchantShowPhone)) {
				store.setMerchantShowPhone(merchantShowPhone);
			}
			if (!StringUtils.isEmpty(alipayShopId)) {
				store.setAlipayShopId(alipayShopId);
			}
			shopService.updateShop(store);

			model.addAttribute("code", 100);
			model.addAttribute("msg", "编辑门店成功!");
		} catch (BizException e) {
			model.addAttribute("code", e.getCode());
			model.addAttribute("msg", e.getMessage());
		}
		return model;
	}

	@RequestMapping("deleteShop")
	@ResponseBody
	public Model deleteShop(@RequestParam(value = "id", defaultValue = "0") int shopId, Model model,
			HttpServletRequest request) {
		// Staff staff_ = CookieCompoment.getLoginUser(request);
		try {
			if (shopId <= 0) {
				throw new BizException(200, "非法参数");
			}
			shopService.closeShop(shopId);

			model.addAttribute("code", 100);
			model.addAttribute("msg", "删除门店成功!");
		} catch (BizException e) {
			model.addAttribute("code", e.getCode());
			model.addAttribute("msg", e.getMessage());
		}
		return model;
	}

}
