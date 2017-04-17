package com.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.DateUtil;
import com.crm.model.Company_dingApp;
import com.crm.model.DingApp;
import com.crm.model.dao.DingAppDao;
import com.crm.service.DingAppService;

@Service
public class DingAppServiceImpl implements DingAppService {

	@Autowired
	private DingAppDao dingAppDao;

	@Override
	public int createDingApp(DingApp app) {
		// TODO Auto-generated method stub
		return dingAppDao.createDingApp(app);
	}

	@Override
	public int updateDingApp(DingApp app) {
		// TODO Auto-generated method stub
		return dingAppDao.updateDingApp(app);
	}

	@Override
	public DingApp getDingAppById(int id) {
		// TODO Auto-generated method stub
		return dingAppDao.getDingAppById(id);
	}

	@Override
	public List<DingApp> listDingAppByCompanyId(Integer companyId) {
		// TODO Auto-generated method stub
		return dingAppDao.listDingAppByCompanyId(companyId);
	}

	@Override
	public List<DingApp> getDingApps() {
		return dingAppDao.getDingApps();
	}

	@Override
	public List<DingApp> getDingAppsByComaonyId(int companyId) {
		return dingAppDao.getDingAppsByComaonyId(companyId);
	}

	@Override
	public Company_dingApp getCompanyDingApp(int companyId, int appId) {
		return dingAppDao.getCompanyDingApp(companyId, appId);
	}

	@Override
	public int updateCompanyDingApp(Company_dingApp companyDingApp) {
		int now = DateUtil.getNow();
		companyDingApp.setUpdated(now);
		return dingAppDao.updateCompanyDingApp(companyDingApp);
	}

	@Override
	public int createCompanyDingApp(Company_dingApp companyDingApp) {
		int now = DateUtil.getNow();
		companyDingApp.setCreated(now);
		companyDingApp.setUpdated(now);
		return dingAppDao.createCompanyDingApp(companyDingApp);
	}
}
