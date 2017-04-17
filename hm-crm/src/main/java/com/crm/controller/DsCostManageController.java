package com.crm.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Cost;
import com.crm.model.CostLog;
import com.crm.model.Permission;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.CostService;
import com.crm.service.DictionaryService;
import com.crm.service.PermissionService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;

/**
 * 电商：渠道花费管理
 * 
 * @author JingChenglong 2016-11-04 09:31
 *
 */
@Controller
@RequestMapping("/cost")
public class DsCostManageController {

	@Autowired
	SourceService sourceService;/* 客资渠道 */

	@Autowired
	StaffService staffService;/* 职工管理 */

	@Autowired
	PermissionService permissionService; // 权限

	@Autowired
	DictionaryService dictionaryService;/* 数据字典 */

	@Autowired
	CostService costService;

	@Autowired
	CompanyService companyService;



	@RequestMapping(value = "ds_cost_index", method = RequestMethod.GET)
	public String dsCostIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request) throws EduException {

		int start = StringUtil.nullToIntZero(maps.get("start"));
		String momth=DateUtil.format(start, "yyyy-MM");
		int end = 0;
		if (start <= 0) {
			start = DateUtil.getStartOfMonth();
			momth=DateUtil.format(new Date(), "yyyy-MM");
		}
		if (end <= 0) {
			end = DateUtil.getEndOfMonth(start);
		}
		if (start > end) {
			int t = start;
			start = end;
			end = t;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return "login/404";
		}
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 网页系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 企业信息 --*/
		Company company = null;
		try {
			company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", new Company());
		}

		/*--获取该公司的全部渠道--*/
		List<Source> sourceList = sourceService.getSrcListByType(staff.getCompanyId(), Source.SRC_TYPE_DS);

		Map<String, Cost> costs = new HashMap<String, Cost>();
		
		List<BigDecimal> sourceTotal = new ArrayList<BigDecimal>();
		List<BigDecimal> dateTotal = new ArrayList<BigDecimal>();
		List<String> dateRange = DateUtil.iterate(start, end, new DateUtil.Collector<String>() {

			@Override
			public String collect(Date date) {
				return DateUtil.toStr(date);
			}

		});
		
		for (int i = 0; i < dateRange.size(); i++) {
			Cost costDate = new Cost();
			costDate.setCompanyId(company.getCompId());
			costDate.setCostTime(dateRange.get(i));
			costDate.setCreateTime(momth);
			List<Cost> costList = costService.listCostsByCostTime(costDate);
			
			BigDecimal sum = new BigDecimal("0.00");
			for (Cost cost : costList) {
				sum = sum.add(cost.getCost());
			}
			dateTotal.add(sum);
			
		}
		
		

		/*--获取渠道花费--*/
		for (int i = 0; i < sourceList.size(); i++) {
			HashMap<String, Object> hashMap = new HashMap<>();
			hashMap.put("companyId", company.getCompId());
			hashMap.put("srcId", sourceList.get(i).getSrcId());
			hashMap.put("start", dateRange.get(0));
			hashMap.put("end", dateRange.get(dateRange.size()-1));
			List<Cost> costList = costService.listCostsBySource(hashMap);
			BigDecimal sum = new BigDecimal("0.00");
			for (Cost cost : costList) {
				sum = sum.add(cost.getCost());
				costs.put(cost.getCostTime() + sourceList.get(i).getSrcId(), cost);
			}
			sourceTotal.add(sum);
		}
		
		
		model.addAttribute("dateTotal", dateTotal);
		model.addAttribute("sourceTotal", sourceTotal);
		model.addAttribute("dateRange", dateRange);
		model.addAttribute("costMap", costs);
		model.addAttribute("sourceList", sourceList);
		model.addAttribute("month", momth);
		return "/cost/ds_cost_index";
	}

	@RequestMapping(value = "add_cost")
	@ResponseBody
	public JSONObject addCost(@RequestParam Map<String, String> maps, HttpServletRequest request) throws EduException {
		JSONObject result = new JSONObject();
		result.put("success", false);
		result.put("msg", "参数有误，请刷新页面");
		
		try {
			
			Staff staff = CookieCompoment.getLoginUser(request);
			if (staff == null) {
				return result;
			}
			String cost = maps.get("cost");
			BigDecimal costDecimal = new BigDecimal(cost);
			String costTime = maps.get("costTime");
			Integer srcId = Integer.valueOf(maps.get("srcId"));
			
			Cost cc = new Cost();
			cc.setSrcId(srcId);
			cc.setCostTime(costTime);
			cc.setCompanyId(staff.getCompanyId());

			Cost old = costService.getCostByCondition(cc);
			if (old != null) {
				result.put("msg", "已经存在数值，请刷新页面");
				return result;
			}else {
				cc.setCost(costDecimal);
				cc.setId(StringUtil.getRandom());
				Integer num = costService.createCost(cc);
				if (num != null && num == 1) {
					CostLog costLog = new CostLog();
					costLog.setCompanyId(staff.getCompanyId());
					costLog.setCompanyName(staff.getCompanyName());
					costLog.setCostId(cc.getId());
					costLog.setOperaId(staff.getId());
					costLog.setOperaName(staff.getName());
					costLog.setOperaIp(WebUtils.getIP(request));
					costLog.setMemo("创建花费");
					costService.createCostLog(costLog);
					result.put("success", true);
					result.put("msg", "操作成功");
				} else {
					result.put("msg", "后台出错请联系技术人员");
				}
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
			
	}

		

	@RequestMapping(value = "edit_cost")
	@ResponseBody
	public JSONObject editCost(@RequestParam Map<String, String> maps, HttpServletRequest request) throws EduException {
		JSONObject result = new JSONObject();
		result.put("success", false);
		result.put("msg", "传入参数有误或者cookie为空");

		Staff staff = CookieCompoment.getLoginUser(request);
		String cost = maps.get("cost");
		BigDecimal costDecimal = null;
		try {
			costDecimal = new BigDecimal(cost);
		} catch (Exception e) {
			result.put("msg", "输入值错误");
			return result;
		}

		String costId = maps.get("costId");

		if (staff == null) {
			return result;
		}

		Cost cc = costService.getCostById(costId);

		if (cc == null) {
			result.put("msg", "没有获取到此条花费");
			return result;
		}

		cc.setCost(costDecimal);

		Integer num = costService.updateCost(cc);
		if (num != null && num == 1) {
			CostLog costLog = new CostLog();
			costLog.setCompanyId(staff.getCompanyId());
			costLog.setCompanyName(staff.getCompanyName());
			costLog.setCostId(cc.getId());
			costLog.setOperaId(staff.getId());
			costLog.setOperaName(staff.getName());
			costLog.setOperaIp(WebUtils.getIP(request));
			costLog.setMemo("修改花费花费金额为：" + costDecimal);
			costService.createCostLog(costLog);

			result.put("success", true);
			result.put("msg", "操作成功");
		} else {
			result.put("msg", "后台出错请联系技术人员");
		}

		return result;
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
		return map;
	}
}