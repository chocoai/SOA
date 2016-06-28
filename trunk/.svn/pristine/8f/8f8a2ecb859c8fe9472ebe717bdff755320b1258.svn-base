/**   
* @Title: RabbitProducerManager.java 
* @Package bps.common 
* @Description: TODO(用一句话描述该文件做什么) 
* @author huadi   
* @date 2015年11月25日 下午3:22:30 
* @version V1.0   
*/
package bps.external.application.notice;

import com.rabbitmq.client.AMQP;
import org.apache.log4j.Logger;

import bps.external.application.Extension;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * (这里用一句话描述这个类的作用) 
 * @author huadi
 * @date 2015年11月25日 下午3:22:30  
 */
public class RabbitProducer{
	
	
	
	public static Logger logger = Logger.getLogger(RabbitProducer.class);
	private String queue_name ;
	private String host_ip ;
	private Integer host_port ;
	private String user_name;
	private String password;
	
	private static volatile RabbitProducer rabbitProducerManager = null;
	private RabbitProducer() throws Exception{
		queue_name = Extension.paramMap.get("rabbitmq.queue_name");
		host_ip = Extension.paramMap.get("rabbitmq.ip");
		host_port = Integer.parseInt(Extension.paramMap.get("rabbitmq.port"));
		user_name = Extension.paramMap.get("rabbitmq.username");
		password = Extension.paramMap.get("rabbitmq.password");
		logger.info(queue_name +"-"+host_ip+"-"+host_port+"-"+user_name+"-"+password);
	}
	
	public static RabbitProducer getInstance() throws Exception{
		if(rabbitProducerManager == null){
			synchronized(RabbitProducer.class){
				if(rabbitProducerManager == null){
					rabbitProducerManager = new RabbitProducer();
				}
			}
		}
		return rabbitProducerManager;
	}

	public void send(String msg) throws Exception{
		logger.info("发送消息开始："+msg);
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host_ip);
		factory.setPort(host_port);
		factory.setUsername(user_name);
		factory.setPassword(password);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(queue_name, false, false, false, null);
		channel.basicPublish("", queue_name, null, msg.getBytes());
		channel.close();
		connection.close();
		logger.info("发送消息结束：");
	}
	/**
	 * 发送信息到队列，延时发送消息
	 * @param _exchangeName		路由
	 * @param _queue_name		队列名称
	 * @param bindingKey		路由和列表绑定名称
	 * @param msg				发送的内容
	 * @param waitTime			延时时间（毫秒）
	 * @throws Exception
	 */
	public void send(String _exchangeName, String _queue_name, String bindingKey, String msg, Long waitTime) throws Exception{
		logger.info("配置信息:"+_exchangeName+"-"+_queue_name +"-"+bindingKey+"-"+waitTime+"-"+host_ip+"-"+host_port+"-"+user_name+"-"+password);
		logger.info("发送消息开始："+msg);

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host_ip);
		factory.setPort(host_port);
		factory.setUsername(user_name);
		factory.setPassword(password);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		//声明延时交换路由
		Map<String, Object> arg = new HashMap<>();
		arg.put("x-delayed-type", "direct");

		channel.exchangeDeclare(_exchangeName, "x-delayed-message", true, false, arg);


		//声明队列，该队列要和交换路由绑定
		channel.queueDeclare(_queue_name, false, false, false, null);

		channel.queueBind(_queue_name, _exchangeName, "");

		//延时
		Map<String, Object> headers = new HashMap<>();
		headers.put("x-delay", waitTime);
		AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder().headers(headers);

		channel.basicPublish(_exchangeName, "",  props.build(), msg.getBytes());

		channel.close();
		connection.close();
		logger.info("发送消息结束：");
	}
}
