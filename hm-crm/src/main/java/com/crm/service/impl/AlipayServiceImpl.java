package com.crm.service.impl;

import org.springframework.stereotype.Service;

import com.crm.service.AlipayService;

@Service
public class AlipayServiceImpl implements AlipayService {

	@Override
	public boolean verifyMerchantAndShop(String merchantPid, String shopId) {

		return true;
		/*
		 * 这个主要是为了验证merchantPid和shopId是否对应
		 * 查询notification和alipay_order(此表还未创建，应该是alipay.open.servicemarket.order
		 * .query拿过来的数据) 目前手动实施不做验证，自动化的时候应该是
		 * if(notificationDao.verfyMerchantAndShop(merchantPid,shopId)!=1){
		 * return false; }
		 * 这个方法notificationDao.verfyMerchantAndShop(merchantPid,shopId); xml对应的是
		 * 
		 * select * from notification n inner join alipay_order o on
		 * n.commodity_order_id=o.commodity_order_id where n.merchant_pid =
		 * #{merchantPid} and o.shop_id = #{shopId}
		 * 
		 */
	}
}