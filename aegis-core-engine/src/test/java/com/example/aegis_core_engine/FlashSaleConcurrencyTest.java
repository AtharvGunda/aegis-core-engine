package com.example.aegis_core_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.aegis_core_engine.service.FlashSaleService;

@SpringBootTest
public class FlashSaleConcurrencyTest {
	
	@Autowired
	private FlashSaleService flashSaleService;
	
	@Test
	void testConcurrentFlashSaleRequests() throws InterruptedException {
		//initial event id and stock
		long eventId=1L;
		int initialStock=2000;
		
		//firing threads to access only 10 initial stock
		int totalThreads=10000;
		
		//initialize stock before the stampede
		flashSaleService.initializeStock(eventId, initialStock);
		
		//an interface that allows creating threads and assigns it tasks
		ExecutorService executorService=Executors.newFixedThreadPool(400);
		
		//start gate for all the threads
		CountDownLatch latch=new CountDownLatch(totalThreads);
		
		//provides threads safe operations to avoid race conditions
		AtomicInteger successCount=new AtomicInteger(0);
		AtomicInteger soldOutCount=new AtomicInteger(0);
		AtomicInteger errorCount = new AtomicInteger(0);
		
		long startTime = System.currentTimeMillis();
		
		for(int i=0;i<totalThreads;i++) {
			Long userId=5000L+i;
			executorService.submit(()->{
				
				try {
					String result=flashSaleService.processFlashSale(eventId, userId);
					
					if(result.contains("Remaining stock")) {
						successCount.incrementAndGet();					
					}
					else if(result.contains("SoldOut")) {
						soldOutCount.incrementAndGet()
;					}
					else {
						errorCount.incrementAndGet();				
						}
					
				}
				catch(Exception e) {
					errorCount.incrementAndGet();
				}
				finally {
					// Decrement latch counter so the main thread knows this worker finished
					latch.countDown();
				}
			});
		}
		
		latch.await();
		executorService.shutdown();
		long duration = System.currentTimeMillis() - startTime;
		
		System.out.println("=== 10K MEGA-STAMPEDE RESULTS ===");
	    System.out.println("Execution Time: " + duration + " ms");
	    System.out.println("Successful Bookings: " + successCount.get());
	    System.out.println("Sold Out Responses: " + soldOutCount.get());
	    System.out.println("Errors/Timeouts: " + errorCount.get());
        
    
        assertEquals(initialStock, successCount.get(),"Overselling occurred! Successful bookings exceeded initial stock.");
		
		
		
	}

}
