package com.crm.model.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.crm.model.Company_dingApp;
import com.crm.model.DingApp;

@Repository
public interface DingAppDao {

	public int createDingApp(DingApp app);

	public int updateDingApp(DingApp app);

	public DingApp getDingAppById(int id);

	public List<DingApp> listDingAppByCompanyId(Integer companyId);

	public List<DingApp> getDingApps();

	public List<DingApp> getDingAppsByComaonyId(int companyId);

	public Company_dingApp getCompanyDingApp(int companyId, int appId);

	public int updateCompanyDingApp(Company_dingApp companyDingApp);

	public int createCompanyDingApp(Company_dingApp companyDingApp);
}
