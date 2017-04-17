package com.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 企业职工
 * 
 * @author JingChenglong 2016-09-08 20:23
 *
 */
public class Staff extends BaseObject {

	private static final long serialVersionUID = 4130572678226854658L;

	public static final String ROLE_DSCJ = "dscj";// 电商客服
	public static final String ROLE_DSYY = "dsyy";// 网络顾问
	public static final String ROLE_ZJSYY = "zjsyy";// 转介绍客服
	public static final String ROLE_MSJD = "msjd";// 门店门市
	public static final String ROLE_ZYZX = "zyzx";// 专业中心
	public static final String ROLE_CWZY = "cwzy";// 财务专员
	public static final String ROLE_GLZX = "glzx";// 管理中心
	public static final String ROLE_RSZY = "rszy";// 人事专员

	public static final String CJ_SUFFIX = "cj";// 身份后缀--采集
	public static final String TG_SUFFIX = "tg";// 身份后缀--推广
	public static final String YY_SUFFIX = "yy";// 身份后缀--邀约
	public static final String JD_SUFFIX = "jd";// 身份后缀--接待

	private Integer id;// 主键ID
	private Integer companyId;// 所属企业ID
	private String companyName;// 所属企业名称
	private String name;// 职工姓名
	private String phone;// 职工电话
	private String headImg;// 头像
	@JsonIgnore
	private Integer role; // 排空系统使用--crm系统不用
	private String username;// 登录账号
	private String password;// 登录密码
	private String corpId;// 钉钉关联ID
	private String merchantPid;// 口碑商户ID
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private String lastTime;// 最后登录时间
	private String lastIp;// 最后登录IP
	private Integer shopId;// 门店ID
	private String shopName;// 门店名称
	private Boolean isChief;// 是否主管
	private Boolean isShow;// 是否启用
	private Boolean isLock;// 锁定标识
	private Boolean isDel;// 删除标识
	private String deptId;// 部门ID
	private String deptName;// 部门名称
	private Integer groupId;// 团队ID
	private String dingUserId;// 钉钉userId
	private String department;// 部门
	private Integer created;// 创建时间
	private Integer updated;// 更新时间

	private String roleType; // 角色类型

	private String job; // 职位类型

	private String srcRelaType;// 渠道关联类型--获取其对应的渠道时使用，采集/邀约...

	private String srcType;// 渠道类型

	private Integer srcId;// 渠道ID（查询时用）

	/*-- 所属企业信息 --*/
	private String compCorpId;// 所属企业corpId
	private String compCorpSecret;// 所属企业CorpSecret
	private String compSsoSecret;// 所属企业ssoSecret
	private String compChannelSecret;// 所属企业channelSecret
	private String agentId;

	private Integer limitNumDay;// 日限额量
	private Integer todayNum;// 今日已接单量
	private Integer jdSort;// 接单顺序
	private Integer jdNum;// 今日接单个数

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getCompSsoSecret() {
		return compSsoSecret;
	}

	public void setCompSsoSecret(String compSsoSecret) {
		this.compSsoSecret = compSsoSecret;
	}

	public String getCompChannelSecret() {
		return compChannelSecret;
	}

	public void setCompChannelSecret(String compChannelSecret) {
		this.compChannelSecret = compChannelSecret;
	}

	public String getCompCorpId() {
		return compCorpId;
	}

	public void setCompCorpId(String compCorpId) {
		this.compCorpId = compCorpId;
	}

	public String getCompCorpSecret() {
		return compCorpSecret;
	}

	public void setCompCorpSecret(String compCorpSecret) {
		this.compCorpSecret = compCorpSecret;
	}

	public Integer getSrcId() {
		return srcId;
	}

	public void setSrcId(Integer srcId) {
		this.srcId = srcId;
	}

	public String getSrcType() {
		return srcType;
	}

	public void setSrcType(String srcType) {
		this.srcType = srcType;
	}

	public Boolean getIsChief() {
		return isChief;
	}

	public void setIsChief(Boolean isChief) {
		this.isChief = isChief;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public Integer getTodayNum() {
		return todayNum;
	}

	public void setTodayNum(Integer todayNum) {
		this.todayNum = todayNum;
	}

	public String getSrcRelaType() {
		return srcRelaType;
	}

	public void setSrcRelaType(String srcRelaType) {
		this.srcRelaType = srcRelaType;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Integer getCreated() {
		return created;
	}

	public void setCreated(Integer created) {
		this.created = created;
	}

	public Integer getUpdated() {
		return updated;
	}

	public void setUpdated(Integer updated) {
		this.updated = updated;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getMerchantPid() {
		return merchantPid;
	}

	public void setMerchantPid(String merchantPid) {
		this.merchantPid = merchantPid;
	}

	public String getDingUserId() {
		return dingUserId;
	}

	public void setDingUserId(String dingUserId) {
		this.dingUserId = dingUserId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateIp() {
		return createIp;
	}

	public void setCreateIp(String createIp) {
		this.createIp = createIp;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastIp() {
		return lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	public Boolean getIsLock() {
		return isLock;
	}

	public void setIsLock(Boolean isLock) {
		this.isLock = isLock;
	}

	public Boolean getIsDel() {
		return isDel;
	}

	public void setIsDel(Boolean isDel) {
		this.isDel = isDel;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public Integer getLimitNumDay() {
		return limitNumDay;
	}

	public void setLimitNumDay(Integer limitNumDay) {
		this.limitNumDay = limitNumDay;
	}

	public Integer getJdSort() {
		return jdSort;
	}

	public void setJdSort(Integer jdSort) {
		this.jdSort = jdSort;
	}

	public Integer getJdNum() {
		return jdNum;
	}

	public void setJdNum(Integer jdNum) {
		this.jdNum = jdNum;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

}