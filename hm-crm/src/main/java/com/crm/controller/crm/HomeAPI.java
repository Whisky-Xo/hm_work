package com.crm.controller.crm;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.Constants;
import com.crm.api.constant.DicConts;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.ClientLogInfo;
import com.crm.model.ClientYaoYueLog;
import com.crm.model.Company;
import com.crm.model.Dictionary;
import com.crm.model.PageConf;
import com.crm.model.Permission;
import com.crm.model.Shop;
import com.crm.model.ShopMeetLog;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.DictionaryService;
import com.crm.service.PageConfService;
import com.crm.service.PermissionService;
import com.crm.service.ShopService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;

/**
 * 首页
 * 
 * @author JingChenglong 2017-01-17 18:24
 *
 */
@Controller
@RequestMapping("/client")
public class HomeAPI {

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */
	@Autowired
	SourceService sourceService;/* 客资渠道 */
	@Autowired
	DictionaryService dictionaryService;/* 数据字典 */
	@Autowired
	StaffService staffService;/* 职工管理 */
	@Autowired
	PermissionService permissionService; /* 权限 */
	@Autowired
	CompanyService companyService;/* 公司管理 */
	@Autowired
	PageConfService pageConfService;/* 企业页面配置 */
	@Autowired
	ShopService shopService; /* 门店 */

	private static final Company QIEIN = new Company();
	private static final List<Source> NO_SOURCE = new ArrayList<Source>();
	private static final List<Dictionary> NO_DIC = new ArrayList<Dictionary>();
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/** -- 系统首页 -- **/
	@RequestMapping(value = "/home")
	public String home(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 职工信息 --*/
		Staff staff = getStaffDetail(request);

		/*-- 页面权限 --*/
		Map<String, String> map = pageControl(staff.getId());

		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("staff", staff);
		model.addAttribute("pageMap", map);// 页面权限

		// String userAgent = request.getHeader("user-agent");
		// // 权限层校验
		// if (!"bosmia".equals(userAgent)) {
		// return "beidou3/error/limit";
		// }

		if (Boolean.valueOf(map.get("P21"))) {
			// 电商客服
			model.addAttribute("roleName", "电商客服");
			model.addAttribute("role", Staff.ROLE_DSCJ);
			model.addAttribute("timeTypeNum", "0");
		} else if (Boolean.valueOf(map.get("P25"))) {
			// 电商邀约
			model.addAttribute("roleName", "网络顾问");
			model.addAttribute("role", Staff.ROLE_DSYY);
			model.addAttribute("timeTypeNum", "1");
		} else if (Boolean.valueOf(map.get("P22"))) {
			// 转介绍客服
			model.addAttribute("roleName", "转介绍客服");
			model.addAttribute("role", Staff.ROLE_ZJSYY);
			model.addAttribute("timeTypeNum", "1");
		} else if (Boolean.valueOf(map.get("P23"))) {
			// 门市
			model.addAttribute("roleName", "门店中心");
			model.addAttribute("role", Staff.ROLE_MSJD);
			model.addAttribute("timeTypeNum", "3");
		}

		/*-- 页面自定义配置 --*/
		List<PageConf> pageConfList = pageConfService.getPageConfListByCompany(Constants.COMP_ID_COMMON);
		for (PageConf page : pageConfList) {
			model.addAttribute("title_" + page.getAction(), page.getTitleTxt());
		}

		model.addAttribute("now", TimeUtils.getCurrentymd());
		List<Source> sourceds = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources_ds", sourceds);

		List<Source> sourceMyCj = sourceService.getSrcListDsByStaffCj(staff.getId());
		model.addAttribute("sources_ds_my", sourceMyCj);

		return "beidou3/home";
	}

	/** -- 客资详情弹框页-- **/
	@RequestMapping(value = "/detail")
	public String detail(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		String kzId = maps.get("kzid");
		if (StringUtil.isEmpty(kzId)) {
			// TODO 未捕获参数
		}
		String roleType = StringUtil.nullToStrTrim(maps.get("role"));

		Staff staff = getStaffDetail(request);

		/*-- 获取客资信息 --*/
		try {
			ClientInfo clientInfo = null;
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("id", kzId);
			String getRstStr = crmBaseApi.doService(reqContent, "clientGetById");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(getRstStr);

			if ("100000".equals(jsInfo.getString("code"))) {
				JSONObject jsContent = JsonFmtUtil.strContentToJsonObj(getRstStr);
				clientInfo = JsonFmtUtil.jsonToClientInfo(jsContent.getJSONObject("clientInfo"));
				List<ClientLogInfo> logList = JsonFmtUtil.jsonArrToClientLogInfo(jsContent.getJSONArray("logList"));
				List<ClientYaoYueLog> yyLogList = JsonFmtUtil
						.jsonArrToClientYaoYueLogInfo(jsContent.getJSONArray("yyLogList"));
				List<ShopMeetLog> qtLogList = JsonFmtUtil
						.jsonArrToClientShopMeetLogInfo(jsContent.getJSONArray("qtLogList"));

				model.addAttribute("info", clientInfo);
				model.addAttribute("logs", logList);
				model.addAttribute("yylogs", yyLogList);
				model.addAttribute("qtlog", qtLogList);
			}
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		// 电商渠道集合
		List<Source> sourceds = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources_ds", sourceds);

		// 无效原因集合
		List<Dictionary> invalidRsnList = getInvalidReasonList(staff.getCompanyId());
		model.addAttribute("invalidreasons", invalidRsnList);

		// 客资流失原因集合
		List<Dictionary> runOffRsnList = getRunOffReasonList(staff.getCompanyId());
		model.addAttribute("runoffreasons", runOffRsnList);

		// 邀约结果集
		List<Dictionary> traceStatus = getTraceStatus(staff.getCompanyId());
		model.addAttribute("tracestatus", traceStatus);

		// 门店下拉菜单
		List<Shop> shopList = shopService.listShops(staff.getCompanyId());
		model.addAttribute("shops", shopList);

		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("staff", staff);
		model.addAttribute("role", roleType);
		return "beidou3/detail";
	}

	/*-- 获取登录人详细信息 --*/
	public Staff getStaffDetail(HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return null;
		}
		return staffService.getStaffInfoById(staff.getId());
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

	// 获取页面渠道下拉菜单
	public List<Source> getSrcList(Staff staff, String srcType) {

		if (staff == null || srcType == null || "".equals(srcType)) {
			return NO_SOURCE;
		}

		List<Source> srcList = null;
		try {
			srcList = sourceService.getSrcListByType(staff.getCompanyId(), srcType);

			// if (staff.getIsChief()) {
			// // 如果是主管，获取指定渠道类型的所有渠道
			// srcList = sourceService.getSrcListByType(staff.getCompanyId(),
			// srcType);
			// } else {
			// // 非主管，只显示自己所负责的渠道
			// srcList = sourceService.getSrcListDsByStaffCj(staff.getId());
			// }

			if (srcList == null) {
				srcList = NO_SOURCE;
			}
		} catch (EduException e) {
			srcList = NO_SOURCE;
			e.printStackTrace();
		}

		return srcList;
	}

	// 无效客资原因下拉菜单
	public List<Dictionary> getInvalidReasonList(Integer companyId) {

		if (companyId == null || companyId == 0) {
			return null;
		}

		List<Dictionary> invalidRsnList = null;

		try {
			invalidRsnList = dictionaryService.getDictionaryListByType(companyId, DicConts.INVALID_REASON);
		} catch (EduException e) {
			invalidRsnList = NO_DIC;
			e.printStackTrace();
		}

		return invalidRsnList;
	}

	// 客资流失原因下拉菜单
	public List<Dictionary> getRunOffReasonList(Integer companyId) {

		if (companyId == null || companyId == 0) {
			return null;
		}

		List<Dictionary> runOffRsnList = null;

		try {
			runOffRsnList = dictionaryService.getDictionaryListByType(companyId, DicConts.RUN_OFF_REASON);
		} catch (EduException e) {
			runOffRsnList = NO_DIC;
			e.printStackTrace();
		}

		return runOffRsnList;
	}

	// 下次跟踪状态结果街
	public List<Dictionary> getTraceStatus(Integer companyId) {

		if (companyId == null || companyId == 0) {
			return null;
		}

		List<Dictionary> traceStatus = null;

		try {
			traceStatus = dictionaryService.getDictionaryListByType(companyId, DicConts.DIC_TYPE_YYRST_DS);
		} catch (EduException e) {
			traceStatus = NO_DIC;
			e.printStackTrace();
		}

		return traceStatus;
	}
}