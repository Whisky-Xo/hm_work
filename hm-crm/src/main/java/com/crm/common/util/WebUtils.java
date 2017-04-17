package com.crm.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import com.crm.common.util.StringUtil;
import com.crm.api.constant.Constants;

/**
 * web请求工具类
 * 
 * @author JingChenglong 2016-08-24 15:31
 * 
 * ********************************
 * *** 用于处理常用的web请求中的通用操作	***
 * ********************************
 */
public class WebUtils {

	public static Object getHeaderBean(HttpServletRequest request, Object bean) {

		@SuppressWarnings("unchecked")
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames == null) {
			return null;
		}

		String key = "";
		String value = "";
		Map<String, Object> map = new HashMap<String, Object>();

		while (headerNames.hasMoreElements()) {
			key = StringUtil.nullToStrTrim(headerNames.nextElement());
			if (StringUtil.isNotEmpty(key)) {
				value = StringUtil.nullToStrTrim(request.getHeader(key));
				map.put(key.toLowerCase(), StringUtil.decode(value));
			}
		}

		try {
			BeanUtils.populate(bean, map);
		} catch (Exception e) {
			return null;
		}

		return bean;
	}

	/*-- 获得请求ip --*/
	public static String getIP(HttpServletRequest request) {

		String ip = "";

		ip = request.getHeader("x-forwarded-for");
		if (StringUtil.isEmpty(ip) || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			if (StringUtil.isEmpty(ip) || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
				if (StringUtil.isEmpty(ip) || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
					ip = request.getRemoteAddr();
				}
			}
		} else {
			String[] ips = ip.split(",");
			if (ips.length > 1) {
				String tempIP = "";
				for (int i = 0; i < ips.length; i++) {
					tempIP = StringUtil.nullToStrTrim(ips[i]);
					if (StringUtil.isNotEmpty(tempIP) && !Constants.UNKNOWN.equalsIgnoreCase(tempIP)) {
						ip = tempIP;
						break;
					}
				}
			}
		}

		return ip;
	}

	public static String getLocalHost() {

		try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
		}

		return "";
	}

	public static void openURL(String urlStr, int connecttimeout, int readtimeout) {

		try {
			HttpURLConnection uRLConnection = (HttpURLConnection) new URL(urlStr).openConnection();
			if (connecttimeout > 0) {
				uRLConnection.setConnectTimeout(connecttimeout);
			}
			if (readtimeout > 0) {
				uRLConnection.setReadTimeout(readtimeout);
			}
			new InputStreamReader(uRLConnection.getInputStream());
		} catch (Exception e) {
		}
	}

	public static String getURLReturn(String urlStr, int connecttimeout, int readtimeout) throws Exception {

		HttpURLConnection uRLConnection = (HttpURLConnection) new URL(urlStr).openConnection();
		if (connecttimeout > 0) {
			uRLConnection.setConnectTimeout(connecttimeout);
		}
		if (readtimeout > 0) {
			uRLConnection.setReadTimeout(readtimeout);
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream()));

		StringBuffer stringBuffer = new StringBuffer();
		String temp = "";
		while ((temp = reader.readLine()) != null) {
			stringBuffer.append(temp).append("\n");
		}

		return StringUtil.nullToStrTrim(stringBuffer.toString());
	}

	public static String postURLReturn(String urlStr, String paras, int connecttimeout, int readtimeout, String charset,
			String split) throws Exception {

		HttpURLConnection uRLConnection = (HttpURLConnection) new URL(urlStr).openConnection();
		if (connecttimeout > 0) {
			uRLConnection.setConnectTimeout(connecttimeout);
		}
		if (readtimeout > 0) {
			uRLConnection.setReadTimeout(readtimeout);
		}
		uRLConnection.setDoOutput(true);

		OutputStreamWriter writer = null;
		if (StringUtil.isEmpty(charset)) {
			writer = new OutputStreamWriter(uRLConnection.getOutputStream());
		} else {
			writer = new OutputStreamWriter(uRLConnection.getOutputStream(), charset);
		}

		writer.write(paras);
		writer.flush();

		BufferedReader reader = null;
		if (StringUtil.isEmpty(charset)) {
			reader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream()));
		} else {
			reader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), charset));
		}

		StringBuffer stringBuffer = new StringBuffer();
		String temp = "";
		while ((temp = reader.readLine()) != null) {
			stringBuffer.append(temp).append(split);
		}
		writer.close();
		reader.close();

		return StringUtil.nullToStrTrim(stringBuffer.toString());
	}

	/*-- 获得请求体中的指定信息 --*/
	public static String getHeader(HttpServletRequest request, String name) {
		return StringUtil.nullToStrTrim(request.getHeader(name));
	}

	/*-- 获得请求体中的指定信息并解码 --*/
	public static String getHeaderDecode(HttpServletRequest request, String name) {
		return StringUtil.decode(WebUtils.getHeader(request, name));
	}

	public static void flushBuffer(HttpServletResponse response) {

		try {
			response.flushBuffer();
		} catch (IOException e) {
		}
	}
}
