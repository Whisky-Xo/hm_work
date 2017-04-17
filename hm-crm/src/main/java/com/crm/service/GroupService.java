package com.crm.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Group;

/**
 * Service : 小组
 * 
 * @author JingChenglong 2016-09-09 16:49
 *
 */
public interface GroupService {

	/*-- 新建小组信息 --*/
	public int createGroup(Group group);

	/*-- 根据ID删除指定小组信息 --*/
	public int removeGroupById(Group group);

	/*-- 根据ID编辑指定小组信息 --*/
	public int editGroupById(Group group);

	/*-- 根据ID获取指定小组信息 --*/
	public Group getGroupInfoById(int groupId);
	
	/*-- 根据名字获取小组 --*/
	public Group getGroupInfoByName(@Param("groupName") String groupName);
	
	/*-- 根据部门id获取小组列表 --*/
	public List<Group> listGroupInfoByDepId(@Param("depId") int depId);
	
}