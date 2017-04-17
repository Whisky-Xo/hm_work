package com.crm.task;

import com.crm.common.util.PushUtil;
import com.crm.common.util.ding.OApiException;
import com.crm.model.ClientInfo;
import com.crm.model.Staff;

public class Test {

	public static void main(String[] args) throws OApiException {
		ClientInfo ci = new ClientInfo();
		ci.setKzId("d4e278872cc089fbfa2f566834d23f16");// 接收拒绝带回参数
		ci.setKzName("林冲1");// 客资名称
		ci.setKzPhone("18703588230");// 客资电话
		ci.setAddress("浙江省杭州市江干区");
		ci.setCollectorId(356);
		ci.setCreateIp("192.168.2.111");
		ci.setCompanyId(60);
		ci.setCreateTime("2016-09-12 14:25:23");// 创建时间
		ci.setStatusId(1);
		ci.setStatus("等待邀约");
		ci.setSource("支付宝口碑");
		ci.setSourceId(2);
		ci.setCompanyName("芭菲摄影s");
		ci.setKzQq("26076008");
		ci.setKzWechat("we25841541");

		Staff staff = new Staff();
		staff.setCompanyId(60);
		staff.setId(386);

		PushUtil.pushNewAdd(staff, ci);
	}
}
