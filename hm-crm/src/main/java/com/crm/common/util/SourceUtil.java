package com.crm.common.util;

import java.util.List;
import com.crm.api.constant.Constants;
import com.crm.model.Source;

/**
 * 渠道工具类
 * 
 * @author JingChenglong 2017-01-02 15:59
 *
 */
public class SourceUtil {

	/** -- 获取渠道ID限定字符串 -- **/
	public static String getSrcIdStr(List<Source> sourceList) {

		if (sourceList == null || sourceList.size() == 0) {
			return "";
		}

		String sourceIds = "";

		for (Source src : sourceList) {
			sourceIds += src.getSrcId();
			sourceIds += Constants.STR_SEPARATOR;
		}
		if (sourceIds.length() != 0) {
			sourceIds = sourceIds.substring(0, sourceIds.length() - 1);
		}

		return sourceIds;
	}
}