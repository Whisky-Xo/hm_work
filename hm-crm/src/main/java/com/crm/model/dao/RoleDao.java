package com.crm.model.dao;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.crm.model.Role;

/**
 * DAO : role 角色
 * 
 * @author sunquan 2016-09-09 09:25
 *
 */
public interface RoleDao {

	/*-- 新建角色 --*/
	public int createRole(Role role);
	
	/*-- 新建角色权限关联--*/
	public int createRolePermissionRelation(HashMap<String, Object> hashMap);

	/*-- 根据ID删除指定角色 --*/
	public int removeRoleById(Role role);
	
	/*-- 删除角色 权限关联--*/
	public int deleteRolePermissionRelation(int roleId);

	/*-- 根据ID编辑指定角色信息 --*/
	public int editRoleById(Role role);

	/*-- 根据ID获取指定角色信息 --*/
	public Role getRoleInfoById(@Param("roleId") int id);

	/*-- 获取渠道列表 --*/
	public List<Role> getRoleList(Role role);
}