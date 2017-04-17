package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.crm.model.Group;

/**
 * DAO : group 企业小组
 * 
 * @author JingChenglong 2016-09-09 16:54
 *
 */
public interface GroupDao {

	/*-- 新建小组--*/
	public int createGroup(Group group);

	/*-- 根据ID删除指定小组 --*/
	public int removeGroupById(Group group);

	/*-- 根据ID编辑指定小组信息 --*/
	public int editGroupById(Group group);

	/*-- 根据ID获取指定小组信息 --*/
	public Group getGroupInfoById(@Param("groupId") int groupId);
	
	/*-- 根据名字获取小组 --*/
	public Group getGroupInfoByName(@Param("groupName") String groupName);
	
	/*-- 根据部门id获取小组列表 --*/
	public List<Group> listGroupInfoByDepId(@Param("depId") int depId);
}