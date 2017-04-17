package com.crm.task;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;

public class ConsumerTest {
	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.ConsumerId, "CID_hm_crm");// 您在MQ控制台创建的Consumer
																	// ID
		properties.put(PropertyKeyConst.AccessKey, "AIYyQFPgNSVl2eKp");// 鉴权用AccessKey，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.SecretKey, "R5m4dtSjApGlASADDuFSNB7QZ0LFAT");// 鉴权用SecretKey，在阿里云服务器管理控制台创建
		Consumer consumer = ONSFactory.createConsumer(properties);
		consumer.subscribe("hm_crm_test", "*", new MessageListener() {
			public Action consume(Message message, ConsumeContext context) {
				try {
					System.out.println("Receive: " + new String(message.getBody(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return Action.CommitMessage;
			}
		});
		consumer.start();
		System.out.println("Consumer Started");
		
	}
}
