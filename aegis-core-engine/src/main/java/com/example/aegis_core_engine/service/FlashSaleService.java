package com.example.aegis_core_engine.service;

import org.springframework.stereotype.Service;

import com.example.aegis_core_engine.model.Order;
import com.example.aegis_core_engine.repository.OrderRepository;

@Service
public class FlashSaleService {
	
	private final RedisInventoryservice redisInventoryservice;
	private final OrderRepository orderRepository;
	
	public FlashSaleService(RedisInventoryservice redisInventoryservice,OrderRepository orderRepository) {
		this.redisInventoryservice=redisInventoryservice;
		this.orderRepository=orderRepository;
	}

	public String processFlashSale(long eventId,long userId) {
		//remaining stock
		Long remainingStock=redisInventoryservice.decrementStock(eventId);
		if(remainingStock==null || remainingStock<0) {
			//we are incrementing because redis will store it as -1 so after increment the stock is 0
			redisInventoryservice.incrementstock(eventId);
			return "SoldOut! No stock remaining";
           }
		
		//if the check passed 
		try{
			//after creating oreder and orderRepository u modified this
			Order newOrder=new Order();
			newOrder.setUserId(userId);
		    newOrder.setEventId(eventId);
			newOrder.setStatus("SUCCESS");
			         
			orderRepository.save(newOrder);
			         
			return "Remaining stock is"+remainingStock;
		}
		catch(Exception e) {
			//Compensating Transacction
			//this is the fallback or rollback if payment fails from the user
			//// If database write fails after securing a Redis slot, refund the stock back to Redis!
			redisInventoryservice.incrementstock(eventId);
			throw new RuntimeException("Order processing failed,stock restored.",e);
		}
	}

	public void initializeStock(Long eventId, int stock) {
		redisInventoryservice.initializeStock(eventId, stock);
		
	}
}
