package bps.common;

import ime.core.Environment;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

public class JedisUtils {
	private static Logger logger = Logger.getLogger(JedisUtils.class);
	private static JedisPool pool = null;

	private static String ip = null;
	private static int port = 0;

	/**
	 * 设置redis的ip和端口
	 * @param setIp		ip
	 * @param setPort	端口
     */
	public static void setParam(String setIp,int setPort){
		ip = setIp;
		port = setPort;
	}
	/**
	 * 构建redis连接池
	 * 
	 * @param ip		redisIP
	 * @param port		端口
	 * @return JedisPool	redis连接池
	 */
	public static JedisPool getPool(String ip, int port) {
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxIdle(500);
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(5);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(1000 * 100);
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(true);
			pool = new JedisPool(config, ip, port);
		}
		return pool;
	}

	/**
	 * 返还到连接池
	 * 
	 * @param pool		redis连接池
	 * @param redis		redis对像
	 */
	public static void returnResource(JedisPool pool, Jedis redis) {
		if (redis != null) {
			pool.returnResource(redis);
		}
	}
	/**
	 * 设置值
	 * @param key 		key
	 * @param value		内容
	 * @return	是否成功
	 * @throws BizException
	 */
	public static boolean setCache(String key, String value) throws BizException{
		logger.info("getCacheByKey参数,key:" + key+"--value:"+value);

		JedisPool pool = null;
		Jedis jedis = null;
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();

			jedis.set(key, value);

			return true;
		}catch(Exception e){
			// 释放redis对象
			if( pool != null )
				pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}

	/**
	 * 通过key获取缓存的value
	 * @param key key
	 * @return	对应内容
	 * @throws BizException
	 */
	public static String getCacheByKey(String key) throws BizException{
		logger.info("getCacheByKey参数:" + key);

		JedisPool pool = null;
		Jedis jedis = null;
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();

			String cacheValue = jedis.get(key);
			logger.info("getCacheByKey返回=" + cacheValue);
			return cacheValue;
		}catch(Exception e){
			// 释放redis对象
			if( pool != null )
				pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}

	/**
	 * 设置哈希表方式
	 * @param key		key
	 * @param field		域
	 * @param value		值
	 * @return	是否操作成功
	 * @throws BizException
	 */
	public static boolean hsetCache(String key, String field, String value) throws BizException{
		logger.info("hsetCache参数,key:" + key+"---field:"+field+"--value:"+value);

		JedisPool pool = null;
		Jedis jedis = null;
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();

			jedis.hset(key,field,value);
			return true;
		}catch(Exception e){
			// 释放redis对象
			if( pool != null )
				pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}

	/**
	 * 取哈希表值
	 * @param key	key
	 * @return	列表
	 * @throws BizException
	 */
	public static Map<String, String> hgetCacheByKey(String key) throws BizException{
		logger.info("hgetCacheByKey参数:" + key);

		JedisPool pool = null;
		Jedis jedis = null;
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();

			return jedis.hgetAll(key);

		}catch(Exception e){
			// 释放redis对象
			if( pool != null )
				pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}

	/**
	 * 取哈希表二级field的值
	 * @param key		哈希表KEY
	 * @param field		二级KEY
	 * @return	value
	 * @throws BizException
	 */
	public static String hgetCacheByField(String key, String field)throws BizException{
		logger.info("hgetCacheByKey参数:" + key);

		JedisPool pool = null;
		Jedis jedis = null;
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();

			return jedis.hget(key,field);

		}catch(Exception e){
			// 释放redis对象
			if( pool != null )
				pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}


	/**
	 * 删除队列
	 * @param key	key
	 * @return	是否操作成功
	 * @throws BizException
	 */
	public static boolean delCache(String key)throws BizException{
		logger.info("delCache参数,key:" + key);

		JedisPool pool = null;
		Jedis jedis = null;
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();

			jedis.del(key);

			return true;
		}catch(Exception e){
			// 释放redis对象
			if( pool != null )
				pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}

	/**
	 * 删除哈希表下二级列
	 * @param key		哈希表KEY
	 * @param field		二级KEY
	 * @return	是否操作成功
	 * @throws BizException
	 */
	public static boolean hdelCache(String key, String field) throws BizException{
		logger.info("delCache参数,key:" + key);

		JedisPool pool = null;
		Jedis jedis = null;
		try{
			pool = JedisUtils.getPool(ip, port);
			jedis = pool.getResource();

			jedis.hdel(key,field);

			return true;
		}catch(Exception e){
			// 释放redis对象
			if( pool != null )
				pool.returnBrokenResource(jedis);
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}finally{
			// 返还到连接池
			JedisUtils.returnResource(pool, jedis);
		}
	}
}

