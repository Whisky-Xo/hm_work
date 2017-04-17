package com.crm.common.util;

/**
 * 手机号码鉴权工具类
 * 
 * @author JingChenglong 2016-09-10 13:58
 *
 */
public final class MobileUtil {
	/**
	 * 是否为中国移动
	 * 134(1349除外），135，136，137，138，139，147，150，151，152，157，158，159，182，183，184，
	 * 187，188
	 */
	public static boolean isChinaMobile(String mobile) {
		mobile = StringUtil.nullToStrTrim(mobile);
		if (!StringUtil.isHalfAngle(mobile)) {
			return false;
		}

		if (mobile.length() != 11) {
			return false;
		}

		String regex = "^1(3[4-9]|4[7]|5[012789]|8[23478])\\d{8}$";

		return mobile.matches(regex) && !isSpecialChinaTelecom(mobile);
	}

	/**
	 * 是否为中国联通 130、131、132、155、156、185、186、145
	 */
	public static boolean isChinaUnicom(String mobile) {
		mobile = StringUtil.nullToStrTrim(mobile);
		if (!StringUtil.isHalfAngle(mobile)) {
			return false;
		}

		if (mobile.length() != 11) {
			return false;
		}

		String regex = "^1(3[012]|4[5]|5[56]|8[56])\\d{8}$";

		return mobile.matches(regex);

	}

	/**
	 * 是否为中国电信 133、153、177、180、181、189
	 */
	public static boolean isChinaTelecom(String mobile) {
		mobile = StringUtil.nullToStrTrim(mobile);
		if (!StringUtil.isHalfAngle(mobile)) {
			return false;
		}

		if (mobile.length() != 11) {
			return false;
		}

		String regex = "^1(3[3]|5[3]|7[7]|8[019])\\d{8}$";

		return mobile.matches(regex) || isSpecialChinaTelecom(mobile);

	}

	/**
	 * 是否是特殊的电信号
	 */
	private static boolean isSpecialChinaTelecom(String mobile) {
		return mobile.startsWith("1349");
	}
}