package redisBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

public class ArticleVote {
	private Jedis jedis;
	private static final int ONE_WEEK_IN_SECONDS = 7 * 86400; //չʾһ����Ҫ�ķ���
	private static final int VOTE_SCORE = 432; //ͶƱ����
	private static final int ARTICLES_PER_PAGE = 25; //��ҳչʾÿҳչʾ��ҳ��

	@Before
	public void setUp() throws Exception {
		jedis = new Jedis("192.168.31.45",6379);
	}

	/**
	 * ��������ȡ����   ����������Ҫ���ݲ��������� ���±��� ����
	 * 1.�洢���������Ϣ
	 * 2.�����ߴ浽��ͶƱ�û�����������
	 * 3.������ʱ��ͳ�ʼ���ַ������򼯺�
	 * @return
	 */
	@Test
	public String postArticle(String user,String title,String link) {
		//����һ���µ�����ID
		String articleId = String.valueOf(jedis.incr("article:"));
		
		//�����߼ӵ���ͶƱ�û���������
		String voted = "voted:" + articleId;
		jedis.sadd(voted, user);
		jedis.expire(voted, ONE_WEEK_IN_SECONDS);//������Чʱ��
		
		long now = System.currentTimeMillis() / 1000;//��ȡUnixʱ��
		String article = "article:" + articleId;
		HashMap<String, String> articleData = new HashMap<String,String>();
		articleData.put("title", title);
		articleData.put("link", link);
		articleData.put("user", user);
		articleData.put("now", String.valueOf(now));
		articleData.put("vates", "1");
		jedis.hmset(article, articleData); //��������Ϣ�浽hash����
		jedis.zadd("score", now + VOTE_SCORE,article);
		jedis.zadd("time:", now,article);
		
		return articleId;
	}
	
	/**
	 * ����ͶƱ �������  �û���  ����
	 * 1.��Ʊ���ӵ�vote����
	 * 2.���û�id�ӵ���ͶƱ�û�����
	 * 
	 */
	@Test
	public void articleVote(String user,String article){
		//�ж�������û�й���
		long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
		if(jedis.zscore("time:", article) < cutoff){//���ʱ�������Чʱ��
			return;
		}
		String articleId = article.substring(article.indexOf(":")+1);//�ӣ���һλ��ʼ��ȡ�ַ�����һֱ��ȡ�����
		if(jedis.sadd("vated:" + articleId, user) == 1){
			//Redis Zincrby ��������򼯺���ָ����Ա�ķ����������� increment
			jedis.zincrby("score:", VOTE_SCORE, article);
			//Redis Hincrby ��������Ϊ��ϣ���е��ֶ�ֵ����ָ������ֵ��
			jedis.hincrBy(article, "votes", 1);

		}
	}
	
	/**
	 * ��������
	 * 1.Redis Zrevrange ����������У�ָ�������ڵĳ�Ա��
		  ���г�Ա��λ�ð�����ֵ�ݼ�(�Ӵ�С)�����С�
	   2.Redis Hgetall �������ڷ��ع�ϣ���У����е��ֶκ�ֵ��
		  �ڷ���ֵ�����ÿ���ֶ���(field name)֮�����ֶε�ֵ(value)��
		  ���Է���ֵ�ĳ����ǹ�ϣ���С��������
	 */
	@Test
	public List<Map<String,String>> getArticles(int page,String order){
		//������ֹҳ
		int start = (page - 1) * ARTICLES_PER_PAGE;
		int end = start + ARTICLES_PER_PAGE - 1;
		
		//���򷵻�  ָ��  ���ϵĳ�Ա
		Set<String> ids = jedis.zrevrange(order, start, end);//�÷����о��ܺ��ð�
		List<Map<String,String>> articles = new ArrayList<Map<String, String>>();
		for (String id : ids) {
			Map<String, String> articleData = jedis.hgetAll(id);//ȡ��ÿ�����µ���Ϣ
			articleData.put("id", id);//��ÿ�����¼�id����
			articles.add(articleData);
		}
		return articles;
	}
	
	public List<Map<String, String>> getArticles(int page){
		return getArticles(page, "score:");
	}

	/**
	 * �����½��з��� 
	 * 1.��Ҫ����Ⱥ��
	 * 2.һ��Ⱥ�鸺���¼���������ĸ�Ⱥ�飬��һ������ȡ��Ⱥ�����������
	 * 
	 */
	@Test
	public void addGroups(String articleId,String[] toAdd){
		String article = "article:" + articleId;
		for(String group : toAdd){
			jedis.sadd("group:" + group, article);
		}
	}

	@Test
	public List<Map<String, String>> getGroupArticles(String group,int page){
		return getGroupArticels(group, page, "score:");
	}
	
	public List<Map<String, String>> getGroupArticels(String group,int page,String order){
		String key = order + group;
		if(!jedis.exists(key)){
			ZParams params = new ZParams().aggregate(ZParams.Aggregate.MAX);
			jedis.zinterstore(key, params,"group:"+group);
			jedis.expire(key, 60);
		}
		return getArticles(page, key);
	}
	
	@Test
	public void printArticles(List<Map<String, String>> articles){
		for (Map<String, String> article : articles) {
			System.out.println("  id: " + article.get("id"));
			for (Map.Entry<String, String> entry : article.entrySet()) {
				if(entry.getKey().equals("id")){
					continue;
				}
				System.out.println("   " + entry.getKey() + ": " + entry.getValue());
			}
		}
	}
}
