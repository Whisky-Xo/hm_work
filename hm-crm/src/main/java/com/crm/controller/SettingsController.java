package com.crm.controller;

import java.util.Date;
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

import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Permission;
import com.crm.model.Shop;
import com.crm.model.SourceType;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.PermissionService;
import com.crm.service.ShopService;
import com.crm.service.SourceTypeService;
import com.crm.service.StaffService;

@RequestMapping(value = "settings")
@Controller
public class SettingsController {

	@Autowired
	CompanyService companyService;
	@Autowired
	ShopService shopService;
	@Autowired
	private StaffService staffService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private SourceTypeService sourceTypeService;

	private static final Company QIEIN = new Company();

	/** -- 1:人员调配 -- **/
	@RequestMapping(value = "/staff_setting_index", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String goStaffSettingIndex(Model model, HttpServletRequest request, HttpServletResponse response)
			throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 获取当前职工所在部门及其子部门下所有职工信息 --*/
		List<Staff> staffList = staffService.getStaffListByDeptId(staff.getDeptId(), staff.getCompanyId());

		model.addAttribute("staffList", staffList);
		model.addAttribute("staff", staff);
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		return "/kz_setting/staff_setting_index";
	}

	/** -- 2.渠道调配 -- **/
	@RequestMapping(value = "/source_setting_index", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String sourceSettingIndex(Model model, HttpServletRequest request, HttpServletResponse response)
			throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 企业渠道信息集合 --*/
		List<SourceType> srcTypeList = sourceTypeService.getSrcListDetailByCompId(staff.getCompanyId());

		model.addAttribute("srcTypeList", srcTypeList);
		model.addAttribute("staff", staff);
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		return "/kz_setting/source_setting_index";
	}

	@RequestMapping(value = "koubei", method = RequestMethod.GET)
	public String koubei(HttpServletRequest request, Model model) {
		// Staff staff = CookieCompoment.getLoginUser(request);
		// int companyId = staff.getCompanyId();
		// Company company = companyService.getById(companyId);
		// String banner =
		// companyService.getCompanyBanner(company.getMerchantPid());
		// if (banner == null)
		// banner = "";
		// model.addAttribute("banner", banner);
		// model.addAttribute("companyName", company.getWebSite());
		return "settings/koubei";
	}

	@RequestMapping(value = "channel", method = RequestMethod.GET)
	public String source(HttpServletRequest request, Model model) {
		// Staff staff = CookieCompoment.getLoginUser(request);
		// int companyId = staff.getCompanyId();
		// Company company = companyService.getById(companyId);
		// List<Source> sources = sourceService.listSources(companyId);
		// int sum = sources.size();
		// String date = DateUtil.toStr(new Date());
		// model.addAttribute("sources", sources);
		// model.addAttribute("sum", sum);
		// model.addAttribute("date", date);
		// model.addAttribute("companyName", company.getWebSite());
		return "settings/channel";
	}

	@RequestMapping(value = "shopInfo", method = RequestMethod.GET)
	public String shopInfo(HttpServletRequest request, Model model) {
		Staff staff = CookieCompoment.getLoginUser(request);
		int companyId = staff.getCompanyId();
		try {
			Company company = companyService.getCompanyInfoById(companyId);

			List<Shop> shops = shopService.listOpeningShops(companyId);
			int sum = shops.size();
			String date = DateUtil.toStr(new Date());
			model.addAttribute("shops", shops);
			model.addAttribute("sum", sum);
			model.addAttribute("date", date);
			model.addAttribute("companyName", company.getWebSite());
			model.addAttribute("companyId", companyId);
		} catch (EduException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "settings/shopInfo";
	}

	/**
	 * 1：渠道管理首页：显示渠道类型集合及其下属渠道集合
	 * 
	 * @throws EduException
	 */

	/**
	 * 1：渠道调配首页：显示渠道类型集合及其下属渠道集合
	 * 
	 * @throws EduException
	 */
	@RequestMapping(value = "/go_source_setting_index", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String goSourceSettingIndex(Model model, HttpServletRequest request, HttpServletResponse response)
			throws EduException {

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("user", staff);

		/*-- 企业信息 --*/
		Company company = companyService.getCompanyInfoById(staff.getCompanyId());
		model.addAttribute("company", company);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		if (company == null) {
			model.addAttribute("error", "登入公司出错!");
			return "login/404";
		}

		/*-- 企业渠道信息集合 --*/
		List<SourceType> srcTypeList = sourceTypeService.getSrcListDetailByCompId(company.getCompId());
		// if(srcTypeList != null){
		// srcTypeList = new ArrayList<SourceType>();
		// SourceType srcType = new SourceType();
		// srcType.setCompanyId(staff.getCompanyId());
		// srcType.setTypeId(1);
		// srcType.setTypeName("网路渠道");
		// srcType.setIsShow(true);
		// Source src = new Source();
		// src.setSrcId(1);
		// src.setTypeId(1);
		// src.setSrcName("微博");
		// src.setCompanyId(staff.getCompanyId());
		// List<Source> srcList = new ArrayList<Source>();
		// srcList.add(src);
		// srcType.setSrcList(srcList);
		// srcTypeList.add(srcType);
		//
		// }
		List<Permission> permissionList = permissionService.getByPermissionByStaffId(staff.getId());
		Boolean salaryflag = false;
		for (Permission p : permissionList) {
			// System.out.println(p.getPermissionId());
			// 薪资管理 权限 permissionId为52
			if (p.getPermissionId() == 52 && p.getValue().equals("true")) {// 职员权限26
				salaryflag = true;
				break;
			}
		}
		model.addAttribute("salaryflag", salaryflag);
		model.addAttribute("srcTypeList", srcTypeList);

		return "/kz_setting/source_setting_index";
	}

	/**
	 * 更改职工的在线/离线状态
	 * 
	 * *** id (必传) 职工ID *** *** flag (状态) 在线1/离线0 ***
	 */
	@RequestMapping("do_change_staff_show")
	@ResponseBody
	public Model doChangeStaffShow(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request) {

		/*-- 参数提取 --*/
		Integer id = Integer.valueOf(maps.get("id"));
		String flag = maps.get("flag");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(id)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 如果是上线，追平职工今日推单额为同范围职工的最小数 --*/
		if ("1".equals(flag) && id != null) {
			// 获取职工集合今日推单最小数
			Integer num = staffService.getTodayNumLine(id, staff.getCompanyId());
			// 追平
			if (num != null) {
				staffService.holdLineStaffTodayNum(id, staff.getCompanyId(), num);
			}
		}

		/*-- 业务执行，状态修改 --*/
		staffService.changeStaffShowById(id, "1".equals(flag) ? 1 : 0);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "职工状态修改成功");
		return model;
	}

	@RequestMapping("do_change_staff_limit_num_day")
	@ResponseBody
	public Model doChangeStaffLimitNumDay(@RequestParam Map<String, String> maps, Model model,
			HttpServletRequest request) {

		/*-- 参数提取 --*/
		String id = maps.get("id");
		String num = maps.get("num");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(num)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 业务执行，状态修改 --*/
		staffService.changeStaffLimitNumDay(Integer.valueOf(id), Integer.valueOf(num));
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "日接单限额修改成功");

		return model;
	}

	/**
	 * 调整职工的日接单顺序
	 * 
	 * *** id (必传) 职工ID *** *** sort (必传) 日接单限额 ***
	 */
	@RequestMapping("do_change_staff_jd_sort_day")
	@ResponseBody
	public Model doChangeStaffJdSortDay(@RequestParam Map<String, String> maps, Model model,
			HttpServletRequest request) {

		/*-- 参数提取 --*/
		String id = maps.get("id");
		String sort = maps.get("sort");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(sort)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 业务执行，状态修改 --*/
		staffService.changeStaffJdSort(Integer.valueOf(id), Integer.valueOf(sort));
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "接单顺序调整成功");

		return model;
	}

	public boolean checkCompany(Staff staff, String companyName, Model model) throws EduException {
		int companyId = staff.getCompanyId();
		Company company = companyService.getCompanyInfoById(companyId);
		String logo = company.getLogo();
		if (company.getColor() == null || "".equals(company.getColor()))
			company.setColor("#ff5400");
		model.addAttribute("company", company);
		model.addAttribute("companyName", companyName);
		model.addAttribute("logo", logo);
		return companyName.equals(company.getCompName());
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

	/*-- 企业信息 --*/
	public Company getCompany(Integer companyId) {

		if (companyId == null || companyId == 0) {
			return QIEIN;
		}

		Company company = null;
		try {
			company = companyService.getCompanyInfoById(companyId);
		} catch (EduException e) {
			company = QIEIN;
			e.printStackTrace();
		}

		return company;
	}

	/*-- 获取职工权限列表 --*/
	public Map<String, String> pageControl(Integer staffId) {
		Map<String, String> map = new HashMap<String, String>();
		if (staffId == null || staffId == 0) {
			return map;
		}
		List<Permission> permissions = permissionService.getByPermissionByStaffId(staffId);
		for (Permission permission : permissions) {
			if ("true".equals(permission.getValue())) {
				map.put("P" + permission.getPermissionId(), permission.getValue());
			}
		}
		return map;
	}
}
