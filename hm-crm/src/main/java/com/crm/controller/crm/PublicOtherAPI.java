package com.crm.controller.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crm.api.CrmBaseApi;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.MobileLocationUtil;
import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.service.ClientInfoService;
import com.crm.service.CompanyService;
import com.crm.service.DeptService;
import com.crm.service.DictionaryService;
import com.crm.service.GroupService;
import com.crm.service.InvitationLogService;
import com.crm.service.PageConfService;
import com.crm.service.PermissionService;
import com.crm.service.ShopService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

/**
 * CRM3.0北斗新星：页面使用数据加载ajax接口
 * 
 * @author JingChenglong 2017-02-14 10:32
 *
 */
@Controller
@RequestMapping("/client")
public class PublicOtherAPI {

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */
	@Autowired
	SourceService sourceService;/* 客资渠道 */
	@Autowired
	StatusService statusService;/* 客资状态 */
	@Autowired
	PermissionService pmsService;/* 权限管理 */
	@Autowired
	StaffService staffService;/* 职工管理 */
	@Autowired
	GroupService groupService;/* 小组管理 */
	@Autowired
	CompanyService companyService;/* 公司管理 */
	@Autowired
	ClientInfoService clientInfoService;// 客资
	@Autowired
	PermissionService permissionService; // 权限
	@Autowired
	DictionaryService dictionaryService;/* 数据字典 */
	@Autowired
	DeptService deptService;/* 数据字典 */
	@Autowired
	InvitationLogService invitationLogService;/* 邀约记录 */
	@Autowired
	ShopService shopService;/* 门店 */
	@Autowired
	PageConfService pageConfService;/* 企业页面配置 */

	private static final Company QIEIN = new Company();
	private static final List<Source> NO_SOURCE = new ArrayList<Source>();
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/** -- 获取渠道 -- **/
	@RequestMapping(value = "get_sources")
	@ResponseBody
	public Model getSource(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		String roleType = StringUtil.nullToStrTrim(maps.get("role_type").toLowerCase());

		if (StringUtil.isEmpty(roleType)) {
			// TODO 参数捕获错误
		}
		List<Source> sourceds = NO_SOURCE;
		if (roleType.startsWith(Source.DS_PREFIX)) {
			// 电商渠道
			sourceds = getSrcList(staff, Source.SRC_TYPE_DS);
		} else if (roleType.startsWith(Source.ZJS_PREFIX)) {
			// 转介绍渠道
			sourceds = getSrcList(staff, Source.SRC_TYPE_INTRODUCE);
		} else if (roleType.startsWith(Source.MS_PREFIX)) {
			// 门店查看渠道
		} else {
			// 其它
		}

		model.addAttribute("sources", sourceds);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/** -- 获取推广邀约人员及门市 -- **/
	@RequestMapping(value = "get_staffs")
	@ResponseBody
	public Model getStaffs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);

		String roleType = StringUtil.nullToStrTrim(maps.get("role_type")).toLowerCase();

		if (StringUtil.isEmpty(roleType)) {
			// TODO 参数捕获错误
		}

		List<Staff> staffs = null;
		if (Staff.ROLE_DSCJ.equals(roleType)) {
			// 电商采集员
			staffs = getSrcRelaStaffs(Staff.CJ_SUFFIX, Source.SRC_TYPE_DS, String.valueOf(staff.getCompanyId()));
		} else if (Staff.ROLE_DSYY.equals(roleType)) {
			// 电商邀约员
			staffs = getSrcRelaStaffs(Staff.YY_SUFFIX, Source.SRC_TYPE_DS, String.valueOf(staff.getCompanyId()));
		} else if (Staff.ROLE_ZJSYY.equals(roleType)) {
			// 转介绍邀约员
			staffs = getSrcRelaStaffs(Staff.YY_SUFFIX, Source.SRC_TYPE_INTRODUCE, String.valueOf(staff.getCompanyId()));
		} else {
			// 其它
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "无需切换");
			return model;
		}

		model.addAttribute("staffs", staffs);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/** -- 根据手机号码获取归属地城市信息 -- **/
	@RequestMapping(value = "get_city_by_phone")
	@ResponseBody
	public Model getCityByPhone(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		String phone = maps.get("phone");

		String address = "";

		if (StringUtil.isNotEmpty(phone)) {
			address = MobileLocationUtil.getCityStr(phone);
		}

		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		model.addAttribute("address", address);
		return model;
	}

	// 获取页面渠道下拉菜单
	public List<Source> getSrcList(Staff staff, String srcType) {

		if (staff == null || srcType == null || "".equals(srcType)) {
			return NO_SOURCE;
		}

		List<Source> srcList = null;
		try {
			srcList = sourceService.getSrcListByType(staff.getCompanyId(), srcType);

			if (srcList == null) {
				srcList = NO_SOURCE;
			}

		} catch (EduException e) {
			srcList = NO_SOURCE;
			e.printStackTrace();
		}

		return srcList;
	}

	// 获取渠道参与人列表
	public List<Staff> getSrcRelaStaffs(String relaType, String srcType, String companyId) {

		if (StringUtil.isEmpty(relaType) || StringUtil.isEmpty(srcType) || StringUtil.isEmpty(companyId)) {
			return null;
		}

		return staffService.getSrcRelaStaffs(relaType, srcType, companyId);
	}

	/*-- 获取登录人详细信息 --*/
	public Staff getStaffDetail(HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return null;
		}
		return staffService.getStaffInfoById(staff.getId());
	}
}