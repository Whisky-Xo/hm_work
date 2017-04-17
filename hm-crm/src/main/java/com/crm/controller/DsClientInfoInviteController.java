package com.crm.controller;

import java.util.ArrayList;
import java.util.Date;
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
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.ClientInfoConstant;
import com.crm.api.constant.Constants;
import com.crm.api.constant.DicConts;
import com.crm.api.constant.PageConfConst;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.BeidouClientInfoFilter;
import com.crm.common.util.ClientInfoUtil;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.SourceUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Company;
import com.crm.model.Dept;
import com.crm.model.Dictionary;
import com.crm.model.PageConf;
import com.crm.model.Permission;
import com.crm.model.Shop;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.model.Status;
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
 * 电商：客资邀约
 * 
 * @author JingChenglong 2016-12-23 11:45
 *
 */
@Controller
@RequestMapping("/client")
public class DsClientInfoInviteController {

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
	private static final List<ClientInfo> NO_CLIENT = new ArrayList<ClientInfo>();
	private static final List<Status> NO_STATUS = new ArrayList<Status>();
	private static final List<Source> NO_SOURCE = new ArrayList<Source>();
	private static final List<Dictionary> NO_DIC = new ArrayList<Dictionary>();
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/** -- 1.电商邀约 -- **/
	@RequestMapping(value = "/yaoyue_ds", method = RequestMethod.GET)
	public String yaoyueDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.YAOYUE_DS);

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("statuses", statusList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());

		/*-- 临时接口数据 --*/
		// 邀约员---该部门下所有员工
		if (staff.getCompanyId() == 3) {
			List<Staff> yyStaffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
					staff.getCompanyId());
			model.addAttribute("yy_staffs", yyStaffs);
			// 提报员---公司所有员工
			List<Staff> tb_staffs = staffService.getByDeptId("0-1-2-4-", "0-1-2-4", staff.getCompanyId());
			model.addAttribute("tb_staffs", tb_staffs);

		}

		return "appointords/yaoyue_ds";
	}

	/** -- 2.客资追踪 -- **/
	@RequestMapping(value = "/trace_client_ds", method = RequestMethod.GET)
	public String traceClientDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.TRACE_CLIENT_DS);

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());
		return "appointords/trace_client_ds";
	}

	/** -- 3.婚期临近 -- **/
	@RequestMapping(value = "/wedding_near_ds", method = RequestMethod.GET)
	public String weddingNearDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.WEDDING_DS_PAGE);

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("weddingrange", "180");
		model.addAttribute("statuses", statusList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());
		return "appointords/wedding_near_ds";
	}

	/** -- 4.预计到店 -- **/
	@RequestMapping(value = "/to_shop_mind_ds", method = RequestMethod.GET)
	public String toShopMindDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Shop> shopList = shopService.listShops(staff.getCompanyId());
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.MINDSHOP_DS_PAGE);

		model.addAttribute("appoint_time_start", TimeUtils.getCurrentymd());
		model.addAttribute("appoint_time_end", TimeUtils.getCurrentymd());
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("statuses", statusList);
		model.addAttribute("shops", shopList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());
		return "appointords/to_shop_mind_ds";
	}

	/** -- 5.我的到店客资 -- **/
	@RequestMapping(value = "/to_shop_success_ds", method = RequestMethod.GET)
	public String toShopSuccessDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Shop> shopList = shopService.listShops(staff.getCompanyId());
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		// 到店状态
		List<Status> cospStsList = new ArrayList<Status>();
		String[] stsArr = ClientInfoConstant.IS_COME_SHOP.split(Constants.STR_SEPARATOR);
		for (String stsId : stsArr) {
			for (Status sts : statusList) {
				if (stsId.equals(sts.getStatusId().toString())) {
					cospStsList.add(sts);
				}
			}
		}

		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.SUCSSHOP_DS_PAGE);

		model.addAttribute("actual_time_start", TimeUtils.getCurrentymd());
		model.addAttribute("actual_time_end", TimeUtils.getCurrentymd());
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("shops", shopList);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("statuses", cospStsList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());
		return "appointords/to_shop_success_ds";
	}

	/** -- 6.电商无效待审核 -- **/
	@RequestMapping(value = "/ds_invalid_check", method = RequestMethod.GET)
	public String dsInvalidCheck(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Dictionary> invalidRsnList = getInvalidReasonList(staff.getCompanyId());
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.INVALID_CHK_PAGE);

		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("invalidRsnList", invalidRsnList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());
		return "appointords/ds_invalid_check";
	}

	/** -- 7.邀约员调配 -- **/
	@RequestMapping(value = "/appointor_mix_ds", method = RequestMethod.GET)
	public String appointorMixDs(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Source> sourceMyCj = sourceService.getSrcListDsByStaffCj(staff.getId());
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.APT_MIX_DS_PAGE);

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("sourcecj", sourceMyCj);
		model.addAttribute("statuses", statusList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());
		return "appointords/appointor_mix_ds";
	}

	/*-- 1.电商邀约 --*/
	@RequestMapping(value = "yaoyue_ds_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model yaoyueDsFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("searchkey");

		String sort = maps.get("sort");
		int sortCode = ClientInfoUtil.getPageCode(maps);
		Staff staff = getStaffDetail(request);
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		String sourceIds = SourceUtil.getSrcIdStr(sourceList);
		String appointIds = getAppointIdLimit(staff, staffId, departId);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? Constants.ORDERSORT_ASC : Constants.ORDERSORT_DESC);
		} else {
			// 电商邀约客资--默认根据客资状态升序，最后跟进时间降序
			reqContent.put("sortname1", "sts.PRIORITY");
			reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
			reqContent.put("sortname2", "info.UPDATETIME");
			reqContent.put("sorttype2", Constants.ORDERSORT_DESC);
			sort = "sts.PRIORITY";
			sortCode = 1;
		}
		try {
			if (StringUtil.isEmpty(searchKey)) {
				if (StringUtil.isValid(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isValid(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isValid(statusId)) {
					reqContent.put("statusids", statusId);
				}
				if (StringUtil.isValid(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				if (StringUtil.isValid(appointIds)) {
					reqContent.put("appointids", appointIds);
				}
				if ((StringUtil.isEmpty(appointIds) || "0".equals(appointIds))
						&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
					reqContent.put("sourceids", sourceIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				statusId = "0";
				sourceId = "0";
				staffId = "0";
				createTimeStart = "";
				createTimeEnd = "";
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 20);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.YAOYUE_DS);
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("time", createTimeStart + " ~ " + createTimeEnd);
		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 2.客资追踪 --*/
	@RequestMapping(value = "trace_client_ds_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model traceClientDsFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String traceTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String traceTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("searchkey");

		String sort = maps.get("sort");
		int sortCode = ClientInfoUtil.getPageCode(maps);
		Staff staff = getStaffDetail(request);
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		String sourceIds = SourceUtil.getSrcIdStr(sourceList);
		String appointIds = getAppointIdLimit(staff, staffId, departId);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? Constants.ORDERSORT_ASC : Constants.ORDERSORT_DESC);
		} else {
			// 客资追踪客资--默认根据客资追踪时间升序，最后跟进时间降序
			reqContent.put("sortname1", "info.TRACETIME");
			reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
			sort = "info.TRACETIME";
			sortCode = 1;
		}
		try {
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("statusids", ClientInfoConstant.BE_TRACK);
				if (StringUtil.isValid(traceTimeStart)) {
					reqContent.put("tracetimestart", traceTimeStart + " 00:00:00");
				}
				if (StringUtil.isValid(traceTimeEnd)) {
					reqContent.put("tracetimeend", traceTimeEnd + " 23:59:59");
				}
				if (StringUtil.isValid(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				if (StringUtil.isValid(appointIds)) {
					reqContent.put("appointids", appointIds);
				}
				if ((StringUtil.isEmpty(appointIds) || "0".equals(appointIds))
						&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
					reqContent.put("sourceids", sourceIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				sourceId = "0";
				staffId = "0";
				traceTimeStart = "";
				traceTimeEnd = "";
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 20);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.TRACE_CLIENT_DS);
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("time", traceTimeStart + " ~ " + traceTimeEnd);
		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 3.婚期临近 --*/
	@RequestMapping(value = "wedding_near_ds_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model weddingNearDsFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		Integer weddingRange = Integer.valueOf(StringUtil.nullToZeroStr(maps.get("weddingrange")));
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("searchkey");

		String sort = maps.get("sort");
		int sortCode = ClientInfoUtil.getPageCode(maps);
		Staff staff = getStaffDetail(request);
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		String sourceIds = SourceUtil.getSrcIdStr(sourceList);
		String appointIds = getAppointIdLimit(staff, staffId, departId);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? Constants.ORDERSORT_ASC : Constants.ORDERSORT_DESC);
		} else {
			// 婚期临近客资--默认按照婚期时间升序
			reqContent.put("sortname1", "info.MARRYTIME");
			reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
			sort = "info.MARRYTIME";
			sortCode = 1;
		}
		try {
			if (StringUtil.isEmpty(searchKey)) {
				if (weddingRange != 999) {
					reqContent.put("marrytimestart", TimeUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					reqContent.put("marrytimeend", TimeUtils.getFutureTime(weddingRange.toString()));
				}
				if (StringUtil.isValid(statusId)) {
					reqContent.put("statusids", statusId);
				}
				if (StringUtil.isValid(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				if (StringUtil.isValid(appointIds)) {
					reqContent.put("appointids", appointIds);
				}
				if ((StringUtil.isEmpty(appointIds) || "0".equals(appointIds))
						&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
					reqContent.put("sourceids", sourceIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				statusId = "0";
				sourceId = "0";
				staffId = "0";
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 20);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.WEDDING_DS_PAGE);
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("weddingRange", weddingRange.toString());
		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 4.预计到店 --*/
	@RequestMapping(value = "to_shop_mind_ds_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model toShopMindDsFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String appointTimeStart = StringUtil.isEmpty(maps.get("appointstart")) ? TimeUtils.getCurrentymd()
				: maps.get("appointstart");
		String appointTimeEnd = StringUtil.isEmpty(maps.get("appointend")) ? TimeUtils.getCurrentymd()
				: maps.get("appointend");
		String shopId = maps.get("shopid");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("searchkey");

		String sort = maps.get("sort");
		int sortCode = ClientInfoUtil.getPageCode(maps);
		Staff staff = getStaffDetail(request);
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		String sourceIds = SourceUtil.getSrcIdStr(sourceList);
		String appointIds = getAppointIdLimit(staff, staffId, departId);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? Constants.ORDERSORT_ASC : Constants.ORDERSORT_DESC);
		} else {
			// 预计到店--默认按照客人预计到店时间升序
			reqContent.put("sortname1", "info.APPOINTTIME");
			reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
			sort = "info.APPOINTTIME";
			sortCode = 1;
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("sparesql", "info.SHOPID is not null AND info.SHOPID != '' AND info.SHOPID != 0 ");// 客资门店ID不能为空
			if (StringUtil.isValid(appointTimeStart)) {
				reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
			}
			if (StringUtil.isValid(appointTimeEnd)) {
				reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
			}
			if (StringUtil.isValid(shopId)) {
				reqContent.put("shopids", shopId);
			}
			if (StringUtil.isValid(statusId)) {
				reqContent.put("statusids", statusId);
			}
			if (StringUtil.isValid(sourceId)) {
				reqContent.put("sourceids", sourceId);
			}
			if (StringUtil.isValid(appointIds)) {
				reqContent.put("appointids", appointIds);
			}
			if ((StringUtil.isEmpty(appointIds) || "0".equals(appointIds))
					&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
				reqContent.put("sourceids", sourceIds);
			}
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
		} else {
			reqContent.put("searchkey", searchKey.trim());
			statusId = "0";
			sourceId = "0";
			staffId = "0";
			shopId = "0";
			appointTimeStart = "";
			appointTimeEnd = "";
		}

		try {
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 20);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.MINDSHOP_DS_PAGE);
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("time", appointTimeStart + " ~ " + appointTimeEnd);
		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 5.我的到店 --*/
	@RequestMapping(value = "to_shop_success_ds_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model toShopSuccessDsFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String comeShopTimeStart = StringUtil.isEmpty(maps.get("actualstart")) ? TimeUtils.getCurrentymd()
				: maps.get("actualstart");
		String comeShopTimeEnd = StringUtil.isEmpty(maps.get("actualend")) ? TimeUtils.getCurrentymd()
				: maps.get("actualend");
		String typeId = maps.get("typeid");// 入店类型：0、1、2
		String scsFlag = maps.get("scsflag");// 查询成功客资标识
		String shopId = maps.get("shopid");
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("searchkey");

		String sort = maps.get("sort");
		int sortCode = ClientInfoUtil.getPageCode(maps);
		Staff staff = getStaffDetail(request);
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		String sourceIds = SourceUtil.getSrcIdStr(sourceList);
		String appointIds = getAppointIdLimit(staff, staffId, departId);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? Constants.ORDERSORT_ASC : Constants.ORDERSORT_DESC);
		} else {
			// 我的到店客资--默认按照客人到店时间降序
			reqContent.put("sortname1", "info.ACTUALTIME");
			reqContent.put("sorttype1", Constants.ORDERSORT_DESC);
			sort = "info.ACTUALTIME";
			sortCode = 0;
		}
		if (StringUtil.isEmpty(searchKey)) {
			reqContent.put("sparesql", "info.SHOPID is not null AND info.SHOPID != '' AND info.SHOPID != 0 ");// 客资门店ID不能为空
			reqContent.put("typeid", typeId);
			if (StringUtil.isValid(comeShopTimeStart)) {
				reqContent.put("comeshoptimestart", comeShopTimeStart + " 00:00:00");
			}
			if (StringUtil.isValid(comeShopTimeEnd)) {
				reqContent.put("comeshoptimeend", comeShopTimeEnd + " 23:59:59");
			}
			if (StringUtil.isValid(shopId)) {
				reqContent.put("shopids", shopId);
			}
			if (StringUtil.isNotEmpty(scsFlag) && "success".equals(scsFlag)) {
				reqContent.put("statusids", ClientInfoConstant.IS_SUCCESS);
			} else {
				if (StringUtil.isValid(statusId)) {
					reqContent.put("statusids", statusId);
				} else {
					reqContent.put("statusids", ClientInfoConstant.IS_COME_SHOP);
				}
			}
			if (StringUtil.isValid(sourceId)) {
				reqContent.put("sourceids", sourceId);
			}
			if (StringUtil.isValid(appointIds)) {
				reqContent.put("appointids", appointIds);
			}
			if ((StringUtil.isEmpty(appointIds) || "0".equals(appointIds))
					&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
				reqContent.put("sourceids", sourceIds);
			}
			reqContent.put("pagesize", pageSize);
			reqContent.put("currentpage", currentPage);
		} else {
			reqContent.put("searchkey", searchKey.trim());
			statusId = "0";
			sourceId = "0";
			staffId = "0";
			shopId = "0";
			comeShopTimeStart = "";
			comeShopTimeEnd = "";
		}

		try {
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQueryComeShop");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 20);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.SUCSSHOP_DS_PAGE);
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("time", comeShopTimeStart + " ~ " + comeShopTimeEnd);
		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 6.无效待审核客资 --*/
	@RequestMapping(value = "ds_invalid_check_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model dsInvalidCheckFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("searchkey");

		String sort = maps.get("sort");
		int sortCode = ClientInfoUtil.getPageCode(maps);
		Staff staff = getStaffDetail(request);
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		String sourceIds = SourceUtil.getSrcIdStr(sourceList);
		String appointIds = getAppointIdLimit(staff, staffId, departId);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		try {
			reqContent.put("companyid", staff.getCompanyId());
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", sortCode == 1 ? Constants.ORDERSORT_ASC : Constants.ORDERSORT_DESC);
			} else {
				// 待确定无效客资--默认按照客资的最后跟进时间降序排列
				reqContent.put("sortname1", "info.UPDATETIME");
				reqContent.put("sorttype1", Constants.ORDERSORT_DESC);
				sort = "info.UPDATETIME";
				sortCode = 0;
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("statusids", ClientInfoConstant.INVALID_BE_CHECK);
				if (StringUtil.isValid(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				if (StringUtil.isValid(appointIds)) {
					reqContent.put("appointids", appointIds);
				}
				if ((StringUtil.isEmpty(appointIds) || "0".equals(appointIds))
						&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
					reqContent.put("sourceids", sourceIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey);
				sourceId = "0";
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 20);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.INVALID_CHK_PAGE);
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 7.电商邀约员调配 --*/
	@RequestMapping(value = "appointor_mix_ds_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model appointorMixDsFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("searchkey");

		String sort = maps.get("sort");
		int sortCode = ClientInfoUtil.getPageCode(maps);
		Staff staff = getStaffDetail(request);
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		String sourceIds = SourceUtil.getSrcIdStr(sourceList);
		String appointIds = getAppointIdLimit(staff, staffId, departId);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		try {
			reqContent.put("companyid", staff.getCompanyId());
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", sortCode == 1 ? Constants.ORDERSORT_ASC : Constants.ORDERSORT_DESC);
			} else {
				// 电商邀约调配--默认按照状态升序，最后跟进时间降序
				reqContent.put("sortname1", "sts.PRIORITY");
				reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
				reqContent.put("sortname2", "info.UPDATETIME");
				reqContent.put("sorttype2", Constants.ORDERSORT_DESC);
				sort = "sts.PRIORITY";
				sortCode = 1;
			}
			if (StringUtil.isEmpty(searchKey)) {
				if (StringUtil.isNotEmpty(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(statusId) && !"0".equals(statusId)) {
					reqContent.put("statusids", statusId);
				}
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				if (StringUtil.isNotEmpty(appointIds) && !"0".equals(appointIds)) {
					reqContent.put("appointids", appointIds);
				}
				if ((StringUtil.isEmpty(appointIds) || "0".equals(appointIds))
						&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
					reqContent.put("sourceids", sourceIds);
				}
				if (staff.getIsChief() && (StringUtil.isEmpty(staffId) || "0".equals(staffId))
						&& (staff.getDeptId().equals(departId))) {
					reqContent.remove("appointids");
					if (StringUtil.isEmpty(sourceId) || "0".equals(sourceId)) {
						reqContent.put("sourceids", sourceIds);
					}
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				statusId = "0";
				sourceId = "0";
				staffId = "0";
				createTimeStart = "";
				createTimeEnd = "";
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				pageInfo = js.getJSONObject("pageInfo");
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			pageInfo.put("totalcount", 0);
			pageInfo.put("totalpage", 0);
			pageInfo.put("pagesize", 20);
			pageInfo.put("currentpage", 1);
			clientList = NO_CLIENT;
		}

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.APT_MIX_DS_PAGE);
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("time", createTimeStart + " ~ " + createTimeEnd);
		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
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
				srcList = sourceService.getSrcListByStaffYy(staff.getId());
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

	// 获取企业状态列表
	public List<Status> getStatusList(Integer companyId, String statusType) {

		if (companyId == null || companyId == 0 || StringUtil.isEmpty(statusType)) {
			return null;
		}

		List<Status> statusList = null;

		try {
			statusList = statusService.getStatusInfoAllList(companyId, statusType);
		} catch (EduException e) {
			statusList = NO_STATUS;
			e.printStackTrace();
		}

		return statusList;
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

	/*-- 获取登录人详细信息 --*/
	public Staff getStaffDetail(HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return null;
		}
		return staffService.getStaffInfoById(staff.getId());
	}

	/*-- 获取邀约员ID集合 --*/
	public String getAppointIdLimit(Staff staff, String staffId, String departId) {

		if (staff == null) {
			return "";
		}

		String appointIds = "";

		if (staff.getIsChief()) {
			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工的信息
				appointIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					appointIds += s.getId();
					appointIds += Constants.STR_SEPARATOR;
				}
				if (appointIds.length() != 0) {
					appointIds = appointIds.substring(0, appointIds.length() - 1);
				}
			}
		} else {
			// 如果不是主管，只能查询自己的信息
			appointIds = staff.getId() + "";
		}

		return appointIds;
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