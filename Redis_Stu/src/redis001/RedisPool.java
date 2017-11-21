package redis001;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
	//reids服务器
	private static String ADDR = "192.168.31.45";
	
	//redis的端口号
	private static int PORT = 6379;
	
	//可用连接实例的最大数目，默认为8；
	//如果赋值为-1，表示不限制；如果pool已经分配了maxActive个jedis实例，
	//则测试pool的状态为exhausted（耗尽）；
	private static int MAX_ACTIVE = 1024;
	
	//控制一个pool最多有多少个状态为idle（空闲）的jedis实例，默认为8；
	private static int MAX_IDLE = 200;
	
	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示用不超时。如果超过等待时间，
	//则直接抛出JedisConnectionException；
	private static int MAX_WAIT=10000;
	
	private static int TIMEOUT = 10000;
	
	//在borrow一个jedis实例时，是否提前进行validate操作，如果为true，则得到的
	//jedis实例均为可用的；
	private static boolean TEST_ON_BORROW = true;
	
	private static JedisPool jedisPool = null;
	
	/**
	 * 初始化redis连接池
	 */
	//静态块
	static{
		try{
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxActive(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWait(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	/**
	 * 获取Jedis实例 
	 * 同步锁 synchronized 为啥这么做为了单例？
	 */
	public synchronized static Jedis getJedis(){
		try{
			if(jedisPool != null){
				Jedis resource = jedisPool.getResource();
				return resource;
			}else{
				return null;
			}
		}catch(Exception exception){
			exception.printStackTrace();
			return null;
		}
	}
	
	//释放jedis资源
	public static void returnResource(final Jedis jedis){
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
		
	}
	
	
}
