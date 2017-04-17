package com.crm.common.util;

import org.springframework.util.StringUtils;

/**
 * 数据库工具类
 * 
 * @author JingChenglong 2017-01-19 11:30
 *
 */
public class DBSplit {

	public static final String INFO = " hm_crm_client_info_";// 客资信息表
	public static final String LOG = " hm_crm_client_log_";// 客资日志表
	public static final String INVITA_LOG = " hm_crm_invitation_log_";// 客资邀约记录表
	public static final String SHOP_LOG = " hm_crm_shop_meet_log_";// 客资洽谈记录表

	/*-- 获取表名 --*/
	public static String getTable(TableEnum tableEnum, Object companyId) {

		if (tableEnum == null || StringUtils.isEmpty(companyId)) {
			return "";
		}

		String tableName = "";
		switch (tableEnum) {
		case info:
			tableName = INFO + String.valueOf(companyId);
			break;
		case log:
			tableName = LOG + String.valueOf(companyId);
			break;
		case invita:
			tableName = INVITA_LOG + String.valueOf(companyId);
			break;
		case shopmeet:
			tableName = SHOP_LOG + String.valueOf(companyId);
			break;
		default:
			break;
		}
		return tableName += " ";
	}
}