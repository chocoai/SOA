package bps.external.tradecommand;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import ime.core.Environment;
import org.apache.log4j.Logger;

import java.net.URLDecoder;

/**
 * 接收单笔批量代付查询
 * Created by yyy on 2016/3/9.
 */
public class BatchDaiFuQueryRabbitCousumer extends Thread {
    public static Logger logger = Logger.getLogger(BatchDaiFuQueryRabbitCousumer.class);
    boolean autoAck = false;    /*信息是否自动确认*/
    Channel channel = null;
    private String batch_daiFu_query ;
    private String hostIp ;
    private Integer hostPort ;
    private String userName;
    private String password;

    public BatchDaiFuQueryRabbitCousumer(){
        try{
            batch_daiFu_query = Environment.instance().getSystemProperty("rabbitmq.queue_name.batch_daiFu_query");
            hostIp = Environment.instance().getSystemProperty("rabbitmq.ip");
            hostPort = Integer.parseInt(Environment.instance().getSystemProperty("rabbitmq.port"));
            userName = Environment.instance().getSystemProperty("rabbitmq.username");
            password = Environment.instance().getSystemProperty("rabbitmq.password");
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    public void run(){
        try{
            logger.info(batch_daiFu_query+"|"+hostIp+"|"+hostPort+"|"+userName+"|"+password);
            QueueingConsumer consumer = getConsumer();
            QueueingConsumer.Delivery delivery;
            while (true) {
                try{
                    logger.info("-----批量代付查询消息等待------");
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
                    channel.basicAck( delivery.getEnvelope().getDeliveryTag(), false);

                    BatchDaiFuQuerySend batchDaiFuSend = new BatchDaiFuQuerySend(message);
                    batchDaiFuSend.start();
                }catch(Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 重新获得链接
     * @return QueueingConsumer
     */
    public QueueingConsumer getConsumer(){
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(hostIp);
            factory.setPort(hostPort);
            factory.setUsername(userName);
            factory.setPassword(password);
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(batch_daiFu_query, false, false, false, null);
            logger.info("-----加载批量代付消费者------");
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(batch_daiFu_query, autoAck, consumer);
            return consumer;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
