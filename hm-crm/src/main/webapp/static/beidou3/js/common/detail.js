$(function() {
	var address_hidden = $("#address_hidden").val();
	var sex_hidden = $("#sex_hidden").val();
	toSelectSex(sex_hidden);
	var addArray = address_hidden.split(",");
	toSelectAddress(addArray[0], addArray[1], addArray[2], addArray[3]);
	var role = GetQueryString('role');
	$("#detail_show_box").val(role);
	if (role == "dscj") {/* 推广渠道 */
		changeContentNavTab('tgqd');
	} else if ((role == "dsyy") || (role == "zjsyy")) {/* 邀约客服 */
		changeContentNavTab('yykf');
	} else if (role == "msjd") {/* 门市接待 */
		changeContentNavTab('mdms');
	} else if (role == "cwzy") {/* 财务收银 */
		changeContentNavTab('cwsy');
	} else {
		changeContentNavTab('tgqd');
	}

})
function closeSonWindow() {
	window.close();
}
/* 选择性别 */
function showSexSelect() {
	if ($(".sex_all_box").hasClass('hide')) {
		$(".sex_all_box").removeClass('hide');
	} else {
		$(".sex_all_box").addClass('hide');
	}
}
/* 选中性别 */
function toSelectSex(type) {
	var str = '';
	if (type == '0') {
		str = '<i attr-value class="user_sex_icon fa fa-male icon_sex icon_sex_male" style="text-align: center;"></i>';
	} else if (type == '1') {
		str = '<i attr-value class="user_sex_icon fa fa-female icon_sex icon_sex_female" style="text-align: center;"></i>';
	} else {
		str = '<i attr-value class="user_sex_icon fa fa-question icon_sex icon_sex_no" style="text-align: center;"></i>';
	}
	$(".sex_icon_into").empty().append(str);
	$("#info_sex").val(type);
	$("#add_sex").val(type);
}
/* 选中省市区 */
function toSelectAddress(prov, city, dist, detatl) {
	if (prov && (city == '')) {
		$("#select_address").citySelect({
			prov : prov,
			nodata : "none"
		});
		$("#detail_selectaddress").citySelect({
			prov : prov,
			nodata : "none"
		});
	} else if (prov && city && (dist == '')) {
		$("#select_address").citySelect({
			prov : prov,
			city : city,
			nodata : "none"
		});
		$("#detail_selectaddress").citySelect({
			prov : prov,
			city : city,
			nodata : "none"
		});
	} else if (prov && city && dist) {
		$("#select_address").citySelect({
			prov : prov,
			city : city,
			dist : dist,
			nodata : "none"
		});
		$("#detail_selectaddress").citySelect({
			prov : prov,
			city : city,
			dist : dist,
			nodata : "none"
		});
	} else {
		$("#select_address").citySelect({
			nodata : "none",
			required : false
		});
		$("#detail_selectaddress").citySelect({
			nodata : "none",
			required : false
		});
	}
	if (detatl) {
		$("#info_address_detail").val(detatl);
		$("#det_address_detail").val(detatl);
	}
}
/* 推广渠道、邀约客服、转介绍提报、门店门市、财务收银切换 */
function changeContentNavTab(icon) {
	var arraydiv = $(".tgqd_yykf_zjstb_mdms_cwsy");
	var arraybox = $(".tgqd_yykf_zjstb_mdms_cwsy_box");
	$.each(arraydiv, function(k, v) {
		if (v.className.indexOf("tab_active") > 0) {
			$(this).removeClass('tab_active');
		}
	})
	$.each(arraybox, function(k, v) {
		if (v.className.indexOf("hide") <= 0) {
			$(this).addClass('hide');
		}
	})
	$("." + icon).addClass("tab_active");
	$("." + icon + '_box').removeClass("hide");
	var role = $("#detail_show_box").val();
	$(".save_btn_detail").attr("onclick", "doSaveEditInfo('" + icon + "')");
}
/* 基本信息、详细信息、日志 */
function changeNavTableHead(icon) {
	var arraydiv = $(".jbxx_xxxx_xgrz");
	var arraybox = $(".jbxx_xxxx_xgrz_box");
	$.each(arraydiv, function(k, v) {
		if (v.className.indexOf("tab_active") > 0) {
			$(this).removeClass('tab_active');
		}
	})
	$.each(arraybox, function(k, v) {
		if (v.className.indexOf("hide") <= 0) {
			$(this).addClass('hide');
		}
	})
	$("." + icon).addClass("tab_active");
	$("." + icon + '_box').removeClass("hide");
}
/* 获取url制定参数的值 */

function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}

/* 保存数据 */
function doSaveEditInfo(type) {
	if ("tgqd" == type) {
		doEditInfoBase();
	} else if ("yykf" == type) {
		saveInviteRst();
	} else if ("zjstb" == type) {
		alert("保存转介绍信息");
	} else if ("mdms" == type) {
		saveReceptLog();
	} else if ("cwsy" == type) {
		alert("保存财务信息");
	}
}