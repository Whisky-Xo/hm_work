package com.crm.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class DingtalkRequest {
	//获取AccessToken
	private static final String url = "https://oapi.dingtalk.com/gettoken";
	//获取企业人数
	private static final String get_org_user_count_url =  "https://oapi.dingtalk.com/user/get_org_user_count";
	//获取微应用后台管理免登SsoToken
	private static final String SsoToken_url = "https://oapi.dingtalk.com/sso/gettoken";
	//发送企业会话消息
	private static final String agent_send_url = "https://oapi.dingtalk.com/message/send";
	/**
	 * 发送请求到DingTalk获取AccessToken
	 * @param url
	 * @return 返回数据
	 */
	public static String sendGetAccessToken(String corpid,String corpsecret) {
		StringBuffer stringBuffer = new StringBuffer("");
		if(StringUtils.isNotEmpty(corpid)&&StringUtils.isNotEmpty(corpsecret)){
			try {
				URL getUrl = new URL(url+"?corpid="+corpid+"&corpsecret="+corpsecret);
				HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(true);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line);
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 发送请求到DingTalk获取org_user_Count
	 * @param ACCESS_TOKEN 调用接口凭证
	 * @param onlyActive 0:非激活人员数量，1:已经激活人员数量
	 * 
	 */
	public static String get_org_user_Count(String ACCESS_TOKEN,String onlyActive) {
		StringBuffer stringBuffer = new StringBuffer("");
		if(StringUtils.isNotEmpty(ACCESS_TOKEN)&&StringUtils.isNotEmpty(onlyActive)){
			try {
				URL getUrl = new URL(get_org_user_count_url+"?access_token="+ACCESS_TOKEN+"&onlyActive="+onlyActive);
				HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(true);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line);
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}
	
	/**
	 * 发送请求到DingTalk获取微应用后台管理免登SsoToken
	 * 
	 * 	 
	 */	public static String sendGetSsoToken(String corpid,String ssosecret) {
		StringBuffer stringBuffer = new StringBuffer("");
		if(StringUtils.isNotEmpty(corpid)&&StringUtils.isNotEmpty(ssosecret)){
			try {
				URL getUrl = new URL(SsoToken_url+"?corpid="+corpid+"&corpsecret="+ssosecret);
				HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(true);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line);
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 发送企业会话消息
	 *
	 * 
	 */
  	public static String sendAgentMessage(String ACCESS_TOKEN){
		StringBuffer stringBuffer = new StringBuffer("");
		if(StringUtils.isNotEmpty(ACCESS_TOKEN)){
			try {
				URL getUrl = new URL(agent_send_url+"?access_token="+ACCESS_TOKEN);
				HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
				JSONObject obj = new JSONObject();
				JSONObject content = new JSONObject();
				content.put("content", "张三");
				obj.put("touser", "042207171837701130");
				obj.put("agentid", "4093094");
				obj.put("msgtype", "text");
				obj.put("text", content);
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestMethod("POST");
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(true);
				connection.setRequestProperty("Content-Type", "application/json");
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
				writer.write(obj.toJSONString());
				System.out.println(obj.toJSONString());
				writer.flush();
				writer.close();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line);
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
		
  	}
	
	public static void main(String[] args) {
		String getDingTalkRequest = sendGetAccessToken("ding06a94ba0944a0164","Ft6qrtMll1VUYtDpuRGg37VDYZ289G-NNC38XIVRGsFGehRpj_-QH3yhoE8XY2QF");
		JSONObject access_tokenJSON = JSON.parseObject(getDingTalkRequest);
		String access_token = access_tokenJSON.getString("access_token");
		System.out.println("AccessToken:"+access_token);
		System.out.println("公司人数:"+get_org_user_Count(access_token,"1"));
		String sendGetSsoToken = sendGetSsoToken("ding06a94ba0944a0164","sCeZDXr5g084FDU423wBGxWsI_pdPtcMVGedeCgnTICJ0D6Y9pg3T0u677QXEqaC");
		JSONObject access_tokenJSON2 = JSON.parseObject(sendGetSsoToken);
		System.out.println("免登SsoToken:"+access_tokenJSON2.getString("access_token"));
		System.out.println(sendAgentMessage(access_token));
	}
}
