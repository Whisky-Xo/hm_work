package com.crm.model;

/**
 * 权限
 * 
 * @author jzl 2016-10-14 15:32
 *
 */
public class Permission extends BaseObject {

	private static final long serialVersionUID = -7126926502494726301L;

	private int id;

	private int created;

	private int updated;
	
	/**
	 * 权限对应ID
	 */
	private int permissionId;
	
	/**
	 * 权限名称
	 */
	private String name;
	
	
	/**
	 * 权限值
	 */
	private String value;

	/**
	 * 权限对应方法
	 */
	private String method;
	
	/**
	 * 权限描述
	 */
	private String description;
	
	/**
	 * 对应应用ID
	 */
	private Integer applicationId;
	
	/**
	 * 对应应用名称
	 */
	private String applicationName;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(int permissionId) {
		this.permissionId = permissionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public int getCreated() {
		return created;
	}

	public void setCreated(int created) {
		this.created = created;
	}

	public int getUpdated() {
		return updated;
	}

	public void setUpdated(int updated) {
		this.updated = updated;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}