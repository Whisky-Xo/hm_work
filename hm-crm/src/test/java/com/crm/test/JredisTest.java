package com.crm.test;

import java.util.Set;

import com.crm.common.util.RedisUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JredisTest {
	public static void main(String[] args) throws Exception {
		
//		RedisUtil redisUtil = new RedisUtil();
//		
//		redisUtil.set("112", "zzz");
		
		
		
		
		JedisPoolConfig config = new JedisPoolConfig();
	    //最大空闲连接数, 应用自己评估，不要超过ApsaraDB for Redis每个实例最大的连接数
	    config.setMaxIdle(200);
	    //最大连接数, 应用自己评估，不要超过ApsaraDB for Redis每个实例最大的连接数
	    config.setMaxTotal(300);
	    config.setTestOnBorrow(false);
	    config.setTestOnReturn(false);
	    String host = "114.55.34.116";
	    String password = "hanmu1505AAA";
	    JedisPool pool = new JedisPool(config, host, 6379, 3000, password);
//	    JedisPool pool = new JedisPool( host, 6379);
	    Jedis jedis = null;
	    try {
	      jedis = pool.getResource();
	      /// ... do stuff here ... for example
	      jedis.set("foo", "bar");
	      System.out.println(jedis.get("foo"));
	    } finally {
	      if (jedis != null) {
	        jedis.close();
	      }
	    }
	    /// ... when closing your application:
	    pool.destroy();
		
	}

	
}
