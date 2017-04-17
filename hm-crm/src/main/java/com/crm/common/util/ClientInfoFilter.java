package com.crm.common.util;

import java.util.List;

import com.crm.api.constant.ClientInfoConstant;
import com.crm.model.ClientInfo;

/**
 * 客资数据过滤器
 * 
 * @author JingChenglong 2016-12-21 17:53
 *
 */
public class ClientInfoFilter {

	public static String doFilter(List<ClientInfo> clientList, String template) {

		if (clientList == null || clientList.size() == 0 || StringUtil.isEmpty(template)) {
			return "";
		}

		StringBuffer webCodeBuf = new StringBuffer();
		for (ClientInfo client : clientList) {
			webCodeBuf.append(executeFilter(client, template));
			webCodeBuf.append("<br/>");
		}

		return webCodeBuf.toString();
	}

	public static String executeFilter(ClientInfo client, String template) {

		if (client == null || StringUtil.isEmpty(template)) {
			return "";
		}
		template = template.replace("${kzid}", StringUtil.nullToStr(client.getKzId()));
		template = template.replace("${kzname}", StringUtil.nullToStr(client.getKzName()));
		template = template.replace("${kzphone}", StringUtil.nullToStr(client.getKzPhone()));
		template = template.replace("${address}", StringUtil.nullToStr(client.getAddress()));
		template = template.replace("${kzqq}", StringUtil.nullToStr(client.getKzQq()));
		template = template.replace("${qqcolor}", client.getQqFlag() != null && client.getQqFlag() ? "#333" : "#aaa");
		template = template.replace("${kzwechat}", StringUtil.nullToStr(client.getKzWechat()));
		template = template.replace("${wechatcolor}",
				client.getWeChatFlag() != null && client.getWeChatFlag() ? "#333" : "#aaa");
		template = template.replace("${srclabel}", StringUtil.nullToStr(client.getSource()));
		template = template.replace("${invalidlabel}", StringUtil.nullToStr(client.getInvalidLabel()));
		template = template.replace("${memo}", StringUtil.nullToStr(client.getMemo()));
		String srcLabel = StringUtil.nullToStr(client.getSource()) + " "
				+ StringUtil.nullToStrObj(client.getSourceSpare());
		template = template.replace("${sourcelabel}", srcLabel);
		template = template.replace("${promoteid}", StringUtil.nullToStrObj(client.getPromoterId()));
		template = template.replace("${promotename}", StringUtil.nullToStr(client.getPromoter()));
		template = template.replace("${appointid}", StringUtil.nullToStrObj(client.getAppointId()));
		template = template.replace("${appointname}", StringUtil.nullToStr(client.getAppoint()));
		template = template.replace("${shopname}", StringUtil.nullToStr(client.getShopName()));
		template = template.replace("${matename}", StringUtil.nullToStr(client.getMateName()));
		template = template.replace("${matephone}", StringUtil.nullToStr(client.getMatePhone()));
		template = template.replace("${oldkzname}", StringUtil.nullToStr(client.getOldKzName()));
		template = template.replace("${oldkzphone}", StringUtil.nullToStr(client.getOldKzPhone()));
		template = template.replace("${collectorid}", StringUtil.nullToStrObj(client.getCollectorId()));
		String collector = StringUtil.nullToStr(client.getCollector()) + " "
				+ StringUtil.nullToStr(client.getCollectorName());
		template = template.replace("${collectorname}", collector);
		template = template.replace("${tracetime}", TimeUtils.formatClientTime(client.getTraceTime()));
		template = template.replace("${appointtime}", TimeUtils.formatClientTime(client.getAppointTime()));
		template = template.replace("${actualtime}", TimeUtils.formatClientTime(client.getActualTime()));
		template = template.replace("${yptime}", TimeUtils.formatMarryTime(client.getYpTime()));
		template = template.replace("${marrytime}", TimeUtils.formatMarryTime(client.getMarryTime()));
		template = template.replace("${createtime}", TimeUtils.formatClientTime(client.getCreateTime()));
		template = template.replace("${updatetime}", TimeUtils.formatClientTime(client.getUpdateTime()));
		template = template.replace("${stscolor}", StringUtil.nullToStr(client.getStsColor()));
		template = template.replace("${statuslabel}", StringUtil.nullToStr(client.getStatus()));
		template = template.replace("${havemerge}", client.getHaveMerge() != null && client.getHaveMerge() ? "有" : "");
		template = template.replace("${shopname}", StringUtil.nullToStr(client.getShopName()));
		template = template.replace("${amount}", StringUtil.nullToStr(client.getAmount()));
		template = template.replace("${promotername}", StringUtil.nullToStr(client.getPromoter()));
		template = template.replace("${promoterid}", StringUtil.nullToStrObj(client.getPromoterId()));
		template = template.replace("${receptorid}", StringUtil.nullToStrObj(client.getReceptor()));
		template = template.replace("${receptorname}", StringUtil.nullToStr(client.getReceptor()));
		template = template.replace("${smscode}", StringUtil.nullToStr(client.getSmsCode()));
		template = template.replace("${havasms}", client.getHavaSms() == null || !client.getHavaSms() ? "未发" : "已发");
		template = template.replace("${smssts}",
				client.getHavaSms() == null || !client.getHavaSms() ? "notsms" : "yessms");
		template = template.replace("${smscol}", client.getHavaSms() == null || !client.getHavaSms() ? "#CCC" : "#FFF");

		if (StringUtil.isNotEmpty(client.getTraceTime())
				&& (client.getStatusId() == Integer.valueOf(ClientInfoConstant.BE_TRACK)
						|| client.getStatusId() == Integer.valueOf(ClientInfoConstant.BE_TRACK_A)
						|| client.getStatusId() == Integer.valueOf(ClientInfoConstant.BE_TRACK_B)
						|| client.getStatusId() == Integer.valueOf(ClientInfoConstant.BE_TRACK_C))) {
			if (TimeUtils.getCurrentymd().equals(client.getTraceTime().substring(0, 10))) {
				template = template.replace("${trcolor}", "background-color:#FFB6C1;");
			} else {
				template = template.replace("${trcolor}", StringUtil.nullToStr(client.getTrColor()));
			}
		} else {
			template = template.replace("${trcolor}", StringUtil.nullToStr(client.getTrColor()));
		}
		return template;
	}

	public static String doFilterDing(List<ClientInfo> clientList, String template) {

		if (clientList == null || clientList.size() == 0 || StringUtil.isEmpty(template)) {
			return "";
		}

		StringBuffer webCodeBuf = new StringBuffer();
		for (ClientInfo client : clientList) {
			webCodeBuf.append(executeDingFilter(client, template));
			webCodeBuf.append("<br/>");
		}

		return webCodeBuf.toString();
	}

	public static String executeDingFilter(ClientInfo client, String template) {

		if (client == null || StringUtil.isEmpty(template)) {
			return "";
		}
		template = template.replace("${kzid}", StringUtil.nullToStr(client.getKzId()));
		template = template.replace("${kzname}", StringUtil.nullToStrBlank(client.getKzName()));
		template = template.replace("${kzphone}", StringUtil.nullToStrBlank(client.getKzPhone()));
		template = template.replace("${address}", StringUtil.nullToStrBlank(client.getAddress()));
		template = template.replace("${kzqq}", StringUtil.nullToStrBlank(client.getKzQq()));
		template = template.replace("${qqcolor}", client.getQqFlag() != null && client.getQqFlag() ? "#333" : "#aaa");
		template = template.replace("${kzwechat}", StringUtil.nullToStrBlank(client.getKzWechat()));
		template = template.replace("${wechatcolor}",
				client.getWeChatFlag() != null && client.getWeChatFlag() ? "#333" : "#aaa");
		template = template.replace("${srclabel}", StringUtil.nullToStrBlank(client.getSource()));
		template = template.replace("${invalidlabel}", StringUtil.nullToStrBlank(client.getInvalidLabel()));
		template = template.replace("${memo}", StringUtil.nullToStrBlank(client.getMemo()));
		String srcLabel = StringUtil.nullToStr(client.getSource()) + " "
				+ StringUtil.nullToStrObj(client.getSourceSpare());
		template = template.replace("${sourcelabel}", srcLabel);
		template = template.replace("${promoteid}", StringUtil.nullToStrObj(client.getPromoterId()));
		template = template.replace("${promotename}", StringUtil.nullToStr(client.getPromoter()));
		template = template.replace("${appointid}", StringUtil.nullToStrObj(client.getAppointId()));
		template = template.replace("${appointname}", StringUtil.nullToStrBlank(client.getAppoint()));
		template = template.replace("${shopname}", StringUtil.nullToStrBlank(client.getShopName()));
		template = template.replace("${matename}", StringUtil.nullToStrBlank(client.getMateName()));
		template = template.replace("${matephone}", StringUtil.nullToStrBlank(client.getMatePhone()));
		template = template.replace("${oldkzname}", StringUtil.nullToStrBlank(client.getOldKzName()));
		template = template.replace("${oldkzphone}", StringUtil.nullToStrBlank(client.getOldKzPhone()));
		template = template.replace("${collectorid}", StringUtil.nullToStrObj(client.getCollectorId()));
		String collector = StringUtil.nullToStr(client.getCollector()) + " "
				+ StringUtil.nullToStr(client.getCollectorName());
		template = template.replace("${collectorname}", collector);
		template = template.replace("${tracetime}", TimeUtils.formatDingClientTime(client.getTraceTime()));
		template = template.replace("${appointtime}", TimeUtils.formatDingClientTime(client.getAppointTime()));
		template = template.replace("${actualtime}", TimeUtils.formatDingClientTime(client.getActualTime()));
		template = template.replace("${yptime}", TimeUtils.formatMarryTime(client.getYpTime()));
		template = template.replace("${marrytime}", TimeUtils.formatMarryTime(client.getMarryTime()));
		template = template.replace("${createtime}", TimeUtils.formatDingClientTime(client.getCreateTime()));
		template = template.replace("${updatetime}", TimeUtils.formatDingClientTime(client.getUpdateTime()));
		template = template.replace("${stscolor}", StringUtil.nullToStrBlank(client.getStsColor()));
		template = template.replace("${statuslabel}", StringUtil.nullToStr(client.getStatus()));
		template = template.replace("${havemerge}", client.getHaveMerge() != null && client.getHaveMerge() ? "有" : "");
		template = template.replace("${shopname}", StringUtil.nullToStrBlank(client.getShopName()));
		template = template.replace("${amount}", StringUtil.nullToStrBlank(client.getAmount()));
		template = template.replace("${promotername}", StringUtil.nullToStrBlank(client.getPromoter()));
		template = template.replace("${promoterid}", StringUtil.nullToStrObj(client.getPromoterId()));
		template = template.replace("${receptorid}", StringUtil.nullToStrObj(client.getReceptor()));
		template = template.replace("${receptorname}", StringUtil.nullToStrBlank(client.getReceptor()));
		template = template.replace("${smscode}", StringUtil.nullToStrBlank(client.getSmsCode()));
		template = template.replace("${havasms}", client.getHavaSms() == null || !client.getHavaSms() ? "未发" : "已发");
		template = template.replace("${smssts}",
				client.getHavaSms() == null || !client.getHavaSms() ? "notsms" : "yessms");
		template = template.replace("${smscol}", client.getHavaSms() == null || !client.getHavaSms() ? "#CCC" : "#FFF");

		if (StringUtil.isNotEmpty(client.getTraceTime())
				&& (client.getStatusId() == Integer.valueOf(ClientInfoConstant.BE_TRACK)
						|| client.getStatusId() == Integer.valueOf(ClientInfoConstant.BE_TRACK_A)
						|| client.getStatusId() == Integer.valueOf(ClientInfoConstant.BE_TRACK_B)
						|| client.getStatusId() == Integer.valueOf(ClientInfoConstant.BE_TRACK_C))) {
			if (TimeUtils.getCurrentymd().equals(client.getTraceTime().substring(0, 10))) {
				template = template.replace("${trcolor}", "background-color:#FFB6C1;");
			} else {
				template = template.replace("${trcolor}", StringUtil.nullToStr(client.getTrColor()));
			}
		} else {
			template = template.replace("${trcolor}", StringUtil.nullToStr(client.getTrColor()));
		}
		return template;
	}
}