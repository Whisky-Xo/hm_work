package com.crm.controller.crm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.CrmBaseApi;
import com.crm.api.constant.Constants;
import com.crm.api.constant.QieinConts;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.DateUtil;
import com.crm.common.util.JsonFmtUtil;
import com.crm.common.util.RedisUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Permission;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.PermissionService;
import com.crm.service.SourceService;
import com.crm.service.StaffService;
import com.crm.service.StatusService;

/**
 * 数据统计接口
 * 
 * @author JingChenglong 2017-02-15 10:03
 *
 */
@Controller
@RequestMapping("/client")
public class DataAnalysisAPI {

	@Autowired
	CrmBaseApi crmBaseApi;/* 后端接口调用 */

	@Autowired
	StatusService statusService;/* 客资状态 */

	@Autowired
	SourceService sourceService;/* 客资渠道 */

	@Autowired
	PermissionService pmsService;/* 权限管理 */

	@Autowired
	StaffService staffService;/* 职工管理 */

	@Autowired
	PermissionService permissionService; // 权限

	@Autowired
	CompanyService companyService;/* 公司管理 */

	private static final Company QIEIN = new Company();
	private static final String fieldKey = "BigData";
	private static Map<String, Object> reqContent;
	static {
		QIEIN.setCompName(QieinConts.QIEIN);
	}

	/*
	 * 1-1：电商推广数据JSON
	 */
	@RequestMapping(value = "/ds_tg_json_datas_beidou3", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject dsTgJsonDatasBeiDou3(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		JSONObject reply = new JSONObject();

		/*-- 时间维度 --*/
		String start = StringUtil.isNotEmpty(maps.get("start")) ? maps.get("start") : TimeUtils.getCurrentymd();
		String end = StringUtil.isNotEmpty(maps.get("end")) ? maps.get("end") : TimeUtils.getCurrentymd();
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));

		if (StringUtil.isNotEmpty(sourceId)) {
			while (true) {
				if (sourceId.endsWith(Constants.STR_SEPARATOR)) {
					sourceId = sourceId.substring(0, sourceId.length() - 1);
				} else {
					break;
				}
			}
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		RedisUtil redisUtil = new RedisUtil();
		String key = staff.getCompanyId() + start + end + sourceId + "ds_tg_json_datas";
		// 从缓存中取内容
		try {
			String result = redisUtil.hget(fieldKey, key);
			if (!StringUtils.isBlank(result)) {
				return (JSONObject) JSONObject.parse(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");
		reqContent.put("deptid", "0-1-2");
		reqContent.put("sourceid", sourceId);

		try {
			String rstStr = crmBaseApi.doService(reqContent, "dataAnalyzeDsTgBeidou3");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(rstStr);
			if ("100000".equals(js.get("code"))) {
				reply.put("code", js.get("code"));
				reply.put("msg", "成功");
				reply.put("analysis", JsonFmtUtil.strContentToJsonObj(rstStr).get("analysis"));
			} else {
				reply.put("code", js.get("code"));
				reply.put("msg", js.get("msg"));
			}
		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "数据获取失败");
		}

		reply.put("start", start);
		reply.put("end", end);
		reply.put("sourceid", sourceId);
		reply.put("cacheTime", DateUtil.format(new Date(), "MM-dd HH:mm:ss"));

		// 向缓存中添加内容
		try {
			redisUtil.hset(fieldKey, key, reply.toJSONString());
			redisUtil.expire(fieldKey, 3600);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reply;
	}

	/*
	 * 1-2：电商推广无效客资数据JSON
	 */
	@RequestMapping(value = "/ds_invalid_json_datas_beidou3", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject dsInvalidJsonDatasBeiDou3(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		JSONObject reply = new JSONObject();

		/*-- 时间维度 --*/
		String start = StringUtil.isNotEmpty(maps.get("start")) ? maps.get("start") : TimeUtils.getCurrentymd();
		String end = StringUtil.isNotEmpty(maps.get("end")) ? maps.get("end") : TimeUtils.getCurrentymd();
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));

		if (StringUtil.isNotEmpty(sourceId)) {
			while (true) {
				if (sourceId.endsWith(Constants.STR_SEPARATOR)) {
					sourceId = sourceId.substring(0, sourceId.length() - 1);
				} else {
					break;
				}
			}
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 渠道集合 --*/
		List<Source> sourceList = sourceService.getSrcListByType(staff.getCompanyId(), Source.SRC_TYPE_DS);
		reply.put("sources", sourceList);
		if (StringUtil.isEmpty(sourceId) || "0".equals(sourceId)) {
			for (Source source : sourceList) {
				sourceId += Integer.valueOf(source.getSrcId());
				sourceId += ",";
			}
			sourceId = sourceId.substring(0, sourceId.length() - 1);
		}

		RedisUtil redisUtil = new RedisUtil();
		String key = staff.getCompanyId() + start + end + sourceId + "ds_invalid_json_datas";
		// 从缓存中取内容
		try {
			String result = redisUtil.hget(fieldKey, key);
			if (!StringUtils.isBlank(result)) {
				return (JSONObject) JSONObject.parse(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");
		reqContent.put("sourceid", sourceId);

		try {
			String rstStr = crmBaseApi.doService(reqContent, "dataAnalyzeInvalid");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(rstStr);
			if ("100000".equals(js.get("code"))) {
				reply.put("code", js.get("code"));
				reply.put("msg", "成功");
				reply.put("analysis", JsonFmtUtil.strContentToJsonObj(rstStr).get("analysis"));
				reply.put("rsnList", JsonFmtUtil.strContentToJsonObj(rstStr).get("rsnList"));
			} else {
				reply.put("code", js.get("code"));
				reply.put("msg", js.get("msg"));
			}
		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "数据获取失败");
		}

		reply.put("start", start);
		reply.put("end", end);
		reply.put("sourceid", sourceId);
		reply.put("cacheTime", DateUtil.format(new Date(), "MM-dd HH:mm:ss"));

		// 向缓存中添加内容
		try {
			redisUtil.hset(fieldKey, key, reply.toJSONString());
			redisUtil.expire(fieldKey, 3600);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reply;
	}

	/*
	 * 2-1：电商邀约数据JSON
	 */
	@RequestMapping(value = "/ds_yy_json_datas_beidou3", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject dsYyJsonDatasBeiDou3(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject reply = new JSONObject();

		/*-- 时间维度 --*/
		String start = StringUtil.isNotEmpty(maps.get("start")) ? maps.get("start") : TimeUtils.getCurrentymd();
		String end = StringUtil.isNotEmpty(maps.get("end")) ? maps.get("end") : TimeUtils.getCurrentymd();

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");

		RedisUtil redisUtil = new RedisUtil();
		String key = staff.getCompanyId() + start + end + "ds_yy_json_datas";
		// 从缓存中取内容
		try {
			String result = redisUtil.hget(fieldKey, key);
			if (!StringUtils.isBlank(result)) {
				JSONObject resultJS = (JSONObject) JSONObject.parse(result);
				return resultJS;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String rstStr = crmBaseApi.doService(reqContent, "dataAnalyzeDsYyNew");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(rstStr);
			if ("100000".equals(js.get("code"))) {
				reply.put("code", js.get("code"));
				reply.put("msg", "成功");
				reply.put("srcList", JsonFmtUtil.strContentToJsonObj(rstStr).get("srcList"));
				reply.put("analysis", JsonFmtUtil.strContentToJsonObj(rstStr).get("analysis"));
			} else {
				reply.put("code", js.get("code"));
				reply.put("msg", js.get("msg"));
			}
		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "数据获取失败");
		}

		reply.put("start", start);
		reply.put("end", end);
		reply.put("cacheTime", DateUtil.format(new Date(), "MM-dd HH:mm:ss"));

		// 向缓存中添加内容
		try {
			redisUtil.hset(fieldKey, key, reply.toJSONString());
			redisUtil.expire(fieldKey, 3600);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reply;
	}

	/*
	 * 1-1：转介绍推广数据JSON
	 */
	@RequestMapping(value = "/zjs_tg_json_datas_beidou3", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject zjsTgJsonDatas(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		JSONObject reply = new JSONObject();

		/*-- 时间维度 --*/
		String start = StringUtil.isNotEmpty(maps.get("start")) ? maps.get("start") : TimeUtils.getCurrentymd();
		String end = StringUtil.isNotEmpty(maps.get("end")) ? maps.get("end") : TimeUtils.getCurrentymd();
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));

		if (StringUtil.isNotEmpty(sourceId)) {
			while (true) {
				if (sourceId.endsWith(Constants.STR_SEPARATOR)) {
					sourceId = sourceId.substring(0, sourceId.length() - 1);
				} else {
					break;
				}
			}
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		RedisUtil redisUtil = new RedisUtil();
		String key = staff.getCompanyId() + start + end + sourceId + "zjs_tg_json_datas";
		// 从缓存中取内容
		try {
			String result = redisUtil.hget(fieldKey, key);
			if (!StringUtils.isBlank(result)) {
				// return (JSONObject) JSONObject.parse(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");
		reqContent.put("deptid", "0-1-3-6");
		reqContent.put("sourceid", sourceId);

		try {
			String rstStr = crmBaseApi.doService(reqContent, "dataAnalyzeZjsTgNew");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(rstStr);
			if ("100000".equals(js.get("code"))) {
				reply.put("code", js.get("code"));
				reply.put("msg", "成功");
				reply.put("analysis", JsonFmtUtil.strContentToJsonObj(rstStr).get("analysis"));
			} else {
				reply.put("code", js.get("code"));
				reply.put("msg", js.get("msg"));
			}
		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "数据获取失败");
		}

		reply.put("start", start);
		reply.put("end", end);
		reply.put("sourceid", sourceId);
		reply.put("cacheTime", DateUtil.format(new Date(), "MM-dd HH:mm:ss"));

		// 向缓存中添加内容
		try {
			redisUtil.hset(fieldKey, key, reply.toJSONString());
			redisUtil.expire(fieldKey, 3600);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reply;
	}

	/*
	 * 2-1：转介绍邀约数据JSON
	 */
	@RequestMapping(value = "/zjs_yy_json_datas_beidou3", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject dsYyJsonDatas(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject reply = new JSONObject();

		/*-- 时间维度 --*/
		String start = StringUtil.isNotEmpty(maps.get("start")) ? maps.get("start") : TimeUtils.getCurrentymd();
		String end = StringUtil.isNotEmpty(maps.get("end")) ? maps.get("end") : TimeUtils.getCurrentymd();

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		RedisUtil redisUtil = new RedisUtil();
		String key = staff.getCompanyId() + start + end + "dsYyJsonDatas";
		// 从缓存中取内容
		try {
			String result = redisUtil.hget(fieldKey, key);
			if (!StringUtils.isBlank(result)) {
				// return (JSONObject) JSONObject.parse(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");
		reqContent.put("deptid", "0-1-3-7");

		try {
			String rstStr = crmBaseApi.doService(reqContent, "dataAnalyzeZjsYyNew");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(rstStr);
			if ("100000".equals(js.get("code"))) {
				reply.put("code", js.get("code"));
				reply.put("msg", "成功");
				reply.put("analysis", JsonFmtUtil.strContentToJsonObj(rstStr).get("analysis"));
			} else {
				reply.put("code", js.get("code"));
				reply.put("msg", js.get("msg"));
			}
		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "数据获取失败");
		}

		reply.put("start", start);
		reply.put("end", end);
		reply.put("cacheTime", DateUtil.format(new Date(), "MM-dd HH:mm:ss"));

		// 向缓存中添加内容
		try {
			redisUtil.hset(fieldKey, key, reply.toJSONString());
			redisUtil.expire(fieldKey, 3600);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reply;
	}

	/*
	 * 2-2：转介绍推广无效客资数据JSON
	 */
	@RequestMapping(value = "/zjs_invalid_json_datas_beidou3", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject dsInvalidJsonDatas(@RequestParam Map<String, String> maps, HttpServletRequest request,
			HttpServletResponse response) throws EduException {
		JSONObject reply = new JSONObject();

		/*-- 时间维度 --*/
		String start = StringUtil.isNotEmpty(maps.get("start")) ? maps.get("start") : TimeUtils.getCurrentymd();
		String end = StringUtil.isNotEmpty(maps.get("end")) ? maps.get("end") : TimeUtils.getCurrentymd();
		String sourceId = StringUtil.nullToZeroStr(maps.get("sourceid"));

		if (StringUtil.isNotEmpty(sourceId)) {
			while (true) {
				if (sourceId.endsWith(Constants.STR_SEPARATOR)) {
					sourceId = sourceId.substring(0, sourceId.length() - 1);
				} else {
					break;
				}
			}
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 渠道集合 --*/
		List<Source> sourceList = sourceService.getSrcListByType(staff.getCompanyId(), Source.SRC_TYPE_INTRODUCE);
		reply.put("sources", sourceList);
		if (StringUtil.isEmpty(sourceId) || "0".equals(sourceId)) {
			for (Source source : sourceList) {
				sourceId += Integer.valueOf(source.getSrcId());
				sourceId += ",";
			}
			sourceId = sourceId.substring(0, sourceId.length() - 1);
		}

		RedisUtil redisUtil = new RedisUtil();
		String key = staff.getCompanyId() + start + end + sourceId + "dsInvalidJsonDatas";
		// 从缓存中取内容
		try {
			String result = redisUtil.hget(fieldKey, key);
			if (!StringUtils.isBlank(result)) {
				// return (JSONObject) JSONObject.parse(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		reqContent = new HashMap<String, Object>();
		reqContent.put("companyid", staff.getCompanyId());
		reqContent.put("start", start + " 00:00:00");
		reqContent.put("end", end + " 23:59:59");
		reqContent.put("sourceid", sourceId);

		try {
			String rstStr = crmBaseApi.doService(reqContent, "dataAnalyzeInvalid");
			JSONObject js = JsonFmtUtil.strInfoToJsonObj(rstStr);
			if ("100000".equals(js.get("code"))) {
				reply.put("code", js.get("code"));
				reply.put("msg", "成功");
				reply.put("analysis", JsonFmtUtil.strContentToJsonObj(rstStr).get("analysis"));
				reply.put("rsnList", JsonFmtUtil.strContentToJsonObj(rstStr).get("rsnList"));
			} else {
				reply.put("code", js.get("code"));
				reply.put("msg", js.get("msg"));
			}
		} catch (EduException e) {
			reply.put("code", "999999");
			reply.put("msg", "数据获取失败");
		}

		reply.put("start", start);
		reply.put("end", end);
		reply.put("sourceid", sourceId);
		reply.put("cacheTime", DateUtil.format(new Date(), "MM-dd HH:mm:ss"));

		// 向缓存中添加内容
		try {
			redisUtil.hset(fieldKey, key, reply.toJSONString());
			redisUtil.expire(fieldKey, 3600);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reply;
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
}