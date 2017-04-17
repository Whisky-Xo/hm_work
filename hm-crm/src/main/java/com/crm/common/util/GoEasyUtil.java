package com.crm.common.util;

import java.text.DecimalFormat;
import com.alibaba.fastjson.JSONObject;
import com.crm.api.constant.NewsConts;
import com.crm.model.ClientInfo;
import com.crm.model.Staff;
import com.crm.model.TimeWarn;
import io.goeasy.GoEasy;
import io.goeasy.publish.GoEasyError;
import io.goeasy.publish.PublishListener;

/**
 * GoEasy：消息推送工具类
 * 
 * @author JingChenglong 2016-12-17 11:27
 *
 */
public class GoEasyUtil {

	public static final String APP_KEY = "f8c84801-0ac1-4cbf-98e3-3edd85d78b09";// GoEasy
	public static final String HM_CRM_CHANNEL = "hm_crm_channel";// 频道

	private static DecimalFormat dcmFmt = new DecimalFormat("0.00");// 金额格式化

	public static void publish(String channel, String content) {

		GoEasy goEasy = new GoEasy(APP_KEY);

		goEasy.publish(channel, content, new PublishListener() {
			public void onSuccess() {
			}

			public void onFailed(GoEasyError error) {
				System.out.print("消息发布失败, 错误编码：" + error.getCode() + "错误信息:" + error.getContent());
			}
		});
	}

	public static void main(String[] args) {
		Staff sf = new Staff();
		sf.setCompanyId(3);
		sf.setId(1);
		ClientInfo info = new ClientInfo();
		info.setCompanyId(3);
		info.setKzName("井成龙");
		info.setKzId("1121");
		pushNewAdd(sf, info);
	}

	/*-- 新客资消息  --*/
	public static void pushNewAdd(Staff staff, ClientInfo client) {

		if (staff == null || client == null || staff.getCompanyId() == null || staff.getCompanyId() == 0
				|| staff.getId() == null || staff.getId() == 0) {
			return;
		}

		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_KZ_ADD);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("kz", client);

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 定时消息 --*/
	public static void pushTimeWarn(Staff staff, TimeWarn timeWarn) {

		if (staff == null || timeWarn == null || staff.getCompanyId() == null || staff.getCompanyId() == 0
				|| staff.getId() == null || staff.getId() == 0) {
			return;
		}

		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_WARN);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("msg", timeWarn.getMsg());
		js.put("warntime", timeWarn.getSpare1());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 罚单消息 --*/
	public static void pushTicket(Staff boss, Staff staff) {

		if (boss == null || staff == null) {
			return;
		}

		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_TICKET);
		js.put("staffid", boss.getCompanyId() + "_" + boss.getId());
		js.put("msg", "您好，您部门下有员工超时未接单<br/><br/>姓名：" + staff.getName());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += boss.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += boss.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 通用消息 --*/
	public static void pushCommonMsg(Staff staff, String msg) {

		if (staff == null) {
			return;
		}

		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_COMMON);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("msg", msg);

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 重复客资邀约员提醒消息 --*/
	public static void pushReClient(ClientInfo client, Staff collector, Staff appointer) {

		if (appointer == null) {
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您有客资被二次提报<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;姓名：");
		sb.append(client.getKzName());
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电话：" + client.getKzPhone());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;二次提报人：" + collector.getName());
		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_COMMON);
		js.put("staffid", appointer.getCompanyId() + "_" + appointer.getId());
		js.put("msg", sb.toString());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += appointer.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += appointer.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 邀约无效驳回消息 --*/
	public static void pushYyValidReject(Staff staff, ClientInfo client, String memo) {

		if (staff == null) {
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您提报的无效客资<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;姓名：");
		sb.append(client.getKzName());
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电话：" + client.getKzPhone());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;被驳回无效，请重新邀约。");
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;备注：" + memo);

		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_COMMON);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("msg", sb.toString());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 采集被判无效消息 --*/
	public static void pushCjValid(Staff staff, ClientInfo client, String memo) {

		if (staff == null) {
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您提报的客资<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;姓名：");
		sb.append(client.getKzName());
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电话：" + client.getKzPhone());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;被判定为无效客资。");
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;备注：" + memo);
		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_COMMON);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("msg", sb.toString());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 成功订单通知 --*/
	public static void pushSuccessOrder(Staff staff, ClientInfo client) {

		if (staff == null) {
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("恭喜您，您的客户在门店成功签订协议<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;姓名：");
		sb.append(client.getKzName());
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电话：" + client.getKzPhone());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;接待门店：" + client.getShopName());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;套系金额：" + (StringUtil.isNotEmpty(client.getAmount())
				? dcmFmt.format(Double.valueOf(client.getAmount())) : "未知"));

		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_COMMON);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("msg", sb.toString());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 门店流失通知--*/
	public static void pushShopFailMeet(Staff staff, ClientInfo client, String reason) {

		if (staff == null) {
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您的客户在门店流失<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;姓名：");
		sb.append(client.getKzName());
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电话：" + client.getKzPhone());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;接待门店：" + client.getShopName());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;流失原因：" + reason);
		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_COMMON);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("msg", sb.toString());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 客资被合并 --*/
	public static void pushClientBeMerged(Staff staff, ClientInfo viceClient, ClientInfo mainClient) {

		if (staff == null) {
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("您好，您提报的客资因重复被合并<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;姓名：");
		sb.append(viceClient.getKzName());
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电话：" + viceClient.getKzPhone());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;被合并至：" + mainClient.getKzName());
		sb.append("<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电话：" + mainClient.getKzPhone());
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;微信：" + mainClient.getKzWechat());
		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_COMMON);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("msg", sb.toString());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}

	/*-- 闪信 --*/
	public static void pushFlashMsg(Staff staff, String msg, Staff sender) {

		if (staff == null || sender == null) {
			return;
		}

		JSONObject js = new JSONObject();
		js.put("type", NewsConts.TYPE_NEW_FLASH);
		js.put("staffid", staff.getCompanyId() + "_" + staff.getId());
		js.put("msg", msg);
		js.put("sender", sender.getName());

		String goEasyChannel = HM_CRM_CHANNEL + "_";
		goEasyChannel += staff.getCompanyId();
		goEasyChannel += "_";
		goEasyChannel += staff.getId();

		publish(goEasyChannel, js.toString());
	}
}