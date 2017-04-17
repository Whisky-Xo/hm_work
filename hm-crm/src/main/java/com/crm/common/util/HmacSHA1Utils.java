package com.crm.common.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * HmacSHA1签名
 * 
 * @author JingChenglong 2016-09-08 10:21
 *
 */
public class HmacSHA1Utils {

	private static final String ALGORITHM = "HmacSHA1";

	public static byte[] signature(String data, String key, String charsetName)
			throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {

		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(charsetName), ALGORITHM);
		Mac mac = Mac.getInstance(ALGORITHM);
		mac.init(signingKey);

		return mac.doFinal(data.getBytes(charsetName));
	}

	public static String signatureString(String data, String key, String charsetName)
			throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {

		return StringUtil.nullToStrTrim(Base64.encodeBase64String(signature(data, key, charsetName)));
	}
}