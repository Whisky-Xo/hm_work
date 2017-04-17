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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.Constants;
import com.crm.api.constant.PageConfConst;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.ClientInfoUtil;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.controller.crm.adapter.ClientInfoAdapter;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.Company;
import com.crm.model.PageConf;
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
 * 客资查询列表
 * 
 * @author JingChenglong 2017-02-08 14:30
 *
 */
@Controller
@RequestMapping("/client")
public class ClientQueryController {

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
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/*-- 客资数据列表--*/
	@RequestMapping(value = "kz_query")
	@ResponseBody
	public Model kzQueryFilter(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		String searchKey = maps.get("search_key");
		if (StringUtil.isNotEmpty(searchKey)) {
			// 客资模糊搜索
			return doSearchLike(searchKey, model, request);
		}

		// -- 客资搜索类型
		String searchType = StringUtil.nullToStrTrim(maps.get("search_type"));

		String staffRole = maps.get("staff_role");
		int page = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int size = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "25" : maps.get("size"));
		String timeType = maps.get("time_type");// 查询的时间类型
		String start = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getCurrentymd() : maps.get("start");
		String end = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getCurrentymd() : maps.get("end");

		String filterType = StringUtil.nullToStrTrim(maps.get("filter_type"));
		String filterKey = StringUtil.nullToStrTrim(maps.get("filter_key"));

		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));// 状态ID
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));// 渠道ID
		String shopId = StringUtil.nullToZeroStr(maps.get("shop_id"));// 门店ID

		String searchStaffId = StringUtil.nullToZeroStr(maps.get("search_staff_id"));// 职员ID

		String sort = maps.get("sort");// 排序字段
		int sortCode = ClientInfoUtil.getPageCode(maps);// 排序规则

		if (StringUtil.isNotEmpty(sourceId)) {
			while (true) {
				if (sourceId.endsWith(Constants.STR_SEPARATOR)) {
					sourceId = sourceId.substring(0, sourceId.length() - 1);
				} else {
					break;
				}
			}
		}

		if (StringUtil.isNotEmpty(searchStaffId)) {
			while (true) {
				if (searchStaffId.endsWith(Constants.STR_SEPARATOR)) {
					searchStaffId = searchStaffId.substring(0, searchStaffId.length() - 1);
				} else {
					break;
				}
			}
		}

		Staff staff = getStaffDetail(request);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();

		try {
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("classid", PageConfConst.getClassIdByNav(searchType));
			// reqContent.put("typeid", ClientInfoConstant.TYPE_HUNSHAZHAO);
			reqContent.put("srctype", getSrcType(staffRole));
			reqContent.put(getParamRole(staffRole), searchStaffId);

			if (StringUtil.isValid(statusId)) {
				reqContent.put("statusids", statusId);
			}
			if (StringUtil.isValid(sourceId)) {
				reqContent.put("sourceids", sourceId);
			}
			if (StringUtil.isValid(shopId)) {
				reqContent.put("shopids", shopId);
			}
			if (StringUtil.isNotEmpty(timeType) && StringUtil.isNotEmpty(start) && StringUtil.isNotEmpty(end)) {
				reqContent.put("timetypea", timeType.toUpperCase() + "TIME");
				reqContent.put("timestarta", start + " 00:00:00");
				reqContent.put("timeenda", end + " 23:59:59");
			}
			if (StringUtil.isNotEmpty(filterType) && StringUtil.isNotEmpty(filterKey)) {
				reqContent.put("filtertypea", filterType);
				reqContent.put("filterkeya", filterKey);
			}

			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", sortCode == 1 ? Constants.ORDERSORT_ASC : Constants.ORDERSORT_DESC);
			} else {
				// 录入客资--默认按照客资编号增序
				reqContent.put("sortname1", "info.ID");
				reqContent.put("sorttype1", Constants.ORDERSORT_ASC);
				sort = "info.ID";
				sortCode = 0;
			}
			reqContent.put("pagesize", size);
			reqContent.put("currentpage", page);
			String clientRstStr = crmBaseApi.doService(reqContent, "clientQuery");
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

		PageConf pageConf;
		try {
			pageConf = pageConfService.getPageConfByCompany(Constants.COMP_ID_COMMON,
					StringUtil.nullToStrTrim(searchType).toLowerCase());
			String content = ClientInfoAdapter.doFilter(searchType, clientList, pageConf.getContent());
			model.addAttribute("content", content);
			model.addAttribute("pageInfo", pageInfo);
			model.addAttribute("title", pageConf.getTitleTxt());
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "成功");
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "失败");
		}

		model.addAttribute("sort", sort);
		model.addAttribute("sortCode", sortCode);

		return model;
	}

	/** -- 客资模糊搜索 -- **/
	private Model doSearchLike(String searchKey, Model model, HttpServletRequest request) {

		if (StringUtil.isEmpty(searchKey)) {
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "成功");
			return model;
		}

		Staff staff = getStaffDetail(request);

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		reqContent = new HashMap<String, Object>();

		try {
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("searchkey", searchKey);
			String clientRstStr = crmBaseApi.doService(reqContent, "clientQueryLike");
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

		PageConf pageConf;
		try {
			pageConf = pageConfService.getPageConfByCompany(Constants.COMP_ID_COMMON, PageConfConst.TAB_ALL);
			String content = ClientInfoAdapter.doFilter(PageConfConst.TAB_ALL, clientList, pageConf.getContent());
			model.addAttribute("content", content);
			model.addAttribute("pageInfo", pageInfo);
			model.addAttribute("title", pageConf.getTitleTxt());
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "成功");
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "失败");
		}

		return model;
	}

	/*-- 获取登录人详细信息 --*/
	public Staff getStaffDetail(HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return null;
		}
		return staffService.getStaffInfoById(staff.getId());
	}

	/*-- 获取职员ID集合 --*/
	public String getStaffIdLimit(Staff staff, String staffId) {

		if (staff == null) {
			return "";
		}

		String staffIds = "";

		if (staff.getIsChief()) {
			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工的信息
				staffIds = staffId;
			} else {
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(staff.getDeptId(), staff.getCompanyId());
				for (Staff s : sfs) {
					staffIds += s.getId();
					staffIds += Constants.STR_SEPARATOR;
				}
				if (staffIds.length() != 0) {
					staffIds = staffIds.substring(0, staffIds.length() - 1);
				}
			}
		} else {
			// 如果不是主管，只能查询自己的信息
			staffIds = staff.getId() + "";
		}

		return staffIds;
	}

	/*-- 根据职员身份获取要查询的渠道类型 --*/
	private String getSrcType(String staffRole) {
		if (StringUtil.isEmpty(staffRole)) {
			return "";
		}
		if (staffRole.startsWith(Source.DS_PREFIX)) {
			// 电商
			return Source.SRC_TYPE_DS;
		} else if (staffRole.startsWith(Source.ZJS_PREFIX)) {
			// 转介绍
			return Source.SRC_TYPE_INTRODUCE;
		}

		return "";
	}

	/*-- 根据职员身份获取要过滤的职员类型 --*/
	private String getParamRole(String staffRole) {
		if (StringUtil.isEmpty(staffRole)) {
			return "null";
		}
		if (staffRole.endsWith(Staff.CJ_SUFFIX)) {
			// 采集
			return "collectorids";
		} else if (staffRole.endsWith(Staff.YY_SUFFIX)) {
			// 邀约
			return "appointids";
		} else if (staffRole.endsWith(Staff.TG_SUFFIX)) {
			// 推广
			return "promoterids";
		} else if (staffRole.endsWith(Staff.JD_SUFFIX)) {
			// 接待
			return "receptorids";
		}

		return "null";
	}
}