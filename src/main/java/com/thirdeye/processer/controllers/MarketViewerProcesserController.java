package com.thirdeye.processer.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdeye.processer.pojos.LiveStockProcesserPayload;
import com.thirdeye.processer.pojos.ProcesserResponse;
import com.thirdeye.processer.services.MarketViewerProcesserService;
import com.thirdeye.processer.utils.AllMicroservicesData;


@RestController
@RequestMapping("/api/marketviewerprocesser")
public class MarketViewerProcesserController {

	@Autowired
	AllMicroservicesData allMicroservicesData;
	
	@Autowired
	MarketViewerProcesserService marketViewerProcesserService;
	
	
    private static final Logger logger = LoggerFactory.getLogger(MarketViewerProcesserController.class);

    @PostMapping("processdata/{uniqueId}")
    public ResponseEntity<Boolean> sendMarketViewerProcessData(@PathVariable("uniqueId") Integer pathUniqueId, @RequestBody LiveStockProcesserPayload liveStockProcesserPayload) {
        if (pathUniqueId.equals(allMicroservicesData.current.getMicroserviceUniqueId())) {
            logger.info("Status check for uniqueId {}: Found", allMicroservicesData.current.getMicroserviceUniqueId());
            marketViewerProcesserService.processData(liveStockProcesserPayload);
            return ResponseEntity.ok(true);
        } else {
            logger.warn("Status check for uniqueId {}: Not Found", allMicroservicesData.current.getMicroserviceUniqueId());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("getlateststock/{uniqueId}")
    public ResponseEntity<List<ProcesserResponse>> getLatestStocks(@PathVariable("uniqueId") Integer pathUniqueId) {
        if (pathUniqueId.equals(allMicroservicesData.current.getMicroserviceUniqueId())) {
            logger.info("Status check for uniqueId {}: Found", allMicroservicesData.current.getMicroserviceUniqueId());
        	List<ProcesserResponse> list = marketViewerProcesserService.updateStocksStocks();
            return ResponseEntity.ok(list);
        } else {
            logger.warn("Status check for uniqueId {}: Not Found", allMicroservicesData.current.getMicroserviceUniqueId());
            return ResponseEntity.notFound().build();
        }
    }
}


