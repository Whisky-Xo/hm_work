package com.crm.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crm.common.util.TimeUtils;
import com.crm.model.Role;
import com.crm.model.dao.RoleDao;
import com.crm.service.RoleService;

/**
 * Service实现：角色信息
 * 
 * @author sunquan 2016-09-09 15:07
 *
 */
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;// role 角色信息持久化

	/*-- 新增角色信息 --*/
	public int createRole(Role role) {

		role.setCreateTime(TimeUtils.getSysTime());
		role.setUpdateTime(TimeUtils.getSysTime());
		role.setIsDel(false);

		return roleDao.createRole(role);
	}
	
	
	/*-- 新建角色权限关联--*/
	public int createRolePermissionRelation(HashMap<String, Object> hashMap){
		hashMap.put("createTime", TimeUtils.getSysTime());
		return roleDao.createRolePermissionRelation(hashMap);
	}
	

	/*-- 根据ID删除指定角色信息 --*/
	public int removeRoleById(Role role) {

		role.setUpdateTime(TimeUtils.getSysTime());

		return roleDao.removeRoleById(role);
	}
	
	
	/*-- 删除角色 权限关联--*/
	public int deleteRolePermissionRelation(int roleId){
		
		return roleDao.deleteRolePermissionRelation(roleId);
	}

	/*-- 根据ID编辑指定角色信息 --*/
	public int editRoleById(Role role) {

		role.setUpdateTime(TimeUtils.getSysTime());

		return roleDao.editRoleById(role);
	}

	/*-- 根据ID获取指定角色信息 --*/
	public Role getRoleInfoById(int roleId) {
		return roleDao.getRoleInfoById(roleId);
	}
	
	
	/*-- 获取渠道列表 --*/
	public List<Role> getRoleList(Role role){
		List<Role> roleList = roleDao.getRoleList(role);
		return roleList;
	}
	
	
	
}