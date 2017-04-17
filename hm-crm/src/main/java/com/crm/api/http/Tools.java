package com.crm.api.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import org.wiztools.commons.StringUtil;

public class Tools {

	public static KeyStore getTrustStore(String trustStorePath,
			char[] trustStorePassword) throws Exception {

		KeyStore trustStore = null;
		FileInputStream instream = null;

		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			if (!StringUtil.isStrEmpty(trustStorePath)) {
				instream = new FileInputStream(new File(trustStorePath));
				trustStore.load(instream, trustStorePassword);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if(instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
				}
				instream = null;
			}
		}

		return trustStore;
	}

}
