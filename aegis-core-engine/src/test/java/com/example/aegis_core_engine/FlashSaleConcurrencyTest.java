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
		int initialStock=10;
		
		//firing threads to access only 10 initial stock
		int totalThreads=30;
		
		//initialize stock before the stampede
		flashSaleService.initializeStock(eventId, initialStock);
		
		//an interface that allows creating threads and assigns it tasks
		ExecutorService executorService=Executors.newFixedThreadPool(20);
		
		//start gate for all the threads
		CountDownLatch latch=new CountDownLatch(totalThreads);
		
		//provides threads safe operations to avoid race conditions
		AtomicInteger successCount=new AtomicInteger(0);
		AtomicInteger soldOutCount=new AtomicInteger(0);
		
		for(int i=0;i<totalThreads;i++) {
			Long userId=200L+i;
			executorService.submit(()->{
				
				try {
					String result=flashSaleService.processFlashSale(eventId, userId);
					
					if(result.contains("Remaining stock")) {
						successCount.incrementAndGet();					
					}
					else if(result.contains("SoldOut")) {
						soldOutCount.incrementAndGet()
;					}
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					// Decrement latch counter so the main thread knows this worker finished
					latch.countDown();
				}
			});
		}
		// 4. Wait for all 30 threads to complete their execution
		latch.await();
		executorService.shutdown();
		
		System.out.println("=== CONCURRENCY TEST RESULTS ===");
        System.out.println("Successful Bookings: " + successCount.get());
        System.out.println("Sold Out Responses: " + soldOutCount.get());
        
     // 5. Assertion: Successful bookings MUST equal initial stock. If it's 11, overselling happened!
        assertEquals(initialStock, successCount.get(),"Overselling occurred! Successful bookings exceeded initial stock.");
		
		
		
	}

}
