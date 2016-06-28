package bps.external.monitor;

import org.apache.log4j.Logger;

import bps.external.application.Extension;
import bps.external.application.increment.other.SocketOpe;

/**
 * 交易监测发送
 * @author Administrator
 *
 */
public class TransMonitorSend extends Thread {
	private static Logger logger = Logger.getLogger(TransMonitorSend.class);
	private static String socketUrl;
	private static int socketPort;
	private String message;
	
	static{
		socketUrl = Extension.paramMap.get("monitor.trans.long.socket.url");
		socketPort = Integer.parseInt(Extension.paramMap.get("monitor.trans.long.socket.port"));
	}
	
	public TransMonitorSend(String message){
		this.message = message;
	}
	
	public void run(){
		try{
			logger.info("发送交易监测报文：socketUrl=" + socketUrl + ",socketPort=" + socketPort + ",message=" + message);
			String ret = SocketOpe.socketClientNoResponse(socketUrl, socketPort, message);
			logger.info("交易监测返回的报文：" + ret);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
}
