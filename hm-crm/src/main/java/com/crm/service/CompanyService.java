package com.crm.service;

import com.crm.exception.EduException;
import com.crm.model.Company;

/**
 * Service : 企业
 * 
 * @author JingChenglong 2016-09-09 15:04
 *
 */
public interface CompanyService {

	/*-- 新建企业信息 --*/
	public Company createCompany(Company company) throws EduException;

	/*-- 根据ID删除指定企业信息 --*/
	public void removeCompanyById(Company company) throws EduException;

	/*-- 根据ID编辑指定企业信息 --*/
	public void editCompanyById(Company company) throws EduException;

	/*-- 根据ID获取指定企业信息 --*/
	public Company getCompanyInfoById(int companyId) throws EduException;

	/*-- 根据merchantPid获取指定企业信息 --*/
	public Company getCompanyInfoByMerchantPid(String merchantPid);
}