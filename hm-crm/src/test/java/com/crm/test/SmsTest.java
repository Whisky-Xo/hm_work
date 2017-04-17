package com.crm.test;

import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

public class SmsTest {
	
    
   
    public static void main(String[] args) throws ApiException {
    	
    	TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest","23578236","a0add298f0572e91d3a7a91fa89e050a");
    	AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
    	req.setSmsType("normal");
    	req.setSmsFreeSignName("我是sun");
    	JSONObject json = new JSONObject();
		json.put("product", "sunquanTest");
    	req.setSmsParamString(json.toString());
    	req.setRecNum("13221009947");
    	req.setSmsTemplateCode("SMS_35340019");
    	AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
    	System.out.println(rsp.getBody());
    	
    }  
	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
	
	
}
