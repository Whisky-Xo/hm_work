package com.crm.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.annotation.RedisCache;
import com.crm.annotation.RedisEvict;
import com.crm.common.util.StringUtil;
import com.crm.common.util.ding.DingUtil;
import com.crm.model.Staff;
import com.crm.model.dao.StaffDao;
import com.crm.service.StaffService;

/**
 * Service实现：员工
 * 
 * @author JingChenglong 2016-09-09 15:07
 *
 */
@Service
public class StaffServiceImpl implements StaffService {

	@Autowired
	private StaffDao staffDao;// staff 员工持久化

	// /*-- 新增员工 --*/
	// public int createStaff(Staff staff) {
	// staff.setCreateTime(TimeUtils.getSysTime());
	// staff.setLastTime(TimeUtils.getSysTime());
	// staff.setIsShow(true);
	// staff.setIsLock(false);
	// staff.setIsDel(false);
	// return staffDao.createStaff(staff);
	// }
	//
	//
	//
	// /*-- 新建员工角色关联 --*/
	// public int setStaffRole(Staff staff){
	// return staffDao.setStaffRole(staff);
	// }
	//
	//
	// /*-- 根据ID删除指定员工 --*/
	// public int removeStaffById(Staff staff) {
	// return staffDao.removeStaffById(staff);
	// }

	/*-- 根据ID编辑指定员工 --*/
	public int editStaffById(Staff staff) {
		return staffDao.editStaffById(staff);
	}

	/*-- 根据ID获取指定员工 --*/
	@RedisCache(cacheKey = "getStaffInfoById", name = "获取员工信息", expire = 7200, type = Staff.class)
	public Staff getStaffInfoById(int staffId) {

		if (staffId == 0) {
			return null;
		}

		Staff staff = staffDao.getStaffInfoById(staffId);
		staff = DingUtil.getDingHeadImg(staff);// 获取钉钉头像

		return staff;
	}

	/*-- 员工登陆方法 --*/
	public Staff getStaffInfoByAccount(@Param("username") String username) {

		return staffDao.getStaffInfoByAccount(username);
	}

	/*-- 获取员工列表 --*/
	public List<Staff> listStaffByCondition(Staff staff) {
		List<Staff> staffList = staffDao.listStaffByCondition(staff);
		return staffList;
	}

	/*-- 获取公司员工列表 --*/
	public List<Staff> listStaffByCompanyId(int companyId) {
		List<Staff> staffList = staffDao.listStaffByCompanyId(companyId);
		return staffList;
	}

	/*-- 重置todaynumber, jdNum --*/
	public int resetTodayNumber(Staff staff) {
		return staffDao.resetTodayNumber(staff);
	}

	/*-- 钉钉免登从数据库查找员工信息 --*/
	public Staff getByMobileAndCorpid(String mobile, String corpid) {
		return staffDao.getByMobileAndCorpid(mobile, corpid);
	}

	/*-- 根据职工的电话号码获取职工信息 --*/
	public Staff getStaffByPhone(Staff staff) {
		return staffDao.getStaffByPhone(staff);
	}

	/*-- 根据部门ID获取职工信息 --*/
	@RedisCache(cacheKey = "getByDeptId", name = "根据部门ID获取职工信息", expire = 10800, type = Staff.class)
	public List<Staff> getByDeptId(String deptId, String XdeptId, Integer companyId) {
		return staffDao.getByDeptId(deptId, XdeptId, companyId);
	}

	/*-- 根据部门ID获取该部门及其下子部门所有职工 --*/
	public List<Staff> getStaffListByDeptId(String deptId, Integer companyId) {
		return staffDao.getStaffListByDeptId(deptId, companyId);
	}

	/*-- 获取职工集合今日推单最小数 --*/
	public Integer getTodayNumLine(int id, int companyId) {
		return staffDao.getTodayNumLine(id, companyId);
	}

	/*-- 追平职工今日退单额为同范围职工的最小数 --*/
	@RedisEvict(type = Staff.class)
	public void holdLineStaffTodayNum(int id, int companyId, int num) {
		staffDao.holdLineStaffTodayNum(id, companyId, num);
	}

	/*-- 修改制定员工的在线/离线状态 --*/
	@RedisEvict(type = Staff.class)
	public int changeStaffShowById(int id, int flag) {
		return staffDao.changeStaffShowById(id, flag);
	}

	/*-- 修改制定员工的日结单限额 --*/
	@RedisEvict(type = Staff.class)
	public void changeStaffLimitNumDay(int id, int num) {
		staffDao.changeStaffLimitNumDay(id, num);
	}

	/*-- 修改制定员工的接单顺序 --*/
	@RedisEvict(type = Staff.class)
	public void changeStaffJdSort(int id, int sort) {
		staffDao.changeStaffJdSort(id, sort);
	}

	/*-- 根据部门ID获取该部门及其下子部门所有职工-包括已删除的员工 --*/
	public List<Staff> getStaffListByDeptIdIgnoDel(String deptId, Integer companyId) {
		if (StringUtil.isEmpty(deptId) || companyId == null || companyId == 0) {
			return null;
		}
		return staffDao.getStaffListByDeptIdIgnoDel(deptId, companyId);
	}

	/*-- 获取指定关联类型和渠道类型的职工集合 --*/
	public List<Staff> getSrcRelaStaffs(String relaType, String srcType, String companyId) {
		if (StringUtil.isEmpty(relaType) || StringUtil.isEmpty(srcType) || StringUtil.isEmpty(companyId)) {
			return null;
		}
		return staffDao.getSrcRelaStaffs(relaType, srcType, companyId);
	}
}