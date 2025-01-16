package com.chumore.servicebell.model;

import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.chumore.util.ResponseUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Repository
public class ServiceBellRepository {
	
	private final JedisPool pool;
	
	public ServiceBellRepository(JedisPool pool) {
		this.pool = pool;
	}
	
	public void saveNotifications(String restId, String tableName, String message) {
		Jedis jedis = null;
		String notifyKey = "restId:" + restId + ":notification";
		
		jedis = pool.getResource();
		jedis.hset(notifyKey, tableName, message);
		jedis.expire(notifyKey, 3600); // 設定此筆Redis資料到期時間，3600sec = 1hr
		
		jedis.close();
		
	}
	
	public Map<String, String> getRestNotifications(String restId) {
		String notifyKey = "restId:" + restId + ":notification";
		Jedis jedis = null;
		Map<String, String> restNotifications = null;
		
		jedis = pool.getResource();
		restNotifications = jedis.hgetAll(notifyKey);
		jedis.close();
		
		return restNotifications;
	}

}
