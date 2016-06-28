package bps.common;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.log4j.Logger;



/**
 * @author Administrator
 *
 */
public class Cousumer{
	public static Logger logger = Logger.getLogger(Cousumer.class);
	private static String topic = null;
	/**
	 * @author Administrator
	 *
	 */
	public abstract interface CousumerEvent {
		public abstract void notifyMsg(String msg);
	}
	
	
	protected CousumerEvent listener = null;
	private final ConsumerConnector consumer;
	private ExecutorService executor;
	
	public Cousumer(String a_zookeeper, String a_groupId, String a_topic) {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(a_zookeeper, a_groupId));
		topic = a_topic;
	}
	
	public void setCousumerEvent(CousumerEvent l){
	    this.listener = l;
	}
	
	private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "30000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "30000");
		props.put("auto.offset.reset", "smallest");

		return new ConsumerConfig(props);
	}
	
	public void shutdown() {
		if (consumer != null)
			consumer.shutdown();
		if (executor != null)
			executor.shutdown();
	}
	
	public void run(int numThreads) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, numThreads);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

		// now launch all the threads 
		executor = Executors.newFixedThreadPool(numThreads);
		executor.submit(new ConsumerMsgTask(streams.get(0)));
	}
	
	class ConsumerMsgTask implements Runnable {
	    private KafkaStream<byte[], byte[]> m_stream;
	 
	    public ConsumerMsgTask(KafkaStream<byte[], byte[]> stream) {
	        m_stream = stream;
	    }
	 
	    public void run() {
	        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
	        while (it.hasNext()){
	        	try {
	        		String msg = new String(it.next().message());
		            msg = URLDecoder.decode(msg, "UTF-8");
		            if(listener != null)
		            	listener.notifyMsg(msg);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
	        }
	    }
	}
	
}
