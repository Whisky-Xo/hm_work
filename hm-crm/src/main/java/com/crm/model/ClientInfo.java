package com.crm.model;

import java.util.List;

/**
 * 客资信息
 * 
 * @author JingChenglong 2016-09-09 09:56
 *
 */
public class ClientInfo extends BaseObject {

	private static final long serialVersionUID = 1l;

	private String kzNum;// 客资编号
	private String kzId;// 客资ID
	private String kzName;// 姓名
	private String kzPhone;// 电话
	private String kzQq;// QQ
	private Boolean qqFlag;// QQ是否已加
	private String kzWechat;// 微信
	private Boolean weChatFlag;// 微信是否已加
	private String sex;// 性别（0男 1女 9未知）
	private String address;// 地址
	private String trColor;// 行颜色
	private Integer classId;// 分类ID
	private String className;// 分类
	private Integer typeId;// 类型ID
	private String typeName;// 类型
	private Integer statusId;// 当前状态ID
	private String stsColor;// 状态对应颜色
	private String status;// 状态
	private Integer sourceId;// 渠道ID
	private String source;// 渠道名称
	private String sourceImg;// 渠道图片地址
	private String appointTime;// 预约到店时间
	private String actualTime;// 实际到店时间
	private String traceTime;// 追踪时间
	private Integer appointId;// 邀约员ID
	private String appoint;// 邀约员姓名
	private String ypTime;// 约拍时间
	private String marryTime;// 婚期
	private Integer collectorId;// 采集员ID
	private String collector;// 采集员姓名
	private String memo;// 追踪备注
	private String remark;// 提报备注
	private String mateName;// 配偶姓名
	private String matePhone;// 配偶电话/微信
	private String merchantPid;// 口碑商户ID
	private String mergeId;// 合并至客资ID
	private String updateTime;// 最后跟进时间
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private Integer companyId;// 所属企业ID
	private String companyName;// 所属企业名称
	private Boolean haveMerge;// 是否有合并
	private Boolean havaSms;// 是否发送预约到店短信
	private Integer shopId;// 门店ID
	private String shopName;// 门店名称
	private Boolean isDel;// 删除标识
	private String statusColor;// 颜色代码值
	private String receiveTime;// 接单时间
	private String receiveStart;//
	private String receiveEnd;//

	private String searchKey;// 模糊查询参数

	private String amount;// 成交金额
	private Integer promoterId;// 推广员ID
	private String promoter;// 推广员姓名

	private String receptorId;// 门市接待员ID
	private String receptor;// 门市接待员姓名

	private List<ClientLogInfo> logList;// 操作日志记录
	private List<ClientYaoYueLog> yyLogList;// 邀约记录
	private List<ShopMeetLog> qtLogList;// 门店洽谈记录

	private String spare1;// 备用属性1

	private String collectorName;// 采集员姓名
	private String collectorPhone;// 采集员电话、微信
	private String sourceSpare;// 渠道附属信息
	private String oldKzName;// 老客户姓名
	private String oldKzPhone;// 老客户电话
	private String oldKzVipNo;// 老客户VIP卡号

	private String validTime;// 有效时间(第一次判定)
	private String comeShopTime;// 到店时间(第一次到店)
	private String successTime;// 成交时间

	private String smsCode;// 短信优惠编码

	private String runOffCode;// 流失原因编码
	private String runOffLabel;// 流失原因
	private String invalidCode;// 无效原因编码
	private String invalidLabel;// 无效原因

	private String tabName;// 表名

	// --详细表信息
	private String zxStyle;// 咨询方式
	private String yxLavel;// 意向登记
	private String ysRange;// 预算范围
	private String adAddress;// 广告着陆页
	private String adId;// 广告ID
	private String oldKzId;// 老客资ID
	private String attachFile;// 附件
	private String marryMemo;// 婚期备注
	private String ypMemo;// 预拍备注
	private String mateWeChat;// 配偶微信
	private String mateQq;// 配偶QQ
	private String birthTime;// 出生日期
	private String ageNum;// 生日
	private String idNum;// 证件号码
	private String job;// 职业
	private String edu;// 教育程度
	private String earn;// 收入程度
	private String carHouse;// 车房情况

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getMateWeChat() {
		return mateWeChat;
	}

	public void setMateWeChat(String mateWeChat) {
		this.mateWeChat = mateWeChat;
	}

	public String getMateQq() {
		return mateQq;
	}

	public void setMateQq(String mateQq) {
		this.mateQq = mateQq;
	}

	public String getBirthTime() {
		return birthTime;
	}

	public void setBirthTime(String birthTime) {
		this.birthTime = birthTime;
	}

	public String getAgeNum() {
		return ageNum;
	}

	public void setAgeNum(String ageNum) {
		this.ageNum = ageNum;
	}

	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getEdu() {
		return edu;
	}

	public void setEdu(String edu) {
		this.edu = edu;
	}

	public String getEarn() {
		return earn;
	}

	public void setEarn(String earn) {
		this.earn = earn;
	}

	public String getCarHouse() {
		return carHouse;
	}

	public void setCarHouse(String carHouse) {
		this.carHouse = carHouse;
	}

	public String getMarryMemo() {
		return marryMemo;
	}

	public void setMarryMemo(String marryMemo) {
		this.marryMemo = marryMemo;
	}

	public String getYpMemo() {
		return ypMemo;
	}

	public void setYpMemo(String ypMemo) {
		this.ypMemo = ypMemo;
	}

	public String getAttachFile() {
		return attachFile;
	}

	public void setAttachFile(String attachFile) {
		this.attachFile = attachFile;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getZxStyle() {
		return zxStyle;
	}

	public void setZxStyle(String zxStyle) {
		this.zxStyle = zxStyle;
	}

	public String getYxLavel() {
		return yxLavel;
	}

	public void setYxLavel(String yxLavel) {
		this.yxLavel = yxLavel;
	}

	public String getYsRange() {
		return ysRange;
	}

	public void setYsRange(String ysRange) {
		this.ysRange = ysRange;
	}

	public String getAdAddress() {
		return adAddress;
	}

	public void setAdAddress(String adAddress) {
		this.adAddress = adAddress;
	}

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}

	public String getOldKzId() {
		return oldKzId;
	}

	public void setOldKzId(String oldKzId) {
		this.oldKzId = oldKzId;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getReceiveStart() {
		return receiveStart;
	}

	public void setReceiveStart(String receiveStart) {
		this.receiveStart = receiveStart;
	}

	public String getReceiveEnd() {
		return receiveEnd;
	}

	public void setReceiveEnd(String receiveEnd) {
		this.receiveEnd = receiveEnd;
	}

	public String getSourceImg() {
		return sourceImg;
	}

	public void setSourceImg(String sourceImg) {
		this.sourceImg = sourceImg;
	}

	public Integer getClassId() {
		return classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
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

	public String getKzNum() {
		return kzNum;
	}

	public void setKzNum(String kzNum) {
		this.kzNum = kzNum;
	}

	public String getStatusColor() {
		return statusColor;
	}

	public void setStatusColor(String statusColor) {
		this.statusColor = statusColor;
	}

	public String getTraceTime() {
		return traceTime;
	}

	public void setTraceTime(String traceTime) {
		this.traceTime = traceTime;
	}

	public String getTrColor() {
		return trColor;
	}

	public void setTrColor(String trColor) {
		this.trColor = trColor;
	}

	public String getStsColor() {
		return stsColor;
	}

	public void setStsColor(String stsColor) {
		this.stsColor = stsColor;
	}

	public Boolean getQqFlag() {
		return qqFlag;
	}

	public void setQqFlag(Boolean qqFlag) {
		this.qqFlag = qqFlag;
	}

	public Boolean getWeChatFlag() {
		return weChatFlag;
	}

	public void setWeChatFlag(Boolean weChatFlag) {
		this.weChatFlag = weChatFlag;
	}

	public String getRunOffCode() {
		return runOffCode;
	}

	public void setRunOffCode(String runOffCode) {
		this.runOffCode = runOffCode;
	}

	public String getRunOffLabel() {
		return runOffLabel;
	}

	public void setRunOffLabel(String runOffLabel) {
		this.runOffLabel = runOffLabel;
	}

	public String getInvalidCode() {
		return invalidCode;
	}

	public void setInvalidCode(String invalidCode) {
		this.invalidCode = invalidCode;
	}

	public String getInvalidLabel() {
		return invalidLabel;
	}

	public void setInvalidLabel(String invalidLabel) {
		this.invalidLabel = invalidLabel;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

	public String getComeShopTime() {
		return comeShopTime;
	}

	public void setComeShopTime(String comeShopTime) {
		this.comeShopTime = comeShopTime;
	}

	public String getSuccessTime() {
		return successTime;
	}

	public void setSuccessTime(String successTime) {
		this.successTime = successTime;
	}

	public String getMatePhone() {
		return matePhone;
	}

	public void setMatePhone(String matePhone) {
		this.matePhone = matePhone;
	}

	public Boolean getHavaSms() {
		return havaSms;
	}

	public void setHavaSms(Boolean havaSms) {
		this.havaSms = havaSms;
	}

	public List<ShopMeetLog> getQtLogList() {
		return qtLogList;
	}

	public void setQtLogList(List<ShopMeetLog> qtLogList) {
		this.qtLogList = qtLogList;
	}

	public String getReceptorId() {
		return receptorId;
	}

	public void setReceptorId(String receptorId) {
		this.receptorId = receptorId;
	}

	public String getReceptor() {
		return receptor;
	}

	public void setReceptor(String receptor) {
		this.receptor = receptor;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Integer getPromoterId() {
		return promoterId;
	}

	public void setPromoterId(Integer promoterId) {
		this.promoterId = promoterId;
	}

	public String getPromoter() {
		return promoter;
	}

	public void setPromoter(String promoter) {
		this.promoter = promoter;
	}

	public String getMarryTime() {
		return marryTime;
	}

	public void setMarryTime(String marryTime) {
		this.marryTime = marryTime;
	}

	public String getCollectorName() {
		return collectorName;
	}

	public void setCollectorName(String collectorName) {
		this.collectorName = collectorName;
	}

	public String getCollectorPhone() {
		return collectorPhone;
	}

	public void setCollectorPhone(String collectorPhone) {
		this.collectorPhone = collectorPhone;
	}

	public String getSourceSpare() {
		return sourceSpare;
	}

	public void setSourceSpare(String sourceSpare) {
		this.sourceSpare = sourceSpare;
	}

	public String getOldKzName() {
		return oldKzName;
	}

	public void setOldKzName(String oldKzName) {
		this.oldKzName = oldKzName;
	}

	public String getOldKzPhone() {
		return oldKzPhone;
	}

	public void setOldKzPhone(String oldKzPhone) {
		this.oldKzPhone = oldKzPhone;
	}

	public String getOldKzVipNo() {
		return oldKzVipNo;
	}

	public void setOldKzVipNo(String oldKzVipNo) {
		this.oldKzVipNo = oldKzVipNo;
	}

	public String getSpare1() {
		return spare1;
	}

	public void setSpare1(String spare1) {
		this.spare1 = spare1;
	}

	public List<ClientYaoYueLog> getYyLogList() {
		return yyLogList;
	}

	public void setYyLogList(List<ClientYaoYueLog> yyLogList) {
		this.yyLogList = yyLogList;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public List<ClientLogInfo> getLogList() {
		return logList;
	}

	public void setLogList(List<ClientLogInfo> logList) {
		this.logList = logList;
	}

	public Boolean getHaveMerge() {
		return haveMerge;
	}

	public void setHaveMerge(Boolean haveMerge) {
		this.haveMerge = haveMerge;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getKzId() {
		return kzId;
	}

	public void setKzId(String kzId) {
		this.kzId = kzId;
	}

	public String getKzName() {
		return kzName;
	}

	public void setKzName(String kzName) {
		this.kzName = kzName;
	}

	public String getKzPhone() {
		return kzPhone;
	}

	public void setKzPhone(String kzPhone) {
		this.kzPhone = kzPhone;
	}

	public String getKzQq() {
		return kzQq;
	}

	public void setKzQq(String kzQq) {
		this.kzQq = kzQq;
	}

	public String getKzWechat() {
		return kzWechat;
	}

	public void setKzWechat(String kzWechat) {
		this.kzWechat = kzWechat;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAppointTime() {
		return appointTime;
	}

	public void setAppointTime(String appointTime) {
		this.appointTime = appointTime;
	}

	public String getActualTime() {
		return actualTime;
	}

	public void setActualTime(String actualTime) {
		this.actualTime = actualTime;
	}

	public Integer getAppointId() {
		return appointId;
	}

	public void setAppointId(Integer appointId) {
		this.appointId = appointId;
	}

	public String getAppoint() {
		return appoint;
	}

	public void setAppoint(String appoint) {
		this.appoint = appoint;
	}

	public String getYpTime() {
		return ypTime;
	}

	public void setYpTime(String ypTime) {
		this.ypTime = ypTime;
	}

	public Integer getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(Integer collectorId) {
		this.collectorId = collectorId;
	}

	public String getCollector() {
		return collector;
	}

	public void setCollector(String collector) {
		this.collector = collector;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getMateName() {
		return mateName;
	}

	public void setMateName(String mateName) {
		this.mateName = mateName;
	}

	public String getMerchantPid() {
		return merchantPid;
	}

	public void setMerchantPid(String merchantPid) {
		this.merchantPid = merchantPid;
	}

	public String getMergeId() {
		return mergeId;
	}

	public void setMergeId(String mergeId) {
		this.mergeId = mergeId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
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

	public Boolean getIsDel() {
		return isDel;
	}

	public void setIsDel(Boolean isDel) {
		this.isDel = isDel;
	}
}