package com.crm.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 日期工具
 * 
 * @author tuoxie
 *
 */
public class DateUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static String getSysDateStr() {
		return format(System.currentTimeMillis(), "yyyy.MM.dd");
	}

	/**
	 * 获取当前秒数
	 * 
	 * @return
	 */
	public static int getNow() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	/**
	 * 当前时间加相应描述
	 */
	public static int getAfter(int addend) {
		int now = (int) (System.currentTimeMillis() / 1000);
		int after = now + addend;
		return after;
	}

	public static String createdToStr(int created) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(new Date(created * 1000L));
	}

	public static int getSecond(Date date) {
		if (null == date) {
			return 0;
		}
		return (int) (date.getTime() / 1000);
	}

	/**
	 * 遍历日期
	 * 
	 * @param start
	 * @param end
	 *            不包含
	 */
	public static <T> List<T> iterate(int start, int end, Collector<T> operator) {
		return iterate(start, end, operator, Calendar.DAY_OF_MONTH);
	}

	public static <T> List<T> iterate(int start, int end, Collector<T> operator, int stepType) {
		Calendar startCalendar = toCalendar(start);
		Calendar endCalendar = toCalendar(end);
		endCalendar.add(Calendar.DAY_OF_MONTH, 1);
		List<T> result = new ArrayList<T>();

		for (; startCalendar.before(endCalendar); startCalendar.add(stepType, 1)) {
			T t = operator.collect(startCalendar.getTime());
			result.add(t);
		}
		return result;
	}

	public static interface Collector<T> {

		T collect(Date date);

	}

	public static Calendar toCalendar(int date) {
		Calendar c = Calendar.getInstance();
		if (date <= 0) {
			return c;
		}
		c.set(Calendar.YEAR, date / 10000);
		c.set(Calendar.MONTH, getMonth(date) - 1);
		c.set(Calendar.DAY_OF_MONTH, getDay(date));
		return c;
	}

	public static int getYear(int date) {
		return date / 10000;
	}

	public static int getDay(int date) {
		return date % 100;
	}

	public static int getMonth(int date) {
		return (date % 10000) / 100;
	}

	public static int toInt(Date date) {
		Calendar c = Calendar.getInstance();
		if (null != date) {
			c.setTime(date);
		}
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		return year * 10000 + month * 100 + day;
	}

	public static boolean isToday(String date) {
		try {
			return toStr(new Date()).equals(date);
		} catch (Exception e) {
			return false;
		}
	}

	public static String getWeek(String date) {
		try {
			Date d = sdf.parse(date);
			return getWeek(d);
		} catch (Exception e) {
		}
		return "未知";
	}

	public static String getWeek(Date d) {
		try {
			return new SimpleDateFormat("EEEE", Locale.CHINESE).format(d);
		} catch (Exception e) {
		}
		return "未知";
	}

	public static String toStr(int date) {
		Calendar c = toCalendar(date);
		return toStr(c.getTime());
	}

	public static String toStr(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return sdf.format(date);
	}

	public static Date toDate(String date) {
		try {
			return sdf.parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static String format(long time, String format) {
		return format(new Date(time), format);
	}

	public static String format(Date time, String format) {
		return new SimpleDateFormat(format).format(time);
	}

	public static String format(int time, String format) {
		return new SimpleDateFormat(format).format(toCalendar(time).getTime());
	}

	public static int getStartOfMonth() {
		return getStartOfMonth(new Date());
	}

	public static int getStartOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return toInt(c.getTime());
	}

	public static int getStartOfMonth(int date) {
		return getStartOfMonth(toCalendar(date).getTime());
	}

	public static int getEndOfMonth() {
		return getEndOfMonth(new Date());
	}

	public static int getEndOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return toInt(c.getTime());
	}

	public static int getEndOfMonth(int date) {
		return getEndOfMonth(toCalendar(date).getTime());
	}

	public static int addMonth(int date, int delta) {
		Calendar c = toCalendar(date);
		c.add(Calendar.MONTH, delta);
		return toInt(c.getTime());
	}

	public static int addDay(int date, int delta) {
		Calendar c = toCalendar(date);
		c.add(Calendar.DAY_OF_MONTH, delta);
		return toInt(c.getTime());
	}

	// 返回两个月份字符串之间的月份
	public static List<Map<String, String>> getMonthBetween(int minDate, int maxDate) throws ParseException {
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 格式化为年月
		SimpleDateFormat sdf2 = new SimpleDateFormat("M月");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMM");

		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();

		min.setTime(sdf.parse(minDate + ""));
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

		max.setTime(sdf.parse(maxDate + ""));
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

		Calendar curr = min;
		while (curr.before(max)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("month", sdf3.format(curr.getTime()));
			map.put("month_cn", sdf2.format(curr.getTime()));
			result.add(map);
			curr.add(Calendar.MONTH, 1);
		}

		return result;
	}

	public static String formatDate(int date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 格式化为年月
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		return sdf2.format(sdf.parse(date + ""));
	}

	public static int getCheckDate(int year) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 格式化为年月
		int date = Integer.parseInt(sdf.format(new Date())) + 10000 * year;
		return date;
	}

	public static int getStartOfYear(int date) {
		return Integer.parseInt(date / 10000 + "01");
	}

	public static int getEndOfYear(int date) {
		return Integer.parseInt(date / 10000 + "12");
	}

	public static String getToday() {
		return toStr(new Date());
	}

	public static String getTomorrow() {
		String today = getToday();
		return getNextDay(today);
	}

	public static String getNextDay(String dateStr) {
		Date date = toDate(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, +1);
		Date next = c.getTime();
		return toStr(next);
	}

	public static int toInt(String date) {
		return toInt(toDate(date));
	}

	public static int getUnixTime(String date) {
		Date d = toDate(date);
		return getSecond(d);
	}

	public static int getweek_(String week) {
		switch (week) {
		case "星期一":
			return 1;
		case "星期二":
			return 2;
		case "星期三":
			return 3;
		case "星期四":
			return 4;
		case "星期五":
			return 5;
		case "星期六":
			return 6;
		case "星期日":
			return 7;
		default:
			break;
		}
		return 7;
	}

	public static void main(String[] args) {
		System.out.println(getNow());
		System.out.println(getAfter(300));

		// System.out.println(format(toCalendar(20160202).getTime(),
		// "yyyy-MM-dd"));
		// System.out.println(toCalendar(20160101).getTime());
		// System.out.println(toCalendar(20161201).getTime());
		// System.out.println(toCalendar(20161231).getTime());
		// System.out.println(getStartOfMonth());
		// System.out.println(getEndOfMonth());
		// System.out.println(getWeek("2016-02-21"));
		// System.out.println(getWeek("2016-02-20"));
		// System.out.println(getCheckDate(2));
		// System.out.println(getCheckDate(3));
		// System.out.println(getStartOfYear(20160711));
		// System.out.println(getEndOfYear(20160711));
		// System.out.println(DateUtil.getToday());
		// try {
		// System.out.println(getMonthBetween(20150501, 20160701));
		// System.out.println(formatDate(20150501));
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
	}

}
