package com.crm.model;

import java.util.List;

/**
 * 客资渠道
 * 
 * @author JingChenglong 2016-09-09 10:25
 *
 */
public class Source extends BaseObject {

	private static final long serialVersionUID = 2743117845753896576L;

	public static final String DS_PREFIX = "ds";// 电商缩写前缀
	public static final String ZJS_PREFIX = "zjs";// 转介绍缩写前缀
	public static final String MS_PREFIX = "ms";// 门市缩写前缀

	public static final String SRC_RELA_TYPE_CJ = "cj";// 关联类型：采集
	public static final String SRC_RELA_TYPE_YY = "yy";// 关联类型：邀约
	public static final String SRC_RELA_TYPE_TG = "tg";// 关联类型：推广

	public static final String SRC_TYPE_CJ = "cj";// 关联类型：采集
	public static final String SRC_TYPE_YY = "yy";// 关联类型：邀约
	public static final String SRC_TYPE_TG = "tg";// 关联类型：推广

	public static final String SRC_TYPE_INNNER_INTRODUCE = "内部转介绍渠道";// 渠道类型：内部转介绍渠道
	public static final String SRC_TYPE_DS = "电商渠道";// 渠道类型：电商渠道
	public static final String SRC_TYPE_OUTER_INTRODUCE = "外部转介绍渠道";// 渠道类型：外部转介绍渠道
    public static final String SRC_TYPE_ZPP = "子品牌渠道";// 渠道类型：子品牌

	public static final String SRC_ALIPAY = "支付宝口碑";
	public static final String SRC_NET_ZHUANJIESHAO = "网络转介绍";// 渠道名：网络转介绍
	public static final String SRC_NATURE_COME_SHOP = "门店自然入客";// 渠道名：自然入客渠道
	public static final String SRC_TYPE_INTRODUCE = "内部转介绍渠道,外部转介绍渠道";// 转介绍

	private Integer srcId;// 渠道主键ID
	private String srcName;// 渠道名称
	private Integer typeId;// 类型ID
	private String typeName;// 类型名称
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private Integer creatorId;// 创建者ID
	private String createrName;// 创建者姓名
	private Integer companyId;// 所属企业ID
	private String companyName;// 所属企业名称
	private Boolean isShow;// 是否启用
	private Integer priority;// 优先级
	private Integer pushSort;// 推送规则

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getPushSort() {
		return pushSort;
	}

	public void setPushSort(Integer pushSort) {
		this.pushSort = pushSort;
	}

	private List<Cost> costList;

	public Source(Integer companyId, String typeName) {
		super();
		this.companyId = companyId;
		this.typeName = typeName;
	}

	public Source() {

	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getSrcId() {
		return srcId;
	}

	public void setSrcId(Integer srcId) {
		this.srcId = srcId;
	}

	public String getSrcName() {
		return srcName;
	}

	public void setSrcName(String srcName) {
		this.srcName = srcName;
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

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	public List<Cost> getCostList() {
		return costList;
	}

	public void setCostList(List<Cost> costList) {
		this.costList = costList;
	}

}