package com.crm.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * 时间工具类
 * 
 * @author JingChenglong 2016-08-24 15:27
 * 
 *         ************************************ *** 用于处理常用的时间及其相关操作 *** * * * *
 *         ********************************
 */
public class TimeUtils {

	/*-- 时间常量：单位为ms --*/
	public static final long MILLISECOND_HALFMINUTE = 30000L;// 半分钟
	public static final long MILLISECOND_ONEMINUTE = 60000L;// 一分钟
	public static final long MILLISECOND_FIVEMINUTES = 300000L;// 五分钟
	public static final long MILLISECOND_TENMINUTES = 600000L;// 十分钟
	public static final long MILLISECOND_HALFHOUR = 1800000L;// 半小时
	public static final long MILLISECOND_HOUR = 3600000L;// 一小时
	public static final long MILLISECOND_DAY = 86400000L;// 一天

	/*-- 时间格式化常量 --*/
	public static SimpleDateFormat ymdSDF = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat yearSDF = new SimpleDateFormat("yyyy");
	public static SimpleDateFormat monthSDF = new SimpleDateFormat("MM");
	public static SimpleDateFormat daySDF = new SimpleDateFormat("dd");
	public static SimpleDateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat yyyyMMddHH_NOT_ = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat MMddHHmm_NOT_ = new SimpleDateFormat("MMddHHmm");

	public static SimpleDateFormat CLIENT_SHOW = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	public static SimpleDateFormat DING_CLIENT_SHOW = new SimpleDateFormat("MM-dd HH:mm");
	public static SimpleDateFormat YM = new SimpleDateFormat("yyyy年MM月dd日");

	/*-- 格式化客资对应时间 --*/
	public static String formatClientTime(String time) {

		if (StringUtil.isEmpty(time)) {
			return "";
		}
		if (!validTime(time, "yyyy-MM-dd HH:mm:ss")) {
			return time;
		}

		try {
			time = CLIENT_SHOW.format(yyyyMMddHHmmss.parse(time));
		} catch (Exception e) {
			time = "";
		}

		return time;
	}

	/** -- 格式化时间 -- **/
	public static String formatTime(String time, String pattern) {

		if (StringUtils.isEmpty(time) || StringUtils.isEmpty(pattern)) {
			return "";
		}

		DateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);

		Date date = null;
		try {
			date = (Date) yyyyMMddHHmmss.parse(time);
		} catch (ParseException e) {
			return "";
		}

		return formatter.format(date);
	}

	/*-- 钉钉端格式化客资对应时间"01-01 11：11" --*/
	public static String formatDingClientTime(String time) {

		if (StringUtil.isEmpty(time)) {
			return "&nbsp;";
		}
		if (!validTime(time, "yyyy-MM-dd HH:mm:ss")) {
			return time;
		}

		try {
			return DING_CLIENT_SHOW.format(yyyyMMddHHmmss.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	/*-- 格式化客资婚期对应时间 --*/
	public static String formatMarryTime(String time) {

		if (StringUtil.isEmpty(time)) {
			return "";
		}
		if (!validTime(time, "yyyy-MM-dd HH:mm:ss")) {
			return time;
		}

		try {
			return YM.format(yyyyMMddHHmmss.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	/*-- 获得当前时间 /格式：2014-12-02 10:38:53 --*/
	public static String getCurrentTime() {
		return yyyyMMddHHmmss.format(new Date());
	}

	/*-- 根据指定时间格式取得当前系统时间 --*/
	public static String getSysTime(String pattern) {

		return formatSysTime(new SimpleDateFormat(pattern));
	}

	/*-- 格式化系统时间 --*/
	private static String formatSysTime(SimpleDateFormat format) {

		return format.format(Calendar.getInstance().getTime());
	}

	/*-- 根据指定格式格式化指定时间 --*/
	public static String format(Date date, String pattern) {

		return new SimpleDateFormat(pattern, Locale.ENGLISH).format(date);
	}

	/*-- 指定时间加一个月 --*/
	public static Date addMonth(Date date, int month) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}

	/*-- 获取指定时间的昨天时间 --*/
	public static Date getYesterday() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/*-- 指定时间减一个月 --*/
	public static Date getLastmonth() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

	/*-- 指定时间减一年 --*/
	public static Date getLastyear() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		return cal.getTime();
	}

	/*-- 当前时间加指定天数 --*/
	public static Date getDate(int days) {

		return getDate(new Date(), days);
	}

	/*-- 指定时间加指定天数 --*/
	public static Date getDate(Date date, int days) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	/*-- 指定时间加指定秒数 --*/
	public static Date getDateBySecond(Date date, int second) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
	}

	/*-- 当前时间加指定小时 --*/
	public static Date getHour(int hours) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return cal.getTime();
	}

	/*-- 校验指定时间是否为指定格式 --*/
	public static boolean validTime(String str, String pattern) {

		DateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);

		Date date = null;
		try {
			date = (Date) formatter.parse(str);
		} catch (ParseException e) {
			return false;
		}

		return str.equals(formatter.format(date));
	}

	/*-- 将指定时间字符串格式化为时间对象 --*/
	public static Date format(String str, String pattern) {

		DateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);

		Date date = null;
		try {
			date = (Date) formatter.parse(str);
		} catch (ParseException e) {
			return null;
		}

		return date;
	}

	/*-- 获得当前年份 --*/
	public static String getSysYear() {

		return getSysTime("yyyy");
	}

	/*-- 获得当前时间 ：精确到秒--*/
	public static String getSysTime() {

		return getSysTime("yyyy-MM-dd HH:mm:ss");
	}

	/*-- 获得当前时间 ：精确到分--*/
	public static String getSysTimeSecond() {

		return getSysTime("yyyy-MM-dd HH:mm");
	}

	/*-- 获得当前时间 ：时-分--*/
	public static String getSysTimeHourSecond() {

		return getSysTime("HH:mm");
	}

	/*-- 获得当前时间：精确到毫秒 --*/
	public static String getSysTimeS() {

		return getSysTime("yyyy-MM-dd HH:mm:ss,SSS");
	}

	/*-- 获得当前时间秒级长整数 --*/
	public static String getSysTimeLong() {

		return getSysTime("yyyyMMddHHmmss");
	}

	/*-- 获得当前时间毫秒级长整数 --*/
	public static String getSysTimeSLong() {

		return getSysTime("yyyyMMddHHmmssSSS");
	}

	/*-- 获得当前日期 --*/
	public static String getSysdate() {

		return getSysTime("yyyy-MM-dd");
	}

	/*-- 获得当前时间年月长整数 --*/
	public static String getSysyearmonthInt() {

		return getSysTime("yyyyMM");
	}

	/*-- 获得当前时间年月日长整数 --*/
	public static String getSysdateInt() {

		return getSysTime("yyyyMMdd");
	}

	public static String getYmdHHNotDateStr(Date date) {

		return yyyyMMddHH_NOT_.format(date.getTime());
	}

	public static String getYmdDateStr(Date date) {

		return yyyyMMddHH_NOT_.format(date.getTime());
	}

	/*-- 获得当天零点 --*/
	public static String getSysdateTimeStart() {

		return getSysdate() + " 00:00:00";
	}

	/*-- 获得当天最后时间 --*/
	public static String getSysdateTimeEnd() {

		return getSysdate() + " 23:59:59";
	}

	/*-- 获得月日时分整数时间 --*/
	public static int getIntTime(Date date) {

		return Integer.valueOf(MMddHHmm_NOT_.format(date));
	}

	/*-- 获得当前时间月日时分整数时间 --*/
	public static int getIntTimeNow() {

		return getIntTime(new Date());
	}

	/*-- 获得指定秒数后的月日时分整数时间 --*/
	public static int getIntTimeFuture(int future) {

		return getIntTime(getSecond(future));
	}

	public static String getSysdateTimeEndLong() {

		return getSysdateInt() + "235959";
	}

	public static String getSysDateLocal() {

		return getSysTime("yyyy年MM月dd日");
	}

	public static String getTimeFormat(String str) {

		return format(format(str, "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss");
	}

	public static String getDateFormat(String str) {

		return format(format(str, "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd");
	}

	public static String getDateFormatLocal(String str) {

		return format(format(str, "yyyy-MM-dd HH:mm:ss"), "yyyy年MM月dd日");
	}

	public static String getYesterdayInt() {

		return format(getYesterday(), "yyyyMMdd");
	}

	public static String getYesterdayDate() {

		return format(getYesterday(), "yyyy-MM-dd");
	}

	private static int getMondayPlus() {

		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == 1) {
			return -6;
		} else {
			return 2 - dayOfWeek;
		}
	}

	public static String getLastmondayInt() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, getMondayPlus() - 7);

		return format(cal.getTime(), "yyyyMMdd");
	}

	public static String getLastmondayDate() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, getMondayPlus() - 7);

		return format(cal.getTime(), "yyyy-MM-dd");
	}

	private static int getSundayPlus() {

		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == 1) {
			return -7;
		} else {
			return 1 - dayOfWeek;
		}
	}

	public static String getLastsundayInt() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, getSundayPlus());

		return format(cal.getTime(), "yyyyMMdd");
	}

	public static String getLastsundayDate() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, getSundayPlus());

		return format(cal.getTime(), "yyyy-MM-dd");
	}

	public static String getLastmonthInt() {

		return format(getLastmonth(), "yyyyMM");
	}

	public static String getLastmonthDate() {

		return format(getLastmonth(), "yyyy-MM");
	}

	public static String getLastmonthenddayInt() {

		return format(getDate(getThismonthTime(1, 0, 0), -1), "yyyyMMdd");
	}

	public static String getLastmonthendDate() {

		return format(getDate(getThismonthTime(1, 0, 0), -1), "yyyy-MM-dd");
	}

	public static String getThismonthInt() {

		return format(getThismonthTime(1, 0, 0), "yyyyMM");
	}

	public static String getThismonthDate() {

		return format(getThismonthTime(1, 0, 0), "yyyy-MM");
	}

	public static String getMonthstarttime(String month) {

		return month + "01000000";
	}

	public static String getMonthendtime(String month) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(format(month, "yyyyMM"));
		cal.add(Calendar.MONTH, 1);

		return format(getDate(cal.getTime(), -1), "yyyyMMdd") + "235959";
	}

	public static String getLastyearInt() {

		return format(getLastyear(), "yyyy");
	}

	public static String getDateInt(int days) {

		return format(getDate(days), "yyyyMMdd");
	}

	public static String getDateFormat(int days) {

		return format(getDate(days), "yyyy-MM-dd");
	}

	public static String getDateFormatLocal(int days) {

		return format(getDate(days), "yyyy年MM月dd日");
	}

	public static String getTimeFormatDays(int days) {

		return format(getDate(days), "yyyy-MM-dd HH:mm:ss");
	}

	public static String getTimeFormatHours(int hours) {

		return format(getHour(hours), "yyyy-MM-dd HH:mm:ss");
	}

	public static Date getHourTime(int hour) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		// calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getHourTime(int hour, int minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getHourTime(int hour, int minute, int second) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getYesterdayHourTime(int hour) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getYesterdayHourTime(int hour, int minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getYesterdayHourTime(int hour, int minute, int second) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getTomorrowHourTime(int hour) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getTomorrowHourTime(int hour, int minute, int second) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getTomorrowHourTime(int hour, int minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getThismonthTime(int day, int hour, int minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static Date getNextmonthTime(int day, int hour, int minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();

		return time;
	}

	public static String getSecondTime(String date, String pattern, int second) {
		Date d = format(date, pattern);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.SECOND, second);
		Date time = calendar.getTime();
		return format(time, pattern);
	}

	public static Date getMinute(int minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, minute);
		Date time = calendar.getTime();

		return time;
	}

	/*-- 当前时间增加固定秒数 --*/
	public static Date getSecond(int second) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, second);
		Date time = calendar.getTime();

		return time;
	}

	public static int countMonths(String end, String start, String pattern) {

		Calendar calend = Calendar.getInstance();
		calend.setTime(format(end, pattern));

		Calendar calstart = Calendar.getInstance();
		calstart.setTime(format(start, pattern));

		return (calend.get(Calendar.YEAR) * 12 + calend.get(Calendar.MONTH))
				- (calstart.get(Calendar.YEAR) * 12 + calstart.get(Calendar.MONTH));
	}

	public static long subtract(String end, String start, String pattern) {

		return format(end, pattern).getTime() - format(start, pattern).getTime();
	}

	public static long subtractDay(String end, String start, String pattern) {

		return subtract(end, start, pattern) / MILLISECOND_DAY;
	}

	public static long subtractSSS(String start) {

		return subtract(getSysTimeS(), start, "yyyy-MM-dd HH:mm:ss,SSS");
	}

	public static long subtractSSS(String end, String start) {

		return subtract(end, start, "yyyy-MM-dd HH:mm:ss,SSS");
	}

	public static long subtractLong(String start) {

		return subtract(getSysTimeLong(), start, "yyyyMMddHHmmss");
	}

	public static long subtractLong(String end, String start) {

		return subtract(end, start, "yyyyMMddHHmmss");
	}

	public static String formatGMTTime(String str, String pattern, String TimeZoneFormat) {

		DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

		Date date = null;
		try {
			date = (Date) dateFormat.parse(str);
		} catch (ParseException e) {
			return "";
		}

		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
		format.setTimeZone(TimeZone.getTimeZone(TimeZoneFormat));
		String time = format.format(date);

		return time;
	}

	public static String formatTime(String str, String strpattern, String pattern, String TimeZoneFormat) {

		DateFormat dateFormat = new SimpleDateFormat(strpattern, Locale.ENGLISH);

		Date date = null;
		try {
			date = (Date) dateFormat.parse(str);
		} catch (ParseException e) {
			return "";
		}

		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
		format.setTimeZone(TimeZone.getTimeZone(TimeZoneFormat));
		String time = format.format(date);

		return time;
	}

	public static String formatGMT8Time(String str) {

		return formatGMTTime(str, "yyyy-MM-dd HH:mm:ss", "GMT+08:00");
	}

	/*-- 当前线程挂起指定毫秒 --*/
	public static void sleep(long millis) {

		if (millis <= 0L) {
			return;
		}

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	public static String secondtoMinutesecond(long second) {

		String result = "";
		result = second / 60 > 0 ? second / 60 + "分" : "";
		result = result + (second % 60 > 0 ? (second % 60 + "秒") : (StringUtil.isNotEmpty(result) ? "钟" : "0分钟"));

		return result;
	}

	/*-- 长整数个数为时间字符串 --*/
	public static String convertTime(long time) {

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
	}

	/**
	 * 可以获取昨天的日期 格式：2014-12-01
	 * 
	 * @return String
	 */
	public static String getYesterdayYYYYMMDD() {
		Date date = new Date(System.currentTimeMillis() - MILLISECOND_DAY);
		String str = yyyyMMdd.format(date);
		try {
			date = yyyyMMddHHmmss.parse(str + " 00:00:00");
			return yyyyMMdd.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*-- 可以获取后退N天的日期   --*/
	public static String getStrDate(String backDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, Integer.parseInt("-" + backDay));
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}

	/*-- 可以获取后退N天的时间  --*/
	public static String getBackTime(String backDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, Integer.parseInt("-" + backDay));
		return yyyyMMddHHmmss.format(calendar.getTime());
	}

	/*-- 可以获取N天后的日期   --*/
	public static String getFutureDate(String futureDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, Integer.parseInt(futureDay));
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}

	/*-- 可以获取N天后的时间 --*/
	public static String getFutureTime(String futureDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, Integer.parseInt(futureDay));
		return yyyyMMddHHmmss.format(calendar.getTime());
	}

	/*-- 获取当前的年、月、日   --*/
	public static String getCurrentYear() {
		return yearSDF.format(new Date());
	}

	public static String getCurrentMonth() {
		return monthSDF.format(new Date());
	}

	public static String getCurrentDay() {
		return daySDF.format(new Date());
	}

	/**
	 * 获取年月日 也就是当前时间 格式：2014-12-02
	 * 
	 * @return String
	 */
	public static String getCurrentymd() {
		return ymdSDF.format(new Date());
	}

	/**
	 * 获取今天0点开始的秒数
	 * 
	 * @return long
	 */
	public static long getTimeNumberToday() {
		Date date = new Date();
		String str = yyyyMMdd.format(date);
		try {
			date = yyyyMMdd.parse(str);
			return date.getTime() / 1000L;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 获取今天的日期 格式：20141202
	 * 
	 * @return String
	 */
	public static String getTodateString() {
		String str = yyyyMMddHH_NOT_.format(new Date());
		return str;
	}

	/**
	 * 获取昨天的日期 格式：20141201
	 * 
	 * @return String
	 */
	public static String getYesterdayString() {
		Date date = new Date(System.currentTimeMillis() - MILLISECOND_DAY);
		String str = yyyyMMddHH_NOT_.format(date);
		return str;
	}

	/**
	 * 获得昨天零点
	 * 
	 * @return Date
	 */
	public static Date getYesterDayZeroHour() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		return cal.getTime();
	}

	/**
	 * 把long型日期转String ；---OK
	 * 
	 * @param date
	 *            long型日期；
	 * @param format
	 *            日期格式；
	 * @return
	 */
	public static String longToString(long date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		// 前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
		java.util.Date dt2 = new Date(date * 1000L);
		String sDateTime = sdf.format(dt2); // 得到精确到秒的表示：08/31/2006 21:08:00
		return sDateTime;
	}

	/**
	 * 获得今天零点
	 * 
	 * @return Date
	 */
	public static Date getTodayZeroHour() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		return cal.getTime();
	}

	/**
	 * 获得昨天23时59分59秒
	 * 
	 * @return
	 */
	public static Date getYesterDay24Hour() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.HOUR, 23);
		return cal.getTime();
	}

	/**
	 * String To Date ---OK
	 * 
	 * @param date
	 *            待转换的字符串型日期；
	 * @param format
	 *            转化的日期格式
	 * @return 返回该字符串的日期型数据；
	 */
	public static Date stringToDate(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获得指定日期所在的自然周的第一天，即周日
	 * 
	 * @param date
	 *            日期
	 * @return 自然周的第一天
	 */
	public static Date getStartDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, 1);
		date = c.getTime();
		return date;
	}

	/**
	 * 获得指定日期所在的自然周的最后一天，即周六
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, 7);
		date = c.getTime();
		return date;
	}

	/**
	 * 获得指定日期所在当月第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStartDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		date = c.getTime();
		return date;
	}

	/**
	 * 获取当前日期所在当月第一天的零点日期字符串
	 */
	public static String getStartDayOfMonthDay() {

		return yyyyMMdd.format(getStartDayOfMonth(new Date()));
	}

	/**
	 * 获取当前日期所在当月最后一天的零点日期字符串
	 */
	public static String getEndDayOfMonthDay() {

		return yyyyMMdd.format(getLastDayOfMonth(new Date()));
	}

	/**
	 * 获得指定日期所在当月最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		date = c.getTime();
		return date;
	}

	/**
	 * 获得指定日期的下一个月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStartDayOfNextMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		date = c.getTime();
		return date;
	}

	/**
	 * 获得指定日期的下一个月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfNextMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, 1);
		c.add(Calendar.MONTH, 2);
		c.add(Calendar.DATE, -1);
		date = c.getTime();
		return date;
	}

	/**
	 * 
	 * 求某一个时间向前多少秒的时间(currentTimeToBefer)---OK
	 * 
	 * @param givedTime
	 *            给定的时间
	 * @param interval
	 *            间隔时间的毫秒数；计算方式 ：n(天)*24(小时)*60(分钟)*60(秒)(类型)
	 * @param format_Date_Sign
	 *            输出日期的格式；如yyyy-MM-dd、yyyyMMdd等；
	 */
	public static String givedTimeToBefer(String givedTime, long interval, String format_Date_Sign) {
		String tomorrow = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format_Date_Sign);
			Date gDate = sdf.parse(givedTime);
			long current = gDate.getTime(); // 将Calendar表示的时间转换成毫秒
			long beforeOrAfter = current - interval * 1000L; // 将Calendar表示的时间转换成毫秒
			Date date = new Date(beforeOrAfter); // 用timeTwo作参数构造date2
			tomorrow = new SimpleDateFormat(format_Date_Sign).format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return tomorrow;
	}

	/**
	 * 把String 日期转换成long型日期；---OK
	 * 
	 * @param date
	 *            String 型日期；
	 * @param format
	 *            日期格式；
	 * @return
	 */
	public static long stringToLong(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date dt2 = null;
		long lTime = 0;
		try {
			dt2 = sdf.parse(date);
			// 继续转换得到秒数的long型
			lTime = dt2.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return lTime;
	}

	/**
	 * 得到二个日期间的间隔日期；
	 * 
	 * @param endTime
	 *            结束时间
	 * @param beginTime
	 *            开始时间
	 * @param isEndTime
	 *            是否包含结束日期；
	 * @return
	 */
	public static Map<String, String> getTwoDay(String endTime, String beginTime, boolean isEndTime) {
		Map<String, String> result = new HashMap<String, String>();
		if ((endTime == null || endTime.equals("") || (beginTime == null || beginTime.equals(""))))
			return null;
		try {
			java.util.Date date = ymdSDF.parse(endTime);
			endTime = ymdSDF.format(date);
			java.util.Date mydate = ymdSDF.parse(beginTime);
			long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
			result = getDate(endTime, Integer.parseInt(day + ""), isEndTime);
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 得到二个日期间的间隔日期；
	 * 
	 * @param endTime
	 *            结束时间
	 * @param beginTime
	 *            开始时间
	 * @param isEndTime
	 *            是否包含结束日期；
	 * @return
	 */
	public static Integer getTwoDayInterval(String endTime, String beginTime, boolean isEndTime) {
		if ((endTime == null || endTime.equals("") || (beginTime == null || beginTime.equals(""))))
			return 0;
		long day = 0l;
		try {
			java.util.Date date = ymdSDF.parse(endTime);
			endTime = ymdSDF.format(date);
			java.util.Date mydate = ymdSDF.parse(beginTime);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return 0;
		}
		return Integer.parseInt(day + "");
	}

	/**
	 * 根据结束时间以及间隔差值，求符合要求的日期集合；
	 * 
	 * @param endTime
	 * @param interval
	 * @param isEndTime
	 * @return
	 */
	public static Map<String, String> getDate(String endTime, Integer interval, boolean isEndTime) {
		Map<String, String> result = new HashMap<String, String>();
		if (interval == 0 || isEndTime) {
			if (isEndTime)
				result.put(endTime, endTime);
		}
		if (interval > 0) {
			int begin = 0;
			for (int i = begin; i < interval; i++) {
				endTime = givedTimeToBefer(endTime, 86400L, "yyyy-MM-dd");
				result.put(endTime, endTime);
			}
		}
		return result;
	}
}
