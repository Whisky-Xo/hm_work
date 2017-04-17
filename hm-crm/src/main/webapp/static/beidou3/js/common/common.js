$(function() {
	getAllNoNews();
	var role = $("#role_select").val();
	var type = $(".select_head_fir_title").text();
	toSelectChange(type, role);
})
/** -- 角色切换 --* */
function toSelectChange(type, role) {
	$(".select_head_fir_title").text(type);
	$("#role_select").val(role);// 切换角色标记属性
	$(".select_head_fir_select").addClass('hide');
	var str = '';
	if ("dscj" == role) {
		$(".search_input").removeClass("hide");//显示搜索框
		str += '<ul class="links">';
		str += '<li style="margin-top:5px;margin-bottom:0px;" onclick="doSearch();">客资中心</li>';
		str += '<li class="separator"></li>';
		str += '<li onclick="toSellManger();">消费管理</li>';
		str += '<li class="separator"></li>';
		str += '<li onclick="toBigData();">数据报表</li>';
		str += '</ul>';
		// 切换为电商客服
	} else if ("dsyy" == role) {
		$(".search_input").removeClass("hide");//显示搜索框
		str += '<ul class="links">';
		str += '<li style="margin-top:5px;margin-bottom:0px;" onclick="doSearch();">客资中心</li>';
		str += '<li class="separator"></li>';
		str += ' <li onclick="toBigData();">数据报表</li>';
		str += '</ul>';
		// 切换为电商邀约
	} else if ("zjsyy" == role) {
		// 切换为转介绍邀约
		$(".search_input").removeClass("hide");//显示搜索框
		str += '<ul class="links">';
		str += '<li style="margin-top:5px;margin-bottom:0px;" onclick="doSearch();">客资中心</li>';
		str += '<li class="separator"></li>';
		str += ' <li onclick="toBigData();">数据报表</li>';
		str += ' </ul>';
	} else if ("msjd" == role) {
		// 切换为门市
		$(".search_input").removeClass("hide");//显示搜索框
	}
	$(".sec_setting_box").empty().append(str);
	changeSrcList(role); // 渠道更换
	changeStaffList(role); // 人员更换
	doSearch();// 数据切换
	/*由大数据切换到客资列表页面，头部的2个select中都有调用doSearch()方法，因此直接写在doSearch()方法里面*/
	if($(".info_main_box").hasClass("hide"))$(".info_main_box").removeClass("hide");
	if(!$(".bigdata").hasClass("hide"))$(".bigdata").addClass("hide");
}
/** -- 角色切换第二联 --* */
function toSelectChangeSec(type) {
	$(".select_head_sec_title").text(type);
	$(".select_head_sec_title").addClass('hide');

	doSearch();
}
/*头部的2联下拉选择框，鼠标移除时隐藏下拉框*/
function leaveHideSelectValue(classname){
	if(!$("."+classname).hasClass('hide')){
		$("."+classname).addClass('hide');
	}
}
/** -- 拖动改变表格列宽 --* */
function doReSize() {
	$("#sample1").colResizable({
		liveDrag : true,
		gripInnerHtml : "<div class='grip'></div>",
		draggingClass : "dragging"
	});
}

/** -- 载入loading --* */
function reflashReload() {
	$(".bgff_reload").css("display", "block");
	var str_reload = '';
	str_reload = '<div id="bonfire-pageloader">';
	str_reload += '<h2 class="reload_word" style="text-align: center;color:#fff;position: absolute;width: 100%;top:70%;">加载中</h2>';
	str_reload += '<div class="bonfire-pageloader-icon" style="top:50%;left:50%;margin-left: -30px;margin-top: -40px;">';
	str_reload += '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"';
	str_reload += '	 width="512px" height="512px" viewBox="0 0 512 512" enable-background="new 0 0 512 512" xml:space="preserve">';
	str_reload += '<path id="loading-12-icon" d="M291,82.219c0,16.568-13.432,30-30,30s-30-13.432-30-30s13.432-30,30-30S291,65.65,291,82.219z';
	str_reload += '	 M261,404.781c-15.188,0-27.5,12.312-27.5,27.5s12.312,27.5,27.5,27.5s27.5-12.312,27.5-27.5S276.188,404.781,261,404.781z';
	str_reload += '	 M361.504,113.167c-4.142,7.173-13.314,9.631-20.487,5.489c-7.173-4.141-9.631-13.313-5.49-20.487';
	str_reload += '	c4.142-7.173,13.314-9.631,20.488-5.489C363.188,96.821,365.645,105.994,361.504,113.167z M188.484,382.851';
	str_reload += '	c-14.348-8.284-32.697-3.368-40.98,10.98c-8.285,14.349-3.367,32.696,10.98,40.981c14.35,8.283,32.697,3.367,40.98-10.981';
	str_reload += '	C207.75,409.482,202.834,391.135,188.484,382.851z M421.33,184.888c-8.368,4.831-19.07,1.965-23.901-6.404';
	str_reload += '	c-4.832-8.368-1.965-19.07,6.404-23.902c8.368-4.831,19.069-1.964,23.9,6.405C432.566,169.354,429.699,180.056,421.33,184.888z';
	str_reload += '	 M135.399,329.767c-8.285-14.35-26.633-19.266-40.982-10.982c-14.348,8.285-19.264,26.633-10.979,40.982';
	str_reload += '	c8.284,14.348,26.632,19.264,40.981,10.98C138.767,362.462,143.683,344.114,135.399,329.767z M436.031,277.249';
	str_reload += '	c-11.044,0-20-8.953-20-19.999c0-11.045,8.955-20.001,20.001-20.001c11.044,0,19.999,8.955,19.999,20.002';
	str_reload += '	C456.031,268.295,447.078,277.249,436.031,277.249z M115.97,257.251c-0.001-16.57-13.433-30.001-30.001-30.002';
	str_reload += '	c-16.568,0.001-29.999,13.432-30,30.002c0.001,16.566,13.433,29.998,30.001,30C102.538,287.249,115.969,273.817,115.97,257.251z';
	str_reload += '	 M401.333,364.248c-10.759-6.212-14.446-19.97-8.234-30.73c6.212-10.759,19.971-14.446,30.731-8.233';
	str_reload += '	 c10.759,6.211,14.445,19.971,8.232,30.73C425.852,366.774,412.094,370.46,401.333,364.248z M135.398,184.736';
	str_reload += '	c8.285-14.352,3.368-32.698-10.98-40.983c-14.349-8.283-32.695-3.367-40.981,10.982c-8.282,14.348-3.366,32.696,10.981,40.981';
	str_reload += '	C108.768,204,127.115,199.082,135.398,184.736z M326.869,421.328c-6.902-11.953-2.807-27.242,9.148-34.145';
	str_reload += '	s27.243-2.806,34.146,9.149c6.902,11.954,2.806,27.243-9.15,34.145C349.059,437.381,333.771,433.284,326.869,421.328z';
	str_reload += '	 M188.482,131.649c14.352-8.286,19.266-26.633,10.982-40.982c-8.285-14.348-26.631-19.264-40.982-10.98';
	str_reload += '	c-14.346,8.285-19.264,26.633-10.98,40.982C155.787,135.017,174.137,139.932,188.482,131.649z"/>';
	str_reload += '</svg>';
	str_reload += '</div>';
	str_reload += '</div>';
	/* var flag=exist("bonfire-pageloader"); */
	if (document.getElementById("bonfire-pageloader") == undefined) {
		$("body").append(str_reload);
	} else {
		if ($(".bonfire-pageloader-icon").hasClass(
				'bonfire-pageloader-icon-hide'))
			$(".bonfire-pageloader-icon").removeClass(
					'bonfire-pageloader-icon-hide');
		if ($("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))
			$("#bonfire-pageloader").removeClass('bonfire-pageloader-hide');
		if ($("#bonfire-pageloader").hasClass('hide'))
			$("#bonfire-pageloader").removeClass('hide');
	}
}
/** -- 取消loading --* */
function reflashCancle() {
	if (!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))
		$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
	if (!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))
		$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
	if (!$("#bonfire-pageloader").hasClass('hide'))
		$("#bonfire-pageloader").addClass('hide');
	$(".bgff_reload").css("display", "none");
}
/** -- 弹出模态页 --* */
function showDialog(url) {
	if (!url) {
		return;
	}
	var modelsize = 800;
	// 获得窗口的垂直位置
	var iTop = (window.screen.availHeight - 30 - modelsize) / 2;
	// 获得窗口的水平位置
	var iLeft = (window.screen.availWidth - 10 - modelsize) / 2;
	var obj = new Object();
	obj.objid = "001";// 传递参数
	obj.userid = "001001";// 传递参数
	if (document.all) // IE
	{
		feature = "dialogWidth:" + modelsize + "px;dialogHeight:" + modelsize
				+ "px;left:" + iLeft2 + "px;top:" + iTop2
				+ "px;status:no;help:no;center:yes;";
		window.showModalDialog(url, '_blank', feature);
	} else {
		// modelessDialog可以将modal换成dialog=yes
		feature = "width=" + modelsize + ",height=" + modelsize + ",top="
				+ iTop + ",left=" + iLeft
				+ " ,menubar=no,toolbar=no,location=no,";
		feature += "scrollbars=no,status=no,modal=yes,center=yes";
		window.open(url, '_blank', feature);
		window.opener = null;
		window.open('', '_self');
		window.close();
	}
}

/** -- 点击制定区域以外的区域获取 --* */
$(document)
		.click(
				function(e) {
					var tar1 = $('.select_head_fir'); // 设置目标区域
					var tar7 = $('.select_head_sec');
					var tar2 = $('.custom-class-select');
					var tar3 = $('.kb-select-scroll');
					var tar4 = $('.addgroup-class-select');
					var tar5 = $('.source-class-select');
					var tar6 = $(".tgry-class-select");
					if ((!tar1.is(e.target) && tar1.has(e.target).length === 0)
							&& (!tar7.is(e.target) && tar7.has(e.target).length === 0)
							&& (!tar2.is(e.target) && tar2.has(e.target).length === 0)
							&& (!tar3.is(e.target) && tar3.has(e.target).length === 0)
							&& (!tar4.is(e.target) && tar4.has(e.target).length === 0)
							&& (!tar5.is(e.target) && tar5.has(e.target).length === 0)
							&& (!tar6.is(e.target) && tar6.has(e.target).length === 0)) {
						if (!$('.select_head_fir_select').hasClass('hide'))
							$('.select_head_fir_select').addClass('hide');
						if (!$('.select_head_sec_select').hasClass('hide'))
							$('.select_head_sec_select').addClass('hide');
						if ($('.custom-class-select').hasClass('kb-openselect'))
							$('.custom-class-select').removeClass(
									'kb-openselect');
						if ($('.addgroup-class-select').hasClass(
								'kb-openselect'))
							$('.addgroup-class-select').removeClass(
									'kb-openselect');
						if ($('.source-class-select').hasClass('kb-openselect'))
							$('.source-class-select').removeClass(
									'kb-openselect');
						if ($('.tgry-class-select').hasClass('kb-openselect'))
							$('.tgry-class-select')
									.removeClass('kb-openselect');
						$(".kb-select-scroll").css("visibility", "hidden");
						$(".kb-select-icon").css({
							'opacity' : 1,
							'visibility' : 'visible',
							'transform' : 'rotate(0deg)'
						});
						/*
						 * $(".kb-icon-min-close").css("visibility", "visible")
						 * .css("opacity", "1");
						 */
					}
					;
				});
// 回车键搜索
function KeyDown() {
	if (event.keyCode == 13) {
		event.returnValue = false;
		event.cancel = true;
		var searchKey = $("#search_key").val();
		doSearchLick(searchKey);
	}
}

// 客资模糊搜索
function doSearchLick(searchKey) {
	if (!searchKey) {
		return;
	}

	// 获取数据
	getContentData("all", '', '0', searchKey);

	// 切换CSS样式
	var arrayli = $(".table_tab_ul li");
	$.each(arrayli, function(k, v) {
		if (k == 0) {
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
	$(".conte_table_all").removeClass("hide");
}
/* 消费管理 */
function toSellManger() {
	alert("消费管理");
	$(".select_head_sec_title").text("消费管理");
}
/* 数据报表、大数据 */
function toBigData() {
	$(".select_head_sec_title").text("数据报表");
	//隐藏搜索框
	$(".search_input").addClass("hide");
	var role = $("#role_select").val();
	if ("dscj" == role) {
		// 电商推广大数据
		getDsTgBigData();
	} else if ("dsyy" == role) {
		// 电商邀约大数据
		getDsYyBigData();
	} else if ("zjsyy" == role) {
		// 转介绍邀约大数据
		getZjsBigData();
	} else if ("msjd" == role) {
		// 门店大数据
		getShopBigData();
	}
}
/*隐藏弹框*/
function hideSelectBoxAll(){
	if(!$(".select_head_fir_select").hasClass("hide"))$(".select_head_fir_select").addClass("hide");
	if(!$(".select_head_sec_select").hasClass("hide"))$(".select_head_sec_select").addClass("hide");
}

//判断当前字符串是否以str结束
if (typeof String.prototype.endsWith != 'function') {
	String.prototype.endsWith = function(str) {
		return this.slice(-str.length) == str;
	};
}