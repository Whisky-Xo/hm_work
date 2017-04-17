package com.crm.common.util.ding;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.AuthHelper;
import com.crm.common.util.HttpHelper;
import com.crm.common.util.StringUtil;
import com.crm.model.Company;
import com.crm.model.Staff;

/**
 * 钉钉消息工具类
 */
public class DingUtil {

	// 注意多个receiver格式必须为 id1|id2|id3 agentid需要企业进入钉钉后台查看应用后获得 content自行填写
	public static void sendCompanyMsg(String receivers, Company company, String agentid, String content)
			throws OApiException {
		String accesstoken = AuthHelper.getAccessToken2(company.getCorpId(), company.getCorpSecret());
		String preurl = "https://oapi.dingtalk.com/message/send?access_token=" + accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject text = new JSONObject();
		text.put("content", content);
		msg.put("touser", receivers);
		msg.put("agentid", agentid);
		msg.put("msgtype", "text");
		msg.put("text", text);
		JSONObject response = HttpHelper.httpPost(preurl, msg);
		System.out.println(response);
	}

	/** -- 1.获取钉钉头像 -- **/
	public static Staff getDingHeadImg(Staff staff) {

		if (staff == null || StringUtil.isEmpty(staff.getCompCorpId()) || StringUtil.isEmpty(staff.getCompCorpSecret())
				|| StringUtil.isEmpty(staff.getDingUserId())) {
			return staff;
		}

		try {
			String accesstoken = AuthHelper.getAccessToken2(staff.getCompCorpId(), staff.getCompCorpSecret());
			String preurl = "https://oapi.dingtalk.com/user/get?access_token=" + accesstoken + "&userid="
					+ staff.getDingUserId();
			JSONObject response = HttpHelper.httpGet(preurl);
			if (response != null && StringUtil.isNotEmpty(response.getString("avatar"))) {
				staff.setHeadImg(response.getString("avatar"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return staff;
	}

}
