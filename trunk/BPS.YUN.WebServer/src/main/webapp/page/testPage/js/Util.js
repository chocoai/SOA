/*
	调用ajax
*/
function invokeAjax(serviceUrl, httpMethod, sucOpeFunc, errorOpeFunc){
	$.ajax({
		url: serviceUrl,
		type: httpMethod,
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		success: function(response){
			sucOpeFunc(response);
		} ,
        error: function(httpRequestObj, message, exceptionObj){
        	errorOpeFunc(httpRequestObj, message, exceptionObj);
        }
	});
}

/*
	获取当前时间，并格式化为yyyy-MM-dd HH:mm:ss
*/

function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var year = date.getFullYear()
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minite = date.getMinutes();
    var second = date.getSeconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (day >= 0 && day <= 9) {
        day = "0" + day;
    }
    if(hour >= 0 && hour <= 9){
        hour = "0" + hour;
    }
    if(minite >= 0 && minite <= 9){
        minite = "0" + minite;
    }
    if(second >= 0 && second <= 9){
        second = "0" + second;
    }

    var currentdate = year + seperator1 + month + seperator1 + day + " " + hour + seperator2 + minite + seperator2 + second;
    return currentdate;
}
