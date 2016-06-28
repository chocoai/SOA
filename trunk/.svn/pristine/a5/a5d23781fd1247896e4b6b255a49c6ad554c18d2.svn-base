package bps.external.monitor;

import java.net.URLDecoder;

import org.apache.log4j.Logger;

import bps.external.application.Extension;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/** 
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年11月25日 下午3:33:46  
 */
public class TransMonitorRabbitCousumer extends Thread{
	public static Logger logger = Logger.getLogger(TransMonitorRabbitCousumer.class);
	private String transMonitorQueueName ;
	private String hostIp ;
	private Integer hostPort ;
	private String userName;
	private String password;
	
	public TransMonitorRabbitCousumer(){
		transMonitorQueueName = Extension.paramMap.get("rabbitmq.trans.monitor.queue.name");
		hostIp = Extension.paramMap.get("rabbitmq.ip");
		hostPort = Integer.parseInt(Extension.paramMap.get("rabbitmq.port"));
		userName = Extension.paramMap.get("rabbitmq.username");
		password = Extension.paramMap.get("rabbitmq.password");
	}
	
	public void run() {
		try{
			logger.info(transMonitorQueueName+"|"+hostIp+"|"+hostPort+"|"+userName+"|"+password);
			QueueingConsumer consumer = getConsumer();
			QueueingConsumer.Delivery delivery = null;
			while (true) {
				try{
					logger.info("-----交易监测消息等待------");
					try{
						delivery = consumer.nextDelivery();
					}catch(Exception e){
						logger.error(e.getMessage(), e);
						sleep(1000L*60L*5L);
						consumer = getConsumer();//重新获得链接
						
						continue;
					}
					String message = new String(delivery.getBody());
					logger.info(" Received:'" + message + "'");
					message = URLDecoder.decode(message, "UTF-8");
					logger.info(" 解码后Received:'" + message + "'");
					
					TransMonitorSend transMonitorSend = new TransMonitorSend(message);
					transMonitorSend.start();
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	
	public QueueingConsumer getConsumer(){
		try{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(hostIp);
			factory.setPort(hostPort);
			factory.setUsername(userName);
			factory.setPassword(password);
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(transMonitorQueueName, false, false, false, null);
			logger.info("-----加载交易监测消费者------");
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(transMonitorQueueName, true, consumer);
			return consumer;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
