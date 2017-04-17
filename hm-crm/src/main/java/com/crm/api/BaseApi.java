package com.crm.api;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.crm.api.http.HttpServiceUtil;
import com.crm.common.util.HmacSHA1Utils;
import com.crm.common.util.MD5Utils;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;

/**
 * 接口调用基类
 * 
 * @author JingChenglong 2016-09-08 11:05
 *
 */
public abstract class BaseApi {

	protected String format = "json";

	public String doService(Map<String, Object> reqcontent, String action, String url) throws EduException {
		Map<String, Object> requestMap = new LinkedHashMap<String, Object>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, Object> common = new LinkedHashMap<String, Object>();
		common.put("action", action);
		common.put("reqtime", TimeUtils.getSysTimeLong());
		map.put("common", common);
		map.put("content", reqcontent);
		requestMap.put("request", map);
		String reqcontentStr = JSON.toJSONString(requestMap);
		String response = "";
		try {
			response = HttpServiceUtil.doService(url, format, getSign(reqcontentStr), reqcontentStr);
		} catch (Exception e) {
			throw new EduException(e.getMessage());
		}
		return response;
	}

	protected abstract String getSign(String reqcontentStr) throws Exception;

	public String doService(Map<String, Object> reqcontent, String action, String url, String filepath, String key)
			throws EduException {
		reqcontent.put("action", action);
		reqcontent.put("reqtime", TimeUtils.getSysTimeLong());
		reqcontent.put("reqlength", getFileLength(filepath));
		reqcontent.put("reqcontentmd5", MD5Utils.md5file(filepath).toLowerCase());
		try {
			String sign = HmacSHA1Utils.signatureString(String.valueOf(reqcontent.get("reqcontentmd5")), key, "UTF-8")
					.trim();
			reqcontent.put("sign", sign);
			HttpServiceUtil.doUpService(url, format, reqcontent, filepath);
		} catch (Exception e) {
		}
		return "";
	}

	public static long getFileLength(String filename) {

		File file = new File(filename);
		if (file.isFile() && file.exists()) {
			return file.length();
		}

		return 0L;
	}
}