package com.crm.model;

public class Notification extends BaseObject {

	private static final long serialVersionUID = 1L;

	private String commodity_order_id;

	private String order_time;

	private String title;

	private String name;

	private String merchant_pid;

	private String contactor;

	private String phone;

	private String order_item_num;

	private String total_price;

	private String biz_type;

	// 0-未操作，1-部署成功，-1-部署失败，2-部署中
	private Integer deployStatus;

	public String getCommodity_order_id() {
		return commodity_order_id;
	}

	public void setCommodity_order_id(String commodity_order_id) {
		this.commodity_order_id = commodity_order_id;
	}

	public String getOrder_time() {
		return order_time;
	}

	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMerchant_pid() {
		return merchant_pid;
	}

	public void setMerchant_pid(String merchant_pid) {
		this.merchant_pid = merchant_pid;
	}

	public String getContactor() {
		return contactor;
	}

	public void setContactor(String contactor) {
		this.contactor = contactor;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOrder_item_num() {
		return order_item_num;
	}

	public void setOrder_item_num(String order_item_num) {
		this.order_item_num = order_item_num;
	}

	public String getTotal_price() {
		return total_price;
	}

	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}

	public String getBiz_type() {
		return biz_type;
	}

	public void setBiz_type(String biz_type) {
		this.biz_type = biz_type;
	}

	public Integer getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(Integer deployStatus) {
		this.deployStatus = deployStatus;
	}
}
