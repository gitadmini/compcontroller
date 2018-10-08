
var hand = 0; // 手在屏幕上的操作分为2钟：1.点击（按下和收起坐标一致） 2.滑动（按下和收起坐标不一致）
var requestHand = 0; // 由手的操作转化为远程的控制操作分为3钟：1单击（左击），2.双击（左击），3.右击，4.滑动
var clickCount = 0; // 连续左击次数
var touchStartTime = 0; //屏幕按下的时间戳
var lastClickEndTime = 0; //上次点击结束的时间戳
var touchStartX = 0;
var touchStartY = 0;
var touchEndX = 0;
var touchEndY = 0;
var touchOldX = 0; //touchmove移动前的x坐标
var touchOldY = 0;

//创建一个Socket实例
var socket = new WebSocket('ws://' + host_ip + ':80/socket/slip');
function reconnect(){
	setTimeout(function(){
		socket = new WebSocket('ws://' + host_ip + ':80/socket/slip');
		socket.onerror = reconnect;
	},1000); //防止弄死客户端
}
socket.onerror = reconnect;

//步骤：
//1.若hand为滑动，完成
//2.hand一定为点击，判断收起和按下的时间差，若超过定义时间，则直接请求右击操作，完成
//3.requestHand一定为左击，clickCount++，500ms后发起请求（单击或双击）--->时间到，读取clickCount并置0，根据clickCount发出请求

var canvas1=document.getElementById('canvas-1'); //背景
var ctx1=canvas1.getContext('2d');

var canvas2=document.getElementById('canvas-2'); //操作
var ctx2=canvas2.getContext('2d');

var imgFinger = new Image();
imgFinger.src = "/mouse/finger.PNG";
var imgBack = new Image();
imgBack.src = "/mouse/mouseback.jpg";
//待图片加载完后，将其显示在canvas上
imgBack.onload = function(){ 
	ctx1.drawImage(this, 0, 0);
}
if(!rec){
	$("#stop").css("display","none"); //隐藏stop
}else{
	$("#start").css("display","none");
}

$(canvas2).on('touchstart',function(e) {
	touchStartX = e.originalEvent.changedTouches[0].pageX;
	touchStartY = e.originalEvent.changedTouches[0].pageY;
	touchOldX = touchStartX;
	touchOldY = touchStartY;
	touchStartTime = new Date().getTime();
    ctx2.drawImage(imgFinger, touchStartX - 30, touchStartY - 30,61,60);
});
$(canvas2).on('touchmove',function(e) {
	e.preventDefault(); //阻止滚动
	var x = e.originalEvent.touches[0].pageX;
	var y = e.originalEvent.touches[0].pageY;
	var oldX = touchOldX;
	var oldY = touchOldY;
	touchOldX = x;
	touchOldY = y;
	requestSlip(oldX,oldY,x,y);
});
$(canvas2).on('touchend',function(e) {
	clearCanvas(canvas2);
	touchEndX = e.originalEvent.changedTouches[0].pageX;
	touchEndY = e.originalEvent.changedTouches[0].pageY;
    var time = new Date().getTime();
    if(touchStartX!=touchEndX||touchStartY!=touchEndY){
    	//滑动
    }else if((time - touchStartTime) > 500){
    	//右击
    	requestRightClick();
    }else{
    	//单击或双击
    	clickCount++;
    	lastClickEndTime = time;
    	setTimeout("requestLeftClick()",500);
    }
});

//x为横坐标位移（电脑上），y为纵坐标位移
function requestSlip(oldX,oldY,targetX,targetY){
	
	var x = targetX - oldX;
	var y = targetY - oldY;
	// 发送一个初始化消息
	socket.send(x + ',' + y);
}

function requestRightClick(){
	
	$.post('/m/right',
	    {
	    },
	    function(data,status){						  	    	
	    });
}

//单击或者双击
function requestLeftClick(){
	
	var nowClickCount = clickCount;
	clickCount = 0; //置0
	if(nowClickCount==1){
		$.post('/m/click',
			{
		    },
		    function(data,status){						  	    	
		    });
	}else if(nowClickCount>1){
		$.post('/m/double',
			{
		    },
		    function(data,status){						  	    	
		    });
	}else{
		
	}
}

//输入字符串
function str(){
	var s = $("#str").val();
	$("#str").val("");
	$("#str").attr("placeholder",s);
	$("#str").focus();
	$.post('/m/str',
		{
		str:s,
	    },
	    function(data,status){						  	    	
	    });
}

//开始录制
function rec_start(){
	
	$.post('/m/start_rec',
		{
	    },
	    function(data,status){						  	    	
	    });
	$("#start").css("display","none");
	$("#stop").css("display","");
}

//结束录制
function rec_stop(){
	
	$.post('/m/stop_rec',
		{
	    },
	    function(data,status){						  	    	
	    });
	$("#start").css("display","");
	$("#stop").css("display","none");
}

//键盘按键
function keyboard(str){
	
	$.post('/m/keyboard',
		{
		str:str,
	    },
	    function(data,status){						  	    	
	    });
}

//播放录制
function play(str){
	
	$.post('/m/play',
		{
		name:str,
	    },
	    function(data,status){						  	    	
	    });
}

//停止播放
function killPlay(){
	
	$.post('/m/kill_play',
		{
	    },
	    function(data,status){						  	    	
	    });
}

//清除画布
function clearCanvas(c)
{  
	c.height=c.height;  
}  

