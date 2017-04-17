package com.crm.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crm.common.util.TimeUtils;
import com.crm.model.Staff;
import com.crm.service.StaffService;
import com.crm.service.impl.ClientInfoServiceImpl;
import com.crm.service.impl.WarnTimerServiceImpl;

@Component
public class TaskAnnotation {

	@Autowired
	private StaffService staffService;
	@Autowired
	private WarnTimerServiceImpl warnTimerServiceImpl;
	@Autowired
	private ClientInfoServiceImpl clientInfoServiceImpl;

	/*
	 * 定时计算。每天凌晨 00:00 执行一次
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void show() {
		// 充值职工今日接单数，接单顺序，接单参考数
		Staff staff = new Staff();
		Integer num = staffService.resetTodayNumber(staff);
		System.out.println("执行了重置方法，有效数量：" + num + " --当前时间：" + TimeUtils.getSysTime());
		// 无效待审批状态的客资，自动判定为无效
		// Integer num1 = clientInfoServiceImpl.decideOverTimeClient();
		// System.out.println("执行了自动审批方法，有效数量：" + num1 + " --当前时间：" +
		// TimeUtils.getSysTime());
	}

	@Scheduled(cron = "0 0/10 9-23 * * ?") // 朝九晚23工作时间内每10分钟
	public void clock() {

		warnTimerServiceImpl.doWarnTimeTask();
		System.out.println("朝九晚六工作时间内每10分钟：" + " --当前时间：" + TimeUtils.getSysTime());
	}

	@Scheduled(cron = "0 0/20 9-10 * * ?") // 9-10工作时间内每20分钟
	public void nightClientPush() {

	}

	// /**
	// * 心跳更新。启动时执行一次，之后每隔3600秒执行一次
	// */
	// @Scheduled(fixedRate = 1000*3600)
	// public void print(){
	// try {
	// clientInfoService.doPullStaffLongTimeNoAccept();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}