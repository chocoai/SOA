package bps.common;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;


public class HttpUtil {
	private static Logger logger = Logger.getLogger(HttpUtil.class.getName());
	public static String encoding = "utf-8";
    private HttpClient client;
    
    public HttpUtil(){
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setConnectionTimeout(15000);
        params.setSoTimeout(30000);
        params.setStaleCheckingEnabled(true);
        params.setTcpNoDelay(true);
        params.setDefaultMaxConnectionsPerHost(100);
        params.setMaxTotalConnections(100);
        params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        
    	HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setParams(params);
        client = new HttpClient(connectionManager);
    }
    
	private String post(String url, String encoding, String content) {
		try {
			byte[] resp = post(url, content.getBytes(encoding));
			if (null == resp)
				return null;
			return new String(resp, encoding);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public String post(String url, String content) {
    	return post(url, encoding, content);
    }
	
    private byte[] post(String url, byte[] content) {
		try {
			return post(url, new ByteArrayRequestEntity(content));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
    }
    
    private byte[] post(String url, RequestEntity requestEntity) throws Exception {
    	Protocol myhttps = new Protocol("https", new SSLSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
        PostMethod method = new PostMethod(url);
        method.addRequestHeader("Connection", "Keep-Alive");
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        method.setRequestEntity(requestEntity);
        method.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK)
                return null;
            return method.getResponseBody();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	return null;
        } finally {
            method.releaseConnection();
        }
    }
    
    public String post(String url, NameValuePair[] datas) {
    	return post(url, encoding, datas);
    }
    
    private String post(String url, String encoding, NameValuePair[] datas) {
		try {
			byte[] resp = postMethod(url, datas);
			if (null == resp)
				return null;
			return new String(resp, encoding);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
    private byte[] postMethod(String url, NameValuePair[] datas) throws Exception {
    	PostMethod method = new PostMethod(url );
    	method.setRequestBody(datas);
        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK)
                return null;
            return method.getResponseBody();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	return null;
        } finally {
            method.releaseConnection();
        }
    }
    
    public String get(String url, String content){
    	Protocol myhttps = new Protocol("https", new SSLSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
		
    	GetMethod method = new GetMethod(url + "?" + content);
    	method.addRequestHeader("Connection", "Keep-Alive");
    	method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
    	method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
    	method.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
    	
    	try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK)
                return null;
            try {
     			byte[] resp = method.getResponseBody();
     			if (null == resp)
     				return null;
     			return new String(resp, encoding);
     		} catch (Exception e) {
     			logger.error(e.getMessage(), e);
     			return null;
     		}
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	return null;
        } finally {
        	method.releaseConnection();
        }
    }
}
