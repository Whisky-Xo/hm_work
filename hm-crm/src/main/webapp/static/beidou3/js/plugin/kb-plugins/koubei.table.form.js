!(function (window, document, Math) {
	var KouBeiTableForm = function(el,options){
		this.el = el;
	    this.defaults = {
	        'anime' : true,
	        'callback' : {
	        	'kbcb_after' : '',
	        	'kbcb_before' : '',
	        	'kbcb_clicktab' : ''
	        }
	    };
	    /* 元素存储 */
	    this.ele = new Array();
	    this.ele.input = new Array();
	    this.ele.select = new Array();
	    /* 合并参数 */
	    this.options = $.extend({},this.defaults,options);

	}
	KouBeiTableForm.prototype = {
		Start:function(){
			// console.log(this.ele.select);
		},
		/* 添加各类元素 */
		AddInput:function(ele){

		},
		AddSelect:function(ele,options){
			var defaulopt = {
		        'search': false,
		        'linkselect' : '',
		        'callback': {
		        	'kbcb_open' : '', // 打开窗口
		        	'kbcb_select' : '', // 选择内容
		        }
		    };
		    ele.options = $.extend({},defaulopt,options);
			if($(this.el).find(ele).length == 0){
				return false;
			}
			var select = $(this.el).find(ele);
			// 元素配置
			$(select).find('.kb-select').after('<span class="kb-select-icon kb-icon-arrow-down"></span>');
			if(ele.options.search){
				$(select).find('.kb-overflow-y ul').before('<input type="text" class="kb-searchbox" >');
			}
			// 绑定元素事件
			$(select).on('click', '.kb-select-scroll', function(event) {
				event.preventDefault();
				event.stopPropagation();
			});
			$(select).on('click','', function() {
				if($(this).hasClass('kb-disabled')){
					return false;
				}
				$(this).toggleClass('kb-openselect');
				var open = false;
				if($(select).hasClass('kb-openselect')){
					open = true;
				}

				if(open){
					$(this).find('.kb-icon-arrow-down').css({
						'opacity': 1,
						'visibility': 'visible',
						'transform': 'rotate(180deg)'
					});
					// 选择框定位
					$(select).attr('style','');
					$(select).find('.kb-select-scroll').attr('style','');
					if($(window).height() - $(select).find('.kb-select-scroll').offset().top <= 10 && $(select).find('.kb-select-scroll').offset().top >= $(select).find('.kb-select-scroll').outerHeight()){
						var t = $(select).find('.kb-select-scroll').outerHeight() - $(select).outerHeight();
						$(select).find('.kb-select-scroll').css({
							top: 0 - t,
							height:h
						});
					}else if($(window).height() <= $(select).find('.kb-select-scroll').outerHeight() + $(select).find('.kb-select-scroll').offset().top){
						var w = $(select).find('.kb-select').outerWidth()+5,l = $(select).find('.kb-select-scroll').offset().left+5,h = $(select).find('.kb-select-scroll').outerHeight();
						if(h > $(window).height()){
							h = $(window).height() - 10;
						}
						$(select).find('.kb-select-scroll').css({
							position: 'fixed',
							top: '0',
							left : l,
							width : w,
							height: h
						});
					}
					if($(select).find('.kb-select-scroll li').length == 0){
						if($(select).find('.kb-notfound').length == 0){
							$(select).find('ul').before('<span class="kb-notfound">Not Found</span>');
						}
					}
					if(ele.options.search){
						$(select).find('.kb-searchbox').focus();
					}
				}else{
					$(this).find('.kb-icon-arrow-down').css({
						'transform': 'rotate(0deg)'
					});
					if($(this).find('.kb-select').html() != ''){
						$(select).find('.kb-icon-arrow-down').css({
							'opacity': 1,
							'visibility': 'visible'
						});
					}
				}
			});
			$(select).find('.kb-select-scroll').on('click', 'li', function(event) {
				var title = $(this).data('title');
				if(title == undefined){
					title = $(this).html();
				}
				$(select).find('.kb-select').html(title);
				$(select).find('.kb-icon-arrow-down').css({
					'opacity': 1,
					'visibility': 'visible'
				});
				$(select).removeClass('kb-openselect');
				// 点击回调
				if(ele.options.callback.kbcb_select != undefined && ele.options.callback.kbcb_select != ''){
					ele.options.callback.kbcb_select($(this),$(select));
				}
			});
			$(select).on('click', '.kb-icon-min-close', function() {
				$(select).find('.kb-icon-min-close').css({
					'opacity': 0,
					'visibility': 'hidden'
				});
				$(select).find('.kb-icon-arrow-down').css({
					'opacity': 1,
					'visibility': 'visible',
					'transform': 'rotate(0deg)'
				});
				$(select).find('.kb-select').html('');
				return false;
			});
			// 搜索事件
			if(ele.options.search){
				$(select).on('propertychange input', '.kb-searchbox', function(event) {
					event.preventDefault();
					var searchtext = $(this).val();
					var n = 0;
					$(select).find('li').each(function(index, el) {
						$(this).hide();
						if($(this).html().indexOf(searchtext) != -1){
							$(this).show();
							n++;
						}
					});
					if(n == 0){
						if($(select).find('.kb-notfound').length == 0){
							$(select).find('.kb-searchbox').after('<span class="kb-notfound">Not Found</span>');
						}
					}else{
						if($(select).find('.kb-notfound').length != 0){
							$(select).find('.kb-notfound').remove();
						}
					}
				});
			}
			// 初始化
			if($(select).data('active') != undefined){
				$(select).find('.kb-select-scroll').find('li').eq($(select).data('active')).click();
			}
		}
	}
	// <span class="kb-notfound">Not Found</span>
	window.KouBeiTableForm = KouBeiTableForm;
})(window, document, Math);