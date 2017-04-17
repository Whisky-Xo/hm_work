package com.crm.test;

import java.util.Date;

import org.junit.Test;

import com.crm.common.util.DateUtil;
import com.crm.common.util.UtilRegex;

public class DateTest {

	@Test
	public void testDevide() {
		System.out.println(devision(45, 15));
		System.out.println(devision(46, 15));
	}

	public static int devision(int a, int b) {
		return a / b + (a % b == 0 ? 0 : 1);
	}
	
	@Test
	public void dateTest(){
		int date = DateUtil.toInt(new Date());
		int day = DateUtil.getDay(date);
		int month = DateUtil.getMonth(date);
		int year = DateUtil.getYear(date);
		String dateStr = DateUtil.getToday();//2016-08-12
		String daysStr = dateStr.substring(8, 10);
		System.out.println(dateStr);
		System.out.println(daysStr);
		System.out.println(day);
		System.out.println(month);
		System.out.println(year);
	}
	
	@Test
	public void strTest(){
		int date = DateUtil.toInt("2016-09-20");
		System.out.println(date);
	}
	
	@Test
	public void todayTest(){
		String today = DateUtil.getToday();
		System.out.println(today);
		String tomorrow = DateUtil.getTomorrow();
		System.out.println(tomorrow);
		
		int unix_today = DateUtil.getUnixTime(today);
		int unix_tomorrow = DateUtil.getUnixTime(tomorrow);
		int i = DateUtil.getNow();
		System.out.println(unix_today);
		System.out.println(i);
		System.out.println(unix_tomorrow);
	}
	
	@Test
	public void unixTest(){
		String date = "2016-08-16";
		int unix_time = DateUtil.getUnixTime(date);
		System.out.println(unix_time);
	}
	
	
	@Test
	public void regTest(){
		String qq = "79086563";
		System.out.println(UtilRegex.validateQq(qq));
		String wechat = "a_0000000";
		System.out.println(UtilRegex.validateWechat(wechat));
	}
	
}
