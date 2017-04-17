package com.crm.service;


import java.util.HashMap;
import java.util.List;

import com.crm.model.Role;

public interface RoleService {

	/*-- 新建角色 --*/
	public int createRole(Role role);
	
	/*-- 新建角色权限关联--*/
	public int createRolePermissionRelation(HashMap<String, Object> hashMap);

	/*-- 根据ID删除指定角色 --*/
	public int removeRoleById(Role role);
	
	/*-- 删除角色 权限关联--*/
	public int deleteRolePermissionRelation(int roleId);

	/*-- 根据ID编辑指定角色 --*/
	public int editRoleById(Role role);

	/*-- 根据ID获取指定角色 --*/
	public Role getRoleInfoById(int roleId);
	
	/*-- 获取渠道列表 --*/
	public List<Role> getRoleList(Role role);
}
