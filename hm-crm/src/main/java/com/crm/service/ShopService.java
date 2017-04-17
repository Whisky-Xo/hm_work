package com.crm.service;

import java.util.List;

import com.crm.model.Shop;
import com.crm.model.Staff;

public interface ShopService {

	public Shop getByShopId(int shopId);

	public Shop getByAlipayShopId(String alipayShopId);

	public int createShop(Shop shop);

	public int closeShop(int shopId);

	public int updateShop(Shop shop);

	public List<Shop> listShops(int companyId);

	public List<Shop> listOpeningShops(int companyId);

	public List<Shop> listOpeningStoresForAlbum(int companyId);

	/*-- 获取本门店所有职工 --*/
	public List<Staff> listStaffByShopId(int companyId, String shopId);
}
