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
 * 电商：录入客资
 * 
 * @author JingChenglong 2016-12-23 10:04
 *
 */
@Controller
@RequestMapping("/client")
public class DsClientInfoAddController {

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

	/** -- 1.录入客资 -- **/
	@RequestMapping(value = "/kz_add", method = RequestMethod.GET)
	public String kzAdd(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Source> sourceMyCj = sourceService.getSrcListDsByStaffCj(staff.getId());
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.KZ_ADD_PAGE);

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
		return "collector/kz_add";
	}

	/** -- 2.无效客资 -- **/
	@RequestMapping(value = "/kz_invalid", method = RequestMethod.GET)
	public String kzInvalid(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Dictionary> invalidRsnList = getInvalidReasonList(staff.getCompanyId());
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.KZ_INVALID_PAGE);

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("invalidRsnList", invalidRsnList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());
		return "collector/kz_invalid";
	}

	/** -- 3.电商无效待审批 -- **/
	@RequestMapping(value = "/ds_invalid_stay", method = RequestMethod.GET)
	public String dsInvalidStay(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Dictionary> invalidRsnList = getInvalidReasonList(staff.getCompanyId());
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.INVALID_STAY_PAGE);

		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("invalidRsnList", invalidRsnList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());
		return "collector/ds_invalid_stay";
	}

	/** -- 4.采集员调配 -- **/
	@RequestMapping(value = "/ds_collector_mix", method = RequestMethod.GET)
	public String dsCollectorMix(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.COL_MIX_PAGE);

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
		return "collector/ds_collector_mix";
	}

	/*-- 1.录入客资 --*/
	@RequestMapping(value = "kz_add_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model kzAddFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
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
		String collectorIds = getCollectorIdLimit(staff, staffId, departId);

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
				// 录入客资--默认按照创建时间降序
				reqContent.put("sortname1", "info.CREATETIME");
				reqContent.put("sorttype1", Constants.ORDERSORT_DESC);
				sort = "info.CREATETIME";
				sortCode = 0;
			}
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
				if (StringUtil.isValid(collectorIds)) {
					reqContent.put("collectorids", collectorIds);
				}
				if ((StringUtil.isEmpty(collectorIds) || "0".equals(collectorIds))
						&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
					reqContent.put("sourceids", sourceIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey);
				statusId = "0";
				sourceId = "0";
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

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.KZ_ADD_PAGE);
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

	/*-- 2.无效客资 --*/
	@RequestMapping(value = "kz_invalid_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model kzInvalidFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String invalidCode = StringUtil.nullToZeroStr(maps.get("invalidcode"));
		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID
		String searchKey = maps.get("searchkey");

		String sort = maps.get("sort");
		int sortCode = ClientInfoUtil.getPageCode(maps);
		Staff staff = getStaffDetail(request);
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		String sourceIds = SourceUtil.getSrcIdStr(sourceList);
		String collectorIds = getCollectorIdLimit(staff, staffId, departId);

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
				// 无效客资--默认按照最后跟进时间降序
				reqContent.put("sortname1", "info.UPDATETIME");
				reqContent.put("sorttype1", Constants.ORDERSORT_DESC);
				sort = "info.UPDATETIME";
				sortCode = 0;
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("statusids", ClientInfoConstant.BE_INVALID);
				if (StringUtil.isValid(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isValid(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isValid(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				if (StringUtil.isValid(collectorIds)) {
					reqContent.put("collectorids", collectorIds);
				}
				if ((StringUtil.isEmpty(collectorIds) || "0".equals(collectorIds))
						&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
					reqContent.put("sourceids", sourceIds);
				}
				if (StringUtil.isValid(invalidCode)) {
					reqContent.put("invalidcode", invalidCode);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey);
				sourceId = "0";
				createTimeStart = "";
				createTimeEnd = "";
			}
			String clientRstStr = crmBaseApi.doService(reqContent, "clientInfoQueryInvalid");
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

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.KZ_INVALID_PAGE);
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

	/*-- 3.无效待审批客资 --*/
	@RequestMapping(value = "ds_invalid_stay_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model dsInvalidStayFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
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
		String collectorIds = getCollectorIdLimit(staff, staffId, departId);

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
				reqContent.put("statusids", ClientInfoConstant.INVALID_BE_STAY);
				if (StringUtil.isValid(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				if (StringUtil.isValid(collectorIds)) {
					reqContent.put("collectorids", collectorIds);
				}
				if ((StringUtil.isEmpty(collectorIds) || "0".equals(collectorIds))
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

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.INVALID_STAY_PAGE);
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("content", content);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("code", "100000");
		model.addAttribute("msg", "成功");
		return model;
	}

	/*-- 4.电商采集员调配 --*/
	@RequestMapping(value = "ds_collector_mix_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model dsCollectorMixFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
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
		String collectorIds = getCollectorIdLimit(staff, staffId, departId);

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
				// 录入客资--默认按照创建时间降序
				reqContent.put("sortname1", "info.CREATETIME");
				reqContent.put("sorttype1", Constants.ORDERSORT_DESC);
				sort = "info.CREATETIME";
			}
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
				if (StringUtil.isValid(collectorIds)) {
					reqContent.put("collectorids", collectorIds);
				}
				if ((StringUtil.isEmpty(collectorIds) || "0".equals(collectorIds))
						&& (StringUtil.isEmpty(sourceId) || "0".equals(sourceId))) {
					reqContent.put("sourceids", sourceIds);
				}
				if (staff.getIsChief() && (StringUtil.isEmpty(staffId) || "0".equals(staffId))
						&& (staff.getDeptId().equals(departId))) {
					reqContent.remove("collectorids");
					if (StringUtil.isEmpty(sourceId) || "0".equals(sourceId)) {
						reqContent.put("sourceids", sourceIds);
					}
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey);
				statusId = "0";
				sourceId = "0";
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

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), PageConfConst.COL_MIX_PAGE);
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

	/*-- 获取采集员员ID集合 --*/
	public String getCollectorIdLimit(Staff staff, String staffId, String departId) {

		if (staff == null) {
			return "";
		}

		String collectorIds = "";

		if (staff.getIsChief()) {
			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工的信息
				collectorIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					collectorIds += s.getId();
					collectorIds += Constants.STR_SEPARATOR;
				}
				if (collectorIds.length() != 0) {
					collectorIds = collectorIds.substring(0, collectorIds.length() - 1);
				}
			}
		} else {
			// 如果不是主管，只能查询自己的信息
			collectorIds = staff.getId() + "";
		}

		return collectorIds;
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