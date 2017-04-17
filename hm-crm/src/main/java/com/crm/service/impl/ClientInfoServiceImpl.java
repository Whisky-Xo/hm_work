package com.crm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.crm.api.constant.ClientInfoConstant;
import com.crm.api.constant.NewsConts;
import com.crm.common.util.DBSplit;
import com.crm.common.util.IntegerUtils;
import com.crm.common.util.PushUtil;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TableEnum;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.ApproveLog;
import com.crm.model.ClientInfo;
import com.crm.model.News;
import com.crm.model.Source;
import com.crm.model.Staff;
import com.crm.model.dao.ApproveLogDao;
import com.crm.model.dao.ClientInfoDao;
import com.crm.model.dao.NewsDao;
import com.crm.model.dao.SourceDao;
import com.crm.model.dao.StaffDao;
import com.crm.service.ClientInfoService;

/**
 * ServiceImpl：客资
 * 
 * @author JingChenglong 2016-09-28 11:32
 *
 */
@Service
public class ClientInfoServiceImpl implements ClientInfoService {

	@Autowired
	private ClientInfoDao clientInfoDao;// cliengInfo 客资信息持久化
	@Autowired
	private StaffDao staffDao;// staff 职工信息持久化
	@Autowired
	private NewsDao newsDao;// news 消息持久化
	@Autowired
	ApproveLogDao approveLogDao;/*-- 审批日志持久层 --*/
	@Autowired
	private SourceDao sourceDao;// 渠道
	// @Autowired
	// private CrmBaseApi crmBaseApi;/* 后端接口调用 */

	/*-- 客资推送 --*/
	public void doClientPush(ClientInfo info) {

		if (info == null || info.getSourceId() == null || info.getSourceId() == 0) {
			return;
		}

		// 获取客资渠道信息
		Source src = sourceDao.getSourceById(info.getSourceId(), info.getCompanyId());
		if (src == null) {
			return;
		}

		// 本人校验--如果提报人既是该客资渠道的邀约员，则直接推送给个人
		Staff staff = checkColIsApt(info.getCollectorId(), info.getSourceId(), info.getCompanyId());
		if (staff != null) {
			if (staff.getIsShow()) {
				// 推送消息
				new PushThread(staff, info).start();
			}
			// 记录消息
			doAddClientNews(staff, info);
			return;
		}

		switch (src.getPushSort()) {
		case 1:
			// 一对一推送
			Staff yyStf = clientInfoDao.getStaffListOfOneToOne(src.getSrcId(), info.getCompanyId());
			if (yyStf == null) {
				return;
			}
			// 推送消息
			new PushThread(yyStf, info).start();
			// 记录消息
			doAddClientNews(yyStf, info);
			// 今日推送客资数+1
			staffDao.doTodayKzNumAdd(yyStf);

			// 启动接单校验（指定时间内职员如果未接单，将客资重新分配，并给主管推送罚单消息）
			synchronized (this) {
				new TimerTaskClientCheckThread(yyStf, info).start();
			}
			break;
		default:
			// 一对多推送
			List<Staff> staffList = clientInfoDao.getStaffListOfOneToMore(src.getSrcId(), info.getCompanyId());
			if (staffList == null || staffList.size() == 0) {
				return;
			}
			for (Staff yy : staffList) {
				// 推送消息
				new PushThread(yy, info).start();
				// 记录消息
				doAddClientNews(yy, info);
				// 今日推送客资数+1
				staffDao.doTodayKzNumAdd(yy);
			}
			break;
		}
	}

	// /*-- 转介绍推送：将指定渠道的新客资推送给相关转介绍邀约员 --*/
	// public void doPullStaffInnerSrcByNewClient(ClientInfo clientInfo) {
	//
	// // 获取转介绍邀约员信息
	// clientInfo.setSpare1(Source.SRC_RELA_TYPE_YY);
	// List<Staff> staffs = clientInfoDao.getStaffByClientInfoZjs(clientInfo);
	//
	// if (staffs == null || staffs.size() == 0) {
	// return;
	// }
	//
	// // 判定是否是邀约员自己提报
	// for (Staff staff : staffs) {
	// if (clientInfo.getCollectorId() != null
	// &&
	// clientInfo.getCollectorId().toString().equals(staff.getId().toString()))
	// {
	// // 推送
	// if (staff != null && clientInfo != null && staff.getIsShow()) {
	// new PushThread(staff, clientInfo).start();
	// }
	//
	// // 职工当日客资推送个数++
	// staffDao.doTodayKzNumAdd(staff);
	//
	// // 添加新客资未读消息
	// News news = new News();
	// news.setType(NewsConts.TYPE_NEW_KZ_ADD);
	// news.setCompanyId(staff.getCompanyId());
	// news.setStaffId(staff.getId());
	// news.setCreateTime(TimeUtils.getSysTime());
	// news.setTitle(NewsConts.TITLE_NEW_KZ_ADD);
	// news.setIsRead(false);
	// news.setSpare1(clientInfo.getKzId());
	// news.setSpare2(clientInfo.getKzName());
	// news.setSpare3(clientInfo.getKzPhone());
	// news.setCreateIp("127.0.0.1");
	// newsDao.addOneNew(news);
	//
	// return;
	// }
	// }
	//
	// for (Staff staff : staffs) {
	// // 推送
	// if (staff != null && clientInfo != null && staff.getIsShow()) {
	// new PushThread(staff, clientInfo).start();
	// }
	//
	// // 职工当日客资推送个数++
	// staffDao.doTodayKzNumAdd(staff);
	//
	// // 添加新客资未读消息
	// News news = new News();
	// news.setType(NewsConts.TYPE_NEW_KZ_ADD);
	// news.setCompanyId(staff.getCompanyId());
	// news.setStaffId(staff.getId());
	// news.setCreateTime(TimeUtils.getSysTime());
	// news.setTitle(NewsConts.TITLE_NEW_KZ_ADD);
	// news.setIsRead(false);
	// news.setSpare1(clientInfo.getKzId());
	// news.setSpare2(clientInfo.getKzName());
	// news.setSpare3(clientInfo.getKzPhone());
	// news.setCreateIp("127.0.0.1");
	// newsDao.addOneNew(news);
	// }
	// }
	//
	// /*-- 电商推送：电商渠道客资录入后推送给相关邀约员 --*/
	// public void doPullStaffEbusSrcByNewClient(ClientInfo clientInfo) {
	//
	// // 判定是否是网络转介绍渠道，如果是网络转介绍渠道，且采集员为邀约员，客资推送给本人，不计入总轮询
	// if (clientInfo.getSource() != null
	// &&
	// Source.SRC_NET_ZHUANJIESHAO.toString().equals(clientInfo.getSource().toString()))
	// {
	// Staff stf = new Staff();
	// stf.setCompanyId(clientInfo.getCompanyId());
	// stf.setId(clientInfo.getCollectorId());
	// stf.setSrcRelaType(Source.SRC_RELA_TYPE_YY);
	// stf.setSrcId(clientInfo.getSourceId());
	// stf = clientInfoDao.getYyStaffById(stf);
	// if (stf != null) {
	// new PushThread(stf, clientInfo).start();
	// // 添加新客资未读消息
	// News news = new News();
	// news.setType(NewsConts.TYPE_NEW_KZ_ADD);
	// news.setCompanyId(stf.getCompanyId());
	// news.setStaffId(stf.getId());
	// news.setCreateTime(TimeUtils.getSysTime());
	// news.setTitle(NewsConts.TITLE_NEW_KZ_ADD);
	// news.setIsRead(false);
	// news.setSpare1(clientInfo.getKzId());
	// news.setSpare2(clientInfo.getKzName());
	// news.setSpare3(clientInfo.getKzPhone());
	// news.setCreateIp("127.0.0.1");
	// newsDao.addOneNew(news);
	// return;
	// }
	// }
	//
	// // 获取邀约员（客资对应渠道的邀约员，在线 / 账号未锁定 / 客资今日被推送量最少 / 根据顺序 / 接单限额未满）
	// clientInfo.setSpare1(Source.SRC_RELA_TYPE_YY);
	// Staff staff = clientInfoDao.getStaffByClientInfo(clientInfo);
	//
	// // 推送
	// if (staff != null && clientInfo != null) {
	// new PushThread(staff, clientInfo).start();
	// } else {
	// return;
	// }
	//
	// // 职工当日客资推送个数++
	// staffDao.doTodayKzNumAdd(staff);
	//
	// // 添加新客资未读消息
	// News news = new News();
	// news.setType(NewsConts.TYPE_NEW_KZ_ADD);
	// news.setCompanyId(staff.getCompanyId());
	// news.setStaffId(staff.getId());
	// news.setCreateTime(TimeUtils.getSysTime());
	// news.setTitle(NewsConts.TITLE_NEW_KZ_ADD);
	// news.setIsRead(false);
	// news.setSpare1(clientInfo.getKzId());
	// news.setSpare2(clientInfo.getKzName());
	// news.setSpare3(clientInfo.getKzPhone());
	// news.setCreateIp("127.0.0.1");
	// newsDao.addOneNew(news);
	//
	// // 启动接单校验（指定时间内职员如果未接单，将客资重新分配，并给主管推送罚单消息）
	// synchronized (this) {
	// new TimerTaskClientCheckThread(staff, clientInfo).start();
	// }
	// }

	// /*-- 获取未被接单的客资并重新推送 --*/
	// public void doPullStaffLongTimeNoAccept() throws Exception {
	//
	// // 获取状态为等待邀约但未绑定邀约员的客资信息
	// ClientInfo infoParam = new ClientInfo();
	// infoParam.setStatusId(Integer.valueOf(ClientInfoConstant.BE_WAIT_MAKE_ORDER));
	//
	// List<ClientInfo> infoList =
	// clientInfoDao.getClientByNoBlindAccept(infoParam);
	// for (int i = 0; i < infoList.size(); i++) {
	// // 获取邀约员信息
	// infoList.get(i).setSpare1(Source.SRC_RELA_TYPE_YY);
	// Staff staff = clientInfoDao.getStaffByClientInfo(infoList.get(i));
	// // 推送
	// new PushThread(staff, infoList.get(i)).start();
	// }
	// }

	/*-- 消息推送线程：将指定客资信息推送给指定职员（PC，钉钉） --*/
	private class PushThread extends Thread {

		private Staff staff = null;
		private ClientInfo clientInfo = null;

		private PushThread(Staff staff, ClientInfo clientInfo) {
			this.staff = staff;
			this.clientInfo = clientInfo;
		}

		public void run() {
			PushUtil.pushNewAdd(staff, clientInfo);
		}
	}

	/*-- 定时任务线程：查询指定客资是否被指定邀约员接收， 如果未接收，将客资重新分配，并给邀约员主管发送罚单消息--*/
	private class TimerTaskClientCheckThread extends Thread {
		private Staff staff = null;
		private ClientInfo clientInfo = null;

		private TimerTaskClientCheckThread(Staff staff, ClientInfo clientInfo) {
			this.staff = staff;
			this.clientInfo = clientInfo;
		}

		public void run() {
			try {
				Thread.sleep(126 * 1000);// 126秒接单时间

				// 校验是否被接单
				clientInfo.setTabName(DBSplit.getTable(TableEnum.info, clientInfo.getCompanyId()));
				clientInfo = clientInfoDao.getInfoById(clientInfo);

				if (clientInfo != null && (clientInfo.getAppointId() == null || 0 == clientInfo.getAppointId())) {
					// 未接单，客资重新调配
					doClientPush(clientInfo);

					// 组长推送罚单消息
					List<Staff> boss = staffDao.getBossStaff(staff);

					for (Staff bs : boss) {
						// 添加罚单未读消息
						News news = new News();
						news.setType(NewsConts.TYPE_NEW_TICKET);
						news.setCompanyId(bs.getCompanyId());
						news.setStaffId(bs.getId());
						news.setCreateTime(TimeUtils.getSysTime());
						news.setTitle(NewsConts.TITLE_NEW_STAFF_TICKET);
						news.setIsRead(false);
						news.setSpare1(staff.getId().toString());
						news.setSpare2(staff.getName());
						news.setSpare3(clientInfo.getKzId());
						news.setCreateIp("127.0.0.1");
						newsDao.addOneNew(news);

						PushUtil.pushTicket(bs, staff);
					}

					// 个人推送罚单消息
					News news = new News();
					news.setType(NewsConts.TYPE_NEW_COMMON);
					news.setCompanyId(staff.getCompanyId());
					news.setStaffId(staff.getId());
					news.setCreateTime(TimeUtils.getSysTime());
					news.setTitle(NewsConts.TITLE_NEW_TICKET);
					news.setIsRead(false);
					// news.setSpare1(staff.getId().toString());
					// news.setSpare2(staff.getName());
					news.setSpare3(clientInfo.getKzId());
					news.setCreateIp("127.0.0.1");
					newsDao.addOneNew(news);

					PushUtil.pushCommonMsg(staff, NewsConts.TITLE_NEW_TICKET);

				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				Thread.interrupted();
			}
		}
	}

	/*-- 根据客资ID获取客资详细信息 --*/
	public ClientInfo getClientInfo(ClientInfo clientInfo) throws Exception {

		if (clientInfo == null || clientInfo.getCompanyId() == null || clientInfo.getCompanyId() == 0
				|| "".equals(clientInfo.getKzId())) {
			return null;
		}

		clientInfo.setTabName(DBSplit.getTable(TableEnum.info, clientInfo.getCompanyId()));
		return clientInfoDao.getInfoById(clientInfo);
	}

	/*-- 客资模糊搜索：姓名/电话/QQ --*/
	public List<ClientInfo> getClientInfoLike(ClientInfo clientInfo) throws Exception {

		if (clientInfo.getCompanyId() == null || clientInfo.getCompanyId() == 0) {
			return null;
		}

		if (StringUtil.isEmpty(clientInfo.getKzQq()) && StringUtil.isEmpty(clientInfo.getKzPhone())
				&& StringUtil.isEmpty(clientInfo.getKzWechat())) {
			return null;
		}

		clientInfo.setTabName(DBSplit.getTable(TableEnum.info, clientInfo.getCompanyId()));
		return clientInfoDao.getClientInfoLike(clientInfo);
	}

	/*-- 获取门店当日已短信预约个数 --*/
	public Integer getSmsCmtByShopId(Integer companyId, String start, String end) {

		if (StringUtil.isEmpty(start) || StringUtil.isEmpty(end) || companyId == null || companyId == 0) {
			return null;
		}
		return clientInfoDao.getSmsCmtByShopId(companyId, start, end, DBSplit.getTable(TableEnum.info, companyId));
	}

	/*-- 微信标记已加/未加 --*/
	public void changeWeChatFlag(String kzId, boolean flag, int companyId) throws EduException {

		if (StringUtil.isEmpty(kzId)) {
			throw new EduException();
		}

		clientInfoDao.changeWeChatFlag(kzId, flag, DBSplit.getTable(TableEnum.info, companyId));
	}

	/*-- QQ标记已加/未加 --*/
	public void changeQqFlag(String kzId, boolean flag, int companyId) throws EduException {

		if (StringUtil.isEmpty(kzId)) {
			throw new EduException();
		}

		clientInfoDao.changeQqFlag(kzId, flag, DBSplit.getTable(TableEnum.info, companyId));
	}

	// /*-- 无效待审批客资超时自动判定为无效 --*/
	// public int decideOverTimeClient() {
	//
	// Map<String, Object> reqContent = new HashMap<String, Object>();
	// JSONObject jsInfo = new JSONObject();
	// ApproveLog log = null;
	//
	// ClientInfo vo = new ClientInfo();
	// vo.setStatusId(Integer.valueOf(ClientInfoConstant.INVALID_BE_STAY));
	// vo.setUpdateTime(TimeUtils.getBackTime("1"));
	//
	// // 获取状态为无效待审批，最后跟进时间超过24小时的客资集合
	// List<ClientInfo> infoList = clientInfoDao.getClientInfoList(vo);
	//
	// // 循环客资集合，添加审批日志，无效原因为自动判定无效，修改客资状态为已判定无效
	// for (ClientInfo info : infoList) {
	// if (info.getCompanyId() == 3) {
	// reqContent.clear();
	// reqContent.put("kzids", info.getKzId());
	// reqContent.put("operaid", "1");
	// reqContent.put("companyid", info.getCompanyId());
	// reqContent.put("statusid", ClientInfoConstant.BE_INVALID);
	// reqContent.put("memo", "超时自动判定为无效");
	// //
	// // /*-- 接口调用，业务执行 --*/
	// // try {
	// // String rstStr = crmBaseApi.doService(reqContent,
	// // "doClientEditStatus");
	// // jsInfo = JsonFmtUtil.strInfoToJsonObj(rstStr);
	// // if ("100000".equals(jsInfo.getString("code"))) {
	// // log = new ApproveLog();
	// // log.setKzId(info.getKzId());
	// // log.setCreateIp("127.0.0.1");
	// // log.setCompanyId(info.getCompanyId());
	// // log.setOperaId(1);
	// // log.setApproveType(ApproveLog.INVALID_KZ);
	// // log.setCode(26);
	// //
	// // // 删除该客资之前所有无效审批
	// // approveLogDao.removeApproveLog(log);
	// //
	// // // 新增
	// // log.setCreateTime(TimeUtils.getSysTime());
	// // log.setIsDel(false);
	// // approveLogDao.createApproveLog(log);
	// // }
	// // } catch (EduException e) {
	// // e.printStackTrace();
	// // }
	// }
	// }
	// return 0;
	// }

	/*-- 提报人，邀约人，同人校验 --*/
	public Staff checkColIsApt(Integer staffId, Integer srcId, Integer companyId) {

		if (IntegerUtils.isNullOrZero(staffId) || IntegerUtils.isNullOrZero(companyId)
				|| IntegerUtils.isNullOrZero(srcId)) {
			return null;
		}

		Staff staff = staffDao.getStaffByIdAndYySrcId(srcId, staffId, companyId);

		if (staff != null) {
			return staff;
		}

		return null;
	}

	/*-- 添加新客资消息 --*/
	public void doAddClientNews(Staff staff, ClientInfo info) {
		if (staff == null || info == null) {
			return;
		}
		// 添加新客资未读消息
		News news = new News();
		news.setType(NewsConts.TYPE_NEW_KZ_ADD);
		news.setCompanyId(staff.getCompanyId());
		news.setStaffId(staff.getId());
		news.setCreateTime(TimeUtils.getSysTime());
		news.setTitle(NewsConts.TITLE_NEW_KZ_ADD);
		news.setIsRead(false);
		news.setSpare1(StringUtil.nullToStrTrim(info.getKzId()));
		news.setSpare2(StringUtil.nullToStrTrim(info.getKzName()));
		news.setSpare3(StringUtil.nullToStrTrim(info.getKzPhone()));
		news.setCreateIp("127.0.0.1");
		newsDao.addOneNew(news);
	}
}