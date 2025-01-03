package com.thirdeye.processer.services;

import java.util.ArrayList;
import java.util.List;

import com.thirdeye.processer.pojos.LiveStockProcesserPayload;
import com.thirdeye.processer.pojos.ProcesserResponse;

public interface MarketViewerProcesserService {

	void updateStocksStocksSceduler();

	List<ProcesserResponse> updateStocksStocks();

	void processData(LiveStockProcesserPayload liveStockProcesserPayload);

	void resetData();

	void websocketSender();

}
