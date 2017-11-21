package redis001;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisTest {
	private Jedis jedis;
	@Before
	public void setUp() throws Exception {
		//����redis������
		jedis = new Jedis("192.168.31.45", 6379);
		//Ȩ����֤   û������Ҳ��Ҫ�� ����ʲô���  ���ۣ�û���������벻��Ҫ����Ȩ����֤
	}

	/**
	 * redis�洢�ַ���
	 */
	@Test
	public void testString() {
		//�������
		jedis.set("age", "23");
		System.out.println(jedis.get("name")+":"+jedis.get("age"));
		
		//ƴ��  �Բۣ�����ƴ��
		jedis.append("name", " is me");
		System.out.println(jedis.get("name"));
		
		//ɾ��ĳ����
		jedis.del("name");
		System.out.println(jedis.get("name"));
		
		//���ö����ֵ��  6666
		jedis.mset("name","wj","desc","bitch","QQ","1729134493");
		jedis.incr("QQ");//��1����  ������ô��Щ��û�У�ʲô�����񾭲���
		System.out.println(jedis.get("name")+"-"+jedis.get("age")
		+" is a "+jedis.get("desc")+"-"+jedis.get("QQ"));
	}
	
	/**
	 * redis����Hash ������վ˵�ǲ���Map��bitch
	 */
	@Test
	public void testHash(){
		//�������
		Map<String,String> map = new HashMap<String,String>();
		map.put("name", "pen");
		map.put("age", "22");
		map.put("qq", "4843");
		map.put("desc", "I have a pen,I have a apple");
		jedis.hmset("user", map);//hmset �ڶ��������Ѿ���ȷ��hash���ͣ�����ô�ǲ���Map
		
		//ɾ��map�е�ĳ��ֵ
		jedis.hdel("user", "age");
		System.out.println(jedis.hmget("user", "age"));
		System.out.println(jedis.hlen("user"));//���� ���� ��3
		System.out.println(jedis.exists("user"));//�ж��Ƿ��и�key ����һ��Boolean����
		System.out.println(jedis.hkeys("user"));//���6�ˣ�����redis��key�е�����key
		System.out.println(jedis.hvals("user"));//��Ӧ�ϱߵ�key������Ƿ������е�value
		
		//����������key
		Iterator<String> iterator = jedis.hkeys("user").iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			System.out.println(key+":"+jedis.hmget("user", key));	
		}
	}
	
	/**
	 * jedis����List
	 */
	@Test
	public void testList(){
		//��ʼǰ���Ƴ����е�����
		jedis.del("java framework");
		System.out.println(jedis.lrange("java framework", 0, -1));
		
		//�������
		jedis.lpush("java framework", "spring");
		jedis.lpush("java framework", "strurs2");
		jedis.lpush("java framework", "Hibernate");
		System.out.println(jedis.lrange("java framework", 0, -1));
		
		//��������
		jedis.lpop("java framework");
		jedis.del("java framework");
		jedis.rpush("java framework", "spring");
		jedis.rpush("java framework", "struts2");
		jedis.rpush("java framework", "Hibernate");
		System.out.println(jedis.lrange("java framework", 0, -1));
	}
	
	/**
	 * jedis����set
	 */
	@Test
	public void testSet(){
		//���
		jedis.sadd("users", "jcm");
		jedis.sadd("users", "jj");
		jedis.sadd("users", "and");
		jedis.sadd("users", "mm");
		jedis.sadd("users", "jingjing");
		//�Ƴ�
		jedis.srem("users", "jj");
		System.out.println(jedis.smembers("users"));
		System.out.println(jedis.sismember("users", "jj"));
		//�������һ��set�����е�ֵ �о���;����
		System.out.println(jedis.srandmember("users"));
		System.out.println(jedis.scard("users"));
	}
	
	/**
	 * jedis����
	 */
	@Test
	public void testSort(){
		jedis.del("a");
		jedis.rpush("a", "1");
		jedis.rpush("a", "2");
		jedis.rpush("a", "9");
		jedis.rpush("a", "3");
		jedis.rpush("a", "6");
		System.out.println(jedis.lrange("a", 0, -1));
		System.out.println(jedis.sort("a"));
		System.out.println(jedis.lrange("a", 0, -1));
	}
	
	//jedis���ӳ�
	@Test
	public void testRedisPool(){
		RedisPool.getJedis().set("poolname", "hello jmc");
		System.out.println(RedisPool.getJedis().get("poolname"));
	}
	
	/**
	 * redis�������꣬6666 rule the world step one
	 */
}
