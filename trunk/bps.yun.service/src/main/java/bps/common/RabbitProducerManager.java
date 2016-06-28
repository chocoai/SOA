/**   
* @Title: RabbitProducerManager.java 
* @Package bps.common 
* @Description: 队列发送
* @author huadi   
* @date 2015年11月25日 下午3:22:30 
* @version V1.0   
*/
package bps.common;

import ime.core.Environment;

import org.apache.log4j.Logger;

import com.rabbitmq.client.AMQP;
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
public class RabbitProducerManager{
	
	public static Logger logger = Logger.getLogger(RabbitProducerManager.class);
	private String queue_name ;
	private String transactionMonitorQueueName;
	private String host_ip ;
	private Integer host_port ;
	private String user_name;
	private String password;
	
	private static volatile RabbitProducerManager rabbitProducerManager = null;
	
	private RabbitProducerManager() throws Exception{
		queue_name = Environment.instance().getSystemProperty("rabbitmq.queue_name");
		transactionMonitorQueueName = Environment.instance().getSystemProperty("rabbitmq.trans.monitor.queue.name");
		host_ip = Environment.instance().getSystemProperty("rabbitmq.ip");
		host_port = Integer.parseInt(Environment.instance().getSystemProperty("rabbitmq.port"));
		user_name = Environment.instance().getSystemProperty("rabbitmq.username");
		password = Environment.instance().getSystemProperty("rabbitmq.password");
		logger.info(queue_name +"-"+host_ip+"-"+host_port+"-"+user_name+"-"+password);
	}
	
	public static RabbitProducerManager getInstance() throws Exception{
		if(rabbitProducerManager == null){
			synchronized(RabbitProducerManager.class){
				if(rabbitProducerManager == null){
					rabbitProducerManager = new RabbitProducerManager();
				}
			}
		}
		return rabbitProducerManager;
	}

	/**
	 * 发送订单完成信息到队列（后台通知商户，默认方式）
	 * @param msg	发送的内容
	 * @throws Exception
     */
	public void send(String msg) throws Exception{
		logger.info("配置信息"+queue_name +"-"+host_ip+"-"+host_port+"-"+user_name+"-"+password);
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
	 * 发送信息到队列（没有使用）
	 * @param _queue_name	队列名称
	 * @param msg			发送的内容
	 * @throws Exception
     */
	public void send(String _exchangeName, String _queue_name, String bindingKey, String msg) throws Exception{
		logger.info("配置信息:"+_exchangeName+"-"+_queue_name +"-"+bindingKey+"-"+host_ip+"-"+host_port+"-"+user_name+"-"+password);
		logger.info("发送消息开始："+msg);

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host_ip);
		factory.setPort(host_port);
		factory.setUsername(user_name);
		factory.setPassword(password);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		//channel.basicQos(1);
		//声明延时交换路由
//		Map<String, Object> arg = new HashMap<>();
//		arg.put("x-delayed-type", "direct");
//
//		channel.exchangeDeclare(_exchangeName, "x-delayed-message", true, false, arg);


		//声明队列，该队列要和交换路由绑定
		channel.queueDeclare(_queue_name, false, false, false, null);
//
//		channel.queueBind(_queue_name, _exchangeName, bindingKey);

		channel.basicPublish("", _queue_name,  null, msg.getBytes());

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

	/**
	 * 交易监测队列发送
	 * @param msg	内容
	 * @throws Exception
	 */
	public void transactionMonitorQueueSend(String msg) throws Exception{
		logger.info("配置信息" + transactionMonitorQueueName + "-" + host_ip + "-" + host_port+"-"+user_name+"-"+password);
		logger.info("transactionMonitorQueueSend参数：msg=" + msg);
		
		Connection connection = null;
		Channel channel = null;
		try{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(host_ip);
			factory.setPort(host_port);
			factory.setUsername(user_name);
			factory.setPassword(password);
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(transactionMonitorQueueName, false, false, false, null);
			channel.basicPublish("", transactionMonitorQueueName, null, msg.getBytes());
			
			logger.info("transactionMonitorQueueSend end");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}finally{
			if(channel != null){
				channel.close();
			}
			
			if(connection != null){
				connection.close();
			}
		}
	}
	public static void testHeaders() throws  Exception{
		ConnectionFactory  factory = new ConnectionFactory();
		//192.168.1.167 	10.55.3.236
		String host = "10.55.3.236";
		String userName = "root";
		String password = "huadi";
		factory.setHost(host);
		factory.setUsername(userName);
		factory.setPassword(password);

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		//声明延时交换路由
		String exchangeName = "delay-exchange167";
		Map<String, Object> arg = new HashMap<>();
		arg.put("x-delayed-type", "direct");

		channel.exchangeDeclare(exchangeName, "x-delayed-message", true, false, arg);
//
//		//声明队列，该队列要和交换路由绑定
		String queueName = "delay-exchange168";
		System.out.printf("queueName:"+queueName);
		//"delay-exchange168";
		channel.queueDeclare(queueName, false, false, false, null);

		channel.queueBind(queueName, exchangeName, "BindingKey");

		//发送延时队列
		String msg = "TEST Message" ;
		byte[] messageBodyBytes = msg.getBytes("UTF-8");

		Map<String, Object> headers = new HashMap<>();
		//延时10s
		headers.put( "x-delay", 1*1000 );
		AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder().headers(headers);
		channel.basicPublish("", "BindingKey", null, messageBodyBytes);


		//关闭渠道、关闭连接
		channel.close();
		connection.close();
	}

	public static void  test(int i) throws Exception{
		String queue_name = "batchDaiFuQueryaaaaaaa";
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("10.55.3.236");
		factory.setPort(5672);
		factory.setUsername("root");
		factory.setPassword("huadi");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare("exchangeName", "direct", true);
		String queueName = channel.queueDeclare().getQueue();

		channel.queueDeclare(queue_name, false, false, false, null);

		channel.queueBind(queueName, "exchangeName", "routingKey");

		//Map<String, Object> headers = new HashMap<>();
		//headers.put("x-delay", 250000);
		//AMQP.BasicProperties.Builder props = new AMQP.BasicProperties().builder().headers(headers);

		String msg = "1458208327015D1603175858544892D1##100000000002";
		channel.basicPublish("exchangeName", "routingKey",  null, msg.getBytes());

		channel.close();
		connection.close();
	}
	public static void main(String[] args) throws Exception {
		test(0);
		for (int i =0; i< 1; i++){
			testHeaders();
		}
	}

}
