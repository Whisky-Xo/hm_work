package com.crm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.crm.common.util.CharsetUtil;
import com.crm.common.util.WebUtils;
import com.crm.model.Group;
import com.crm.service.GroupService;

/**
 * 权限管理
 * 
 * @author JingChenglong 2016-09-11 09:59
 *
 */
@Controller
@RequestMapping("/pms")
public class PermissionController {

	@Autowired
	GroupService groupService;		/* 权限操作 */

	/**
	 * 1：新增权限信息
	 * 
	 * *** pmsName			(必传)	权限名称			***
	 * *** method			(必传)	方法名			***
	 * *** memo						描述说明			***
	 * *** applicId					所属应用ID		***
	 */
	@RequestMapping(value = "/add_pms_info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ CharsetUtil.CHARSET)
	public String addPmsInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 */
//		Permission pms = new Permission();
//		pms.setPmsName("创建部门");

		// group.setGroupName("销售A组");
		// group.setDeptId(11);
		// group.setShopId(13);
		// group.setCompanyId(20);
		// group.setCreateIp(WebUtils.getIP(request));
		// group.setUpdateIp(WebUtils.getIP(request));
		// group.setMemo("我们权限是最棒的");

		/*-- 业务执行 --*/
		// groupService.createGroup(group);

		return "";
	}

	/**
	 * 2：根据ID删除权限信息
	 * 
	 * *** id		(必传)	权限ID		***
	 */
	@RequestMapping(value = "/remove_group_byid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ CharsetUtil.CHARSET)
	public String removeGroupById(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 */
		Group group = new Group();
		group.setGroupId(2);
		group.setUpdateIp(WebUtils.getIP(request));

		/*-- 业务执行 --*/
		groupService.removeGroupById(group);

		return "";
	}

	/**
	 * 3：根据ID编辑权限信息
	 * 
	 * *** group_id			(必传)	权限ID			***
	 * *** dept_id					部门ID			***
	 * *** group_id					所属企业ID			***
	 * *** memo						备注说明			***
	 * *** shop_id					所属门店ID			***
	 */
	@RequestMapping(value = "/edit_group_info_byid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ CharsetUtil.CHARSET)
	public String editGroupInfoById(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 */
		Group group = new Group();
		group.setGroupId(1);
		group.setDeptId(23);
		group.setCompanyId(12);
		group.setMemo("我们是最棒的权限");
		group.setShopId(12);
		group.setUpdateIp(WebUtils.getIP(request));

		/*-- 业务执行 --*/
		groupService.editGroupById(group);

		return "";
	}

	/**
	 * 4：根据ID获取权限信息
	 * 
	 * *** id		(必传)	权限ID		***
	 */
	@RequestMapping(value = "/get_group_info_byid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ CharsetUtil.CHARSET)
	public String getGroupInfoByid(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 */
		int groupId = 1;

		/*-- 业务执行 --*/
		Group group = groupService.getGroupInfoById(groupId);
		System.out.println(group.getGroupName());

		return "";
	}
}