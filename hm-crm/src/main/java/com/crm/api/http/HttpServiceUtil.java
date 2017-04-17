package com.crm.api.http;

import java.net.MalformedURLException;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpException;

import com.crm.common.util.StringUtil;
import com.crm.exception.EduException;

public class HttpServiceUtil {

	private static final String CHARSET_ENCODING = "UTF-8";
	private static final String HTTPMETHOD_POST = "POST";

	public static String doService(String url, String format, String sign, String reqcontent) throws EduException {

		HttpClient httpClient = null;
		String rstStr = "";
		try {
			httpClient = getClient(url, format);

			reqcontent = StringUtil.nullToStrTrim(reqcontent);

			if (StringUtil.isNotEmpty(reqcontent)) {
				httpClient.addHeader("reqlength", String.valueOf(StringUtil.getRealLength(reqcontent)));

				if (StringUtil.isNotEmpty(sign)) {
					httpClient.addHeader("sign", StringUtil.encode(sign));
				}
				httpClient.setEntity(reqcontent, CHARSET_ENCODING);
			}
			httpClient.execute();

			boolean isfile = false;
			String fileName = "";
			Header[] responseHeaders = httpClient.getAllHeaders();
			for (Header header : responseHeaders) {
				System.out.println(header.getName() + "  " + StringUtil.decode(header.getValue()));
				if ((StringUtil.decode(header.getValue())).indexOf("attachment;") != -1) {
					String tp = StringUtil.decode(header.getValue());
					fileName = tp.substring(tp.indexOf("filename") + 9, tp.length());
					fileName = fileName.replaceAll("\"", "");
					isfile = true;
				}
			}
			if (isfile) {
				httpClient.getContentFile("E:\\down_" + fileName);
			} else {
				rstStr = httpClient.getContentStr();
			}
		} catch (Exception e) {
			httpClient.abortExecution();
			throw new EduException(e.getMessage());
		} finally {
			httpClient.shutdown();
		}
		return rstStr;
	}

	public static Map<String, Object> doUpService(String url, String format, Map<String, Object> reqcontent,
			String filepath) throws EduException {
		System.out.println(reqcontent);
		HttpClient httpClient = null;
		Map<String, Object> result = null;

		try {
			httpClient = getClient(url, format);

			if (reqcontent != null && !reqcontent.isEmpty()) {
				httpClient.addHeaderObj(reqcontent);
			}

			httpClient.setEntity(filepath);
			httpClient.execute();

			/*
			 * Header[] responseHeaders = httpClient.getAllHeaders(); for
			 * (Header header : responseHeaders) {
			 * System.out.println(header.getName() + "=" +
			 * StringUtil.decode(header.getValue())); }
			 */
			System.out.println(httpClient.getContentStr());
			// result = Xml2MapUtil.convert(httpClient.getContentStr());
		} catch (Exception e) {
			httpClient.abortExecution();
			throw new EduException(e.getMessage());
		} finally {
			httpClient.shutdown();
		}
		return result;
	}

	public static HttpClient getClient(String url, String format) throws MalformedURLException, HttpException {
		HttpClient httpClient = new HttpClientImpl();

		String httpVersion = "1.1";
		String httpMethod = HTTPMETHOD_POST;
		int requestTimeoutInMillis = 60000;
		httpClient.setUrl(url);

		httpClient.setHttpVersion(httpVersion);
		httpClient.setHttpContentCharset(CHARSET_ENCODING);
		httpClient.setRequestTimeoutInMillis(requestTimeoutInMillis);

		httpClient.setHttpMethod(httpMethod);

		if (StringUtil.isNotEmpty(format)) {
			httpClient.addHeader("format", StringUtil.encode(format));
		}
		return httpClient;
	}

}