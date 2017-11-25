package redis001;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisTest {
	private Jedis jedis;
	@Before
	public void setUp() throws Exception {
		//链接redis服务器
		jedis = new Jedis("192.168.31.45", 6379);
		//权限认证   没有设置也需要？ 看报什么错吧  结论：没有设置密码不需要进行权限认证
	}

	/**
	 * redis存储字符串
	 */
	@Test
	public void testString() {
		//添加数据
		jedis.set("age", "23");
		System.out.println(jedis.get("name")+":"+jedis.get("age"));
		
		//拼接  卧槽，还有拼接
		jedis.append("name", " is me");
		System.out.println(jedis.get("name"));
		
		//删除某个键
		jedis.del("name");
		System.out.println(jedis.get("name"));
		
		//设置多个键值对  6666
		jedis.mset("name","wj","desc","bitch","QQ","1729134493");
		jedis.incr("QQ");//加1操作  书上怎么这些都没有？什么鬼啊，神经病啊
		System.out.println(jedis.get("name")+"-"+jedis.get("age")
		+" is a "+jedis.get("desc")+"-"+jedis.get("QQ"));
	}
	
	/**
	 * redis操作Hash 垃圾网站说是操作Map，bitch
	 */
	@Test
	public void testHash(){
		//添加数据
		Map<String,String> map = new HashMap<String,String>();
		map.put("name", "pen");
		map.put("age", "22");
		map.put("qq", "4843");
		map.put("desc", "I have a pen,I have a apple");
		jedis.hmset("user", map);//hmset 第二个参数已经明确是hash类型，还他么是操作Map
		
		//删除map中的某个值
		jedis.hdel("user", "age");
		System.out.println(jedis.hmget("user", "age"));
		System.out.println(jedis.hlen("user"));//长度 长度 是3
		System.out.println(jedis.exists("user"));//判断是否有该key 返回一个Boolean类型
		System.out.println(jedis.hkeys("user"));//这个6了，返回redis的key中的所有key
		System.out.println(jedis.hvals("user"));//照应上边的key，这个是返回所有的value
		
		//迭代器遍历key
		Iterator<String> iterator = jedis.hkeys("user").iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			System.out.println(key+":"+jedis.hmget("user", key));	
		}
	}
	
	/**
	 * jedis操作List
	 */
	@Test
	public void testList(){
		//开始前，移除所有的内容
		jedis.del("java framework");
		System.out.println(jedis.lrange("java framework", 0, -1));
		
		//添加数据
		jedis.lpush("java framework", "spring");
		jedis.lpush("java framework", "strurs2");
		jedis.lpush("java framework", "Hibernate");
		System.out.println(jedis.lrange("java framework", 0, -1));
		
		//弹出数据
		jedis.lpop("java framework");
		jedis.del("java framework");
		jedis.rpush("java framework", "spring");
		jedis.rpush("java framework", "struts2");
		jedis.rpush("java framework", "Hibernate");
		System.out.println(jedis.lrange("java framework", 0, -1));
	}
	
	/**
	 * jedis操作set
	 */
	@Test
	public void testSet(){
		//添加
		jedis.sadd("users", "jcm");
		jedis.sadd("users", "jj");
		jedis.sadd("users", "and");
		jedis.sadd("users", "mm");
		jedis.sadd("users", "jingjing");
		//移除
		jedis.srem("users", "jj");
		System.out.println(jedis.smembers("users"));
		System.out.println(jedis.sismember("users", "jj"));
		//随机返回一个set集合中的值 感觉用途不大
		System.out.println(jedis.srandmember("users"));
		System.out.println(jedis.scard("users"));
	}
	
	/**
	 * jedis排序
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
	
	//jedis连接池
	@Test
	public void testRedisPool(){
		RedisPool.getJedis().set("poolname", "hello jmc");
		System.out.println(RedisPool.getJedis().get("poolname"));
	}
	
	/**
	 * redis基础过完，6666 rule the world step one
	 */
	
	@Test
	public void testString1(){
		String string = "abedhdcdefge";//7
		int length = string.length();
		//char charAt = 0;
		String str = "";
		HashMap<Integer, String> hash = new HashMap<Integer, String>();
		for(int i = 0;i <length;i++){
			for(int sub = i+1;sub<=length;sub++){
				str = string.substring(i,sub);
				if (!str.matches(".*(.).*\\1.*")) {
					hash.put(str.length(), str);
					System.out.println(str);
				}
				//String reString = containRepeatChar(str);
				//System.out.println(reString);
				/*if (sub<length) {
					charAt = string.charAt(sub);
				}else{
					System.out.println(str);
					return;
				}
				if(str.contains(String.valueOf(charAt))){
					System.out.println(str);
					i = string.indexOf(String.valueOf(charAt));
					break;
				}*/
				/*for(int stri = 0;stri<str.length();stri++){
					if(!str.contains(str.substring(stri, stri+1)) && res<=str.length()){
						res = str.length();
						
					}
				}*/
			}
		}
		//判断hash的value值
		//List<Integer> set = (List<Integer>) hash.values();
		int a = 0;
		for (Integer integer : hash.keySet()) {
			if (integer>a) {
				a = integer;
			}
		}
		System.out.println(a+"********");
		System.out.println(hash.get(a));
	}
	
    public static String containRepeatChar(String str){
        if(str==null||str.isEmpty()){
            return "";
        }
        char[] elements=str.toCharArray();    
        for(char e:elements){
            if(str.indexOf(e)!=str.lastIndexOf(e)){
                return str;
            }
        }
        return "";
    }
	
	@Test
	public void test(){
		int k = lengthOfLongestSubstring("asdfgshj");
		System.out.println(k);
	}
	    public int lengthOfLongestSubstring(String s) {
	        int n = s.length();
	        int ans = 0;
	        for (int i = 0; i < n; i++)
	            for (int j = i + 1; j <= n; j++)
	                if (allUnique(s, i, j)) ans = Math.max(ans, j - i);
	        return ans;
	    }

	    public boolean allUnique(String s, int start, int end) {
	        Set<Character> set = new HashSet<>();
	        for (int i = start; i < end; i++) {
	            Character ch = s.charAt(i);
	            if (set.contains(ch)) return false;
	            set.add(ch);
	        }
	        return true;
	    }
}
