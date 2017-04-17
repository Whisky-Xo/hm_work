package com.crm.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.TimeUtils;
import com.crm.model.Department;
import com.crm.model.dao.DepartmentDao;
import com.crm.service.DepartmentService;

/**
 * Service实现：部门信息
 * 
 * @author JingChenglong 2016-09-09 18:02
 *
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

	@Autowired
	private DepartmentDao departmentDao;// department 部门信息持久化

	/*-- 新增部门信息 --*/
	public int createDepartment(Department department) {

		department.setCreateTime(TimeUtils.getSysTime());
		department.setUpdateTime(TimeUtils.getSysTime());
		department.setIsDel(false);

		return departmentDao.createDepartment(department);
	}

	/*-- 根据ID删除指定部门信息 --*/
	public int removeDepartmentById(Department department) {

		department.setUpdateTime(TimeUtils.getSysTime());

		return departmentDao.removeDepartmentById(department);
	}

	/*-- 根据ID编辑指定部门信息 --*/
	public int editDepartmentById(Department department) {

		department.setUpdateTime(TimeUtils.getSysTime());

		return departmentDao.editDepartmentById(department);
	}

	/*-- 根据ID获取指定部门信息 --*/
	public Department getDepartmentInfoById(int deptId) {

		return departmentDao.getDepartmentInfoById(deptId);
	}

	/*-- 获取公司下面的所有部门 --*/
	public List<Department> listDepartmentByCompanyId(@Param("companyId") int companyId) {
		List<Department> depList = departmentDao.listDepartmentByCompanyId(companyId);
		return depList;
	}
}