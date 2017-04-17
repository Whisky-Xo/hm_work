package com.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.DateUtil;
import com.crm.model.Permission;
import com.crm.model.dao.PermissionDao;
import com.crm.service.PermissionService;

/**
 * Service实现：权限信息
 * 
 * @author jzl 2016-10-14 09:55
 *
 */
@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionDao permissionDao;

	@Override
	public List<Permission> getByRole(List<Integer> roleIds) {
		// TODO Auto-generated method stub
		return permissionDao.getByRole(roleIds);
	}

	@Override
	public List<Permission> getByMethod(String method,int staffId) {
		return permissionDao.getByMethod(method,staffId);
	}

	@Override
	public int createPermission(Permission permission) {
		int now = DateUtil.getNow();
		permission.setUpdated(now);
		if (permission.getId() == 0) {
			permission.setCreated(now);
			permissionDao.createPermission(permission);
		} else {
			permissionDao.updatePermission(permission);
		}
		return permission.getId();
	}

	@Override
	public List<Permission> getByPermissionIds(List<Integer> permissionIds,int staffId) {
		// TODO Auto-generated method stub
		return permissionDao.getByPermissionIds(permissionIds,staffId);
	}

	@Override
	public List<Permission> getPermissions(int roleId) {
		
		return permissionDao.getPermissions(roleId);
	}
	
	@Override
	public List<Permission> getByPermissionId(int permissionId,int staffId){
		return permissionDao.getByPermissionId(permissionId,staffId);
	}
	
	@Override
	public List<Permission> getByPermissionByStaffId(int staffId){
		return permissionDao.getByPermissionByStaffId(staffId);
	}
	
	@Override
	public List<Permission> getPermissionByAppId(int roleId,int applicationId){
		return permissionDao.getPermissionByAppId(roleId,applicationId);
	}
}