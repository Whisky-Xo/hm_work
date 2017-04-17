package com.crm.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crm.common.util.CookieCompoment;
import com.crm.model.Permission;
import com.crm.model.Staff;
import com.crm.service.PermissionService;

@Controller
@RequestMapping("role")
public class RoleController {

	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping("sceneGroupGetByType")
	@ResponseBody
	public Model sceneGroupGetByType(@RequestParam(value = "roleId",defaultValue = "0") int roleId,
			Model model, HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		int staffId = staff.getId();
		List<Permission> permissions = permissionService.getByPermissionByStaffId(staffId);
		String role = "";
		for(Permission permission :permissions){
			role += "true".equals(permission.getValue())?permission.getPermissionId()+",":"";
		}
		if(!"".equals(role))
		role = role.substring(0,role.length()-1);
		model.addAttribute("role", role);
		return model;
	}
	
}
