package com.crm.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
//import org.bouncycastle.util.IPAddress;
import com.crm.common.util.MD5Utils;
import com.crm.api.constant.Constants;
import com.crm.exception.EduException;

/**
 * 字符串工具类
 * 
 * @author JingChenglong 2016-09-08 10:17
 *
 */
public class StringUtil {

	public static String substring(String value, int beginIndex) {
		return value.substring(beginIndex);
	}

	public static String substring(String value, int beginIndex, int endIndex) {
		return value.substring(beginIndex, endIndex);
	}

	public static String replaceAll(String value, String regex, String replacement) {
		return value.replaceAll(regex, replacement);
	}

	public static String join(List<String> list, String split) {
		StringBuffer sb = new StringBuffer();
		for (String string : list) {
			sb.append(string);
			sb.append(split);
		}
		int length = sb.length();
		if (length > split.length()) {
			sb.setLength(length - split.length());
		}
		return sb.toString();
	}

	public static final double MONEY_MIN = 0.01D;
	public static final double MONEY_MAX = 1000000000.00D;
	public static final String[] SIZEUNITS = new String[] { "BYTE", "KB", "MB", "GB", "TB", "PB" };

	private static Random rand = null;

	private static SecureRandom secureRand = null;

	private static String localhost = "";

	static {
		secureRand = new SecureRandom();
		rand = new Random(secureRand.nextLong());
		localhost = WebUtils.getLocalHost();
	}

	public static boolean isEmpty(String str) {

		return ((str == null) || (str.trim().length() == 0));
	}

	public static boolean isNotEmpty(String str) {

		return ((str != null) && (str.trim().length() > 0));
	}

	/*-- CRM系统专用，校验参数有效性 --*/
	public static boolean isValid(String str) {

		return ((str != null) && (str.trim().length() > 0) && !"0".equals(str));
	}

	public static boolean isBlank(String str) {

		int length = 0;

		if ((str == null) || ((length = str.length()) == 0)) {
			return true;
		}

		for (int i = 0; i < length; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	public static String nullToStr(String str) {

		if (str == null) {
			str = "";
		}

		return str;
	}

	public static String nullToStrBlank(String str) {

		if (isEmpty(str)) {
			str = "&nbsp;";
		}

		return str;
	}

	public static String nullToStrObj(Object obj) {
		if (obj == null) {
			obj = "";
		}
		return String.valueOf(obj);
	}

	public static String nullToStrTrim(String str) {

		if (str == null || "null".equals(str)) {
			str = "";
		}

		return str.trim();
	}

	public static int nullToIntZero(String str) {

		if (isEmpty(str)) {
			str = "0";
		}

		return Integer.valueOf(str.trim(), 10);
	}

	public static String nullToZeroStr(String str) {

		if (isEmpty(str)) {
			str = "0";
		}

		return str.trim();
	}

	public static double nullToDoubleZero(String str) {

		if (str == null || str.trim().length() == 0) {
			str = "0.0";
		}

		return Double.valueOf(str.trim());
	}

	public static long nullToLongZero(String str) {

		if (str == null || str.trim().length() == 0) {
			str = "0";
		}

		return Long.valueOf(str.trim(), 10);
	}

	public static boolean nullToBoolean(String str) {

		if (str == null || str.trim().length() == 0) {
			return false;
		}
		if ("1".equals(str)) {
			return true;
		} else if ("0".equals(str)) {
			return false;
		}

		return Boolean.valueOf(str.trim());
	}

	public static String nullToUnknown(String str) {

		if (isEmpty(str)) {
			str = Constants.UNKNOWN;
		}

		return str.trim();
	}

	public static String encodeHTML(String str) {

		if (str == null || str.length() == 0) {
			return "";
		}

		char content[] = new char[str.length()];
		str.getChars(0, str.length(), content, 0);
		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < content.length; i++) {
			switch (content[i]) {
			case '<':
				stringBuffer.append("&lt;");
				break;
			case '>':
				stringBuffer.append("&gt;");
				break;
			case '&':
				stringBuffer.append("&amp;");
				break;
			case '"':
				stringBuffer.append("&quot;");
				break;
			case '\'':
				stringBuffer.append("&#039;");
				break;
			default:
				stringBuffer.append(content[i]);
			}
		}

		return stringBuffer.toString();
	}

	public static String addQuote(String str) {

		str = "'" + encodeSingleQuotedString(str) + "'";

		return str;
	}

	public static String inQuote(String str) {

		String strs = "";
		for (String s : str.split(";")) {
			strs = strs + "," + StringUtil.addQuote(s);
		}
		if (strs.length() >= 1) {
			strs = strs.substring(1);
		}

		return strs;
	}

	public static void checkMaxWordLength(String str, int maxlength, int code) throws EduException {

		if (StringUtil.isNotEmpty(str)) {
			if (StringUtil.getWordLength(str) > maxlength) {
				throw new EduException(code);
			}
		}
	}

	public static String encodeSingleQuotedString(String str) {

		str = nullToStr(str);

		if (str.length() == 0) {
			return str;
		} else {
			StringBuffer stringBuffer = new StringBuffer(128);
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (c == '\'')
					stringBuffer.append("''");
				else
					stringBuffer.append(c);
			}
			return stringBuffer.toString();
		}
	}

	public static String encode(String str, String enc) {

		String strEncode = "";

		try {
			if (str != null)
				strEncode = URLEncoder.encode(str, enc);
		} catch (UnsupportedEncodingException e) {
		}

		return strEncode;
	}

	public static String decode(String str, String enc) {

		String strDecode = "";

		try {
			if (str != null)
				strDecode = URLDecoder.decode(str, enc);
		} catch (UnsupportedEncodingException e) {
		}

		return strDecode;
	}

	public static String encode(String str) {

		return encode(str, Constants.CHARSETNAME_DEFAULT);
	}

	public static String decode(String str) {

		return decode(str, Constants.CHARSETNAME_DEFAULT);
	}

	public static String convertCharset(String str, String charsetNameFrom, String charsetNameTo) {

		str = nullToStrTrim(str);

		if (isNotEmpty(str)) {
			try {
				str = new String(str.getBytes(charsetNameFrom), charsetNameTo);
			} catch (UnsupportedEncodingException e) {
			}
		}

		return str;
	}

	public static int getRealLength(String str) {

		return getRealLength(str, Constants.CHARSETNAME_DEFAULT);
	}

	public static int getRealLength(String str, String charsetName) {

		str = nullToStrTrim(str);

		if (isEmpty(str)) {
			return 0;
		}

		try {
			return str.getBytes(charsetName).length;
		} catch (UnsupportedEncodingException e) {
			return 0;
		}
	}

	public static String getUUID() {

		return UUID.randomUUID().toString();
	}

	public static int getRandom(int accuracy) {

		return (int) (Math.random() * accuracy);
	}

	public static String getRandom(String randstring, int length) {

		String rand = "";

		Random random = new Random();
		String c = "";
		for (int i = 1; i <= length; i++) {
			c = String.valueOf(randstring.charAt(random.nextInt(randstring.length())));
			rand += c;
		}

		return rand;
	}

	public static String getRawRandom() {

		String str = MD5Utils
				.md5(getUUID() + System.currentTimeMillis() + getRandom(999999999) + rand.nextLong() + localhost);
		str = str.toLowerCase();

		return str;
	}

	public static String getRandom() {

		String str = MD5Utils.md5(getUUID() + System.currentTimeMillis() + getRandom(999999999) + rand.nextLong());
		str = str.toLowerCase();

		return str;
	}

	public static String getRawRandom(String string) {

		String str = MD5Utils.md5(
				getUUID() + System.currentTimeMillis() + getRandom(999999999) + rand.nextLong() + localhost + string);
		str = str.toLowerCase();

		return str;
	}

	public static String getRandom(String string) {

		String str = MD5Utils.md5(
				getUUID() + System.currentTimeMillis() + getRandom(999999999) + rand.nextLong() + localhost + string);
		str = str.toLowerCase();

		return str;
	}

	public static boolean checkNumber(String number) {

		number = nullToStrTrim(number);

		if (isBlank(number) || number.split(".").length > 2 || !number.replace(".", "").matches("[0-9]+")) {
			return false;
		}

		try {
			Double.valueOf(number);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static String decimalFormat(String str, String pattern) {

		DecimalFormat decimalFormat = new DecimalFormat(pattern);

		return decimalFormat.format(Double.valueOf(nullToDoubleZero(str)));
	}

	public static String decimalFormat(Double num, String pattern) {

		DecimalFormat decimalFormat = new DecimalFormat(pattern);

		return decimalFormat.format(num);
	}

	public static int getWordLength(String str) {

		str = nullToStr(str);
		return str.replaceAll("[^\\x00-\\xff]", "**").length();
	}

	public static boolean isHalfAngle(String str) {

		str = nullToStrTrim(str);
		return str.length() == getWordLength(str);
	}

	public static String encodeBase64String(byte[] binaryData) {

		return Base64.encodeBase64String(binaryData).trim();
	}

	public static String encodeBase64String(String data) {

		return Base64.encodeBase64String(data.getBytes()).trim();
	}

	public static String encodeBase64String(String data, String charsetName) {

		try {
			return Base64.encodeBase64String(data.getBytes(charsetName)).trim();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static long convertSize(String strSize) {

		strSize = nullToStrTrim(strSize.toUpperCase());
		long tmp = 0L;
		if (!strSize.equalsIgnoreCase(Constants.UNKNOWN) && strSize.length() > 2) {
			tmp = nullToLongZero(strSize.substring(0, strSize.length() - 2));
		}

		long size = 0;
		if (strSize.endsWith("KB")) {
			size = Constants.BYTE_HEX * tmp;
		} else if (strSize.endsWith("MB")) {
			size = Constants.BYTE_HEX * Constants.BYTE_HEX * tmp;
		} else if (strSize.endsWith("GB")) {
			size = Constants.BYTE_HEX * Constants.BYTE_HEX * Constants.BYTE_HEX * tmp;
		}

		return size;
	}

	public static String convertSize(long size) {

		int i = 0;
		double tmpSize = size;
		if (tmpSize < 0L) {
			tmpSize = 0.0D;
		}

		while (i <= 5 && tmpSize >= Constants.BYTE_HEX) {

			i++;
			tmpSize = tmpSize / Constants.BYTE_HEX;
		}

		return decimalFormat(tmpSize, "#0.##") + SIZEUNITS[i];
	}

	public static double convertSizeToMB(long size) {

		if (size <= 0L) {
			return 0.0D;
		}

		return Double.valueOf(size) / Constants.MBYTE;
	}

	public static String convertSize(long size, int hex) {

		return convertSize(size * hex);
	}

	public static String convertKBSize(long size) {

		return convertSize(size * Constants.BYTE_HEX);
	}

	public static boolean checkEmail(String email) {

		email = nullToStrTrim(email);
		if (isEmpty(email)) {
			return false;
		}
		if (!isHalfAngle(email) || email.length() < 5 || email.length() > 60) {
			return false;
		}

		String regex = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";

		return email.matches(regex);
	}

	private static boolean checkSubphone(String subphone) {

		if (isEmpty(subphone) || !isHalfAngle(subphone) || isBlank(subphone) || subphone.length() > 5) {
			return false;
		}

		String regex = "[0-9]+";

		return subphone.matches(regex);
	}

	public static boolean isPhone(String phone) {

		return phone.startsWith("0");
	}

	public static boolean isMobile(String mobile) {

		return mobile.startsWith("1");
	}

	public static boolean checkPhone(String phone) {

		if (isEmpty(phone) || !isHalfAngle(phone) || isBlank(phone)) {
			return false;
		}

		if (phone.length() != 11 && phone.length() != 12) {
			return false;
		}

		String regex = "0([1-9])[0-9]+";

		return phone.matches(regex);
	}

	public static boolean checkPhoneNew(String phone) {

		phone = nullToStrTrim(phone);
		if (!isHalfAngle(phone) || isBlank(phone)) {
			return false;
		}

		if (phone.length() > 20) {
			return false;
		}

		if (phone.indexOf("-") < 0) {
			return checkPhone(phone);
		}

		return checkPhone(phone.substring(0, phone.indexOf("-")))
				&& checkSubphone(phone.substring(phone.indexOf("-") + 1));
	}

	public static boolean checkPhone86(String phone) {

		phone = nullToStrTrim(phone);
		if (!isHalfAngle(phone)) {
			return false;
		}

		if (phone.length() != 13 && phone.length() != 14) {
			return false;
		}

		String regex = "860([1-9])[0-9]+";

		return phone.matches(regex);
	}

	public static boolean checkMobile(String mobile) {

		mobile = nullToStrTrim(mobile);
		if (!isHalfAngle(mobile)) {
			return false;
		}

		if (mobile.length() != 11) {
			return false;
		}

		String regex = "^(1([3,4,5,8,7][0-9]))\\d{8}$";

		return mobile.matches(regex);
	}

	public static boolean checkMobileNew(String mobile) {

		mobile = nullToStrTrim(mobile);
		if (!isHalfAngle(mobile) || isBlank(mobile)) {
			return false;
		}

		if (mobile.length() > 20) {
			return false;
		}

		if (mobile.indexOf("-") < 0) {
			return checkMobile(mobile);
		}

		return checkMobile(mobile.substring(0, mobile.indexOf("-")))
				&& checkSubphone(mobile.substring(mobile.indexOf("-") + 1));
	}

	public static boolean checkMobile86(String mobile) {

		mobile = nullToStrTrim(mobile);
		if (!isHalfAngle(mobile)) {
			return false;
		}

		if (mobile.length() != 13) {
			return false;
		}

		String regex = "^(861([3,4,5,8][0-9]))\\d{8}$";

		return mobile.matches(regex);
	}

	public static boolean check00(String phone) {

		phone = nullToStrTrim(phone);
		if (!isHalfAngle(phone)) {
			return false;
		}

		if (phone.length() != 10) {
			return false;
		}

		String regex = "[400,800][0-9]+";

		return phone.matches(regex);
	}

	public static boolean checkIdcardno(String idcardno) {

		idcardno = nullToStrTrim(idcardno);
		if (!isHalfAngle(idcardno)) {
			return false;
		}

		if (idcardno.length() != 15 && idcardno.length() != 18) {
			return false;
		}

		return checkString(idcardno, "[1-9][0-9][0-9xX]+");
	}

	public static boolean checkPostcode(String postcode) {

		postcode = nullToStrTrim(postcode);
		if (!isHalfAngle(postcode)) {
			return false;
		}

		if (postcode.length() != 6) {
			return false;
		}

		return checkString(postcode, "[1-9][0-9]+");
	}

	// public static boolean checkIp(String ip) {
	//
	// if (isEmpty(ip) || isBlank(ip) || !isHalfAngle(ip)) {
	// return false;
	// }
	// if (ip.length() < 7) {
	// return false;
	// }
	//
	// return IPAddress.isValidIPv4(ip) || IPAddress.isValidIPv6(ip);
	// }

	public static boolean checkMac(String mac) {

		mac = mac.trim();
		if (isEmpty(mac) || isBlank(mac) || !isHalfAngle(mac)) {
			return false;
		}
		if (mac.length() != 12 && mac.length() != 17) {
			return false;
		}

		return checkString(mac, "([0-9A-Fa-f]{2}){6}") || checkString(mac, "([0-9A-Fa-f]{2}-){5}[0-9A-Fa-f]{2}")
				|| checkString(mac, "([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}");
	}

	public static boolean checkString(String str, String regex) {

		return str.matches(regex);
	}

	public static String leftPad(String str, int length, String padchar) {

		return String.format("%" + length + "s", str).replaceAll(" ", padchar);
	}

	public static int getRandom(int min, int max) {

		if (min > max) {
			int temp = min;
			min = max;
			max = temp;
		}
		Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}

	public static boolean checkMoney(String money) throws EduException {

		money = nullToStrTrim(money);

		if (money.length() > 10 || isBlank(money) || money.split(".").length > 2
				|| !checkString(money.replace(".", ""), "[0-9]+")) {
			return false;
		}

		if (money.indexOf(".") > 0) {
			String decimal = money.substring(money.indexOf(".") + 1);
			if (decimal.length() >= 3) {
				if (decimal.substring(2).replaceAll("0", "").length() > 0) {
					return false;
				}
			}
		}

		double moneyD = 0.0D;
		try {
			moneyD = Double.valueOf(money);
		} catch (NumberFormatException e) {
			return false;
		}

		if (moneyD != 0.0D && (moneyD < MONEY_MIN || moneyD > MONEY_MAX)) {
			return false;
		}

		return true;
	}

	public static boolean checkDigit(String digit) {

		digit = nullToStrTrim(digit);

		if (isEmpty(digit) || isBlank(digit) || !digit.matches("[0-9]+")) {
			return false;
		}

		return true;
	}

	public static boolean checkDigit(String digit, int maxlength, long min, long max) {

		digit = nullToStrTrim(digit);
		if (digit.length() > maxlength || !checkString(digit, "[0-9]+")) {
			return false;
		}

		if (nullToLongZero(digit) < min || nullToLongZero(digit) > max) {
			return false;
		}

		return true;
	}

	public static boolean checkTime(String time) {

		if (time.length() != 14) {
			return false;
		}

		if (!checkString(time, "[2][0-9]+")) {
			return false;
		}

		return TimeUtils.validTime(time, "yyyyMMddHHmmss");
	}

	public static boolean checkDate(String date) {

		if (date.length() != 8) {
			return false;
		}

		if (!checkString(date, "[2][0-9]+")) {
			return false;
		}

		return TimeUtils.validTime(date, "yyyyMMdd");
	}

	public static boolean checkMonth(String month) {

		if (month.length() != 6) {
			return false;
		}

		if (!checkString(month, "[2][0-9]+")) {
			return false;
		}

		return TimeUtils.validTime(month, "yyyyMM");
	}

	public static boolean isBeforeCurrentDate(String date) {

		return (nullToLongZero(date) < nullToLongZero(TimeUtils.getSysdateInt()));
	}

	public static boolean isAfterCurrentTime(String time) {

		return (nullToLongZero(time) > nullToLongZero(TimeUtils.getSysTimeLong()));
	}

	public static boolean isAfterCurrentTime(String time, long delay) {

		return (nullToLongZero(time) > nullToLongZero(TimeUtils.getSysTimeLong()) + delay);
	}

	public static boolean isAfterCurrentDate(String date) {

		return (nullToLongZero(date) > nullToLongZero(TimeUtils.getSysdateInt()));
	}

	public static boolean isAfterCurrentMonth(String month) {

		return (nullToLongZero(month) > nullToLongZero(TimeUtils.getSysyearmonthInt()));
	}

	public static boolean isorAfterCurrentMonth(String month) {

		return (nullToLongZero(month) >= nullToLongZero(TimeUtils.getSysyearmonthInt()));
	}

	public static boolean checkMd5(String md5) {

		if (md5.length() != 32) {
			return false;
		}

		return checkString(md5, "[0-9A-Fa-f]+");
	}

	public static boolean checkCoordinates(String str) {

		if (str.length() > 50 || !isHalfAngle(str)) {
			return false;
		}

		String regex = "[0-9]+_[0-9]+-[0-9]+_[0-9]+";

		return str.matches(regex);
	}

	public static boolean checkConfig(String config, String checkData) {

		return checkData.indexOf(";") == -1 && (";" + config + ";").indexOf(";" + checkData + ";") >= 0;
	}

	public static boolean checkRegioncode(String code) {

		return code.length() == 6 && checkString(code, "[1-9][0-9]+");
	}

	public static Map<String, String> splitUrlQuery(String query, boolean decodeflag) {
		Map<String, String> result = new HashMap<String, String>();

		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] params = pair.split("=", 2);
				if (params != null && params.length == 2) {
					result.put(params[0], decodeflag ? decode(params[1]) : params[1]);
				}
			}
		}

		return result;
	}

	public static String byte2hex(byte[] b) {

		String str = "";
		String stmp = "";

		int length = b.length;

		for (int n = 0; n < length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				str += "0";
			}
			str += stmp;
		}

		return str.toLowerCase();
	}

	public static Map<String, Object> putMap(Map<String, Object> map, String key, Object value) {

		if (map == null) {
			map = new LinkedHashMap<String, Object>();
		}
		if (!map.containsKey(key)) {
			map.put(key, value);
		}

		return map;
	}

	public static Map<String, Object> putMaps(Map<String, Object> map, String keys, Object value) {

		if (map == null) {
			map = new LinkedHashMap<String, Object>();
		}
		for (String key : keys.split(";")) {
			if (!map.containsKey(key)) {
				map.put(key, value);
			}
		}

		return map;
	}

	public static Map<String, Object> putMaps(Map<String, Object> map, String key, String subkeys, Object value) {

		if (map == null) {
			map = new LinkedHashMap<String, Object>();
		}
		for (String subkey : subkeys.split(";")) {
			if (!map.containsKey(key + subkey)) {
				map.put(key + subkey, value);
			}
		}

		return map;
	}

	/*
	 * 判断是否为整数
	 * 
	 * @param str 传入的字符串
	 * 
	 * @return 是整数返回true,否则返回false
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	public static int getPagesize(String pagesize) {

		return getPagesize(pagesize, Constants.MAX_PAGESIZE);
	}

	public static int getPagesize(String pagesize, int maxpagesize) {

		pagesize = StringUtil.nullToStrTrim(pagesize);
		if (StringUtil.isEmpty(pagesize) || !StringUtil.isHalfAngle(pagesize) || pagesize.length() > 4
				|| !StringUtil.checkDigit(pagesize)) {
			return Constants.DEFAULT_PAGESIZE;
		}

		int pagesizeI = StringUtil.nullToIntZero(pagesize);
		if (pagesizeI == 0) {
			return Constants.DEFAULT_PAGESIZE;
		} else if (pagesizeI > maxpagesize) {
			return maxpagesize;
		}

		return pagesizeI;
	}

	public static long getCurrentpage(String currentpage) {

		currentpage = StringUtil.nullToStrTrim(currentpage);
		if (StringUtil.isEmpty(currentpage) || !StringUtil.isHalfAngle(currentpage) || currentpage.length() > 8
				|| !StringUtil.checkDigit(currentpage)) {

			return Constants.DEFAULT_CURRENTPAGE;
		}

		// int currentpageI = StringUtil.nullToIntZero(currentpage);
		// if(currentpageI == 0) {
		// return Constants.DEFAULT_CURRENTPAGE;
		// } else if(currentpageI > Constants.MAX_PAGE) {
		// return Constants.MAX_PAGE;
		// }
		//
		// return currentpageI;

		long currentpageL = StringUtil.nullToIntZero(currentpage);
		if (currentpageL == 0L) {
			return Constants.DEFAULT_CURRENTPAGE;
		} else if (currentpageL > Constants.MAX_PAGE) {
			return Constants.MAX_PAGE;
		}

		return currentpageL;
	}

	public static boolean checkUseridPattern(String userid) {

		if (userid.indexOf("__") >= 0 || userid.endsWith("_")) {
			return false;
		}

		return StringUtil.checkString(userid, "[A-Za-z][0-9A-Za-z_]+");
	}

	public static String getRightValue(String value, String defaultValue) {
		return isBlank(value) ? nullToStrTrim(defaultValue) : nullToStrTrim(value);
	}
}
