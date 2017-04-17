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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.ClientInfoConstant;
import com.crm.api.constant.DicConts;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.common.util.UtilRegex;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.ClientInfoStatis;
import com.crm.model.Company;
import com.crm.model.Dept;
import com.crm.model.Dictionary;
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
import com.crm.service.PermissionService;
import com.crm.service.ShopService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

/**
 * 客资管理_pro
 * 
 * *** 提供pro版本客资录入及查询操作接口 *** *** 包括： *** *** 客资动态分页查询，新增客资，编辑客资 ***
 * 
 * @author JingChenglong 2016-09-14 14:31
 *
 */
@Controller
@RequestMapping("/client")
public class ClientInfoProController {

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

	private static final Company QIEIN = new Company();
	private static final List<Status> NO_STATUS = new ArrayList<Status>();
	private static final List<Source> NO_SOURCE = new ArrayList<Source>();
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/**
	 * 1：获取客资：录入客资--我的客资列表
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** status_id 客资状态ID *** *** source_id 客资渠道ID *** *** search_key
	 * 查询关键字 *** *** deptid 部门ID *** *** staffid 职员ID ***
	 * 
	 */
	@RequestMapping(value = "/list_new", method = RequestMethod.GET)
	public String listClientInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		String statusId = StringUtil.nullToZeroStr(maps.get("status_id"));
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String searchKey = maps.get("search_key");

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int code = StringUtil.nullToIntZero(maps.get("code"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			code = code == 0 ? 1 : 0;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

		/*-- 获取采集员所负责的渠道列表 --*/
		List<Source> sourceMyCj = sourceService.getSrcListDsByStaffCj(staff.getId());
		model.addAttribute("sourcecj", sourceMyCj);

		/*-- 渠道范围限定 --*/
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}

		/*-- 判定职员身份，获取查询采集员的ID集合 --*/
		String collectorIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				collectorIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					collectorIds += s.getId();
					collectorIds += ",";
				}
				if (collectorIds.length() != 0) {
					collectorIds = collectorIds.substring(0, collectorIds.length() - 1);
				}
			}
		} else {
			collectorIds = staff.getId() + "";
		}

		/*-- 客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		Map<String, Object> reqContent = new HashMap<String, Object>();
		try {
			reqContent.put("companyid", staff.getCompanyId());
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 录入客资--默认按照创建时间降序
				reqContent.put("sortname1", "info.CREATETIME");
				reqContent.put("sorttype1", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("collectorids", collectorIds);
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
				} else {
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
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			clientList = new ArrayList<ClientInfo>();
		}

		model.addAttribute("clients", clientList);

		/*-- 分页信息 --*/
		model.addAttribute("total", pageInfo.get("totalcount"));// 总记录数
		model.addAttribute("totalPage", pageInfo.get("totalpage"));// 总页数
		model.addAttribute("size", pageInfo.get("pagesize"));// 分页大小
		model.addAttribute("page", pageInfo.get("currentpage"));// 当前页码

		/*-- 客资统计 --*/
		reqContent.clear();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("collectorids", collectorIds);
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsDsCj");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}

		model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 客资状态下拉菜单 --*/
		List<Status> statusList = getStatusList(staff.getCompanyId(), Status.STATUS_TYPE_DS);
		model.addAttribute("statuses", statusList);

		/*-- 系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", new Company());
		}

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("statusId", statusId);// 状态ID
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("startStr", createTimeStart);// 录入起始时间
		model.addAttribute("endStr", createTimeEnd);// 录入截止时间
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);// 详细
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式

		return "client/list_new";
	}

	@RequestMapping(value = "list_new_ajax", method = RequestMethod.POST)
	@ResponseBody
	public Model listNewAjax(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/

		return model;
	}

	/**
	 * 2：获取客资：无效客资--无效客资列表
	 * 
	 * *** page 当前页码 *** *** size 分页大小 *** *** start 录入开始时间 *** *** end 录入截止时间
	 * *** *** source_id 客资渠道ID *** *** search_key 查询关键字 *** *** deptid 部门ID ***
	 * *** staffid 职员ID ***
	 * 
	 */
	@RequestMapping(value = "/invalid_new", method = RequestMethod.GET)
	public String invalidClientInfos(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		int currentPage = Integer.valueOf(StringUtil.isEmpty(maps.get("page")) ? "1" : maps.get("page"));
		int pageSize = Integer.valueOf(StringUtil.isEmpty(maps.get("size")) ? "15" : maps.get("size"));
		String createTimeStart = StringUtil.isEmpty(maps.get("start")) ? TimeUtils.getStartDayOfMonthDay()
				: maps.get("start");
		String createTimeEnd = StringUtil.isEmpty(maps.get("end")) ? TimeUtils.getSysTime("yyyy-MM-dd")
				: maps.get("end");
		String sourceId = StringUtil.nullToZeroStr(maps.get("source_id"));
		String statusId = ClientInfoConstant.BE_INVALID;
		String invalidCode = StringUtil.nullToZeroStr(maps.get("invalid_code"));
		String searchKey = maps.get("search_key");

		String departId = maps.get("deptid");// 部门ID
		String staffId = maps.get("staffid");// 职员ID

		String sort = maps.get("sort");
		int code = StringUtil.nullToIntZero(maps.get("code"));
		String flag = maps.get("flag");
		if (!"1".equals(flag)) {
			code = code == 0 ? 1 : 0;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("staff", staff);

		/*-- 客资渠道下拉菜单 --*/
		List<Source> sourceList = getSrcList(staff, Source.SRC_TYPE_DS);
		model.addAttribute("sources", sourceList);

		/*-- 渠道限定 --*/
		String sourceIds = "";
		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += ",";
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}

		/*-- 判定职员身份，获取查询采集员的ID集合 --*/
		String collectorIds = "";
		if (staff.getIsChief()) { // 是否是主管

			if (StringUtil.isNotEmpty(staffId) && !"0".equals(staffId)) {
				// 定点查询某职工
				collectorIds = staffId;
			} else {
				if (StringUtil.isEmpty(departId) || "0".equals(departId)) {
					departId = staff.getDeptId();
				}
				// 获取部门下所有员工
				List<Staff> sfs = staffService.getStaffListByDeptIdIgnoDel(departId, staff.getCompanyId());
				for (Staff s : sfs) {
					collectorIds += s.getId();
					collectorIds += ",";
				}
				if (collectorIds.length() != 0) {
					collectorIds = collectorIds.substring(0, collectorIds.length() - 1);
				}
			}
		} else {
			collectorIds = staff.getId() + "";
		}

		/*-- 无效客资信息结果集 --*/
		JSONObject pageInfo = new JSONObject();
		List<ClientInfo> clientList = null;
		Map<String, Object> reqContent = new HashMap<String, Object>();
		try {
			reqContent.put("companyid", staff.getCompanyId());
			if (StringUtil.isNotEmpty(sort)) {
				// 自定义排序
				reqContent.put("sortname1", sort);
				reqContent.put("sorttype1", code == 1 ? "ASC" : "DESC");
			} else {
				// 无效客资--默认按照最后跟进时间降序
				reqContent.put("sortname1", "UPDATETIME");
				reqContent.put("sorttype1", "DESC");
			}
			if (StringUtil.isEmpty(searchKey)) {
				reqContent.put("collectorids", collectorIds);
				reqContent.put("statusids", statusId);
				if (StringUtil.isNotEmpty(createTimeStart)) {
					reqContent.put("createtimestart", createTimeStart + " 00:00:00");
				}
				if (StringUtil.isNotEmpty(createTimeEnd)) {
					reqContent.put("createtimeend", createTimeEnd + " 23:59:59");
				}
				if (StringUtil.isNotEmpty(sourceId) && !"0".equals(sourceId)) {
					reqContent.put("sourceids", sourceId);
				} else {
					reqContent.put("sourceids", sourceIds);
				}
				if (StringUtil.isNotEmpty(invalidCode) && !"0".equals(invalidCode)) {
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
			pageInfo.put("pagesize", 15);
			pageInfo.put("currentpage", 1);
			clientList = new ArrayList<ClientInfo>();
		}
		model.addAttribute("clients", clientList);

		/*-- 分页信息 --*/
		model.addAttribute("total", pageInfo.get("totalcount"));// 总记录数
		model.addAttribute("totalPage", pageInfo.get("totalpage"));// 总页数
		model.addAttribute("size", pageInfo.get("pagesize"));// 分页大小
		model.addAttribute("page", pageInfo.get("currentpage"));// 当前页码

		/*-- 客资统计 --*/
		reqContent.clear();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("collectorids", collectorIds);
		String clientStatisticsRstStr = crmBaseApi.doService(reqContent, "doClientStatisticsCj");
		JSONObject jsStatistics = JsonFmtUtil.strToJsonObj(clientStatisticsRstStr);
		ClientInfoStatis clientInfoStatis = null;
		if (jsStatistics != null) {
			clientInfoStatis = JSONObject.toJavaObject(jsStatistics.getJSONObject("clientInfoStatis"),
					ClientInfoStatis.class);
		}

		model.addAttribute("clientInfoStatis", clientInfoStatis);// 邀约员统计结果

		/*-- 页面系统时间 --*/
		long time = System.currentTimeMillis();
		String date = DateUtil.format(time, "yyyy.MM.dd");
		model.addAttribute("date", date);

		/*-- 企业信息 --*/
		try {
			Company company = companyService.getCompanyInfoById(staff.getCompanyId());
			model.addAttribute("company", company);
		} catch (EduException e) {
			model.addAttribute("company", new Company());
		}

		/*-- 无效原因结果集 --*/
		List<Dictionary> invalidRsnList = null;
		try {
			invalidRsnList = dictionaryService.getDictionaryListByType(staff.getCompanyId(), DicConts.INVALID_REASON);
		} catch (EduException e2) {
			invalidRsnList = new ArrayList<Dictionary>();
		}
		model.addAttribute("invalidRsnList", invalidRsnList);

		/*-- 部门-职工信息 --*/
		List<Dept> depts = deptService.listDeptsByDeptId(staff.getDeptId() + "-", staff.getDeptId(),
				staff.getCompanyId());
		model.addAttribute("depts", depts);
		List<Staff> staffs = staffService.getByDeptId(staff.getDeptId() + "-", staff.getDeptId(), staff.getCompanyId());
		model.addAttribute("staffs", staffs);

		/*-- 页面信息 --*/
		Map<String, String> map = pageControl(request);
		model.addAttribute("pageMap", map);// 页面权限

		/*-- 筛选信息 --*/
		model.addAttribute("sourceId", sourceId);// 渠道ID
		model.addAttribute("startStr", createTimeStart);// 录入起始时间
		model.addAttribute("endStr", createTimeEnd);// 录入截止时间
		model.addAttribute("staffId", staffId);
		model.addAttribute("deptId", departId);
		model.addAttribute("searchkey", searchKey);
		model.addAttribute("invalid_code", invalidCode);
		model.addAttribute("sort", sort);// 排序字段
		model.addAttribute("code", code);// 排序方式
		return "client/invalid_new";
	}

	/**
	 * 3：PC端：电商渠道--新增客资
	 * 
	 * *** name (必传) 客资姓名 *** *** source_id (必传) 渠道ID *** *** phone (必传) 电话号码
	 * *** *** wechat 微信号 *** *** qq QQ *** *** sex 性别 *** *** address 地址 ***
	 * *** remark 备注 *** *** yptime 约排时间 *** *** marrytime 婚期时间 *** *** matename
	 * 配偶姓名 *** *** matephone 配偶电话 ***
	 */
	@RequestMapping(value = "add_new_client", method = RequestMethod.POST)
	@ResponseBody
	public Model createNewClientInfo(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		String kzName = maps.get("name");
		String sourceId = maps.get("source_id");
		String kzPhone = maps.get("phone");
		String kzWeChat = maps.get("wechat");
		String kzQq = maps.get("qq");
		String kzSex = maps.get("sex");
		String address = maps.get("address");
		String memo = maps.get("remark");
		String ypTime = maps.get("yptime");
		String marryTime = maps.get("marrytime");
		String mateName = maps.get("matename");
		String matePhone = maps.get("matephone");

		String ip = WebUtils.getIP(request);

		/*-- 参数校验 --*/
		if (StringUtil.isEmpty(kzName)) {
			model.addAttribute("code", 1002);
			model.addAttribute("msg", "请输入姓名");
			return model;
		}
		if (!StringUtil.isEmpty(kzPhone) && !UtilRegex.validateMobile(kzPhone)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的手机号");
			return model;
		}
		if (!StringUtil.isEmpty(kzQq) && !UtilRegex.validateQq(kzQq)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的qq号");
			return model;
		}
		// 因为微信添加好友时可以通过手机号、QQ号搜索，因此手机号、QQ号也可以通过校验
		if (StringUtil.isNotEmpty(kzWeChat) && !UtilRegex.validateWechat(kzWeChat)
				&& !UtilRegex.validateMobile(kzWeChat) && !UtilRegex.validateQq(kzWeChat)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的微信号");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());
		model.addAttribute("user", staff);

		/*-- 接口调用，业务执行 --*/
		try {
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("ip", ip);
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("sourceid", sourceId);
			reqContent.put("shopid", staff.getShopId());
			reqContent.put("name", kzName);
			reqContent.put("phone", kzPhone);
			reqContent.put("qq", kzQq);
			reqContent.put("wechat", kzWeChat);
			reqContent.put("address", address);
			reqContent.put("sex", kzSex);
			reqContent.put("yptime", ypTime);
			if (StringUtil.isNotEmpty(marryTime)) {
				reqContent.put("marrytime", marryTime);
			}
			reqContent.put("merchantpid", staff.getMerchantPid());
			reqContent.put("memo", memo);
			reqContent.put("collectorid", staff.getId());
			reqContent.put("ip", WebUtils.getIP(request));
			if (StringUtil.isNotEmpty(mateName)) {
				reqContent.put("matename", mateName);
			}
			if (StringUtil.isNotEmpty(matePhone)) {
				reqContent.put("matephone", matePhone);
			}
			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoAdd");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			// 客资新增成功，将客资推送给指定邀约员
			if ("100000".equals(jsInfo.getString("code"))) {
				String kzId = JsonFmtUtil.strContentToJsonObj(addRstStr).getString("kzId");
				synchronized (this) {
					ClientInfo client = new ClientInfo();
					client.setKzId(kzId);
					client.setCompanyId(staff.getCompanyId());
					client = clientInfoService.getClientInfo(client);
					clientInfoService.doClientPush(client);
				}
			} else if ("130004".equals(jsInfo.getString("code")) || "130005".equals(jsInfo.getString("code"))
					|| "130006".equals(jsInfo.getString("code"))) {
				// 重复客资，给邀约员推送提醒
				ClientInfo client = new ClientInfo();
				if (StringUtil.isNotEmpty(kzPhone)) {
					client.setKzPhone(kzPhone);
				}
				if (StringUtil.isNotEmpty(kzQq)) {
					client.setKzQq(kzQq);
				}
				if (StringUtil.isNotEmpty(kzWeChat)) {
					client.setKzWechat(kzWeChat);
				}
				client.setCompanyId(staff.getCompanyId());

				List<ClientInfo> clientList = clientInfoService.getClientInfoLike(client);
				if (clientList != null) {
					Staff appointer = new Staff();
					for (ClientInfo c : clientList) {
						if (c.getAppointId() == null || c.getAppointId() == 0) {
							continue;
						}
						appointer.setCompanyId(c.getCompanyId());
						appointer.setId(c.getAppointId());
						appointer = staffService.getStaffInfoById(appointer.getId());
						if (appointer != null) {
							PushUtil.pushReClient(c, staff, appointer);
						}
					}
				}
			}
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/**
	 * 4.编辑客资
	 * 
	 * *** id (必传) 客资ID *** *** name (必传) 客资姓名 *** *** source_id (必传) 渠道ID ***
	 * *** phone (必传) 电话 *** *** qq QQ *** *** wechat 微信 *** *** address 地址 ***
	 * *** yptime 约拍时间 *** *** marrytime 婚期时间 *** *** matename 配偶姓名 *** ***
	 * matephone 配偶电话 *** *** sex 性别 *** *** remark 备注 ***
	 * 
	 */
	@RequestMapping(value = "update_client_info_pro", method = RequestMethod.POST)
	@ResponseBody
	public Model updateClientInfoPro(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String kzId = maps.get("id");
		String kzName = maps.get("name");
		String sourceId = maps.get("source_id");
		String kzPhone = maps.get("phone");
		String kzQq = maps.get("qq");
		String kzWeChat = maps.get("wechat");
		String address = maps.get("address");
		String ypTime = maps.get("yptime");
		String marryTime = maps.get("marrytime");
		String mateName = maps.get("mateName");
		String sex = maps.get("sex");
		String remark = maps.get("remark");
		String oldKzName = maps.get("oldKzName");
		String oldKzPhone = maps.get("oldKzPhone");
		String matePhone = maps.get("matePhone");

		/*--  参数校验 --*/
		if (StringUtil.isEmpty(kzId)) {
			model.addAttribute("code", 1004);
			model.addAttribute("msg", "请传入id值");
			return model;
		}
		if (StringUtil.isEmpty(kzName)) {
			model.addAttribute("code", 1002);
			model.addAttribute("msg", "请输入姓名");
			return model;
		}
		if (StringUtil.isNotEmpty(kzPhone) && !UtilRegex.validateMobile(kzPhone)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的手机号");
			return model;
		}
		if (StringUtil.isNotEmpty(kzQq) && !UtilRegex.validateQq(kzQq)) {
			model.addAttribute("code", 1004);
			model.addAttribute("msg", "请输入正确的qq号");
			return model;
		}
		// 因为微信添加好友时可以通过手机号、QQ号搜索，因此手机号、QQ号也可以通过校验
		if (StringUtil.isNotEmpty(kzWeChat) && !UtilRegex.validateWechat(kzWeChat)
				&& !UtilRegex.validateMobile(kzWeChat) && !UtilRegex.validateQq(kzWeChat)) {
			model.addAttribute("code", 1003);
			model.addAttribute("msg", "请输入正确的微信号");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 接口调用，业务执行 --*/
		try {
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("id", kzId);
			reqContent.put("name", kzName);
			reqContent.put("sourceid", sourceId);
			reqContent.put("phone", kzPhone);
			reqContent.put("qq", kzQq);
			reqContent.put("wechat", kzWeChat);
			reqContent.put("sex", sex);
			reqContent.put("address", address);
			reqContent.put("yptime", ypTime);
			if (StringUtil.isNotEmpty(marryTime)) {
				reqContent.put("marrytime", marryTime);
			}
			if (StringUtil.isNotEmpty(oldKzName)) {
				reqContent.put("oldkzname", oldKzName);
			}
			if (StringUtil.isNotEmpty(oldKzPhone)) {
				reqContent.put("oldkzphone", oldKzPhone);
			}
			if (StringUtil.isNotEmpty(matePhone)) {
				reqContent.put("matephone", matePhone);
			}
			if (StringUtil.isNotEmpty(mateName)) {
				reqContent.put("matename", mateName);
			}
			reqContent.put("memo", remark);
			reqContent.put("operaid", staff.getId());
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("ip", WebUtils.getIP(request));
			String addRstStr = crmBaseApi.doService(reqContent, "clientInfoEditById");
			JSONObject jsInfo = JsonFmtUtil.strInfoToJsonObj(addRstStr);
			model.addAttribute("code", jsInfo.getString("code"));
			model.addAttribute("msg", jsInfo.getString("msg"));
		} catch (Exception e) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "网络异常");
		}

		return model;
	}

	/**
	 * 5：获取部门下子部门及子部门成员数组
	 * 
	 * *** deptid (必传) 部门ID ***
	 * 
	 */
	@RequestMapping(value = "get_group_staff_list_by_deptid", method = RequestMethod.POST)
	@ResponseBody
	public Model getGroupStaffListByDeptId(@RequestParam Map<String, String> maps, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		Staff staff = CookieCompoment.getLoginUser(request);
		try {
			/*-- 参数提取 --*/
			String deptId = maps.get("deptid");
			List<Staff> staffs = staffService.getByDeptId(deptId + "-", deptId, staff.getCompanyId());
			model.addAttribute("staffs", staffs);
		} catch (Exception e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
		}

		return model;
	}

	/**
	 * 8：客资模糊搜索
	 * 
	 * *** search_key (必传) 关键字 ***
	 */
	@RequestMapping(value = "do_kz_search_like", method = RequestMethod.GET)
	@ResponseBody
	public Model doKzSearchLike(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String searchKey = maps.get("search_key");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(searchKey)) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 接口调用，业务操作 --*/
		try {
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("searchkey", searchKey);
			String clientRstStr = crmBaseApi.doService(reqContent, "doClientQueryLike");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(clientRstStr);
			if ("100000".equals(js.get("code"))) {
				model.addAttribute("code", js.get("code"));
				model.addAttribute("infoList", JsonFmtUtil.strContentToJsonObj(clientRstStr).get("infoList"));
			} else {
				model.addAttribute("code", js.get("code"));
				model.addAttribute("msg", js.get("msg"));
			}
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/**
	 * 9：客资合并
	 * 
	 * *** mainid (必传) 主客资ID *** *** viceids (必传) 待合并客资ID(多个客资逗号分隔)
	 * 
	 * @throws EduException
	 *             ***
	 * 
	 */
	@RequestMapping(value = "do_kz_merge", method = RequestMethod.GET)
	@ResponseBody
	public Model doKzMerge(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		String mainId = maps.get("mainid");
		String viceIds = maps.get("viceids");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(mainId) || StringUtils.isEmpty(viceIds)) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "参数不完整或格式错误");
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 接口调用，业务执行 --*/
		try {
			Map<String, Object> reqContent = new HashMap<String, Object>();
			reqContent.put("mainid", mainId);
			reqContent.put("viceids", viceIds);
			reqContent.put("companyid", staff.getCompanyId());
			reqContent.put("operaid", staff.getId());
			reqContent.put("ip", WebUtils.getIP(request));
			String clientRstStr = crmBaseApi.doService(reqContent, "doClientMerge");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(clientRstStr);
			model.addAttribute("code", js.get("code"));
			model.addAttribute("msg", js.get("msg"));
			if ("100000".equals(js.get("code"))) {
				// 合并成功
				// 获取主客资信息
				ClientInfo mainKz = new ClientInfo();
				mainKz.setKzId(mainId);
				mainKz.setCompanyId(staff.getCompanyId());
				try {
					mainKz = clientInfoService.getClientInfo(mainKz);
					if (mainKz == null) {
						return model;
					}
					// 获取子客资信息
					String[] kzIds = viceIds.split(",");
					ClientInfo viceKz = new ClientInfo();
					Staff collector = null;
					for (String id : kzIds) {
						viceKz.setKzId(id);
						viceKz.setCompanyId(staff.getCompanyId());
						viceKz = clientInfoService.getClientInfo(viceKz);
						if (viceKz == null || viceKz.getCollectorId() == null) {
							continue;
						}
						collector = staffService.getStaffInfoById(viceKz.getCollectorId());
						if (collector != null) {
							PushUtil.pushClientBeMerged(collector, mainKz, viceKz);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 被合并客资消息提醒
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

		return model;
	}

	/**
	 * 10:在线状态
	 * 
	 * *** staffId (必传) 用户ID *** *** status (必传) 在线状态 ***
	 * 
	 */
	@RequestMapping(value = "online", method = RequestMethod.GET)
	@ResponseBody
	public Model online(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String staffId = maps.get("staffId");
		String status = maps.get("status");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(status)) {
			model.addAttribute("code", 999999);
			model.addAttribute("msg", "参数不完整或格式错误");
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		int staffId_ = staff.getId();
		if (!StringUtils.isEmpty(staffId)) {
			staffId_ = Integer.parseInt(staffId);
		}
		staff = staffService.getStaffInfoById(staffId_);

		/*-- 接口调用，业务执行 --*/
		try {
			staff.setIsShow("true".equals(status));
			int t = staffService.editStaffById(staff);
			if (t != 1) {
				throw new EduException();
			}
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "操作成功");
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "网络错误");
		}

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