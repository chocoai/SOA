/**   
* @Title: RabbitCousumer.java 
* @Package bps.external.application.notice 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年11月25日 下午3:33:46 
* @version V1.0   
*/
package bps.external.application.notice;


import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.json.JSONObject;

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
public class RabbitCousumer extends Thread{
	public static Logger logger = Logger.getLogger(RabbitCousumer.class);
	private String queue_name ;
	private String host_ip ;
	private Integer host_port ;
	private String user_name;
	private String password;
	private Channel channel;
	
	public RabbitCousumer(){
		queue_name = Extension.paramMap.get("rabbitmq.queue_name");
		host_ip = Extension.paramMap.get("rabbitmq.ip");
		host_port = Integer.parseInt(Extension.paramMap.get("rabbitmq.port"));
		user_name = Extension.paramMap.get("rabbitmq.username");
		password = Extension.paramMap.get("rabbitmq.password");
	}
	
	public void run() {
		try{
			QueueingConsumer consumer = getConsumer();
			QueueingConsumer.Delivery delivery = null;
			while (true) {
				try{
					logger.info("-----消息等待------");
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
					JSONObject param = new JSONObject(message);
					BackNotice backNotice = new BackNotice(param);
					backNotice.start();
					
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
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
			factory.setHost(host_ip);
			factory.setPort(host_port);
			factory.setUsername(user_name);
			factory.setPassword(password);
			Connection connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(queue_name, false, false, false, null);
			logger.info("-----加载消费者------");
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queue_name, false, consumer);
			return consumer;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
