package com.crm.common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5Utils {

	private static final int BUFFERSIZE = 8196;
	private static final String ALGORITHM = "MD5";
	private static final String CHARSET = "UTF-8";

	public MD5Utils() {
	}

	public static String md5(String input) {
		return md5(input, CHARSET);
	}

	private static String md5(String input, String charsetName) {
		try {
			MessageDigest md5 = MessageDigest.getInstance(ALGORITHM);
			byte md5Bytes[] = md5.digest(input.getBytes(charsetName));
			return StringUtil.byte2hex(md5Bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}

	public static String md5reverse(String md5) {
		return "";
	}

	public static String md5(InputStream inputStream) throws Exception {

		BufferedInputStream bufferedInputStream = null;
		MessageDigest md = null;

		try {
			byte[] buffer = new byte[BUFFERSIZE];
			int i = 0;

			bufferedInputStream = new BufferedInputStream(inputStream, BUFFERSIZE);
			md = MessageDigest.getInstance(ALGORITHM);

			while ((i = bufferedInputStream.read(buffer)) != -1) { // >0
				md.update(buffer, 0, i);
			}

			buffer = null;

			byte[] md5Bytes = md.digest();
			return StringUtil.byte2hex(md5Bytes);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
				}
				bufferedInputStream = null;
			}
		}
	}

	/**
	 * csj 2014-06-18 备案规则
	 */
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
			"z" };

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		System.out.println(" b.length:" + b.length);
		for (int i = 0; i < b.length; i++) {
			System.out.println("i:" + i + "----byteToHexString(b[i]):" + byteToHexString(b[i]));
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	public static String encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			System.out.println("resultString.getBytes():" + resultString.getBytes());
			System.out.println("md.digest(resultString.getBytes()):" + md.digest(resultString.getBytes()));
			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 36;
		int d2 = n % 36;
		int d = d1 + d2;
		if (d >= 36) {
			d = d % 36;
		}
		System.out.println("d:" + d);
		// return hexDigits[d1] + hexDigits[d2];
		return hexDigits[d];
	}

	public static String md5file(String filename) {
		BufferedInputStream bufferedInputStream = null;
		MessageDigest md;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(filename), BUFFERSIZE);
			md = MessageDigest.getInstance(ALGORITHM);
			byte[] buffer = new byte[BUFFERSIZE];
			int i = 0;
			while ((i = bufferedInputStream.read(buffer)) != -1) {
				md.update(buffer, 0, i);
			}
			return StringUtil.byte2hex(md.digest());
		} catch (Exception e) {
			return null;
		} finally {
			closeBufferedInputStream(bufferedInputStream);
		}
	}

	public static void closeBufferedInputStream(BufferedInputStream bufferedInputStream) {

		if (bufferedInputStream != null) {
			try {
				bufferedInputStream.close();
			} catch (IOException e) {
			}
			bufferedInputStream = null;
		}
	}
}