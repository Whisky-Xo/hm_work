!(function (window, document, Math) {
	var KouBeiTabs = function(el,options){
		this.el = el;
	    this.defaults = {
	        'anime' : true,
	        'callback' : {
	        	'kbcb_after' : '', // 事件运行前
	        	'kbcb_before' : '', // 事件结束
	        	'kbcb_clicktab' : '' // 点击菜单回调
	        }
	    };
	    this.page = new Array();
	    this.prevstep = undefined; // 前一个对象
	    this.options = $.extend({},this.defaults,options);
	}
	KouBeiTabs.prototype = {
		Start:function(){
			this.Reset();
		},
		// 初始化内容并生成
		Reset:function(){
			if(this.el.data('active') == undefined){
				this.el.data('active',0);
			}
			if(this.el.find('.kb-tabs-bar').length == 0){
				this.el.find('.kb-tabs-ul').before('<div class="kb-tabs-bar"></div>');
			}
			this._SetEleClass();
			this._Bind(this.el);
			this._Init(this.el);
		},
		// 绑定事件
		_Bind:function(el){
			var ele = this;
			// 选择 Tab
			$(el).on('click', 'li.kb-tabs-li', function() {
				// 点击回调
				if(ele.options.callback.kbcb_clicktab != undefined && ele.options.callback.kbcb_clicktab != ''){
					ele.options.callback.kbcb_clicktab($(this),$(this).index());
				}

				$(this).parent('.kb-tabs-ul').find('.kb-tabs-li').each(function(index,e) {
					$(e).removeClass('kb-tabs-active');
				});
				$(this).addClass('kb-tabs-active');
				$(el).data('active',$(this).index());
				ele._Init();
			});
		},
		// 核心函数
		_Init:function(el){
			// 开始回调
			if(this.options.callback.kbcb_before != undefined && this.options.callback.kbcb_before != ''){
				this.options.callback.kbcb_before();
			}

			if(el == undefined){
				el = this.el;
			}
			var el_index = $(el).data('active');
				el_li	= $(el).find('.kb-tabs-li'),
				el_li_this = $(el_li).eq(el_index);
				el_ul		= $(el).find('.kb-tabs-ul'),
				el_bar		= $(el).find('.kb-tabs-bar');
			var l = $(el_li_this).offset().left - $(el_ul).offset().left,
				r = $(el_ul).outerWidth() + $(el_ul).offset().left - ($(el_li_this).offset().left + $(el_li_this).outerWidth());

			if(this.prevstep == undefined){
				this.prevstep = el_index;
			}

			$(el_li_this).addClass('kb-tabs-active');
			// 是否需要动画
			if(this.options.anime){
				$(el_bar).removeClass('kb-tabs-bar-back');
				$(el_bar).removeClass('kb-tabs-bar-forward');
				$(el_bar).addClass('kb-tabs-anime');

				if($(el_bar).css('left').replace(/[^0-9]/ig, "") < l){
					// 往前
					$(el_bar).addClass('kb-tabs-bar-forward');
					this.dir = 'right';
				}else if($(el_bar).css('left').replace(/[^0-9]/ig, "") > l){
					// 往后
					$(el_bar).addClass('kb-tabs-bar-back');
					this.dir = 'left';
				}else{
					this.dir = '';
				}

				// 给绑定的元素添加动画
				this._RemoveEleClass();
				var prev = this.prevstep;
				switch(this.dir){
					case 'right':
						$(this.page).each(function(index, ele) {
							$(ele).eq(prev).addClass('kb-move-RightOut');
							$(ele).eq(el_index).addClass('kb-move-LeftIn');
						});
					break;
					case 'left':
						$(this.page).each(function(index, ele) {
							$(ele).eq(prev).addClass('kb-move-LeftOut');
							$(ele).eq(el_index).addClass('kb-move-RightIn');
						});
					break;
					default:break;
				}
				
			}
			// 将需要显示的内容显示出来
			if(this.dir != ''){
				if(this.prevstep == undefined){
					if(this.dir == 'left'){
						this.prevstep = el_index + 1;
					}else if(this.dir == 'right'){
						this.prevstep = el_index - 1;
					}
				}

				var prev = this.prevstep;
				$(this.page).each(function(index, ele) {
					$(ele).each(function(index, el) {
						$(this).addClass('kb-hide');
					});
					$(ele).eq(prev).removeClass('kb-hide');
					$(ele).eq(el_index).removeClass('kb-hide');
				});
			}
			// 开始
			if(this.dir != ''){
				var hide = this;
				setTimeout(function () {
					hide._RemoveEleClass();
	        		hide._HideEleClass(el_index);
	    		},400);
			}else{
				this._RemoveEleClass();
        		this._HideEleClass(el_index);
			}
			
			// 定义样式
			$(el_bar).css({
				'left': l,
				'right': r
			});

			if(this.dir != ''){
				this.prevstep = el_index;
			}
			// 结束回调
			if(this.options.callback.kbcb_after != undefined && this.options.callback.kbcb_after != ''){
				this.options.callback.kbcb_after();
			}
		},
		// 添加元素
		addEle:function(parent,chlid){
			this.page.push($(parent).find(chlid));
		},
		// 动画类控制
		_HideEleClass:function(el_index){
			$(this.page).each(function(index, ele) {
				$(ele).each(function(index, el) {
					$(this).addClass('kb-hide');
				});
				$(ele).eq(el_index).removeClass('kb-hide');
			});
		},
		_SetEleClass:function(){
			$(this.page).each(function(index, ele) {
				$(ele).each(function(index, el) {
					$(this).parent().css({
						'overflow': 'hidden',
						'position': 'relative'
					});
				});
			});
		},
		_RemoveEleClass:function(){
			$(this.page).each(function(index, ele) {
				$(ele).each(function(index, el) {
					$(this).addClass('kb-tabs-page-move');
					$(this).removeClass('kb-move-LeftIn');
					$(this).removeClass('kb-move-LeftOut');
					$(this).removeClass('kb-move-RightIn');
					$(this).removeClass('kb-move-RightOut');
				});
			});
		}
	}
	window.KouBeiTabs = KouBeiTabs;
})(window, document, Math);