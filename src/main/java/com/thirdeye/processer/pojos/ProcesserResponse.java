package com.thirdeye.processer.pojos;

import java.util.LinkedList;
import java.util.Queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ProcesserResponse {
    private String stockName;
    private Long score;
    private Queue<Long> pastSumScores = new LinkedList<>();
}
