package com.thirdeye.processer.utils;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye.processer.pojos.LiveStockProcesserPayload;

@Service
public class CalculateScore {
	
	@Autowired
	TimeManagementUtil timeManagementUtil;

	public LiveStockProcesserPayload calculateScoreOfStock(LiveStockProcesserPayload newLiveStock, LiveStockProcesserPayload oldLiveStock)
	{
		Double priceRange = ((oldLiveStock.getNewPrice() - oldLiveStock.getOldPrice()) * 1000)/oldLiveStock.getOldPrice();
		Double pointsIncrement = priceRange / 1000;
		Double score = ((newLiveStock.getNewPrice() - oldLiveStock.getOldPrice()) * 1000) / (pointsIncrement * oldLiveStock.getOldPrice());
		long minutesDifference = timeManagementUtil.getDifferenceInMinutes(newLiveStock.getTime(), oldLiveStock.getTime());
		if(minutesDifference < 1)
		{
			minutesDifference = 1;
		}
		Double score1 = ((newLiveStock.getOldPrice() - oldLiveStock.getNewPrice()) * 1000) / (pointsIncrement * minutesDifference * oldLiveStock.getOldPrice());
		Double totalScore = score + score1;
		newLiveStock.setScore(totalScore.longValue());
		if(oldLiveStock.getSumScore() >= 100000L)
		{
			oldLiveStock.setSumScore(0L);
		}
		newLiveStock.setSumScore(oldLiveStock.getSumScore() + totalScore.longValue());
		Queue<Long> pastSumScores = oldLiveStock.getPastSumScores();
		pastSumScores.add(oldLiveStock.getSumScore() + totalScore.longValue());
		if(pastSumScores.size() > 30)
		{
			pastSumScores.poll();
		}
		newLiveStock.setPastSumScores(pastSumScores);
		return newLiveStock;
	}
	
//	public LiveStockProcesserPayload calculateScoreOfStockStarting(LiveStockProcesserPayload newLiveStock)
//	{
//		Double priceRange = ((oldLiveStock.getNewPrice() - oldLiveStock.getOldPrice()) * 1000)/oldLiveStock.getOldPrice();
//		Double pointsIncrement = priceRange / 1000;
//		Double score = ((newLiveStock.getNewPrice() - oldLiveStock.getOldPrice()) * 1000) / (pointsIncrement * oldLiveStock.getOldPrice());
//		long minutesDifference = timeManagementUtil.getDifferenceInMinutes(newLiveStock.getTime(), oldLiveStock.getTime());
//		if(minutesDifference < 1)
//		{
//			minutesDifference = 1;
//		}
//		Double score1 = ((newLiveStock.getOldPrice() - oldLiveStock.getNewPrice()) * 1000) / (pointsIncrement * minutesDifference * oldLiveStock.getOldPrice());
//		Double totalScore = score + score1;
//		newLiveStock.setScore(totalScore.longValue());
//		newLiveStock.setSumScore(oldLiveStock.getSumScore() + totalScore.longValue());
//		Queue<Long> pastSumScores = oldLiveStock.getPastSumScores();
//		pastSumScores.add(oldLiveStock.getSumScore() + totalScore.longValue());
//		if(pastSumScores.size() > 30)
//		{
//			pastSumScores.poll();
//		}
//		newLiveStock.setPastSumScores(pastSumScores);
//		return newLiveStock;
//	}
}
