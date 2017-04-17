package com.crm.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Staff;

/**
 * Service : 企业
 * 
 * @author JingChenglong 2016-09-09 15:04
 *
 */
public interface StaffService {

	// /*-- 新建员工 --*/
	// public int createStaff(Staff staff);
	//
	// /*-- 新建员工角色关联 --*/
	// public int setStaffRole(Staff staff);
	//
	// /*-- 根据ID删除指定员工 --*/
	// public int removeStaffById(Staff staff);
	//
	/*-- 根据ID编辑指定员工 --*/
	public int editStaffById(Staff staff);

	/*-- 根据ID获取指定员工 --*/
	public Staff getStaffInfoById(int staffId);

	/*-- 员工登陆方法 --*/
	public Staff getStaffInfoByAccount(@Param("username") String username);

	/*-- 获取员工列表 --*/
	public List<Staff> listStaffByCondition(Staff staff);

	/*-- 获取公司员工列表 --*/
	public List<Staff> listStaffByCompanyId(int companyId);

	/*-- 重置todaynumber --*/
	public int resetTodayNumber(Staff staff);

	/*-- 钉钉免登从数据库查找员工信息 --*/
	public Staff getByMobileAndCorpid(String mobile, String corpid);

	/*-- 根据职工的电话号码获取职工信息 --*/
	public Staff getStaffByPhone(Staff staff);

	/*-- 根据部门ID获取职工信息 --*/
	public List<Staff> getByDeptId(String deptId, String XdeptId, Integer companyId);

	/*-- 根据部门ID获取该部门及其下子部门所有职工 --*/
	public List<Staff> getStaffListByDeptId(String deptId, Integer companyId);

	public int changeStaffShowById(int id, int num);

	/*-- 追平职工今日退单额为同范围职工的最小数 --*/
	public void holdLineStaffTodayNum(int id, int companyId, int num);

	/*-- 修改制定员工的日结单限额 --*/
	public void changeStaffLimitNumDay(int id, int num);

	/*-- 获取职工集合今日推单最小数 --*/
	public Integer getTodayNumLine(int id, int companyId);

	/*-- 修改制定员工的接单顺序 --*/
	public void changeStaffJdSort(int id, int sort);

	/*-- 根据部门ID获取部门及其子部门下所有员工-包括删除的员工 --*/
	public List<Staff> getStaffListByDeptIdIgnoDel(String deptId, Integer companyId);

	/*-- 获取指定关联类型和渠道类型的职工集合 --*/
	public List<Staff> getSrcRelaStaffs(String relaType, String srcType, String companyId);
}