package com.thirdeye.processer.pojos;

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
}
