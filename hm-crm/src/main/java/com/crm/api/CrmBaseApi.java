package com.crm.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.crm.api.constant.Constants;
import com.crm.api.constant.ServiceConstants;
import com.crm.common.util.HmacSHA1Utils;
import com.crm.common.util.MD5Utils;
import com.crm.exception.EduException;

/**
 * CRM接口调用
 * 
 * @author JingChenglong 2016-09-08 11:13
 *
 */
@Controller
@RequestMapping("/crmba")
public class CrmBaseApi extends BaseApi {

	private static String url;// 接口调用地址
	private static String accessid = "819974ff209236630597d53a593e1767";// 通行证编码
	private static String key = "NzRhNDhkZGExNjFkZDBiNTRkMjI3Mjg4ODc1NDU2MzU=";// 签名

	private String purl;

	@Value("${URL.API}")
	public void setPurl(String purl) {
		 this.url = "http://114.55.34.116:8888/hm_crm/http/HttpService";
		 System.out.println(purl);
//		this.url = purl;
	}

	@RequestMapping("test")
	public String test(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(url);
		return null;
	}

	// 签名类型(1 md5签名 ,2 hmacsh1 签名)
	private String signtype = "2";

	public String doService(Map<String, Object> reqcontent, String action) throws EduException {
		initData(reqcontent);
		return doService(reqcontent, action, url);
	}

	public void doServiceUpLoad(Map<String, Object> reqcontent, String action, String filepath) throws EduException {
		initData(reqcontent);
		doService(reqcontent, action, url, filepath, key);
	}

	private void initData(Map<String, Object> reqcontent) {
		reqcontent.put("requestType", "crm");
		reqcontent.put("accessid", accessid);
	}

	protected String getSign(String reqcontentStr) throws Exception {
		String sign = "";
		if (signtype.equals(ServiceConstants.SERVICE_SIGNTYPE_HMACSH1)) {
			sign = HmacSHA1Utils
					.signatureString(MD5Utils.md5(reqcontentStr).toLowerCase(), key, Constants.ENCODING_UTF8).trim();
		} else {
			sign = MD5Utils.md5(reqcontentStr).toLowerCase();
		}
		return sign;
	}
}