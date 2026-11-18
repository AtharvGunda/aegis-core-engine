package com.example.aegis_core_engine.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisInventoryservice {
	
	//connect java application to redis
	private final StringRedisTemplate redistemplate;
	
	public RedisInventoryservice(StringRedisTemplate redistemplate) {
		this.redistemplate=redistemplate;
	}
	
	//Used to avoid name space collisions in redis
	private static final String STOCK_KEY_PREFIX="flashsale:event:stock:";
	
	
	//Used  to initialize the stock
	public void initializeStock(long eventId,int stock) {
		String Key=STOCK_KEY_PREFIX+eventId;
		redistemplate.opsForValue().set(Key,String.valueOf(stock));
	}
	
	//used to decrement(atomic operation in redis) the stock
	public Long decrementStock(long eventId) {
		String Key=STOCK_KEY_PREFIX+eventId;
		return redistemplate.opsForValue().decrement(Key);
	}
	
	//when payment fails
	public Long incrementstock(Long eventId) {
		String Key=STOCK_KEY_PREFIX+eventId;
		return redistemplate.opsForValue().increment(Key);
	}
	
	public Long getCurrentStock(Long eventId) {
		String Key=STOCK_KEY_PREFIX+eventId;
		String currentStock=redistemplate.opsForValue().get(Key);
		return currentStock!=null?Long.parseLong(currentStock):0L;
	}

}
