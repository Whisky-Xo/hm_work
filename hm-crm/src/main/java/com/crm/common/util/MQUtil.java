package com.crm.common.util;

import java.util.Properties;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;

/**
 * 阿里云消息队列
 * 
 * @author JingChenglong 2016-09-20 16:30
 *
 */
public class MQUtil {

	private static Producer producer;

	static {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.ProducerId, "PID_hm_crm");// 您在MQ控制台创建的ProducerID
		properties.put(PropertyKeyConst.AccessKey, "AIYyQFPgNSVl2eKp");// 鉴权用AccessKey，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.SecretKey, "R5m4dtSjApGlASADDuFSNB7QZ0LFAT ");// 鉴权用SecretKey，在阿里云服务器管理控制台创建
		producer = ONSFactory.createProducer(properties);
		producer.start();// 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可
	}

	public static void sendMsg(String msgType, String msgJson) {
		Message msg = new Message(
				// Message Topic
				"hm_crm_test",
				// Message Tag, 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
				msgType,
				// Message Body 任何二进制形式的数据， MQ不做任何干预，
				// 需要Producer与Consumer协商好一致的序列化和反序列化方式
				msgJson.getBytes());
		// 设置代表消息的业务关键属性，请尽可能全局唯一，以方便您在无法正常收到消息情况下，可通过MQ控制台查询消息并补发
		// 注意：不设置也不会影响消息正常收发
		msg.setKey("ORDERID_100");
		// 发送消息，只要不抛异常就是成功
		producer.send(msg);
		System.out.println("消息发送成功");
	}

	public static void main(String[] args) {
		sendMsg("Taga", "孙泉你好");
	}
}
