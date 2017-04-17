package com.crm.common.util;

import java.text.DecimalFormat;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.ding.OApiException;
import com.crm.model.ClientInfo;
import com.crm.model.Staff;
import com.crm.model.TimeWarn;

/**
 * 消息推送：钉钉消息工具类
 * 
 * @author JingChenglong 2016-11-04 11:33
 *
 */
public class DingPushUtil {

	private static DecimalFormat dcmFmt = new DecimalFormat("0.00");// 金额格式化

	/*-- 新客资消息 --*/
	public static Boolean pushNewAdd(Staff staff, ClientInfo client) {

		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject link = new JSONObject();

		StringBuffer sb = new StringBuffer();
		sb.append("姓名：");
		sb.append(client.getKzName());
		sb.append("\n电话：");
		sb.append(client.getKzPhone() == null || "".equals(client.getKzPhone()) ? client.getKzWechat()
				: client.getKzPhone());
		sb.append("\n渠道：");
		sb.append(client.getSource() + "  " + client.getSourceSpare());

		link.put("messageUrl", "http://oa.qiein.com/hm-crm/outer/do_kz_make_order_ding?kzid=" + client.getKzId()
				+ "&staffid=" + staff.getId());
		link.put("picUrl", "http://oss-cn-hangzhou.aliyuncs.com/hmcrm/hm_dingding_jiedan_ic.png");
		link.put("title", "新客资来了");
		link.put("text", sb.toString());

		msg.put("touser", staff.getDingUserId());
		msg.put("agentid", staff.getAgentId());
		msg.put("msgtype", "link");
		msg.put("link", link);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 罚单消息 --*/
	public static Boolean pushTicket(Staff boss, Staff staff) {

		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(boss.getCompCorpId(), boss.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}

		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();
		String dingMsg = "您好，你部门下有员工超时未接单，姓名：" + staff.getName();
		text.put("content", dingMsg);
		msg.put("touser", boss.getDingUserId());
		msg.put("agentid", boss.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 定时消息 --*/
	public static Boolean pushTimeWarn(Staff staff, TimeWarn timeWarn) {

		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();

		StringBuffer dingMsg = new StringBuffer();
		dingMsg.append("您好，你有新的定时提醒\n时间：");
		dingMsg.append(timeWarn.getSpare1());
		dingMsg.append("\n内容：");
		dingMsg.append(timeWarn.getMsg());

		text.put("content", dingMsg.toString());
		msg.put("touser", staff.getDingUserId());
		msg.put("agentid", staff.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 通用消息 --*/
	public static Boolean pushCommonMsg(Staff staff, String dingMsg) {

		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		dingMsg = dingMsg.replaceAll("<br/>", "\n");

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();

		text.put("content", dingMsg.toString());
		msg.put("touser", staff.getDingUserId());
		msg.put("agentid", staff.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 重复客资邀约员提醒消息 --*/
	public static Boolean pushReClient(ClientInfo client, Staff collector, Staff appointer) {
		if (appointer.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(appointer.getCompCorpId(), appointer.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您有客资被二次提报");
		sb.append("\n姓名：" + client.getKzName());
		sb.append("\n电话：" + client.getKzPhone());
		sb.append("\n二次提报人：" + collector.getName());

		text.put("content", sb.toString());
		msg.put("touser", appointer.getDingUserId());
		msg.put("agentid", appointer.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 邀约无效驳回消息 --*/
	public static Boolean pushYyValidReject(Staff staff, ClientInfo client, String memo) {
		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您提交的无效客资");
		sb.append("\n姓名：" + client.getKzName());
		sb.append("\n电话：" + client.getKzPhone());
		sb.append("\n被驳回无效，请重新邀约。");
		sb.append("\n备注：" + memo);

		text.put("content", sb.toString());
		msg.put("touser", staff.getDingUserId());
		msg.put("agentid", staff.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 采集被判无效消息 --*/
	public static Boolean pushCjValid(Staff staff, ClientInfo client, String memo) {
		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您提报的客资");
		sb.append("\n姓名：" + client.getKzName());
		sb.append("\n电话：" + client.getKzPhone());
		sb.append("\n被判定为无效客资。");
		sb.append("\n备注：" + memo);

		text.put("content", sb.toString());
		msg.put("touser", staff.getDingUserId());
		msg.put("agentid", staff.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 成功订单通知 --*/
	public static Boolean pushSuccessOrder(Staff staff, ClientInfo client) {
		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您提报的客资");
		sb.append("\n姓名：" + client.getKzName());
		sb.append("\n电话：" + client.getKzPhone());
		sb.append("\n已成功签订协议。\n套系金额：" + (StringUtil.isNotEmpty(client.getAmount())
				? dcmFmt.format(Double.valueOf(client.getAmount())) : "未知"));
		sb.append("\n接待门店：" + client.getShopName());

		text.put("content", sb.toString());
		msg.put("touser", staff.getDingUserId());
		msg.put("agentid", staff.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 门店流失通知 --*/
	public static Boolean pushShopFailMeet(Staff staff, ClientInfo client, String reason) {
		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您提报的客资");
		sb.append("\n姓名：" + client.getKzName());
		sb.append("\n电话：" + client.getKzPhone());
		sb.append("\n在门店流失。\n流失原因：" + reason);
		sb.append("\n接待门店：" + client.getShopName());

		text.put("content", sb.toString());
		msg.put("touser", staff.getDingUserId());
		msg.put("agentid", staff.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}

	/*-- 客资被合并通知 --*/
	public static Boolean pushClientBeMerged(Staff staff, ClientInfo viceClient, ClientInfo mainClient) {
		if (staff.getDingUserId() == null) {
			System.out.println("钉钉userId为空请先授权");
			return false;
		}

		String accesstoken;
		try {
			accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您提报的客资因为重复被合并");
		sb.append("\n姓名：" + viceClient.getKzName());
		sb.append("\n电话：" + viceClient.getKzPhone());
		sb.append("\n微信：" + viceClient.getKzWechat());
		sb.append("\n被合并至：" + mainClient.getKzName());
		sb.append("\n电话：" + mainClient.getKzPhone());
		sb.append("\n微信：" + mainClient.getKzWechat());

		text.put("content", sb.toString());
		msg.put("touser", staff.getDingUserId());
		msg.put("agentid", staff.getAgentId());
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response;
		try {
			response = HttpHelper.httpPost(preurl, msg);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(response);
		if (response.get("messageId") != null) {
			return true;
		} else {
			return false;
		}
	}
}