package com.crm.controller;

import java.util.List;
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

import com.crm.api.constant.NewsConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.Staff;
import com.crm.model.TimeWarn;
import com.crm.service.StaffService;
import com.crm.service.WarnTimerService;

/**
 * 定时任务
 * 
 * @author JingChenglong 2016-11-04 15:16
 *
 */
@Controller
@RequestMapping("/warn")
public class WarnTimeTaskController {

	@Autowired
	StaffService staffService;/* 职工管理 */

	@Autowired
	WarnTimerService warnTimerService;/* 定时任务管理 */

	/*-- 新增定时任务 --*/
	@RequestMapping(value = "do_add_task", method = RequestMethod.POST)
	@ResponseBody
	public Model doAddTask(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String warnTime = maps.get("time");
		String msg = maps.get("msg");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(warnTime) || StringUtils.isEmpty(msg)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		TimeWarn task = new TimeWarn();
		task.setCompanyId(staff.getCompanyId());
		task.setCreateId(staff.getId());
		task.setType(NewsConts.TYPE_NEW_WARN);
		task.setMsg(msg);
		task.setWarnTime(TimeUtils.getIntTime(TimeUtils.format(warnTime, "yyyy-MM-dd HH:mm")));
		task.setTargetId(staff.getId());
		task.setSpare1(warnTime);

		try {
			warnTimerService.addWarnTime(task);
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "成功");
		} catch (EduException e) {
			e.printStackTrace();
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "删除失败");
		}

		return model;
	}

	/*-- 获取个人定时任务列表 --*/
	@RequestMapping(value = "get_own_tasks", method = RequestMethod.POST)
	@ResponseBody
	public Model getOwnTasks(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<TimeWarn> taskList;
		try {
			taskList = warnTimerService.getWarnTaskList(staff);
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "成功");
			model.addAttribute("tasks", taskList);
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "失败");
		}

		return model;
	}

	/*-- 删除定时任务 --*/
	@RequestMapping(value = "do_remove_task", method = RequestMethod.POST)
	@ResponseBody
	public Model doRemoveTask(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Integer id = Integer.valueOf(maps.get("id"));

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		TimeWarn task = new TimeWarn();
		task.setId(id);
		task.setCompanyId(staff.getCompanyId());

		try {
			warnTimerService.removeWarnTime(task);
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "成功");
		} catch (EduException e) {
			e.printStackTrace();
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "删除失败");
		}

		return model;
	}
}