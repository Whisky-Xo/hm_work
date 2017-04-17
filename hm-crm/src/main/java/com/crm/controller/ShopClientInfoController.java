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
import com.crm.api.constant.DicConts;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.BeidouClientInfoFilter;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.common.util.JsonFmtUtil;
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
 * 门店：客资管理
 * 
 * @author JingChenglong 2016-12-30 13:47
 *
 */
@Controller
@RequestMapping("/client")
public class ShopClientInfoController {

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

	/** -- 1.网络预计进店 -- **/
	@RequestMapping(value = "/net_to_shop_all", method = RequestMethod.GET)
	public String netToShopAll(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Dictionary> sourceList = getShopSrcList(staff.getCompanyId());
		List<Dictionary> statusList = getShopStatusList(staff.getCompanyId());

		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), "net_to_shop_all");

		model.addAttribute("start", "");
		model.addAttribute("end", "");
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("sources", sourceList);
		model.addAttribute("statuses", statusList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());

		return "shopmeet/net_to_shop_all";
	}

	/** -- 2.网络预计今日进店 -- **/
	@RequestMapping(value = "/net_to_shop_today", method = RequestMethod.GET)
	public String netToShopToday(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Dictionary> sourceList = getShopSrcList(staff.getCompanyId());
		List<Dictionary> statusList = getShopStatusList(staff.getCompanyId());

		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), "net_to_shop_all");

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

		return "shopmeet/net_to_shop_today";
	}

	/** -- 3.自然入客 -- **/
	@RequestMapping(value = "/nature_to_shop", method = RequestMethod.GET)
	public String natureToShopToday(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		List<Dictionary> statusList = getShopStatusList(staff.getCompanyId());

		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), "nature_to_shop");

		model.addAttribute("start", TimeUtils.getCurrentymd());
		model.addAttribute("end", TimeUtils.getCurrentymd());
		model.addAttribute("staffs", staffs);
		model.addAttribute("depts", depts);
		model.addAttribute("date", DateUtil.getSysDateStr());
		model.addAttribute("staff", staff);
		model.addAttribute("statuses", statusList);
		model.addAttribute("company", getCompany(staff.getCompanyId()));
		model.addAttribute("pageMap", pageControl(staff.getId()));
		model.addAttribute("title", pageConf.getTitleTxt());

		return "shopmeet/nature_to_shop";
	}

	/*-- 1.网络预计进店 --*/
	@RequestMapping(value = "net_to_shop_all_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model netToShopAllFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String appointTimeStart = maps.get("start");
		String appointTimeEnd = maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String searchKey = maps.get("searchkey");

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int sortCode = StringUtil.nullToIntZero(maps.get("sortcode"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			sortCode = sortCode == 0 ? 1 : 0;
		}

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 判定职员身份，获取查询门市的ID集合 --*/
		String receptorIds = "";
		if (staff.getIsChief()) { // 是否是主管
			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				receptorIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				if (!departId.equals(staff.getDeptId())) {
					// 获取部门下所有员工
					List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
					for (Staff s : sfs) {
						receptorIds += s.getId();
						receptorIds += ",";
					}
					if (receptorIds.length() != 0) {
						receptorIds = receptorIds.substring(0, receptorIds.length() - 1);
					}
				}
			}
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? "ASC" : "DESC");
		} else {
			// 网络预约到店客资--默认按照客人预约到店时间升序
			reqContent.put("sortname1", "sts.PRIORITY");
			reqContent.put("sorttype1", "ASC");
			reqContent.put("sortname2", "info.APPOINTTIME");
			reqContent.put("sorttype2", "ASC");
			sort = "sts.PRIORITY";
			sortCode = 1;
		}
		try {
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("shopids", staff.getShopId());
				reqContent.put("statusids", statusId);
				if (StringUtil.isNotEmpty(appointTimeStart)) {
					reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(appointTimeEnd)) {
					reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				if (StringUtil.isNotEmpty(receptorIds) && !"0".equals(receptorIds)) {
					reqContent.put("receptorids", receptorIds);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				statusId = "0";
				sourceId = "0";
				staffId = "0";
				appointTimeStart = "";
				appointTimeEnd = "";
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

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), "net_to_shop_all");
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

	/*-- 2.网络预计今日进店 --*/
	@RequestMapping(value = "net_to_shop_today_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model netToShopTodayFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String appointTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String appointTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));
		String searchKey = maps.get("searchkey");

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int sortCode = StringUtil.nullToIntZero(maps.get("sortcode"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			sortCode = sortCode == 0 ? 1 : 0;
		}

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 判定职员身份，获取查询门市的ID集合 --*/
		String receptorIds = "";
		if (staff.getIsChief()) { // 是否是主管
			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				receptorIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				if (!departId.equals(staff.getDeptId())) {
					// 获取部门下所有员工
					List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
					for (Staff s : sfs) {
						receptorIds += s.getId();
						receptorIds += ",";
					}
					if (receptorIds.length() != 0) {
						receptorIds = receptorIds.substring(0, receptorIds.length() - 1);
					}
				}
			}
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? "ASC" : "DESC");
		} else {
			// 网络预约到店客资--默认按照客人预约到店时间升序
			reqContent.put("sortname1", "info.APPOINTTIME");
			reqContent.put("sorttype1", "ASC");
			sort = "info.APPOINTTIME";
			sortCode = 1;
		}
		try {
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("shopids", staff.getShopId());
				reqContent.put("receptorids", receptorIds);
				reqContent.put("statusids", statusId);
				if (StringUtil.isNotEmpty(appointTimeStart)) {
					reqContent.put("appointtimestart", appointTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(appointTimeEnd)) {
					reqContent.put("appointtimeend", appointTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				statusId = "0";
				sourceId = "0";
				staffId = "0";
				appointTimeStart = "";
				appointTimeEnd = "";
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

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), "net_to_shop_all");
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

	/*-- 3.自然入客 --*/
	@RequestMapping(value = "nature_to_shop_filter", method = RequestMethod.POST)
	@ResponseBody
	public Model natureToShopFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "20" : maps.get("size"));
		String actualTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String actualTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("statusid"));
		String searchKey = maps.get("searchkey");

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int sortCode = StringUtil.nullToIntZero(maps.get("sortcode"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			sortCode = sortCode == 0 ? 1 : 0;
		}

		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 判定职员身份，获取查询门市的ID集合 --*/
		String receptorIds = "";
		if (staff.getIsChief()) { // 是否是主管
			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				receptorIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				if (!departId.equals(staff.getDeptId())) {
					// 获取部门下所有员工
					List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
					for (Staff s : sfs) {
						receptorIds += s.getId();
						receptorIds += ",";
					}
					if (receptorIds.length() != 0) {
						receptorIds = receptorIds.substring(0, receptorIds.length() - 1);
					}
				}
			}
		}

		Integer sourceId = sourceService.getSrcByName(Source.SRC_NATURE_COME_SHOP, staff.getCompanyId()).getSrcId();// 自然入客渠道ID

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		if (StringUtil.isNotEmpty(sort)) {
			// 自定义排序
			reqContent.put("sortname1", sort);
			reqContent.put("sorttype1", sortCode == 1 ? "ASC" : "DESC");
		} else {
			// 门店自然到店客资--默认按照客人到店时间降序
			reqContent.put("sortname1", "info.ACTUALTIME");
			reqContent.put("sorttype1", "DESC");
			sort = "info.ACTUALTIME";
			sortCode = 1;
		}
		try {
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("shopids", staff.getShopId());
				reqContent.put("receptorids", receptorIds);
				reqContent.put("statusids", statusId);
				reqContent.put("sourceids", sourceId);
				if (StringUtil.isNotEmpty(actualTimeStart)) {
					reqContent.put("actualtimestart", actualTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(actualTimeEnd)) {
					reqContent.put("actualttimeend", actualTimeEnd + " 23:59:59");
				}
				reqContent.put("pagesize", pageSize);
				reqContent.put("currentpage", currentPage);
			} else {
				reqContent.put("searchkey", searchKey.trim());
				statusId = "0";
				staffId = "0";
				actualTimeStart = "";
				actualTimeEnd = "";
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

		PageConf pageConf = pageConfService.getPageConfByCompany(staff.getCompanyId(), "nature_to_shop");
		String content = BeidouClientInfoFilter.doFilter(clientList, pageConf.getContent());

		model.addAttribute("time", actualTimeStart + " ~ " + actualTimeEnd);
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
				srcList = sourceService.getSrcListByStaffTg(staff.getId());
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

	// 门店渠道下拉菜单
	public List<Dictionary> getShopSrcList(Integer companyId) {

		if (companyId == null || companyId == 0) {
			return null;
		}

		List<Dictionary> shopSrcList = null;

		try {
			shopSrcList = dictionaryService.getDictionaryListByType(companyId, DicConts.SHOP_SRC_IDS);
		} catch (EduException e) {
			shopSrcList = NO_DIC;
			e.printStackTrace();
		}

		return shopSrcList;
	}

	// 门店状态下拉菜单
	public List<Dictionary> getShopStatusList(Integer companyId) {

		if (companyId == null || companyId == 0) {
			return null;
		}

		List<Dictionary> shopSrcList = null;

		try {
			shopSrcList = dictionaryService.getDictionaryListByType(companyId, DicConts.SHOP_STS_IDS);
		} catch (EduException e) {
			shopSrcList = NO_DIC;
			e.printStackTrace();
		}

		return shopSrcList;
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