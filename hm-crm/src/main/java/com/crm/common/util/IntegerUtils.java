package com.crm.common.util;

/**
 * 整型常量类
 * 
 * @author JingChenglong 2017-01-06 11:17
 *
 */
public class IntegerUtils {

	/*-- 判断是否为空或等于0 --*/
	public static Boolean isNullOrZero(Integer num) {

		if (num == null || num == 0) {
			return true;
		}

		return false;
	}
}