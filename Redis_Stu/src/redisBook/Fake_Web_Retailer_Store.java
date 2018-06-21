package redisBook;

import java.util.UUID;

import redis.clients.jedis.Jedis;

public class Fake_Web_Retailer_Store {
	public static final void main(String[] args) {
		
	}
	
	public void run(){
		Jedis jedis = new Jedis("192.168.31.45",6379);
		jedis.select(15);
		
		
	}
	
	/**
	 * ��½cookie ʹ������cookie
	 * @param jedis
	 */
	public void testLoginCookies(Jedis jedis){
		System.out.println("\n----- testLoginCookies -----");
		String token = UUID.randomUUID().toString();//����UUID��Ϊ����
		
	}
	
	/**
	 * ��������
	 * @param jedis
	 * @param token
	 * @param user
	 * @param item
	 */
	public void updateToken(Jedis jedis,String token,String user,String item){
		long timestamp = System.currentTimeMillis() / 1000;//��ȡ��ǰʱ���
		jedis.hset("login:", token, user);//ά�����ƺ��ѵ�½�û�֮���ӳ��
		jedis.zadd("recent:", timestamp,token);//��¼�������һ�γ��ֵ�ʱ��
		if(item != null){
			jedis.zadd("viewed:" + token, timestamp, item);//��¼�û����������Ʒ
			jedis.zremrangeByRank("viewed:" + token, 0, -26);//�Ƴ��ɵļ�¼��ֻ�����û�����������25����Ʒ
			jedis.zincrby("viewed:", -1, item);
		}
	}
}
