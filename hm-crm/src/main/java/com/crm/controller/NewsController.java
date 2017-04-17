package com.crm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.News;
import com.crm.model.Permission;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.NewsService;
import com.crm.service.PermissionService;
import com.crm.service.StaffService;

/**
 * 消息
 * 
 * @author JingChenglong 2016-10-11 14:54
 *
 */
@Controller
@RequestMapping("/news")
public class NewsController {

	@Autowired
	CompanyService companyService;/* 公司管理 */
	
	@Autowired
	PermissionService pmsService;/* 权限管理 */

	@Autowired
	private PermissionService permissionService; // 权限

	@Autowired
	NewsService newsService; /* 消息 */

	@Autowired
	StaffService staffService;/* 职工管理 */

	/**
	 * 1：消息主页
	 */
	@RequestMapping(value = "/news_index", method = RequestMethod.GET)
	public String newsIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限
		
		Company company = companyService.getCompanyInfoById(staff.getCompanyId());
		model.addAttribute("company", company);

		return "news/my_news";
	}

	/**
	 * 2：获取全部消息
	 * 
	 */
	@RequestMapping(value = "get_news_page", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getNewsPage(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject reply = new JSONObject();
		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		int startIndex = (currentPage-1)*pageSize;
		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 数据查询 --*/
		List<News> newsList = new ArrayList<>();
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("id", staff.getId());
		hashMap.put("companyId", staff.getCompanyId());
		hashMap.put("startIndex", startIndex);
		hashMap.put("pageSize", pageSize);
		
		try {
			newsList = newsService.getNewsPage(hashMap);
			reply.put("code", "100000");
			reply.put("msg", "操作成功");
			reply.put("newsList", newsList);
		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "操作失败");
		}

		// /*-- 分页信息 --*/
		Integer count = newsService.countNewsAll(staff);
		JSONObject pageInfo = new JSONObject();
		pageInfo.put("totalcount", count);// 总记录数
		pageInfo.put("totalpage", count/pageSize);// 总页数
		pageInfo.put("pagesize", pageSize);// 分页大小
		pageInfo.put("currentpage", currentPage);// 当前页码
		reply.put("pageInfo", pageInfo);
		return reply;
	}

	/**
	 * 3：获取全部未读消息
	 * 
	 */
	@RequestMapping(value = "get_news_no_read_all", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getNewsNoReadAll(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject reply = new JSONObject();
		
		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		/*-- 业务执行 --*/
		try {
			List<News> newsList = newsService.getNewsNoReadAll(staff);
			reply.put("code", "100000");
			reply.put("msg", "操作成功");
			reply.put("newsList", newsList);
			reply.put("size", newsList.size());

		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "操作失败");
		}

		return reply;
	}

	/**
	 * 4：消息标记为已读
	 * 
	 * *** newsIds		(必传)	消息ID（多个时用逗号分隔）		***
	 */
	@RequestMapping(value = "set_news_be_read", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject setNewsBeRead(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject reply = new JSONObject();
		/*-- 参数提取 --*/
		String newsIds = maps.get("newsIds");

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(newsIds)) {
			reply.put("code", "999999");
			reply.put("msg", "参数不完整或格式错误");
			return reply;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装 --*/
		String[] idsArr = newsIds.split(",");
		List<News> newsList = new ArrayList<News>();
		for (int i = 0; i < idsArr.length; i++) {
			News news = new News();
			news.setId(Integer.valueOf(idsArr[i]));
			news.setCompanyId(staff.getCompanyId());
			news.setStaffId(staff.getId());
			newsList.add(news);
		}

		/*-- 业务执行 --*/
		try {
			newsService.doUpdateNewsBeRead(newsList);
			reply.put("code", "100000");
			reply.put("msg", "操作成功");
		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "操作失败");
		}

		return reply;
	}

	public Map<String, String> pageControl(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Staff staff = CookieCompoment.getLoginUser(request);
		int staffId = staff.getId();
		List<Permission> permissions = permissionService.getByPermissionByStaffId(staffId);
		for (Permission permission : permissions) {
			if ("true".equals(permission.getValue())) {
				map.put("P" + permission.getPermissionId(), permission.getValue());
			}
		}
		// System.out.println(map);
		return map;
	}
}