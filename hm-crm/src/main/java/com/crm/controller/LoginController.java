package com.crm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DigestUtils;
import com.crm.model.Staff;
import com.crm.service.PermissionService;
import com.crm.service.StaffService;

@Controller
public class LoginController {

	@Autowired
	private StaffService staffService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	PermissionService permissionService;/* 权限 */

	@RequestMapping(value = "doLogin")
	@ResponseBody
	public Model doLogin(String username, String password, Model model, String code, HttpServletRequest request,
			HttpServletResponse response) {
		Staff staff = null;

		if ("".equals(username) || "".equals(password)) {
			model.addAttribute("code", 101);
			model.addAttribute("msg", "用户名或密码不能为空");
		}
		String code_ = CookieCompoment.getCode(request);
		if (code_ == null || (!code_.equals(code.toUpperCase()) && !"0".equals(code_))) {
			model.addAttribute("code", 104);
			model.addAttribute("msg", "请输入正确的验证码!");
		}
		staff = staffService.getStaffInfoByAccount(username);
		if (null == staff) {
			model.addAttribute("code", 102);
			model.addAttribute("msg", "用户名不存在");
		} else if (!staff.getPassword().equals(DigestUtils.hash(password))) {
			model.addAttribute("code", 103);
			model.addAttribute("msg", "用户名密码错误");
		} else if (staff.getIsLock()) {
			model.addAttribute("code", 104);
			model.addAttribute("msg", "该用户已失效，暂时无法登录");
		} else {
			CookieCompoment.setLoginUser(response, staff);
			model.addAttribute("code", 100);
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put("id", staff.getId());
			// map.put("flag", true);
			// staffService.changeStaffShowById(map);

		}
		return model;
	}

	/**
	 * 退出
	 */
	@RequestMapping(value = "/logout")
	public String logout(Model model,HttpServletRequest request, HttpServletResponse response) {
		Staff staff = CookieCompoment.getLoginUser(request);
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			CookieCompoment.setLoginUser(response, null);
			return "redirect:../"+company.getWebSite()+"/login";
		} catch (EduException e) {
			e.printStackTrace();
			return "login/404";
		}
	}
}