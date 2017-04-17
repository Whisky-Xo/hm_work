package com.crm.common.util;

import java.util.Map;

/**
 * 客资信息工具类
 * 
 * @author JingChenglong 2017-01-02 15:52
 *
 */
public class ClientInfoUtil {

	/*-- 获取分页标识code --*/
	public static int getPageCode(Map<String, String> maps) {

		int sortCode = StringUtil.nullToIntZero(maps.get("sortcode"));
		String flag = maps.get("flag");// 分页点击标识，"1"表示分页点击

		if (!"1".equals(flag)) {
			// 如果不是分页点击，排序标识升降翻转
			sortCode = sortCode == 0 ? 1 : 0;
		}
		
		return sortCode;
	}
}