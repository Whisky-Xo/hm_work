jQuery("html").addClass('bonfire-html-onload');

/* disable browser scroll on touch devices */
jQuery(document.body).on("touchmove", function(e) {
    e.preventDefault();
});

/* disable browser scroll on desktop */
var scrollPosition = [
self.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft,
self.pageYOffset || document.documentElement.scrollTop  || document.body.scrollTop
];
var html = jQuery('html');
html.data('scroll-position', scrollPosition);
html.data('previous-overflow', html.css('overflow'));
html.css('overflow', 'hidden');
window.scrollTo(scrollPosition[0], scrollPosition[1]);

jQuery(window).load(function() {	
		var html = jQuery('html');
		var scrollPosition = html.data('scroll-position');
		html.css('overflow', html.data('previous-overflow'));
		window.scrollTo(scrollPosition[0], scrollPosition[1]);
		/* slide down html */
		jQuery("html").removeClass('bonfire-html-onload');
	
});
function resizenow() {
	var browserwidth = jQuery(window).width();
	var browserheight = jQuery(window).height();
	jQuery('.bonfire-pageloader-icon').css('right', ((browserwidth - jQuery(".bonfire-pageloader-icon").width())/2)).css('top', ((browserheight - jQuery(".bonfire-pageloader-icon").height()-200)/2));
	jQuery('.reload_word').css('right', ((browserwidth - jQuery(".reload_word").width())/2)).css('top', (((browserheight - jQuery(".reload_word").height())-300)/2));
};