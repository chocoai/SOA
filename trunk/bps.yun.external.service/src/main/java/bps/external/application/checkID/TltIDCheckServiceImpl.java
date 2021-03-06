/**   
* @Title: TltIDCheckServiceImpl.java 
* @Package bps.external.application.checkID 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年10月14日 下午4:00:04 
* @version V1.0   
*/
package bps.external.application.checkID;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aipg.common.AipgReq;
import com.aipg.common.InfoReq;
import com.aipg.idverify.IdVer;
import com.allinpay.XmlTools;
import com.kinorsoft.tools.DuXMLDoc;

import bps.common.BizException;
import bps.common.ErrorCode;
import bps.external.application.Extension;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年10月14日 下午4:00:04  
 */
public class TltIDCheckServiceImpl implements  bps.external.application.service.checkID.TltIDCheckService{

	private static Logger tlt_logger = Logger.getLogger(TltIDCheckServiceImpl.class.getName());
	/* (非 Javadoc) 
	* <p>Title: checkIDNo</p> 
	* <p>Description: </p> 
	* @param name
	* @param IDNo
	* @return
	* @throws Exception 
	* @see bps.external.application.service.checkID.TltIDCheckService#checkIDNo(java.lang.String, java.lang.String) 
	*/
	public Map<String, Object> checkIDNo(String name, String IDNo)throws Exception {
		// TODO Auto-generated method stub
		try{
			tlt_logger.info("通联通身份验证开始:");
	        tlt_logger.info("name:" + name + "IDNo:" + IDNo);
	        
	        Map<String, Object> result = new HashMap<String, Object>();
	        
	        try{
	        	DateFormat format = new SimpleDateFormat("yyyy-MM-dd yyyy-MM-dd HH:mm:ss");
	        	tlt_logger.info("start:"+format.format(new Date()));
	            result = idVerify(name, IDNo);
	            tlt_logger.info("end:"+format.format(new Date()));
	        }catch(Exception e){
	            tlt_logger.info(e.getMessage(), e);
	            throw e;
	        }
	        String ret_code1    =  (String)result.get("RET_CODE1");
	        String err_msg1     = (String)result.get("ERR_MSG1");
	        String ret_code2    =  (String)result.get("RET_CODE");
	        String err_msg2     = (String)result.get("ERR_MSG");
	        if(ret_code1.equals("0000")){   //交易已处理
	            if(ret_code2.equals("0000")){    //成功
	                //验证成功
	                tlt_logger.info("checkIDNoSucess");
	                Map<String, Object> retuenValue = new HashMap<String, Object>();
	                retuenValue.put("command_result",   "1");
	                tlt_logger.info("retuenValue="+retuenValue);
	                return retuenValue;
	            }else{  //失败
	                Map<String, Object> retuenValue = new HashMap<String, Object>();
	                retuenValue.put("command_result",   "0");
	                retuenValue.put("ret_code1", ret_code1);
	                retuenValue.put("err_msg1",  err_msg1);
	                retuenValue.put("ret_code2", ret_code2);
	                retuenValue.put("err_msg2", err_msg2);
	                tlt_logger.info("2retuenValue="+retuenValue);
	                return retuenValue;
	            }
	        }else{  //交易失败
	            Map<String, Object> retuenValue = new HashMap<String, Object>();
	            retuenValue.put("command_result",   "0");
	            retuenValue.put("ret_code1", ret_code1);
	            retuenValue.put("err_msg1",  err_msg1);
	            retuenValue.put("ret_code2", ret_code2);
	            retuenValue.put("err_msg2", err_msg2);
	            tlt_logger.info("3retuenValue="+retuenValue);
	            return retuenValue;
	        }
		}catch(Exception e){
            tlt_logger.error(e.getMessage(), e);
			throw e;
		}
	}

	private static String userName = null;
    private static String password = null;
    private static String merchantId = null;
    private static String tltcerPath = null;
    private static String pfxPath = null;
    private static String pfxPassword = null;
    private static String url = null;
	 /**
     * 
     * 功能：国民身份验证 220001
     * @author DON
     * @throws Exception 
     */
    public Map<String,Object> idVerify(String name, String IDNO) throws Exception {
        tlt_logger.info("idVerify start");
        if(userName == null) {
            userName = Extension.paramMap.get("AlipayConfig.interfaces.userName");
        }
        if(password == null) {
            password = Extension.paramMap.get("AlipayConfig.interfaces.passWord");
        }
        if(merchantId == null) {
            merchantId = Extension.paramMap.get("AlipayConfig.interfaces.merchantId");
        }
        if(tltcerPath == null) {
            tltcerPath = Extension.paramMap.get("AlipayConfig.interfaces.tltcerPath");
        }
        if(pfxPath == null) {
            pfxPath = Extension.paramMap.get("AlipayConfig.interfaces.pfxPath");
        }
        if(pfxPassword == null) {
            pfxPassword = Extension.paramMap.get("AlipayConfig.interfaces.pfxPassword");
        }
        if(url == null) {
            url = Extension.paramMap.get("AlipayConfig.interfaces.serverUrl");
        }
        
        Boolean isTLTFront = false;
        String xml="";
        AipgReq aipgReq=new AipgReq();
        InfoReq info=makeReq("220001");
        aipgReq.setINFO(info);
        
        IdVer vbreq=new IdVer();
        
        vbreq.setNAME(name);
        vbreq.setIDNO(IDNO);
        aipgReq.addTrx(vbreq);

        tlt_logger.info("aipgReq="+aipgReq.toString());
        xml=XmlTools.buildXml(aipgReq,true);
        tlt_logger.info("xml="+xml.toString());
        String resp = sendToTlt(xml, isTLTFront, url);
        tlt_logger.info("resp="+resp);
        DuXMLDoc xmlDoc = new DuXMLDoc();
        return xmlDoc.xmlElementsObject(resp);
        //dealRet(resp);
    }

    
    /**
     * 组装报文头部
     * @param trxcod
     * @return
     *日期：Sep 9, 2012
     */
    private InfoReq makeReq(String trxcod)
    {
        InfoReq info=new InfoReq();
        info.setTRX_CODE(trxcod);
        info.setREQ_SN(merchantId+"-"+String.valueOf(System.currentTimeMillis()));
        info.setUSER_NAME(userName);
        info.setUSER_PASS(password);
        info.setLEVEL("5");
        info.setDATA_TYPE("2");
        info.setVERSION("03");
        if("300000".equals(trxcod)||"300001".equals(trxcod)||"300003".equals(trxcod)||"220001".equals(trxcod)||"220003".equals(trxcod)){
            info.setMERCHANT_ID(merchantId);
        }
        return info;
    }
    
    public String sendToTlt(String xml,boolean flag,String url) {
        try{
            if(!flag){
                xml=this.signMsg(xml);
            }else{
                xml=xml.replaceAll("<SIGNED_MSG></SIGNED_MSG>", "");
            }
            return sendXml(xml,url,flag);
        }catch(Exception e){
            e.printStackTrace();
            if(e.getCause() instanceof ConnectException||e instanceof ConnectException){
                System.out.println("请求链接中断，请做交易结果查询，以确认上一笔交易是否已被通联受理，避免重复交易");
            }
        }
        return "请求链接中断，请做交易结果查询，以确认上一笔交易是否已被通联受理，避免重复交易";
    }
    
    public String sendXml(String xml,String url,boolean isFront) throws UnsupportedEncodingException, Exception{
        System.out.println("======================发送报文======================：\n"+xml);
        String resp=XmlTools.send(url,xml);
        System.out.println("======================响应内容======================") ;
        boolean flag= this.verifyMsg(resp, tltcerPath,isFront);
        if(flag){
            System.out.println("响应内容验证通过") ;
        }else{
            System.out.println("响应内容验证不通过") ;
        }
        return resp;
    }
    
    /**
     * 验证签名
     * @param msg
     * @return
     *日期：Sep 9, 2012
     * @throws Exception 
     */
    public boolean verifyMsg(String msg,String cer,boolean isFront) throws Exception{
        boolean flag=XmlTools.verifySign(msg, cer, false,isFront);
        System.out.println("验签结果["+flag+"]") ;
        return flag;
    }
    
    /**
     * 报文签名
     * @param xml
     * @return
     *日期：Sep 9, 2012
     * @throws Exception 
     */
    public String signMsg(String xml) throws Exception{
        xml=XmlTools.signMsg(xml, pfxPath,pfxPassword, false);
        return xml;
    }
}
