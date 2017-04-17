package com.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.annotation.RedisCache;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.Company;
import com.crm.model.Dept;
import com.crm.model.dao.CompanyDao;
import com.crm.service.CompanyService;

/**
 * Service实现：企业信息
 * 
 * @author JingChenglong 2016-09-09 15:07
 *
 */
@Service
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyDao companyDao;// company 企业信息持久化

	/*-- 新增企业信息 --*/
	public Company createCompany(Company company) throws EduException {

		// 校验是否已存在同名企业
		if (companyDao.getCompanyInfoByCompName(company.getCompName()) != null) {
			throw new EduException("存在同名企业");
		}

		// 创建企业
		company.setCreateTime(TimeUtils.getSysTime());
		company.setUpdateTime(TimeUtils.getSysTime());
		company.setIsDel(false);
		if (1 != companyDao.createCompany(company)) {
			throw new EduException("服务器繁忙");
		}

		return company;
	}

	/*-- 根据ID删除指定企业信息 --*/
	public void removeCompanyById(Company company) throws EduException {

		company.setUpdateTime(TimeUtils.getSysTime());

		if (1 != companyDao.removeCompanyById(company)) {
			throw new EduException("企业不存在");
		}
	}

	/*-- 根据ID编辑指定企业信息 --*/
	public void editCompanyById(Company company) throws EduException {

		company.setUpdateTime(TimeUtils.getSysTime());

		if (1 != companyDao.editCompanyById(company)) {
			throw new EduException("更新失败");
		}
	}

	/*-- 根据ID获取指定企业信息 --*/
	@RedisCache(cacheKey = "getCompanyInfoById", name = "根据ID获取指定企业信息",expire=10800,type = Company.class)
	public Company getCompanyInfoById(int companyId) throws EduException {

		Company comp = companyDao.getCompanyInfoById(companyId);

		if (comp == null) {
			throw new EduException("企业不存在");
		}

		return comp;
	}

	/*-- 根据merchantPid获取指定企业信息 --*/
	public Company getCompanyInfoByMerchantPid(String merchantPid) {

		return companyDao.getCompanyInfoByMerchantPid(merchantPid);
	}
}