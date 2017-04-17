package com.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crm.api.constant.NewsConts;
import com.crm.common.util.PushUtil;
import com.crm.common.util.TimeUtils;
import com.crm.common.util.ding.OApiException;
import com.crm.exception.EduException;
import com.crm.model.News;
import com.crm.model.Staff;
import com.crm.model.TimeWarn;
import com.crm.model.dao.NewsDao;
import com.crm.model.dao.StaffDao;
import com.crm.model.dao.TimeWarnDao;
import com.crm.service.WarnTimerService;

/**
 * ServiceImpl：定时任务
 * 
 * @author JingChenglong 2016-11-03 17:28
 *
 */
@Service
public class WarnTimerServiceImpl implements WarnTimerService {

	@Autowired
	TimeWarnDao timeWarnDao;

	@Autowired
	private StaffDao staffDao;// staff 员工持久化

	@Autowired
	private NewsDao newsDao;// news 消息持久化

	private Staff staff = new Staff();

	/*-- 创建定时任务 --*/
	public void addWarnTime(TimeWarn timeWarn) throws EduException {

		timeWarn.setCreateTime(TimeUtils.getSysTime());
		timeWarn.setIsDel(false);
		timeWarnDao.createTimeWarn(timeWarn);
	}

	/*-- 删除指定定时任务 --*/
	public void removeWarnTime(TimeWarn timeWarn) throws EduException {

		if (timeWarn == null || timeWarn.getCompanyId() == null || timeWarn.getCompanyId() == 0
				|| timeWarn.getId() == null || timeWarn.getId() == 0) {
			throw new EduException("请选择定时任务");
		}

		timeWarnDao.removeWarnTime(timeWarn);
	}

	/*-- 获取个人定时任务列表 --*/
	public List<TimeWarn> getWarnTaskList(Staff staff) throws EduException {

		if (staff == null || staff.getCompanyId() == null || staff.getCompanyId() == 0 || staff.getId() == null
				|| staff.getId() == 0) {
			return null;
		}

		TimeWarn timeWarn = new TimeWarn();
		timeWarn.setCompanyId(staff.getCompanyId());
		timeWarn.setTargetId(staff.getId());
		timeWarn.setWarnTimeStart(TimeUtils.getIntTimeNow());

		return timeWarnDao.getWarnTaskList(timeWarn);
	}

	/*-- 执行定时任务 --*/
	public void doWarnTimeTask() {

		// 获取提醒时间在当前时间及未来600秒内的任务
		TimeWarn timeWarn = new TimeWarn();
		timeWarn.setWarnTimeStart(TimeUtils.getIntTimeNow());
		timeWarn.setWarnTimeEnd(TimeUtils.getIntTimeFuture(600));

		List<TimeWarn> timeWarns = timeWarnDao.getTimeWarnBetween(timeWarn);
		for (TimeWarn task : timeWarns) {

			if (NewsConts.TYPE_NEW_WARN.equals(task.getType())) {
				// 定时提醒任务
				staff.setId(task.getTargetId());
				staff.setCompanyId(task.getCompanyId());
				staff = staffDao.getStaffInfoById(staff.getId());

				PushUtil.pushTimeWarn(staff, task);

				// 添加定时未读消息
				News news = new News();
				news.setType(NewsConts.TYPE_NEW_WARN);
				news.setCompanyId(staff.getCompanyId());
				news.setStaffId(staff.getId());
				news.setCreateTime(TimeUtils.getSysTime());
				news.setTitle(task.getMsg());
				news.setIsRead(false);
				news.setSpare1(task.getSpare1());
				news.setCreateIp("127.0.0.1");
				newsDao.addOneNew(news);
			}
		}
	}
}