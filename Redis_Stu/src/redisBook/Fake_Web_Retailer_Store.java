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
	 * 登陆cookie 使用令牌cookie
	 * @param jedis
	 */
	public void testLoginCookies(Jedis jedis){
		System.out.println("\n----- testLoginCookies -----");
		String token = UUID.randomUUID().toString();//生成UUID作为令牌
		
	}
	
	/**
	 * 更新令牌
	 * @param jedis
	 * @param token
	 * @param user
	 * @param item
	 */
	public void updateToken(Jedis jedis,String token,String user,String item){
		long timestamp = System.currentTimeMillis() / 1000;//获取当前时间戳
		jedis.hset("login:", token, user);//维持令牌和已登陆用户之间的映射
		jedis.zadd("recent:", timestamp,token);//记录令牌最后一次出现的时间
		if(item != null){
			jedis.zadd("viewed:" + token, timestamp, item);//记录用户浏览过的商品
			jedis.zremrangeByRank("viewed:" + token, 0, -26);//移除旧的记录，只保留用户最近浏览过的25个商品
			jedis.zincrby("viewed:", -1, item);
		}
	}
}
