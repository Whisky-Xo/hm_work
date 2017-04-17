package com.crm.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Company_dingApp;
import com.crm.model.DingApp;

public interface DingAppService {

	public int createDingApp(DingApp app);

	public int updateDingApp(DingApp app);

	public DingApp getDingAppById(@Param("id") int id);

	public List<DingApp> listDingAppByCompanyId(Integer companyId);

	public List<DingApp> getDingApps();

	public List<DingApp> getDingAppsByComaonyId(int companyId);

	public Company_dingApp getCompanyDingApp(int companyId, int appId);

	public int updateCompanyDingApp(Company_dingApp companyDingApp);

	public int createCompanyDingApp(Company_dingApp companyDingApp);

}
