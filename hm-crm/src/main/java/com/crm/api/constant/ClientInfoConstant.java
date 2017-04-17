package com.crm.api.constant;

/**
 * 客资操作常量类
 * 
 * @author JingChenglong 2016-09-13 15:14
 *
 */
public class ClientInfoConstant {

	/*-- 客资状态 --*/
	public static final String BE_WAIT_MAKE_ORDER = "12";// 待接单
	public static final String BE_HAVE_MAKE_ORDER = "1";// 已接单

	public static final String BE_TRACK = "6";// 待追踪
	public static final String BE_TRACK_A = "18";// 待追踪-A
	public static final String BE_TRACK_B = "19";// 待追踪-B
	public static final String BE_TRACK_C = "20";// 待追踪-C
	public static final String NO_ORDER = "13";// 未下单
	public static final String BE_WAITING_CALL_ZJS = "17";// 待邀约
	public static final String BE_WAITING_CALL_A = "2";// 待邀约-A------------------------有效
	public static final String BE_WAITING_CALL_B = "15";// 待邀约-B-----------------------有效
	public static final String BE_WAITING_CALL_C = "16";// 待邀约-C-----------------------有效
	public static final String BE_COMFIRM = "3";// 确定意向--------------------------------有效
	public static final String COME_SHOP_FAIL = "8";// 未到店/定别家------------------------有效
	public static final String INVALID_BE_CHECK = "21";// 无效待审核----------------无效
	public static final String INVALID_BE_STAY = "4";// 无效待审批----------------无效
	public static final String BE_INVALID_REJECT = "7";// 无效驳回
	public static final String BE_INVALID = "5";// 无效--------------------------无效

	/*-- 算到店 --*/
	public static final String BE_SUCCESS = "9";// 成功成交（顺利订单）------------------------有效
	public static final String BE_RUN_OFF = "10";// 流失----------------------------------有效

	/*-- 状态集合 --*/
	public static final String RANGE_BE_TRACK = "6,18,19,20";// 待追踪
	public static final String RANGE_BE_WAITING_CALL = "17,2,15,16";// 待邀约

	public static final String IS_COME_SHOP = "9,10,30,31,32,33,34,35,36";// 算到店
	public static final String IS_SUCCESS = "9,30,31,32,40,41";// 算成交
	public static final String IS_RUN_OFF = "10,33,34,35,36,45,46";// 算流失

	public static final String IS_VALID_ZJS = "17,2,15,16,3,8,9,10,30,31,32,33,34,35,36,40,41,45,46";// 转介绍算有效

	/** -- 客资类型 -- **/
	public static final String TYPE_HUNSHAZHAO = "0";// 婚纱照
	public static final String TYPE_XIEZHEN = "1";// 写真
	public static final String TYPE_QUANJIAFU = "2";// 全家福
	public static final String TYPE_ERTONG = "3";// 儿童

	/** -- 客资分类ID -- **/
	public static final String CLASSID_NEW = "0";// 新客资
	public static final String CLASSID_TRACE = "1";// 待跟踪
	public static final String CLASSID_ORDER = "2";// 已预约
	public static final String CLASSID_COME = "3";// 已进店
	public static final String CLASSID_SUCCESS = "4";// 已定单
	public static final String CLASSID_INVALID = "5";// 无效
}