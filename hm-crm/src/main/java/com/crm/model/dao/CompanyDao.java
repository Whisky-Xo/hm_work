package com.crm.model.dao;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Company;

/**
 * DAO : company 企业
 * 
 * @author JingChenglong 2016-09-09 09:25
 *
 */
public interface CompanyDao {

	/*-- 新建企业 --*/
	public int createCompany(Company company);

	/*-- 根据ID删除指定企业 --*/
	public int removeCompanyById(Company company);

	/*-- 根据ID编辑指定企业信息 --*/
	public int editCompanyById(Company company);

	/*-- 根据ID获取指定企业信息 --*/
	public Company getCompanyInfoById(@Param("compId") int compId);

	/*-- 根据merchantPid获取指定企业信息 --*/
	public Company getCompanyInfoByMerchantPid(@Param("merchantPid") String merchantPid);
	/*-- 根据企业名称获取企业信息 --*/
	public Company getCompanyInfoByCompName(@Param("compName") String compName);
	
}