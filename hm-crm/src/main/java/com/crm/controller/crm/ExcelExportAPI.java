package com.crm.controller.crm;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crm.common.util.*;
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
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
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
 * 客资excel导出导入操作
 * 
 * @author JingChenglong 2017-02-15 17:27
 *
 */
@Controller
@RequestMapping("/client")
public class ExcelExportAPI {

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

	/*-- 客资导出excel--*/
	@RequestMapping(value = "excel_export")
	@ResponseBody
	public void excelExport(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		// -- 客资搜索类型
		String searchType = StringUtil.nullToStrTrim(maps.get("search_type"));

		String staffRole = maps.get("staff_role");
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
			reqContent.put("pagesize", 9999);
			reqContent.put("currentpage", 1);
			String clientRstStr = crmBaseApi.doService(reqContent, "clientQuery");
			JSONObject js = JsonFmtUtil.strToJsonObj(clientRstStr);
			if (js != null) {
				clientList = JsonFmtUtil.jsonArrToClientInfo(js.getJSONArray("infoList"));
			}
		} catch (Exception e) {
			clientList = NO_CLIENT;
		}

		String title = start + "~" + end;
		title += "客资";
		excelControl(response, clientList, staff, title);
	}

	/** -- 客资导出excel -- **/
	private void excelControl(HttpServletResponse response, List<ClientInfo> clientList, Staff staff,
			String excelName) {
		try {
			String[] titles = new String[] { "提报时间", "提报人", "提报渠道", "新客姓名", "新客电话", "新客微信", "新客QQ", "配偶姓名", "配偶电话/微信",
					"婚期", "地址", "追踪备注", "当前状态", "邀约员", "推广员", "跟进时间", "到店时间", "接待门店", "接待门市", "优惠编码", "成交时间", "成交金额",
					"老客姓名", "老客电话/微信" };
			// String[] titles = new String[] { "提报日期", "提报部门", "提报人", "新客姓名",
			// "新客电话", "老客姓名", "老客电话", "新客微信", "客户备注",
			// "区域", "到店日期", "状态", "订单金额" };
			int[] length = new int[] { 19, 12, 12, 15, 18, 18, 18, 15, 15, 22, 25, 63, 15, 15, 17, 26, 26, 15, 19, 20,
					25, 20, 15, 25 };
			// 导出
			response.setContentType("application/x-msdownload;charset=gbk");
			response.setCharacterEncoding("UTF-8");
			String fileNameTemp;
			fileNameTemp = URLEncoder.encode(excelName + "（" + staff.getName() + "）.xls", "UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
			OutputStream os = response.getOutputStream();
			ExcelUtils eu = new ExcelUtils();
			List<Object[]> datas = new ArrayList<Object[]>();
			for (ClientInfo client : clientList) {
				// "","","","","","","",
				// "","","","","","","",
				// "","","到店时间","接待门店","接待门市","优惠编码","成交时间",
				// "成交金额","老客姓名","老客电话/微信"
				Object[] objects = new Object[titles.length];
				objects[0] = client.getCreateTime();// 提报时间
				objects[1] = client.getCollector();// 提报人
				objects[2] = client.getSource();// 提报渠道
				objects[3] = client.getKzName();// 新客姓名
				objects[4] = client.getKzPhone();// 新客电话
				objects[5] = client.getKzWechat();// 新客微信
				objects[6] = client.getKzQq();// 新客QQ
				objects[7] = client.getMateName();// 配偶姓名
				objects[8] = client.getMatePhone();// 配偶电话/微信
				objects[9] = client.getMarryTime();// 婚期
				objects[10] = client.getAddress();// 地址
				objects[11] = client.getMemo();// 追踪备注
				objects[12] = client.getStatus() + "," + client.getStsColor();// 当前状态
				objects[13] = client.getAppoint();// 邀约员
				objects[14] = client.getPromoter();// 推广员
				objects[15] = client.getUpdateTime();// 跟进时间
				objects[16] = client.getComeShopTime();// 到店时间
				objects[17] = client.getShopName();// 接待门店
				objects[18] = client.getReceptor();// 接待门市
				objects[19] = client.getSmsCode();// 优惠编码
				objects[20] = client.getSuccessTime();// 成交时间
				objects[21] = client.getAmount();// 成交金额
				objects[22] = client.getOldKzName();// 老客姓名
				objects[23] = client.getOldKzPhone();// 老客电话/微信
				datas.add(objects);
			}
			eu.exportColor(os, titles, length, datas);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*-- 获取登录人详细信息 --*/
	public Staff getStaffDetail(HttpServletRequest request) {
		Staff staff = CookieCompoment.getLoginUser(request);
		if (staff == null) {
			return null;
		}
		return staffService.getStaffInfoById(staff.getId());
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
}