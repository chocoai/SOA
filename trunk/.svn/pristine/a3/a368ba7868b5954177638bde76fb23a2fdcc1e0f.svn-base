package bps.external.application.increment.other;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketOpe {
	private static Logger logger = Logger.getLogger(SocketOpe.class);
	
	/**
	 * socket客户端
	 * @param url
	 * @param port
	 * @param outStr
	 * @return
	 * @throws Exception
	 */
	public static String socketClientNoResponse(String url, int port, String outStr) throws Exception {
		logger.info("======================SocketOpe.socketClientNoResponse start=========================");
		
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		
		try{
			socket = new Socket(url, port);
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.write(outStr);
			out.flush();
		
			logger.info("======================SocketOpe.socketClientNoResponse end=========================");
			return "";
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}finally{
			if(socket != null){
				socket.close();
			}
			if(in != null){
				in.close();
			}
			if(out != null){
				out.close();
			}
		}
	}
	
	/**
	 * socket客户端
	 * @param url
	 * @param port
	 * @param outStr
	 * @return
	 * @throws Exception
	 */
	public static String socketClient(String url, int port, String outStr) throws Exception {
		logger.info("======================SocketOpe.socketClient start=========================");
		
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		
		try{
			logger.info("a");
			socket = new Socket(url, port);
			logger.info("b");
			out = new PrintWriter(socket.getOutputStream());
			logger.info("c");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			logger.info("d");
			out.write(outStr);
			logger.info("e");
			out.flush();
			logger.info("f");
			
			char[] inChars = new char[1024];
			logger.info("g");
			StringBuilder sb = new StringBuilder();
			while(in.read(inChars) > 0){
				logger.info("1");
				sb.append(inChars);
				inChars = new char[1024];
			}
			logger.info("h");
			String inStr = sb.toString();
			logger.info("返回:" + inStr);
			logger.info("======================SocketOpe.socketClient end=========================");
			return "";
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}finally{
			if(socket != null){
				socket.close();
			}
			if(in != null){
				in.close();
			}
			if(out != null){
				out.close();
			}
		}
	}
}
