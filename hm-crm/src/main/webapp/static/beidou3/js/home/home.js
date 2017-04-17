$(function() {
	doReSize();// 拖动改变客资表格列宽
	var LinkSelect = function() {
	};
	var TableForm = new KouBeiTableForm($('.kb-table-form'), {});
	TableForm.AddSelect($('.custom-class-select'));
	TableForm.AddSelect($('.addgroup-class-select'));
	TableForm.Start();

	var staffRole = $("#role_select").val();// 用户角色
	changeSrcList(staffRole);// 渠道切换
	changeStaffList(staffRole);// 人员切换

	getContentData('new', '', '0');
	getContentData('trace', '', '0');
	getContentData('order', '', '0');
	getContentData('come', '', '0');
	getContentData('success', '', '0');
	getContentData('invalid', '', '0');

	// 页面加载完毕执行
	getClientInfo("all", "0");// 查询当前所有客资
});
/** -- 更改页面渠道 --* */
function changeSrcList(roleType) {

	if (!roleType) {
		return;
	}

	$
			.ajax({
				url : '../client/get_sources',
				type : "POST",
				data : {
					role_type : roleType
				},
				success : function(data) {
					if (data.code == '100000') {
						var sourcestr = '';
						var data_sourcestr = '';
						sourcestr += '<div class="more_select_li" data-title="全部渠道" style="font-weight: 700;">';
						sourcestr += '<span class="checkonebox_0"><input attr-title="0" attr-name="全部渠道" name="source_box" checked="checked" class="checkone_0" onclick="sourceCheck(\'0\',\'全部渠道\')" type="checkbox" value="2" style="width:14px;"/></span>';
						sourcestr += '<span>全部渠道</span>';
						sourcestr += '</div>';
						data_sourcestr += '<div class="data_more_select_li" data-title="全部渠道" style="font-weight: 700;">';
						data_sourcestr += '<span class="data_checkbox_0"><input attr-title="0" attr-name="全部渠道" name="data_source_box" checked="checked" class="data_checkone_0" onclick="dataSourceCheck(\'0\',\'全部渠道\')" type="checkbox" value="2" style="width:14px;"/></span>';
						data_sourcestr += '<span>全部渠道</span>';
						data_sourcestr += '</div>';
						if (data.sources.length > 0) {
							$
									.each(
											data.sources,
											function(k, v) {
												sourcestr += '<div class="more_select_li" data-title="'
														+ v['srcName'] + '">';
												sourcestr += '<span class="checkonebox_'
														+ v['srcId']
														+ '"><input attr-title='
														+ v['srcId']
														+ ' attr-name='
														+ v['srcName']
														+ ' name="source_box" checked="checked" class="checkone_'
														+ v['srcId']
														+ '" onclick="sourceCheck(\''
														+ v['srcId']
														+ '\',\''
														+ v['srcName']
														+ '\')" type="checkbox" value="2" style="width:14px;"/></span>';
												sourcestr += '<span>'
														+ v['srcName']
														+ '</span>';
												sourcestr += '</div>';
												
												data_sourcestr += '<div class="data_more_select_li" data-title="'
													+ v['srcName'] + '">';
											data_sourcestr += '<span class="data_checkonebox_'
													+ v['srcId']
													+ '"><input attr-title='
													+ v['srcId']
													+ ' attr-name='
													+ v['srcName']
													+ ' name="data_source_box" checked="checked" class="data_checkone_'
													+ v['srcId']
													+ '" onclick="dataSourceCheck(\''
													+ v['srcId']
													+ '\',\''
													+ v['srcName']
													+ '\')" type="checkbox" value="2" style="width:14px;"/></span>';
											data_sourcestr += '<span>'
													+ v['srcName']
													+ '</span>';
											data_sourcestr += '</div>';
											})
						}
						$(".allsource .more_select_box").empty().append(sourcestr);
						$(".data_allsource .data_more_select_box").empty().append(data_sourcestr);
					}
				}
			});
}
/** -- 更改显示人员 --* */
function changeStaffList(roleType) {
	if (!roleType) {
		return;
	}

	var userId = $("#main_userid").val();
	var userName = $("#main_username").val();

	$
			.ajax({
				url : '../client/get_staffs',
				type : "POST",
				data : {
					role_type : roleType
				},
				success : function(data) {
					if (data.code == '100000') {
						var staffsstr = '';
						staffsstr += '<div class="more_select_li" data-title="全部人员" style="font-weight: 700;">';
						staffsstr += '<span class="checkstaffbox_0"><input attr-title="0" attr-name="全部人员" name="staff_box "';

						staffsstr += ' class="checkstaff_0" onclick="staffCheck(\'0\',\'全部人员\')" type="checkbox" value="2" checked="checked" style="width:14px;"/></span><span>全部人员</span>';

						staffsstr += '</div>';

						var remove_to = '';/* 转移客资弹框里面的人员 */
						if (data.staffs.length > 0) {
							$
									.each(
											data.staffs,
											function(k, v) {

												staffsstr += '<div class="more_select_li" data-title="'
														+ v['name'] + '">';
												staffsstr += '<span class="checkstaffbox_'
														+ v['id']
														+ '"><input attr-title="'
														+ v['id']
														+ '" attr-name="'
														+ v['name'] + '"';
												staffsstr += "checked='checked'";

												staffsstr += ' name="staff_box" class="checkstaff_'
														+ v['id']
														+ '" onclick="staffCheck(\''
														+ v['id']
														+ '\',\''
														+ v['name']
														+ '\')" type="checkbox" value="2" style="width:14px;"/></span>';
												staffsstr += '<span>'
														+ v['name'] + '</span>';
												staffsstr += '</div>';

												remove_to += '<option value="'
														+ v['id'] + '">'
														+ v['name']
														+ '</option>';
											})

						}
						$(".allstaff .more_select_box").empty().append(staffsstr);
						
						$("#remove_to_id").empty().append(remove_to);
					}
				}
			});
}
/** -- 搜索按钮 --* */
function doSearch() {
	$(".select_head_sec_title").text("客资中心");
	$(".search_input").removeClass("hide");// 显示搜索框
	var searchType = $("#class_type").val();
	getContentData(searchType, '', '0');
	/* 由大数据切换到客资列表页面，头部的2个select中都有调用doSearch()方法，因此直接写在doSearch()方法里面 */
	if ($(".info_main_box").hasClass("hide"))
		$(".info_main_box").removeClass("hide");
	if (!$(".bigdata").hasClass("hide"))
		$(".bigdata").addClass("hide");
}
/** -- 显示客资详情页 --* */
function toDetail(kzid) {
	if (!kzid) {
		return;
	}
	var staffRole = $("#role_select").val();// 用户角色
	var url = "../client/detail?kzid=" + kzid + "&role=" + staffRole;
	showDialog(url);
}
/** -- 查询时间方式切换 --* */
function selectTimeTo(type) {
	$("#time_select").val(type);
	var searchType = $("#class_type").val();
	getContentData(searchType, '', '0');
}
/** -- 切换客资分类tab --* */
function changeSelectValue(classname) {
	var flag = '1';
	if ($("." + classname).hasClass("hide")) {
		flag = '2';
	}
	$(".select_head_select").addClass('hide');
	if (flag == '2') {
		$("." + classname).removeClass("hide");
	}
}
/** -- 选择性别 --* */
function showSexSelect() {
	if ($(".sex_all_box").hasClass('hide')) {
		$(".sex_all_box").removeClass('hide');
	} else {
		$(".sex_all_box").addClass('hide');
	}
}
/** -- 选中性别 --* */
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
/** -- 推广渠道、邀约客服、转介绍提报、门店门市、财务收银切换 --* */
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
}
/** -- 基本信息、详细信息、日志 --* */
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
/** -- 客资tab切换数据集 --* */
function getClientInfo(nav, i) {

	$("#class_type").val(nav);

	// 获取数据
	getContentData(nav, '', '0');

	// 切换CSS样式
	var arrayli = $(".table_tab_ul li");
	$.each(arrayli, function(k, v) {
		if (k == i) {
			if (v['className'] != 'table_active')
				v['className'] = "table_active";
		} else {
			if (v['className'] == 'table_active')
				v['className'] = '';
		}
	})
	var arraytable = $(".conte_table");
	$.each(arraytable, function(k, v) {
		if (v.className.indexOf("hide") <= 0) {
			$(this).addClass('hide');
		}
	})
	$(".conte_table_" + nav).removeClass("hide");
}

/** -- 获取客资列表 --* */
function getContentData(type, sort, flag, searchKey) {
	// 显示loading
	reflashReload();
	// type:查询类型--all/所有客资;new/新客资;trace/待跟踪;order/已预约;come/到店;success/已定单;invalid/无效。
	// sort:排序字段
	// flag:是否是分页点击--1/是;0/否。
	// searchKey--模糊查询关键字

	if (!searchKey) {
		searchKey = "";
		$("#search_key").val("");
	}

	var staffRole = $("#role_select").val();// dscj,dsyy,zjsyy,msjd：当前用户角色：电商采集，电商邀约，转介绍邀约，门市----根据权限获取
	var page = $("#current_page").html();// 当前页
	var size = $("#page_size").val();// 分页大小
	var timeType = $("#time_select").val();// 查询的时间类型（提报时间，更新时间，入店时间，追踪时间，婚期。。。）
	var timeStart = $("#start_time").val();// 时间起始
	var timeEnd = $("#end_time").val();// 时间截止
	var filterType = "";// 过滤条件类型
	var filterKey = "";// 过滤条件关键字
	var sortCode = $("#sort_code").val();// 排序升降标识0-1
	var sourceId = getCheckedBoxSourceIds("ids");// 渠道
	var shopId = "";// 门店
	var searchStaffId = getCheckedBoxStaffIds("ids");// 要查询的职员ID

	if (isNaN(page)) {
		page = "";
	}
	if (isNaN(size)) {
		size = "";
	}

	$.ajax({
		url : '../client/kz_query',
		type : "POST",
		data : {
			search_key : searchKey,
			search_type : type,
			staff_role : staffRole,
			page : page,
			size : size,
			time_type : timeType,
			start : timeStart,
			end : timeEnd,
			filter_type : filterType,
			filter_key : filterKey,
			sortcode : sortCode,
			flag : flag,
			sort : sort,
			source_id : sourceId,
			shop_id : shopId,
			search_staff_id : searchStaffId
		},
		success : function(data) {
			if (data.code == '100000') {
				$('#sort_code').val(data.sortCode);
				tableDataCheckedNo();
				// 追加客资列表
				appendInfoList(type, data);
				// 取消loading
				reflashCancle();
				// 分页数据结果展示
				$(".totalpage").html(data.pageInfo.totalpage);// 总页数
				$(".totalcount").html(data.pageInfo.totalcount);// 总条数
				$(".currentpage").html(data.pageInfo.currentpage);// 当前页码

			}
		}
	});
}

/*-- 追加客资列表 --*/
function appendInfoList(type, data) {
	if (type == 'all') {
		$('#data_content_all').empty();
		$('#data_content_all').append(data.content);
		$('#all_num').html("(" + data.pageInfo.totalcount + ")");
	} else if (type == 'new') {
		$('#data_content_new').empty();
		$('#data_content_new').append(data.content);
		$('#new_num').html("(" + data.pageInfo.totalcount + ")");
	} else if (type == 'trace') {
		$('#data_content_trace').empty();
		$('#data_content_trace').append(data.content);
		$('#trace_num').html("(" + data.pageInfo.totalcount + ")");
	} else if (type == 'order') {
		$('#data_content_order').empty();
		$('#data_content_order').append(data.content);
		$('#order_num').html("(" + data.pageInfo.totalcount + ")");
	} else if (type == 'come') {
		$('#data_content_come').empty();
		$('#data_content_come').append(data.content);
		$('#come_num').html("(" + data.pageInfo.totalcount + ")");
	} else if (type == 'success') {
		$('#data_content_success').empty();
		$('#data_content_success').append(data.content);
		$('#success_num').html("(" + data.pageInfo.totalcount + ")");
	} else if (type == 'invalid') {
		$('#data_content_invalid').empty();
		$('#data_content_invalid').append(data.content);
		$('#invalid_num').html("(" + data.pageInfo.totalcount + ")");
	}
}

function checkAllId(type) {
	var checkbox = $('#check_all_' + type);
	checkbox.val() == 1 ? checkbox.val('2') : checkbox.val('1');
	var checkboxs = document.getElementsByName('box_' + type);
	for (var i = 0; i < checkboxs.length; i++) {
		if (checkbox.val() == 1) {
			checkboxs[i].checked = false;// 全不选
			checkboxs[i].value = '1';
			checkbox.attr("checked", false);
		} else {
			checkboxs[i].checked = true;// 全选
			checkboxs[i].value = '2';
			checkbox.attr("checked", true);
		}
		/*
		 * checkboxs[i].checked?checkboxs[i].checked=false:checkboxs[i].checked=true;//反选
		 */}
}
function checkId(type, id) {
	var obj = $("#data_content_" + type + " .info_id" + id).val();
	if (obj == 1) {
		$("#data_content_" + type + " .info_id" + id).attr("checked", true);
		$("#data_content_" + type + " .info_id" + id).val('2');
	} else {
		console.log(obj);
		$("#data_content_" + type + " .info_id" + id).attr("checked", false);
		$("#data_content_" + type + " .info_id" + id).val('1');
		if ($("#check_all_" + type).val() == '2') {
			$("#check_all_" + type).val('1');
			$("#check_all_" + type).attr("checked", false);
		}
	}
}

/* 新增客资 */
function addNewkz() {
	$("#add_name").val('');
	$("#add_phone").val('');
	$("#add_wechat").val('');
	$("#add_qq").val('');
	$("#now_time").html(getNow());
	if ($(".addnew_box").hasClass("hide")) {
		$(".addnew_box").removeClass("hide").animate({
			"top" : "50%"
		});
	}
	$(".bg000").css("display", "block").attr("onclick", "closeAddNew()");
}
/* 关闭新增客资弹框 */
function closeAddNew() {
	if (!$(".addnew_box").hasClass("hide")) {
		$(".addnew_box").animate({
			"top" : "-100%"
		}, function() {
			$(".addnew_box").fadeOut();
			$(".addnew_box").addClass("hide"); // 等动画结束了吧菜单隐藏，不至于有滚动条
		});
	}
	$(".bg000").css("display", "none").attr("onclick", "");
}
/* 选中渠道复选框 */
function sourceCheck(i, name) {
	if (i == 0) {
		var checkbox = $(".checkone_0");
		var checkval = checkbox.val();
		var checkboxs = document.getElementsByName('source_box');
		if (checkval == 2) {
			for (var i = 0; i < checkboxs.length; i++) {
				checkboxs[i].checked = false;// 全不选
				checkboxs[i].value = "1";
			}
			$(".source_checkbox").empty().text('');
		} else {
			for (var i = 0; i < checkboxs.length; i++) {
				checkboxs[i].checked = true;// 全选
				checkboxs[i].value = "2";
			}
			$(".source_checkbox").empty().text('全部渠道');
		}
	} else {
		var obj = $(".checkone_" + i);
		if (obj.val() == "2") {
			$(".checkone_0").val('1');
			$(".checkone_0").prop('checked', false);
			$(".checkonebox_" + i)
					.empty()
					.append(
							'<input attr-title="'
									+ i
									+ '" attr-name="'
									+ name
									+ '" name="source_box" class="checkone_'
									+ i
									+ '" onclick="sourceCheck(\''
									+ i
									+ '\',\''
									+ name
									+ '\')" type="checkbox" value="1" style="width:14px;"/>');
		} else {
			$(".checkonebox_" + i)
					.empty()
					.append(
							'<input attr-title="'
									+ i
									+ '" attr-name="'
									+ name
									+ '" name="source_box" class="checkone_'
									+ i
									+ '" onclick="sourceCheck(\''
									+ i
									+ '\',\''
									+ name
									+ '\')" type="checkbox" value="2" checked="true" style="width:14px;"/>');
		}
		var names = getCheckedBoxSourceIds('name');
		$(".source_checkbox").empty().text(names);
	}
	doSearch();// 加载数据
}

/* 所有渠道多选 */
function showAllSource() {
	if (!$(".source_checkbox").hasClass("open_checkbox")) {
		$(".source_checkbox").addClass("open_checkbox");
		$(".allsource").css("visibility", "visible").css("opacity", "1");
		$(".bgfff_shadow").removeClass('hide').attr('onclick',
				'hideSelectBox("source_checkbox","allsource")');
		$(".source_checkbox_icon").css({
			'opacity' : 1,
			'visibility' : 'visible',
			'transform' : 'rotate(180deg)'
		});
	} else {
		$(".source_checkbox").removeClass("open_checkbox");
		$(".allsource").css("visibility", "hidden").css("opacity", "0");
		$(".bgfff_shadow").addClass('hide').attr('onclick', '');
		$(".source_checkbox_icon").css({
			'opacity' : 1,
			'visibility' : 'visible',
			'transform' : 'rotate(0deg)'
		});
	}
	doSearch();// 加载数据
}
/* 选中人员复选框 */
function staffCheck(i, name) {
	var staff = getCheckedBoxStaffIds();
	if (i == 0) {
		var checkbox = $(".checkstaff_0");
		var checkval = checkbox.val();
		var checkboxs = document.getElementsByName('staff_box');
		if (checkval == 2) {
			for (var i = 0; i < checkboxs.length; i++) {
				checkboxs[i].checked = false;// 全不选
				$(".checkstaff_0").val('1');
				checkboxs[i].value = "1";
			}
			$(".staff_checkbox").text('');
		} else {
			for (var i = 0; i < checkboxs.length; i++) {
				$(".checkstaff_0").val('2');
				checkboxs[i].checked = true;// 全选
				checkboxs[i].value = "2";
			}
			$(".staff_checkbox").empty().text('全部人员');
		}
	} else {
		var obj = $(".checkstaff_" + i);
		if (obj.val() == "2") {
			$(".checkstaff_0").val('1');
			$(".checkstaff_0").prop('checked', false);
			$(".checkstaffbox_" + i)
					.empty()
					.append(
							'<input attr-title="'
									+ i
									+ '" attr-name="'
									+ name
									+ '" name="staff_box" class="checkstaff_'
									+ i
									+ '" onclick="staffCheck(\''
									+ i
									+ '\',\''
									+ name
									+ '\')" type="checkbox" value="1" style="width:14px;"/>');
		} else {
			$(".checkstaffbox_" + i)
					.empty()
					.append(
							'<input attr-title="'
									+ i
									+ '" attr-name="'
									+ name
									+ '" name="staff_box" class="checkstaff_'
									+ i
									+ '" onclick="staffCheck(\''
									+ i
									+ '\',\''
									+ name
									+ '\')" type="checkbox" value="2" checked="true" style="width:14px;"/>');
		}
		var names = getCheckedBoxStaffIds('name');
		$(".staff_checkbox").empty().text(names);
	}
	doSearch();// 加载数据
}
/* 所有人员复选 */
function showAllStaff() {
	if (!$(".staff_checkbox").hasClass("open_checkbox")) {
		$(".staff_checkbox").addClass("open_checkbox");
		$(".allstaff").css("visibility", "visible").css("opacity", "1");
		$(".bgfff_shadow").removeClass('hide').attr('onclick',
				'hideSelectBox("staff_checkbox","allstaff")');
		$(".staff_checkbox_icon").css({
			'opacity' : 1,
			'visibility' : 'visible',
			'transform' : 'rotate(180deg)'
		});
	} else {
		$(".staff_checkbox").removeClass("open_checkbox");
		$(".allstaff").css("visibility", "hidden").css("opacity", "0");
		$(".bgfff_shadow").addClass('hide').attr('onclick', '');
		$(".staff_checkbox_icon").css({
			'opacity' : 1,
			'visibility' : 'visible',
			'transform' : 'rotate(0deg)'
		});
	}
	doSearch();// 加载数据
}
function hideSelectBox(checkbox, all) {
	$("." + checkbox).removeClass("open_checkbox");
	$("." + all).css("visibility", "hidden").css("opacity", "0");
	if (!$(".bgfff_shadow").hasClass('hide'))
		$(".bgfff_shadow").addClass('hide').attr('onclick', '');
	$("." + checkbox + "_icon").css({
		'opacity' : 1,
		'visibility' : 'visible',
		'transform' : 'rotate(0deg)'
	});
}

/* 短信 */
function messCheckBox() {
	layer.msg("该功能暂未开放");
}

/* 获取table里面选中的客资id */
function getCheckedBoxIds() {
	var objchexked = $(".check_box");
	var valall = '';
	$.each(objchexked, function(k, v) {
		if (v['value'] == 2) {
			valall += v['attributes']['attr-title']['value'] + ",";
		}
	});
	console.log(valall);
	return valall;
}
/* 获取搜索条件中 复选渠道选中的渠道id */
function getCheckedBoxSourceIds(type) {
	/*
	 * type==ids,获取选中的渠道的id集合 type==name,获取选中渠道的name集合
	 */
	var checkboxs = document.getElementsByName('source_box');
	var valall = '';
	if ($(".checkone_0").val() == '2') {
		valall = '';
	} else {
		$.each(checkboxs, function(k, v) {
			if (v['value'] == 2) {
				if (v['value'] == 2) {
					if (type == "name") {
						valall += v['attributes']['attr-name']['value'] + ",";
					} else {
						valall += v['attributes']['attr-title']['value'] + ",";
					}

				}
			}
		});
	}

	return valall;
}
/* 获取搜索条件中 复选人员选中的人员id */
function getCheckedBoxStaffIds(type) {
	/*
	 * type==ids,获取选中的人员的id集合 type==name,获取选中人员的name集合
	 */
	var checkboxs = document.getElementsByName('staff_box');
	var valall = '';
	if ($(".checkstaff_0").val() == '2') {
		valall = '';
	} else {
		$.each(checkboxs, function(k, v) {
			if (v['value'] == 2) {
				if (type == "name") {
					valall += v['attributes']['attr-name']['value'] + ",";
				} else {
					valall += v['attributes']['attr-title']['value'] + ",";
				}

			}
		});
	}

	return valall;
}
/** -- 根据手机号自动获取地址并填充 --* */
function phoneChange() {
	var phone = $("#add_phone").val();
	if (!phone || phone.length != 11) {
		return;
	}
	$.ajax({
		url : '../client/get_city_by_phone',
		type : "POST",
		data : {
			phone : phone
		},
		success : function(data) {
			if (data.code == '100000') {
				var address = data.address;
				if (!address) {
					return;
				}
				var adsarr = address.split(",");
				// $("#info_address_prov").val(adsarr[0]);
				// $("#info_address_city").val(adsarr[1]);
			}
		}
	});
}
/* 弹出转移弹框 */
function showMixKz() {
	var kzIds = getCheckedBoxIds();
	if (!kzIds) {
		layer.msg("请选择要转移的客资");
		return;
	} else {
		if ($(".remove_box").hasClass("hide"))
			$(".remove_box").removeClass("hide").animate({
				"top" : "50%"
			});
		$(".bg000").css("display", "block").attr("onclick", "closeMixKz()");
	}

}
/* 关闭转移弹框 */
function closeMixKz() {
	$(".remove_box").animate({
		"top" : "-100%"
	}, function() {
		$(".remove_box").fadeOut();
		$(".remove_box").addClass("hide"); // 等动画结束了把菜单隐藏，不至于有滚动条
	});
	$(".bg000").css("display", "none").attr("onclick", "");
}
/* 搜索边上的下拉选择时间show */
function showSearchSelect() {
	if ($(".search_box_select").hasClass("hide")) {
		$(".search_box_select").removeClass("hide");
	}
	if ($(".search_r_select").hasClass("borBRrad6")) {
		$(".search_r_select").removeClass("borBRrad6");
	}
	if ($(".searchbtn").hasClass("borBLrad6")) {
		$(".searchbtn").removeClass("borBLrad6");
	}
}
/* 搜索边上的下拉选择时间hide */
function leaveSearchSelect() {
	if (!$(".search_box_select").hasClass("hide")) {
		$(".search_box_select").addClass("hide");
	}
	if (!$(".search_r_select").hasClass("borBRrad6")) {
		$(".search_r_select").addClass("borBRrad6");
	}
	if (!$(".searchbtn").hasClass("borBLrad6")) {
		$(".searchbtn").addClass("borBLrad6");
	}
}
/* 选择时间获取数据 */
function getTimeData(type) {
	var start = "";
	var end = "";
	if ("0" == type) {
		// 本周
		start = getFirstYmdThisWeek();
		end = getLastYmdThisWeek();
	} else if ("1" == type) {
		// 上周
		start = getLastWeekFistDay();
		end = getLastWeekLastDay();
	} else if ("2" == type) {
		// 本月
		start = getYmdMonthFirst();
		end = getYmdMonthLast();
	} else if ("3" == type) {
		// 上月
		start = getLastYmdMonthFirst();
		end = getLastYmdMonthEnd();
	} else if ("4" == type) {
		// 今日
		start = getYmdToday();
		end = start;
	} else if ("5" == type) {
		start = getYesterDay();
		end = start;
	}
	$("#start_time").val(start);
	$("#end_time").val(end);
	doSearch();
	leaveSearchSelect();
}

// 页码回车键搜索
function pageNoKeyDown() {
	if (event.keyCode == 13) {
		toLastPage("2");
	}
}

/* 分页里面的上一页、下一页、go */
function toLastPage(type) {
	var currentPage = $("#current_page").html();
	if ("0" == type) {
		// 上一页
		if (Number(currentPage) < 2) {
			layer.msg("已经是第一页了");
			return;
		}
		$("#current_page").html(Number(currentPage) - 1);
	} else if ("1" == type) {
		// 下一页
		var totalPage = $(".totalpage").html();// 总页数
		if (Number(currentPage) >= Number(totalPage)) {
			layer.msg("已经是最后一页了");
			return;
		}
		$("#current_page").html(Number(currentPage) + 1);
	} else if ("2" == type) {
		// GO
		var totalPage = $(".totalpage").html();// 总页数
		var pageNum = $("#page_no_go").val();// 要跳转的页数
		if (isNaN(pageNum) || Number(pageNum) < 1) {
			layer.msg("请输入正确的页码");
			$("#page_no_go").val(1);
			return;
		}
		if (Number(pageNum) > Number(totalPage)) {
			$("#page_no_go").val(Number(totalPage));
			$("#current_page").html(Number(totalPage));
		} else {
			$("#current_page").html(Number(pageNum));
		}
	}

	doSearch();
}
/* table里面的所有数据为非选择状态 */
function tableDataCheckedNo() {
	var checkbox = $('#check_all_all');
	checkbox.val('1');
	checkbox.attr("checked", false);
	var checkboxs = document.getElementsByName('box_all');
	for (var i = 0; i < checkboxs.length; i++) {
		checkboxs[i].checked = false;// 全不选
		checkboxs[i].value = '1';
	}
	var checkbox = $('#check_all_new');
	checkbox.val('1');
	checkbox.attr("checked", false);
	var checkboxs = document.getElementsByName('box_new');
	for (var i = 0; i < checkboxs.length; i++) {
		checkboxs[i].checked = false;// 全不选
		checkboxs[i].value = '1';
	}

	var checkbox = $('#check_all_trace');
	checkbox.val('1');
	checkbox.attr("checked", false);
	var checkboxs = document.getElementsByName('box_trace');
	for (var i = 0; i < checkboxs.length; i++) {
		checkboxs[i].checked = false;// 全不选
		checkboxs[i].value = '1';
	}
	var checkbox = $('#check_all_order');
	checkbox.val('1');
	checkbox.attr("checked", false);
	var checkboxs = document.getElementsByName('box_order');
	for (var i = 0; i < checkboxs.length; i++) {
		checkboxs[i].checked = false;// 全不选
		checkboxs[i].value = '1';
	}
	var checkbox = $('#check_all_come');
	checkbox.val('1');
	checkbox.attr("checked", false);
	var checkboxs = document.getElementsByName('box_come');
	for (var i = 0; i < checkboxs.length; i++) {
		checkboxs[i].checked = false;// 全不选
		checkboxs[i].value = '1';
	}
	var checkbox = $('#check_all_success');
	checkbox.val('1');
	checkbox.attr("checked", false);
	var checkboxs = document.getElementsByName('box_success');
	for (var i = 0; i < checkboxs.length; i++) {
		checkboxs[i].checked = false;// 全不选
		checkboxs[i].value = '1';
	}
	var checkbox = $('#check_all_invalid');
	checkbox.val('1');
	checkbox.attr("checked", false);
	var checkboxs = document.getElementsByName('box_invalid');
	for (var i = 0; i < checkboxs.length; i++) {
		checkboxs[i].checked = false;// 全不选
		checkboxs[i].value = '1';
	}
}