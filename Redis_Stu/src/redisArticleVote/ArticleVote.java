package redisArticleVote;

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
	private static final int ONE_WEEK_IN_SECONDS = 7 * 86400; //展示一周需要的分数
	private static final int VOTE_SCORE = 432; //投票常量
	private static final int ARTICLES_PER_PAGE = 25; //分页展示每页展示的页数

	@Before
	public void setUp() throws Exception {
		jedis = new Jedis("192.168.31.45",6379);
	}

	/**
	 * 发布并获取文章   发布文章需要传递参数：作者 文章标题 链接
	 * 1.存储文章相关信息
	 * 2.将作者存到已投票用户名单集合中
	 * 3.将发布时间和初始评分放入有序集合
	 * @return
	 */
	@Test
	public String postArticle(String user,String title,String link) {
		//生成一个新的文章ID
		String articleId = String.valueOf(jedis.incr("article:"));
		
		//将作者加到已投票用户名集合中
		String voted = "voted:" + articleId;
		jedis.sadd(voted, user);
		jedis.expire(voted, ONE_WEEK_IN_SECONDS);//设置有效时间
		
		long now = System.currentTimeMillis() / 1000;//获取Unix时间
		String article = "article:" + articleId;
		HashMap<String, String> articleData = new HashMap<String,String>();
		articleData.put("title", title);
		articleData.put("link", link);
		articleData.put("user", user);
		articleData.put("now", String.valueOf(now));
		articleData.put("vates", "1");
		jedis.hmset(article, articleData); //将文章信息存到hash表中
		jedis.zadd("score", now + VOTE_SCORE,article);
		jedis.zadd("time:", now,article);
		
		return articleId;
	}
	
	/**
	 * 文章投票 所需参数  用户名  文章
	 * 1.将票数加到vote集合
	 * 2.将用户id加到已投票用户集合
	 * 
	 */
	@Test
	public void articleVote(String user,String article){
		//判断文章有没有过期
		long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
		if(jedis.zscore("time:", article) < cutoff){//如果时间大于有效时间
			return;
		}
		String articleId = article.substring(article.indexOf(":")+1);//从：下一位开始截取字符串，一直截取到最后
		if(jedis.sadd("vated:" + articleId, user) == 1){
			//Redis Zincrby 命令对有序集合中指定成员的分数加上增量 increment
			jedis.zincrby("score:", VOTE_SCORE, article);
			//Redis Hincrby 命令用于为哈希表中的字段值加上指定增量值。
			jedis.hincrBy(article, "votes", 1);

		}
	}
	
	/**
	 * 文章排序
	 * 1.Redis Zrevrange 命令返回有序集中，指定区间内的成员，
		  其中成员的位置按分数值递减(从大到小)来排列。
	   2.Redis Hgetall 命令用于返回哈希表中，所有的字段和值。
		  在返回值里，紧跟每个字段名(field name)之后是字段的值(value)，
		  所以返回值的长度是哈希表大小的两倍。
	 */
	@Test
	public List<Map<String,String>> getArticles(int page,String order){
		//设置起止页
		int start = (page - 1) * ARTICLES_PER_PAGE;
		int end = start + ARTICLES_PER_PAGE - 1;
		
		//有序返回  指定  集合的成员
		Set<String> ids = jedis.zrevrange(order, start, end);//该方法感觉很好用啊
		List<Map<String,String>> articles = new ArrayList<Map<String, String>>();
		for (String id : ids) {
			Map<String, String> articleData = jedis.hgetAll(id);//取到每个文章的信息
			articleData.put("id", id);//给每个文章加id属性
			articles.add(articleData);
		}
		return articles;
	}
	
	public List<Map<String, String>> getArticles(int page){
		return getArticles(page, "score:");
	}

	/**
	 * 对文章进行分组 
	 * 1.需要两个群组
	 * 2.一个群组负责记录文章属于哪个群组，另一个负责取出群组里面的文章
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
