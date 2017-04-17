package com.crm.api.constant;

/**
 * 消息常量类
 * 
 * @author JingChenglong 2016-10-11 16:30
 *
 */
public class NewsConts {

	/*-- 消息类型 --*/
	public final static String TYPE_NEW_KZ_ADD = "kzadd";// 新客资提醒
	public final static String TYPE_NEW_TICKET = "ticket";// 罚单
	public final static String TYPE_NEW_WARN = "warntime";// 定时消息
	public final static String TYPE_NEW_COMMON = "common";// 通用消息
	public final static String TYPE_NEW_FLASH = "flash";// 闪信

	/*-- 消息标题 --*/
	public final static String TITLE_NEW_KZ_ADD = "您有一个新客资等待接单";// 新客资等待接单
	public final static String TITLE_KZ_STATUS_CHANGE = "您有一个客资状态被改变";// 客资状态被改变
	public final static String TITLE_NEW_STAFF_TICKET = "您有一个员工超时未接单";// 职工接单超时
	public final static String TITLE_NEW_TICKET = "好悲催，您因为超时未领客资而被开罚单";// 职工接单超时
}