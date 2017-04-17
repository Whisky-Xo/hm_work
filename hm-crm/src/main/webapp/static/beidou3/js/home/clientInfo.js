/** -- 客资信息操作 --* */
// 新增客资
function doAddInfo(type) {
	// type:--客资录入方式：ds/电商-zjs/转介绍-ms/门市
	if (!type) {
		// TODO 页面错误，方法调用异常
	}
	var name = $("#add_name").val();// 姓名
	var sex = $("#add_sex").val();// 性别
	var phone = $("#add_phone").val();// 电话
	var wechat = $("#add_wechat").val();// 微信
	var qq = $("#add_qq").val();// QQ
	var sourceId = $("#add_source_id").val();// 渠道ID
	var zxStyle = $("#add_zx_style").val();// 咨询方式
	var adId = $("#add_ad_id").val();// 广告ID
	var collectorId = $("#add_collector_id").val();// 采集员ID
	var adAddress = $("#add_ad_address").val();// 广告着陆页
	var typeId = $("#add_type_id").val();// 咨询类型
	var yxLavel = $("#add_yx_lavel").val();// 有效等级
	var ysRange = $("#add_ys_range").val();// 预算范围
	var marryMemo = $("#add_marry_memo").val();// 婚期描述
	var ypMemo = $("#add_yp_memo").val();// 约拍描述
	var address = getAddressStr("add");// 地址
	var remark = $("#add_remark").val();// 推广备注
	$.ajax({
		url : '../client/add',
		type : "POST",
		data : {
			add_type : type,
			name : name,
			sex : sex,
			phone : phone,
			wechat : wechat,
			qq : qq,
			address : address,
			source_id : sourceId,
			zx_style : zxStyle,
			collector_id : collectorId,
			ad_address : adAddress,
			ad_id : adId,
			type_id : typeId,
			yx_lavel : yxLavel,
			ys_range : ysRange,
			marry_memo : marryMemo,
			yp_memo : ypMemo,
			remark : remark
		},
		success : function(data) {
			if (data.code == '100000') {
				layer.msg("添加成功");
			} else {
				layer.msg(data.msg);
			}
		}
	});
}

// 电商推广编辑客资基础信息
function doEditInfoBase() {
	var kzId = $("#info_kzid").val();// 客资ID
	var typeId = $("#info_typeid").val();// 咨询类型
	var yxLavel = $("#info_yx_lavel").val();// 意向等级
	var ysRange = $("#info_ys_range").val();// 预算范围
	var marryMemo = $("#info_marry_memo").val();// 婚期描述
	var ypMemo = $("#info_yp_memo").val();// 预拍描述
	var name = $("#info_name").val();// 姓名
	var sex = $("#info_sex").val();// 性别
	var phone = $("#info_phone").val();// 电话
	var wechat = $("#info_wechat").val();// 微信
	var qq = $("#info_qq").val();// QQ
	var address = getAddressStr("edit");// 地址
	var sourceId = $("#info_source_id").val();// 渠道ID
	var zxStyle = $("#info_zx_style").val();// 咨询方式
	var collectorId = $("#info_collector_id").val();// 采集员ID
	var adAddress = $("#info_ad_address").val();// 广告着陆页
	var adId = $("#info_ad_id").val();// 广告ID
	var remark = $("#info_remark").val();// 推广备注
	if (!name) {
		layer.msg("客人姓名不能为空");
		return;
	}
	if (!phone && !wechat && !qq) {
		layer.msg("请填写联系方式");
		return;
	}
	$.ajax({
		url : '../client/edit_base',
		type : "POST",
		data : {
			kzid : kzId,
			type_id : typeId,
			yx_lavel : yxLavel,
			ys_range : ysRange,
			marry_memo : marryMemo,
			yp_memo : ypMemo,
			name : name,
			sex : sex,
			phone : phone,
			wechat : wechat,
			qq : qq,
			address : address,
			source_id : sourceId,
			zx_style : zxStyle,
			collector_id : collectorId,
			ad_address : adAddress,
			ad_id : adId,
			remark : remark
		},
		success : function(data) {
			if (data.code == '100000') {
				layer.msg("修改成功");
				// 更新文本内容
				updateInput(name, sex, phone, wechat, qq, address, marryMemo,
						ypMemo, remark);
			} else {
				layer.msg(data.msg);
			}
		}
	});
}
// 更新文本内容
function updateInput(name, sex, phone, wechat, qq, address, marryMemo, ypMemo,
		remark) {
	$("#det_kz_name").val(name);
	$("#det_kz_phone").val(phone);
	$("#det_kz_wechat").val(wechat);
	$("#det_kz_qq").val(qq);
	$("#det_marry_memo").val(marryMemo);
	$("#det_yp_memo").val(ypMemo);
	$("#det_remark").val(remark);

	$("#info_name").val(name);
	$("#info_phone").val(phone);
	$("#info_wechat").val(wechat);
	$("#info_qq").val(qq);
	$("#info_marry_memo").val(marryMemo);
	$("#info_yp_memo").val(ypMemo);
	$("#info_remark").val(remark);

	toSelectSex(sex);
	var addArray = address.split(",");
	toSelectAddress(addArray[0], addArray[1], addArray[2], addArray[3]);
}

// 编辑客资详细信息
function doSaveInfoDetail() {
	var kzId = $("#info_kzid").val();
	var typeId = $("#info_typeid").val();
	var name = $("#det_kz_name").val();
	var sex = $("#info_sex").val();
	var phone = $("#det_kz_phone").val();
	var wechat = $("#det_kz_wechat").val();
	var qq = $("#det_kz_qq").val();
	var mateName = $("#det_mate_name").val();
	var matePhone = $("#det_mate_phone").val();
	var mateWeChat = $("#det_mate_wechat").val();
	var mateQq = $("#det_mate_qq").val();

	var birthTime = $("#det_birthtime").val();
	var ageNum = $("#det_age_num").val();
	var idNum = $("#det_id_num").val();

	var job = $("#det_job").val();
	var edu = $("#det_edu").val();
	var earn = $("#det_earn").val();
	var carHouse = $("#det_car_house").val();
	var address = getAddressStr("detail");
	var marryMemo = $("#det_marry_memo").val();
	var marryTime = $("#det_marry_time").val();
	var ypMemo = $("#det_yp_memo").val();
	var ypTime = $("#det_yp_time").val();
	var remark = $("#det_remark").val();

	if (!kzId) {
		layer.msg("参数获取错误，请刷新重试");
		return;
	}
	if (!name) {
		layer.msg("客资姓名不能为空");
		return;
	}
	if (!phone && !wechat && !qq) {
		layer.msg("客资联系方式不能都为空");
		return;
	}

	$.ajax({
		url : '../client/edit_detail',
		type : "POST",
		data : {
			kzid : kzId,
			typeid : typeId,
			name : name,
			sex : sex,
			phone : phone,
			wechat : wechat,
			qq : qq,
			matename : mateName,
			matephone : matePhone,
			matewechat : mateWeChat,
			mateqq : mateQq,
			address : address,
			birthtime : birthTime,
			age : ageNum,
			idnum : idNum,
			job : job,
			edu : edu,
			earn : earn,
			carhouse : carHouse,
			marrymemo : marryMemo,
			ypmemo : ypMemo,
			marrytime : marryTime,
			yptime : ypTime,
			remark : remark
		},
		success : function(data) {
			if (data.code == '100000') {
				layer.msg("修改成功");
				// 更新文本内容
				updateInput(name, sex, phone, wechat, qq, address, marryMemo,
						ypMemo, remark);

			} else {
				layer.msg(data.msg);
			}
		}
	});
}

/* 删除客资 */
function deleteCheckBox() {

	var kzIds = getCheckedBoxIds();

	while (true) {
		if (kzIds.endsWith(",")) {
			kzIds = kzIds.substring(0, kzIds.length - 1);
		} else {
			break;
		}
	}

	if (!kzIds) {
		layer.msg("请选择要删除的客资");
		return;
	}
	layer.open({
		title : '提醒',
		content : '删除这些客资?',
		btn : [ '确定', '取消' ],
		yes : function(index, layero) {
			$.ajax({
				url : '../client/remove',
				type : "POST",
				data : {
					kzids : kzIds
				},
				success : function(data) {
					if (data.code == '100000') {
						layer.msg("删除成功");
						doSearch();
					} else {
						layer.msg(data.msg);
					}
				}
			});
		}
	})
}
// 转移客资
function mixClients() {
	// type---转移方式;COLLECTORID-转移采集,APPOINTID-转移邀约员,PROMOTERID-转移推广员,RECEPTORID-转移接待员
	var staffRole = $("#role_select").val();// 用户角色

	if (!staffRole) {
		layer.msg("角色身份获取错误,请刷新后重试");
		return;
	}

	if (staffRole.endsWith("cj")) {
		type = "COLLECTORID";
	} else if (staffRole.endsWith("yy")) {
		type = "APPOINTID";
	} else if (staffRole.endsWith("tg")) {
		type = "PROMOTERID";
	} else if (staffRole.endsWith("jd")) {
		type = "RECEPTORID";
	} else {
		layer.msg("当前职工身份页不能转移客资");
		return;
	}

	var kzIds = getCheckedBoxIds();// 要转移的客资ID
	var staffId = $("#remove_to_id").val();// 目标职员ID

	while (true) {
		if (kzIds.endsWith(",")) {
			kzIds = kzIds.substring(0, kzIds.length - 1);
		} else {
			break;
		}
	}

	if (!kzIds) {
		layer.msg("请选择要转移的客资");
		return;
	}
	if (!type) {
		layer.msg("请选择转移方式");
		return;
	}
	if (!staffId) {
		layer.msg("请选择客资的归属职工");
		return;
	}

	$.ajax({
		url : '../client/mix',
		type : "POST",
		data : {
			kzids : kzIds,
			type : type,
			staffid : staffId
		},
		success : function(data) {
			if (data.code == '100000') {
				layer.msg("转移成功");
				doSearch();
				closeMixKz();
			} else {
				layer.msg(data.msg);
			}
		}
	});
}

/* 导入 */
function importInfoList() {
	layer.msg("该功能暂未开放");
}

/* 导出 */
function exportInfoList() {
	var type = $("#class_type").val();
	var staffRole = $("#role_select").val();// dscj,dsyy,zjsyy,msjd：当前用户角色：电商采集，电商邀约，转介绍邀约，门市----根据权限获取
	var timeType = $("#time_select").val();// 查询的时间类型（提报时间，更新时间，入店时间，追踪时间，婚期。。。）
	var timeStart = $("#start_time").val();// 时间起始
	var timeEnd = $("#end_time").val();// 时间截止
	var filterType = "";// 过滤条件类型
	var filterKey = "";// 过滤条件关键字
	var sourceId = getCheckedBoxSourceIds("ids");// 渠道
	var shopId = "";// 门店
	var searchStaffId = getCheckedBoxStaffIds("ids");// 要查询的职员ID
	var url = "../client/excel_export?search_type=" + type + "&staff_role="
			+ staffRole + "&time_type=" + timeType + "&start=" + timeStart
			+ "&end=" + timeEnd + "&filter_type=" + filterType + "&filter_key="
			+ filterKey + "&source_id=" + sourceId + "&shop_id=" + shopId
			+ "&search_staff_id=" + searchStaffId;
	window.location.href = url;
}

// 获取地址字符串
function getAddressStr(type) {

	var prov = "";
	var city = "";
	var dist = "";
	var detail = "";

	if (type == "edit") {
		prov = $("#info_address_prov").val();
		city = $("#info_address_city").val();
		dist = $("#info_address_dist").val();
		detail = $("#info_address_detail").val();
	} else if (type == "detail") {
		prov = $("#det_address_prov").val();
		city = $("#det_address_city").val();
		dist = $("#det_address_dist").val();
		detail = $("#det_address_detail").val();
	} else if (type == "add") {
		prov = $("#info_address_prov").val();
		city = $("#info_address_city").val();
		dist = $("#info_address_dist").val();
		detail = $("#info_address_detail").val();
	}

	if (!prov) {
		return "";
	}
	return prov + "," + city + "," + dist + "," + detail;
}

// 根据手机号码 获取手机归属地
function getPhoneAddress() {
	var phone = $("#add_phone").val();
	phone = "18703882312";
	if (!phone || phone.length != 11) {
		return;
	}
}