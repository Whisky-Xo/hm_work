package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Dept;

/**
 * DAO：部门
 * 
 * @author JingChenglong 2016-09-28 20:12
 *
 */
public interface DeptDao {

	/*-- 创建部门 --*/
	public int createDept(Dept Dept);

	/*-- 编辑部门 --*/
	public int updateDept(Dept Dept);

	/*-- 根据部门ID获取部门信息 --*/
	public Dept getByDeptId(String deptId);

	/*-- 根据主键ID获取部门信息 --*/
	public Dept getById(int id);

	public List<Dept> listDeptsByDeptId(@Param("deptId") String deptId,@Param("XdeptId") String XdeptId,@Param("companyId") int companyId);

	public List<Dept> getByParentId(@Param("deptId") String deptId, @Param("companyId") int companyId);

	public List<Dept> listOpeningDepts(int companyId);
	
	public List<Dept> listDeptsExceptDeptId(@Param("companyId") int companyId,@Param("deptId") String deptId);
	
	public List<Dept> listSubdeptsByDeptId(@Param("deptId") String deptId,@Param("companyId") int companyId);
}
