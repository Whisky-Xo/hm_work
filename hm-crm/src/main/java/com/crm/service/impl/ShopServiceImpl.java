package com.crm.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.DateUtil;
import com.crm.common.util.StringUtil;
import com.crm.model.Shop;
import com.crm.model.Staff;
import com.crm.model.dao.ShopDao;
import com.crm.service.ShopService;

@Service
public class ShopServiceImpl implements ShopService {

	@Autowired
	ShopDao shopDao;

	@Override
	public Shop getByShopId(int shopId) {
		return shopDao.getByShopId(shopId);
	}

	@Override
	public Shop getByAlipayShopId(String alipayShopId) {
		return shopDao.getByAlipayShopId(alipayShopId);
	}

	@Override
	public int createShop(Shop shop) {
		String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		shop.setCreateTime(now);
		shop.setUpdateTime(now);
		return shopDao.createShop(shop);
	}

	@Override
	public int closeShop(int shopId) {
		// 1--启用，0--关闭
		Shop shop = getByShopId(shopId);
		shop.setIsShow(false);
		return updateShop(shop);
	}

	@Override
	public int updateShop(Shop shop) {
		String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		shop.setUpdateTime(now);
		return shopDao.updateShop(shop);
	}

	@Override
	public List<Shop> listShops(int companyId) {
		return shopDao.listShops(companyId);
	}

	@Override
	// @RedisCache(name = "获取商店列表", expire = 600, cacheKey =
	// "listOpeningShops",type = Shop.class)
	// @RedisEvict(type = Shop.class)
	public List<Shop> listOpeningShops(int companyId) {
		// RedisUtil redisUtil = new RedisUtil();
		// //从缓存中取内容
		// try {
		// String result = redisUtil.get(companyId+"listOpeningShops");
		// if (!StringUtils.isBlank(result)) {
		// //把字符串转换成list
		// List<Shop> shopList = JsonUtils.jsonToList(result, Shop.class);
		// System.out.println("从redis取出了shoplist");
		// return shopList;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		List<Shop> shopList = shopDao.listOpeningShops(companyId);

		// //向缓存中添加内容
		// try {
		// //把list转换成字符串
		// String cacheString = JsonUtils.objectToJson(shopList);
		// redisUtil.set(companyId+"listOpeningShops", cacheString);
		// System.out.println("把shoplist存入redis了");
		// } catch (Exception e) {
		// e.printStackTrace();
		// return shopList;
		// }

		return shopList;
	}

	@Override
	public List<Shop> listOpeningStoresForAlbum(int companyId){
		return shopDao.listOpeningStoresForAlbum(companyId);
	}

	/*-- 获取本门店所有职工 --*/
	public List<Staff> listStaffByShopId(int companyId, String shopId) {

		if (companyId == 0 || StringUtil.isEmpty(shopId)) {
			return null;
		}
		return shopDao.listStaffByShopId(companyId, shopId);
	}
}