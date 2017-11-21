package redis001;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
	//reids������
	private static String ADDR = "192.168.31.45";
	
	//redis�Ķ˿ں�
	private static int PORT = 6379;
	
	//��������ʵ���������Ŀ��Ĭ��Ϊ8��
	//�����ֵΪ-1����ʾ�����ƣ����pool�Ѿ�������maxActive��jedisʵ����
	//�����pool��״̬Ϊexhausted���ľ�����
	private static int MAX_ACTIVE = 1024;
	
	//����һ��pool����ж��ٸ�״̬Ϊidle�����У���jedisʵ����Ĭ��Ϊ8��
	private static int MAX_IDLE = 200;
	
	//�ȴ��������ӵ����ʱ�䣬��λ���룬Ĭ��ֵΪ-1����ʾ�ò���ʱ����������ȴ�ʱ�䣬
	//��ֱ���׳�JedisConnectionException��
	private static int MAX_WAIT=10000;
	
	private static int TIMEOUT = 10000;
	
	//��borrowһ��jedisʵ��ʱ���Ƿ���ǰ����validate���������Ϊtrue����õ���
	//jedisʵ����Ϊ���õģ�
	private static boolean TEST_ON_BORROW = true;
	
	private static JedisPool jedisPool = null;
	
	/**
	 * ��ʼ��redis���ӳ�
	 */
	//��̬��
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
	 * ��ȡJedisʵ�� 
	 * ͬ���� synchronized Ϊɶ��ô��Ϊ�˵�����
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
	
	//�ͷ�jedis��Դ
	public static void returnResource(final Jedis jedis){
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
		
	}
	
	
}
