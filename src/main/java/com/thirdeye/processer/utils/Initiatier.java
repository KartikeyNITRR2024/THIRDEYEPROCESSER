package com.thirdeye.processer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thirdeye.processer.services.MarketViewerProcesserService;
import com.thirdeye.processer.services.impl.MarketViewerProcesserServiceImpl;
import com.thirdeye.processer.services.impl.StocksListServiceImpl;

import jakarta.annotation.PostConstruct;


@Component 
public class Initiatier {
	
	@Autowired
	AllMicroservicesData allMicroservicesData;
	
	@Autowired
    StocksListServiceImpl stocksListServiceImpl;
	
	@Autowired
	PropertyLoader propertyLoader;
	
	@Autowired
	MarketViewerProcesserService marketViewerProcesserService;
	
	private static final Logger logger = LoggerFactory.getLogger(Initiatier.class);
	
	@PostConstruct
    public void init() throws Exception{
        logger.info("Initializing Initiatier...");
        allMicroservicesData.getAllMicroservicesData();
        propertyLoader.updatePropertyLoader();
	    stocksListServiceImpl.getStockListInBatches();
	    marketViewerProcesserService.resetData();
        logger.info("Initiatier initialized.");
    }
}
