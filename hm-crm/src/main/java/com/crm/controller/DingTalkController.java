package com.crm.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.DicConts;
import com.crm.api.constant.PageConfConst;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.AuthHelper;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.HttpHelper;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.ServiceHelper;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.common.util.ding.DingTalkEncryptException;
import com.crm.common.util.ding.DingTalkEncryptor;
import com.crm.common.util.ding.Env;
import com.crm.common.util.ding.FileUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.ClientInfoStatis;
import com.crm.model.ClientLogInfo;
import com.crm.model.ClientYaoYueLog;
import com.crm.model.Company;
import com.crm.model.Dept;
import com.crm.model.Dictionary;
import com.crm.model.InnnerAnalyze;
import com.crm.model.PageConf;
import com.crm.model.Permission;
import com.crm.model.Shop;
import com.crm.model.ShopMeetLog;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.model.Status;
import com.crm.service.ClientInfoService;
import com.crm.service.CompanyService;
import com.crm.service.DeptService;
import com.crm.service.DictionaryService;
import com.crm.service.DingAppService;
import com.crm.service.GroupService;
import com.crm.service.PageConfService;
import com.crm.service.PermissionService;
import com.crm.service.ShopService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;
import com.dingtalk.open.client.api.model.isv.CorpAuthSuiteCode;

/**
 * 钉钉：H5
 * 
 * @author JingChenglong 2016-10-10 14：13
 *
 */
@Controller
@RequestMapping("/ding")
public class DingTalkController {

	String preurl = null;

	@Value("${DING.HOST}")
	private String host;

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
	DictionaryService dictionaryService;/* 数据字典 */
	@Autowired
	PermissionService permissionService;/* 权限管理 */
	@Autowired
	PageConfService pageConfService;/* 企业页面配置 */
	@Autowired
	DingAppService dingAppService;
	@Autowired
	DeptService deptService; /* 部门 */
	@Autowired
	ShopService shopService; /* 门店 */

	private static final Company QIEIN = new Company();
	private static final List<InnnerAnalyze> NO_ANALYZE = new ArrayList<InnnerAnalyze>();
	private static final List<Dictionary> NO_DIC = new ArrayList<Dictionary>();
	private static final List<Source> NO_SOURCE = new ArrayList<Source>();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/** -- 1：客资提报 -- **/
	@RequestMapping(value = "/ding_kz_add_index", method = RequestMethod.GET)
	public String dingKzAddIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		List<Source> sourceList = sourceService.getSrcListInnerTypeByStaffCj(staff.getId());

		model.addAttribute("user", staff);
		model.addAttribute("sources", sourceList);
		return "mobile/keziadd/inner_referred";
	}

	/** -- 2：我的提报 -- **/
	@RequestMapping(value = "ding_my_kz", method = RequestMethod.GET)
	public String dingMyKz(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
			return "mobile/skip/kz_add_exist_fail";
		}
		model.addAttribute("staff", staff);

		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "999" : maps.get("size"));

		/*-- 内部转介绍客资渠道集合 --*/
		List<Source> sourceList = sourceService.getSrcListByType(staff.getCompanyId(),
				Source.SRC_TYPE_INNNER_INTRODUCE);

		String sourceIds = "";
		for (Source sc : sourceList) {
			sourceIds += sc.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}

		// 本月客资信息
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientListMonth = null;
		reqContent = new HashMap<String, Object>();
		try {
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("collectorids", staff.getId());
			reqContent.put("sortname1", "UPDATETIME");// 根据最后更新时间排序
			reqContent.put("sorttype1", "DESC");// 最后更新时间倒序
			reqContent.put("sourceids", sourceIds);// 渠道限定为内部转介绍渠道
			reqContent.put("createtimestart", TimeUtils.getStartDayOfMonthDay() + " 00:00:00");
			reqContent.put("createtimeend", TimeUtils.getEndDayOfMonthDay() + " 23:59:59");
			reqContent.put("pagesize", 999);
			reqContent.put("currentpage", currentPage);
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);

			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientListMonth = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}

		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			clientListMonth = new ArrayList<ClientInfo>();
		}

		model.addAttribute("clients", clientListMonth);

		// 历史客资信息
		List<ClientInfo> historyClientList = null;
		try {
			reqContent.clear();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("collectorids", staff.getId());
			reqContent.put("sortname1", "UPDATETIME");// 根据最后更新时间排序
			reqContent.put("sorttype1", "DESC");// 最后更新时间倒序
			reqContent.put("sourceids", sourceIds);// 渠道限定为内部转介绍渠道
			reqContent.put("createtimeend", TimeUtils.getStartDayOfMonthDay() + " 00:00:00");
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);

			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				historyClientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}

		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			historyClientList = new ArrayList<ClientInfo>();
		}

		// 客资统计
		reqContent.clear();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("collectorids", staff.getId().toString());
		reqContent.put("sourceids", sourceIds);// 渠道限定为内部转介绍渠道
		String clientStatisticsRstStr;
		ClientInfoStatis clientInfoStatis = null;
		try {
			clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsCj");
			JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);

			if (jsStatistics != null) {
				clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
						ClientInfoStatis.class);
			}

		} catch (Exception e) {
			e.printStackTrace();
			clientInfoStatis = new ClientInfoStatis();
			clientInfoStatis.setNumCollAllHistory(0);
			clientInfoStatis.setCollSuccessNumHistory(0);
			clientInfoStatis.setCollSuccessRateHistory("0");
			clientInfoStatis.setNumCollValidMonth(0);
			clientInfoStatis.setCollSuccessNumMonth(0);
			clientInfoStatis.setCollSuccessRateMonth("0");
		}

		model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果
		model.addAttribute("historyClients", historyClientList);

		return "mobile/mykz/my_introduce";
	}

	/** -- 3：客资邀约--电商 -- **/
	@RequestMapping(value = "/ding_kz_yy_ds_index", method = RequestMethod.GET)
	public String dingKzYyDsIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.MOBOLE_DS_YY);

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("title", pageConf.getTitleTxt());
		model.addAttribute("deptName", staff.getDeptName());

		/*-- 权限判定，判定是否是电商邀约员 --*/
		List<Permission> permissions = permissionService.getByPermissionByStaffId(staff.getId());
		String role = "";
		for (Permission permission : permissions) {
			role += "true".equals(permission.getValue()) ? permission.getPermissionId() + "," : "";
		}
		if (!"".equals(role))
			role = role.substring(0, role.length() - 1);

		List<String> roles = Arrays.asList(role.split(","));
		if (!roles.contains("25")) {
			return "mobile/skip/pms_fail";
		}

		/*-- 客资渠道下拉菜单 --*/
		List<Source> srcList = null;
		if (staff.getIsChief()) {
			// 如果是主管，获取指定渠道类型的所有渠道
			srcList = sourceService.getSrcListByType(staff.getCompanyId(), Source.SRC_TYPE_DS);
		} else {
			// 非主管，只显示自己所负责的渠道
			srcList = sourceService.getSrcListByStaffYy(staff.getId());
		}
		model.addAttribute("sources", srcList);

		/*-- 客资状态下拉菜单 --*/
		List<Status> statusList = statusService.getStatusInfoAllList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		model.addAttribute("statuses", statusList);

		return "mobile/keziyy/yy_ds_index";
	}

	/** -- 4：客资邀约--转介绍 -- **/
	@RequestMapping(value = "/ding_kz_yy_zjs_index", method = RequestMethod.GET)
	public String dingKzYyZjsIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.MOBOLE_ZJS_YY);

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("title", pageConf.getTitleTxt());
		model.addAttribute("deptName", staff.getDeptName());

		/*-- 权限判定，判定是否是转介绍邀约员 --*/
		List<Permission> permissions = permissionService.getByPermissionByStaffId(staff.getId());
		String role = "";
		for (Permission permission : permissions) {
			role += "true".equals(permission.getValue()) ? permission.getPermissionId() + "," : "";
		}
		if (!"".equals(role))
			role = role.substring(0, role.length() - 1);

		List<String> roles = Arrays.asList(role.split(","));
		if (!roles.contains("22")) {
			return "mobile/skip/pms_fail";
		}

		/*-- 客资渠道下拉菜单 --*/
		List<Source> srcList = null;
		if (staff.getIsChief()) {
			// 如果是主管，获取指定渠道类型的所有渠道
			srcList = sourceService.getSrcListByType(staff.getCompanyId(), Source.SRC_TYPE_INTRODUCE);
		} else {
			// 非主管，只显示自己所负责的渠道
			srcList = sourceService.getSrcListByStaffYy(staff.getId());
		}
		model.addAttribute("sources", srcList);

		/*-- 客资状态下拉菜单 --*/
		List<Status> statusList = statusService.getStatusInfoAllList(staff.getCompanyId(), Status.STATUS_TYPE_ZJS);
		model.addAttribute("statuses", statusList);

		return "mobile/keziyy/yy_zjs_index";
	}

	/** 门店洽谈 **/
	@RequestMapping(value = "/ding_kz_shop_index", method = RequestMethod.GET)
	public String dingKzShopIndex(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.MOBOLE_SHOP_MEET);

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("title", pageConf.getTitleTxt());
		model.addAttribute("shopName", staff.getShopName());

		/*-- 权限判定，判定是否是门市 --*/
		List<Permission> permissions = permissionService.getByPermissionByStaffId(staff.getId());
		String role = "";
		for (Permission permission : permissions) {
			role += "true".equals(permission.getValue()) ? permission.getPermissionId() + "," : "";
		}
		if (!"".equals(role))
			role = role.substring(0, role.length() - 1);

		List<String> roles = Arrays.asList(role.split(","));
		if (!roles.contains("23")) {
			// TODO 非门市，权限不足页面
			return "mobile/skip/pms_fail";
		}

		return "mobile/kezishop/shop_index";
	}

	/** -- 销售分析 -- **/
	@RequestMapping(value = "/ding_sell_analysis", method = RequestMethod.GET)
	public String dingSellAnalysis(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		/*-- 门店信息 --*/
		List<Shop> shopList = shopService.listOpeningShops(staff.getCompanyId());
		model.addAttribute("shopList", shopList);

		model.addAttribute("pageMap", pageControl(staff.getId()));

		return "mobile/analyze/innner_intro_analyze2";
	}

	/** -- 销售排行榜-- **/
	@RequestMapping(value = "/ding_sell_ranking", method = RequestMethod.GET)
	public String dingSellRanking(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		String start = StringUtil.isNotEmpty(maps.get("start")) ? maps.get("start") : TimeUtils.getCurrentymd();
		String end = StringUtil.isNotEmpty(maps.get("end")) ? maps.get("end") : TimeUtils.getCurrentymd();
		String shopId = maps.get("shopid");
		String type = maps.get("type");

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		/*-- 门店信息 --*/
		List<Shop> shopList = shopService.listOpeningShops(staff.getCompanyId());
		model.addAttribute("shopList", shopList);

		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("shopid", shopId);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("type", type);
		return "mobile/analyze/ding_sell_ranking";
	}

	/**
	 * 5：电商录入
	 */
	@RequestMapping(value = "/ding_kz_add_index_ds", method = RequestMethod.GET)
	public String dingKzAddIndexDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("user", staff);

		/*-- 权限判定，判定是否是电商采集员 --*/
		List<Permission> permissions = permissionService.getByPermissionByStaffId(staff.getId());
		String role = "";
		for (Permission permission : permissions) {
			role += "true".equals(permission.getValue()) ? permission.getPermissionId() + "," : "";
		}
		if (!"".equals(role))
			role = role.substring(0, role.length() - 1);

		List<String> roles = Arrays.asList(role.split(","));
		if (!roles.contains("21")) {
			return "mobile/skip/pms_fail";
		}

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = null;
		if (staff.getIsChief()) { // 是否是主管
			// 如果是主管，获取指定渠道类型的所有渠道
			sourceList = sourceService.getSrcListByType(staff.getCompanyId(), Source.SRC_TYPE_DS);
		} else {
			// 非主管，只显示自己所负责的渠道
			sourceList = sourceService.getSrcListDsByStaffCj(staff.getId());
		}
		model.addAttribute("sources", sourceList);

		return "mobile/keziadd/inner_referred_ds";
	}

	/**
	 * 转介绍提报分析
	 */
	@RequestMapping(value = "/innner_intro_analyze", method = RequestMethod.GET)
	public String innnerIntroAnalyze(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 权限判定，只有主管可以进入 --*/
		if (!staff.getIsChief()) {
			return "mobile/skip/pms_fail";
		}

		String start = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String end = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String departId = maps.get("deptid");// 部门ID
		String staffIds = maps.get("staffids");// 职员ID

		if (StringUtils.isEmpty(departId)) {
			departId = staff.getDeptId();
		}
		/*-- 统计结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<InnnerAnalyze> analyzeList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");
		reqContent.put("deptid", departId);

		if (StringUtil.isNotEmpty(staffIds) && !"0".equals(staffIds)) {
			reqContent.put("staffid", staffIds);
		}

		try {
			String analyzeRstStr = crmBaseApi.doService(reqContent, "doInnerTbAnalyze");
			JSONObject js = JsonFmtUtil.strToJsonObj(analyzeRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				analyzeList = JsonFmtUtil.jsonArrToAnalyze(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			analyzeList = NO_ANALYZE;
		}

		model.addAttribute("analyzes", analyzeList);

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getStaffListByDeptIdIgnoDel(
				StringUtil.isNotEmpty(departId) && !"0".equals(departId) ? departId : staff.getDeptId() + "-",
				staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 页面搜索项 --*/
		model.addAttribute("staffId", staffIds);
		model.addAttribute("deptId", departId);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		return "mobile/analyze/innner_intro_analyze";
	}

	/** -- 提报客资邀约记录表单---电商 -- **/
	@RequestMapping(value = "/ding_add_appoint_log_skip_ds", method = RequestMethod.GET)
	public String dingAdappointLogSkipDs(@RequestParam Map<String, String> maps, Model model,
			HttpServletRequest request, HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");
		if (StringUtil.isEmpty(kzId)) {
			return "/mobile/skip/fail_404";
		}
		model.addAttribute("kzId", kzId);

		/*-- 网页系统时间 --*/
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 邀约结果集 --*/
		List<Dictionary> callRstList = null;
		try {
			callRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.DIC_TYPE_YYRST_DS);
		} catch (EduException e2) {
			callRstList = new ArrayList<Dictionary>();
		}
		model.addAttribute("callRstList", callRstList);

		/*-- 门店信息 --*/
		List<Shop> shopList = shopService.listOpeningShops(staff.getCompanyId());
		model.addAttribute("shopList", shopList);

		return "mobile/keziyy/kz_yy_rst_form";
	}

	/** -- 提报客资邀约记录表单---转介绍 -- **/
	@RequestMapping(value = "/ding_add_appoint_log_skip_zjs", method = RequestMethod.GET)
	public String dingAdappointLogSkipZjs(@RequestParam Map<String, String> maps, Model model,
			HttpServletRequest request, HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");
		if (StringUtil.isEmpty(kzId)) {
			return "/mobile/skip/fail_404";
		}
		model.addAttribute("kzId", kzId);

		/*-- 网页系统时间 --*/
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 邀约结果集 --*/
		List<Dictionary> callRstList = null;
		try {
			callRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.DIC_TYPE_YYRST_ZJS);
		} catch (EduException e2) {
			callRstList = new ArrayList<Dictionary>();
		}
		model.addAttribute("callRstList", callRstList);

		/*-- 门店信息 --*/
		List<Shop> shopList = shopService.listOpeningShops(staff.getCompanyId());
		model.addAttribute("shopList", shopList);

		return "mobile/keziyy/kz_yy_rst_form";
	}

	/** 提报客资洽谈记录表单 **/
	@RequestMapping(value = "/ding_add_qiatan_log_skip", method = RequestMethod.GET)
	public String dingAddQiatanLogSkip(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		/*-- 参数提取 --*/
		String kzId = maps.get("kzid");
		if (StringUtil.isEmpty(kzId)) {
			// TODO 参数获取失败，错误页面
			return "/mobile/skip/fail_404";
		}
		model.addAttribute("kzId", kzId);

		/*-- 洽谈结果集 --*/
		List<Dictionary> qtRstList = null;
		try {
			qtRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.SHOP_MEET_QT_RST);
		} catch (EduException e2) {
			qtRstList = new ArrayList<Dictionary>();
		}
		model.addAttribute("qtRstList", qtRstList);

		/*-- 流失原因结果集 --*/
		List<Dictionary> runOffList = null;
		try {
			runOffList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.RUN_OFF_REASON);
		} catch (EduException e2) {
			runOffList = NO_DIC;
		}
		model.addAttribute("runOffList", runOffList);

		/*-- 网页系统时间 --*/
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 获取本部门全部门市信息 --*/
		List<Staff> staffList = staffService.getStaffListByDeptIdIgnoDel(staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffList", staffList);

		return "mobile/kezishop/kz_qiatan_rst_form";
	}

	/** -- 门店自然入客提报页面 -- **/
	@RequestMapping(value = "/kz_come_shop", method = RequestMethod.GET)
	public String kzComeShop(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 职工信息 --*/
		Staff staff = getStaffDetail(request);
		if (staff == null) {
			// TODO 获取职工信息失败，跳转
		}
		model.addAttribute("staff", staff);

		/*-- 获取部门下员工列表 --*/
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId(), staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffList", staffs);

		/*-- 门店自然入客结果集 --*/
		List<Dictionary> rkRstList = null;
		try {
			rkRstList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.SHOP_MEET_RK_RST);
		} catch (EduException e2) {
			rkRstList = NO_DIC;
		}
		model.addAttribute("rkRstList", rkRstList);

		/*-- 网页系统时间 --*/
		model.addAttribute("now", TimeUtils.getSysTimeSecond());

		/*-- 流失原因结果集 --*/
		List<Dictionary> runOffList = null;
		try {
			runOffList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.RUN_OFF_REASON);
		} catch (EduException e2) {
			runOffList = NO_DIC;
		}
		model.addAttribute("runOffList", runOffList);

		return "mobile/kezishop/kz_come_shop";
	}

	/**
	 * 客资提报成功页面
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		return "mobile/skip/kz_add_success";
	}

	/**
	 * 权限错误页面
	 */
	@RequestMapping(value = "/pms_fail", method = RequestMethod.GET)
	public String pmsFail(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		return "mobile/skip/pms_fail";
	}

	@RequestMapping(value = "do_kz_merge", method = RequestMethod.GET)
	@ResponseBody
	public Model doKzMerge(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		return model;
	}

	/** -- 获取客资详细记录信息 -- **/
	@RequestMapping(value = "get_ding_kz_detail_by_id", method = RequestMethod.GET)
	public String getDingKzDetailById(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Staff staff = getStaffDetail(request);

		/*-- 参数提取 --*/
		String kzId = maps.get("id");
		String flag = maps.get("flag");
		model.addAttribute("flag", flag);

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return "mobile/time";
		}

		/*-- 接口调用，业务执行 --*/
		try {
			ClientInfo clientInfo = null;
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("id", kzId);
			String getRstStr = crmBaseApi.doService(reqContent, "clientInfoGetById");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(getRstStr);

			if ("100000".equals(jsInfo.getString("code"))) {
				JSONObject jsContent = JsonFmtUtil.strContentToJsonObj(getRstStr);
				clientInfo = JsonFmtUtil.jsonToClientInfo(jsContent.getJSONObject("clientInfo"));
				List<ClientLogInfo> logList = JsonFmtUtil.jsonArrToClientLogInfo(jsContent.getJSONArray("logList"));
				clientInfo.setLogList(logList);
				List<ClientYaoYueLog> yyLogList = JsonFmtUtil
						.jsonArrToClientYaoYueLogInfo(jsContent.getJSONArray("yyLogList"));
				clientInfo.setYyLogList(yyLogList);
				List<ShopMeetLog> qtLogList = JsonFmtUtil
						.jsonArrToClientShopMeetLogInfo(jsContent.getJSONArray("qtLogList"));
				clientInfo.setQtLogList(qtLogList);
				model.addAttribute("qtLogList", qtLogList);
				model.addAttribute("yyLogList", yyLogList);
				model.addAttribute("logList", logList);
				model.addAttribute("clientInfo", clientInfo);
			}
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (EduException e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return "mobile/mykz/logs";
	}

	/**
	 * 获取部门职工二联动
	 */
	@RequestMapping(value = "listDepartStaffs")
	@ResponseBody
	public Model listDepartStaffs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		if (staff == null) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "cookie中信息为空");
			return model;
		}

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getStaffListByDeptIdIgnoDel(staff.getDeptId() + "-", staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		return model;
	}

	/*
	 * 微应用首页
	 */
	@RequestMapping(value = "auth")
	public String dingAuth(Model model, HttpServletRequest request) {
		model.addAttribute("corpid", request.getParameter("corpid"));
		model.addAttribute("SUITE_KEY", Env.SUITE_KEY);
		model.addAttribute("host", Env.HOST);
		return "ding/forward";
	}

	@RequestMapping(value = "listStaffByDepId")
	@ResponseBody
	public JSONObject listStaffByDepId(String deptId, HttpServletRequest request) {

		JSONObject result = new JSONObject();
		result.put("success", false);
		result.put("msg", "系统错误");

		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			result.put("msg", "cookie中信息为空");
			return result;
		}

		Staff staff2 = new Staff();
		staff2.setCompanyId(staff.getCompanyId());
		staff2.setDeptId(deptId);
		List<Staff> staffs = staffService.listStaffByCondition(staff2);
		result.put("success", true);
		result.put("msg", "获取成功");
		result.put("staffList", staffs);
		return result;
	}

	@RequestMapping(value = "dinglogin")
	public String dingLogin(Model model, HttpServletRequest request, HttpServletResponse response) {
		if (preurl == null) {
			preurl = "https://oapi.dingtalk.com/user/get_private_info?access_token=%s&tmp_auth_code=%s";
		}

		String corpid = request.getParameter("corpid");
		String mobile = null;
		String code = request.getParameter("code");
		JSONObject user = null;
		try {
			String accesstoken = AuthHelper.getAccessToken(corpid);
			String url = String.format(preurl, accesstoken, code);
			user = HttpHelper.httpGet(url);
			mobile = user.getString("corpMobile");

		} catch (Exception e) {
			e.printStackTrace();
		}

		Staff staff = staffService.getByMobileAndCorpid(mobile, corpid);
		if (null == staff) {
			return "ding/fail";
		}

		if (user.getString("userId") != null) {
			if (staff.getDingUserId() == null || staff.getDingUserId().equals("")) {
				staff.setDingUserId(user.getString("userId"));
				staffService.editStaffById(staff);
			}
		}
		CookieCompoment.setLoginUser(response, staff);
		return "mobile/kezilog/kz_index";
	}

	/*
	 * 接收钉钉服务器的回调数据
	 * 
	 */
	@RequestMapping(value = "isvreceive")
	public void receive(HttpServletRequest request, HttpServletResponse response) {
		try {

			/** url中的签名 **/
			String msgSignature = request.getParameter("signature");
			/** url中的时间戳 **/
			String timeStamp = request.getParameter("timestamp");
			/** url中的随机字符串 **/
			String nonce = request.getParameter("nonce");

			System.out.println(msgSignature);
			System.out.println(timeStamp);
			System.out.println(nonce);

			/** post数据包数据中的加密数据 **/
			ServletInputStream sis = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(sis));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			/*
			 * post数据包数据中的加密数据转换成JSON对象，JSON对象的格式如下 { "encrypt":
			 * "1ojQf0NSvw2WPvW7LijxS8UvISr8pdDP+rXpPbcLGOmIBNbWetRg7IP0vdhVgkVwSoZBJeQwY2zhROsJq/HJ+q6tp1qhl9L1+ccC9ZjKs1wV5bmA9NoAWQiZ+7MpzQVq+j74rJQljdVyBdI/dGOvsnBSCxCVW0ISWX0vn9lYTuuHSoaxwCGylH9xRhYHL9bRDskBc7bO0FseHQQasdfghjkl"
			 * }
			 */
			JSONObject jsonEncrypt = JSONObject.parseObject(sb.toString());
			String encrypt = "";

			/** 取得JSON对象中的encrypt字段， **/
			try {
				encrypt = jsonEncrypt.getString("encrypt");
			} catch (Exception e) {
				e.printStackTrace();
			}

			/** 对encrypt进行解密 **/
			DingTalkEncryptor dingTalkEncryptor = null;
			String plainText = null;
			DingTalkEncryptException dingTalkEncryptException = null;
			try {
				/*
				 * 创建加解密类 第一个参数为注册套件的之时填写的token
				 * 第二个参数为注册套件的之时生成的数据加密密钥（ENCODING_AES_KEY）
				 * 第三个参数，ISV开发传入套件的suiteKey，普通企业开发传Corpid
				 * 具体参数值请查看开发者后台(http://console.d.aliyun.com)
				 * 
				 * 注：其中，对于第三个参数，在第一次接受『验证回调URL有效性事件的时候』
				 * 传入Env.CREATE_SUITE_KEY，对于这种情况，已在异常中catch做了处理。
				 * 具体区别请查看文档『验证回调URL有效性事件』
				 */
				dingTalkEncryptor = new DingTalkEncryptor(Env.TOKEN, Env.ENCODING_AES_KEY, Env.SUITE_KEY);
				/*
				 * 获取从encrypt解密出来的明文
				 */
				plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);
			} catch (DingTalkEncryptException e) {
				dingTalkEncryptException = e;
				e.printStackTrace();
			} finally {
				if (dingTalkEncryptException != null) {
					if (dingTalkEncryptException.code == DingTalkEncryptException.COMPUTE_DECRYPT_TEXT_CORPID_ERROR) {
						try {
							/*
							 * 第一次创建套件生成加解密类需要传入Env.CREATE_SUITE_KEY，
							 */
							dingTalkEncryptor = new DingTalkEncryptor(Env.TOKEN, Env.ENCODING_AES_KEY,
									Env.CREATE_SUITE_KEY);
							plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);
						} catch (DingTalkEncryptException e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					} else {
						System.out.println(dingTalkEncryptException.getMessage());
						dingTalkEncryptException.printStackTrace();
					}
				}
			}

			/*
			 * 对从encrypt解密出来的明文进行处理 不同的eventType的明文数据格式不同
			 */
			JSONObject plainTextJson = JSONObject.parseObject(plainText);
			String eventType = plainTextJson.getString("EventType");
			/*
			 * res是需要返回给钉钉服务器的字符串，一般为success
			 * "check_create_suite_url"和"check_update_suite_url"事件为random字段
			 * 具体请查看文档或者对应eventType的处理步骤
			 */
			String res = "success";

			switch (eventType) {
			case "suite_ticket":
				/*
				 * "suite_ticket"事件每二十分钟推送一次,数据格式如下 { "SuiteKey": "suitexxxxxx",
				 * "EventType": "suite_ticket ", "TimeStamp": 1234456,
				 * "SuiteTicket": "adsadsad" }
				 */
				Env.suiteTicket = plainTextJson.getString("SuiteTicket");
				// 获取到suiteTicket之后需要换取suiteToken，
				String suiteToken = ServiceHelper.getSuiteToken(Env.SUITE_KEY, Env.SUITE_SECRET, Env.suiteTicket);
				/*
				 * ISV应当把最新推送的suiteTicket做持久化存储， 以防重启服务器之后丢失了当前的suiteTicket
				 * 
				 */
				JSONObject json = new JSONObject();
				json.put("suiteTicket", Env.suiteTicket);
				json.put("suiteToken", suiteToken);
				FileUtils.write2File(json, "ticket");
				break;
			case "tmp_auth_code":
				/*
				 * "tmp_auth_code"事件将企业对套件发起授权的时候推送 数据格式如下 { "SuiteKey":
				 * "suitexxxxxx", "EventType": " tmp_auth_code", "TimeStamp":
				 * 1234456, "AuthCode": "adads" }
				 */
				Env.authCode = plainTextJson.getString("AuthCode");
				Object value = FileUtils.getValue("ticket", "suiteToken");// 获取当前的suiteToken
				if (value == null) {
					break;
				}
				String suiteTokenPerm = value.toString();
				/*
				 * 拿到tmp_auth_code（临时授权码）后，需要向钉钉服务器获取企业的corpId（企业id）
				 * 和permanent_code（永久授权码）
				 */
				CorpAuthSuiteCode corpAuthSuiteCode = ServiceHelper.getPermanentCode(Env.authCode, suiteTokenPerm);
				String corpId = corpAuthSuiteCode.getAuth_corp_info().getCorpid();
				String permanent_code = corpAuthSuiteCode.getPermanent_code();
				/*
				 * 将corpId（企业id）和permanent_code（永久授权码）做持久化存储
				 * 之后在获取企业的access_token时需要使用
				 */
				JSONObject jsonPerm = new JSONObject();
				jsonPerm.put(corpId, permanent_code);
				FileUtils.write2File(jsonPerm, "permanentcode");
				/*
				 * 对企业授权的套件发起激活，
				 */
				ServiceHelper.getActivateSuite(suiteTokenPerm, Env.SUITE_KEY, corpId, permanent_code);
				/*
				 * 获取对应企业的access_token，每一个企业都会有一个对应的access_token，
				 * 访问对应企业的数据都将需要带上这个access_token access_token的过期时间为两个小时
				 */
				try {
					AuthHelper.getAccessToken(corpId);

				} catch (Exception e1) {
					System.out.println(e1.toString());
					e1.printStackTrace();
				}
				break;
			case "change_auth":
				/*
				 * "change_auth"事件将在企业授权变更消息发生时推送 数据格式如下 { "SuiteKey":
				 * "suitexxxxxx", "EventType": " change_auth", "TimeStamp":
				 * 1234456, "AuthCorpId": "xxxxx" }
				 */

				// String corpid = plainTextJson.getString("AuthCorpID");
				// 由于以下操作需要从持久化存储中获得数据，而本demo并没有做持久化存储（因为部署环境千差万别），所以没有具体代码，只有操作指导。
				// 1.根据corpid查询对应的permanent_code(永久授权码)
				// 2.调用『企业授权的授权数据』接口（ServiceHelper.getAuthInfo方法），此接口返回数据具体详情请查看文档。
				// 3.遍历从『企业授权的授权数据』接口中获取所有的微应用信息
				// 4.对每一个微应用都调用『获取企业的应用信息接口』（ServiceHelper.getAgent方法）
				/*
				 * 5.获取『获取企业的应用信息接口』接口返回值其中的"close"参数，才能得知微应用在企业用户做了授权变更之后的状态，
				 * 有三种状态码 分别为0，1，2.含义如下： 0:禁用（例如企业用户在OA后台禁用了微应用） 1:正常
				 * (例如企业用户在禁用之后又启用了微应用) 2:待激活 (企业已经进行了授权，但是ISV还未为企业激活应用)
				 * 再根据具体状态做具体操作。
				 */

				break;
			case "check_create_suite_url":
				/*
				 * "check_create_suite_url"事件将在创建套件的时候推送 {
				 * "EventType":"check_create_suite_url", "Random":"brdkKLMW",
				 * "TestSuiteKey":"suite4xxxxxxxxxxxxxxx" }
				 */
				// 此事件需要返回的"Random"字段，
				res = plainTextJson.getString("Random");
				// String testSuiteKey =
				// plainTextJson.getString("TestSuiteKey");
				break;

			case "check_update_suite_url":
				/*
				 * "check_update_suite_url"事件将在更新套件的时候推送 {
				 * "EventType":"check_update_suite_url", "Random":"Aedr5LMW",
				 * "TestSuiteKey":"suited6db0pze8yao1b1y"
				 * 
				 * }
				 */
				res = plainTextJson.getString("Random");
				break;
			default: // do something
				break;
			}

			/** 对返回信息进行加密 **/
			long timeStampLong = Long.parseLong(timeStamp);
			Map<String, String> jsonMap = null;
			try {
				/*
				 * jsonMap是需要返回给钉钉服务器的加密数据包
				 */
				jsonMap = dingTalkEncryptor.getEncryptedMap(res, timeStampLong, nonce);
			} catch (DingTalkEncryptException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			JSONObject json = new JSONObject();
			json.putAll(jsonMap);
			response.getWriter().append(json.toString());
		} catch (Exception er) {
			er.printStackTrace();
		}
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

	// 获取页面渠道下拉菜单
	public List<Source> getSrcList(Staff staff, String srcType) {

		if (staff == null || srcType == null || "".equals(srcType)) {
			return NO_SOURCE;
		}

		List<Source> srcList = null;
		try {
			if (staff.getIsChief()) {
				// 如果是主管，获取指定渠道类型的所有渠道
				srcList = sourceService.getSrcListByType(staff.getCompanyId(), srcType);
			} else {
				// 非主管，只显示自己所负责的渠道
				srcList = sourceService.getSrcListDsByStaffCj(staff.getId());
			}

			if (srcList == null) {
				srcList = NO_SOURCE;
			}
		} catch (EduException e) {
			srcList = NO_SOURCE;
			e.printStackTrace();
		}

		return srcList;
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

	/*-- 获取登录人详细信息 --*/
	public Staff getStaffDetail(HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return null;
		}
		return staffService.getStaffInfoById(staff.getId());
	}
}