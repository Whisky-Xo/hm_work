package com.crm.api.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wiztools.commons.MultiValueMap;

import com.crm.common.util.StringUtil;


/**
 * 
 * @author schandran
 */
public final class Util {

	// private constructor so that no instance from outside can be created
	private Util() {
	}

	public static String getStackTrace(final Throwable aThrowable) {
		String errorMsg = aThrowable.getMessage();
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return errorMsg + "\n" + result.toString();
	}

	public static String getHTMLListFromList(List<String> ll) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><ul>");
		for (String str : ll) {
			sb.append("<li>").append(str).append("</li>");
		}
		sb.append("</ul></html>");
		return sb.toString();
	}

	private static final String ENCODE = "UTF-8";

	@SuppressWarnings("unused")
	public static String parameterEncode(MultiValueMap<String, String> params) {
		final StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			try {
				for (final String value : params.get(key)) {
					String encodedKey = URLEncoder.encode(key, ENCODE);
					String encodedValue = URLEncoder.encode(value, ENCODE);
					sb.append(encodedKey).append("=").append(encodedValue)
							.append("&");
				}
			} catch (UnsupportedEncodingException ex) {
				assert true : "Encoder UTF-8 supported in all Java platforms.";
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * Parses the HTTP response status line, and returns the status code.
	 * 
	 * @param statusLine
	 * @return The status code from HTTP response status line.
	 */
	public static int getStatusCodeFromStatusLine(final String statusLine) {
		int retVal = -1;
		final String STATUS_PATTERN = "[^\\s]+\\s([0-9]{3})\\s.*";
		Pattern p = Pattern.compile(STATUS_PATTERN);
		Matcher m = p.matcher(statusLine);
		if (m.matches()) {
			retVal = Integer.parseInt(m.group(1));
		}
		return retVal;
	}

	/**
	 * Method formats content-type and charset for use as HTTP header value
	 * 
	 * @param contentType
	 * @param charset
	 * @return The formatted content-type and charset.
	 */
	public static String getFormattedContentType(final String contentType,
			final String charset) {
		String charsetFormatted = StringUtil.isEmpty(charset) ? ""
				: "; charset=" + charset;
		return contentType + charsetFormatted;
	}

	public static String getCharsetFromContentType(final String contentType) {
		Pattern p = Pattern.compile("^.+charset=([^;]+).*$");
		Matcher m = p.matcher(contentType);
		if (m.matches()) {
			return m.group(1).trim();
		}
		return null;
	}

	/**
	 * Parses the Content-Type HTTP header and returns the MIME type part of the
	 * response. For example, when receiving Content-Type header like:
	 * 
	 * application/xml;charset=UTF-8
	 * 
	 * This method will return "application/xml".
	 * 
	 * @param contentType
	 * @return
	 */
	public static String getMimeFromContentType(final String contentType) {
		final int occurance = contentType.indexOf(';');
		if (occurance == -1) {
			return contentType;
		} else {
			return contentType.substring(0, occurance);
		}
	}
}