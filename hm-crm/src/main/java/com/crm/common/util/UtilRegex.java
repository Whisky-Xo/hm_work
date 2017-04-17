package com.crm.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class UtilRegex {
	private static final Logger logger = LoggerFactory.getLogger(UtilRegex.class);

	private static final String UUID_PATTERN = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
	private static Pattern uuidPattern = Pattern.compile(UUID_PATTERN);
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
	private static final String CHINA_MOBILE_PATTERN = "^1[3-8][0-9]{9}$";
	private static final String CHINA_PHONE_PATTERN = "^0\\d{1,4}-{0,1}\\d{7,8}$";
	private static final String IP_ADDRESS_PATTERN = "^\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}$";
	private static Pattern chinaMobile = Pattern.compile(CHINA_MOBILE_PATTERN);
	private static Pattern chinaPhone = Pattern.compile(CHINA_PHONE_PATTERN);
	private static Pattern ipAddress = Pattern.compile(IP_ADDRESS_PATTERN);
	private static final String URL_PATTERN = "^((http|https|ftp)\\://)?([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
			+ "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
			+ "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
			+ "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
			+ "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
			+ "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
			+ "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
			+ "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
	private static Pattern urlPattern = Pattern.compile(URL_PATTERN);
	private static final String QQ_PATTERN = "^[1-9][0-9]{4,10}$";
	private static final String WECHAT_PATTERN = "^[a-zA-Z][a-zA-Z\\d_-]{5,19}$";
	private static Pattern qqPattern = Pattern.compile(QQ_PATTERN);
	private static Pattern wechatPattern = Pattern.compile(WECHAT_PATTERN);

	public static boolean validateUUID(final String src) {
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = uuidPattern.matcher(src);
			return m.matches();
		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}
	}

	public static boolean validateEmail(final String src) {
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = emailPattern.matcher(src);
			return m.matches();
		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}

	}

	/*-- 校验电话号码 --*/
	public static boolean validateMobile(final String src) {
		logger.debug("validate mobile[{}]...", src);
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = chinaMobile.matcher(src);
			return m.matches();
		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}
	}

	/*-- 校验手机号码 --*/
	public static boolean validatePhone(final String src) {
		logger.debug("validate phone[{}]...", src);
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = chinaPhone.matcher(src);
			return m.matches();
		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}
	}

	/*-- 校验电话号码和手机号码 --*/
	public static boolean validateMobileOrPhone(final String src) {
		logger.debug("validate mobile or phone[{}]...", src);
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = chinaMobile.matcher(src);
			if (m.matches())
				return true;
			else {
				m = chinaPhone.matcher(src);
				return m.matches();
			}

		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}
	}

	/*-- 校验IP地址 --*/
	public static boolean validateIpAddress(final String src) {
		logger.debug("validate ip address[{}]...", src);
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = ipAddress.matcher(src);
			return m.matches();
		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}
	}

	/*-- 校验URL --*/
	public static boolean validateUrl(final String src) {
		logger.debug("validate url[{}]");
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = urlPattern.matcher(src);
			return m.matches();
		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}
	}

	public static boolean validateQq(final String src) {
		logger.debug("validate url[{}]");
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = qqPattern.matcher(src);
			return m.matches();
		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}
	}

	public static boolean validateWechat(final String src) {
		logger.debug("validate url[{}]");
		if (StringUtils.isEmpty(src))
			return false;
		try {
			Matcher m = wechatPattern.matcher(src);
			return m.matches();
		} catch (Exception e) {
			logger.warn("exception occured", e);
			return false;
		}
	}

}