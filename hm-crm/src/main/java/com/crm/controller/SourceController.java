package com.crm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.CookieCompoment;
import com.crm.common.util.WebUtils;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Source;
import com.crm.model.SourceStaffRela;
import com.crm.model.SourceType;
import com.crm.model.Staff;
import com.crm.service.CompanyService;
import com.crm.service.SourceService;
import com.crm.service.SourceTypeService;
import com.crm.service.StaffService;


/**
 * 渠道类型管理
 * 
 * @author JingChenglong 2016-09-27 09:23
 *
 */
@Controller
@RequestMapping("source")
public class SourceController {

	@Autowired
	private SourceTypeService sourceTypeService;/*-- 渠道类型 --*/

	@Autowired
	private SourceService sourceService;/*-- 渠道 --*/

	@Autowired
	private CompanyService companyService;/*-- 企业信息 --*/

	@Autowired
	private StaffService staffService;/*-- 职工 --*/

	@RequestMapping("getSrcListDetailByCompId")
	@ResponseBody
	public Model getSrcListDetailByCompId(Model model, HttpServletRequest request) throws EduException {
		Staff staff = CookieCompoment.getLoginUser(request);
		int companyId = staff.getCompanyId();
		Company company = companyService.getCompanyInfoById(companyId);
		/*-- 企业渠道信息集合 --*/
		List<SourceType> srcTypeList = sourceTypeService.getSrcListDetailByCompId(company.getCompId());
		model.addAttribute("srcTypeList", srcTypeList);
		return model;
	}

	/**
	 * 2：新增渠道类型
	 * 
	 * *** typeName (必传) 类型名称 *** *** memo 渠道描述说明 ***
	 */
	@RequestMapping(value = "/add_source_type", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model addSourceType(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 --*/
		String typeName = maps.get("typeName");
		String memo = maps.get("memo");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(typeName)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装 */
		SourceType srcType = new SourceType();
		srcType.setTypeName(typeName);
		srcType.setMemo(memo);
		srcType.setCreateIp(WebUtils.getIP(request));
		srcType.setCreatorId(staff.getId());
		srcType.setCompanyId(staff.getCompanyId());

		/*-- 业务执行 --*/
		if (1 == sourceTypeService.createSourceType(srcType)) {
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "新建渠道类型成功");
		} else {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "新建渠道类型失败");
		}

		return model;
	}

	/**
	 * 3：删除指定ID的渠道类型
	 * 
	 * *** typeid (必传) 类型ID ***
	 */
	@RequestMapping(value = "/remove_source_type", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model removeSourceType(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		/*-- 参数提取 */
		Integer typeId = Integer.valueOf(maps.get("typeid"));
		String code = maps.get("code");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(typeId) || StringUtils.isEmpty(code) || (!"1".equals(code) && !"0".equals(code))) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装 --*/
		SourceType srcType = new SourceType();
		srcType.setTypeId(typeId);
		;
		srcType.setIsShow("1".equals(code));
		srcType.setCompanyId(staff.getCompanyId());

		/*-- 业务执行 --*/
		if (1 == sourceTypeService.updateSourceType(srcType)) {
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "操作成功");
		} else {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "操作失败");
		}

		return model;
	}

	/**
	 * 4：修改指定ID的渠道类型
	 * 
	 * *** typeid (必传) 类型ID *** *** typename 类型名称 *** *** memo 类型描述 ***
	 */
	@RequestMapping(value = "/edit_source_type", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public JSONObject editSourceType(@RequestParam Map<String, String> maps,HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject reply = new JSONObject();
		/*-- 参数提取 --*/
		Integer typeId = Integer.valueOf(maps.get("typeid"));
		String typeName = maps.get("typeName");
		String memo = maps.get("memo");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(typeId)) {
			reply.put("code", "999999");
			reply.put("msg", "参数不完整或格式错误");
			return reply;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装 */
		SourceType srcType = new SourceType();
		srcType.setTypeId(typeId);
		srcType.setTypeName(typeName);
		srcType.setMemo(memo);
		srcType.setUpdateIp(WebUtils.getIP(request));
		srcType.setCompanyId(staff.getCompanyId());

		/*-- 业务执行 --*/
		System.out.println(sourceTypeService.updateSourceType(srcType));
		if (1 == sourceTypeService.updateSourceType(srcType)) {
			reply.put("code", "100000");
			reply.put("msg", "编辑渠道成功");
		} else {
			reply.put("code", "999999");
			reply.put("msg", "编辑渠道失败");
		}

		return reply;
	}

	/**
	 * 5：新增渠道信息
	 * 
	 * *** srcname (必传) 渠道名称 *** *** typeid (必传) 类型ID 
	 * @throws EduException ***
	 */
	@RequestMapping(value = "/add_source", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model addSource(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 --*/
		String srcName = maps.get("srcname");
		Integer typeId = Integer.valueOf(maps.get("typeid"));

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(srcName) || typeId == null || typeId == 0) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装 */
		Source src = new Source();
		src.setSrcName(srcName);
		src.setTypeId(typeId);
		src.setCreateIp(WebUtils.getIP(request));
		src.setCreatorId(staff.getId());
		src.setCompanyId(staff.getCompanyId());

		/*-- 业务执行 --*/
		if (1 == sourceService.createSource(src)) {
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "新建渠道成功");
		} else {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "新建渠道失败");
		}

		return model;
	}

	/**
	 * 6：删除渠道
	 * 
	 * *** srcid (必传) 渠道ID ***
	 */
	@RequestMapping(value = "/remove_source", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model removeSource(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		return model;
	}

	/**
	 * 7：修改渠道信息
	 * 
	 * *** srcid (必传) 渠道ID *** *** typeid 类型ID *** *** srcname 渠道名称 
	 * @throws EduException ***
	 */
	@RequestMapping(value = "/edit_source", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model editSource(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 */
		Integer srcId = Integer.valueOf(maps.get("srcid"));
		Integer typeId = Integer.valueOf(maps.get("typeid"));
		String srcName = maps.get("srcname");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(srcId)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装 --*/
		Source src = new Source();
		src.setSrcId(srcId);
		src.setSrcName(srcName);
		src.setTypeId(typeId);
		src.setCompanyId(staff.getCompanyId());

		/*-- 业务执行 --*/
		if (1 == sourceService.updateSource(src)) {
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "修改信息成功");
		} else {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "修改信息失败");
		}

		return model;
	}

	/**
	 * 8：渠道关闭/开启
	 * 
	 * *** srcid (必传) 渠道ID *** *** code 开启1/关闭0 
	 * @throws EduException ***
	 */
	@RequestMapping(value = "/close_or_show_source", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model closeOrShowSource(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) throws EduException {

		/*-- 参数提取 */
		Integer srcId = Integer.valueOf(maps.get("srcid"));
		String code = maps.get("code");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(srcId) || StringUtils.isEmpty(code) || (!"1".equals(code) && !"0".equals(code))) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装 --*/
		Source src = new Source();
		src.setSrcId(srcId);
		src.setIsShow("1".equals(code));
		src.setCompanyId(staff.getCompanyId());

		/*-- 业务执行 --*/
		if (1 == sourceService.updateSource(src)) {
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "操作成功");
		} else {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "操作失败");
		}

		return model;
	}

	/**
	 * 9：渠道关联采集员/邀约员
	 * 
	 * *** srcids 		(必传) 	渠道IDs 	*** 
	 * *** staffids 	(必传)	采集员IDs ***
	 * *** type			(必传)	关联类型	  采集：cj,邀约:yy***
	 */
	@RequestMapping(value = "/set_src_rela_cj_yy", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model setSrcRelaCjYy(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 */
		String srcIds = maps.get("srcids");
		String staffIds = maps.get("staffids");
		String type = maps.get("type");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(srcIds) || StringUtils.isEmpty(staffIds) || StringUtils.isEmpty(type)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}
		if (!Source.SRC_TYPE_CJ.equals(type) && !Source.SRC_TYPE_YY.equals(type) && !Source.SRC_TYPE_TG.equals(type)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装，业务执行 --*/
		String[] srcIdsArr = srcIds.split(",");
		String[] staffIdsArr = staffIds.split(",");
		try {
			List<SourceStaffRela> relas = new ArrayList<SourceStaffRela>();
			for (int i = 0; i < srcIdsArr.length; i++) {

				relas.clear();
				for (int j = 0; j < staffIdsArr.length; j++) {
					SourceStaffRela rela = new SourceStaffRela();
					rela.setCompanyId(staff.getCompanyId());
					rela.setSrcId(Integer.valueOf(srcIdsArr[i]));
					rela.setStaffId(Integer.valueOf(staffIdsArr[j]));
					rela.setRelaType(type);
					relas.add(rela);
				}

				sourceService.addSrcStaffCjYyRela(relas);
			}
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "操作成功");
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "操作失败");
		}

		return model;
	}

	/**
	 * 10：渠道删除关联采集员/邀约员
	 * 
	 * *** srcids 		(必传) 	渠道IDs 	*** 
	 * *** staffids 	(必传)	采集员IDs ***
	 * *** type			(必传)	关联类型	***
	 */
	@RequestMapping(value = "/remove_src_rela_cj_yy", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model removeSrcRelaCjYy(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 */
		String srcIds = maps.get("srcids");
		String staffIds = maps.get("staffids");
		String type = maps.get("type");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(srcIds) || StringUtils.isEmpty(staffIds) || StringUtils.isEmpty(type)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}
		if (!Source.SRC_TYPE_CJ.equals(type) && !Source.SRC_TYPE_YY.equals(type) && !Source.SRC_TYPE_TG.equals(type)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装，业务执行 --*/
		String[] srcIdsArr = srcIds.split(",");
		String[] staffIdsArr = staffIds.split(",");
		try {
			for (int i = 0; i < srcIdsArr.length; i++) {
				for (int j = 0; j < staffIdsArr.length; j++) {
					SourceStaffRela rela = new SourceStaffRela();
					rela.setCompanyId(staff.getCompanyId());
					rela.setSrcId(Integer.valueOf(srcIdsArr[i]));
					rela.setStaffId(Integer.valueOf(staffIdsArr[j]));
					rela.setRelaType(type);
					sourceService.deleteSrcStaffCjYyRela(rela);
				}
			}
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "操作成功");
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "操作失败");
		}

		return model;
	}

	/**
	 * 11：获取渠道的关联采集员/邀约员
	 * 
	 * *** srcid 		(必传) 	渠道ID 	*** 
	 * *** type			(必传)	关联类型	***
	 */
	@RequestMapping(value = "/get_staffs_for_src", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Model getStaffsForSrc(@RequestParam Map<String, String> maps, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		/*-- 参数提取 */
		String srcId = maps.get("srcid");
		String type = maps.get("type");

		/*-- 参数校验 --*/
		if (StringUtils.isEmpty(srcId) || StringUtils.isEmpty(type)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}
		if (!Source.SRC_TYPE_CJ.equals(type) && !Source.SRC_TYPE_YY.equals(type) && !Source.SRC_TYPE_TG.equals(type)) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "参数不完整或格式错误");
			return model;
		}

		/*-- 职工信息 --*/
		Staff staff = CookieCompoment.getLoginUser(request);
		staff = staffService.getStaffInfoById(staff.getId());

		/*-- 参数封装 --*/
		SourceStaffRela rela = new SourceStaffRela();
		rela.setCompanyId(staff.getCompanyId());
		rela.setSrcId(Integer.valueOf(srcId));
		rela.setRelaType(type);

		/*-- 业务执行 --*/
		try {
			List<Staff> staffList = sourceService.getSrcRelaStaffs(rela);
			model.addAttribute("code", "100000");
			model.addAttribute("msg", "操作成功");
			model.addAttribute("staffList", staffList);
		} catch (EduException e) {
			model.addAttribute("code", "999999");
			model.addAttribute("msg", "操作失败");
		}

		return model;
	}
}