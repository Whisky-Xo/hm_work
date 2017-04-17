package com.crm.common.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.exception.EduException;
import com.crm.model.ClientInfo;
import com.crm.model.ClientLogInfo;
import com.crm.model.ClientYaoYueLog;
import com.crm.model.InnnerAnalyze;
import com.crm.model.ShopMeetLog;
import com.crm.model.ShopSellAnalisys;

/**
 * 返回结果解析工具类
 * 
 * @author JingChenglong 2016-09-10 16:32
 *
 */
public class JsonFmtUtil {

	/*-- 字符串转JSON --*/
	public static JSONObject strToJsonObj(String str) throws EduException {

		JSONObject res = (JSONObject) JSONObject.parse(str);

		if ("100000".equals(res.getJSONObject("response").getJSONObject("info").get("code"))) {
			return res.getJSONObject("response").getJSONObject("content");
		} else {
			throw new EduException(res.getJSONObject("response").getJSONObject("info").getString("msg"));
		}
	}

	/*-- JSONArray转客资对象集合 --*/
	public static List<ClientInfo> jsonArrToClientInfo(JSONArray jsArr) {

		List<ClientInfo> infoList = new ArrayList<ClientInfo>();
		for (int i = 0; i < jsArr.size(); i++) {
			ClientInfo client = jsonToClientInfo(jsArr.getJSONObject(i).getJSONObject("info"));
			infoList.add(client);

		}
		return infoList;
	}

	/*-- JSON对象转客资对象 --*/
	public static ClientInfo jsonToClientInfo(JSONObject json) {

		if (json == null) {
			return null;
		}

		ClientInfo client = new ClientInfo();
		client.setKzId(json.getString("kzId"));
		client.setKzName(json.getString("kzName"));
		client.setKzPhone(json.getString("kzPhone"));
		client.setKzQq(json.getString("kzQq"));
		client.setKzWechat(json.getString("weChat"));
		client.setAddress(json.getString("address"));
		client.setTypeName(json.getString("typeName"));
		client.setClassName(json.getString("className"));
		client.setStatusId(StringUtil.nullToIntZero(json.getString("statusId")));
		client.setStatus(json.getString("statusName"));
		client.setSourceId(StringUtil.nullToIntZero(json.getString("sourceId")));
		client.setSourceImg(json.getString("srcImg"));
		client.setSource(StringUtil.nullToStrTrim(json.getString("srcName"))
				+ StringUtil.nullToStrTrim(json.getString("sourceSpare")));
		client.setAppointTime(json.getString("appointTime"));
		client.setActualTime(json.getString("actualTime"));
		client.setAppointId(StringUtil.nullToIntZero(json.getString("appointId")));
		client.setYpTime(TimeUtils.formatTime(json.getString("ypTime"), "yyyy-MM-dd"));
		client.setMarryTime(TimeUtils.formatTime(json.getString("marryTime"), "yyyy-MM-dd"));
		client.setCollectorId(StringUtil.nullToIntZero(json.getString("collectorId")));
		client.setCollector(StringUtil.nullToStrTrim(json.getString("collectorName"))
				+ StringUtil.nullToStrTrim(json.getString("collectorNameSpare")));
		client.setCollectorPhone(json.getString("collectorPhone"));
		client.setOldKzName(json.getString("oldKzName"));
		client.setOldKzPhone(json.getString("oldKzPhone"));
		client.setAppoint(json.getString("appointName"));
		client.setMemo(json.getString("memo"));
		client.setRemark(json.getString("remark"));
		client.setMateName(json.getString("mateName"));
		client.setMatePhone(json.getString("matePhone"));
		client.setMerchantPid(json.getString("merchantPid"));
		client.setCreateTime(TimeUtils.formatClientTime(json.getString("createTime")));
		client.setCreateIp(json.getString("createIp"));
		client.setUpdateTime(json.getString("updateTime"));
		client.setCompanyId(StringUtil.nullToIntZero(json.getString("companyId")));
		client.setShopId(StringUtil.nullToIntZero(json.getString("shopId")));
		client.setShopName(json.getString("shopName"));
		client.setPromoterId(json.getInteger("promoterId"));
		client.setPromoter(json.getString("promoter"));
		client.setReceptor(json.getString("receptor"));
		client.setReceptorId(json.getString("receptorId"));
		if (StringUtil.isNotEmpty(json.getString("amount"))) {
			try {
				client.setAmount(
						"¥" + new java.text.DecimalFormat("0.00").format(Double.valueOf(json.getString("amount"))));
			} catch (Exception e) {
			}
		}
		client.setHavaSms(StringUtil.nullToBoolean(json.getString("haveSms")));
		client.setValidTime(json.getString("validTime"));
		client.setComeShopTime(json.getString("comeShopTime"));
		client.setSuccessTime(json.getString("successTime"));
		client.setTraceTime(json.getString("traceTime"));
		client.setSmsCode(json.getString("smsCode"));
		client.setInvalidCode(json.getString("invalidCode"));
		client.setInvalidLabel(json.getString("invalidReason"));
		client.setRunOffCode(json.getString("runOffCode"));
		client.setRunOffLabel(json.getString("runOffReason"));
		client.setTrColor(json.getString("colorCode"));
		client.setStsColor(json.getString("stsColor"));
		client.setStatusColor(StringUtil.nullToStrTrim(json.getString("statusColor")));
		client.setKzNum(json.getString("kzNum"));
		client.setSex(json.getString("sex"));
		client.setQqFlag(StringUtil.nullToBoolean(json.getString("qqFlag")));
		client.setWeChatFlag(StringUtil.nullToBoolean(json.getString("weFlag")));
		client.setTypeId(Integer.valueOf(StringUtil.nullToZeroStr(json.getString("typeId"))));
		client.setClassId(Integer.valueOf(StringUtil.nullToZeroStr(json.getString("classId"))));
		client.setReceiveTime(json.getString("receiveTime"));
		client.setZxStyle(json.getString("zxStyle"));
		client.setYxLavel(json.getString("yxLavel"));
		client.setYsRange(json.getString("ysRange"));
		client.setAdId(json.getString("adId"));
		client.setAdAddress(json.getString("adAddress"));
		client.setOldKzId(json.getString("oldKzId"));
		client.setMarryMemo(json.getString("marryMemo"));
		client.setYpMemo(json.getString("ypMemo"));

		client.setAttachFile(json.getString("attachFile"));
		client.setMateWeChat(json.getString("mateWechat"));
		client.setMateQq(json.getString("mateQq"));
		client.setBirthTime(TimeUtils.formatClientTime(json.getString("birthTime")));
		client.setAgeNum(json.getString("ageNum"));
		client.setIdNum(json.getString("idNum"));
		client.setJob(json.getString("job"));
		client.setEdu(json.getString("edu"));
		client.setEarn(json.getString("earn"));
		client.setCarHouse(json.getString("carHouse"));
		return client;
	}

	/*-- JSONArray转转介绍分析集合 --*/
	public static List<InnnerAnalyze> jsonArrToAnalyze(JSONArray jsArr) {

		List<InnnerAnalyze> analyzeList = new ArrayList<InnnerAnalyze>();
		for (int i = 0; i < jsArr.size(); i++) {
			InnnerAnalyze analyze = JSONObject.toJavaObject(jsArr.getJSONObject(i), InnnerAnalyze.class);
			analyzeList.add(analyze);
		}
		return analyzeList;
	}

	/*-- JSONObject转销售分析对象 --*/
	public static ShopSellAnalisys jsonArrToShopSellAnalisys(JSONObject jsObj) {

		ShopSellAnalisys analyze = JSONObject.toJavaObject(jsObj, ShopSellAnalisys.class);

		return analyze;
	}

	/*-- 获取返回结果JSON --*/
	public static JSONObject strInfoToJsonObj(String str) throws EduException {

		JSONObject res = (JSONObject) JSONObject.parse(str);

		return res.getJSONObject("response").getJSONObject("info");
	}

	/*-- 获取返回内容JSON --*/
	public static JSONObject strContentToJsonObj(String str) throws EduException {

		JSONObject res = (JSONObject) JSONObject.parse(str);

		return res.getJSONObject("response").getJSONObject("content");
	}

	/*-- JSONArray转客资操作日志对象集合 --*/
	public static List<ClientLogInfo> jsonArrToClientLogInfo(JSONArray jsArr) {

		List<ClientLogInfo> logList = new ArrayList<ClientLogInfo>();

		for (int i = 0; i < jsArr.size(); i++) {
			ClientLogInfo log = new ClientLogInfo();
			log.setLogId(jsArr.getJSONObject(i).getString("logId"));
			log.setKzId(jsArr.getJSONObject(i).getString("kzId"));
			log.setOperaId(StringUtil.nullToIntZero(jsArr.getJSONObject(i).getString("operaId")));
			log.setOperaName(jsArr.getJSONObject(i).getString("operaName"));
			log.setMemo(jsArr.getJSONObject(i).getString("memo"));
			log.setOperaTime(TimeUtils.formatClientTime(jsArr.getJSONObject(i).getString("operaTime")));
			log.setOperaIp(jsArr.getJSONObject(i).getString("operaIp"));
			log.setCompanyId(StringUtil.nullToIntZero(jsArr.getJSONObject(i).getString("companyId")));

			logList.add(log);
		}

		return logList;
	}

	/*-- JSONArray转客资邀约记录对象集合 --*/
	public static List<ClientYaoYueLog> jsonArrToClientYaoYueLogInfo(JSONArray jsArr) {

		List<ClientYaoYueLog> logList = new ArrayList<ClientYaoYueLog>();

		for (int i = 0; i < jsArr.size(); i++) {
			ClientYaoYueLog log = new ClientYaoYueLog();
			log.setYyLogId(jsArr.getJSONObject(i).getString("yyLogId"));
			log.setKzId(jsArr.getJSONObject(i).getString("kzId"));
			log.setRstCode(jsArr.getJSONObject(i).getString("rstCode"));
			log.setRst(jsArr.getJSONObject(i).getString("rst"));
			log.setYyMemo(jsArr.getJSONObject(i).getString("yyMemo"));
			log.setComeTime(jsArr.getJSONObject(i).getString("comeTime"));
			log.setShopId(StringUtil.nullToIntZero(jsArr.getJSONObject(i).getString("shopId")));
			log.setMainShopName(jsArr.getJSONObject(i).getString("shopName"));
			log.setBranchShopName(jsArr.getJSONObject(i).getString("merchantShopName"));
			log.setReceptorId(jsArr.getJSONObject(i).getString("receptorId"));
			log.setReceptorName(jsArr.getJSONObject(i).getString("receptorName"));
			log.setWarnTime(jsArr.getJSONObject(i).getString("warnTime"));
			log.setStaffId(jsArr.getJSONObject(i).getString("staffId"));
			log.setStaffName(jsArr.getJSONObject(i).getString("staffName"));
			log.setFile1(jsArr.getJSONObject(i).getString("file1"));
			log.setFile2(jsArr.getJSONObject(i).getString("file2"));
			log.setFile3(jsArr.getJSONObject(i).getString("file3"));
			log.setFile4(jsArr.getJSONObject(i).getString("file4"));
			log.setFile5(jsArr.getJSONObject(i).getString("file5"));
			log.setFile6(jsArr.getJSONObject(i).getString("file6"));
			log.setFile7(jsArr.getJSONObject(i).getString("file7"));
			log.setFile8(jsArr.getJSONObject(i).getString("file8"));
			log.setFile9(jsArr.getJSONObject(i).getString("file9"));
			log.setFile10(jsArr.getJSONObject(i).getString("file10"));
			log.setCreateIp(jsArr.getJSONObject(i).getString("createIp"));
			log.setCreateTime(TimeUtils.formatClientTime(jsArr.getJSONObject(i).getString("createTime")));
			log.setStatusColor(jsArr.getJSONObject(i).getString("statusColor"));
			logList.add(log);
		}

		return logList;
	}

	/*-- JSONArray转客资门店洽谈记录对象集合 --*/
	public static List<ShopMeetLog> jsonArrToClientShopMeetLogInfo(JSONArray jsArr) {

		List<ShopMeetLog> logList = new ArrayList<ShopMeetLog>();

		for (int i = 0; i < jsArr.size(); i++) {
			ShopMeetLog log = new ShopMeetLog();
			log.setQtLogId(jsArr.getJSONObject(i).getString("qtLogId"));
			log.setKzId(jsArr.getJSONObject(i).getString("kzId"));
			log.setArriveTime(jsArr.getJSONObject(i).getString("arriveTime"));
			log.setRstCode(jsArr.getJSONObject(i).getString("rstCode"));
			log.setRstLabel(jsArr.getJSONObject(i).getString("rstLabel"));
			log.setQtMemo(jsArr.getJSONObject(i).getString("qtMemo"));
			log.setAmount(jsArr.getJSONObject(i).getString("amount"));
			log.setRecepterId(jsArr.getJSONObject(i).getString("recepterId"));
			log.setRecepterName(jsArr.getJSONObject(i).getString("recepterName"));
			log.setOperaId(jsArr.getJSONObject(i).getString("operaId"));
			log.setOperaName(jsArr.getJSONObject(i).getString("operaName"));
			log.setCreateTime(jsArr.getJSONObject(i).getString("createTime"));
			log.setShopId(jsArr.getJSONObject(i).getString("shopId"));
			log.setShopName(jsArr.getJSONObject(i).getString("shopName"));
			log.setCompanyId(jsArr.getJSONObject(i).getString("companyId"));
			log.setRunOffCode(jsArr.getJSONObject(i).getString("runOffCode"));
			log.setRunOffLabel(jsArr.getJSONObject(i).getString("runOffLabel"));

			logList.add(log);
		}

		return logList;
	}
}