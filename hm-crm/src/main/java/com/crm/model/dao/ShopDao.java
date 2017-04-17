package com.crm.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.crm.model.Shop;
import com.crm.model.Staff;

public interface ShopDao {

	public Shop getByShopId(int shopId);

	public Shop getByAlipayShopId(@Param("alipayShopId") String alipayShopId);

	public int createShop(Shop shop);

	public int updateShop(Shop shop);

	public List<Shop> listShops(int companyId);

	public List<Shop> listOpeningShops(int companyId);

	public List<Shop> listOpeningStoresForAlbum(int companyId);

	/*-- 获取本门店所有职工 --*/
	public List<Staff> listStaffByShopId(@Param("companyId") int companyId, @Param("shopId") String shopId);
}
