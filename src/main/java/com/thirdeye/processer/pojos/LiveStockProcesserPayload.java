package com.thirdeye.processer.pojos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LiveStockProcesserPayload {
	private Integer stockId;
	private Timestamp time;
	private Double oldPrice;
	private Double newPrice;
	private Double percentIncrease;
	private Double timeDifference;
	private Long score;
	private Long sumScore;
	private Queue<Long> pastSumScores = new LinkedList<>();
}

