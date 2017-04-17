package com.crm.model;

/**
 * 客资邀约记录
 * 
 * @author JingChenglong 2016-09-21 16:43
 *
 */
public class ClientYaoYueLog extends BaseObject {

	private static final long serialVersionUID = 1l;

	private String yyLogId;// 邀约日志ID
	private String kzId;// 客资ID
	private String rstCode;// 邀约结果代码
	private String rst;// 邀约结果
	private String yyMemo;// 邀约备注
	private String comeTime;// 邀约到店时间
	private Integer shopId;// 门店ID
	private String mainShopName;// 门店一级名称
	private String branchShopName;// 门店二级名称
	private String receptorId;// 接待员ID
	private String receptorName;// 接待员姓名
	private String warnTime;// 提醒时间
	private String staffId;// 邀约员ID
	private String staffName;// 邀约员姓名
	private String file1;// 附件1
	private String file2;// 附件2
	private String file3;// 附件3
	private String file4;// 附件4
	private String file5;// 附件5
	private String file6;// 附件6
	private String file7;// 附件7
	private String file8;// 附件8
	private String file9;// 附件9
	private String file10;// 附件10
	private String createTime;// 创建时间
	private String createIp;// 创建IP
	private String statusColor;// 颜色代码值

	public String getStatusColor() {
		return statusColor;
	}

	public void setStatusColor(String statusColor) {
		this.statusColor = statusColor;
	}

	public String getYyLogId() {
		return yyLogId;
	}

	public void setYyLogId(String yyLogId) {
		this.yyLogId = yyLogId;
	}

	public String getKzId() {
		return kzId;
	}

	public void setKzId(String kzId) {
		this.kzId = kzId;
	}

	public String getRstCode() {
		return rstCode;
	}

	public void setRstCode(String rstCode) {
		this.rstCode = rstCode;
	}

	public String getRst() {
		return rst;
	}

	public void setRst(String rst) {
		this.rst = rst;
	}

	public String getYyMemo() {
		return yyMemo;
	}

	public void setYyMemo(String yyMemo) {
		this.yyMemo = yyMemo;
	}

	public String getComeTime() {
		return comeTime;
	}

	public void setComeTime(String comeTime) {
		this.comeTime = comeTime;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getMainShopName() {
		return mainShopName;
	}

	public void setMainShopName(String mainShopName) {
		this.mainShopName = mainShopName;
	}

	public String getBranchShopName() {
		return branchShopName;
	}

	public void setBranchShopName(String branchShopName) {
		this.branchShopName = branchShopName;
	}

	public String getReceptorId() {
		return receptorId;
	}

	public void setReceptorId(String receptorId) {
		this.receptorId = receptorId;
	}

	public String getReceptorName() {
		return receptorName;
	}

	public void setReceptorName(String receptorName) {
		this.receptorName = receptorName;
	}

	public String getWarnTime() {
		return warnTime;
	}

	public void setWarnTime(String warnTime) {
		this.warnTime = warnTime;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getFile1() {
		return file1;
	}

	public void setFile1(String file1) {
		this.file1 = file1;
	}

	public String getFile2() {
		return file2;
	}

	public void setFile2(String file2) {
		this.file2 = file2;
	}

	public String getFile3() {
		return file3;
	}

	public void setFile3(String file3) {
		this.file3 = file3;
	}

	public String getFile4() {
		return file4;
	}

	public void setFile4(String file4) {
		this.file4 = file4;
	}

	public String getFile5() {
		return file5;
	}

	public void setFile5(String file5) {
		this.file5 = file5;
	}

	public String getFile6() {
		return file6;
	}

	public void setFile6(String file6) {
		this.file6 = file6;
	}

	public String getFile7() {
		return file7;
	}

	public void setFile7(String file7) {
		this.file7 = file7;
	}

	public String getFile8() {
		return file8;
	}

	public void setFile8(String file8) {
		this.file8 = file8;
	}

	public String getFile9() {
		return file9;
	}

	public void setFile9(String file9) {
		this.file9 = file9;
	}

	public String getFile10() {
		return file10;
	}

	public void setFile10(String file10) {
		this.file10 = file10;
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
}