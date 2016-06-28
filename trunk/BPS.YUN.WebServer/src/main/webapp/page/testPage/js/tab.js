//  tabchange

/*switchTab(选项卡标题ID,选项内容总ID,事件【mouseover/click】,出现状态【show/slideDown/fadeIn】,默认显示第几个【0/1/2】)*/
function switchTab(tabid,tabbox,events,effect,num){
var n=num;
$(tabid+">li").eq(n).attr("class","current").siblings(":not(.none)").attr("class","normal");
$(tabbox+">div").eq(n).show().siblings().hide();
$(tabid+" li:not(.none)").bind(events,function(){
$(this).attr("class","current").siblings(":not(.none)").attr("class","normal");
$n=$(tabid+">li").index($(this)[0]);	
switch(effect){
case "slide":
$(tabbox+">div").eq($n).slideDown().siblings().hide();
case "show":
$(tabbox+">div").eq($n).show().siblings().hide();
case "fadeIn":
$(tabbox+">div").eq($n).fadeIn().siblings().hide();
default:
$(tabbox+">div").eq($n).show().siblings().hide();
}	
})
}



