package bps.external.application.increment.other;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import bps.common.BizException;
import bps.external.application.Extension;

public class GetIpInfo {
	private static final String charset = "UTF-8";
	private static String requestIp = null;    
	private static Logger logger = Logger.getLogger(GetIpInfo.class);
	public static JSONObject get(String requestUrl) throws Exception{
		StringBuffer sb = new StringBuffer();
		HttpURLConnection conn = null;
		BufferedReader br = null;
		String temp = "";
		
		try{
			if(requestIp == null){
				requestIp = Extension.paramMap.get("allinpay.getIp.url");
			}
			
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(inputStream, charset));
			while((temp = br.readLine()) != null){
				sb.append(temp);
			}
			
			return new JSONObject(sb.toString());
		}
		catch(Exception ex){
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
	public static String decodeUnicode(String theString) {      
		   
	    char aChar;      
	   
	     int len = theString.length();      
	   
	    StringBuffer outBuffer = new StringBuffer(len);      
	   
	    for (int x = 0; x < len;) {      
	   
	     aChar = theString.charAt(x++);      
	   
	     if (aChar == '\\') {      
	   
	      aChar = theString.charAt(x++);      
	   
	      if (aChar == 'u') {      
	   
	       // Read the xxxx      
	   
	       int value = 0;      
	   
	       for (int i = 0; i < 4; i++) {      
	   
	        aChar = theString.charAt(x++);      
	   
	        switch (aChar) {      
	   
	        case '0':      
	   
	        case '1':      
	   
	        case '2':      
	   
	        case '3':      
	   
	       case '4':      
	   
	        case '5':      
	   
	         case '6':      
	          case '7':      
	          case '8':      
	          case '9':      
	           value = (value << 4) + aChar - '0';      
	           break;      
	          case 'a':      
	          case 'b':      
	          case 'c':      
	          case 'd':      
	          case 'e':      
	          case 'f':      
	           value = (value << 4) + 10 + aChar - 'a';      
	          break;      
	          case 'A':      
	          case 'B':      
	          case 'C':      
	          case 'D':      
	          case 'E':      
	          case 'F':      
	           value = (value << 4) + 10 + aChar - 'A';      
	           break;      
	          default:      
	           throw new IllegalArgumentException(      
	             "Malformed   \\uxxxx   encoding.");      
	          }      
	   
	        }      
	         outBuffer.append((char) value);      
	        } else {      
	         if (aChar == 't')      
	          aChar = '\t';      
	         else if (aChar == 'r')      
	          aChar = '\r';      
	   
	         else if (aChar == 'n')      
	   
	          aChar = '\n';      
	   
	         else if (aChar == 'f')      
	   
	          aChar = '\f';      
	   
	         outBuffer.append(aChar);      
	   
	        }      
	   
	       } else     
	   
	       outBuffer.append(aChar);      
	   
	      }      
	   
	      return outBuffer.toString();      
	   
	     }     
	
	public Map<String, String> getAll(String ip) throws BizException {
		if(ip==null){
			throw new BizException("99999","ip不能为空！");
		}
		StringBuffer sb = new StringBuffer();
		sb.setLength(0);
		sb.append(requestIp);
		sb.append("?key=").append(ip);
		String ipComplete = sb.toString();
		logger.info(ipComplete);
		JSONObject response = null;
		try{
			response = get(ipComplete);
			logger.info(response.toString());
			JSONObject ret = new JSONObject();
			
			String temp=new String();
			int errorCode = (Integer) response.get("error_code");
			if(errorCode == 0){
				ret.put("respCode", 0);
				ret.put("respMsg", (String)response.getString("reason"));
				ret.put("result", (JSONArray)response.get("result"));
				String country=(String) ret.get("country");
				temp=decodeUnicode(country);
				ret.put("country", temp);
				String area=(String) ret.get("area");
				temp=decodeUnicode(area);
				ret.put("area", temp);
				String region=(String) ret.get("region");
				temp=decodeUnicode(region);
				ret.put("region", temp);
				String city=(String) ret.get("city");
				temp=decodeUnicode(city);
				ret.put("city", temp);
				String county=(String) ret.get("county");
				temp=decodeUnicode(county);
				ret.put("county", temp);
				String isp=(String) ret.get("isp");
				temp=decodeUnicode(isp);
				ret.put("isp", temp);
				
			}
			else{
				ret.put("respCode", errorCode);
				ret.put("respMsg", (String)response.get("reason"));
				logger.error("respCode：" + errorCode + "，respMsg：" + (String)response.getString("reason"));
			}
			
			return ret.getMap();
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			 throw new BizException("99999",ex.getMessage());
		}
	}
		
	}
	


