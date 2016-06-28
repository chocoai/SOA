SecretTool = {};
SecretTool.innerObj = null;

SecretTool.init = function(){
	innerObj = new ActiveXObject("SecretDog.SecretTool");
	innerObj.admin();
}

SecretTool.encrypt = function(item){
	if( innerObj == null )
		init();

	if( typeof(item) == "string" )
		return innerObj.encrypt(item);
	return item;
}

SecretTool.decrypt = function(item){
	if( innerObj == null )
		init();

	if( typeof(item) == "string" && innerObj.isEncrypt(item) )
		return innerObj.decrypt(item);
	return item;
}