package com.chumore.notification.model;

import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.chumore.util.ResponseUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Repository
public class NotificationRepository {
	
	private final JedisPool pool;
	
	public NotificationRepository(JedisPool pool) {
		this.pool = pool;
	}
	
	public void saveNotifications(String restId, String caller, String message, String type) {
		Jedis jedis = null;
		String notifyKey = "restId:" + restId + ":" + type;
		
		jedis = pool.getResource();
		jedis.select(3); // 資料儲存到DB3
		jedis.hset(notifyKey, caller, message);
		
		if (type.equals("reservation")) {
			jedis.expire(notifyKey, 86400 * 3); // 訂位通知的保留時間是三天
		} else {
			jedis.expire(notifyKey, 3600); // 設定此筆Redis資料到期時間，3600sec = 1hr
		}
		
		jedis.close();
		
	}
	
	public Map<String, String> getRestNotifications(String restId, String type) {
		String notifyKey = "restId:" + restId + ":" + type;
		Jedis jedis = null;
		Map<String, String> restNotifications = null;
		
		jedis = pool.getResource();
		restNotifications = jedis.hgetAll(notifyKey);
		jedis.close();
		
		return restNotifications;
	}

}
