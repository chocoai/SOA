package bps.web.testPage;

import ime.service.util.RSAUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.PrivateKey;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

//获取签名
public class SignOpe extends HttpServlet {
	Logger logger = Logger.getLogger(SignOpe.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		logger.info("======================SignOpe.doGet start===========================");
		
		String sysid = request.getParameter("sysid");
		String req =  request.getParameter("req");
		String timestamp = request.getParameter("timestamp");
		
		JSONObject ret = new JSONObject();
		try {
			if(sysid == null || sysid == ""){
				throw new Exception("sysid不能为空或null。");
			}
			if(req == null || req == ""){
				throw new Exception("req不能为空或null。");
			}
			if(timestamp == null || timestamp == ""){
				throw new Exception("timestamp不能为空或null。");
			}
			
			req = java.net.URLDecoder.decode(req, "UTF-8");
			
			logger.info("参数：sysid=" + sysid + ";req=" + req + ";timestamp=" + timestamp);
			System.out.println("参数：sysid=" + sysid + ";req=" + req + ";timestamp=" + timestamp);
			
			String signStr = sysid + req + timestamp;
			
			//密钥密码
			String pwd = "697057";
			//证书名称
			String alias = "100000000002";  
			//证书文件路径，用户指定
			String path = "/home/bps.yun.test/tomcat6203/webapps/ROOT/pfx/100000000002.pfx";
			//获取私钥
			PrivateKey privateKey = RSAUtil.loadPrivateKey(alias, path, pwd);
			String sign = RSAUtil.sign(privateKey, signStr);

			response.setContentType("text/html;charset=utf-8");

			ret.put("status", "0");
			ret.put("sign", sign);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.error(e.getMessage(), e);
			
			try {
				ret.put("status", "1");
				ret.put("message", e.getMessage());
			} catch (JSONException e1) {
				
			}
		}
		
		System.out.println("ret=" + ret);
		logger.info("ret:" + ret.toString());
		PrintWriter out = response.getWriter();
		out.print(ret.toString());
		logger.info("======================SignOpe.doGet end===========================");
    }
}
