package com.crm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.CharsetUtil;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.StringUtil;
import com.crm.common.util.WebUtils;
import com.crm.exception.BizException;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Department;
import com.crm.model.Dept;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.DepartmentService;
import com.crm.service.DeptService;
import com.crm.service.StaffService;



/**
 * 部门管理
 * 
 * @author JingChenglong 2016-09-09 18:14
 *
 */
@Controller
@RequestMapping("/department")
public class DepartmentController {

	@Autowired
	DepartmentService departMentService;/* 部门操作 */
	
	@Autowired
	DeptService deptService;/* 部门操作 */
	
	@Autowired
	private StaffService staffService;/*-- 职员 --*/

	@Autowired
	private CompanyService companyService;/*-- 企业 --*/

	
	
	/**
	 * 新建部门
	 */
	@RequestMapping(value = "/createDept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model createDept(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String deptName = maps.get("deptName");
		String parentId = maps.get("parentId");
		String timeStart = maps.get("timeStart");
		String timeEnd = maps.get("timeEnd");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(parentId) || StringUtils.isEmpty(timeStart) || StringUtils.isEmpty(timeEnd)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "请填写部门信息");
			return model;
		} else {
			timeStart = timeStart.replace(":", "");
			timeEnd = timeEnd.replace(":", "");
		}

		Staff staff = CookieCompoment.getLoginUser(request);
		int companyId = staff.getCompanyId();
		String ip = WebUtils.getIP(request);
		try {
			Dept dept = new Dept();
			dept.setCompanyId(companyId);
			dept.setDeptName(deptName);
			dept.setParentId(parentId);
			dept.setIsDel(false);
			dept.setIsShow(true);
			dept.setCreateIp(ip);
			dept.setUpdateIp(ip);
			dept.setTimeStart(Integer.valueOf(timeStart));
			dept.setTimeEnd(Integer.valueOf(timeEnd));
			dept = deptService.createDept(dept);
			dept.setDeptId(parentId + "-" + dept.getId());
			deptService.updateDept(dept);

			model.addAttribute("code", 100);
			model.addAttribute("msg", "新建部门成功!");
		} catch (Exception e) {
			model.addAttribute("code", 200);
			model.addAttribute("msg", "新建部门失败!");
		}
		return model;
	}
	
	
	
	
	@RequestMapping("updateDept")
	@ResponseBody
	public Model updateDept(@RequestParam(value = "deptId", defaultValue = "") String deptId, String parentId,
			String deptName, int timeStart, int timeEnd, Model model, HttpServletRequest request) {
		Staff staff_ = CookieCompoment.getLoginUser(request);
		try {
			if ("".equals(deptId)) {
				throw new BizException(200, "非法参数");
			}
			/*-- 修改部门信息 --*/
			Dept dept = deptService.getByDeptId(deptId);
			String deptId_ = "0".equals(parentId)?dept.getId()+"":parentId + "-" + dept.getId();
			dept.setDeptName(deptName);
			dept.setParentId(parentId);
			dept.setDeptId(deptId_);
			dept.setTimeStart(timeStart);
			dept.setTimeEnd(timeEnd);
			deptService.updateDept(dept);
			
			/*-- 修改该部门下子部门信息和附属员工信息 --*/
			List<Dept> depts = deptService.listSubdeptsByDeptId(deptId+"-",staff_.getCompanyId());
			for (Dept _dept : depts){
				String _deptId = _dept.getDeptId().replace(deptId, deptId_);
				String _parentId = _dept.getParentId().replace(deptId, deptId_);
				_dept.setParentId(_parentId);
				_dept.setDeptId(_deptId);
				deptService.updateDept(_dept);
			}
			List<Staff> staffs = staffService.getByDeptId(deptId+"-",deptId, staff_.getCompanyId());
			for (Staff staff : staffs){
				String _deptId = staff.getDeptId().replace(deptId, deptId_);
				staff.setDeptId(_deptId);
				staffService.editStaffById(staff);
			}
			
			model.addAttribute("code", 100);
			model.addAttribute("msg", "编辑部门成功!");
		} catch (BizException e) {
			model.addAttribute("code", e.getCode());
			model.addAttribute("msg", e.getMessage());
		}
		return model;
	}

	
	
	
	
	
	@RequestMapping("getDepts")
	@ResponseBody
	public Model getDepts(Model model, HttpServletRequest request) throws EduException {
		Staff staff = CookieCompoment.getLoginUser(request);
		int companyId = staff.getCompanyId();
		
		Company company = companyService.getCompanyInfoById(companyId);
		List<Dept> depts = deptService.listOpeningDepts(companyId);
		List<Map<String, Object>> deptMaps = new ArrayList<Map<String, Object>>();
		List<Staff> staffs = staffService.listStaffByCompanyId(companyId);
		for (Dept dept : depts) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pid", dept.getParentId());
			String pname = "0".equals(dept.getParentId())?company.getCompName():dept.getParentName();
			map.put("pname",pname);
			map.put("nodeId", dept.getDeptId());
			map.put("name", dept.getDeptName());
			map.put("id", dept.getId());
			map.put("timeStart", dept.getTimeStart());
			map.put("timeEnd", dept.getTimeEnd());
			//获取当前部门及其子部门的成员的总数
			int count = 0;
			//获取当前部门的成员
			List<Staff> staffs_ = new ArrayList<Staff>();

			for (Staff staff_ : staffs) {
				if(staff_.getDeptId().indexOf(dept.getDeptId()+"-")!= -1){
					count++;
				}else if(staff_.getDeptId().equals(dept.getDeptId())){
					count++;
					staffs_.add(staff_);
				}
			}
			map.put("count",count);
//			List<Staff> staffs_ = staffService.getByDeptId("----",dept.getDeptId(),companyId);

			map.put("staffs", staffs_);
			deptMaps.add(map);
		}
		List<Map<String, Object>> deptHashMaps = getTree(deptMaps);
		
		 
	    model.addAttribute("deptMaps", deptHashMaps);
		return model;
	}
	
	
	/*- 列出开启的部门-*/
	@RequestMapping("listOpenDepts")
	@ResponseBody
	public Model listOpenDepts(@RequestParam(value = "deptId", defaultValue = "") String deptId,Model model, HttpServletRequest request) throws EduException {
		Staff staff = CookieCompoment.getLoginUser(request);
		int companyId = staff.getCompanyId();
		Company company = companyService.getCompanyInfoById(companyId);
		if("".equals(deptId)){
			deptId = null;
		}
		List<Dept> depts = deptService.listDeptsExceptDeptId(companyId,deptId+"-");
		Dept dept = new Dept();
		dept.setDeptId("0");
		dept.setDeptName(company.getCompName());
		depts.add(0, dept);
		model.addAttribute("depts",depts);
		return model;
	}
	
	
	@RequestMapping("getByDeptId")
	@ResponseBody
	public Model addStaffByDeptId(@RequestParam(value = "deptId", defaultValue = "0") String deptId, Model model,
			HttpServletRequest request) throws EduException {
		Staff staff = CookieCompoment.getLoginUser(request);
		int companyId = staff.getCompanyId();
		List<Staff> staffs = staffService.getByDeptId("---",deptId, companyId);
		Dept dept = null;
		if ("0".equals(deptId)) {
			Company company = companyService.getCompanyInfoById(companyId);
			dept = new Dept();
			dept.setCompanyId(companyId);
			dept.setDeptName(company.getCompName());
			dept.setCount(staffs.size());
		} else {
			dept = deptService.getByDeptId(deptId);
		}
		List<Dept> subdepts = deptService.getByParentId(deptId, companyId);

		model.addAttribute("subdepts", subdepts);
		model.addAttribute("dept", dept);
		model.addAttribute("staffs", staffs);
		return model;
	}
	
	
	
	
	public List<Map<String, Object>> getTree(List<Map<String, Object>> list) {
		List<Map<String, Object>> topList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> node : list) {
			if ("0".equals(node.get("pid"))) {
				topList.add(node);
			} else {
				nodeList.add(node);
			}
		}

		for (Map<String, Object> top : topList) {
			top.put("subNodes", getTreeNode(top, nodeList));
			returnList.add(top);
		}
		return returnList;
	}
	
	
	
	public List<Map<String, Object>> getTreeNode(Map<String, Object> parent, List<Map<String, Object>> nodeList) {
		List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> node : nodeList) {
			if (node.get("pid").equals(parent.get("nodeId"))) {
				childList.add(node);
			} else {
				tempList.add(node);
			}
		}

		if (childList.size() > 0) {
			for (Map<String, Object> child : childList) {
				if (tempList.size() > 0) {
					child.put("subNodes", getTreeNode(child, tempList));
				}
				returnList.add(child);
			}
		}
		return returnList;
	}
	
	
}