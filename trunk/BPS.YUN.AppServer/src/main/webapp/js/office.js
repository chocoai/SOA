var Office = {};

/**
 * �������ƿռ�
 */
Office.getSharePoint_OpenDocuments = function() {
	if (window.SharePoint_OpenDocuments) {
		return window.SharePoint_OpenDocuments;
	}
	else {
		var progids = ["SharePoint.OpenDocuments.3", "SharePoint.OpenDocuments.2", "SharePoint.OpenDocuments.1"];
		for (var i = 0; i < progids.length; ++i) {
			try {
				return (window.SharePoint_OpenDocuments = new ActiveXObject(progids[i]));
			}
			catch (e)
			{ }
		}
		return null;
	}
}
Office.onWordClose = function(url){
	//TODO ֪ͨApp�ͷ���Դ
}
Office.onExcelClose = function(url){
	
}
Office.openOfficeDocument = function(url, readOnly)
{
	var openDocObj; 
    openDocObj = getSharePoint_OpenDocuments(); 
	if( openDocObj != null && openDocObj != undefined ){
		if( readOnly == true )
			openDocObj.ViewDocument(url);
		else
		    openDocObj.EditDocument(url);
	}
}
/**
 * ���ߴ�Excel�ĵ�
 * @param url �ĵ�·��
 * @param user �û�����
 * @param readOnly �Ƿ�ֻ��
 * @param trackRevisions �Ƿ����༭�ۼ�
 */
Office.openExcel = function(url, user, readOnly, trackRevisions) 
{
	var oExcel = null;
	var oWorkbook = null;
	try{
		oExcel = new ActiveXObject("Excel.Application");
		oWorkbook = oExcel.Workbooks.Open(url, false, readOnly);
		oExcel.DisplayAlerts = false;
		oExcel.Visible = true;
		oExcel.UserName = user;
		oWorkbook.KeepChangeHistory = trackRevisions;

		eval("function oWorkbook::Close() {return Office.onExcelClose(url);}");
	}
	catch(e){
		alert("�ĵ��򿪴���" + e.message);
		if( oExcel != null )
			oExcel.Quit();
	}
	return false;
}
/**
 * ���ߴ�Word�ĵ�
 * @param url �ĵ�·��
 * @param user �û�����
 * @param readOnly �Ƿ�ֻ��
 * @param trackRevisions �Ƿ����༭�ۼ�
 */
Office.openWord = function(url, user, readOnly, trackRevisions) 
{
	var wordApp = null;
	var wordDocument = null;
	try{
		wordApp = new ActiveXObject("Word.Application");       
		
		wordApp.Documents.Open(url, false, readOnly, false, "", "", false, "", "", 0);
		wordApp.Visible = true;
		wordApp.Activate();
		
		wordDocument = wordApp.ActiveDocument;

		if( trackRevisions ){ //�����ۼ�
			 wordDocument.TrackRevisions = true ;
			 wordDocument.ShowRevisions = true  ;
		}
		else{
			 wordDocument.TrackRevisions = false ;
			 wordDocument.ShowRevisions = false  ;           
		}      
		wordDocument.Application.UserName= user;
		eval("function wordDocument::Close() {return Office.onWordClose(url);}");
		return true;
	}
	catch(e){
		alert("�ĵ��򿪴���" + e.message);
		if( wordApp != null )
			wordApp.Quit();
	}
	return false;
}
