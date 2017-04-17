package com.crm.api.constant;

/**
 * 页面信息配置常量类
 * 
 * @author JingChenglong 2017-01-02 16:37
 *
 */
public class PageConfConst {

	/** -- 页面tab页 -- **/
	public static final String TAB_ALL = "all";// 所有客资
	public static final String TAB_NEW = "new";// 新客资
	public static final String TAB_TRACE = "trace";// 待追踪客资
	public static final String TAB_ORDER = "order";// 已预约客资
	public static final String TAB_COME = "come";// 已进店客资
	public static final String TAB_SUCCESS = "success";// 已定单客资
	public static final String TAB_INVALID = "invalid";// 无效客资

	/** -- 电商录入 -- **/
	public static final String KZ_ADD_PAGE = "kz_add";// 录入客资
	public static final String KZ_INVALID_PAGE = "kz_invalid";// 无效客资
	public static final String INVALID_STAY_PAGE = "ds_invalid_stay";// 无效待审批
	public static final String COL_MIX_PAGE = "ds_collector_mix";// 采集员调配

	/** -- 电商推广 -- **/
	public static final String PROMOTE_DS = "promote_ds";// 电商推广
	public static final String PROMOTE_MIX_PAGE = "promote_mix_ds";// 电商推广员调配

	/** -- 电商邀约 -- **/
	public static final String YAOYUE_DS = "yaoyue_ds";// 电商邀约
	public static final String TRACE_CLIENT_DS = "trace_client_ds";// 客资追踪
	public static final String WEDDING_DS_PAGE = "wedding_near_ds";// 婚期临近
	public static final String MINDSHOP_DS_PAGE = "to_shop_mind_ds";// 预计到店
	public static final String SUCSSHOP_DS_PAGE = "to_shop_success_ds";// 我的到店
	public static final String INVALID_CHK_PAGE = "ds_invalid_check";// 无效待审核
	public static final String APT_MIX_DS_PAGE = "appointor_mix_ds";// 邀约员调配

	/** -- 转介绍推广 -- **/
	public static final String PROMOTE_ZJS = "promote_zjs";// 转介绍推广
	public static final String INVALID_STAY_ZJS = "zjs_invalid_stay";// 转介绍无效待审批
	public static final String PROMOTE_MIX_ZJS = "promote_mix_ds";// 转介绍推广员调配

	/** -- 转介绍邀约 -- **/
	public static final String RECVE_ORDER_PAGE = "receive_order_zjs";// 批量接单
	public static final String YY_ZJS_PAGE = "yaoyue_zjs";// 转介绍邀约
	public static final String TRACE_ZJS_PAGE = "trace_client_zjs";// 客资追踪
	public static final String WEDDING_ZJS_PAGE = "wedding_near_zjs";// 婚期临近
	public static final String MINDSHOP_ZJS_PAGE = "to_shop_mind_zjs";// 预计到店
	public static final String SUCSSHOP_ZJS_PAGE = "to_shop_success_zjs";// 我的到店
	public static final String APT_MIX_ZJS_PAGE = "appointor_mix_zjs";// 邀约员调配

	/** -- 手机端 -- **/
	public static final String MOBOLE_DS_YY = "mobile_ds_yy";// 电商邀约
	public static final String MOBOLE_ZJS_YY = "mobile_zjs_yy";// 转介绍邀约
	public static final String MOBOLE_SHOP_MEET = "mobile_shop_meet";// 门店洽谈

	/** -- 根据tab切换页获取对应的客资类型ID -- **/
	public static String getClassIdByNav(String nav) {
		String classId = "";
		if (TAB_NEW.equals(nav)) {
			classId = ClientInfoConstant.CLASSID_NEW;
		} else if (TAB_TRACE.equals(nav)) {
			classId = ClientInfoConstant.CLASSID_TRACE;
		} else if (TAB_ORDER.equals(nav)) {
			classId = ClientInfoConstant.CLASSID_ORDER;
		} else if (TAB_COME.equals(nav)) {
			classId = ClientInfoConstant.CLASSID_COME;
		} else if (TAB_SUCCESS.equals(nav)) {
			classId = ClientInfoConstant.CLASSID_SUCCESS;
		} else if (TAB_INVALID.equals(nav)) {
			classId = ClientInfoConstant.CLASSID_INVALID;
		}
		return classId;
	}
}