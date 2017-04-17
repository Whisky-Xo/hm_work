package com.crm.test;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.util.AuthHelper;
import com.crm.common.util.HttpHelper;
import com.crm.common.util.ding.FileUtils;
import com.crm.common.util.ding.OApiException;
import com.crm.common.util.ding.OApiResultException;

import io.goeasy.GoEasy;
import io.goeasy.publish.GoEasyError;
import io.goeasy.publish.PublishListener;

public class PushTest {
	
	
	public static void goEasyPush () throws OApiException {
		GoEasy goEasy = new GoEasy("f8c84801-0ac1-4cbf-98e3-3edd85d78b09");
		goEasy.publish("crm_bottom","努比亚手机---z11",new PublishListener(){
			@Override
			public void onSuccess(){
				System.out.print("消息发布成功。");
			}
			
			@Override
			public void onFailed(GoEasyError error){
				System.out.print("消息发布失败, 错误编码："+error.getCode()+"错误信息:"+error.getContent());
			}
			
		});
		
		goEasy.publish("crm_top","sing i love you lord",new PublishListener(){
			@Override
			public void onSuccess(){
				System.out.print("消息发布成功。");
			}
			
			@Override
			public void onFailed(GoEasyError error){
				System.out.print("消息发布失败, 错误编码："+error.getCode()+"错误信息:"+error.getContent());
			}
			
		});
	}
	
	
	
	public static String getAccessToken2() throws OApiException {
		String url = "https://oapi.dingtalk.com/gettoken?corpid=" + "ding26a068994a748b63" + "&corpsecret=" + "L2R48Q2eMvwBp_8aRzOWHuzIlIkI1Brf8QKywinAAFmqchfoCHRcYugqUj3noCSY";
		JSONObject response = HttpHelper.httpGet(url);
		String ssoToken;
		if (response.containsKey("access_token")) {
			ssoToken = response.getString("access_token");
		} else {
			throw new OApiResultException("Sso_token");
		}
		return ssoToken;
	}
	
	
	
	
	
	
	public static void sendDingMsg () throws OApiException {
		String accesstoken = getAccessToken2();
		String preurl ="https://oapi.dingtalk.com/message/send?access_token="+accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject link = new JSONObject();
		String dingMsg =  "姓名：" + "刘德华" +  "\n电话：" + "1320005623" + "\n渠道：" + "支付宝口碑"+"\n时间";
		link.put("messageUrl","http://1l59s31524.iask.in:14603/hm-crm/outer/do_kz_make_order_ding?kzid=f2d1c77ead1423433707aee62d0710e0&staffid=1");
		link.put("picUrl","http://oss-cn-hangzhou.aliyuncs.com/hmcrm/hm_dingding_jiedan_ic.png");
		link.put("title","新客资来了");
		link.put("text",dingMsg);
		msg.put("touser", "sunquan112");
		msg.put("agentid", "45508736");
		msg.put("msgtype", "link");
		msg.put("link", link);
		JSONObject response = HttpHelper.httpPost(preurl, msg);
		System.out.println(response);
	}
	
	//195.168.14.156/hm-crm/client/do_kz_make_order_ding?kzid=f2d1c77ead1423433707aee62d0710e0
	
	public static void sendDingOaMsg () throws Exception {

		String accesstoken = getAccessToken2();
		String preurl ="https://oapi.dingtalk.com/message/send?access_token="+accesstoken;
		JSONObject msg = new JSONObject();
		JSONObject oa = new JSONObject();
		
		JSONObject head = new JSONObject();
		head.put("bgcolor", "FFBBBBBB");
		head.put("text", "头部标题");
		
		JSONObject body = new JSONObject();
		JSONObject form = new JSONObject();
		form.put("姓名", "123");
		form.put("年龄", "12345");
		body.put("form",form);
		
		oa.put("body", body);
		oa.put("head", head);
		oa.put("message_url","http://www.baidu.com");
		

		msg.put("touser", "sunquan112");
		msg.put("agentid", "");
		msg.put("msgtype", "oa");
		msg.put("oa", oa);
		
		JSONObject response = HttpHelper.httpPost(preurl, msg);
		System.out.println(response);
	}
	
	
	
	public static void getDepartmentList() throws OApiException {
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String preurl ="https://oapi.dingtalk.com/department/list?access_token="+accesstoken;
		JSONObject response = HttpHelper.httpGet(preurl);
		JSONArray array = response.getJSONArray("department");
		for (int i = 0; i < array.size(); i++) {
			try {
				JSONObject dept=(JSONObject) array.get(i);
				String ss=String.valueOf(dept.get("id")) ;
				getDeptStaffList(ss);
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	
	public static void getStaffList() throws OApiException {
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String preurl ="https://oapi.dingtalk.com/user/simplelist?access_token="+accesstoken+"&department_id=12886371";
		JSONObject response = HttpHelper.httpGet(preurl);
		System.out.println(response);
	}
	
	public static void getInfoList() throws OApiException {
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String preurl ="https://oapi.dingtalk.com/user/simplelist?access_token="+accesstoken+"&department_id=12886371";
		JSONObject response = HttpHelper.httpGet(preurl);
		System.out.println(response);
	}
	
		
	public static void getAgentId(String corpId, String appId) throws OApiException {
		String agentId = "";
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String url = "https://oapi.dingtalk.com/service/get_auth_info?suite_access_token=" + accesstoken;
		JSONObject args = new JSONObject();
		args.put("suite_key", "suite6z1umg1ghjtumxzk");
		args.put("auth_corpid", corpId);
		args.put("permanent_code", FileUtils.getValue("permanentcode", corpId));
		JSONObject response = HttpHelper.httpPost(url, args);
		System.out.println(response);
		if (response.containsKey("auth_info")) {
			JSONArray agents = (JSONArray) ((JSONObject) response.get("auth_info")).get("agent");

			for (int i = 0; i < agents.size(); i++) {

				if (((JSONObject) agents.get(i)).get("appid").toString().equals(appId)) {
					agentId = ((JSONObject) agents.get(i)).get("agentid").toString();
					break;
				}
			}
		} else {
			throw new OApiResultException("agentid");
		}

	}
	
	
	public static void getApps(String corpId, String appId) throws OApiException {
		String agentId = null;
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String url = "https://oapi.dingtalk.com/service/get_auth_info?suite_access_token=" + accesstoken;
		JSONObject args = new JSONObject();
		args.put("suite_key", "suite6z1umg1ghjtumxzk");
		args.put("auth_corpid", corpId);
//		args.put("permanent_code", FileUtils.getValue("permanentcode", corpId));
		JSONObject response = HttpHelper.httpPost(url, args);

		if (response.containsKey("auth_info")) {
			JSONArray agents = (JSONArray) ((JSONObject) response.get("auth_info")).get("agent");

			for (int i = 0; i < agents.size(); i++) {

				if (((JSONObject) agents.get(i)).get("appid").toString().equals(appId)) {
					agentId = ((JSONObject) agents.get(i)).get("agentid").toString();
					break;
				}
			}
		} else {
			throw new OApiResultException("agentid");
		}
	}

	public static void getContactList() throws OApiException {
	
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String preurl ="https://oapi.dingtalk.com/auth/scopes?access_token="+accesstoken;
		JSONObject response = HttpHelper.httpGet(preurl);
		System.out.println(response);
	
	}
	
	
	public static void getStaffInfo() throws OApiException {
		
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String preurl ="https://oapi.dingtalk.com/user/get?access_token="+accesstoken+"&userid=2252";
		
		JSONObject response = HttpHelper.httpGet(preurl);
		
		System.out.println(response);
	
	}
	
	public static void getDeptStaffList(String depId) throws OApiException {
		
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String preurl ="https://oapi.dingtalk.com/user/list?access_token="+accesstoken+"&department_id="+depId;
		JSONObject response = HttpHelper.httpGet(preurl);
		JSONArray array = response.getJSONArray("userlist");
		if (array.size()>0) {
			for (int j = 0; j < array.size(); j++) {
				JSONObject staff = (JSONObject) array.get(j);
				String name = staff.getString("name");
				String mobile = staff.getString("mobile");
				String position = staff.getString("position");
				System.out.println(name+"--"+mobile+"--"+position);
			}
			
		}
	
	}
	
	
	
	public static void allStaffList() throws OApiException {
		String accesstoken = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
		String preurl ="https://oapi.dingtalk.com/department/list?access_token="+accesstoken;
		JSONObject response = HttpHelper.httpGet(preurl);
		JSONArray array1 = response.getJSONArray("department");
		JSONArray staffList = new JSONArray();
		for (int i = 0; i < array1.size(); i++) {
			try {
				JSONObject dept=(JSONObject) array1.get(i);
				String ss=String.valueOf(dept.get("id")) ;
				
				Thread.sleep(100);
				
				String accesstoken2 = AuthHelper.getAccessToken2("ding189ce9531955723d","uqMj1j9LjCzq3-xf1mlylbn8Z34eJomZICrJrBZl_IvK1Refv2CZ8Ioyxg_5l6bN");
				String preurl2 ="https://oapi.dingtalk.com/user/list?access_token="+accesstoken2+"&department_id="+ss;
				JSONObject response2 = HttpHelper.httpGet(preurl2);
				JSONArray array2 = response2.getJSONArray("userlist");
				if (array2.size()>0) {
					for (int j = 0; j < array2.size(); j++) {
						JSONObject staff = (JSONObject) array2.get(j);
						String name = staff.getString("name");
						String mobile = staff.getString("mobile");
						JSONObject people = new JSONObject();
						people.put("name",name);
						people.put("mobile",mobile);
						
						Boolean flag=staffList.contains(people);
						if (!flag) {
							staffList.add(people);
						}else {
							System.out.println("存在重复数据"+people);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		System.out.println(staffList);
	}
	
	
	
	
	
	
	public static void main(String[] args) throws Exception {
//		getInfoList();
//		sendDingMsg();
//		getStaffList();
//		getDepartmentList();
//		sendDingOaMsg();
//		getAgentId("ding189ce9531955723d", "1660");
//		getContactList();
//		getStaffInfo();
//		getDeptStaffList();
//		allStaffList();
		
		AuthHelper.getAgentId("ding189ce9531955723d", "1660");
	}
	
	
}
