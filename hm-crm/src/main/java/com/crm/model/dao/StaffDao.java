package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Staff;

/**
 * DAO : staff 员工
 * 
 * @author JingChenglong 2016-09-09 09:25
 *
 */
public interface StaffDao {

	// /*-- 新建员工 --*/
	// public int createStaff(Staff staff);
	//
	// /*-- 设置员工角色关联 --*/
	// public int setStaffRole(Staff staff);
	//
	// /*-- 删除指定员工 --*/
	// public int removeStaffById(Staff staff);
	//

	/*-- 根据ID编辑指定员工 --*/
	public int editStaffById(Staff staff);

	/*-- 根据ID获取指定员工 --*/
	public Staff getStaffInfoById(@Param("id") int id);

	/*-- 员工登陆方法 --*/
	public Staff getStaffInfoByAccount(@Param("username") String usename);

	/*-- 获取员工列表 --*/
	public List<Staff> listStaffByCondition(Staff staff);

	/*-- 获取公司员工列表 --*/
	public List<Staff> listStaffByCompanyId(int companyId);

	/*-- 重置todaynumber, jdNum --*/
	public int resetTodayNumber(Staff staff);

	/*-- 职工当日客资推送个数++ --*/
	public void doTodayKzNumAdd(Staff staff);

	/*-- 钉钉免登从数据库查找员工信息 --*/
	public Staff getByMobileAndCorpid(String mobile, String corpid);

	/*-- 根据职工的电话号码获取职工信息 --*/
	public Staff getStaffByPhone(Staff staff);

	/*-- 根据部门ID获取职工信息 --*/
	public List<Staff> getByDeptId(@Param("deptId") String deptId, @Param("XdeptId") String XdeptId,
			@Param("companyId") Integer companyId);

	/*-- 根据部门ID获取该部门及其下子部门所有职工 --*/
	public List<Staff> getStaffListByDeptId(@Param("deptId") String deptId, @Param("companyId") Integer companyId);

	/*-- 获取指定部门的同级别组长 --*/
	public List<Staff> getBossStaff(Staff staff);

	/*-- 修改制定员工的在线/离线状态 --*/
	public int changeStaffShowById(@Param("id") int id, @Param("flag") int flag);

	/*-- 修改制定员工的日结单限额 --*/
	public void changeStaffLimitNumDay(@Param("id") int id, @Param("num") int num);

	/*-- 修改制定员工的接单顺序 --*/
	public void changeStaffJdSort(@Param("id") int id, @Param("sort") int sort);

	/*-- 获取职工集合今日推单最小数 --*/
	public Integer getTodayNumLine(@Param("id") int id, @Param("companyId") int companyId);

	/*-- 追平职工今日退单额为同范围职工的最小数 --*/
	public void holdLineStaffTodayNum(@Param("id") int id, @Param("companyId") int companyId, @Param("num") int num);

	/*-- 根据部门ID获取该部门及其下子部门所有职工-包括离职员工 --*/
	public List<Staff> getStaffListByDeptIdIgnoDel(@Param("deptId") String deptId,
			@Param("companyId") Integer companyId);

	/*-- 获取指定渠道，指定职工ID，且为该渠道邀约员的职工信息 --*/
	public Staff getStaffByIdAndYySrcId(@Param("srcId") int srcId, @Param("staffId") int staffId,
			@Param("companyId") int companyId);

	/*-- 获取指定关联类型和渠道类型的职工集合 --*/
	public List<Staff> getSrcRelaStaffs(@Param("relaType") String relaType, @Param("srcType") String srcType,
			@Param("companyId") String companyId);
}