package com.crm.controller.crm.adapter;

import java.util.List;

import com.crm.api.constant.ClientInfoConstant;
import com.crm.api.constant.ImgSrcCont;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.model.ClientInfo;

/**
 * 客资数据过滤器
 * 
 * @author JingChenglong 2016-12-21 17:53
 *
 */
public class ClientInfoComeFilter {

	public static String doFilter(List<ClientInfo> clientList, String template) {

		if (clientList == null || clientList.size() == 0 || StringUtil.isEmpty(template)) {
			return getEmptyContentStr(template);
			// return "";
		}

		StringBuffer webCodeBuf = new StringBuffer();
		for (ClientInfo client : clientList) {
			webCodeBuf.append(executeFilter(client, template));
			webCodeBuf.append("<br/>");
		}

		if (clientList.size() < 25) {
			for (int i = 0; i < 25 - clientList.size(); i++) {
				webCodeBuf.append(getEmptyContentStrOne(template));
				webCodeBuf.append("<br/>");
			}
		}

		return webCodeBuf.toString();
	}

	public static String executeFilter(ClientInfo client, String template) {

		if (client == null || StringUtil.isEmpty(template)) {
			return "";
		}
		// --基础信息
		template = template.replace("${kzid}", StringUtil.nullToStrTrim(client.getKzId()));
		template = template.replace("${kznum}", StringUtil.nullToStrTrim(client.getKzNum()));
		template = template.replace("${kzname}", StringUtil.nullToStrTrim(client.getKzName()));
		template = template.replace("${kzphone}", StringUtil.nullToStr(client.getKzPhone()));
		template = template.replace("${kzqq}", StringUtil.nullToStr(client.getKzQq()));
		template = template.replace("${kzwechat}", StringUtil.nullToStr(client.getKzWechat()));
		template = template.replace("${address}", StringUtil.nullToStr(client.getAddress()));
		template = template.replace("${typename}", StringUtil.nullToStr(client.getTypeName()));
		template = template.replace("${classname}", StringUtil.nullToStr(client.getClassName()));
		template = template.replace("${statusname}", StringUtil.nullToStr(client.getStatus()));
		template = template.replace("${srcname}", StringUtil.nullToStr(client.getSource()));
		template = template.replace("${shopname}", StringUtil.nullToStr(client.getShopName()));
		template = template.replace("${amount}", StringUtil.nullToStr(client.getAmount()));
		template = template.replace("${matename}", StringUtil.nullToStr(client.getMateName()));
		template = template.replace("${matephone}", StringUtil.nullToStr(client.getMatePhone()));
		template = template.replace("${oldkzname}", StringUtil.nullToStr(client.getOldKzName()));
		template = template.replace("${oldkzphone}", StringUtil.nullToStr(client.getOldKzPhone()));
		template = template.replace("${memo}", StringUtil.nullToStr(client.getMemo()));
		template = template.replace("${invalidlabel}", StringUtil.nullToStr(client.getInvalidLabel()));
		template = template.replace("${smscode}", StringUtil.nullToStr(client.getSmsCode()));
		// --参与职员
		template = template.replace("${collectorname}", StringUtil.nullToStr(client.getCollector()));
		template = template.replace("${appointname}", StringUtil.nullToStr(client.getAppoint()));
		template = template.replace("${promotername}", StringUtil.nullToStr(client.getPromoter()));
		template = template.replace("${receptorname}", StringUtil.nullToStr(client.getReceptor()));
		template = template.replace("${collectorid}", StringUtil.nullToStrObj(client.getCollectorId()));
		template = template.replace("${promoterid}", StringUtil.nullToStrObj(client.getPromoterId()));
		template = template.replace("${appointid}", StringUtil.nullToStrObj(client.getAppointId()));
		template = template.replace("${receptorid}", StringUtil.nullToStrObj(client.getReceptor()));
		// --时间
		template = template.replace("${tracetime}", TimeUtils.formatClientTime(client.getTraceTime()));
		template = template.replace("${appointtime}", TimeUtils.formatClientTime(client.getAppointTime()));
		template = template.replace("${actualtime}", TimeUtils.formatClientTime(client.getActualTime()));
		template = template.replace("${yptime}", TimeUtils.formatClientTime(client.getYpTime()));
		template = template.replace("${marrytime}", TimeUtils.formatMarryTime(client.getMarryTime()));
		template = template.replace("${createtime}", TimeUtils.formatClientTime(client.getCreateTime()));
		template = template.replace("${updatetime}", TimeUtils.formatClientTime(client.getUpdateTime()));
		template = template.replace("${receivetime}", TimeUtils.formatClientTime(client.getReceiveTime()));
		// --渲染
		template = template.replace("${srcimg}", StringUtil.nullToStr(client.getSourceImg()));
		template = template.replace("${srcshow}", StringUtil.nullToStr(getSrcShow(client)));
		template = template.replace("${stsshow}", StringUtil.nullToStr(client.getStsColor()));
		template = template.replace("${phoneimg}", ImgSrcCont.PHONE);
		template = template.replace("${qqimg}", client.getQqFlag() ? ImgSrcCont.QQ_ONLINE : ImgSrcCont.QQ_OFFLINE);
		template = template.replace("${weimg}", client.getWeChatFlag() ? ImgSrcCont.WE_ONLINE : ImgSrcCont.WE_OFFLINE);
		template = template.replace("${seximg}", "0".equals(client.getSex()) ? ImgSrcCont.SEX_MAIL
				: "1".equals(client.getSex()) ? ImgSrcCont.SEX_FEMAIL : ImgSrcCont.SEX_NULL);
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

	public static String getEmptyContentStr(String template) {
		StringBuffer webCodeBuf = new StringBuffer();
		for (int i = 0; i < 25; i++) {
			webCodeBuf.append(executeEmptyFill(template));
			webCodeBuf.append("<br/>");
		}

		return webCodeBuf.toString();
	}

	public static String getEmptyContentStrOne(String template) {
		StringBuffer webCodeBuf = new StringBuffer();
		webCodeBuf.append(executeEmptyFill(template));

		return webCodeBuf.toString();
	}

	private static Object executeEmptyFill(String template) {
		template = template.replace(
				"<input class=\"check_box info_id${kzid}\" attr-title=\"${kzid}\" onclick=\"checkId('come','${kzid}')\" value=\"1\" name=\"box_come\" type=\"checkbox\">",
				" ");
		template = template.replace("<img class=\"icon_sex\" src=\"${seximg}\" />", "");
		template = template.replace("<img class=\"icon_sex\" src=\"${phoneimg}\" />", "");
		template = template.replace("<img class=\"icon_sex\" src=\"${weimg}\" />", "");
		template = template.replace(
				"<a href=\"tencent://message/?uin=${kzqq}&Site=&Menu=yes\"><img class=\"icon_sex\" src=\"${qqimg}\" /></a>",
				"");
		template = template.replace("${kzid}", "");
		template = template.replace("${kznum}", "");
		template = template.replace("${kzname}", "");
		template = template.replace("${kzphone}", "");
		template = template.replace("${kzqq}", "");
		template = template.replace("${kzwechat}", "");
		template = template.replace("${address}", "");
		template = template.replace("${typename}", "");
		template = template.replace("${classname}", "");
		template = template.replace("${statusname}", "");
		template = template.replace("${srcname}", "");
		template = template.replace("${shopname}", "");
		template = template.replace("${amount}", "");
		template = template.replace("${matename}", "");
		template = template.replace("${matephone}", "");
		template = template.replace("${oldkzname}", "");
		template = template.replace("${oldkzphone}", "");
		template = template.replace("${memo}", "");
		template = template.replace("${invalidlabel}", "");
		template = template.replace("${smscode}", "");
		template = template.replace("${collectorname}", "");
		template = template.replace("${appointname}", "");
		template = template.replace("${promotername}", "");
		template = template.replace("${receptorname}", "");
		template = template.replace("${collectorid}", "");
		template = template.replace("${promoterid}", "");
		template = template.replace("${appointid}", "");
		template = template.replace("${receptorid}", "");
		// --时间
		template = template.replace("${tracetime}", "");
		template = template.replace("${appointtime}", "");
		template = template.replace("${actualtime}", "");
		template = template.replace("${yptime}", "");
		template = template.replace("${marrytime}", "");
		template = template.replace("${createtime}", "");
		template = template.replace("${updatetime}", "");
		template = template.replace("${receivetime}", "");
		// --渲染
		template = template.replace("${srcimg}", "");
		template = template.replace("${srcshow}", "");
		template = template.replace("${stsshow}", "");
		template = template.replace("${phoneimg}", "");
		template = template.replace("${qqimg}", "");
		template = template.replace("${weimg}", "");
		template = template.replace("${seximg}", "");
		template = template.replace("${havasms}", "");
		template = template.replace("${smssts}", "");
		template = template.replace("${smscol}", "#FFF");

		template = template.replace("${trcolor}", "background-color:#FFF;");
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

	public static String getSrcShow(ClientInfo vo) {
		if (StringUtil.isNotEmpty(vo.getSourceImg())) {
			// 使用渠道图片
			String template = "<img class='icon_src' title='${srcname}' src='${srcimg}' />";
			template = template.replace("${srcname}", vo.getSource());
			template = template.replace("${srcimg}", vo.getSourceImg());
			return template;
		} else {
			// 生成缩略图
			String template = "<div class='icon_word'>${srcname}</div>";
			template = template.replace("${srcname}", vo.getSource().substring(0, 1));
			return template;
		}
	}
}