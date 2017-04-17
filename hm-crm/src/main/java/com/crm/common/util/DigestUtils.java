package com.crm.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class DigestUtils {

	private static MessageDigest digest = null;

	public synchronized static final String hash(String data) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest. ");
				nsae.printStackTrace();
				return null;
			}
		}
		// Now, compute hash.
		digest.update(data.getBytes());
		return encodeHex(digest.digest());
	}

	public static final String encodeHex(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	public static String base64encode(String value) {
		return new String(Base64.encodeBase64(value.getBytes()));
	}

	public static String base64decode(String value) {
		return new String(Base64.decodeBase64(value.getBytes()));
	}

	public static void main(String[] args) {
		String value = "zasdasdqe";
		String code = base64encode(StringUtils.reverse(value));
		String result = StringUtils.reverse(base64decode(code));
		System.out.println(value + " => " + code + " => " + result);
	}

}
