package com.crm.service;

import java.util.List;

import com.crm.model.Permission;

/**
 * Service : 权限
 * 
 * @author jzl 2016-10-14 09:54
 *
 */
public interface PermissionService {

	/**
	 * 根据role取权限
	 * 
	 * @param role
	 * @return
	 */
	public List<Permission> getByRole(List<Integer> roleIds);
	
	/**
	 * 根据方法和用户id取权限值
	 * 
	 * @param role
	 * @return
	 */
	public List<Permission> getByMethod(String method,int staffId);
	
	/**
	 * 根据权限Id和用户id取权限值
	 * 
	 * @param role
	 * @return
	 */
	public List<Permission> getByPermissionId(int permissionId,int staffId);
	
	/**
	 * 新建权限
	 * 
	 * @param role
	 * @return
	 */
	public int createPermission(Permission permission);
	
	/**
	 * 根据权限ID获取权限对象
	 * 
	 * @param role
	 * @return
	 */
	public List<Permission> getByPermissionIds(List<Integer> permissionIds,int staffId);

	public List<Permission> getPermissions(int roleId);

	public List<Permission> getByPermissionByStaffId(int staffId);
	
	public List<Permission> getPermissionByAppId(int roleId,int applicationId);
}
