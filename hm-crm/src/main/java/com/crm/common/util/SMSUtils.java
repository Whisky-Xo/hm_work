package com.crm.common.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.crm.exception.EduException;
import com.crm.model.SmsCode;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * 阿里大于：短信工具类
 * 
 * @author JingChenglong 2016-09-10 14:01
 *
 */
public class SMSUtils {

	private static String url;
	private static String appkey;
	private static String secret;
	private static TaobaoClient client;
	private static AlibabaAliqinFcSmsNumSendRequest req;
	private static AlibabaAliqinFcSmsNumSendResponse rsp;

	static {
		url = "http://gw.api.taobao.com/router/rest";
		appkey = "23453403";
		secret = "ef084b61ae7155445d12bebcf4383644";
		client = new DefaultTaobaoClient(url, appkey, secret);
		req = new AlibabaAliqinFcSmsNumSendRequest();
	}

	/** 口碑商户发送新客资记录 */
	public static String sendNewKzAddOrder(String telNo, String kzNo) throws ApiException, EduException {
		req.setSmsType("normal");// 短信类型
		req.setSmsFreeSignName("清莹网口碑客资");// 短信签名，传入的短信签名必须是在阿里大鱼“管理中心-短信签名管理”中的可用签名。
		JSONObject json = new JSONObject();
		try {
			json.put("name", kzNo);
			json.put("product", "清莹网CRM管理中心");
		} catch (JSONException e) {
			e.printStackTrace();
			e.printStackTrace();
			throw new EduException(150029);
		}
		req.setSmsParamString(json.toString());// 短信模板变量，传参规则{"key":"value"}，key的名字须和申请模板中的变量名一致，多个变量之间以逗号隔开。
		req.setRecNum(telNo);// 短信接收号码（短信接收号码。支持单个或多个手机号码，传入号码为11位手机号码，不能加0或+86。群发短信需传入多个号码，以英文逗号分隔，一次调用最多传入200个号码。）
		req.setSmsTemplateCode("SMS_14770732");// 短信模板ID
		rsp = client.execute(req);
		return rsp.getBody();
	}

	/** 金夫人云盘取片验证码 */
	public static String sendYunPanVerifiCodeJinFuRen(String telNo, String code) throws ApiException, EduException {
		req.setSmsType("normal");// 短信类型
		req.setSmsFreeSignName("金夫人");// 短信签名，传入的短信签名必须是在阿里大鱼“管理中心-短信签名管理”中的可用签名。
		JSONObject json = new JSONObject();
		try {
			json.put("code", code);
			json.put("product", "清莹网CRM管理中心");
		} catch (JSONException e) {
			e.printStackTrace();
			e.printStackTrace();
			throw new EduException(150029);
		}
		req.setSmsParamString(json.toString());// 短信模板变量，传参规则{"key":"value"}，key的名字须和申请模板中的变量名一致，多个变量之间以逗号隔开。
		req.setRecNum(telNo);// 短信接收号码（短信接收号码。支持单个或多个手机号码，传入号码为11位手机号码，不能加0或+86。群发短信需传入多个号码，以英文逗号分隔，一次调用最多传入200个号码。）
		req.setSmsTemplateCode("SMS_15510343");// 短信模板ID
		rsp = client.execute(req);
		return rsp.getBody();
	}

	/** 清莹插件短信通知 */
	public static String sendPlugMsgQieIn(String telNo, String plugName) throws ApiException, EduException {
		req.setSmsType("normal");// 短信类型
		req.setSmsFreeSignName("清莹插件");// 短信签名，传入的短信签名必须是在阿里大鱼“管理中心-短信签名管理”中的可用签名。
		JSONObject json = new JSONObject();
		try {
			json.put("name", plugName);
			json.put("product", "清莹网CRM管理中心");
		} catch (JSONException e) {
			e.printStackTrace();
			e.printStackTrace();
			throw new EduException(150029);
		}
		req.setSmsParamString(json.toString());// 短信模板变量，传参规则{"key":"value"}，key的名字须和申请模板中的变量名一致，多个变量之间以逗号隔开。
		req.setRecNum(telNo);// 短信接收号码（短信接收号码。支持单个或多个手机号码，传入号码为11位手机号码，不能加0或+86。群发短信需传入多个号码，以英文逗号分隔，一次调用最多传入200个号码。）
		req.setSmsTemplateCode("SMS_26275290");// 短信模板ID
		rsp = client.execute(req);
		return rsp.getBody();
	}

	/** 客资预约进店短信通知 */
	public static String sendKzComeShopJinFuRen(String telNo, String code, String comeTime, String address,
			String shopTelNo, SmsCode smsCode) throws ApiException, EduException {
		req.setSmsType("normal");// 短信类型
		req.setSmsFreeSignName(StringUtil.nullToStrTrim(smsCode.getSign()));// 短信签名，传入的短信签名必须是在阿里大鱼“管理中心-短信签名管理”中的可用签名。
		JSONObject json = new JSONObject();
		try {
			json.put("code", StringUtil.nullToStrTrim(code));
			json.put("time", StringUtil.nullToStrTrim(comeTime));
			json.put("address", StringUtil.nullToStrTrim(address));
			json.put("telno", StringUtil.nullToStrTrim(shopTelNo));
			json.put("product", "清莹网CRM管理中心");
		} catch (JSONException e) {
			e.printStackTrace();
			e.printStackTrace();
			throw new EduException(150029);
		}
		req.setSmsParamString(json.toString());// 短信模板变量，传参规则{"key":"value"}，key的名字须和申请模板中的变量名一致，多个变量之间以逗号隔开。
		req.setRecNum(telNo);// 短信接收号码（短信接收号码。支持单个或多个手机号码，传入号码为11位手机号码，不能加0或+86。群发短信需传入多个号码，以英文逗号分隔，一次调用最多传入200个号码。）
		req.setSmsTemplateCode(StringUtil.nullToStrTrim(smsCode.getTemplateId()));// 短信模板ID
		DefaultTaobaoClient yyClient = new DefaultTaobaoClient(smsCode.getUrl(), smsCode.getAppKey(),
				smsCode.getSecret());
		rsp = yyClient.execute(req);
		return rsp.getBody();
	}
}