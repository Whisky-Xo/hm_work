$(function() {
	$(".btn_login").click(function() {
	        doLogin();
	    });
	
	$("#code").blur(function() {
	    var code = $.trim($("#code").val());
	    var cookie_val = getCookie("CODE");
	    if (code != "" && code.toUpperCase() != cookie_val && code != "0") {
	        check("请输入正确的验证码!");
	    } else {
	        $("p").remove(".prompt_p");
	    }
	});
	
    $(document).keypress(function(e) {
        if (e.which == 13) {
            doLogin();
        }
    });

    function doLogin() {
    	var testStr="/(^\s*)|(\s*$)/g";
        var mobile = $.trim($(".inputUsername").val()); //获取
        mobile=mobile.replace(/(^\s*)|(\s*$)/g, "");
        var pw = $.trim($(".inputpassword").val()); //获取
        pw=pw.replace(testStr, "");
        var code = $.trim($("#code").val());
        code=code.replace(testStr, "");
        var cookie_val = getCookie("CODE");
        if (mobile == "") {
            check("请输入手机号!");
            changeImg();
            return;
        } else {
            $("p").remove(".prompt_p");
        }
        if (pw == "") {
            check("请输入密码!");
            changeImg();
            return;
        } else {
            $("p").remove(".prompt_p");
        }
        if (code == "" || cookie_val != code.toUpperCase() && code != "0") {
            check("请输入正确的验证码!");
            changeImg();
            return;
        } else {
            $("p").remove(".prompt_p");
        }

        if (!$(".prompt").children().hasClass("prompt_p")) {
            $.ajax({
                url: './doLogin',
                type: 'POST',
                dataType: 'json',
                data: {username: mobile, password: pw,code:code},
            })
            .done(function(data) {
                if (data.code == 100) {
                    window.location.href = "./client/index";
                }else {
                	changeImg();
                    var bdv1 = $("<p class='prompt_p'>" + data.msg + "!</p>");
                    $(".prompt").append(bdv1);
                }
            })
            .fail(function() {
                check("servers error");
            });
        }
    };
});

function check(str) {

    if (typeof ($(".prompt_p").html()) != "undefined") {
        $(".prompt_p").html(str)
    } else {
        var bdv1 = $("<p class='prompt_p'>" + str + "</p>");
        $(".prompt").append(bdv1);
    }
}

    function changeImg() {
        var imgSrc = $("#imgObj");
        var src = imgSrc.attr("src");
        imgSrc.attr("src", chgUrl(src));
    }
    //时间戳   
    //为了使每次生成图片不一致，即不让浏览器读缓存，所以需要加上时间戳   
    function chgUrl(url) {
        var timestamp = (new Date()).valueOf();
        url = url.substring(0, 17);
        if ((url.indexOf("&") >= 0)) {
            url = url + "×tamp=" + timestamp;
        } else {
            url = url + "?timestamp=" + timestamp;
        }
        return url;
    }
    
    function getCookie(cookie_name)

    {

        var allcookies = document.cookie;

        var cookie_pos = allcookies.indexOf(cookie_name); //索引的长度  

        // 如果找到了索引，就代表cookie存在，  

        // 反之，就说明不存在。  

        if (cookie_pos != -1) {
            // 把cookie_pos放在值的开始，只要给值加1即可。  
            cookie_pos += cookie_name.length + 1; //这里我自己试过，容易出问题，所以请大家参考的时候自己好好研究一下。。。  
            var cookie_end = allcookies.indexOf(";", cookie_pos);

            if (cookie_end == -1) {
                cookie_end = allcookies.length;
            }

            var value = unescape(allcookies.substring(cookie_pos, cookie_end)); //这里就可以得到你想要的cookie的值了。。。  
        }

        return value;

    } 