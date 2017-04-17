package com.crm.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Department;

/**
 * Service : 部门
 * 
 * @author JingChenglong 2016-09-09 17:55
 *
 */
public interface DepartmentService {

	/*-- 新建部门信息 --*/
	public int createDepartment(Department department);

	/*-- 根据ID删除指定部门信息 --*/
	public int removeDepartmentById(Department department);

	/*-- 根据ID编辑指定部门信息 --*/
	public int editDepartmentById(Department department);

	/*-- 根据ID获取指定部门信息 --*/
	public Department getDepartmentInfoById(int departmentId);
	
	/*-- 获取公司下面的所有部门 --*/
	public List<Department> listDepartmentByCompanyId(@Param("companyId") int companyId);
}