package com.crm.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crm.model.Staff;

public class CookieCompoment {

	private static Logger logger = LoggerFactory.getLogger(CookieCompoment.class);

	private final static String LOGIN_USER = "__pglu";

	private final static String SCENE_TYPE = "__pgst";

	private final static int LOGIN_EXPIRED = 30 * 86400;

	private final static int SCENE_EXPIRED = 30 * 86400;

	/**
	 * 设置当前场景类型
	 * 
	 * @param response
	 * @param type
	 */
	public static void setSceneType(HttpServletResponse response, int type) {
		addCookie(response, SCENE_TYPE, String.valueOf(type), null, SCENE_EXPIRED, "/");
	}

	/**
	 * 获取当前场景类型
	 * 
	 * @param request
	 * @return
	 */
	public static int getSceneType(HttpServletRequest request) {
		Cookie cookie = getCookie(request, SCENE_TYPE);
		if (null == cookie) {
			return 0;
		}
		try {
			String value = cookie.getValue();
			return Integer.parseInt(value);
		} catch (Exception e) {

		}
		return 0;
	}

	/** 取登录用户信息 **/
	public static Staff getLoginUser(HttpServletRequest request) {
		Cookie cookie = getCookie(request, LOGIN_USER);
		if (null == cookie) {
			return null;
		}
		String value = cookie.getValue();
		try {
			value = URLDecoder.decode(value, "UTF-8");
		} catch (Exception e) {
		}
		return decodeStaff(value);
	}

	/** 取验证码 **/
	public static String getCode(HttpServletRequest request) {
		Cookie cookie = getCookie(request, "CODE");
		if (null == cookie) {
			return null;
		}
		String value = cookie.getValue();
		return value;
	}

	private static String encodeStaff(Staff staff) {
		if (null == staff) {
			return null;
		}
		String value = MessageFormat.format("{0}-{1}-{2}-{3}-{4}-{5}-{6}-{7}-{8}",
				new Object[] { String.valueOf(staff.getId()), String.valueOf(staff.getCompanyId()),
						String.valueOf(staff.getRole()), staff.getName(), staff.getUsername(), staff.getPhone(),
						staff.getRoleType(), staff.getJob(), staff.getIsChief() });
		return DigestUtils.base64encode(StringUtils.reverse(value));
	}

	private static Staff decodeStaff(String value) {
		if (null == value) {
			return null;
		}
		value = StringUtils.reverse(DigestUtils.base64decode(value));
		String[] strArr = value.split("-");
		Staff staff = new Staff();
		try {
			staff.setId(Integer.parseInt(strArr[0]));
		} catch (Exception e) {
			return null;
		}
		try {
			staff.setCompanyId(Integer.parseInt(strArr[1]));
		} catch (Exception e) {
			return null;
		}
		try {
			staff.setRole(Integer.parseInt(strArr[2]));
		} catch (Exception e) {
			return null;
		}
		try {
			staff.setName(strArr[3]);
		} catch (Exception e) {
			return null;
		}
		try {
			staff.setUsername(strArr[4]);
		} catch (Exception e) {
			return null;
		}
		try {
			staff.setPhone(strArr[5]);
		} catch (Exception e) {
			return null;
		}
		try {
			staff.setRoleType(strArr[6]);
		} catch (Exception e) {
			return null;
		}
		try {
			staff.setJob(strArr[7]);
		} catch (Exception e) {
			return null;
		}
		try {
			staff.setIsChief(Boolean.parseBoolean(strArr[8]));
		} catch (Exception e) {
			return null;
		}
		return staff;
	}

	/**
	 * 设置用户信息
	 * 
	 * @param response
	 * @param value
	 */
	public static void setLoginUser(HttpServletResponse response, Staff staff) {
		addCookie(response, LOGIN_USER, encodeStaff(staff), null, LOGIN_EXPIRED, "/");
	}

	private static Cookie getCookie(HttpServletRequest request, String cookieName) {
		if (null == request) {
			return null;
		}
		Cookie[] cookies = request.getCookies();
		if (null == cookies) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookieName.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	private static Cookie parseCookie(String name, String value, String domain, int expire, String path) {
		if (null != value) {
			try {

				value = URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Cookie cookie = new Cookie(name, value);
		if (null != domain) {
			cookie.setDomain(domain);
		}
		cookie.setMaxAge(expire);
		cookie.setPath(path);
		return cookie;
	}

	public static void addCookie(HttpServletResponse response, String name, String value, String domain, int expire,
			String path) {
		Cookie cookie = parseCookie(name, value, domain, expire, path);
		logger.info("new cookie name {}, value {}, domain {}, expire {}, path {}",
				new Object[] { name, value, domain, expire, path });
		response.addCookie(cookie);
	}

}
