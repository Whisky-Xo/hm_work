window["ss"] = 1;
window["time"] = 120;
var timer='';
/** -- GoEasy消息 --* */
/*-- 底部消息 --*/
var mainUserId = $("#main_userid").val();
var mainUserCompanyId = $("#main_usercompanyid").val();
var goEasyChannel = "hm_crm_channel_" + mainUserCompanyId + "_" + mainUserId;

var goEasyTop = new GoEasy({
	appkey : "f8c84801-0ac1-4cbf-98e3-3edd85d78b09"
});
goEasyTop
		.subscribe({
			channel : goEasyChannel,
			onMessage : function(message) {
				var msg = jsFmt(message.content);
				var getobj = jQuery.parseJSON(msg);
				var staffId = $("#main_usercompanyid").val().trim() + "_"
						+ $("#main_userid").val().trim();
				if (staffId != getobj.staffid) {
					return false;
				}
				if (!$('.floor-alert').hasClass('hide')) {// 是否已经有一条消息弹出
					window["ss"] = 2;
					blockHide("floor-alert");
				}
				var oldnewsnum = parseInt($(".newsnum").text()) + 1;// 顶部的消息数字加1
				$(".newsnum").empty().append(oldnewsnum);
				console.log(getobj);
				if (getobj.type == "kzadd") {// 新客资提醒
					doPlayRadio("kzadd");// 播放声音
					blinkHead();// 顶部频闪
					if ($('.msg_alertbtn').hasClass('hide'))
						$(".msg_alertbtn").removeClass("hide");
					if ($('.msg_alerttitle').hasClass('hide'))
						$(".msg_alerttitle").removeClass("hide");
					$(".jujue_btn_news").removeClass("hide");
					$(".jieshou_btn_news").removeClass("hide");
					var newsContentStr = "";
					newsContentStr += "<h5><b style='font-weight:300;'>客资名称：</b>"
							+ getobj.kz['kzName'] + "</h5>";
					newsContentStr += "<h5><b style='font-weight:300;'>客资QQ：</b>"
							+ getobj.kz['kzQq'] + "</h5>";
					newsContentStr += "<h5><b style='font-weight:300;'>采集员：</b>"
							+ getobj.kz['collector'] + "</h5>";
					newsContentStr += "<h5><b style='font-weight:300;'>提报时间：</b>"
							+ getobj.kz['createTime'] + "</h5>";
					if (getobj.kz["kzId"]) {
						$(".jujue_btn_news").attr("onclick",
								"jujue('" + getobj.kz["kzId"] + "')");
						$(".jieshou_btn_news").attr("onclick",
								"jieshou('" + getobj.kz["kzId"] + "')");
					}
					$(".news-contents").empty();
					$(".news-contents").append(newsContentStr);
					
				} else if (getobj.type == "warntime") {// 定时提醒
					doPlayRadio("warntime");
					if (!($('.msg_alertbtn').hasClass('hide')))
						$(".msg_alertbtn").addClass("hide");
					if (!($('.msg_alerttitle').hasClass('hide')))
						$(".msg_alerttitle").addClass("hide");
					var newsContentStr = "<h5><b style='background: #eeeeee;font-weight:700;color: #FF0000;'>定时消息</b></h5>";
					newsContentStr += "<h5><b style='font-weight:300;'>时间：</b>"
							+ getobj.warntime + "</h5>";
					newsContentStr += "<h5><b style='font-weight:300;'>内容：</b>"
							+ getobj.msg + "</h5>";
					$(".news-contents").empty();
					$(".news-contents").append(newsContentStr);
					
				} else if (getobj.type == "flash") {// 定时提醒
					doPlayRadio("msg");
					if (!($('.msg_alertbtn').hasClass('hide')))
						$(".msg_alertbtn").addClass("hide");
					if (!($('.msg_alerttitle').hasClass('hide')))
						$(".msg_alerttitle").addClass("hide");
					var newsContentStr = "<h5 style='height:130px;'>消息：<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ getobj.msg + "</h5>";
					if ($('.come_').hasClass('hide')) {
						$('.come_').removeClass('hide');
					}
					$(".come_from_name").empty().append(getobj.sender);
					$(".news-contents").empty();
					$(".news-contents").append(newsContentStr);
				} else if (getobj.type == "ticket") {// 罚单消息
					doPlayRadio("ticket");
					if (!($('.msg_alertbtn').hasClass('hide')))
						$(".msg_alertbtn").addClass("hide");
					if (!($('.msg_alerttitle').hasClass('hide')))
						$(".msg_alerttitle").addClass("hide");
					var newsContentStr = "<h5><b style='background: #eeeeee;font-weight:700;color: #FF0000;'>罚单消息</b></h5>";
					newsContentStr += "<h5><b style='font-weight:300;'>内容：</b>"
							+ getobj.msg + "</h5>";
					$(".news-contents").empty();
					$(".news-contents").append(newsContentStr);
				} else if (getobj.type == "common") {// 通用消息
					doPlayRadio("msg");
					if (!($('.msg_alertbtn').hasClass('hide')))
						$(".msg_alertbtn").addClass("hide");
					if (!($('.msg_alerttitle').hasClass('hide')))
						$(".msg_alerttitle").addClass("hide");
					var newsContentStr = "<h5 style='height:130px;'>消息：<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ getobj.msg + "</h5>";
					$(".news-contents").empty();
					$(".news-contents").append(newsContentStr);
				}
				showFloorAlert();
			}
		});

/*-- 播放声音 --*/
function doPlayRadio(type) {
	var audio = document.createElement("audio");
	if (type == "kzadd") {// 新客资
		audio.src = "http://data3.huiyi8.com/2014/12/03/25.mp3";
		audio.play();
	} else if (type == "warntime") {// 定时消息
		audio.src = "http://data3.huiyi8.com/2016/dqd/07/25/1.mp3";
		audio.play();
	} else if (type == "msg") {// 通用消息
		audio.src = "http://data3.huiyi8.com/2016/dqd/04/22/12.mp3";
		audio.play();
	} else if (type == "ticket") {// 罚单消息
		audio.src = "http://data3.huiyi8.com/2016/dqd/06/23/1.mp3";
		audio.play();
	} else {// 其他
		audio.src = "http://data3.huiyi8.com/2016/dqd/05/18/2.mp3";
		audio.play();
	}
}

/*-- 转义字符还原 --*/
function jsFmt(msg) {
	var str = msg;
	while (str.indexOf("&quot;") != -1) {
		str = str.replace("&quot;", "\"");
	}
	return str;
}

function showNewsAlert() {// 弹出消息提醒框
	blockHide('clock_alert')
	getAllNoNews();
	if ($('.news_alert').hasClass('hide')) {
		$('.news_alert').removeClass('hide').animate({
			"top" : "50px"
		});
	}
	$(".bg000_op0").css("display", "block").attr("onclick","blockHide('news_alert')");
}
/* 点击，不显示弹框（消息提醒和底部弹框提醒） */
function blockHide(className) {
	window["ss"] = 1;
	if ((className == "news_alert") || (className == "clock_alert")) {
		$('.' + className).animate({
			"top" : "-100%"
		}, function() {
			if (!$('.' + className).hasClass("hide")) {
				$('.' + className).addClass("hide")
			}
		});
	} else {
		$('.' + className).animate({
			'bottom' : "-100%"
		})
		if (!$('.floor-alert').hasClass("hide")) {
			$('.floor-alert').addClass("hide");
		}
	}
	$(".bg000_op0").css("display", "none").attr("onclick", "");
	if (!$(".bgshadow").hasClass("hide")) {
		$(".bgshadow").addClass("hide");
	}
}
function showFloorAlert() {// 弹出底部的消息提醒框
	if ($('.floor-alert').hasClass('hide')) {
		$('.floor-alert').removeClass('hide').animate({
			'bottom' : "0px"
		});
		if (window["ss"] == 2) {
			window["time"] = 120;
		} else {
			window["time"] = 120;
			timeMins(window["time"]);
		}
	} else {
		window["ss"] = 1;
		$('.floor-alert').addClass('hide');
		showFloorAlert();
	}
}
/* 点击底部弹框知道了 */
function blockHideFloor(className) {
	if (timer !=""){stoptimer();}
	window["ss"] = 1;
	if ((className == "news_alert") || (className == "clock_alert")) {
		$('.' + className).animate({
			"top" : "-100%"
		}, function() {
			if (!$('.' + className).hasClass("hide")) {
				$('.' + className).addClass("hide")
			}
		});
	} else {
		$('.' + className).animate({
			'bottom' : "-100%"
		}, function() {
			if (!$('.' + className).hasClass("hide")) {
				$('.' + className).addClass("hide")
			}
		});
	}
	$(".bg000_op0").css("display", "none").attr("onclick", "");
}
function stoptimer() {
	clearTimeout(timer);
	document.getElementById('blink').style.backgroundColor = "#0096E0";
	}
/*-- 新客资接单 --*/
function jieshou(kzId) {
	stoptimer();
	// kzId为待接单的客资ID
	if (!kzId) {
		return false;
	}
	$.ajax({
		url : 'do_kz_make_order',
		type : "POST",
		data : {
			kzid : kzId
		},
		success : function(data) {
			if (data.code == '100000') {
				layer.msg("接单成功");
				blockHide("floor-alert");
				doSearch();
			} else {
				layer.msg(data.msg);
				blockHide("floor-alert");
			}
		}
	});
}
/* 倒计时60秒 */
function timeMins(time) {
	var timeOut;
	if (time <= 1) {
		if (!$(".jieshou_btn_news").hasClass("bge5")) {
			$(".jieshou_btn_news").addClass("bge5");
		}
		$(".jieshou_btn_news").text("接收");
		$(".jieshou_btn_news").removeAttr("onclick");
		window["ss"] = 1;
	} else {
		if ($(".jieshou_btn_news").hasClass("bge5")) {
			$(".jieshou_btn_news").removeClass("bge5");
		}
		time--;
		window["time"]--;
		timeOut = setTimeout(function() {
			timeMins(window["time"]);
		}, 1000);
		var str = "接收" + window["time"];
		$(".jieshou_btn_news").text(str);
	}
}
/* 头部不停闪动 */
function blinkHead() {
	if (!document.getElementById('blink').style.backgroundColor) {
		document.getElementById('blink').style.backgroundColor = "rgb(255, 84, 0)";
	}
	if (document.getElementById('blink').style.backgroundColor == "rgb(255, 84, 0)") {
		document.getElementById('blink').style.backgroundColor = "rgb(45, 183, 245)";
	} else {
		document.getElementById('blink').style.backgroundColor = "rgb(255, 84, 0)";
	}
	timer = setTimeout("blinkHead()", 100);
}


function getAllNoNews() {
	/* 获取全部未读消息 */
	$
			.ajax({
				url : '../news/get_news_no_read_all',
				type : "POST",
				success : function(data) {
					if (data.code == '100000') {
						var newsList = data.newsList;
						$(".newsnum").empty().append(data.size);
						if (newsList.length > 0) {
							var showNewNewsList = "";
							$
									.each(
											newsList,
											function(key, val) {
												if (val['type'] == "kzadd") {// 判断为添加客资类型有接收按钮
													showNewNewsList += '<li id="btnafter'
															+ val['id']
															+ '" style="border-bottom: 1px #e5e5e5 solid;"><b>'
															+ val['title']
															+ '</b><b>'
															+ val['spare2']
															+ '</b><b style="border-radius: 4px;text-align:center;float:right;background: #2DB7F5;color:#fff;margin-top: -5px; padding: 5px 15px;" class="com_tip_btn btn_hover js_btn_news'
															+ key
															+ '" onclick="jieshouHead(\''
															+ val['spare1']
															+ '\',\''
															+ val['id']
															+ '\')">接收'
															+ '</b></li>';
												} else if (val['type'] == "ticket") {
													showNewNewsList += '<li id="btnafter'
															+ val['id']
															+ '" style="border-bottom: 1px #e5e5e5 solid;"><b>'
															+ val['title']
															+ '</b><b>'
															+ val['spare2']
															+ '</b><b style="border-radius: 4px;text-align:center;float:right;background:#ED0000;color:#fff;margin-top: -5px; padding: 5px 15px;" class="com_tip_btn btn_hover js_btn_news'
															+ key
															+ '" onclick="knowNews(\''
															+ val['id']
															+ '\')">知道了'
															+ '</b>';
													/*
													 * <b class="js_btn_news
													 * bge5"
													 * style="float:right;">接收</b></li>';
													 */} else {
													showNewNewsList += '<li id="btnafter'
															+ val['id']
															+ '" style="padding-left: 20px; padding-right: 20px;border-bottom: 1px #e5e5e5 solid;"><b>'
															+ val['title']
															+ '</b>'
															+ '<b style="border-radius: 4px;text-align:center;float:right;background:#57C5F7;color:#fff;margin-top: -5px; padding: 5px 15px;" class="com_tip_btn btn_hover js_btn_news'
															+ key
															+ '" onclick="knowNews(\''
															+ val['id']
															+ '\')">知道了'
															+ '</b>';
												}
											})
							$(".showNewNewsList").empty().append(
									showNewNewsList);
						}
					} else {
						layer.msg(data.msg);
					}
				}
			});
}
/*-- 头部消息中接单 --*/
function jieshouHead(kzId, id) {
	/*停止头部不停闪动*/
	stoptimer();
	/*if($(".newsnum").text()>0){
		alert($(".newsnum").text());
	}*/
	// kzId为待接单的客资ID
	if (!kzId) {
		return false;
	}
	$.ajax({
		url : '../client/do_kz_make_order',
		type : "POST",
		data : {
			kzid : kzId
		},
		success : function(data) {
			if (data.code == '100000') {
				layer.msg("接单成功");
				$.ajax({
					url : '../news/set_news_be_read',
					type : "POST",
					data : {
						newsIds : id
					},
					success : function(data) {
						$("#btnafter" + id).css("display", "none");
						$(".newsnum").text($(".newsnum").text() - 1);
						/*getAllData("0", "", "1");*/
						
					}
				});
			} else {
				layer.msg(data.msg);
				$.ajax({
					url : '../news/set_news_be_read',
					type : "POST",
					data : {
						newsIds : id
					},
					success : function(data) {
						$("#btnafter" + id).css("display", "none");
						$(".newsnum").text($(".newsnum").text() - 1);
						/*getAllData("0", "", "1");*/
					}
				});
			}
		}
	});
}