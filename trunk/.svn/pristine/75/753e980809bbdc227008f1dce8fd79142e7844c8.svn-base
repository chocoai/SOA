package bps.web.testPage;

import ime.service.client.SOAClient;
import ime.service.util.RSAUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

//SOA请求
public class Port extends HttpServlet {
	private Logger logger = Logger.getLogger(Port.class);
	private static SOAClient client;
	private static PublicKey publicKey;
	private static PrivateKey privateKey;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException
    {
		System.out.println("=================Port.doGet start=====================");
		logger.info("=================Port.doGet start=====================");
		
		String soaUrl = request.getParameter("serviceCompleteUrl");
		
		System.out.println("参数soaUrl=" + soaUrl);
		logger.info("参数soaUrl=" + soaUrl);
		
		JSONObject ret = new JSONObject();
		try{
			//发送接口请求，并获取返回值
			ret = getHttp(soaUrl);
			ret.put("status", "0");
		}catch(Exception e){
			System.out.println("testCreateMember error:" + e.getMessage());
			logger.error(e.getMessage(), e);
			
			try {
				ret.put("status", "1");
				ret.put("message", e.getMessage());
			} catch (JSONException e1) {
				
			}
		}
		
		PrintWriter out = response.getWriter();
		out.print(java.net.URLEncoder.encode(ret.toString(), "UTF-8"));
		
		System.out.println(ret.toString());
		System.out.println("=================Port.doGet end=====================");
		logger.info("ret:" + ret.toString());
		logger.info("=================Port.doGet end=====================");
    }
	
    /**
	 * get请求
	 * @param  requestUrl notNullable  请求的url地址
	 * @return JSONObject 获取的response
	 * @throws Exception
	 */
	private JSONObject getHttp(String requestUrl) throws Exception{
		System.out.println("aa");
		System.out.println("requestUrl=" + requestUrl);
		JSONObject ret = new JSONObject();
		
		String charset = "UTF-8";
		StringBuffer sb = new StringBuffer();
		HttpURLConnection conn = null;
		BufferedReader br = null;
		String temp = "";
		
		try{
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			ret.put("header", getHeaderStr(conn.getHeaderFields()));
			br = new BufferedReader(new InputStreamReader(inputStream, charset));
			while((temp = br.readLine()) != null){
				sb.append(temp);
			}
			ret.put("body", sb.toString());
			
			return ret;
		}
		catch(Exception ex){
			System.out.println("ex=" + ex.getMessage());
			throw ex;
		}
		finally{
			if(conn != null){
				conn.disconnect();
			}
			if(br != null){
				br.close();
			}
		}
	}
	
	private String getHeaderStr(Map<String, List<String>> map){
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, List<String>> entry : map.entrySet()){
			if(entry.getKey() != null){
				sb.append(entry.getKey()).append(":");
			}
			
			for(String temp : entry.getValue()){
				sb.append(temp);
			}
			
			sb.append("<br />");
		}
		
		return sb.toString();
	}
}
