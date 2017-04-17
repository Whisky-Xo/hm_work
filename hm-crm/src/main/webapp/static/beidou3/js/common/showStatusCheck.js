$(function(){
/*渠道*/
	var sourceid=$(".source_hide").val();
	if(sourceid){
		var sourcearr = sourceid.split(',');
		/*$("#sourceckeck").empty();*/
		$.each(sourcearr,function(k,v){
			if(v=="0"){
				$("#sourceckeck").append("<span class='allsource' title='0'>全部渠道</span>");
			}else{
				$(".sourcebox"+v).val("2");
				$(".sourcebox"+v).attr("checked",true);
				var status="<span class='checksource"+v+"' title='"+v+"'>"+$(".sourcebox"+v).attr("title")+",</span>";
				if(!$(".allsource").hasClass("hide")){
					$(".allsource").remove();
				}
				$("#sourceckeck").empty().append(status);
			}
		})
	}else{
		$("#sourceckeck").append("<span class='allsource' title=''>全部渠道</span>");
	}
	
})
/*状态*/
function showStatusCheck(){
	if($(".status_block").hasClass("hide")){
		$(".status_block").removeClass("hide");
		$(".check_status").removeClass("hide");
		/*$("#statusckeck").css("border","1px solid #66afe9");*/
	}else{
		$(".status_block").addClass("hide");
		$(".check_status").addClass("hide");
		/*$("#statusckeck").css("border","1px solid #ccc");*/
	}
}
function checkStatus(statucid){
	if($(".checkbox"+statucid).val()=="1"){
		$(".checkbox"+statucid).val("2");
		$(".checkbox"+statucid).attr("checked",true);
		var status="<span class='checkappend"+statucid+"' title='"+statucid+"'>"+$(".checkbox"+statucid).attr("title")+",</span>";
		if(!$(".allstatus").hasClass("hide")){
			$(".allstatus").remove();
		}
		$("#statusckeck").append(status);
	}else{
		$(".checkbox"+statucid).val("1");
		$(".checkbox"+statucid).attr("checked",false);
		$(".checkappend"+statucid).remove();
		var check_length=$("#statusckeck span");
		if(check_length.length<1){
			$("#statusckeck").append("<span class='allstatus' title='0'>全部状态</span>");
		}
	}
}
/*渠道*/
function showSourceCheck(){
		if($(".source_block").hasClass("hide")){
			$(".source_block").removeClass("hide");
			$(".check_source").removeClass("hide");
			/*$("#sourceckeck").css("border","1px solid #66afe9");*/
		}else{
			$(".source_block").addClass("hide");
			$(".check_source").addClass("hide");
			/*$("#sourceckeck").css("border","1px solid #ccc");*/
		}
	}
function checkSource(sourceid){
		if($(".sourcebox"+sourceid).val()=="1"){
			$(".sourcebox"+sourceid).val("2");
			$(".sourcebox"+sourceid).attr("checked",true);
			var source="<span class='checksource"+sourceid+"' title='"+sourceid+"'>"+$(".sourcebox"+sourceid).attr("title")+",</span>";
			if(!$(".allsource").hasClass("hide")){
				$(".allsource").remove();
			}
			$("#sourceckeck").append(source);
		}else{
			$(".sourcebox"+sourceid).val("1");
			$(".sourcebox"+sourceid).attr("checked",false);
			$(".checksource"+sourceid).remove();
			var check_length=$("#sourceckeck span");
			if(check_length.length<1){
				$("#sourceckeck").append("<span class='allsource' title=''>全部渠道</span>");
			}
		}
	}
