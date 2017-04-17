/** -- 邀约结果js --* */
/*-- 获取短信预览 --*/
function getSmsPreview() {
	var kzId = $("#info_kzid").val();// 客资ID
	var phone = $("#info_phone").val();// 手机号
	var shopId = $("#invite_shop_id").val();
	var yyTime = $("#invite_yy_time").val();
	if (!kzId) {
		alert("客资信息获取失败，请刷新重试");
		return;
	}
	if (!shopId || "0" == shopId) {
		alert("请选择预约门店");
		return;
	}
	if (!yyTime) {
		alert("请选择预约时间");
		return;
	}

	$.ajax({
		url : '../client/msg_preview',
		type : "POST",
		data : {
			kzid : kzId,
			shopid : shopId,
			yytime : yyTime,
			phone : phone
		},
		success : function(data) {
			if (data.code == '100000') {
				layer.open({
					title : '短信预览',
					content : data.sms,
					btn : [ '确定', '取消' ],
					yes : function(index, layero) {
						alert('确定');
					}
				})
			} else {
				// TODO 获取短信预览失败
				alert(data.msg);
			}
		}
	});
}

/*-- 发送预约短信 --*/
function sendYySms() {
	var kzId = $("#info_kzid").val();// 客资ID
	var phone = $("#info_phone").val();// 手机号
	var shopId = $("#invite_shop_id").val();
	var yyTime = $("#invite_yy_time").val();
	if (!kzId) {
		alert("客资信息获取失败，请刷新重试");
		return;
	}
	if (!shopId || "0" == shopId) {
		alert("请选择预约门店");
		return;
	}
	if (!yyTime) {
		alert("请选择预约时间");
		return;
	}

	$.ajax({
		url : '../client/send_msg',
		type : "POST",
		data : {
			kzid : kzId,
			shopid : shopId,
			yytime : yyTime,
			phone : phone
		},
		success : function(data) {
			if (data.code == '100000') {
				alert("预约短信发送成功");
			} else {
				// TODO 发送短信失败
				alert(data.msg);
			}
		}
	});
}

// 保存客服邀约记录
function saveInviteRst() {
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

	if (!kzId) {
		layer.msg("客资信息获取错误，请刷新后重试");
		return;
	}
	if (!name) {
		layer.msg("客资姓名不能为空");
		return;
	}
	if (!phone && !wechat && !qq) {
		layer.msg("客资联系方式不能为空");
		return;
	}

	// 获取邀约结果
	var yyCode = $("input[name='yy_rst']:checked").val();
	if ("-1" == yyCode) {
		yyCode = $("#yy_rst_other").val();
	}

	var invalidRsnCode = "";// 无效原因编码
	var invalidRsnLabel = "";// 无效原因文本
	var invalidMemo = "";// 无效备注
	var traceTime = "";// 追踪时间
	var shopId = "";// 预约门店ID
	var shopName = "";// 预约门店名称
	var yyTime = "";// 预约时间
	if ("5" == yyCode) {
		// 无效
		invalidRsnCode = $("#yy_invalid_reason").val();
		invalidRsnLabel = $("#yy_invalid_reason").find("option:selected")
				.text();
		invalidMemo = $("#yy_invalid_memo").val();
		if (!invalidRsnCode) {
			layer.msg("请选择无效原因");
			return;
		}
		if (!invalidRsnLabel) {
			layer.msg("无效原因获取失败，请刷新后重试");
			return;
		}
	} else if ("6" == yyCode) {
		// 下次追踪
		traceTime = $("#invite_trace_time").val();
		if (!traceTime) {
			layer.msg("请选择追踪时间");
			return;
		}
	} else if ("3" == yyCode) {
		// 预约到店
		shopId = $("#invite_shop_id").val();
		shopName = $("#invite_shop_id").find("option:selected").text();
		yyTime = $("#invite_yy_time").val();
		if (!shopId) {
			layer.msg("请选择预约门店");
			return;
		}
		if (!shopName) {
			layer.msg("未获取到预约门店信息,请刷新后重试");
			return;
		}
		if (!yyTime) {
			layer.msg("请选择预约时间");
			return;
		}
	}

	var yyMemo = $("#yy_memo").val();

	if (!yyCode) {
		layer.msg("请选择邀约结果");
		return;
	}

	$.ajax({
		url : '../client/invite',
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
			yy_rst : yyCode,
			invalid_code : invalidRsnCode,
			invalid_label : invalidRsnLabel,
			invalid_memo : invalidMemo,
			trace_time : traceTime,
			shopid : shopId,
			shopname : shopName,
			yy_time : yyTime,
			yy_memo : yyMemo
		},
		success : function(data) {
			if (data.code == '100000') {
				layer.msg("保存邀约记录成功");
				// TODO 追加
			} else {
				layer.msg(data.msg);
			}
		}
	});
}

/** -- 保存门市接待记录 --* */
function saveReceptLog() {
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

	if (!kzId) {
		layer.msg("客资信息获取错误，请刷新后重试");
		return;
	}
	if (!name) {
		layer.msg("客资姓名不能为空");
		return;
	}
	if (!phone && !wechat && !qq) {
		layer.msg("客资联系方式不能为空");
		return;
	}

	var jdRst = $("input[name='jd_rst']:checked").val();// 接待结果

	if (!jdRst) {
		layer.msg("请选择客资接待结果");
		return;
	}

	var shopId = "";// 门店ID
	var shopName = "";// 门店名称
	var recepterId = "";// 接待门市ID
	var recepterName = "";// 接待门市名称
	var comeShopTime = "";// 到店时间
	var runOffCode = "";// 流失原因编码
	var runOffLabel = "";// 流失原因文本
	var appointTime = "";// 预约下次到店时间
	var jdMemo = "";// 接待备注
	var amount = "";// 成交金额

	if ("10" == jdRst) {
		// 到店流失
		shopId = $("#mdjd_come_shop_id").val();// 门店ID
		shopName = $("#mdjd_come_shop_id").find("option:selected").text();// 门店名称
		recepterId = $("#mdjd_come_receptor_id").val();// 接待门市ID
		recepterName = $("#mdjd_come_receptor_id").find("option:selected")
				.text();// 接待门市名称
		comeShopTime = $("#jd_come_shop_time").val();// 到店时间
		runOffCode = $("#jd_runoff_reason").val();// 流失原因编码
		runOffLabel = $("#jd_runoff_reason").find("option:selected").text();// 流失原因文本
		appointTime = $("#jd_appoint_time").val();// 下次预约时间

		if (!shopId) {
			layer.msg("请选择接待门店");
			return;
		}
		if (!shopName) {
			layer.msg("未获取到门店信息,请刷新后重试");
			return;
		}
		if (!recepterId) {
			layer.msg("请选择接待门市");
			return;
		}
		if (!recepterName) {
			layer.msg("未获取到门市信息,请刷新后重试");
			return;
		}
		if (!comeShopTime) {
			layer.msg("请选择客人到店时间");
			return;
		}
		if (!runOffCode) {
			layer.msg("请选择到店未定原因");
			return;
		}
		if (!runOffLabel) {
			layer.msg("未获取到流失详情,请刷新后重试");
			return;
		}
	} else if ("9" == jdRst) {
		// 成交
		shopId = $("#mdjd_success_shop_id").val();// 门店ID
		shopName = $("#mdjd_success_shop_id").find("option:selected").text();// 门店名称
		recepterId = $("#mdjd_success_receptor_id").val();// 接待门市ID
		recepterName = $("#mdjd_success_receptor_id").find("option:selected")
				.text();// 接待门市名称
		comeShopTime = $("#jd_success_time").val();// 到店时间
		amount = $("#amount").val();// 成交金额
	}

	jdMemo = $("#jd_memo").val();

}