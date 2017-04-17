package com.crm.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.annotation.RedisCache;
import com.crm.common.util.DateUtil;
import com.crm.model.Dept;
import com.crm.model.Status;
import com.crm.model.dao.DeptDao;
import com.crm.service.DeptService;

/**
 * ServiceImpl：部门
 * 
 * @author JingChenglong 2016-09-28 20:11
 *
 */
@Service
public class DeptServiceImpl implements DeptService {

	@Autowired
	DeptDao deptDao;/*-- 部门持久层 --*/

	/*-- 新建部门 --*/
	public Dept createDept(Dept dept) {

		String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		dept.setCreateTime(now);
		dept.setUpdateTime(now);
		if (1 != deptDao.createDept(dept)) {

		}
		return dept;
	}

	@Override
	public Dept getByDeptId(String deptId) {
		return deptDao.getByDeptId(deptId);
	}

	@Override
	public Dept getById(int id) {
		return deptDao.getById(id);
	}

	@Override
	public void closeDept(int id) {
		// 1--启用，0--关闭
		Dept dept = getById(id);
		dept.setIsShow(false);
		if (1 != deptDao.updateDept(dept)) {

		}
	}

	@Override
	public void deleteDept(int id) {
		// 1--删除，0--关闭
		Dept dept = getById(id);
		dept.setIsDel(true);
		if (1 != deptDao.updateDept(dept)) {

		}
	}

	@Override
	public void updateDept(Dept dept) {
		String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		dept.setUpdateTime(now);
		if (1 != deptDao.updateDept(dept)) {

		}
	}

	@Override
	public List<Dept> getByParentId(String deptId, int companyId) {
		return deptDao.getByParentId(deptId, companyId);
	}

	@Override
	@RedisCache(cacheKey = "listDeptsByDeptId", name = "根据上级部门id获取下级部门列表",expire=10800,type = Dept.class)
	public List<Dept> listDeptsByDeptId(String deptId, String XdeptId, int companyId) {
		return deptDao.listDeptsByDeptId(deptId, XdeptId, companyId);
	}

	@Override
	public List<Dept> listOpeningDepts(int companyId) {
		return deptDao.listOpeningDepts(companyId);
	}

	@Override
	public List<Dept> listDeptsExceptDeptId(int companyId, String deptId) {
		return deptDao.listDeptsExceptDeptId(companyId, deptId);
	}

	@Override
	public List<Dept> listSubdeptsByDeptId(String deptId, int companyId) {
		return deptDao.listSubdeptsByDeptId(deptId, companyId);
	}
}