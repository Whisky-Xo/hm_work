package com.crm.service;

import java.util.List;

import com.crm.model.Dept;

/**
 * Service：部门
 * 
 * @author JingChenglong 2016-09-28 20:10
 *
 */
public interface DeptService {

	/*-- 新建部门 --*/
	public Dept createDept(Dept dept);

	public Dept getByDeptId(String deptId);

	public Dept getById(int id);

	public void closeDept(int id);

	public void deleteDept(int id);

	public void updateDept(Dept dept);

	public List<Dept> getByParentId(String deptId, int companyId);

	public List<Dept> listDeptsByDeptId(String deptId,String XdeptId,int companyId);

	public List<Dept> listOpeningDepts(int companyId);
	
	public List<Dept> listDeptsExceptDeptId(int companyId,String deptId);
	
	public List<Dept> listSubdeptsByDeptId(String deptId,int companyId);
}
