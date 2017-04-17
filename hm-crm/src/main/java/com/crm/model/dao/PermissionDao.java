package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Permission;

/**
 * DAO : permission 权限
 * 
 * @author JingChenglong 2016-09-11 09:52
 *
 */
public interface PermissionDao {

	public List<Permission> getByRole(@Param("roleIds") List<Integer> roleIds);
	
	public List<Permission> getByMethod(@Param("method") String method,@Param("staffId") int staffId);
	
	public int createPermission(Permission permission);
	
	public int updatePermission(Permission permission);
	
	public List<Permission> getByPermissionIds(@Param("permissionIds") List<Integer> permissionIds,@Param("staffId") int staffId);

	public List<Permission> getPermissions(@Param("roleId") int roleId);
	
	public List<Permission> getByPermissionId(@Param("permissionId") int permissionId,@Param("staffId") int staffId);
	
	public List<Permission> getByPermissionByStaffId(@Param("staffId") int staffId);
	
	public List<Permission> getPermissionByAppId(@Param("roleId") int roleId,@Param("applicationId") int roleIdapplicationId);
	
}