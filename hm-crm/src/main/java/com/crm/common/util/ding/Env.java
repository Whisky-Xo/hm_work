package com.crm.common.util.ding;

import org.springframework.stereotype.Component;

@Component
public class Env {
	
	
//	public static String SUITE_KEY;
//	public static String SUITE_SECRET;
//	
//	private String psuite_key;
//	private String psuite_secret;
//	
//	@Value("${DING.SUITE_KEY}")
//	public void setPsuite_key(String psuite_key){
//		Env.SUITE_KEY = psuite_key;
//	}
//	@Value("${DING.SUITE_SECRET}")
//	public void setPsuite_secret(String psuite_secret){
//		Env.SUITE_SECRET = psuite_secret;
//	}
	
	public static final String OAPI_HOST = "https://oapi.dingtalk.com";
	public static String suiteTicket; 
	public static String authCode; 
	public static String suiteToken; 
	public static final String CREATE_SUITE_KEY = "suite4xxxxxxxxxxxxxxx";

	
	public static String HOST="oa.qiein.com";
	public static String SUITE_KEY="suite6z1umg1ghjtumxzk";
	public static String SUITE_SECRET="tdzD2wMXNWFV1dtqkIZJlEf3sToLo5IkZhN7PBR6se0MWDax6_pcI1Q3IMIBz8k2";
	public static String TOKEN="1234qwer";
	public static String ENCODING_AES_KEY="wky3l3lc8bkr04t2iwypfqr6fntlp4i2ffd3qlgiwnv";
		
	
}
