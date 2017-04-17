package com.crm.controller;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crm.common.util.*;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Shop;
import com.crm.service.CompanyService;
import com.crm.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.model.Staff;
import com.crm.service.StaffService;


@Controller
@RequestMapping("staff")
public class StaffController {
	
	 @Autowired
	 StaffService staffService;

	@Autowired
	CompanyService companyService;

	@Autowired
	ShopService shopService;
	 
	/**
	 * 1：新增员工
	 * 
	 * *** name			(必传)	员工姓名			***
	 * *** phone		(必传)	员工手机号			***
	 * *** username		(必传)	员工用户名			***
	 * *** password		(必传)	密码				***
	 * *** corpId				钉钉关联ID		***
	 * *** merchantPid			口碑商户ID		***
	 * *** staffId				所属公司id		***
	 * *** staffName			公司名称			***
	 * *** shopId				门店ID			***
	 * *** shopName				门店名字			
	 * @throws UnsupportedEncodingException ***
	 */
	 
	@RequestMapping(value = "/add_staff_info",
			produces = "charset=UTF-8")
	@ResponseBody
	public JSONObject addStaffInfo(
			@RequestParam(value = "role") int role,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "phone") String phone,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "corpId") String corpId,
            @RequestParam(value = "companyId") Integer companyId,
            @RequestParam(value = "merchantPid",required=false) String merchantPid,
            @RequestParam(value = "staffId",required=false) String staffId,
            @RequestParam(value = "staffName",required=false) String staffName,
            @RequestParam(value = "shopId",required=false) Integer shopId,
            @RequestParam(value = "shopName",required=false) String shopName,
			HttpServletRequest request,
			HttpServletResponse response){
		JSONObject reply = new JSONObject();
		reply.put("code", 140001);
		reply.put("msg","内部错误");
		Staff old = staffService.getStaffInfoByAccount(phone);
		if (old!=null) {
			reply.put("msg","存在相同的用户名，请更换手机号");
			return reply;
		}
		
		
		/*-- 参数提取 */
		Staff staff = new Staff();
		staff.setName(name);
		staff.setPhone(phone);
		staff.setUsername(phone);
		staff.setPassword(DigestUtils.hash(password));
		staff.setCorpId(corpId);
		staff.setShopName(staffName);
		staff.setShopId(shopId);
		staff.setShopName(shopName);
		staff.setRole(1);
		staff.setCompanyId(companyId);
		staff.setCreateIp(WebUtils.getIP(request));
		staff.setLastIp(WebUtils.getIP(request));

//		/*-- 业务执行 --*/
//		Integer num=staffService.createStaff(staff);
//		if (num!=null&&num!=0) {
//			reply.put("code", 100000);
//			reply.put("msg","创建成功");
//		}
		
		return reply;
	}

	
	
	
	/**
	 * 2：根据ID删除员工信息
	 * 
	 * *** id		(必传)	员工ID		***
	 */
	@RequestMapping(value = "/remove_staff_byid", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject removeStaffById(
			@RequestParam(value = "id") Integer id,
			HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject reply = new JSONObject();
		reply.put("code", 140001);
		reply.put("msg","内部错误");

		/*-- 参数提取 */
		Staff staff = new Staff();
		staff.setId(id);

//		/*-- 业务执行 --*/
//		Integer num=staffService.removeStaffById(staff);
//		if (num!=null&&num>0) {
//			reply.put("code", 100000);
//			reply.put("msg","删除成功");
//		}

		return reply;
	}

	
	
	
	
	/**
	 * 3：根据ID编辑员工信息
	 * *** id			(必传)	员工id			***
	 * *** name					员工姓名			***
	 * *** phone				员工手机号			***
	 * *** corpId				钉钉关联ID		***
	 * *** merchantPid			口碑商户ID		***
	 * *** staffId				所属公司id		***
	 * *** staffName			公司名称			***
	 * *** shopId				门店ID			***
	 * *** shopName				门店名字			***
	 */
	
	@RequestMapping(value = "/edit_staff_info_byid", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject editStaffInfoById(
			@RequestParam(value = "old_password", defaultValue = "") String old_password,
			@RequestParam(value = "password", defaultValue = "") String password,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "username", defaultValue = "") String username,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "role") int role,
			@RequestParam(value = "id") Integer id,
            @RequestParam(value = "corpId",required=false) String corpId,
            @RequestParam(value = "merchantPid",required=false) String merchantPid,
            @RequestParam(value = "staffId",required=false) String staffId,
            @RequestParam(value = "staffName",required=false) String staffName,
            @RequestParam(value = "shopId",required=false) Integer shopId,
            @RequestParam(value = "shopName",required=false) String shopName, 
			Model model, HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject reply = new JSONObject();
		reply.put("code", 140001);
		reply.put("msg","内部错误");
		
		
		Staff staff = staffService.getStaffInfoById(id);
		if(!"".equals(password) && !"".equals(old_password)){
			if (!staff.getPassword().equals(DigestUtils.hash(old_password))) {
				reply.put("msg","旧密码输入有误!");
				return reply;
			}
		}
		
		
		if(!"".equals(password)){
			password = DigestUtils.hash(password);
			staff.setPassword(password);
		}
		
		if(!"".equals(phone)){
			Staff old = staffService.getStaffInfoByAccount(phone);
			if(old != null){
				reply.put("msg","存在相同的用户名，请更换手机号");
				return reply;
			}else {
				staff.setPhone(phone);
				staff.setUsername(phone);
			}
				
			
		}
		if(!"".equals(name)){
			staff.setName(name);
		}
		
		
		/*-- 参数提取 */
		staff.setId(id);
		staff.setCorpId(corpId);
		staff.setShopName(staffName);
		staff.setShopId(shopId);
		staff.setShopName(shopName);
		staff.setRole(role);
		staff.setLastIp(WebUtils.getIP(request));

		// /*-- 业务执行 --*/
		// Integer num=staffService.editStaffById(staff);
		// if (num!=null&&num>0) {
		// reply.put("code", 100000);
		// reply.put("msg","修改成功");
		// }

		return reply;
	}

	/**
	 * 4：根据ID获取企业信息
	 * 
	 * *** id		(必传)	企业ID		***
	 */
	@RequestMapping(value = "/get_staff_info_byid", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject getStaffInfoByid(
			@RequestParam(value = "id") Integer id,
			HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject reply = new JSONObject();
		reply.put("code", 140001);
		reply.put("msg","内部错误");
		
		Staff staff = staffService.getStaffInfoById(id);
		if (staff==null) {
			reply.put("msg","没有找到员工");
		}else {
			reply.put("code", 100000);
			reply.put("msg","查询成功");
		}

		return reply;
	}


	@RequestMapping("getAlbumData")
	@ResponseBody
	public Model getAlbumData(Model model, HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		int companyId = staff.getCompanyId();
		try {
			Company company = companyService.getCompanyInfoById(companyId);
			Map<String,String> albumData =  new HashMap<String,String>();
			String path = request.getRequestURL().substring(0,request.getRequestURL().indexOf("/",7));
			albumData.put("company_nick",RSAUtils.etCipher(URLEncoder.encode(company.getCompName(),"UTF-8")));
			albumData.put("company_id",RSAUtils.etCipher(URLEncoder.encode(company.getCompId()+"","UTF-8")));
			albumData.put("company_slug",RSAUtils.etCipher(URLEncoder.encode(company.getWebSite(),"UTF-8")));
			albumData.put("company_home",RSAUtils.etCipher(URLEncoder.encode(path+"/"+company.getWebSite()+"/index","UTF-8")));
			String company_color = "#ff5400";
			if(company.getColor() != null && !"".equals(company.getColor())){
				company_color = company.getColor();
			}
			albumData.put("company_color", RSAUtils.etCipher(URLEncoder.encode(company_color,"UTF-8")));
			String LOGO = "";
			if(company.getLogo() !=null && !"".equals(company.getLogo())){
				LOGO = "http://qieinoa.oss-cn-hangzhou.aliyuncs.com/"+company.getLogo();
			}
			albumData.put("company_logo",RSAUtils.etCipher(URLEncoder.encode(LOGO,"UTF-8")));
			albumData.put("company_signout",RSAUtils.etCipher(URLEncoder.encode(path+"/"+company.getWebSite()+"/logout","UTF-8")));
			albumData.put("ali_mid",RSAUtils.etCipher(URLEncoder.encode(company.getMerchantPid(),"UTF-8")));
			String templatenum = company.getTemplatenum();
			if(company.getTemplatenum() == null || "".equals(company.getTemplatenum())){
				templatenum = "2";
			}
			albumData.put("resour_templatenum",RSAUtils.etCipher(templatenum));
			albumData.put("unix",RSAUtils.etCipher((DateUtil.getNow()+60)+""));
			String resour_limit = "{\"sample\":{\"cat\":\""+RSAUtils.etCipher(8+"")+"\",\"post\":\""+RSAUtils.etCipher(6+"")+"\",\"images\":\""+RSAUtils.etCipher(20+"")+"\"},\"customer\":{\"cat\":\""+RSAUtils.etCipher(0+"")+"\",\"post\":\""+RSAUtils.etCipher(12+"")+"\",\"images\":\""+RSAUtils.etCipher(20+"")+"\"}}";
			albumData.put("resour_limit",URLEncoder.encode( resour_limit,"UTF-8"));

			List<Shop> stores = shopService.listOpeningStoresForAlbum(company.getCompId());
			String shop = "[";
			for(Shop store:stores){
				shop += "{\"shop_id\":\""+RSAUtils.etCipher(store.getShopId()+"")+"\",\"ali_shop_id\":\""+RSAUtils.etCipher(store.getAlipayShopId())+"\",\"shop_name\":\""+store.getShopName()+"\",\"shop_crm\":\""+path+"/hm-crm/mobile/edit\",\"shop_tel\":\""+store.getMerchantShowPhone()+"\"},";
			}
			if(shop.length()>1){
				shop = shop.substring(0, shop.length()-1);
			}
			shop += "]";
			albumData.put("shop", URLEncoder.encode(shop,"UTF-8"));

			model.addAttribute("albumData",albumData);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EduException e) {
			e.printStackTrace();
		}
		return model;
	}
}
