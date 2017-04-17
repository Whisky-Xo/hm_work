package com.crm.api.constant;

/**
 * 常用返回信息
 * 
 * @author JingChenglong 2016-09-08 10:06
 *
 */
public class ResponseConstants {

	public static final int SUCCESS 					= 100000;// 成功
	public static final int SYSTEMMAINTAIN 				= 100001;// 系统维护中
	public static final int SYSTEMBUSY					= 100002;// 系统忙，请稍候再试
	public static final int SERVICEMAINTAIN				= 100003;// 服务维护中
	public static final int REQUEST_METHOD_NOSUPPORT	= 110001;// 请求的HTTP METHOD不支持
	public static final int REQUESTENTITYTOOLARGE		= 110003;// 请求内容长度超过限制
	public static final int SERVICE_INTERFACE_NONE		= 110009;// 接口不存在
	public static final int IP_DENIED 					= 110010;// IP受限，禁止访问
	public static final int PERMISSION_DENIED 			= 110034;// 权限不足，禁止访问
	public static final int SIGNORPWDERROR 				= 110036;// 签名不匹配或密码不正确
	public static final int NORECORD 					= 110042;// 暂无记录
	public static final int RECORDNOTEXISTED 			= 110043;// 记录不存在
	public static final int USERNOTEXISTED 				= 120020;
	public static final int AUTHCODEERROR 				= 120040;
	public static final int ACCESS_TIMEOUT 				= 120093;// accessId超时，请重新登录
	public static final int ACCESS_UPDATE_FAILED 		= 120100;// accessId更新失败或相应的记录没找到
}