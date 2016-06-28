package bps.external.tradecommand;

import bps.common.Constant;
import bps.order.OrderServiceImpl;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import ime.core.Environment;
import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/1.
 */
public class DaikouRabbitConsumer extends Thread {
    private static Logger logger = Logger.getLogger(DaikouRabbitConsumer.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    boolean autoAck = false;    /*信息是否自动确认*/
    Channel channel = null;
    private String daikouQueueName;
    private String hostIp ;
    private Integer hostPort ;
    private String userName;
    private String password;

    public DaikouRabbitConsumer(){
        try{
            daikouQueueName = Environment.instance().getSystemProperty("rabbitmq.queue_name.daikouOrder");
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
            logger.info(daikouQueueName+"|"+hostIp+"|"+hostPort+"|"+userName+"|"+password);
            QueueingConsumer consumer = getConsumer();
            QueueingConsumer.Delivery delivery;
            while (true) {
                try{
                    logger.info("----代扣通道查询队列消费者开启------");
                    try{
                        delivery = consumer.nextDelivery();

                    }catch(Exception e){
                        logger.error(e.getMessage(), e);
                        sleep(1000L*60L*5L);
                        consumer = getConsumer();//重新获得链接

                        continue;
                    }
                    String message = new String(delivery.getBody());
                    logger.info("代扣通道查询队列消费者 Received:'" + message + "'");
                    message = URLDecoder.decode(message, "UTF-8");
                    logger.info("代扣通道查询队列消费者 解码后Received:'" + message + "'");
                    channel.basicAck( delivery.getEnvelope().getDeliveryTag(), false);

                    String orgNo = message.split("\\|")[0];
                    String commandNo = message.split("\\|")[1];
                    String requestTimeStr = message.split("\\|")[2];
                    String queryId = message.split("\\|")[3];
                    handler(orgNo, commandNo, requestTimeStr, queryId);
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
            channel.queueDeclare(daikouQueueName, false, false, false, null);
            logger.info("-----加载批量代付消费者------");
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(daikouQueueName, autoAck, consumer);
            return consumer;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private void handler(String orgNo, String commandNo, String requestTimeStr, String queryId) throws Exception {
        logger.info("orgNo=" + orgNo + ",commandNo=" + commandNo + ",requestTimeStr=" + requestTimeStr + ",queryId=" + queryId);

        try{
            OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
            Map<String, Object> order = orderServiceImpl.getOrder(commandNo.split(Constant.COMMAND_SPLIT_SIGN)[0]);

            //如果订单状态为进行中，则查询
            if(Constant.ORDER_STATE_PENDING.equals(order.get("order_state"))){
                //异步发起查询
                DaikouQueryTradeResult daikouQueryTradeResult = new DaikouQueryTradeResult(orgNo, sdf.parse(requestTimeStr), queryId, commandNo);
                daikouQueryTradeResult.start();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
