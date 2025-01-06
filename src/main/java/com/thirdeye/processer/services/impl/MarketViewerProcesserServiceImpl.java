package com.thirdeye.processer.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.thirdeye.processer.pojos.LiveStockProcesserPayload;
import com.thirdeye.processer.pojos.ProcesserResponse;
import com.thirdeye.processer.services.MarketViewerProcesserService;
import com.thirdeye.processer.services.StocksListService;
import com.thirdeye.processer.utils.CalculateScore;
import com.thirdeye.processer.utils.PropertyLoader;

@Service
public class MarketViewerProcesserServiceImpl implements MarketViewerProcesserService {
	
	@Autowired
	private CalculateScore calculateScore;
	
    @Value("${decreasePointsInMinute}")
    private Long decreasePointsInMinute;
    
    @Value("${noOfStocksToRead}")
    private Integer noOfStocksToRead;
    
//    @Value("${telegram_service_url}")
//    private String telegramServiceUrl;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    PropertyLoader propertyLoader;
    
    @Autowired
    private StocksListService stocksListService;
    
    private static final Logger logger = LoggerFactory.getLogger(MarketViewerProcesserServiceImpl.class);
    
	
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
	public Map<Long,LiveStockProcesserPayload> liveStockList = new HashMap<>();
	public PriorityQueue<ArrayList<Long>> pq = new PriorityQueue<>((a, b) -> Long.compare(b.get(0), a.get(0)));
    List<ProcesserResponse> topStocks = new ArrayList<>();
	
	@Override
	public void resetData()
	{
		liveStockList = new HashMap<>();
		pq = new PriorityQueue<>((a, b) -> Long.compare(b.get(0), a.get(0)));
		topStocks = new ArrayList<>();
	}
	
	@Override
	public void processData(LiveStockProcesserPayload liveStockProcesserPayload)
	{
		 if(!liveStockList.containsKey((long)liveStockProcesserPayload.getStockId()))
		 {
			 System.out.println("FirstTime");
			 liveStockProcesserPayload.setScore(0L);
			 liveStockProcesserPayload.setSumScore(0L);
			 Queue<Long> pastSumScores = new LinkedList<>();
			 pastSumScores.add(0L);
			 liveStockProcesserPayload.setPastSumScores(pastSumScores);
			 updatePriorityStock(liveStockProcesserPayload);
		 }
		 else
		 {
			 LiveStockProcesserPayload oldData = null;
			 try {
		         lock.readLock().lock();
			     oldData = liveStockList.get((long)liveStockProcesserPayload.getStockId());
			 } finally {
	             lock.readLock().unlock();
	         }
			 if (oldData.getTime().compareTo(liveStockProcesserPayload.getTime()) == 0) {
				    return;
			 }
			 LiveStockProcesserPayload processedData = calculateScore.calculateScoreOfStock(liveStockProcesserPayload, oldData);
			 updatePriorityStock(processedData);
		 }
	}
	
	private void updatePriorityStock(LiveStockProcesserPayload processedData)
	{    
         try {
	         lock.writeLock().lock();
			 liveStockList.put((long)processedData.getStockId(), processedData);
			 removeById(processedData.getStockId());
			 ArrayList<Long> list = new ArrayList<>(); 
			 list.add(processedData.getSumScore());
			 list.add((long)processedData.getStockId());
			 pq.add(list);
         } finally {
             lock.writeLock().unlock();
         }
	}
	
	private boolean removeById(long value) {
        Iterator<ArrayList<Long>> iterator = pq.iterator();
        while (iterator.hasNext()) {
            ArrayList<Long> list = iterator.next();
            System.out.println("List is "+list);
            if (list.get(1) == value) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
	
	private void decreasePoints()
	{
		logger.info("Decresing points started");
		Map<Long,LiveStockProcesserPayload> liveStockList1 = null;
		PriorityQueue<ArrayList<Long>> pq1 = null;
		Map<Long,LiveStockProcesserPayload> liveStockList2 = new HashMap<>();
		PriorityQueue<ArrayList<Long>> pq2 = new PriorityQueue<>((a, b) -> Long.compare(b.get(0), a.get(0)));
		try {
            lock.readLock().lock();
            liveStockList1 = new HashMap<Long,LiveStockProcesserPayload>(liveStockList);
            pq1 = new PriorityQueue<>(pq);
		} finally {
            lock.readLock().unlock();
        }
		
		for (Map.Entry<Long, LiveStockProcesserPayload> entry : liveStockList1.entrySet()) {
            LiveStockProcesserPayload payload = entry.getValue();
            long decreasedScore = (long) (payload.getSumScore() / Math.pow(2, (1.0 / 20)));
            payload.setSumScore(decreasedScore);
            Queue<Long> pastSumScores = payload.getPastSumScores();
            pastSumScores.add(decreasedScore);
            payload.setPastSumScores(pastSumScores);
            liveStockList2.put(entry.getKey(), payload);
            ArrayList<Long> updatedList = new ArrayList<>();
            updatedList.add(decreasedScore);
            updatedList.add((long)payload.getStockId());
            pq2.add(updatedList);
        }
		
		try {
            lock.writeLock().lock();
            liveStockList = liveStockList2;
            pq = pq2;
		} finally {
            lock.writeLock().unlock();
        }
		logger.info("Decresing points ended");
	}

	@Override
    public List<ProcesserResponse> updateStocksStocks() {
        return topStocks;
    }
	
	@Scheduled(fixedRate = 60000)
	@Override
    public void updateStocksStocksSceduler() {
		logger.info("Updating data");
		decreasePoints();
        List<ProcesserResponse> topStocks1 = new ArrayList<>();
        try {
            lock.readLock().lock();
            Iterator<ArrayList<Long>> iterator = pq.iterator();
            int count = 0;
            while (iterator.hasNext() && count < noOfStocksToRead) {
            	ArrayList<Long> stock = iterator.next();
            	topStocks1.add(new ProcesserResponse(stocksListService.getIdToStock(stock.get(1)), stock.get(0), liveStockList.get((long)stock.get(1)).getPastSumScores()));
                count++;
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            topStocks = topStocks1;
		} finally {
            lock.writeLock().unlock();
        }
        websocketSender();
    }
	
	@Override
    public void websocketSender() {
        try {
        	Gson gson = new Gson();
            String jsonString = gson.toJson(topStocks);
	        String destination = "/lateststock";
	        simpMessagingTemplate.convertAndSend(destination, jsonString);
	        logger.info("Latest Stocks Send");
        } catch (Exception e) {
            logger.error("Could not send latest stocks", e);
            logger.error("Latest Stock not sent using websocket");
        }
    }
	
	
}
