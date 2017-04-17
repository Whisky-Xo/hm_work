/** -- 数据分析-大数据 --* */
// 电商推广大数据
function getDsTgBigData() {
	hideSelectBoxAll();/*隐藏弹框*/
	reloadDsTgBigData('ds',' ');
	$(".nav_dstg").text("电商推广大数据");
	$(".nav_first_tab").attr("onclick","showNewsTab('nav_dstg','nav_dsqd','','reloadDsTgBigData')");
	$(".nav_third_tab").attr("onclick","showNewsTab('nav_dsqd','nav_dstg','','reloadDsTgBigData')");
	if(!$(".zjsyy_nav_box").hasClass("hide"))$(".zjsyy_nav_box").addClass("hide");/*隐藏转介绍中的第三个切换*/
	if($(".main_nav").hasClass("hide"))$(".main_nav").removeClass("hide");	
	if($(".source_bigdata_box").hasClass("hide"))$(".source_bigdata_box").removeClass("hide");/*电商里面有渠道*/
	if(!$(".info_main_box").hasClass("hide"))$(".info_main_box").addClass("hide");
	if($(".bigdata").hasClass("hide"))$(".bigdata").removeClass("hide");
}

// 电商邀约大数据
function getDsYyBigData() {
	$(".ant-tabs-ink-bar").css({"left":"175px","right":"262px"});
	$(".search_cer").attr("onclick","reloadDsYyBigData('0')");/*搜索*/
	$(".last_m").attr("onclick","reloadDsYyBigData('1')");/*上月*/
	$(".last_w").attr("onclick","reloadDsYyBigData('2')");/*上周*/
	$(".yestoday").attr("onclick","reloadDsYyBigData('3')");/*昨日*/
	$(".today").attr("onclick","reloadDsYyBigData('4')");/*今日*/
	$(".this_w").attr("onclick","reloadDsYyBigData('5')");/*本周*/
	$(".this_m").attr("onclick","reloadDsYyBigData('6')");/*本月*/
	hideSelectBoxAll();/*隐藏弹框*/
	reloadDsYyBigData();
	if(!$(".main_nav").hasClass("hide"))$(".main_nav").addClass("hide");
	if(!$(".info_main_box").hasClass("hide"))$(".info_main_box").addClass("hide");
	if($(".bigdata").hasClass("hide"))$(".bigdata").removeClass("hide");
}

// 转介绍推广大数据
function getZjsBigData() {
	hideSelectBoxAll();/*隐藏弹框*/
	reloadZjsTgBigData('ds',' ');
	$(".nav_first_tab").attr("onclick","showNewsTab('nav_dstg','nav_dsqd','','reloadZjsTgBigData')");/*转介绍推广*/
	$(".nav_third_tab").attr("onclick","showNewsTab('nav_dsqd','nav_dstg','zjs_invalid_json_datas_beidou3','reloadZjsTgBigData')");/*转介绍无效分析*/
	if($(".zjsyy_nav_box").hasClass("hide"))$(".zjsyy_nav_box").removeClass("hide");/*转介绍大数据切换有3个是转介绍邀约和转介绍推广的合并，比电商多了一个*/
	$(".nav_dstg").text("转介绍推广大数据")
	if(!$(".source_bigdata_box").hasClass("hide"))$(".source_bigdata_box").addClass("hide");/*转介绍里面没有渠道*/
	if($(".main_nav").hasClass("hide"))$(".main_nav").removeClass("hide");
	if(!$(".info_main_box").hasClass("hide"))$(".info_main_box").addClass("hide");
	if($(".bigdata").hasClass("hide"))$(".bigdata").removeClass("hide");
}

// 门店大数据
function getShopBigData() {
	alert("门店大数据");
}

/*电商推广大数据*/
function reloadDsTgBigData(class_type,type){
	var sourceid='';
	var source_check=$("#sourceckeck span");
	$.each(source_check,function(key,val){
		sourceid+=$(this).attr("title")+",";
	})
	sourceid=sourceid.substring(0,sourceid.length-1);

	if($(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").removeClass('bonfire-pageloader-icon-hide');
	if($("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").removeClass('bonfire-pageloader-hide');
	if($("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").removeClass('hide');
	var rang_time='';
	if(type=="1"){/* 上月 */
        rang_time="&"+selectDateSubmit('9');
	}else if(type=="2"){/* 上周 */
		rang_time="&"+selectDateSubmit('8');
	}else if(type=="3"){/* 昨日 */
		rang_time="&"+selectDateSubmit('7');
	}else if(type=="4"){/* 今日 */
		rang_time="&"+selectDateSubmit('1');
	}else if(type=="5"){/* 本周 */
		rang_time="&"+selectDateSubmit('3');
	}else if(type=="6"){/* 本月 */
		rang_time="&"+selectDateSubmit('5');
	}else if(type=="0"){/* 开始结束时间搜索 */
		var startStr = $("#bigdata_start_time").val();// 时间起始
            var endStr = $("#bigdata_end_time").val();// 时间截止
            if(!startStr){
            	startStr = endStr;
            }
            rang_time="&start=" + startStr + "&end=" + endStr;
	}else{
		rang_time='';
	}
	
	var url;
	if(class_type=="ds"){
		$.ajax({
			url : 'ds_tg_json_datas_beidou3?sourceid='+sourceid+rang_time,
			success : function(data) {
				if(data.code == '100000'){
				var tabletitle='<tr class="table_tittle border-bo"><td><table><tr><td class="fontw700 table-first-left width_ds_tg_td border-ri">渠道</td><td><table><tr><td class="fontw700 width_ds_tg_td border-ri">人员</td>';
				/* $.each(data.srcList,function(k,v){ */
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">提报量</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效量</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">入店量</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">成交量</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">毛客资入店率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效客资入店率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">入店成交率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">毛客资成交率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效客资成交率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">花费</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">毛客资成本</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效客资成本</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">入店成本</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">成交成本</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">成交均价</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">营业额</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">ROI</td>';
				/* }); */
				tabletitle+='</tr></table></td></tr></table></td></tr>';
				$(".table-kz-thead").empty().append(tabletitle);
					var tabletbody='<tr class="border-bo">';
    					/* tabletbody+='<td class="bgf9 border-ri table-first-left cursorpo fontw700 mian_title"><span style="font-weight:700;width:14px;display:inline-block;font-size: 16px;">电商推广分部</span></td>'; */
    					tabletbody+='<td>';
						tabletbody+='<table><tbody>';
							$.each(data.analysis,function(k,v){
								if(v['srcName']=='合计'){
									tabletbody+='<tr class="tr_hover">';
									tabletbody+='<td class="bgf9 cursorpo widthhj heighthj" style="border-right:1px #F9F9F9 solid;"><span>'+v['srcName']+'</span></td>';
								}else{
									tabletbody+='<tr class="tr_hover border-to box-bg-dept'+v['srcId']+'">';
									tabletbody+='<td class="bgf9 border-ri cursorpo" onclick="showRenyuanAll(\'dept'+v['srcId']+'\')"><span>'+v['srcName']+'</span></td>';
								}
								tabletbody+='<td><table>';
									$.each(v['srcNumList'],function(k_1,v_1){
										if(v_1['staffName']=='合计'){
											tabletbody+='<tr class="tr_hover box-hj-bg-dept'+v['srcId']+'">';
											if(v['srcName']=='合计'){
												tabletbody+='<td class="bgf9 border-ri cursorpo"><span></span></td>';
											}else{
												tabletbody+='<td class="bgf9 border-ri cursorpo" onclick="showRenyuanAll(\'dept'+v['srcId']+'\')"><span>'+v_1['staffName']+'</span></td>';
											}
										}else{
											if(v_1['staffName']=="合计"){
												tabletbody+='<tr class="hide dept'+v['srcId']+' tr_hover">';
											}else{
												tabletbody+='<tr class="hide dept'+v['srcId']+' border-to tr_hover">';
											}
		    									tabletbody+='<td class="bgf9 border-ri cursorpo"><span>'+v_1['staffName']+'</span></td>';
											}
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['kzNum']+'</span></td>';/* 毛客资咨询量 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['yxNum']+'</span></td>';/* 有效咨询量 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['rdNum']+'</span></td>';/* 入店量 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['cjNum']+'</span></td>';/* 成交量 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['yxRate']+'</span></td>';/* 有效率 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['grossRdRate']+'</span></td>';/* 毛客资咨询入店率 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['rdRate']+'</span></td>';/* 有效客资咨询入店率 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['cjRate']+'</span></td>';/* 入店成交率 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['grossCjRate']+'</span></td>';/* 毛客资成交率 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['yxCjRate']+'</span></td>';/* 有效客资成交率 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['amount']+'</span></td>';/* 花费 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['grossCost']+'</span></td>';/* 毛客资成本 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['yxCost']+'</span></td>';/* 有效客资成本 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['rdCost']+'</span></td>';/* 入店成本 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['cjCost']+'</span></td>';/* 成交成本 */
										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['jjAmount']+'</span></td>';/* 成交均价*/
										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['yyAmount']+'</span></td>';/* 营业额*/
										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['roi']+'</span></td>';/* ROI */
										tabletbody+='</tr>';
									});
								
								tabletbody+='</table></td>';
								tabletbody+='</tr>';
 		                   });
							tabletbody+='</tbody></table>';
               			tabletbody+='</td>';
               		tabletbody+='</tr>';
               		$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
				$(".table-kz-tbody").empty().append(tabletbody);
				
				
				$(".right_table tr td").css("display","table-cell"); 
				$(".table.right_table td").css("display","table-cell");
				$(".table_tittle td").css("display","table-cell");
				
				
				$("#rangeA").val(data.start+"~"+data.end);
				if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
		    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
		    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
				}else{
					$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
					alert(data.msg);
					if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
			    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
			    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
				}
			}
		});
	}else{
		$.ajax({
			url : 'ds_invalid_json_datas_beidou3?sourceid='+sourceid+rang_time,
			success : function(data) {
				if(data.code == '100000'){
				var tabletitle='<tr class="table_tittle invail_kz_res border-bo"><td class="width_title_wx_td bgf9 border-ri"></td>';
				$.each(data.rsnList,function(k,v){
					tabletitle+='<td class="fontw700 bgf9 border-ri border-bo width_ds_wx_td">'+v['dicName']+'</td>';
				}); 
				tabletitle+='</tr>';
				$(".table-kz-thead").empty().append(tabletitle);
				var tabletbody='';
				$.each(data.analysis,function(k,v){
						tabletbody+='<tr class="tr_hover invail_kz_res">';
						tabletbody+='<td class="bgf9 border-ri border-bo table-first-left fontw700 width_title_wx_td"><span style="font-weight:700;">'+v['srcName']+'</span></td>';
						$.each(v['faliKzList'],function(k_1,v_1){
							tabletbody+='<td class="border-bo border-ri width_ds_wx_td"><span>'+v_1['num']+'</span></td>';
						});
						tabletbody+='</tr>';
				}); 
				$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
				$(".table-kz-tbody").empty().append(tabletbody);
				
				$(".right_table tr td").css("display","inline-block"); 
				$(".table.right_table td").css("display","inline-block");
				$(".table_tittle td").css("display","inline-block");
				
				
				$("#rangeA").val(data.start+"~"+data.end);
				if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
		    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
		    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
			}else{
				$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
				alert(data.msg);
				if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
		    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
		    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
			}
			}
		});
	}
}
/*电商推广大数据结束*/

/*电商邀约大数据*/
function reloadDsYyBigData(type){
	if($(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").removeClass('bonfire-pageloader-icon-hide');
	if($("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").removeClass('bonfire-pageloader-hide');
	if($("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").removeClass('hide');
	var rang_time;
	if(type=="1"){/* 上月 */
        rang_time="?"+selectDateSubmit('9');
	}else if(type=="2"){/* 上周 */
		rang_time="?"+selectDateSubmit('8');
	}else if(type=="3"){/* 昨日 */
		rang_time="?"+selectDateSubmit('7');
	}else if(type=="4"){/* 今日 */
		rang_time="?"+selectDateSubmit('1');
	}else if(type=="5"){/* 本周 */
		rang_time="?"+selectDateSubmit('3');
	}else if(type=="6"){/* 本月 */
		rang_time="?"+selectDateSubmit('5');
	}else if(type=="0"){/* 开始结束时间搜索 */
		var startStr = $("#bigdata_start_time").val();// 时间起始
        var endStr = $("#bigdata_end_time").val();// 时间截止
            if(!startStr){
            	startStr = endStr;
            }
            rang_time="?start=" + startStr + "&end=" + endStr;
	}else{
		rang_time='';
	}
	
	$.ajax({
			url : 'ds_yy_json_datas_beidou3'+rang_time,
			success : function(data) {
				if(data.code == '100000'){
					var tabletitle='<tr class="table_tittle border-bo">';
    				tabletitle+='<td><table><tr>';
    				tabletitle+='<td><table><tr>';
    				tabletitle+='<td class="fontw700 width_ds_yy_td border-ri" style="width:55px;">人员</td>';
    				tabletitle+='<td><table><tr>';
    				tabletitle+='<td class="fontw700 width_ds_yy_td border-ri">参数</td>';
    				tabletitle+='</tr></table></td>';
    				var sourcelist=data.srcList;
    				$.each(data.srcList,function(k,v){
    					tabletitle+='<td><table><tr>';
    					tabletitle+='<td class="fontw700 width_ds_yy_td border-ri">'+v['srcName']+'</td>';
    					tabletitle+='</tr></table></td>';
    				});
    				tabletitle+='</tr></table></td></tr></table></td></tr></table></td>';
    				tabletitle+='</tr>'
    				$(".table-kz-thead").empty().append(tabletitle);
					var mianstr="";
					var analysis=data.analysis;
					mianstr+=' <tr class="border-bo">';
					 mianstr+='<td><table>';
					 $.each(analysis,function(k,v){
						 if(v['staffName']=='合计'){
							 mianstr+='<tr> <td class="bgf9 cursorpo border-ri" style="width:55px;"><span>'+v['staffName']+'</span></td>'; 
						 }else{
							 mianstr+='<tr class="dborder-to"> <td class="bgf9 cursorpo border-ri" style="width:55px;"><span>'+v['staffName']+'</span></td>';
						 }
							mianstr+='<td><table>';
							mianstr+='<tr>';
								mianstr+='<td><table>';
								mianstr+='<td><table>';
								mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>客资量</span></td></tr>';
								mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>有效量</span></td></tr>';
								 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>有效率</span></td></tr>';
								 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>入店量</span></td></tr>';
								 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>入店率</span></td></tr>';
								 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>成交量</span></td></tr>';
								 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>成交率</span></td></tr>';
								 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>营业额</span></td></tr>';
								 mianstr+='<tr class="border-ri"><td class="table_td"><span>均价</span></td></tr>';
								 mianstr+='</table></td>';
								$.each(sourcelist,function(s_key,s_val){
									mianstr+='<td><table>';
									var type='';
									var hassource='';
									 $.each(v['srcNumList'],function(key,val){
										 if(val['sourceId']==s_val['srcId']){
											 type=true;
											 hassource+='<tr class="border-ri"><td class="table_td border-bo"><span>'+val['kzNum']+'</span></td></tr>';
											 hassource+='<tr class="border-ri"><td class="table_td border-bo"><span>'+val['yxNum']+'</span></td></tr>';
											 hassource+='<tr class="border-ri"><td class="table_td border-bo"><span>'+val['yxRate']+'</span></td></tr>';
											 hassource+='<tr class="border-ri"><td class="table_td border-bo"><span>'+val['rdNum']+'</span></td></tr>';
											 hassource+='<tr class="border-ri"><td class="table_td border-bo"><span>'+val['rdRate']+'</span></td></tr>';
											 hassource+='<tr class="border-ri"><td class="table_td border-bo"><span>'+val['cjNum']+'</span></td></tr>';
											 hassource+='<tr class="border-ri"><td class="table_td border-bo"><span>'+val['cjRate']+'</span></td></tr>';
											 hassource+='<tr class="border-ri"><td class="table_td border-bo"><span>'+val['yyAmount']+'</span></td></tr>';
											 hassource+='<tr class="border-ri"><td class="table_td"><span>'+val['jjAmount']+'</span></td></tr>';
										 }
										});
									 if(type){
										 mianstr+=hassource;
									 }else{
										 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>0</span></td></tr>';
										 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>0</span></td></tr>';
										 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>0</span></td></tr>';
										 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>0</span></td></tr>';
										 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>0</span></td></tr>';
										 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>0</span></td></tr>';
										 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>0</span></td></tr>';
										 mianstr+='<tr class="border-ri"><td class="table_td border-bo"><span>0</span></td></tr>';
										 mianstr+='<tr class="border-ri"><td class="table_td"><span>0</span></td></tr>';
									 }
									 mianstr+='</table></td>';
								 });
								mianstr+='</table></td>';
								mianstr+='</tr>';
							
							mianstr+='</table></td>';
							mianstr+='</tr>';
						});
					mianstr+='</table></td>'
						mianstr+='</tr>';
						$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
					$(".table-kz-tbody").empty().append(mianstr);
					
					$(".right_table tr td").css("display","table-cell"); 
					$(".table.right_table td").css("display","table-cell");
					$(".table_tittle td").css("display","table-cell");
					
					
					$("#rangeA").val(data.start+"~"+data.end);
					
			    	if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
			    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
			    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
				}else{
					$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
    				layer.msg(data.msg);
					if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
			    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
			    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
    			}	
			}
		});
}
/*电商邀约大数据结束*/
/*转介绍推广大数据*/
function reloadZjsTgBigData(class_type,type,urlchange){
	var sourceid='';
	var source_check=$("#sourceckeck span");
	$.each(source_check,function(key,val){
		sourceid+=$(this).attr("title")+",";
	})
	sourceid=sourceid.substring(0,sourceid.length-1);

	if($(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").removeClass('bonfire-pageloader-icon-hide');
	if($("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").removeClass('bonfire-pageloader-hide');
	if($("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").removeClass('hide');
	var rang_time='';
	if(type=="1"){/* 上月 */
        rang_time="&"+selectDateSubmit('9');
	}else if(type=="2"){/* 上周 */
		rang_time="&"+selectDateSubmit('8');
	}else if(type=="3"){/* 昨日 */
		rang_time="&"+selectDateSubmit('7');
	}else if(type=="4"){/* 今日 */
		rang_time="&"+selectDateSubmit('1');
	}else if(type=="5"){/* 本周 */
		rang_time="&"+selectDateSubmit('3');
	}else if(type=="6"){/* 本月 */
		rang_time="&"+selectDateSubmit('5');
	}else if(type=="0"){/* 开始结束时间搜索 */
		var startStr = $("#bigdata_start_time").val();// 时间起始
        var endStr = $("#bigdata_end_time").val();// 时间截止
            if(!startStr){
            	startStr = endStr;
            }
            rang_time="&start=" + startStr + "&end=" + endStr;
	}else{
		rang_time='';
	}
	
	var url='zjs_tg_json_datas_beidou3';
	if(urlchange){url=urlchange}
	if(class_type=="ds"){
		$.ajax({
			url : url+'?sourceid='+sourceid+rang_time,
			success : function(data) {
				 if(data.code == '100000'){
					 var tabletitle='<tr class="table_tittle border-top border-bo">';
 					tabletitle+='<td class="fontw700 table-first-left width_ds_tg_td border-ri">人员</td>';
 					tabletitle+='<td><table><tr><td class="fontw700 width_ds_tg_td border-ri">渠道</td>';
 					
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">客资量</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效量</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效率</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">入店量</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">入店率</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">成交量</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">成交率</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">套系总金额</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">均价</td>';
 					
 				tabletitle+='</tr></table></td>';
 				tabletitle+='</tr></table></td></tr>';
 				$(".table-kz-thead").empty().append(tabletitle);
 					var tabletbody='';
 							$.each(data.analysis,function(k,v){
 								if(v['srcName']=='合计'){
 									tabletbody+='<tr>';
 									tabletbody+='<td class="bgf9 width_zjs_hj cursorpo" style="border-right:1px #F9F9F9 solid;"><span>'+v['srcName']+'</span></td>';
 								}else{
 									tabletbody+='<tr class="border-to box-bg-dept'+v['srcId']+'">';
 									tabletbody+='<td class="bgf9 border-ri cursorpo" onclick="showRenyuanAll(\'dept'+v['srcId']+'\')"><span>'+v['srcName']+'</span></td>';
									}
 								tabletbody+='<td><table>';
 									$.each(v['srcNumList'],function(k_1,v_1){
 										if(v_1['staffName']=='合计'){
 											tabletbody+='<tr class="box-hj-bg-dept'+v['srcId']+'">';
 											 if(v['srcName']=='合计'){
 												tabletbody+='<td class="bgf9 border-ri cursorpo"><span></span></td>';
 											}else{ 
 												tabletbody+='<td class="bgf9 border-ri cursorpo" onclick="showRenyuanAll(\'dept'+v['srcId']+'\')"><span>'+v_1['staffName']+'</span></td>';
 											 } 
 										}else{
		    									tabletbody+='<tr class="dept'+v['srcId']+' hide border-to">';
		    									tabletbody+='<td class="bgf9 border-ri cursorpo"><span>'+v_1['staffName']+'</span></td>';
											}
 										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['kzNum']+'</span></td>';/* 提报量 */
 										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['yxNum']+'</span></td>';/* 有效量 */
 										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['yxRate']+'</span></td>';/* 有效率 */
 										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['rdNum']+'</span></td>';/* 入店量 */
 										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['rdRate']+'</span></td>';/* 入店率 */
 										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['cjNum']+'</span></td>';/* 成交量 */
 										tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['cjRate']+'</span></td>';/* 成交率 */
 										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['yyAmount']+'</span></td>';/* 套系总金额 */
 										tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+v_1['jjAmount']+'</span></td>';/* 均价 */
 										/* tabletbody+='<td class="border-ri cursorpo"><span>'+v_1['lsNum']+'</span></td>'; *//* 营业额*/
 										tabletbody+='</tr>';
 									});
 								
 								tabletbody+='</table></td>';
 								tabletbody+='</tr>';
  		                   });
 							tabletbody+='</table>';
                			tabletbody+='</td>';
                		tabletbody+='</tr>';
                		$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
 				$(".table-kz-tbody").empty().append(tabletbody);
 				

				$(".right_table tr td").css("display","table-cell"); 
				$(".table.right_table td").css("display","table-cell");
				$(".table_tittle td").css("display","table-cell");
				
 				$("#rangeA").val(data.start+"~"+data.end);
 				if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
			    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
			    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
			}else{
				$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
				layer.msg(data.msg);
				if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
		    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
		    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
			}	
				/*  console.log($('#table_body').width()); */
		/* $('#table_head').width($('#table_body').width()); */
			}
		});
	}else{
		$.ajax({
			url : url+'?sourceid='+sourceid+rang_time,
			success : function(data) {
				if(data.code == '100000'){
				var tabletitle='<tr class="table_tittle invail_kz_res"><td class="fontw700 bgf9 border-bo width_title"></td>';
				$.each(data.rsnList,function(k,v){
					tabletitle+='<td class="fontw700 bgf9 border-ri border-bo">'+v['dicName']+'</td>';
				}); 
				tabletitle+='</tr>';
				$(".table-kz-thead").empty().append(tabletitle);
				var tabletbody='';
				$.each(data.analysis,function(k,v){
						tabletbody+='<tr class="tr_hover invail_kz_res">';
						tabletbody+='<td class="bgf9 border-ri border-bo table-first-left fontw700 width_title"><span style="font-weight:700;">'+v['srcName']+'</span></td>';
						$.each(v['faliKzList'],function(k_1,v_1){
							tabletbody+='<td class="border-bo border-ri"><span>'+v_1['num']+'</span></td>';
						});
						tabletbody+='</tr>';
				}); 
				$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
				$(".table-kz-tbody").empty().append(tabletbody);
				

				$(".right_table tr td").css("display","table-cell"); 
				$(".table.right_table td").css("display","table-cell");
				$(".table_tittle td").css("display","table-cell");
				
				$("#rangeA").val(data.start+"~"+data.end);
				if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
		    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
		    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
				}else{
					$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
    				layer.msg(data.msg);
					if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
			    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
			    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
    			}	
			}
		});
	}
}
/*转介绍推广大数据结束*/

/*转介绍邀约大数据*/
function getZjsYyBigData(type){
	if($(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").removeClass('bonfire-pageloader-icon-hide');
	if($("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").removeClass('bonfire-pageloader-hide');
	if($("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").removeClass('hide');
	var rang_time;
	if(type=="1"){/* 上月 */
        rang_time="?"+selectDateSubmit('9');
	}else if(type=="2"){/* 上周 */
		rang_time="?"+selectDateSubmit('8');
	}else if(type=="3"){/* 昨日 */
		rang_time="?"+selectDateSubmit('7');
	}else if(type=="4"){/* 今日 */
		rang_time="?"+selectDateSubmit('1');
	}else if(type=="5"){/* 本周 */
		rang_time="?"+selectDateSubmit('3');
	}else if(type=="6"){/* 本月 */
		rang_time="?"+selectDateSubmit('5');
	}else if(type=="0"){/* 开始结束时间搜索 */
		var startStr = $("#bigdata_start_time").val();// 时间起始
        var endStr = $("#bigdata_end_time").val();// 时间截止
            if(!startStr){
            	startStr = endStr;
            }
            rang_time="?start=" + startStr + "&end=" + endStr;
	}else{
		rang_time='';
	}
	$.ajax({
			url : 'zjs_yy_json_datas_beidou3'+rang_time,
			success : function(data) {
				console.log(data);
				if(data.code == '100000'){
					var tabletitle='<tr class="table_tittle border-bo"><td class="fontw700 width_ds_yy_td border-ri">人员</td>';
					tabletitle+='<td><table><tr><td class="fontw700 width_ds_yy_td border-ri"></td>';
    				tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">总客资</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效量</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">有效率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">入店量</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">咨询入店率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">成交量</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">成交率</td>';
					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">套系总金额</td>';
 					tabletitle+='<td class="fontw700 width_ds_tg_td border-ri">均价</td>';
					tabletitle+='</tr></table></td>';
    				tabletitle+='</tr>'
    				$(".table-kz-thead").empty().append(tabletitle);
					var tabletbody="";
					var analysis=data.analysis;
					$.each(analysis,function(dept_k,dept_v){
						if(dept_v['staffName']=='合计'){
							tabletbody+='<tr> <td class="bgf9" style="border-right:1px #F9F9F9 solid;"><span style="display:inline-block;">'+dept_v['staffName']+'</span></td>';
						}else{
							tabletbody+='<tr class="border-to box-bg-dept'+dept_v['staffId']+'"> <td class="bgf9 border-ri" onclick="showRenyuanAll(\'dept'+dept_v['staffId']+'\')"><span style="display:inline-block;">'+dept_v['staffName']+'</span></td>';
						}
					tabletbody+='<td><table>';
								/* if(dept_v['staffName']=='合计'){
									tabletbody+='<td class="bgf9 cursorpo" style="border-right:1px #F9F9F9 solid;"><span></span></td>';
								}else{
									tabletbody+='<td class="bgf9 border-ri  cursorpo"><span>'+dept_v['staffName']+'</span></td>';
								}	
									tabletbody+='<td><table>'; */
							$.each(dept_v['srcNumList'],function(num_k,num_v){
								if(num_v['staffName']=="合计"){
									tabletbody+='<tr class="box-hj-bg-dept'+dept_v['staffId']+'">';
									tabletbody+='<td class="bgf9 border-ri cursorpo" onclick="showRenyuanAll(\'dept'+dept_v['staffId']+'\')"><span>'+num_v['staffName']+'</span></td>';
								}else{
									tabletbody+='<tr class="border-to hide dept'+dept_v['staffId']+'">';
									tabletbody+='<td class="bgf9 border-ri cursorpo"><span>'+num_v['sourceName']+'</span></td>';
								}
								tabletbody+='<td class="border-ri cursorpo"><span>'+num_v['kzNum']+'</span></td>';/* 总客资 */
								tabletbody+='<td class="border-ri cursorpo"><span>'+num_v['yxNum']+'</span></td>';/* 有效量 */
								tabletbody+='<td class="border-ri cursorpo"><span>'+num_v['yxRate']+'</span></td>';/* 有效率 */
								tabletbody+='<td class="border-ri cursorpo"><span>'+num_v['rdNum']+'</span></td>';/* 入店量 */
								tabletbody+='<td class="border-ri cursorpo"><span>'+num_v['rdRate']+'</span></td>';/* 入店率 */
								tabletbody+='<td class="border-ri cursorpo"><span>'+num_v['cjNum']+'</span></td>';/* 成交量 */
								tabletbody+='<td class="border-ri cursorpo"><span>'+num_v['cjRate']+'</span></td>';/* 成交率 */
								tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+num_v['yyAmount']+'</span></td>';/* 套系总金额 */
								tabletbody+='<td class="border-ri cursorpo"><span>'+'¥'+num_v['jjAmount']+'</span></td>';/* 均价 */
								
								tabletbody+='</tr>';
							})
							tabletbody+='</table></td>';
							tabletbody+='</tr>';
						});
					 		tabletbody+='</table></td>'
						 tabletbody+='</tr>';
						 $(".update_time").empty().append("本次数据更新于："+data.cacheTime);
					$(".table-kz-tbody").empty().append(tabletbody);
					

					$(".right_table tr td").css("display","table-cell"); 
					$(".table.right_table td").css("display","table-cell");
					$(".table_tittle td").css("display","table-cell");
					
					
					$("#rangeA").val(data.start+"~"+data.end);
					
			    	if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
			    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
			    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
				}else{
					$(".update_time").empty().append("本次数据更新于："+data.cacheTime);
					layer.msg(data.msg);
					if(!$(".bonfire-pageloader-icon").hasClass('bonfire-pageloader-icon-hide'))$(".bonfire-pageloader-icon").addClass('bonfire-pageloader-icon-hide');
			    	if(!$("#bonfire-pageloader").hasClass('bonfire-pageloader-hide'))$("#bonfire-pageloader").addClass('bonfire-pageloader-hide');
			    	if(!$("#bonfire-pageloader").hasClass('hide'))$("#bonfire-pageloader").addClass('hide');
				}
			}
		});
}
/*转介绍邀约大数据结束*/
/*电商推广里面的main顶部导航切换*/
function showNewsTab(main, sec,url,clickfun) {
	if (main == "nav_dstg") {/*电商推广*/
		if(!$(".nav_dstg").hasClass("nav_on")){
			$(".nav_dstg").addClass("nav_on");
			$(".nav_dsqd").removeClass("nav_on");
		}
		if($(".nav_dstg_head").hasClass("hide")){
			$(".nav_dstg_head").removeClass("hide");
			$(".nav_dsqd_head").addClass("hide");
		}
		if($(".nav_dstg_box").hasClass("hide")){
			$(".nav_dstg_box").removeClass("hide");
			$(".nav_dsqd_box").addClass("hide");
		}
		if(url){
			$(".search_cer").attr("onclick",clickfun+"('ds','0','"+url+"')");
			$(".last_m").attr("onclick",clickfun+"('ds','1','"+url+"')");
			$(".last_w").attr("onclick",clickfun+"('ds','2','"+url+"')");
			$(".yestoday").attr("onclick",clickfun+"('ds','3','"+url+"')");
			$(".today").attr("onclick",clickfun+"('ds','4'),'"+url+"')");
			$(".this_w").attr("onclick",clickfun+"('ds','5','"+url+"')");
			$(".this_m").attr("onclick",clickfun+"('ds','6','"+url+"')");
		}else{
			$(".search_cer").attr("onclick",clickfun+"('ds','0')");
			$(".last_m").attr("onclick",clickfun+"('ds','1')");
			$(".last_w").attr("onclick",clickfun+"('ds','2')");
			$(".yestoday").attr("onclick",clickfun+"('ds','3')");
			$(".today").attr("onclick",clickfun+"('ds','4')");
			$(".this_w").attr("onclick",clickfun+"('ds','5')");
			$(".this_m").attr("onclick",clickfun+"('ds','6')");
		}
		
		$(".ant-tabs-ink-bar").css({"left":"11px","right":"422px"});
		$(".ant-tabs-ink-bar-transition-backward").css({"-webkit-transition": "right .3s cubic-bezier(.645,.045,.645,1) .09s,left .3s cubic-bezier(.645,.045,.645,1)",
	    "transition": "right .3s cubic-bezier(.645,.045,.645,1) .09s,left .3s cubic-bezier(.645,.045,.645,1)",
	    "-webkit-transform": "translateZ(0)",
	    "transform": "translateZ(0)"});
		if(clickfun=="reloadDsTgBigData"){
			reloadDsTgBigData('ds',' ');
			}else{
				reloadZjsTgBigData('ds',' ');
			}
		
	} else if(main == "nav_dsqd"){/*电商、转介绍-无效客资分析*/
		if(!$(".nav_dsqd").hasClass("nav_on")){
			$(".nav_dsqd").addClass("nav_on");
			$(".nav_dstg").removeClass("nav_on")
		}
		if(url){
			$(".search_cer").attr("onclick",clickfun+"('wx','0','"+url+"')");
			$(".last_m").attr("onclick",clickfun+"('wx','1','"+url+"')");
			$(".last_w").attr("onclick",clickfun+"('wx','2','"+url+"')");
			$(".yestoday").attr("onclick",clickfun+"('wx','3','"+url+"')");
			$(".today").attr("onclick",clickfun+"('wx','4'),'"+url+"')");
			$(".this_w").attr("onclick",clickfun+"('wx','5','"+url+"')");
			$(".this_m").attr("onclick",clickfun+"('wx','6','"+url+"')");
		}else{
			$(".search_cer").attr("onclick",clickfun+"('wx','0')");
			$(".last_m").attr("onclick",clickfun+"('wx','1')");
			$(".last_w").attr("onclick",clickfun+"('wx','2')");
			$(".yestoday").attr("onclick",clickfun+"('wx','3')");
			$(".today").attr("onclick",clickfun+"('wx','4')");
			$(".this_w").attr("onclick",clickfun+"('wx','5')");
			$(".this_m").attr("onclick",clickfun+"('wx','6')");
		}
		
		if($(".nav_dsqd").hasClass("nav_on")){
			$(".ant-tabs-ink-bar-transition-backward").css({"-webkit-transition": "right .3s cubic-bezier(.645,.045,.645,1),left .3s cubic-bezier(.645,.045,.645,1) .09s",
				"transition": "right .3s cubic-bezier(.645,.045,.645,1),left .3s cubic-bezier(.645,.045,.645,1) .09s",
			    "-webkit-transform": "translateZ(0)",
			    "transform": "translateZ(0)"});
			}else{
				$(".ant-tabs-ink-bar-transition-backward").css({"-webkit-transition": "right .3s cubic-bezier(.645,.045,.645,1) .09s,left .3s cubic-bezier(.645,.045,.645,1)",
					"transition": "right .3s cubic-bezier(.645,.045,.645,1) .09s,left .3s cubic-bezier(.645,.045,.645,1)",
				    "-webkit-transform": "translateZ(0)",
				    "transform": "translateZ(0)"});
			}
		$(".ant-tabs-ink-bar").css({"left":"175px","right":"262px"});
		if(clickfun=="reloadDsTgBigData"){
			reloadDsTgBigData('wx',' ',url);
			}else{
				reloadZjsTgBigData('wx',' ',url);
			}
	}else{/*转介绍邀约大数据*/
		$(".ant-tabs-ink-bar").css({"left":"346px","right":"115px"});
		
		$(".search_cer").attr("onclick","getZjsYyBigData('0')");/*搜索*/
		$(".last_m").attr("onclick","getZjsYyBigData('1')");/*上月*/
		$(".last_w").attr("onclick","getZjsYyBigData('2')");/*上周*/
		$(".yestoday").attr("onclick","getZjsYyBigData('3')");/*昨日*/
		$(".today").attr("onclick","getZjsYyBigData('4')");/*今日*/
		$(".this_w").attr("onclick","getZjsYyBigData('5')");/*本周*/
		$(".this_m").attr("onclick","getZjsYyBigData('6')");/*本月*/
		getZjsYyBigData();
	}
}
/*电商邀约大数据里面点击部门展示人员*/
function showRenyuanAll(deptid,dept_id,money){
	var alldept=$("."+deptid);
	var all_dept=$("."+dept_id);
	alldept.each(function(){
		if($(this).hasClass("hide")){
			$(this).removeClass("hide");
			/*if($(".box-bg-"+deptid).find("td").hasClass('bgf9')){
				$(".box-bg-"+deptid).find("td").removeClass('bgf9');
			}*/
			if(!$(".box-bg-"+deptid).find("td").hasClass('box-bg')){
				$(".box-bg-"+deptid).find("td").addClass('box-bg');
			}
			if($(".box-hj-bg-"+deptid).find("td").hasClass('box-bg')){
				$(".box-hj-bg-"+deptid).find("td").removeClass('box-bg').addClass('box-hj-bg');
			}
		}else{
			$(this).addClass("hide");
			/*if(!$(".box-bg-"+deptid).find("td").hasClass('bgf9')){
				$(".box-bg-"+deptid).find("td").addClass('bgf9');
			}*/
			if($(".box-bg-"+deptid).find("td").hasClass('box-bg')){
				$(".box-bg-"+deptid).find("td").removeClass('box-bg');
			}
			if($(".box-hj-bg-"+deptid).find("td").hasClass('box-hj-bg')){
				$(".box-hj-bg-"+deptid).find("td").removeClass('box-hj-bg');
			}
			all_dept.each(function(){
				if(!$(this).hasClass("hide")){
					$(this).addClass("hide");
					}
			});
		}
	});
}

/* 大数据渠道多选 */
function showDataAllSource() {
	if (!$(".data_source_checkbox").hasClass("open_checkbox")) {
		$(".data_source_checkbox").addClass("open_checkbox");
		$(".data_allsource").css("visibility", "visible").css("opacity", "1");
		$(".data_bgfff_shadow").removeClass('hide').attr('onclick','hideDataSelectBox("data_source_checkbox","data_allsource")');
		$(".data_source_checkbox_icon").css({
			'opacity' : 1,
			'top': '0px',
			'visibility' : 'visible',
			'transform' : 'rotate(180deg)'
		});
	} else {
		$(".data_source_checkbox").removeClass("open_checkbox");
		$(".data_allsource").css("visibility", "hidden").css("opacity", "0");
		$(".bgfff_shadow").addClass('hide').attr('onclick', '');
		$(".data_source_checkbox_icon").css({
			'opacity' : 1,
			'top': '3px',
			'visibility' : 'visible',
			'transform' : 'rotate(0deg)'
		});
	}
}

/** -- 更改大数据渠道 --* */
function changeDataSrcList(roleType) {

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
						sourcestr += '<div class="more_select_li" data-title="全部渠道" style="font-weight: 700;">';
						sourcestr += '<span class="checkonebox_0"><input attr-title="0" attr-name="全部渠道" name="source_box" checked="checked" class="checkone_0" onclick="sourceCheck(\'0\',\'全部渠道\')" type="checkbox" value="2" style="width:14px;"/></span>';
						sourcestr += '<span>全部渠道</span>';
						sourcestr += '</div>';
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
											})
						}
						$(".allsource .more_select_box").empty().append(sourcestr);
						$(".data_allsource .data_more_select_box").empty().append(sourcestr);
					}
				}
			});
}

/* 大数据中选中渠道复选框 */
function dataSourceCheck(i, name) {
	if (i == 0) {
		var checkbox = $(".data_checkone_0");
		var checkval = checkbox.val();
		var checkboxs = document.getElementsByName('data_source_box');
		if (checkval == 2) {
			for (var i = 0; i < checkboxs.length; i++) {
				checkboxs[i].checked = false;// 全不选
				checkboxs[i].value = "1";
			}
			$(".data_source_checkbox").empty().text('');
		} else {
			for (var i = 0; i < checkboxs.length; i++) {
				checkboxs[i].checked = true;// 全选
				checkboxs[i].value = "2";
			}
			$(".data_source_checkbox").empty().text('全部渠道');
		}
	} else {
		var obj = $(".data_checkone_" + i);
		if (obj.val() == "2") {
			$(".data_checkone_0").val('1');
			$(".data_checkone_0").prop('checked', false);
			$(".data_checkonebox_" + i)
					.empty()
					.append(
							'<input attr-title="'
									+ i
									+ '" attr-name="'
									+ name
									+ '" name="data_source_box" class="data_checkone_'
									+ i
									+ '" onclick="dataSourceCheck(\''
									+ i
									+ '\',\''
									+ name
									+ '\')" type="checkbox" value="1" style="width:14px;"/>');
		} else {
			$(".data_checkonebox_" + i)
					.empty()
					.append(
							'<input attr-title="'
									+ i
									+ '" attr-name="'
									+ name
									+ '" name="data_source_box" class="data_checkone_'
									+ i
									+ '" onclick="dataSourceCheck(\''
									+ i
									+ '\',\''
									+ name
									+ '\')" type="checkbox" value="2" checked="true" style="width:14px;"/>');
		}
		var names = getDataCheckedBoxSourceIds('name');
		$(".data_source_checkbox").empty().text(names);
	}
}
/* 获取大数据中 复选渠道选中的渠道id */
function getDataCheckedBoxSourceIds(type) {
	/*
	 * type==ids,获取选中的渠道的id集合 type==name,获取选中渠道的name集合
	 */
	var checkboxs = document.getElementsByName('data_source_box');
	var valall = '';
	if ($(".data_checkone_0").val() == '2') {
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
function hideDataSelectBox(checkbox, all) {
	$("." + checkbox).removeClass("open_checkbox");
	$("." + all).css("visibility", "hidden").css("opacity", "0");
	if (!$(".data_bgfff_shadow").hasClass('hide'))
		$(".data_bgfff_shadow").addClass('hide').attr('onclick', '');
	$("." + checkbox + "_icon").css({
		'opacity' : 1,
		'top': '3px',
		'visibility' : 'visible',
		'transform' : 'rotate(0deg)'
	});
}