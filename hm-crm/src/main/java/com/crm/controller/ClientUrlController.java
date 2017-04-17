package com.crm.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.crm.common.util.CookieCompoment;
import com.crm.model.Permission;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.PermissionService;
import com.crm.service.StaffService;

/**
 * 客资管理：页面跳转
 * 
 * @author jzl 2016-10-18 15:04
 *
 */
@Controller
@RequestMapping("/client")
public class ClientUrlController {

	@Autowired
	PermissionService permissionService;/* 权限管理 */

	@Autowired
	StaffService staffService;/* 职工管理 */

	@Autowired
	CompanyService companyService;/* 公司管理 */

	/**
	 * 页面跳转
	 * 
	 */
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String ClientUrlInfo(Model model, HttpServletRequest request, HttpServletResponse response) {

//		/*-- 职工信息 --*/
//		Staff staff = CookieCompoment.getLoginUser(request);
//		List<Permission> permissions = permissionService.getByPermissionByStaffId(staff.getId());
//		String role = "";
//		for (Permission permission : permissions) {
//			role += "true".equals(permission.getValue()) ? permission.getPermissionId() + "," : "";
//		}
//		if (!"".equals(role))
//			role = role.substring(0, role.length() - 1);
//
//		List<String> roles = Arrays.asList(role.split(","));
//
//		if (!roles.contains("21") && roles.contains("22")) {
//			// 转介绍邀约员
//			return "redirect:/client/yaoyue_zjs";
//		} else if (!roles.contains("21") && roles.contains("30")) {
//			// 转介绍推广
//			return "redirect:/client/promote_zjs";
//		} else if (!roles.contains("21") && roles.contains("23")) {
//			// 门店接待
//			return "redirect:/client/to_shop_kz";
//		} else if (roles.contains("21") && roles.contains("25")) {
//			// 电商邀约员
//			return "redirect:/client/yaoyue_ds";
//		} else if (roles.contains("55")) {
//			// 电商推广员
//			return "redirect:/client/promote_ds";
//		} else if (roles.contains("21")) {
//			// 电商采集
//			return "redirect:/client/kz_add";
//		}

		return "redirect:/client/home";
	}

}