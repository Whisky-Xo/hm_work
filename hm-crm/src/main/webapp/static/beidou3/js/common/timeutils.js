/*-- 时间工具 --*/
//获取当前时间，格式"yyyy-MM-DD",例：2017-01-05
function getYmdToday() {
	var day = new Date();
	var Year = 0;
	var Month = 0;
	var Day = 0;
	var CurrentDate = "";
	Year = day.getFullYear();
	Month = day.getMonth() + 1;
	Day = day.getDate();
	CurrentDate += Year + "-";
	if (Month >= 10) {
		CurrentDate += Month + "-";
	} else {
		CurrentDate += "0" + Month + "-";
	}
	if (Day >= 10) {
		CurrentDate += Day;
	} else {
		CurrentDate += "0" + Day;
	}
	return CurrentDate;
}
// 获取昨日
function getYesterDay() {
	var dd = new Date();
	dd.setDate(dd.getDate() - 1);// 获取AddDayCount天后的日期
	var y = dd.getFullYear();
	var m = dd.getMonth() + 1;// 获取当前月份的日期
	var d = dd.getDate();
	if (m < 10) {
		m = "0" + m;
	}
	if (d < 10) {
		d = "0" + d;
	}
	return y + "-" + m + "-" + d;
}
// 获取当前时间，格式"yyyy-MM-DD hh:mm",例：2017-01-05 19:02
function getYmdhmToday() {
	var day = new Date();
	var Year = 0;
	var Month = 0;
	var Day = 0;
	var Hour = 0;
	var Minute = 0;
	var CurrentDate = "";
	Year = day.getFullYear();
	Month = day.getMonth() + 1;
	Day = day.getDate();
	Hour = day.getHours();
	Minute = day.getMinutes();
	CurrentDate += Year + "-";
	if (Month >= 10) {
		CurrentDate += Month + "-";
	} else {
		CurrentDate += "0" + Month + "-";
	}
	if (Day >= 10) {
		CurrentDate += Day;
	} else {
		CurrentDate += "0" + Day;
	}
	CurrentDate += " ";
	if (Hour >= 10) {
		CurrentDate += Hour;
	} else {
		CurrentDate += "0" + Hour;
	}
	CurrentDate += " ";
	if (Minute >= 10) {
		CurrentDate += Minute;
	} else {
		CurrentDate += "0" + Minute;
	}
	return CurrentDate;
}

// 获取当月第一天，格式"yyyy-MM-DD",例：2017-01-01
function getYmdMonthFirst() {
	var day = new Date();
	var Year = 0;
	var Month = 0;
	var firstDate = "";
	Year = day.getFullYear();
	Month = day.getMonth() + 1;
	Day = day.getDate();
	firstDate += Year + "-";
	if (Month >= 10) {
		firstDate += Month + "-";
	} else {
		firstDate += "0" + Month + "-";
	}
	firstDate += "01";
	return firstDate;
}

// 获取当月最后一天，格式"yyyy-MM-DD",例：2017-01-31
function getYmdMonthLast() {
	var date = new Date();
	var currentMonth = date.getMonth();
	var nextMonth = ++currentMonth;
	var nextMonthFirstDay = new Date(date.getFullYear(), nextMonth, 1);
	var oneDay = 1000 * 60 * 60 * 24;
	var day = new Date(nextMonthFirstDay - oneDay);
	var Year = 0;
	var Month = 0;
	var lastDate = "";
	Year = day.getFullYear();
	Month = day.getMonth() + 1;
	Day = day.getDate();
	lastDate += Year + "-";
	if (Month >= 10) {
		lastDate += Month + "-";
	} else {
		lastDate += "0" + Month + "-";
	}
	if (Day >= 10) {
		lastDate += Day;
	} else {
		lastDate += "0" + Day;
	}
	return lastDate;
}

// 获取上月第一天，格式"yyyy-MM-DD",例：2017-01-01
function getLastYmdMonthFirst() {

	var nowdays = new Date();
	var year = nowdays.getFullYear();
	var month = nowdays.getMonth();
	if (month == 0) {
		month = 12;
		year = year - 1;
	}
	if (month < 10) {
		month = "0" + month;
	}
	var firstDay = year + "-" + month + "-" + "01";// 上个月的第一天

	var myDate = new Date(year, month, 0);

	return firstDay;
}

// 获取上月最后一天，格式"yyyy-MM-DD",例：2017-01-01
function getLastYmdMonthEnd() {

	var nowdays = new Date();
	var year = nowdays.getFullYear();
	var month = nowdays.getMonth();
	if (month == 0) {
		month = 12;
		year = year - 1;
	}
	if (month < 10) {
		month = "0" + month;
	}

	var myDate = new Date(year, month, 0);
	var lastDay = year + "-" + month + "-" + myDate.getDate();// 上个月的最后一天

	return lastDay;
}

// 获取当前时间，格式"yyyy/MM/DD hh:mm:ss",例：2017-01-01 01:01:01
function getNow() {
	var date = new Date();
	var year = 0;
	var month = 0;
	var day = 0;
	var hour = 0;
	var minute = 0;
	var second = 0;
	var now = "";
	// 初始化时间
	year = date.getFullYear();// ie火狐下都可以
	month = date.getMonth() + 1;
	day = date.getDate();
	hour = date.getHours();
	minute = date.getMinutes();
	second = date.getSeconds();
	now += year + "/";
	if (month >= 10) {
		now += month + "/";
	} else {
		now += "0" + month + "/";
	}
	if (day >= 10) {
		now += day;
	} else {
		now += "0" + day;
	}
	now += " ";
	if (hour >= 10) {
		now += hour;
	} else {
		now += "0" + hour;
	}
	now += ":";
	if (minute >= 10) {
		now += minute;
	} else {
		now += "0" + minute;
	}
	now += ":";
	if (second >= 10) {
		now += second;
	} else {
		now += "0" + second;
	}

	return now;
}

/*-- 获取本周第一天 --*/
function getFirstYmdThisWeek() {
	var now = new Date();
	var nowTime = now.getTime();
	var day = now.getDay();
	var oneDayLong = 24 * 60 * 60 * 1000;

	var MondayTime = nowTime - (day - 1) * oneDayLong;
	var SundayTime = nowTime + (7 - day) * oneDayLong;
	var m = new Date(MondayTime);

	var month = m.getMonth() + 1;
	var date = m.getDate();
	if (month < 10) {
		month = "0" + month;
	}
	if (date < 10) {
		date = "0" + date;
	}

	starttime = m.getFullYear() + '-' + month + '-' + date;

	return starttime;
}

/*-- 获取本周最后一天 --*/
function getLastYmdThisWeek() {
	var now = new Date();
	var nowTime = now.getTime();
	var day = now.getDay();
	var oneDayLong = 24 * 60 * 60 * 1000;
	var MondayTime = nowTime - (day - 1) * oneDayLong;
	var SundayTime = nowTime + (7 - day) * oneDayLong;
	var s = new Date(SundayTime);

	var month = s.getMonth() + 1;
	var date = s.getDate();
	if (month < 10) {
		month = "0" + month;
	}
	if (date < 10) {
		date = "0" + date;
	}

	endtime = s.getFullYear() + '-' + month + '-' + date;
	return endtime;
}

/*-- 获取上周第一天 --*/
function getLastWeekFistDay() {
	var now = new Date();
	var nowTime = now.getTime();
	var day = now.getDay();
	var oneDayLong = 24 * 60 * 60 * 1000;
	var MondayTime = nowTime - (day + 6) * oneDayLong;
	var SundayTime = nowTime + (0 - day) * oneDayLong;
	var m = new Date(MondayTime);
	var month = m.getMonth() + 1;
	var date = m.getDate();
	if (month < 10) {
		month = "0" + month;
	}
	if (date < 10) {
		date = "0" + date;
	}

	starttime = m.getFullYear() + '-' + month + '-' + date;

	return starttime;
}

/*-- 获取上周最后一天 --*/
function getLastWeekLastDay() {
	var now = new Date();
	var nowTime = now.getTime();
	var day = now.getDay();
	var oneDayLong = 24 * 60 * 60 * 1000;
	var MondayTime = nowTime - (day + 6) * oneDayLong;
	var SundayTime = nowTime + (0 - day) * oneDayLong;
	var m = new Date(MondayTime);
	starttime = m.getFullYear() + '-' + (m.getMonth() + 1) + '-' + m.getDate();
	var s = new Date(SundayTime);
	var month = s.getMonth() + 1;
	var date = s.getDate();
	if (month < 10) {
		month = "0" + month;
	}
	if (date < 10) {
		date = "0" + date;
	}

	endtime = s.getFullYear() + '-' + month + '-' + date;
	return endtime;
}
/*-- 获取下周第一天 --*/
function getNextWeekFirstDay() {
	var now = new Date();
	var nowTime = now.getTime();
	var day = now.getDay();
	var oneDayLong = 24 * 60 * 60 * 1000;

	var MondayTime = nowTime - (day - 8) * oneDayLong;
	var m = new Date(MondayTime);
	var start_m = m.getMonth() + 1;
	if (start_m < 10) {
		start_m = "0" + start_m;
	}
	var start_d = m.getDate();
	if (start_d < 10) {
		start_d = "0" + start_d;
	}
	starttime = m.getFullYear() + '-' + start_m + '-' + start_d;
	return starttime;
}
/*-- 获取下周最后一天 --*/
function getNextWeekLastDay() {
	var now = new Date();
	var nowTime = now.getTime();
	var day = now.getDay();
	var oneDayLong = 24 * 60 * 60 * 1000;

	var SundayTime = nowTime + (14 - day) * oneDayLong;
	var s = new Date(SundayTime);
	var end_m = s.getMonth() + 1;
	if (end_m < 10) {
		end_m = "0" + end_m;
	}
	var end_d = s.getDate();
	if (end_d < 10) {
		end_d = "0" + end_d;
	}
	endtime = s.getFullYear() + '-' + end_m + '-' + end_d;
	return endtime;
}
/*-- 获取下月第一天 --*/
function getNextMonthFirstDay(){
	var nowdays = new Date();
	var year = nowdays.getFullYear();
	var month = nowdays.getMonth();
	if (month == 11) {
		month = 1;
		year = year + 1;
	}else{
		month += 2;
	}
	if (month < 10) {
		month = "0" + month;
	}
	var firstDay = year + "-" + month + "-" + "01";// 下个月的第一天

	var myDate = new Date(year, month, 0);

	return firstDay;
}

/*-- 获取下月最后一天 --*/
function getNestMonthLastDay(){
	var nowdays = new Date();
	var year = nowdays.getFullYear();
	var month = nowdays.getMonth();
	if (month == 11) {
		month = 1;
		year = year + 1;
	}else{
		month += 2;
	}
	if (month < 10) {
		month = "0" + month;
	}

	var myDate = new Date(year, month, 0);
	var lastDay = year + "-" + month + "-" + myDate.getDate();// 下个月的最后一天

	return lastDay;
}