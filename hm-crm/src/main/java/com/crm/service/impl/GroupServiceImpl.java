package com.crm.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.TimeUtils;
import com.crm.model.Group;
import com.crm.model.dao.GroupDao;
import com.crm.service.GroupService;

/**
 * Service实现：小组信息
 * 
 * @author JingChenglong 2016-09-09 16:51
 *
 */
@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupDao groupDao;// group 小组信息持久化

	/*-- 新增小组信息 --*/
	@Override
	public int createGroup(Group group) {

		group.setCreateTime(TimeUtils.getSysTime());
		group.setUpdateTime(TimeUtils.getSysTime());
		group.setIsDel(false);

		return groupDao.createGroup(group);
	}

	/*-- 根据ID删除指定小组信息 --*/
	@Override
	public int removeGroupById(Group group) {

		group.setUpdateTime(TimeUtils.getSysTime());

		return groupDao.removeGroupById(group);
	}

	/*-- 根据ID编辑指定小组信息 --*/
	@Override
	public int editGroupById(Group group) {

		group.setUpdateTime(TimeUtils.getSysTime());

		return groupDao.editGroupById(group);
	}

	/*-- 根据ID获取指定小组信息 --*/
	@Override
	public Group getGroupInfoById(int groupId) {

		return groupDao.getGroupInfoById(groupId);
	}
	
	/*-- 根据名字获取小组 --*/
	@Override
	public Group getGroupInfoByName(@Param("groupName") String groupName){
		return groupDao.getGroupInfoByName(groupName);
	}
	

	@Override
	public List<Group> listGroupInfoByDepId(int depId) {
		List<Group> groupList = groupDao.listGroupInfoByDepId(depId);
		return groupList;
	}
}