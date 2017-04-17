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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.CharsetUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.WebUtils;
import com.crm.model.Group;
import com.crm.service.GroupService;

/**
 * 小组管理
 * 
 * @author JingChenglong 2016-09-09 16:31
 *
 */
@Controller
@RequestMapping("/group")
public class GroupController {

	@Autowired
	GroupService groupService;/* 小组操作 */

	/**
	 * 1：新增小组信息
	 * 
	 * *** groupName		(必传)	小组名称			***
	 * *** deptId			(必传)	部门ID			***
	 * *** compId			(必传)	所属企业ID			***
	 * *** memo						备注说明			***
	 * *** shopId					所属门店ID			***
	 */
	@RequestMapping(value = "/add_group_info",produces = MediaType.APPLICATION_JSON_VALUE
			+ CharsetUtil.CHARSET)
	@ResponseBody
	public JSONObject addGroupInfo(
			@RequestParam Map<String, String> maps,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject reply = new JSONObject();
		reply.put("code", 140001);
		reply.put("msg","内部错误");

		
		
		
		/*-- 参数提取 */
		String groupName = maps.get("groupName");
		String deptId = maps.get("deptId");
		String shopId = maps.get("shopId");
		String compId = maps.get("compId");
		String memo = maps.get("memo");
		
		
		if (StringUtil.isEmpty(groupName)) {
			reply.put("msg","小组名称不能为空");
			return reply;
		}
		
		if (StringUtil.isEmpty(deptId)) {
			reply.put("msg","部门id不能为空");
			return reply;
		}
		
		if (StringUtil.isEmpty(compId)) {
			reply.put("msg","公司id不能为空");
			return reply;
		}
		
		Group old = groupService.getGroupInfoByName(groupName);
		if (old!=null) {
			reply.put("msg","存在相同的小组名称，请更换小组名称");
			return reply;
		}
		
		
		Group group = new Group();
		group.setGroupName(groupName);
		group.setDeptId(Integer.valueOf(deptId));
		if (!StringUtil.isEmpty(shopId)) {
			group.setShopId(Integer.valueOf(shopId));
		}
		group.setCompanyId(Integer.valueOf(compId));
		group.setCreateIp(WebUtils.getIP(request));
		group.setUpdateIp(WebUtils.getIP(request));
		group.setMemo(memo);

		/*-- 业务执行 --*/
		Integer num=groupService.createGroup(group);
		if (num!=null&&num>0) {
			reply.put("code", 100000);
			reply.put("msg","创建小组成功");
		}

		return reply;
	}

	/**
	 * 2：根据ID删除小组信息
	 * 
	 * *** id		(必传)	小组ID		***
	 */
	@RequestMapping(value = "/remove_group_byid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ CharsetUtil.CHARSET)
	@ResponseBody
	public JSONObject removeGroupById(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject reply = new JSONObject();
		reply.put("code", 140001);
		reply.put("msg","内部错误");
		
		
		/*-- 参数提取 */
		Group group = new Group();
		String id = maps.get("id");
		if (StringUtil.isEmpty(id)) {
			group.setGroupId(Integer.valueOf(id));
		}
		group.setUpdateIp(WebUtils.getIP(request));

		/*-- 业务执行 --*/
		Integer num=groupService.removeGroupById(group);
		if (num!=null&&num>0) {
			reply.put("code", 100000);
			reply.put("msg","删除成功");
		}
		

		return reply;
	}

	/**
	 * 3：根据ID编辑小组信息
	 * 
	 * *** group_id			(必传)	小组ID			***
	 * *** dept_id					部门ID			***
	 * *** group_id					所属企业ID			***
	 * *** memo						备注说明			***
	 * *** shop_id					所属门店ID			***
	 */
	@RequestMapping(value = "/edit_group_info_byid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ CharsetUtil.CHARSET)
	@ResponseBody
	public JSONObject editGroupInfoById(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject reply = new JSONObject();
		reply.put("code", 140001);
		reply.put("msg","内部错误");
		
		
		/*-- 参数提取 */
		Group group = new Group();
		group.setGroupId(1);
		group.setDeptId(23);
		group.setCompanyId(12);
		group.setMemo("我们是最棒的小组");
		group.setShopId(12);
		group.setUpdateIp(WebUtils.getIP(request));

		/*-- 业务执行 --*/
		groupService.editGroupById(group);

		return reply;
	}

	/**
	 * 4：根据ID获取小组信息
	 * 
	 * *** id		(必传)	小组ID		***
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