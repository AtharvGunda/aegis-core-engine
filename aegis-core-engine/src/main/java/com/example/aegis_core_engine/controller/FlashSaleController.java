package com.example.aegis_core_engine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aegis_core_engine.service.FlashSaleService;

@RestController
@RequestMapping("/api/flash-sale")
public class FlashSaleController {
	
	private final FlashSaleService flashSaleService;
	
	public FlashSaleController(FlashSaleService flashSaleService) {
		this.flashSaleService=flashSaleService;
	}
	@PostMapping("/init")//used to initialize the stock in redis cache
	public ResponseEntity<String> initializeStock(@RequestParam Long eventId,int stock){
		flashSaleService.initializeStock(eventId,stock);
		return ResponseEntity.ok("Successfully initialized "+ stock+"tickets for event ID:"+eventId);
	}
	
	@PostMapping("/book")//used for booking the tickets
	//POST endpoint to handle high-concurrency ticket bookings.
	public ResponseEntity<String> bookTicket(@RequestParam Long eventId,@RequestParam Long userId){
		String responseMessage=flashSaleService.processFlashSale(eventId, userId);
		return ResponseEntity.ok(responseMessage);
	}
	

}
