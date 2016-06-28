package bps.web.testPage;

import ime.service.util.RSAUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

//加密解密
public class EncryptionDecryption extends HttpServlet {
	private Logger logger = Logger.getLogger(Port.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		logger.info("========================EncryptionDecryption.doGet start=================================");
		
		String str = request.getParameter("str");
		String type = request.getParameter("type");
		System.out.println("str=" + str + ";type=" + type);
		logger.info("参数：str=" + str + ";type=" + type);
		
		JSONObject ret = new JSONObject();
		try {
			str = java.net.URLDecoder.decode(str, "UTF-8");
			
			//密钥密码
			String pwd = "697057";
			//证书名称
			String alias = "100000000002";  
			//证书文件路径，用户指定
			String path = "/home/bps.yun.test/tomcat6203/webapps/ROOT/pfx/100000000002.pfx";
			PrivateKey privateKey = RSAUtil.loadPrivateKey(alias, path, pwd);
			PublicKey publicKey = RSAUtil.loadPublicKey(alias, path, pwd);
			RSAUtil rsaUtil = new RSAUtil((RSAPublicKey)publicKey, (RSAPrivateKey)privateKey);
			String retStr = "1".equals(type) ? rsaUtil.encrypt(str) :  rsaUtil.dencrypt(str);
			retStr = java.net.URLEncoder.encode(retStr, "UTF-8");
			
			ret.put("status", "0");
			ret.put("str", retStr);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.error(e.getMessage(), e);
			
			try {
				ret.put("status", "1");
				ret.put("message", e.getMessage());
			} catch (JSONException e1) {
				
			}
		}
		
		PrintWriter out = response.getWriter();
		out.print(ret);
		logger.info("ret:" + ret);
		logger.info("========================EncryptionDecryption.doGet end=================================");
    }
}
