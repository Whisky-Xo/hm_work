/*changedatetime*/
function selectDateSubmit(type) {
	var starttime = "";
	var endtime = "";

	var now = new Date();
	var nowTime = now.getTime();
	var day = now.getDay();
	var oneDayLong = 24 * 60 * 60 * 1000;

	if (type == "1") {// 今日
		starttime = getYmdToday();
		endtime = starttime;
	} else if (type == "2") {// 明日
		starttime = GetDateStr(1);
		endtime = starttime;
	} else if (type == "3") {// 本周
		starttime = getFirstYmdThisWeek();
		endtime = getLastYmdThisWeek();
	} else if (type == "4") {// 下周
		var MondayTime = nowTime - (day - 8) * oneDayLong;
		var SundayTime = nowTime + (14 - day) * oneDayLong;
		var m = new Date(MondayTime);
		starttime = m.getFullYear() + '-' + (m.getMonth() + 1) + '-'
				+ m.getDate() /*
								 * + ' ' + d.getHours() + ':' + d.getMinutes() +
								 * ':' + d.getSeconds()
								 */;
		var s = new Date(SundayTime);
		endtime = s.getFullYear() + '-' + (s.getMonth() + 1) + '-'
				+ s.getDate();
	} else if (type == "5") {// 本月
		starttime = getYmdMonthFirst();
		endtime = getYmdMonthLast();
	} else if (type == "6") {// 下月
		var nextDate = new Date();
		nextDate.setMonth(nextDate.getMonth() + 1);
		starttime = nextDate.getFullYear() + '-' + (nextDate.getMonth() + 1)
				+ '-01';
		var nextmonth = nextDate.getMonth() + 1;
		var nextmonthdays = getMonthDays(nextDate.getFullYear(), nextmonth);
		endtime = nextDate.getFullYear() + '-' + (nextDate.getMonth() + 1)
				+ '-' + nextmonthdays;
	} else if (type == "7") {// 昨日
		starttime = getYesterDay();
		endtime = starttime;
	} else if (type == "8") {// 上周
		starttime = getLastWeekFistDay();
		endtime = getLastWeekLastDay();
	} else if (type == "9") {// 上月
		var lastDate = new Date();
		starttime = getLastYmdMonthFirst();
		endtime = getLastYmdMonthEnd();
	}
	$("#bigdata_start_time").val(starttime);
	$("#bigdata_end_time").val(endtime);
	return rang_time = "start=" + starttime + "&end=" + endtime;
}

/* 获取日期 */
function GetDateStr(AddDayCount) { // AddDayCount为1时获取的是明日，AddDayCount为-1时获取的是昨日，AddDayCount为2时获取的是后天的日期
	var dd = new Date();
	dd.setDate(dd.getDate() + AddDayCount);// 获取AddDayCount天后的日期
	var y = dd.getFullYear();
	var m = dd.getMonth() + 1;// 获取当前月份的日期
	var d = dd.getDate();
	return y + "-" + m + "-" + d;
}
// 获得某月的天数
function getMonthDays(nowYear, myMonth) {
	nowYear = 2016;
	myMonth = 10;
	var monthStartDate = new Date(nowYear, myMonth, 1);
	var monthEndDate = new Date(nowYear, myMonth + 1, 1);
	var days = (monthEndDate - monthStartDate) / (1000 * 60 * 60 * 24);
	return days;
}

/*-- 时间前后切换 --*/
function timeGo(type, url) {
	var startTime = $("#start_time").val();
	var endTime = $("#end_time").val();

	var deptid = $("#dept_id").val();
	var staffids = $("#staff_id").val();

	var start = new Date(startTime);
	var end = new Date(endTime);
	if (type) {
		start.setDate(start.getDate() + 1);
		end.setDate(end.getDate() + 1);
	} else {
		start.setDate(start.getDate() - 1);
		end.setDate(end.getDate() - 1);
	}
	startTime = start.getFullYear() + "-" + (start.getMonth() + 1) + "-"
			+ start.getDate();
	endTime = end.getFullYear() + "-" + (end.getMonth() + 1) + "-"
			+ end.getDate();

	var href = "start=" + startTime + "&end=" + endTime;
	if (url) {
		window.location.href = url + '?' + href;
	} else {
		window.location.href = 'innner_intro_analyze?' + href;
	}
}
