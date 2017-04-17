package com.crm.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.crm.common.util.OSSUtil;
import com.crm.service.CompanyService;

@Controller
public class OSSController {

	@Autowired
	CompanyService companyService;

	@RequestMapping("updateBanner")
	@ResponseBody
	public Model updateBanner(@RequestParam("bannerimg") MultipartFile file,
			// @RequestParam(value = "merchant_pid", required = true) String
			// merchant_pid,
			Model model, HttpServletRequest request, HttpSession session) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String date = sdf.format(new Date());
			String fileName = "banner/" + date + file.getOriginalFilename();
			// String savePath = "//root//dev//LOGO//"+fileName;
			// file.transferTo(new File(savePath));
			OSSUtil.putOssObject(fileName, file.getInputStream());

			// 先取到旧banner数据，以便保存心banner后删除旧的
			// 保存banner进company表
			// Staff staff = CookieCompoment.getLoginUser(request);
			// Integer companyId = staff.getCompanyId();
			// Company company = companyService.getById(companyId);
			// String merchantPid = company.getMerchantPid();
			// String path = companyService.getCompanyBanner(merchantPid);
			// companyService.saveCompanyBanner(fileName, merchantPid);
			// if (path != null) {
			// OSSUtil.deleteOssObject(path);
			// }
			model.addAttribute("code", 100);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("code", 200);
			model.addAttribute("msg", "修改banner失败!");
		}
		return model;
	}
}
