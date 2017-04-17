package com.crm.model;

/**
 * 客资审核记录
 * 
 * @author JingChenglong 2016-11-14 13:40
 *
 */
public class ApproveLog extends BaseObject {

	private static final long serialVersionUID = 6568695110185659685L;

	public static final String INVALID_KZ = "invalid_kz";// 无效客资
	public static final String RUN_OFF_KZ = "run_off_kz";// 流失客资

	private Integer logId;// 主键
	private String kzId;// 客资ID
	private String approveType;// 类型
	private Integer code;// 结果编码
	private Integer operaId;// 审批人ID
	private String createTime;// 时间
	private String createIp;// IP
	private Integer companyId;// 企业ID
	private Boolean isDel;// 删除标识

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public String getKzId() {
		return kzId;
	}

	public void setKzId(String kzId) {
		this.kzId = kzId;
	}

	public String getApproveType() {
		return approveType;
	}

	public void setApproveType(String approveType) {
		this.approveType = approveType;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getOperaId() {
		return operaId;
	}

	public void setOperaId(Integer operaId) {
		this.operaId = operaId;
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