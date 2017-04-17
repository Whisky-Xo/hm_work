package com.crm.common.util;

import com.crm.model.ClientInfo;
import com.crm.model.Staff;
import com.crm.model.TimeWarn;

/**
 * 消息推送工具类
 * 
 * @author JingChenglong 2016-11-04 11:36
 *
 */
public class PushUtil {

	/*-- 新客资提醒 --*/
	public static void pushNewAdd(Staff staff, ClientInfo clientInfo) {
		GoEasyUtil.pushNewAdd(staff, clientInfo);
		DingPushUtil.pushNewAdd(staff, clientInfo);
	}

	/*-- 罚单消息 --*/
	public static void pushTicket(Staff boss, Staff staff) {
		GoEasyUtil.pushTicket(boss, staff);
		DingPushUtil.pushTicket(boss, staff);
	}

	/*-- 定时消息 --*/
	public static void pushTimeWarn(Staff staff, TimeWarn task) {
		GoEasyUtil.pushTimeWarn(staff, task);
		DingPushUtil.pushTimeWarn(staff, task);
	}

	/*-- 通用消息 --*/
	public static void pushCommonMsg(Staff staff, String msg) {
		GoEasyUtil.pushCommonMsg(staff, msg);
		DingPushUtil.pushCommonMsg(staff, msg);
	}

	/*-- 重复客资邀约员提醒消息 --*/
	public static void pushReClient(ClientInfo client, Staff collector, Staff appointer) {
		GoEasyUtil.pushReClient(client, collector, appointer);
		DingPushUtil.pushReClient(client, collector, appointer);
	}

	/*-- 邀约无效驳回消息 --*/
	public static void pushYyValidReject(Staff staff, ClientInfo client, String memo) {
		GoEasyUtil.pushYyValidReject(staff, client, memo);
		DingPushUtil.pushYyValidReject(staff, client, memo);
	}

	/*-- 采集被判无效消息 --*/
	public static void pushCjValid(Staff staff, ClientInfo client, String memo) {
		GoEasyUtil.pushCjValid(staff, client, memo);
		DingPushUtil.pushCjValid(staff, client, memo);
	}

	/*-- 成功订单通知 --*/
	public static void pushSuccessOrder(Staff staff, ClientInfo client) {
		GoEasyUtil.pushSuccessOrder(staff, client);
		DingPushUtil.pushSuccessOrder(staff, client);
	}

	/*-- 门店流失通知 --*/
	public static void pushShopFailMeet(Staff staff, ClientInfo client, String reason) {
		GoEasyUtil.pushShopFailMeet(staff, client, reason);
		DingPushUtil.pushShopFailMeet(staff, client, reason);
	}

	/*-- 客资被合并通知 --*/
	public static void pushClientBeMerged(Staff staff, ClientInfo viceClient, ClientInfo mainClient) {
		GoEasyUtil.pushClientBeMerged(staff, viceClient, mainClient);
		DingPushUtil.pushClientBeMerged(staff, viceClient, mainClient);
	}

	/*-- 闪信 --*/
	public static void pushFlashMsg(Staff staff, String msg, Staff sender) {
		GoEasyUtil.pushFlashMsg(staff, msg, sender);
	}
}